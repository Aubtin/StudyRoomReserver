package com.aubtin.studyroomreserver.Activities;

import android.app.ProgressDialog;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.aubtin.studyroomreserver.R;

public class GmailOpener extends AppCompatActivity {

    private WebView emailView;
    private ProgressDialog progressDialog;

    private String startTime = "";
    private String roomNumber = "";
    private String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmail_opener);

        progressDialog = new ProgressDialog(GmailOpener.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        if (getIntent() != null)
        {
            startTime = getIntent().getExtras().getString("StartTime");
            roomNumber = getIntent().getExtras().getString("RoomNumber");
            date = getIntent().getExtras().getString("Date");
        }

        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        setTitle("Reservation Viewer");

        emailView = (WebView) findViewById(R.id.emailWebView);
        emailView.clearHistory();
        emailView.clearFormData();
        emailView.clearCache(true);
        emailView.getSettings().setJavaScriptEnabled(true);
        emailView.loadUrl("http://www.gmail.com");

        emailView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView wv, String url) {
                wv.loadUrl("javascript:document.getElementById('Email').value = 'ivcsuccess@gmail.com';\n" +
                        "document.getElementById('next').click();\n" +
                        "setTimeout(function() {\n" +
                        "    document.getElementById('Passwd').value = 'ivcsuccess88';\n" +
                        "    document.getElementById('signIn').click();\n" +
                        "}, 0);" +
                        "");

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                Toast.makeText(GmailOpener.this, date + " at " + startTime + ", Room " + roomNumber, Toast.LENGTH_LONG).show();
            }
        });
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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("saved_start", startTime);
        outState.putString("saved_roomnumber", roomNumber);
        outState.putString("saved_date", date);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        startTime = savedInstanceState.getString("saved_start");
        roomNumber = savedInstanceState.getString("saved_roomnumber");
        date = savedInstanceState.getString("saved_date");
    }
}
