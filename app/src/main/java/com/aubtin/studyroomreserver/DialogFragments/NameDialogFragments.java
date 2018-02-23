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

public class NameDialogFragments extends DialogFragment
{
    private EditText nameFirstET;
    private EditText nameLastET;

    public interface NameDialogListener {
        void positiveNameDialogClick(String first, String last, DialogInterface dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof NameDialogListener)) {
            throw new ClassCastException(activity.toString() + " must implement NameDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder schoolIDDialog = new AlertDialog.Builder(getActivity());
        schoolIDDialog.setTitle("Name Change");
        schoolIDDialog.setMessage("Enter your name:");
        setCancelable(true);

        nameFirstET = new EditText(getActivity());
        nameFirstET.setHint("First name");
        nameFirstET.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        nameFirstET.requestFocus();

        nameLastET = new EditText(getActivity());
        nameLastET.setHint("Last name");
        nameLastET.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        if(savedInstanceState != null)
        {
            nameFirstET.setText(savedInstanceState.getString("FIRSTNAME_DIALOG_EDITTEXT"));
            nameLastET.setText(savedInstanceState.getString("LASTNAME_DIALOG_EDITTEXT"));
        }

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        nameFirstET.setLayoutParams(layoutParams);
        nameLastET.setLayoutParams(layoutParams);
        linearLayout.addView(nameFirstET);
        linearLayout.addView(nameLastET);
        linearLayout.setPadding(60, 0, 60, 0);

        schoolIDDialog.setView(linearLayout);
        schoolIDDialog.setIcon(R.drawable.ic_alert);

        schoolIDDialog.setPositiveButton("Go",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((NameDialogListener) getActivity()).positiveNameDialogClick(nameFirstET.getText().toString(), nameLastET.getText().toString(), dialogInterface);
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
        outState.putString("FIRSTNAME_DIALOG_EDITTEXT", nameFirstET.getText().toString());
        outState.putString("LASTNAME_DIALOG_EDITTEXT", nameLastET.getText().toString());
    }
}
