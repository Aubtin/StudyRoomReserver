package com.aubtin.studyroomreserver.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aubtin.studyroomreserver.Adapter.RequestedRoomAdapter;
import com.aubtin.studyroomreserver.Adapter.ReservedRoomAdapter;
import com.aubtin.studyroomreserver.DialogFragments.NameDialogFragments;
import com.aubtin.studyroomreserver.DialogFragments.SchoolEmailDialogFragments;
import com.aubtin.studyroomreserver.DialogFragments.SchoolUserIDDialogFragments;
import com.aubtin.studyroomreserver.R;
import com.aubtin.studyroomreserver.utils.CheckedInUser;
import com.aubtin.studyroomreserver.utils.RequestedRooms;
import com.aubtin.studyroomreserver.utils.ReservedRooms;
import com.aubtin.studyroomreserver.utils.User;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, SchoolUserIDDialogFragments.SchoolUserIDDialogListener,
        SchoolEmailDialogFragments.SchoolEmailDialogListener, NameDialogFragments.NameDialogListener {

    private static final String TAG = "MainActivity";

    //Google
    private GoogleApiClient mGoogleApiClient;

    //Firebase
    private FirebaseAuth mFirebaseAuth;
            private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference userDatabase;
    private DatabaseReference groupDatabase;
    private DatabaseReference groupDatabaseNFCManager;
    private ValueEventListener roomReservedListener;
    private ChildEventListener roomTimeRequestListener;

    //Other
    private Context mContext;
    private User currentUser;

    //Room Data
    private List<ReservedRooms> reservedRoomsList;
    private List<RequestedRooms> requestedRoomsList;

    //UI
    private RecyclerView requestedRoomsRecycler;
    private RecyclerView reservedRoomsRecycler;
    private LinearLayoutManager layoutManagerReserved;
    private LinearLayoutManager layoutManagerRequest;
    private TextView reservedRoomsNoneTV;
    private TextView requestRoomsNoneTV;

    //Recycler Adapters
    private RequestedRoomAdapter requestedRoomAdapter;
    private ReservedRoomAdapter reservedRoomAdapter;

    //NFC
    private boolean nfcUsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mFirebaseAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        groupDatabase = FirebaseDatabase.getInstance().getReference("groups").child("xX2SmBz9ftb");
        groupDatabaseNFCManager = FirebaseDatabase.getInstance().getReference("groups").child("xX2SmBz9ftb").child("user-checkin");

        reservedRoomsList = new ArrayList<>();
        requestedRoomsList = new ArrayList<>();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("group_xX2SmBz9ftb");
                    Log.d(TAG, "Login State in MainActivity changed.");
                    Intent activityStarter = new Intent(MainActivity.this, LoginChecker.class);
                    activityStarter.setFlags(FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startActivity(activityStarter);
                }
                else
                {
                    //Deal with NFC once user established, then set to false.
                    if(nfcUsed) {
                        NdefMessage[] msgs = null;
                        Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                        if (rawMsgs != null) {
                            msgs = new NdefMessage[rawMsgs.length];
                            for (int i = 0; i < rawMsgs.length; ++i) {
                                msgs[i] = (NdefMessage) rawMsgs[i];
                            }
                        }

                        if ((msgs != null) && (msgs.length > 0)) {
                            NdefRecord[] records = msgs[0].getRecords();
                            NdefRecord firstRecord = records[0];
                            byte[] payloadData = firstRecord.getPayload();

                            //Process NFC.
                            String pushkey = groupDatabaseNFCManager.push().getKey();
                            CheckedInUser request = new CheckedInUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), new String(payloadData));
                            groupDatabaseNFCManager.child(pushkey).setValue(request);
                            nfcUsed = false;
                            //Have to clear the intent or else it'll keep thinking NFC was hit when the app is resumed (turn screen off and on).
                            setIntent(new Intent());
                        }
                    }
                }
            }
        };

        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Google stuff needed for logout.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_server_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        FirebaseMessaging.getInstance().subscribeToTopic("group_xX2SmBz9ftb");

        requestedRoomsRecycler = (RecyclerView) findViewById(R.id.requestedRecyclerView);
        requestedRoomAdapter = new RequestedRoomAdapter(requestedRoomsList, new RequestedRoomAdapter.RequestAdapterListeners() {
            @Override
            public void clickedItemRequest(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to delete this reservation?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                groupDatabase.child("requested").child(requestedRoomsList.get(position).getKey()).removeValue();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        layoutManagerRequest = new LinearLayoutManager(this);
        requestedRoomsRecycler.setLayoutManager(layoutManagerRequest);
        requestedRoomsRecycler.setAdapter(requestedRoomAdapter);

        reservedRoomsRecycler = (RecyclerView) findViewById(R.id.reservedRecyclerView);
        reservedRoomAdapter = new ReservedRoomAdapter(reservedRoomsList, new ReservedRoomAdapter.ReservedAdapterListeners() {
            @Override
            public void clickedItemReserved(int position) {
                Intent activityStarter = new Intent(MainActivity.this, GmailOpener.class);
                activityStarter.putExtra("RoomNumber", reservedRoomsList.get(position).getRoomName());
                activityStarter.putExtra("StartTime", reservedRoomsList.get(position).getStartTime());
                activityStarter.putExtra("Date", reservedRoomsList.get(position).getDate());
                startActivity(activityStarter);
            }
        });
        layoutManagerReserved = new LinearLayoutManager(this);
        reservedRoomsRecycler.setLayoutManager(layoutManagerReserved);
        reservedRoomsRecycler.setAdapter(reservedRoomAdapter);

        //Add dividers
        DividerItemDecoration dividerItemDecorationRequested = new DividerItemDecoration(requestedRoomsRecycler.getContext(),
                layoutManagerRequest.getOrientation());
        requestedRoomsRecycler.addItemDecoration(dividerItemDecorationRequested);

        //Add dividers
        DividerItemDecoration dividerItemDecorationReserved = new DividerItemDecoration(reservedRoomsRecycler.getContext(),
                layoutManagerReserved.getOrientation());
        reservedRoomsRecycler.addItemDecoration(dividerItemDecorationReserved);

        reservedRoomsNoneTV = (TextView) findViewById(R.id.resRoomNoneTV);
        requestRoomsNoneTV = (TextView) findViewById(R.id.reqTimeNoneTV);

        roomReservedListener = groupDatabase.child("reserved").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reservedRoomsList.clear();

                for (DataSnapshot mainChildren : dataSnapshot.getChildren()) {
                    for (DataSnapshot subChildren : mainChildren.getChildren())
                    {
                        reservedRoomsList.add(new ReservedRooms(Long.parseLong(subChildren.getKey()), subChildren.getValue(String.class)));

                        Collections.sort(reservedRoomsList, new Comparator<ReservedRooms>() {
                            public int compare(ReservedRooms o1, ReservedRooms o2) {
                                return (o1.getStartEpochTimeString().compareTo(o2.getStartEpochTimeString()));
                            }
                        });
                    }
                    reservedRoomAdapter.notifyDataSetChanged();
                }

                if (reservedRoomsList.size() == 0)
                {
                    reservedRoomsNoneTV.setVisibility(View.VISIBLE);
                    reservedRoomsRecycler.setVisibility(View.GONE);
                }
                else
                {
                    reservedRoomsNoneTV.setVisibility(View.GONE);
                    reservedRoomsRecycler.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        roomTimeRequestListener = groupDatabase.child("requested").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                RequestedRooms requestedRooms = dataSnapshot.getValue(RequestedRooms.class);
                requestedRoomsList.add(requestedRooms);

                if(requestedRoomsList.size() > 0)
                {
                    requestRoomsNoneTV.setVisibility(View.GONE);
                    requestedRoomsRecycler.setVisibility(View.VISIBLE);
                }
                requestedRoomAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                RequestedRooms requestedRooms = dataSnapshot.getValue(RequestedRooms.class);
                for (int x = 0; x < requestedRoomsList.size(); x++)
                {
                    if (requestedRooms.equals(requestedRoomsList.get(x)))
                    {
                        requestedRoomsList.remove(x);
                        requestedRoomAdapter.notifyDataSetChanged();
                    }
                }

                if(!dataSnapshot.exists() || requestedRoomsList.size() == 0) {
                    requestRoomsNoneTV.setVisibility(View.VISIBLE);
                    requestedRoomsRecycler.setVisibility(View.GONE);
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.d(TAG, "CONNECTED TO FIREBASE");
                } else {
                    Log.d(TAG, "NOT CONNECTED TO FIREBASE");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

        DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("test");
        testRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String test = snapshot.getValue(String.class);
                Log.d(TAG, "CHANGED: " + test);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                mFirebaseAuth.signOut();
                                LoginManager.getInstance().logOut();
                                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                        new ResultCallback<Status>() {
                                            @Override
                                            public void onResult(@NonNull Status status) {
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            case R.id.action_changeName:
                new NameDialogFragments().show(getSupportFragmentManager(), "nameDialogFragment");
                return true;
            case R.id.action_changeStudentSchoolID:
                new SchoolUserIDDialogFragments().show(getSupportFragmentManager(), "schooluseridDialogFragment");
                return true;
            case R.id.action_changeStudentEmail:
                new SchoolEmailDialogFragments().show(getSupportFragmentManager(), "schoolEmailDialogFragment");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }

        if(roomReservedListener != null)
            groupDatabase.removeEventListener(roomReservedListener);
        if(roomTimeRequestListener != null)
            groupDatabase.removeEventListener(roomTimeRequestListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void positiveSchoolIDDialogClick(String schoolID, DialogInterface dialog) {
        if( schoolID == null || schoolID.equals(""))
            new SchoolUserIDDialogFragments().show(getSupportFragmentManager(), "schooluseridDialogFragment");
        else {
            currentUser.setSchoolUserID(schoolID);
            FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                    setValue(currentUser);

        }
    }

    @Override
    public void positiveSchoolEmailDialogClick(String schoolEmail, DialogInterface dialog) {
        if( schoolEmail == null || schoolEmail.equals(""))
            new SchoolEmailDialogFragments().show(getSupportFragmentManager(), "schoolEmailDialogFragment");
        else {
            currentUser.setSchoolEmail(schoolEmail);
            FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                    setValue(currentUser);
        }
    }

    @Override
    public void positiveNameDialogClick(String newFirst, String newLast, DialogInterface dialog) {
        if(newFirst == null || newFirst.equals("") || newLast == null || newLast.equals(""))
            new NameDialogFragments().show(getSupportFragmentManager(), "nameDialogFragment");
        else {
            currentUser.setFirstName(newFirst);
            currentUser.setLastName(newLast);

            String newFullName = newFirst + " " + newLast;
            currentUser.setName(newFullName);

            UserProfileChangeRequest profileNameUpdate = new UserProfileChangeRequest.Builder()
            .setDisplayName(newFullName)
            .build();
            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileNameUpdate);

            FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                    setValue(currentUser);
        }
    }

    public void addReservation(View view)
    {
        Intent activityStarter = new Intent(MainActivity.this, RequestRoomActivity.class);
        startActivity(activityStarter);
    }

    //NFC
    public void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            nfcUsed = true;
        }
    }
}