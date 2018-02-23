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

public class SchoolEmailDialogFragments extends DialogFragment
{
    private EditText schoolEmailET;

    public interface SchoolEmailDialogListener {
        void positiveSchoolEmailDialogClick(String schoolEmail, DialogInterface dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof SchoolEmailDialogListener)) {
            throw new ClassCastException(activity.toString() + " must implement SchoolEmailDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder schoolIDDialog = new AlertDialog.Builder(getActivity());
        schoolIDDialog.setTitle("Student School Email");
        schoolIDDialog.setMessage("Enter your student email:");
        setCancelable(true);

        schoolEmailET = new EditText(getActivity());
        schoolEmailET.setHint("Enter school email address");
        schoolEmailET.setRawInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        schoolEmailET.requestFocus();

        if(savedInstanceState != null)
        {
            schoolEmailET.setText(savedInstanceState.getString("SCHOOLEMAIL_DIALOG_EDITTEXT"));
        }

        LinearLayout linearLayout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        schoolEmailET.setLayoutParams(layoutParams);
        linearLayout.addView(schoolEmailET);
        linearLayout.setPadding(60, 0, 60, 0);

        schoolIDDialog.setView(linearLayout);
        schoolIDDialog.setIcon(R.drawable.ic_alert);

        schoolIDDialog.setPositiveButton("Go",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((SchoolEmailDialogListener) getActivity()).positiveSchoolEmailDialogClick(schoolEmailET.getText().toString(), dialogInterface);
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
        outState.putString("SCHOOLEMAIL_DIALOG_EDITTEXT", schoolEmailET.getText().toString());
    }
}
