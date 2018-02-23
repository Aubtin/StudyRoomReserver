package com.aubtin.studyroomreserver.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.aubtin.studyroomreserver.R;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SurveyActivity extends AppCompatActivity {

    private EditText studentIDET;
    private EditText studentEmailET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        studentEmailET = (EditText) findViewById(R.id.studentEmailET);
        studentIDET = (EditText) findViewById(R.id.studentIDET);

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

    public void continueClick(View view)
    {
        String emailString = studentEmailET.getText().toString().trim();
        String idString = studentIDET.getText().toString().trim();

        if (idString.equals(""))
        {
            studentIDET.setText("");
            studentIDET.setError("Cannot leave this field blank!");
            studentIDET.requestFocus();
            return;
        }
        else if(emailString.equals(""))
        {
            studentEmailET.setText("");
            studentEmailET.setError("Cannot leave this field blank!");
            studentEmailET.requestFocus();
            return;
        }

        //Check size first to not hit null
        if (emailString.length() <= 8)
        {
            studentEmailET.setError("Email must end in '@ivc.edu'!");
            studentEmailET.requestFocus();
            return;
        }

        String emailEnding = emailString.substring(emailString.length() - 8);
        if(!emailEnding.equals("@ivc.edu"))
        {
            studentEmailET.setError("Email must end in '@ivc.edu'!");
            studentEmailET.requestFocus();
            return;
        }

        Bundle userData = new Bundle();
        userData.putString("STUDENTID", idString);
        userData.putString("STUDENTEMAIL", emailString);

        Intent activityStarter = new Intent(SurveyActivity.this, LoginActivity.class);
        activityStarter.putExtras(userData);
        startActivity(activityStarter);
    }
}
