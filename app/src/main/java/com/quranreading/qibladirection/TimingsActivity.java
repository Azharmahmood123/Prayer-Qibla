package com.quranreading.qibladirection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quranreading.alarms.AlarmHelper;
import com.quranreading.fragments.CompassFragmentIndex;
import com.quranreading.helper.CalculatePrayerTime;
import com.quranreading.helper.ManualDialogCustom;
import com.quranreading.helper.TimeFormateConverter;
import com.quranreading.listeners.OnDailogButtonSelectionListner;
import com.quranreading.listeners.OnLocationSetListner;
import com.quranreading.sharedPreference.AlarmSharedPref;
import com.quranreading.sharedPreference.LocationPref;
import com.quranreading.sharedPreference.TimeEditPref;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TimingsActivity extends AppCompatActivity implements OnLocationSetListner, OnClickListener, OnDailogButtonSelectionListner {

    public static final String LOCATION_INTENT_FILTER = "timings_location_receiver";
    public static final String CITY_NAME = "city";
    public static final String LATITUDE = "lat";

    public static final String LONGITUDE = "lng";
    LocationReceiver mLocationReceiver;
    LocationPref locationPref;

    private double latitude;
    private double longitude;
    private String cityName;

    // These will be used to handle location access scenario when user try to turn on location access from Settings
    Runnable mRunnableLocation;
    Handler mHandlerLocation;
    //  public static int LOCATION_REQUEST_DELAY = 0;

    protected boolean inProcess = false;

    private Calendar mCalender;

    private Button btnTransprnt;
    private ProgressBar progressBar;

    private AlarmSharedPref mAlarmSharedPref;
    private AlarmHelper mAlarmHelper;

    private ArrayList<String> prayerTimingsDefault = new ArrayList<>();

    private CalculatePrayerTime mCalculatePrayerTime;
    private TextView tvCity;
    private LinearLayout lacationNameLayout;
    ManualDialogCustom manualDialog;

    TextView[] tvPrayerAlarmTimes = new TextView[6];
    TextView[] tvPrayerTimes = new TextView[6];
    TextView[] tvEdited = new TextView[6];
    RelativeLayout[] layoutsTiming = new RelativeLayout[6];

    private static final int Fajar = 0;
    private static final int Sunrise = 1;
    private static final int Zuhar = 2;
    private static final int Asar = 3;
    private static final int Maghrib = 4;
    private static final int Isha = 5;

    TimeEditPref timeEditPref;

    private boolean[] chkEditedTime = new boolean[6];
    private boolean[] chkAlarmsSaved = new boolean[6];
    private boolean[] chkAlarmsTemp = new boolean[6];
    private ImageButton[] btnBells = new ImageButton[6];

    private boolean isPrayerTimeSet = false;

    Context mActivity = this;

    // For Applying Timer for Prayer Time
    CountDownTimer timer;
    long miliTimes = 0;
    TextView tvTimer, tvTimeLeft;
    TextView tvHeading;

    RelativeLayout layoutImageShare, layoutImageSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated methodIndex stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_timings);
        //mUserLocation = new UserLocation(mContext);


        LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimingsActivity.super.onBackPressed();

            }
        });
        tvHeading = (TextView) findViewById(R.id.txt_toolbar);
        tvHeading.setText(R.string.salat_timings);
        layoutImageShare = (RelativeLayout) findViewById(R.id.layout_image_share);
        layoutImageSettings = (RelativeLayout) findViewById(R.id.layout_timmings_settings);


        mCalculatePrayerTime = new CalculatePrayerTime(mActivity);
        mAlarmSharedPref = new AlarmSharedPref(mActivity);
        mAlarmHelper = new AlarmHelper(mActivity);
        timeEditPref = new TimeEditPref(mActivity);
        locationPref = new LocationPref(mActivity);

        // It will be used to handle location access when user try to turn on location access from Settings
        mHandlerLocation = new Handler();
        mRunnableLocation = new Runnable() {
            @Override
            public void run() {
                inProcess = false;
                //mUserLocation.setOnLocationSetListner(TimingsFragment.this);
                //mUserLocation.checkLocation(false);
            }
        };

        mLocationReceiver = new LocationReceiver();
        mActivity.registerReceiver(mLocationReceiver, new IntentFilter(TimingsActivity.LOCATION_INTENT_FILTER));

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnTransprnt = (Button) findViewById(R.id.btn_transparent);
        progressBar.setVisibility(View.GONE);
        btnTransprnt.setVisibility(View.GONE);

        btnBells[Fajar] = (ImageButton) findViewById(R.id.btn_fajar);
        btnBells[Sunrise] = (ImageButton) findViewById(R.id.btn_sunrise);
        btnBells[Zuhar] = (ImageButton) findViewById(R.id.btn_zuhar);
        btnBells[Asar] = (ImageButton) findViewById(R.id.btn_asar);
        btnBells[Maghrib] = (ImageButton) findViewById(R.id.btn_maghrib);
        btnBells[Isha] = (ImageButton) findViewById(R.id.btn_isha);

        btnBells[Fajar].setOnClickListener(this);
        btnBells[Sunrise].setOnClickListener(this);
        btnBells[Zuhar].setOnClickListener(this);
        btnBells[Asar].setOnClickListener(this);
        btnBells[Maghrib].setOnClickListener(this);
        btnBells[Isha].setOnClickListener(this);

        lacationNameLayout = (LinearLayout) findViewById(R.id.layout_locationTimings);
        tvCity = (TextView) findViewById(R.id.tv_city);

        layoutsTiming[Fajar] = (RelativeLayout) findViewById(R.id.fajr_layout);
        layoutsTiming[Sunrise] = (RelativeLayout) findViewById(R.id.sunrise_layout);
        layoutsTiming[Zuhar] = (RelativeLayout) findViewById(R.id.zuhr_layout);
        layoutsTiming[Asar] = (RelativeLayout) findViewById(R.id.asar_layout);
        layoutsTiming[Maghrib] = (RelativeLayout) findViewById(R.id.maghrib_layout);
        layoutsTiming[Isha] = (RelativeLayout) findViewById(R.id.isha_layout);

        tvTimer = (TextView) findViewById(R.id.tvTimer);
        tvTimeLeft = (TextView) findViewById(R.id.tvTimeLeft);

        tvPrayerAlarmTimes[Fajar] = (TextView) findViewById(R.id.tv_fajar_time);
        tvPrayerAlarmTimes[Sunrise] = (TextView) findViewById(R.id.tv_sunrise_time);
        tvPrayerAlarmTimes[Zuhar] = (TextView) findViewById(R.id.tv_zuhar_time);
        tvPrayerAlarmTimes[Asar] = (TextView) findViewById(R.id.tv_asar_time);
        tvPrayerAlarmTimes[Maghrib] = (TextView) findViewById(R.id.tv_maghrib_time);
        tvPrayerAlarmTimes[Isha] = (TextView) findViewById(R.id.tv_isha_time);

        tvPrayerTimes[Fajar] = (TextView) findViewById(R.id.tv_fajar);
        tvPrayerTimes[Sunrise] = (TextView) findViewById(R.id.tv_sunrise);
        tvPrayerTimes[Zuhar] = (TextView) findViewById(R.id.tv_zuhar);
        tvPrayerTimes[Asar] = (TextView) findViewById(R.id.tv_asar);
        tvPrayerTimes[Maghrib] = (TextView) findViewById(R.id.tv_maghrib);
        tvPrayerTimes[Isha] = (TextView) findViewById(R.id.tv_isha);

        tvEdited[Fajar] = (TextView) findViewById(R.id.tv_fajar_time_edited);
        tvEdited[Sunrise] = (TextView) findViewById(R.id.tv_sunrise_time_edited);
        tvEdited[Zuhar] = (TextView) findViewById(R.id.tv_zuhar_time_edited);
        tvEdited[Asar] = (TextView) findViewById(R.id.tv_asar_time_edited);
        tvEdited[Maghrib] = (TextView) findViewById(R.id.tv_maghrib_time_edited);
        tvEdited[Isha] = (TextView) findViewById(R.id.tv_isha_time_edited);

        for (int i = 0; i<tvEdited.length;i++)
        {
            tvEdited[i].setTypeface(((GlobalClass) mActivity.getApplicationContext()).faceRobotoR);
        }


        for (int i = 0; i < tvPrayerAlarmTimes.length; i++) {
            tvPrayerAlarmTimes[i].setOnClickListener(onAlarmTimeClickListner);
        }

        tvCity.setTypeface(((GlobalClass) mActivity.getApplicationContext()).faceRobotoL);
        tvTimer.setTypeface(((GlobalClass) mActivity.getApplicationContext()).faceRobotoR);
        tvTimeLeft.setTypeface(((GlobalClass) mActivity.getApplicationContext()).faceRobotoR);

        if (locationPref.getCityName().equals("")) {
            tvCity.setText(mActivity.getString(R.string.set) + " " + mActivity.getString(R.string.location));
            showToast(mActivity.getString(R.string.set) + " " + mActivity.getString(R.string.location));
        } else {
            showPrevSavedTime();
        }

        TextView salahLabels[] = new TextView[6];
        salahLabels[0] = (TextView) findViewById(R.id.tvSalah1);
        salahLabels[1] = (TextView) findViewById(R.id.tvSalah2);
        salahLabels[2] = (TextView) findViewById(R.id.tvSalah3);
        salahLabels[3] = (TextView) findViewById(R.id.tvSalah4);
        salahLabels[4] = (TextView) findViewById(R.id.tvSalah5);
        salahLabels[5] = (TextView) findViewById(R.id.tvSalah6);
        for (int index = 0; index < salahLabels.length; index++) {

            salahLabels[index].setTypeface(((GlobalClass) mActivity.getApplicationContext()).faceRobotoL);
            tvPrayerTimes[index].setTypeface(((GlobalClass) mActivity.getApplicationContext()).faceRobotoL);
            tvPrayerAlarmTimes[index].setTypeface(((GlobalClass) mActivity.getApplicationContext()).faceRobotoL);
        }

        lacationNameLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (manualDialog != null) {
                    if (!manualDialog.isShowing()) {
                        manualDialog = new ManualDialogCustom(mActivity, TimingsActivity.this);
                        manualDialog.show();
                    }
                } else {
                    manualDialog = new ManualDialogCustom(mActivity, TimingsActivity.this);
                    manualDialog.show();
                }
            }
        });


        layoutImageShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = "";
                String[] arrPrayers = {getString(R.string.txt_fajr), getString(R.string.txt_zuhr), getString(R.string.txt_asar), getString(R.string.txt_maghrib), getString(R.string.txt_isha), getString(R.string.txt_sunrise)};

                msg += getString(R.string.salat_timings) + " " + getString(R.string.timings) + " " + getString(R.string.in) + " " + "\n" + cityName + "\n\n";

                for (int index = 0; index < 6; index++) {
                    msg = msg + arrPrayers[index] + "        " + tvPrayerTimes[index].getText().toString() + "\n";
                }
                msg += "\n\n";
                msg += getString(R.string.share_timings_message);
                shareMessage(getString(R.string.app_name) + "- " + getString(R.string.salat_timings), msg);
            }
        });

        layoutImageSettings.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(TimingsActivity.this, SettingsActivity.class));
            }
        });

        // setEditedTimeCheck();
    }

    private void showPrevSavedTime() {

        tvCity.setText(locationPref.getCityName());
        // String[] prayerTimes = new String[6];
        HashMap<String, String> alarmTime = mAlarmSharedPref.getPrayerTimes();

        for (int index = 0; index < tvPrayerTimes.length; index++) {

            String timePrayer, timeNotification;
            timePrayer = alarmTime.get(AlarmSharedPref.TIME_PRAYERS[index]);
            timeNotification = timeEditPref.getAlarmNotifyTime(TimeEditPref.ALARMS_TIME_PRAYERS[index]);

            tvPrayerTimes[index].setText(timePrayer);
            tvPrayerAlarmTimes[index].setText(timeNotification);

            if (!timeNotification.isEmpty() && !timePrayer.isEmpty()) {
                if (!timeNotification.equals(timePrayer)) {
                    tvEdited[index].setVisibility(View.VISIBLE);
                } else {
                    tvEdited[index].setVisibility(View.GONE);
                }
            } else {
                tvEdited[index].setVisibility(View.GONE);
            }
        }

        chkAlarms();
    }

	/*
     * private void setEditedTimeCheck() { isEditTime[Fajar] = timeEditPref.getEditTimeCheck(TimeEditPref.IS_EDIT_TIME_FAJR); isEditTime[Sunrise] = timeEditPref.getEditTimeCheck(TimeEditPref.IS_EDIT_TIME_SUNRISE); isEditTime[Zuhar] = timeEditPref.getEditTimeCheck(TimeEditPref.IS_EDIT_TIME_DUHR);
	 * isEditTime[Asar] = timeEditPref.getEditTimeCheck(TimeEditPref.IS_EDIT_TIME_ASR); isEditTime[Maghrib] = timeEditPref.getEditTimeCheck(TimeEditPref.IS_EDIT_TIME_MAGHRIB); isEditTime[Isha] = timeEditPref.getEditTimeCheck(TimeEditPref.IS_EDIT_TIME_ISHA); }
	 */

    // ///////////////////////////////

    private void setAlarmViews(int position, boolean state) {

        chkAlarmsTemp[position] = state;
        if (state) {
            btnBells[position].setImageResource(R.drawable.bell_on);
        } else {
            btnBells[position].setImageResource(R.drawable.bell_off);
        }
        if (state) {
            showShortToast(getResources().getString(R.string.alarm_on), 400);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated methodIndex stub
        onAlarmBellClicked(v);
    }

    private void onAlarmBellClicked(View view) {
        Integer tagIndex = Integer.parseInt(view.getTag().toString().trim());

        if (tvPrayerTimes[tagIndex].getText().toString().length() > 0) {
            if (!chkAlarmsTemp[tagIndex]) {
                showTimePicker(tagIndex);
            } else {
                chkAlarmsTemp[tagIndex] = false;
                tvPrayerAlarmTimes[tagIndex].setText("");
                showShortToast(getResources().getString(R.string.alarm_off), 400);
                btnBells[tagIndex].setImageResource(R.drawable.bell_off);

                tvEdited[tagIndex].setVisibility(View.GONE);
                mAlarmSharedPref.removeAlarm(AlarmSharedPref.CHK_PRAYERS[tagIndex]);

                // Alarm id 6 is used for Sunrize in previous versions and other like
                // fajar =1, zuhur= 2, asar =3, maghrib=4, isha = 5, sunrize = 6
                if (tagIndex == 0) {
                    mAlarmHelper.cancelAlarm(1);
                } else if (tagIndex == 1) {
                    mAlarmHelper.cancelAlarm(6);
                } else {
                    mAlarmHelper.cancelAlarm(tagIndex);
                }
                timeEditPref.setAlarmNotifyTime(TimeEditPref.ALARMS_TIME_PRAYERS[tagIndex], "");
            }
        }
    }

    private void showToast(String msg) {
        Toast toast = Toast.makeText(mActivity.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void showShortToast(String message, int milliesTime) {
        final Toast toast = Toast.makeText(mActivity, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, milliesTime);
    }

    @Override
    protected void onResume() {
        super.onResume();

        inProcess = false;
//		if(LOCATION_REQUEST_DELAY > 0)
//		{
//			requestLocationDelayed();
//		}
//		else
//		{
//			requestLocaion();
//		}

        String city = locationPref.getCityName();
        double lat = Double.parseDouble(locationPref.getLatitude());
        double lng = Double.parseDouble(locationPref.getLongitude());
        onLocationSet(city, lat, lng);

        if (manualDialog != null) {
            manualDialog.onResumeLocationDialog();
        }
    }

    private void requestLocaion() {
        //mUserLocation.setOnLocationSetListner(this);
        //mUserLocation.checkLocation(false);
    }

    private void requestLocationDelayed() {
//        mHandlerLocation.removeCallbacks(mRunnableLocation);
//        mHandlerLocation.postDelayed(mRunnableLocation, LOCATION_REQUEST_DELAY);
//        LOCATION_REQUEST_DELAY = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // mContext.unregisterReceiver(mNetworkStateBroadcast);
        mHandlerLocation.removeCallbacks(mRunnableLocation);
        progressBar.setVisibility(View.VISIBLE);
        btnTransprnt.setVisibility(View.VISIBLE);
        // mUserLocation.dismissDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(mLocationReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            int index = data.getIntExtra(SettingsTimeAlarmActivity.EXTRA_PRAYER_INDEX, -1);
            chkAlarmsSaved[index] = true;
            chkAlarmsTemp[index] = true;

            setAlarmViews(index, true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getTimings() {
        isPrayerTimeSet = true;

        setHeading();

        prayerTimingsDefault = mCalculatePrayerTime.NamazTimings(mCalender, latitude, longitude);

        for (int index = 0; index < btnBells.length; index++) {

            String timePrayer, timeNotification;

            String lastTime = mAlarmSharedPref.getPrayerTime(AlarmSharedPref.TIME_PRAYERS[index]);
            timePrayer = prayerTimingsDefault.get(index);

            timeNotification = timeEditPref.getAlarmNotifyTime(TimeEditPref.ALARMS_TIME_PRAYERS[index]);

            //////////////////////////////
            ///////if New Time of Prayer is Changed Change Alarm time Accordingly//////
            ///////////////////////////////////
            if (!lastTime.equals(timePrayer)) {
                if (!timeNotification.isEmpty()) {
                    int diff = getTimeDiffInMinutes(lastTime, timePrayer);
                    timeNotification = getNewAddedTime(timeNotification, diff);

                    timeEditPref.setAlarmNotifyTime(TimeEditPref.ALARMS_TIME_PRAYERS[index], timeNotification);
                }
            }
            ////////////////////////////
            /////////////////////////////
            ////////////////////////////


            tvPrayerTimes[index].setText(timePrayer);
            tvPrayerAlarmTimes[index].setText(timeNotification);
            mAlarmSharedPref.savePrayerTime(AlarmSharedPref.TIME_PRAYERS[index], prayerTimingsDefault.get(index));

            if (!timeNotification.isEmpty() && !timePrayer.isEmpty()) {
                if (!timeNotification.equals(timePrayer)) {
                    tvEdited[index].setVisibility(View.VISIBLE);
                } else {
                    tvEdited[index].setVisibility(View.GONE);
                }
            } else {
                tvEdited[index].setVisibility(View.GONE);
            }
        }

        startPrayerTimer();

        chkAlarms();
        setAlarm();
    }


    private void setHeading() {
//        String[] dataMethod;
//        dataMethod = getResources().getStringArray(R.array.array_calculation_methods_new);
//        PrayerTimeSettingsPref salatSharedPref = new PrayerTimeSettingsPref(this);
//        int methodIndex = salatSharedPref.getCalculationMethodIndex();
//        tvHeading.setText(dataMethod[methodIndex]);
    }

    public void chkAlarms() {
        HashMap<String, Boolean> alarm = mAlarmSharedPref.checkAlarms();
        HashMap<String, String> alarmTime = mAlarmSharedPref.getPrayerTimes();

        for (int index = 0; index < chkAlarmsSaved.length; index++) {
            chkAlarmsSaved[index] = alarm.get(AlarmSharedPref.CHK_PRAYERS[index]);
        }

        for (int index = 0; index < btnBells.length; index++) {
            if (chkAlarmsSaved[index]) {
                chkAlarmsTemp[index] = true;
                btnBells[index].setImageResource(R.drawable.bell_on);

                if (!timeEditPref.getAlarmNotifyTime(TimeEditPref.ALARMS_TIME_PRAYERS[index]).isEmpty()) {
                    tvPrayerAlarmTimes[index].setText(timeEditPref.getAlarmNotifyTime(TimeEditPref.ALARMS_TIME_PRAYERS[index]));
                } else {
                    tvPrayerAlarmTimes[index].setText(alarmTime.get(AlarmSharedPref.TIME_PRAYERS[index]));
                }
            } else {
                chkAlarmsTemp[index] = false;
                btnBells[index].setImageResource(R.drawable.bell_off);
            }
        }
    }

    public void setAlarm() {
        int alarmIDPrayer;
        boolean checkFajar = false;

        for (int index = 0; index < btnBells.length; index++) {
            if (tvPrayerAlarmTimes[index].getText().toString().length() > 0 && !tvPrayerAlarmTimes[index].getText().toString().contains("-----")) {
                checkFajar = false;

                // Alarm id 6 is used for Sunrize in previous versions and other like
                // fajar =1, zuhur= 2, asar =3, maghrib=4, isha = 5, sunrize = 6
                if (index == 0) {
                    alarmIDPrayer = 1;
                    checkFajar = true;
                } else if (index == 1) {
                    alarmIDPrayer = 6;
                } else {
                    alarmIDPrayer = index;
                }

                if (chkAlarmsTemp[index]) {
                    mAlarmHelper.cancelAlarm(alarmIDPrayer);

                    String prayerTime = tvPrayerTimes[index].getText().toString();
                    String alarmTime = tvPrayerAlarmTimes[index].getText().toString();

                    breakTimeSetAlarm(alarmIDPrayer, alarmTime, checkFajar);
                    mAlarmSharedPref.saveAlarm(AlarmSharedPref.CHK_PRAYERS[index]);

//                    int diff = getTimeDiffInMinutes(prayerTime, alarmTime);
//                    timeEditPref.setPrayerAlarmDifference(TimeEditPref.ALARMS_TIME_PRAYERS_DIFFERENCE[index], diff);

                    timeEditPref.setAlarmNotifyTime(TimeEditPref.ALARMS_TIME_PRAYERS[index], alarmTime);
                }
            }
        }
    }

    public void breakTimeSetAlarm(int index, String time, boolean chkFajar) {
        String hour = "";
        String min = "";
        String am_pm = "";
        String[] timeArray = new String[3];

        timeArray = time.split("\\s|:");

        hour = timeArray[0];
        min = timeArray[1];
        am_pm = timeArray[2];

        Log.v("Alarm Time " + index, hour + ":" + min + " " + am_pm);
        Calendar c1 = mAlarmHelper.setAlarmTime(Integer.parseInt(hour.trim()), Integer.parseInt(min.trim()), am_pm);
        mAlarmHelper.setAlarmEveryDay(c1, index, chkFajar);
    }


    int getTimeDiffInMinutes(String prayerTime, String alarmTime) {
        String[] prayerTimes = TimeFormateConverter.convertTime12To24(prayerTime).split("\\s|:");
        String[] alarmTimes = TimeFormateConverter.convertTime12To24(alarmTime).split("\\s|:");

        int timePrayerMinutes = (Integer.parseInt(prayerTimes[0].trim()) * 60) + Integer.parseInt(prayerTimes[1].trim());
        int timeAlarmMinutes = (Integer.parseInt(alarmTimes[0].trim()) * 60) + Integer.parseInt(alarmTimes[1].trim());

        return timeAlarmMinutes - timePrayerMinutes;
    }


    String getNewAddedTime(String time12, int minutes) {
        String[] prayerTimes = TimeFormateConverter.convertTime12To24(time12).split("\\s|:");

        int timePrayerMinutes = (Integer.parseInt(prayerTimes[0].trim()) * 60) + Integer.parseInt(prayerTimes[1].trim()) + minutes;

        int hours = timePrayerMinutes / 60;
        int mints = timePrayerMinutes % 60;

        time12 = TimeFormateConverter.convertTime24To12(hours + ":" + mints);

        return time12;
    }

    public void setNamazTimings(String Address, double latitude, double longitude) {

        cityName = Address;
        tvCity.setText(cityName);
        // tvCity.setText(cityName +" " + setHijriTime());
        Date now = new Date();
        mCalender = Calendar.getInstance();
        mCalender.setTime(now);
        getTimings();
    }


    private class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            inProcess = false;

            try {
                String city = intent.getStringExtra(TimingsActivity.CITY_NAME);
                double lat = intent.getDoubleExtra(TimingsActivity.LATITUDE, 0);
                double lng = intent.getDoubleExtra(TimingsActivity.LONGITUDE, 0);

                isPrayerTimeSet = false;

                tvCity.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                btnTransprnt.setVisibility(View.GONE);
                if (lat == 0 || lng == 0 || lat == -2 || lng == -2) {
                    tvCity.setText(mActivity.getString(R.string.set) + " " + mActivity.getString(R.string.location));
                    showToast(mActivity.getString(R.string.set) + " " + mActivity.getString(R.string.location));
                } else {
                    cityName = city;
                    latitude = lat;
                    longitude = lng;
                    saveLatestLocation(cityName, "" + latitude, "" + longitude);
                    setNamazTimings(cityName, latitude, longitude);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLocationSet(String cityName, double latitude, double longitude) {
        // TODO Auto-generated methodIndex stub

        inProcess = false;
        if (locationPref.isFirstSalatLaunch() && !locationPref.getCityName().equals("") && !cityName.equals("")) {
            locationPref.setFirstSalatLauch();
            startActivity(new Intent(mActivity, FirstOptionsActivity.class));
        }

        isPrayerTimeSet = false;
        tvCity.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        btnTransprnt.setVisibility(View.GONE);
        if (latitude == 0 || longitude == 0 || latitude == -2 || longitude == -2) {
            tvCity.setText(mActivity.getString(R.string.set) + " " + mActivity.getString(R.string.location));
            showToast(mActivity.getString(R.string.set) + " " + mActivity.getString(R.string.location));
        } else {
            this.cityName = cityName;
            this.latitude = latitude;
            this.longitude = longitude;
            saveLatestLocation(cityName, "" + latitude, "" + longitude);
            setNamazTimings(cityName, latitude, longitude);
        }
        try {
            Intent intnet = new Intent(CompassFragmentIndex.LOCATION_INTENT_FILTER);
            intnet.putExtra(CompassFragmentIndex.CITY_NAME, cityName);
            intnet.putExtra(CompassFragmentIndex.LATITUDE, latitude);
            intnet.putExtra(CompassFragmentIndex.LONGITUDE, longitude);
            mActivity.sendBroadcast(intnet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewLocationDetected(String newCityName, String oldCityName, double latitude, double longitude) {

    }

    private void saveLatestLocation(String address, String lat, String lng) {

        locationPref = new LocationPref(mActivity);
        locationPref.setLocation(address, lat, lng);
    }

    @Override
    public void onDailogButtonSelectionListner(String title, int selectedIndex, boolean selection) {
        // TODO Auto-generated methodIndex stub
        if (title.equals(mActivity.getResources().getString(R.string.remove_ads_heading))) {
            // Toast.makeText(mContext, title, Toast.LENGTH_SHORT).show();
        }

    }

    private int posPrayer;

    private void showTimePicker(int pos) {
        if (isPrayerTimeSet && !inProcess) {
            inProcess = true;
            posPrayer = pos;
            String[] timeNotification = getPrayerNotificationTime(posPrayer);
            // String[] getPrayerTimeArray = getPrayerTimeArray(posPrayer);


            Intent intent = new Intent(this, SettingsTimeAlarmActivity.class);
            intent.putExtra(SettingsTimeAlarmActivity.EXTRA_PRAYER_INDEX, pos);
            intent.putExtra(SettingsTimeAlarmActivity.EXTRA_PRAYER_NOTIFICATION_TIME, timeNotification);
            // intent.putExtra(SettingsTimeAlarmActivity.EXTRA_PRAYER_TIME, getPrayerTimeArray);
            startActivityForResult(intent, 1);

//            DialogFragment newFragment = new TimePickerFragment();
//            newFragment.show(getSupportFragmentManager(), "timePicker");
        }
    }

//    @SuppressLint("ValidFragment")
//    public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
//
//        private boolean isClickListnerCalled = false;
//
//        public TimePickerFragment() {
//
//        }
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current time as the default values for the picker
//            // final Calendar c = Calendar.getInstance();
//            isClickListnerCalled = false;
//
//            String[] time = getPrayerNotificationTime(posPrayer);
//            int hour = Integer.parseInt(time[0]);
//            int minute = Integer.parseInt(time[1]);
//
//            // Create a new instance of TimePickerDialog and return it
//
//            TimePickerDialog timeDialog = new TimePickerDialog(mActivity, this, hour, minute, false);
//            timeDialog.setTitle(getResources().getString(R.string.set_time));
//            // timeDialog.setButton(DialogInterface.BUTTON_POSITIVE,mContext.getResources().getString(R.string.okay), timeDialog);
//            timeDialog.setButton(DialogInterface.BUTTON_NEGATIVE, mActivity.getResources().getString(R.string.cancel), onResetClickListner);
//            return timeDialog;
//        }
//
//        DialogInterface.OnClickListener onResetClickListner = new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                // This check is applied as Button Click listners are calling 2 Times
//
//                if (!isClickListnerCalled) {
//                    isClickListnerCalled = true;
//                }
//            }
//        };
//
//        @Override
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//
//            // This check is applied as Button Click listners are calling 2 Times
//            if (!isClickListnerCalled) {
//                setAlarmViews(posPrayer, true);
//                isClickListnerCalled = true;
//                String t = TimeFormateConverter.convertTime24To12("" + hourOfDay + ":" + minute);
//                timeEditPref.setAlarmNotifyTime(TimeEditPref.ALARMS_TIME_PRAYERS[posPrayer], t);
//                getTimings();
//            }
//        }
//
//        @Override
//        public void onDismiss(DialogInterface dialog) {
//            // TODO Auto-generated methodIndex stub
//            super.onDismiss(dialog);
//            inProcess = false;
//        }
//    }

    private String[] getPrayerNotificationTime(int posPrayer) {
        // TODO Auto-generated methodIndex stub
        String[] timeArray = new String[2];
        String time = "";

        if (tvPrayerAlarmTimes[posPrayer].getText().toString().length() > 0 && !tvPrayerAlarmTimes[posPrayer].getText().toString().contains("-----")) {
            time = tvPrayerAlarmTimes[posPrayer].getText().toString();
            timeArray = TimeFormateConverter.convertTime12To24(time).split("\\s|:");
        } else if (tvPrayerTimes[posPrayer].getText().toString().length() > 0 && !tvPrayerTimes[posPrayer].getText().toString().contains("-----")) {
            time = tvPrayerTimes[posPrayer].getText().toString();
            timeArray = TimeFormateConverter.convertTime12To24(time).split("\\s|:");
        } else {
            Time now = new Time();
            now.setToNow();
            timeArray[0] = String.valueOf(now.hour);
            timeArray[1] = String.valueOf(now.minute);
        }
        return timeArray;
    }

    private String[] getPrayerTimeArray(int posPrayer) {
        // TODO Auto-generated methodIndex stub
        String[] timeArray = new String[2];
        String time = "";

        if (tvPrayerTimes[posPrayer].getText().toString().length() > 0 && !tvPrayerTimes[posPrayer].getText().toString().contains("-----")) {
            time = tvPrayerTimes[posPrayer].getText().toString();
            timeArray = TimeFormateConverter.convertTime12To24(time).split("\\s|:");
        }

        return timeArray;
    }

    private OnClickListener onAlarmTimeClickListner = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Integer index = Integer.parseInt(v.getTag().toString().trim());
            if (tvPrayerAlarmTimes[index].getText().toString().trim().length() > 0) {
                showTimePicker(index);
            }
        }
    };

    private void startPrayerTimer() {

        resetPrayerTimeLayout();

        for (int index = 0; index < tvPrayerTimes.length; index++) {

            if (checkPrayerTime(index)) {
                if (miliTimes > 0)
                    startCounter(miliTimes);
                break;
            }
        }
    }

    private void startCounter(long miliTimes) {
        // To Start Counter
        if (timer != null)
            timer.cancel();

        timer = new CountDownTimer(miliTimes, 1000) {

            public void onTick(long millisUntilFinished) {

                long second = (millisUntilFinished / 1000) % 60;
                long minute = (millisUntilFinished / (1000 * 60)) % 60;
                long hour = (millisUntilFinished / (1000 * 60 * 60)) % 24;

                String t = String.format("%02d:%02d:%02d", hour, minute, second);
                tvTimer.setText(t);
            }

            public void onFinish() {
                startPrayerTimer();
            }
        };
        timer.start();
    }

    private boolean checkPrayerTime(int idPrayer) {

        if (tvPrayerTimes[idPrayer].getText().toString().equals("-----")) {
            miliTimes = 0;
            return false;
        }

        int[] arrTime = new int[2]; // Time array of hours, minutes

        long miliTimesPrayer = 0, miliTimesCurrent;
        Time now, prayerTime;

        now = new Time();
        now.setToNow();
        miliTimesCurrent = now.toMillis(false);

        if (!tvPrayerTimes[0].getText().toString().equals("-----")) {
            if (idPrayer == 5 || chkFajrTimer(now)) {// For next Fajr time
                arrTime = getCurrentPrayerTime(0);
                // if Prayer time not found and "-----" is set
                if (arrTime == null) {
                    miliTimes = 0;
                    return false;
                }
                prayerTime = new Time();
                prayerTime.set(now);
                // ///For Next Day of Fajar/////
                prayerTime.monthDay = prayerTime.monthDay + 1;
                // ////////
                prayerTime.second = 0;
                prayerTime.hour = arrTime[0];
                prayerTime.minute = arrTime[1];
                miliTimesPrayer = prayerTime.toMillis(false);
                idPrayer = 5;
                miliTimes = miliTimesPrayer - miliTimesCurrent;
            } else {
                arrTime = getCurrentPrayerTime(idPrayer + 1);
                // if Prayer time not found and "-----" is set
                if (arrTime == null) {
                    miliTimes = 0;
                    return false;
                }
                prayerTime = new Time();
                prayerTime.set(now);
                prayerTime.second = 0;
                prayerTime.hour = arrTime[0];
                prayerTime.minute = arrTime[1];
                miliTimesPrayer = prayerTime.toMillis(false);

                miliTimes = miliTimesPrayer - miliTimesCurrent;
            }
        } else {
            arrTime = getCurrentPrayerTime(idPrayer + 1);
            // if Prayer time not found and "-----" is set
            if (arrTime == null) {
                miliTimes = 0;
                return false;
            }
            prayerTime = new Time();
            prayerTime.set(now);
            prayerTime.second = 0;
            prayerTime.hour = arrTime[0];
            prayerTime.minute = arrTime[1];
            miliTimesPrayer = prayerTime.toMillis(false);

            miliTimes = miliTimesPrayer - miliTimesCurrent;
        }

        if (miliTimesPrayer > miliTimesCurrent) {
            layoutsTiming[idPrayer].setBackgroundResource(R.drawable.bg_timing_hover);
            return true;
        } else {
            miliTimes = 0;
            return false;
        }
    }

    private boolean chkFajrTimer(Time now) {

        int[] arr = getCurrentPrayerTime(0);
        if (arr == null) {
            return false;
        }

        int current = now.hour * 60 + now.minute;
        int fajr = arr[0] * 60 + arr[1];
        if (fajr < current) {
            return false;
        }

        return true;
    }

    private int[] getCurrentPrayerTime(int pos) {
        int[] t = new int[2];

        String time = tvPrayerTimes[pos].getText().toString();
        String[] split = time.split(":");
        if (split.length < 2)
            return null;
        String[] split1 = split[1].split(" ");

        if (split1[1].equals("am")) {
            t[0] = Integer.parseInt(split[0]);

            t[1] = Integer.parseInt(split1[0]);
        } else if (split1[1].equals("pm")) {
            t[0] = Integer.parseInt(split[0]);

            if (t[0] != 12) // to Add time for 24 hours if time is greater then
                // 12 pm i,e 1pm etc.
                t[0] += 12;

            t[1] = Integer.parseInt(split1[0]);
        }

        return t;
    }

    private void resetPrayerTimeLayout() {

        for (int index = 0; index < tvPrayerTimes.length; index++) {
            layoutsTiming[index].setBackgroundResource(R.drawable.bg_row_resource);
        }
    }


    private void shareMessage(String subject, String body) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

	/*
     * private String setHijriTime() {
	 * 
	 * String[] weekdays = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
	 * 
	 * String[] months = mContext.getResources().getStringArray(R.array.solar_month_names);
	 * 
	 * String[] islamicMonths = mContext.getResources().getStringArray(R.array.hijri_month_names);
	 *
	 * String[] currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()).split("-"); Calendar calendar = Calendar.getInstance(); int day = calendar.get(Calendar.DAY_OF_WEEK);
	 * 
	 * Time now = new Time(); now.setToNow(); String[] currentTime = now.format("%k:%M:%S").split(":");
	 * 
	 * if(currentDate.length == 3 && currentTime.length == 3) { int year = Integer.parseInt(currentDate[2].trim()); int month = Integer.parseInt(currentDate[1].trim()); int date = Integer.parseInt(currentDate[0].trim());
	 * 
	 * int hour = Integer.parseInt(currentTime[0].trim()); int mint = Integer.parseInt(currentTime[1].trim()); int sec = Integer.parseInt(currentTime[2].trim());
	 * 
	 * // setup date object for midday on May Day 2004 (ISO year 2004) DateTime dtISO = new DateTime(year, month, date, hour, mint, sec, 0);
	 * 
	 * // find out what the same instant is using the Islamic Chronology DateTime dtIslamic = dtISO.withChronology(IslamicChronology.getInstance());
	 * 
	 * currentDate = dtIslamic.toString().split("T")[0].split("-"); int islamicYear = Integer.parseInt(currentDate[0].trim()); int islamicMonth = Integer.parseInt(currentDate[1].trim()); int islamicDate = Integer.parseInt(currentDate[2].trim());
	 * 
	 * String islamicDateValue = ""+islamicDate + " " +islamicMonths[islamicMonth-1]+ " " + islamicYear; String simpleDateValue = ""+weekdays[day - 1] + " "+ date + " " +months[month-1]+ " " + year;
	 * 
	 * 
	 * 
	 * 
	 * String dates = islamicDateValue + "\n" + simpleDateValue;
	 * 
	 * return dates; }
	 * 
	 * return ""; }
	 */

}