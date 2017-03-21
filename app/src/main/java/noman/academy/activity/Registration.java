package noman.academy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import noman.Ads.AdIntegration;
import noman.CommunityGlobalClass;
import noman.community.prefrences.SavePreference;

/**
 * Created by Administrator on 3/15/2017.
 */

public class Registration extends AdIntegration {
    EditText edEmail, edName,  edMobile;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linear_ad));
        }

        LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        LinearLayout btnCall = (LinearLayout) findViewById(R.id.btn_call);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhoneDialog();
            }
        });

        edEmail = (EditText) findViewById(R.id.edit_email);
        edName = (EditText) findViewById(R.id.edit_name);
        edMobile = (EditText) findViewById(R.id.edit_mobile);

        Button submit = (Button) findViewById(R.id.btn_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edName.getText().toString().trim().length() > 0) {
                    if (CommunityGlobalClass.getInstance().isValidPhoneNumber(edMobile.getText().toString().trim())) {
                        if (CommunityGlobalClass.getInstance().emailValidator(edEmail.getText().toString().trim())) {
                            sendEmailUsingGMAIL();
                        } else {
                            CommunityGlobalClass.getInstance().showToast("Please enter valid email");
                        }
                    } else {
                        CommunityGlobalClass.getInstance().showToast("Please enter valid number");
                    }

                } else {
                    CommunityGlobalClass.getInstance().showToast("Please enter name");
                }

            }
        });


    }

    public void showPhoneDialog() {
        final String[] phone = {"1-718-208-4590", "905-487-8501", "0203-002-0934"};
        CharSequence[] array = {"USA : 1-718-208-4590", "Canada : 905-487-8501", "UK : 0203-002-0934"};
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle("Select Option")
                .setSingleChoiceItems(array, SavePreference.getMenuOption(this), null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();

                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone[selectedPosition]));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
        )
                .show();


    }


    void sendEmailUsingGMAIL() {
        String body = "";
        body = "Name: " + edName.getText().toString() + "\n" +
                "Email: " + edEmail.getText().toString() + "\n" +
                "Mobile: " + edMobile.getText().toString() + "\n";


        String rec[] = {getString(R.string.mail_address)};
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(android.content.Intent.EXTRA_EMAIL, rec);
        i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Registration [App]");
        i.putExtra(android.content.Intent.EXTRA_TEXT,
                body);
        startActivity(i);

        refreshEditText();
    }

    public void refreshEditText()
    {edEmail.setText("");
        edName.setText("");

        edMobile.setText("");



    }
}
