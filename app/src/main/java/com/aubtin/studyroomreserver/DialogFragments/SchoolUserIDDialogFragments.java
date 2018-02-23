package com.aubtin.studyroomreserver.DialogFragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.aubtin.studyroomreserver.R;

/**
 * Created by Aubtin on 1/30/2017.
 */

public class SchoolUserIDDialogFragments extends DialogFragment
{
    private EditText schoolUserIDET;
    private EditText schoolEmailET;

    public interface SchoolUserIDDialogListener {
        void positiveSchoolIDDialogClick(String schoolID, DialogInterface dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof SchoolUserIDDialogListener)) {
            throw new ClassCastException(activity.toString() + " must implement SchoolUserIDDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder schoolIDDialog = new AlertDialog.Builder(getActivity());
        schoolIDDialog.setTitle("Student ID");
        schoolIDDialog.setMessage("Enter your student ID:");
        setCancelable(true);

        schoolUserIDET = new EditText(getActivity());
        schoolUserIDET.setHint("Enter school student ID");
        schoolUserIDET.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        schoolUserIDET.requestFocus();

        if(savedInstanceState != null)
        {
            schoolUserIDET.setText(savedInstanceState.getString("SCHOOLUSERID_DIALOG_EDITTEXT"));
        }

        LinearLayout linearLayout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        schoolUserIDET.setLayoutParams(layoutParams);
        linearLayout.addView(schoolUserIDET);
        linearLayout.setPadding(60, 0, 60, 0);

        schoolIDDialog.setView(linearLayout);
        schoolIDDialog.setIcon(R.drawable.ic_alert);

        schoolIDDialog.setPositiveButton("Go",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((SchoolUserIDDialogListener) getActivity()).positiveSchoolIDDialogClick(schoolUserIDET.getText().toString(), dialogInterface);
                    }
                });

        schoolIDDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        Dialog showSchoolID = schoolIDDialog.show();
        showSchoolID.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return showSchoolID;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Store Values
        outState.putString("SCHOOLUSERID_DIALOG_EDITTEXT", schoolUserIDET.getText().toString());
    }
}
