package com.aubtin.studyroomreserver.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.aubtin.studyroomreserver.R;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }

    public void yesAccount(View view)
    {
        Intent activityStarter = new Intent(IntroActivity.this, LoginActivity.class);
        startActivity(activityStarter);
    }

    public void noAccount(View view)
    {
        Intent activityStarter = new Intent(IntroActivity.this, SurveyActivity.class);
        startActivity(activityStarter);
    }
}
