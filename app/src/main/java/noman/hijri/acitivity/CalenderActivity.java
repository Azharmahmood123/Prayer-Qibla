package noman.hijri.acitivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import noman.Ads.AdIntegration;
import noman.CommunityGlobalClass;
import noman.community.prefrences.SavePreference;
import noman.hijri.fragment.CalenderFragment;
import noman.hijri.fragment.EventFragment;
import noman.hijri.preference.DateAdjustmentPref;


public class CalenderActivity extends AdIntegration {


    private TabLayout tabLayout;
    ViewPager viewPager;
    DateAdjustmentPref adjustPref;
    public Calendar calendar;
    public DateFormat dateFormat;
    public SimpleDateFormat timeFormat;
    private String TIME_PATTERN = "HH:mm";
    Button btnToday, btnEvent, btnConvert;
    CalenderFragment calenderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linearAd));
        }
        RelativeLayout img_back=(RelativeLayout)findViewById(R.id.layout_drawer_menu_ic) ;
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        CommunityGlobalClass.mCalenderTabsActivity = this;
        adjustPref = new DateAdjustmentPref(this);
        CommunityGlobalClass.getInstance().dateAdjustment = adjustPref.getAdjustmentValue();



        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

        btnToday = (Button) findViewById(R.id.btn_Today);
        btnEvent = (Button) findViewById(R.id.btn_Event);
        btnConvert = (Button) findViewById(R.id.btn_Converter);

        btnToday.setTypeface(((GlobalClass) CalenderActivity.this.getApplicationContext()).faceRobotoR);
        btnEvent.setTypeface(((GlobalClass) CalenderActivity.this.getApplicationContext()).faceRobotoR);
        btnConvert.setTypeface(((GlobalClass) CalenderActivity.this.getApplicationContext()).faceRobotoR);


        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToCalenderTab();
                //moveTodayTab();
            }
        });
        btnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CalenderActivity.this, EventActivity.class));
            }
        });
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CalenderActivity.this, ConverterDialog.class));
            }
        });

        //INitial show calender
        moveToCalenderTab();

      // CommunityGlobalClass.getInstance().notifiyEvents(this);
        handleNotificaitonIntent();
    }

    public void handleNotificaitonIntent()
    {
        //If open from the intent notificaiton
        if(this.getIntent().getExtras() !=null)
        {
            Bundle bundle= getIntent().getExtras();
            if(bundle.getString("from").equals("notificaton")) {
                Intent intent = new Intent(this, EventActivity.class);
                intent.putExtra("from", "notificaton");
                startActivity(intent);
            }
        }
    }


    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        switch (item.getItemId()) {
            case R.id.action_hijri:
                showDialogHijri();
                return true;
            case R.id.action_notification:
                showDialogNotification();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void moveToEventTab() {

        setFragment(new EventFragment());
    }

    public void moveTodayTab() {
        calenderFragment.setCurrentMonth();
    }

    public void moveToCalenderTab() {
        calenderFragment = CalenderFragment.newInstance(CalenderActivity.this);
        setFragment(calenderFragment);
    }


    public void showDialogHijri() {

        final CharSequence[] array = {"-1", "-2", "0", "+1", "+2"};
        new AlertDialog.Builder(CalenderActivity.this,R.style.MyAlertDialogStyle)
                .setTitle("Select Option")
                .setSingleChoiceItems(array, SavePreference.getHijriCorrectionSetting(CalenderActivity.this), null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        int date = Integer.parseInt(array[selectedPosition].toString().trim().replace("+", ""));
                        adjustPref.setAdjustmentValue(returnAdjustDate(date));

                        SavePreference.setHijriCorrectionSetting(CalenderActivity.this, selectedPosition);
                        CommunityGlobalClass.getInstance().dateAdjustment = adjustPref.getAdjustmentValue();
                        moveToCalenderTab();

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

    public void showDialogNotification() {

        CharSequence[] array = {"ON", "OFF"};
        new AlertDialog.Builder(this)
                .setTitle("Select Option")
                .setSingleChoiceItems(array, SavePreference.getNotificationSetting(CalenderActivity.this), null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        SavePreference.setNotificationSetting(CalenderActivity.this, selectedPosition);

                    }
                }).setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
        )
                .show();
    }

    public int returnAdjustDate(int value) {
        int i = 1;
        switch (value) {
            case 0:
                i = 1;
                break;
            case 1:
                i = 2;
                break;
            case 2:
                i = 3;
                break;
            case -1:
                i = 0;
                break;
            case -2:
                i = -1;
                break;
        }
        return i;
    }
}
