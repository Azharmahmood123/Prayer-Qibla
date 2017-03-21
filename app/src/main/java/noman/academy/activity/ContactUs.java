package noman.academy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import noman.Ads.AdIntegration;
import noman.CommunityGlobalClass;

/**
 * Created by Administrator on 3/15/2017.
 */

public class ContactUs extends AdIntegration {
    EditText edEmail, edName, edPhone, edMobile,edCountry;
    RadioGroup radioGroup;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);


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

        edCountry=(EditText)findViewById(R.id.edit_country);
        edEmail = (EditText) findViewById(R.id.edit_email);
        edName = (EditText) findViewById(R.id.edit_name);
        edPhone = (EditText) findViewById(R.id.edit_phone);
        edMobile = (EditText) findViewById(R.id.edit_mobile);
        radioGroup = (RadioGroup) findViewById(R.id.radio);


        Button submit = (Button) findViewById(R.id.btn_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edName.getText().toString().trim().length() > 0) {
                    if (CommunityGlobalClass.getInstance().isValidPhoneNumber(edMobile.getText().toString().trim())) {
                        if (CommunityGlobalClass.getInstance().isValidPhoneNumber(edPhone.getText().toString().trim())) {
                            if (CommunityGlobalClass.getInstance().emailValidator(edEmail.getText().toString().trim())) {
                                sendEmailUsingGMAIL();
                            } else {
                                CommunityGlobalClass.getInstance().showToast("Please enter valid email");
                            }
                        } else {
                            CommunityGlobalClass.getInstance().showToast("Please enter valid number");
                        }
                    }
                    else
                    {
                        CommunityGlobalClass.getInstance().showToast("Please enter valid number");
                    }

                } else {
                    CommunityGlobalClass.getInstance().showToast("Please enter name");
                }

            }
        });


    }


    void sendEmailUsingGMAIL() {
        String body = "";
        body = "Name: " + edName.getText().toString() + "\n" +
                "Email: " + edEmail.getText().toString() + "\n" +
                "Phone: " + edPhone.getText().toString() + "\n" +
                "Mobile: " + edMobile.getText().toString() + "\n";

        String goodTime = "Good Time: ";
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.rd_day_time) {
            goodTime = goodTime + "dayTime";
        } else if (selectedId == R.id.rd_evening) {
            goodTime = goodTime + "Evening";
        } else {
            goodTime = goodTime + "Week end";
        }

        body = body + goodTime;

        String rec[] = {getString(R.string.mail_address)};
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(android.content.Intent.EXTRA_EMAIL, rec);
        i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Contact-Us [App]");
        i.putExtra(android.content.Intent.EXTRA_TEXT,
                body);
        startActivity(i);

        refreshEditText();
    }


    public void refreshEditText()
    {edEmail.setText("");
        edName.setText("");
        edPhone.setText("");
        edMobile.setText("");
              edCountry.setText("");


    }
}
