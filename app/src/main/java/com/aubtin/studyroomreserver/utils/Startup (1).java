package com.aubtin.studyroomreserver.utils;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.logging.Logger;

/**
 * Created by Aubtin on 1/30/2017.
 */

public class Startup extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setLogLevel(com.google.firebase.database.Logger.Level.DEBUG);
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
        /* Enable disk persistence  */
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//
//            Intent intent = new Intent(Startup.this, BackgroundMonitorService.class);
//            Startup.this.startService(intent);
        }
        registerActivityLifecycleCallbacks(new ApplicationLifecycleManager());
    }
}
