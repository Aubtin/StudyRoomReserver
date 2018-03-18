package com.aubtin.studyroomreserver.Activities;

import android.app.DownloadManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aubtin.studyroomreserver.R;
import com.aubtin.studyroomreserver.utils.RequestedRooms;
import com.aubtin.studyroomreserver.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RequestRoomActivity extends AppCompatActivity {

    //Firebase
    DatabaseReference groupRequest;
    DatabaseReference groupRequestNewOnly;
    DatabaseReference userList;

    private Spinner dayPicker;
    private Spinner timeStartPicker;
    private Spinner timeEndPicker;
    private Spinner roomPreference;
    private Spinner userSelector;

    ArrayList<User> users;
    ArrayList<String> usersString;
    ArrayAdapter<String> spinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_room);

        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);

        dayPicker = (Spinner) findViewById(R.id.dayPicker);
        timeStartPicker = (Spinner) findViewById(R.id.startTimeSpinner);
        timeEndPicker = (Spinner) findViewById(R.id.endTimeSpinner);
        roomPreference = (Spinner) findViewById(R.id.preferredRoomSelector);
        userSelector = (Spinner) findViewById(R.id.userSelectorSpinner);

        groupRequest = FirebaseDatabase.getInstance().getReference("groups").child("xX2SmBz9ftb").child("requested");
        groupRequestNewOnly = FirebaseDatabase.getInstance().getReference("groups").child("xX2SmBz9ftb").child("requested-new");
        userList = FirebaseDatabase.getInstance().getReference("users");


        users = new ArrayList<User>();
        usersString = new ArrayList<String>();
        usersString.add("Select User");
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, usersString); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSelector.setAdapter(spinnerArrayAdapter);

        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> userIterate = dataSnapshot.getChildren();

                while(userIterate.iterator().hasNext())
                {
                    User tempUser = userIterate.iterator().next().getValue(User.class);
                    users.add(tempUser);
                    usersString.add(tempUser.getName());
                }
                spinnerArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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


    public void submitRequest(View view)
    {
        //Check if nothing was selected (empty request).
        if(dayPicker.getSelectedItemId() == 0)
        {
            Toast.makeText(this, "Please select a day.", Toast.LENGTH_LONG).show();
            return;
        }
        else if(timeStartPicker.getSelectedItemPosition() == 0)
        {
            Toast.makeText(this, "Please select a start time.", Toast.LENGTH_LONG).show();
            return;
        }
        else if(timeEndPicker.getSelectedItemPosition() == 0)
        {
            Toast.makeText(this, "Please select an end time.", Toast.LENGTH_LONG).show();
            return;
        }
        else if(roomPreference.getSelectedItemPosition() == 0)
        {
            Toast.makeText(this, "Please select a room.", Toast.LENGTH_LONG).show();
            return;
        }
        else if(userSelector.getSelectedItemPosition() == 0)
        {
            Toast.makeText(this, "Please assign this block to a user.", Toast.LENGTH_LONG).show();
            return;
        }
        else if(timeEndPicker.getSelectedItemPosition() - timeStartPicker.getSelectedItemPosition() > 6)
        {
            Toast.makeText(this, "Time blocks have a maximum of 3 hours.", Toast.LENGTH_LONG).show();
            return;
        }

        //Make sure end time is larger than start
        if(timeEndPicker.getSelectedItemPosition() <= timeStartPicker.getSelectedItemPosition())
        {
            Toast.makeText(this, "Your end time can't be less than or equal to the start time.", Toast.LENGTH_LONG).show();
            return;
        }


        //Subtracting one to make it standard that 0 = 8:00 AM across platforms.
        //Not adding -1 to datePicked to ignore Sunday
        int dayPicked = dayPicker.getSelectedItemPosition();
        int startTimePicked = timeStartPicker.getSelectedItemPosition() - 1;
        int endTimePicked = timeEndPicker.getSelectedItemPosition() - 1;
        int userAssignPicked = userSelector.getSelectedItemPosition() - 1;

        //Passed empty tests, rest of values don't need empty check.
        int preferredRoom = roomPreference.getSelectedItemPosition() - 1;

        //Send to server.
        String pushkey = groupRequest.push().getKey();
        RequestedRooms request = new RequestedRooms(pushkey, dayPicked, startTimePicked, endTimePicked, preferredRoom, FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                users.get(userAssignPicked).getUserUID(), users.get(userAssignPicked).getName());
        groupRequest.child(pushkey).setValue(request);
        groupRequestNewOnly.child(pushkey).setValue(request);
        finish();
    }
}
