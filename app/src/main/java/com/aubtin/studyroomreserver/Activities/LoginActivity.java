package com.aubtin.studyroomreserver.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aubtin.studyroomreserver.R;
import com.aubtin.studyroomreserver.utils.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private static final int RC_SIGN_IN_GOOGLE = 9001;
    private static final int RC_SIGN_IN_FACEBOOK = 64206;

    //Google Login
    private GoogleApiClient mGoogleApiClient;

    //Facebook Login
    CallbackManager callbackManager;
    AccessToken accessToken;

    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Others
    private Context mContext;

    //User specific data if new
    String userSchoolID = "";
    String userSchoolEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        if (getIntent() != null)
        {
            userSchoolID = getIntent().getStringExtra("STUDENTID");
            userSchoolEmail = getIntent().getStringExtra("STUDENTEMAIL");
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mContext = this;

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_server_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton googleSignIn = (SignInButton) findViewById(R.id.login_button_google);
        googleSignIn.setSize(SignInButton.SIZE_STANDARD);
        googleSignIn.setOnClickListener(this);

        //Facebook Login
        LoginButton fbLogin = (LoginButton)findViewById(R.id.login_button_facebook);
        fbLogin.setReadPermissions("email", "public_profile");
        //Facebook Login Callback.
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "" + loginResult.getAccessToken());
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

        AccessToken.refreshCurrentAccessTokenAsync();

        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //Process signup.
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Working");
        progress.setMessage("Still going......");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        progress.dismiss();

                        if (task.isSuccessful())
                        {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            //Add the other user info.
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(acct.getGivenName() + " " + acct.getFamilyName())
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (userSchoolID != null && userSchoolEmail != null && !userSchoolID.equals("") && !userSchoolEmail.equals(""))
                                            {
                                                User newUser = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                                        FirebaseAuth.getInstance().getCurrentUser().getEmail(), userSchoolID, acct.getGivenName(), acct.getFamilyName(), userSchoolEmail);

                                                FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                                        setValue(newUser);
                                            }

                                            FirebaseUser userNew = FirebaseAuth.getInstance().getCurrentUser();
                                            Log.e(TAG, "Login.java: " + userNew.getDisplayName());
                                            startActivity(new Intent(LoginActivity.this, LoginChecker.class));
                                            progress.dismiss();

                                        }
                                    });
                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Something went wrong.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        //Process login.
        final ProgressDialog progress = new ProgressDialog(mContext);
        progress.setTitle("Working");
        progress.setMessage("Still going......");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        progress.dismiss();

                        if (task.isSuccessful())
                        {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            //Add the other user info.
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(Profile.getCurrentProfile().getFirstName() + " " + Profile.getCurrentProfile().getLastName())
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (userSchoolID != null && userSchoolEmail != null && !userSchoolID.equals("") && !userSchoolEmail.equals(""))
                                            {
                                                 User newUser = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                    FirebaseAuth.getInstance().getCurrentUser().getEmail(), userSchoolID, Profile.getCurrentProfile().getFirstName(), Profile.getCurrentProfile().getLastName(), userSchoolEmail);

                                                FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                                        setValue(newUser);
                                            }

                                            FirebaseUser userNew = FirebaseAuth.getInstance().getCurrentUser();
                                            Log.e(TAG, "Login.java: " + userNew.getDisplayName());
                                            startActivity(new Intent(LoginActivity.this, LoginChecker.class));
                                            progress.dismiss();
                                        }
                                    });
                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(LoginActivity.this, "That email is already registered!", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(LoginActivity.this, "Something went wrong.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            LoginManager.getInstance().logOut();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode: " + requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "Status Code: " + result.getStatus().getStatusCode());
            if(result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }

        //Facebook Callback
        if(requestCode == RC_SIGN_IN_FACEBOOK)
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void signInGoogle()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        Log.d(TAG, "signInGoogle() called.");
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button_google:
                signInGoogle();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}