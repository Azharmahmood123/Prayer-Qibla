package noman.academy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import org.w3c.dom.Text;

import noman.Ads.AdIntegration;
import noman.community.prefrences.SavePreference;

/**
 * Created by Administrator on 3/15/2017.
 */

public class QuranAcademy extends AdIntegration {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acadmey_main);

        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linear_ad));
        }


        initializeToolbarView();
        containerContactUs();
        containerRegistration();
        downloadContainer();
        quickLinks();
        testimonialView();
    }


    public void initializeToolbarView() {
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

        LinearLayout btnShare = (LinearLayout) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String body = getResources().getString(R.string.share_msg);
                shareMessage(getResources().getString(R.string.app_name), body);
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

    public void shareMessage(String subject, String body) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public void containerContactUs() {
        TextView btnContact = (TextView) findViewById(R.id.txt_contact);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuranAcademy.this, ContactUs.class));
            }
        });
    }

    public void containerRegistration() {
        Button btnContact = (Button) findViewById(R.id.btn_now);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuranAcademy.this, Registration.class));
            }
        });
    }

    public void courseDetail(View v) {
        startActivity(new Intent(this, CourseDetail.class));
    }

    public void downloadContainer()
    {
        TextView downoad=(TextView)findViewById(R.id.txt_clk_download);
        downoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMarket("https://play.google.com/store/apps/details?id=com.skype.raider&hl=en");
            }
        });
    }

    public void goToMarket(String url) {
        Uri marketUri = Uri.parse(url);
        Intent myIntent = new Intent(Intent.ACTION_VIEW, marketUri);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(myIntent);

    }
    void quickLinks()
    {
        TextView   tv1=(TextView)findViewById(R.id.link1);
        TextView   tv2=(TextView)findViewById(R.id.link2);
        TextView   tv3=(TextView)findViewById(R.id.link3);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMarket("https://"+getString(R.string.txt_link_1));
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMarket("https://"+getString(R.string.txt_link_2));
            }
        });
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMarket("https://"+getString(R.string.txt_link_3));
            }
        });


    }

void testimonialView()
{
    TextView testimonial=(TextView)findViewById(R.id.txt_testimonial);
    testimonial.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(QuranAcademy.this,TestimonialDetail.class));
        }
    });

}
}
