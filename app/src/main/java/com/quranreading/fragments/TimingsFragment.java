package com.quranreading.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.quranreading.alarms.AlarmHelper;
import com.quranreading.helper.CalculatePrayerTime;
import com.quranreading.helper.ManualDialogCustom;
import com.quranreading.helper.TimeFormateConverter;
import com.quranreading.listeners.OnDailogButtonSelectionListner;
import com.quranreading.listeners.OnLocationSetListner;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.JuristicDailogActivity;
import com.quranreading.qibladirection.MainActivityNew;
import com.quranreading.qibladirection.R;
import com.quranreading.sharedPreference.AlarmSharedPref;
import com.quranreading.sharedPreference.LocationPref;
import com.quranreading.sharedPreference.TimeEditPref;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TimingsFragment extends Fragment implements OnLocationSetListner, OnClickListener, OnDailogButtonSelectionListner {

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

    private ArrayList<String> prayerTimingsDefault = new ArrayList<String>();

    private CalculatePrayerTime mCalculatePrayerTime;
    private TextView tvCity;
    private LinearLayout lacationNameLayout;
    ManualDialogCustom manualDialog;

    TextView[] tvPrayerAlarmTimes = new TextView[6];
    TextView[] tvPrayerTimes = new TextView[6];
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

    MainActivityNew mActivity;

    // For Applying Timer for Prayer Time
    CountDownTimer timer;
    long miliTimes = 0;
    TextView tvTimer, tvTimeLeft;

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mActivity = (MainActivityNew) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //mUserLocation = new UserLocation(mActivity);

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
        mActivity.registerReceiver(mLocationReceiver, new IntentFilter(TimingsFragment.LOCATION_INTENT_FILTER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timings, container, false);
        inProcess = false;

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        btnTransprnt = (Button) rootView.findViewById(R.id.btn_transparent);
        progressBar.setVisibility(View.GONE);
        btnTransprnt.setVisibility(View.GONE);

        btnBells[Fajar] = (ImageButton) rootView.findViewById(R.id.btn_fajar);
        btnBells[Sunrise] = (ImageButton) rootView.findViewById(R.id.btn_sunrise);
        btnBells[Zuhar] = (ImageButton) rootView.findViewById(R.id.btn_zuhar);
        btnBells[Asar] = (ImageButton) rootView.findViewById(R.id.btn_asar);
        btnBells[Maghrib] = (ImageButton) rootView.findViewById(R.id.btn_maghrib);
        btnBells[Isha] = (ImageButton) rootView.findViewById(R.id.btn_isha);

        btnBells[Fajar].setOnClickListener(this);
        btnBells[Sunrise].setOnClickListener(this);
        btnBells[Zuhar].setOnClickListener(this);
        btnBells[Asar].setOnClickListener(this);
        btnBells[Maghrib].setOnClickListener(this);
        btnBells[Isha].setOnClickListener(this);

        lacationNameLayout = (LinearLayout) rootView.findViewById(R.id.layout_locationTimings);
        tvCity = (TextView) rootView.findViewById(R.id.tv_city);

        layoutsTiming[Fajar] = (RelativeLayout) rootView.findViewById(R.id.fajr_layout);
        layoutsTiming[Sunrise] = (RelativeLayout) rootView.findViewById(R.id.sunrise_layout);
        layoutsTiming[Zuhar] = (RelativeLayout) rootView.findViewById(R.id.zuhr_layout);
        layoutsTiming[Asar] = (RelativeLayout) rootView.findViewById(R.id.asar_layout);
        layoutsTiming[Maghrib] = (RelativeLayout) rootView.findViewById(R.id.maghrib_layout);
        layoutsTiming[Isha] = (RelativeLayout) rootView.findViewById(R.id.isha_layout);

        tvTimer = (TextView) rootView.findViewById(R.id.tvTimer);
        tvTimeLeft = (TextView) rootView.findViewById(R.id.tvTimeLeft);

        tvPrayerAlarmTimes[Fajar] = (TextView) rootView.findViewById(R.id.tv_fajar_time);
        tvPrayerAlarmTimes[Sunrise] = (TextView) rootView.findViewById(R.id.tv_sunrise_time);
        tvPrayerAlarmTimes[Zuhar] = (TextView) rootView.findViewById(R.id.tv_zuhar_time);
        tvPrayerAlarmTimes[Asar] = (TextView) rootView.findViewById(R.id.tv_asar_time);
        tvPrayerAlarmTimes[Maghrib] = (TextView) rootView.findViewById(R.id.tv_maghrib_time);
        tvPrayerAlarmTimes[Isha] = (TextView) rootView.findViewById(R.id.tv_isha_time);

        tvPrayerTimes[Fajar] = (TextView) rootView.findViewById(R.id.tv_fajar);
        tvPrayerTimes[Sunrise] = (TextView) rootView.findViewById(R.id.tv_sunrise);
        tvPrayerTimes[Zuhar] = (TextView) rootView.findViewById(R.id.tv_zuhar);
        tvPrayerTimes[Asar] = (TextView) rootView.findViewById(R.id.tv_asar);
        tvPrayerTimes[Maghrib] = (TextView) rootView.findViewById(R.id.tv_maghrib);
        tvPrayerTimes[Isha] = (TextView) rootView.findViewById(R.id.tv_isha);

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
        salahLabels[0] = (TextView) rootView.findViewById(R.id.tvSalah1);
        salahLabels[1] = (TextView) rootView.findViewById(R.id.tvSalah2);
        salahLabels[2] = (TextView) rootView.findViewById(R.id.tvSalah3);
        salahLabels[3] = (TextView) rootView.findViewById(R.id.tvSalah4);
        salahLabels[4] = (TextView) rootView.findViewById(R.id.tvSalah5);
        salahLabels[5] = (TextView) rootView.findViewById(R.id.tvSalah6);
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
                        manualDialog = new ManualDialogCustom(mActivity, TimingsFragment.this);
                        manualDialog.show();
                    }
                } else {
                    manualDialog = new ManualDialogCustom(mActivity, TimingsFragment.this);
                    manualDialog.show();
                }
            }
        });
        // setEditedTimeCheck();
        return rootView;
    }

    private void showPrevSavedTime() {

        tvCity.setText(locationPref.getCityName());
        // String[] prayerTimes = new String[6];
        HashMap<String, String> alarmTime = mAlarmSharedPref.getPrayerTimes();

        for (int index = 0; index < tvPrayerTimes.length; index++) {
            tvPrayerTimes[index].setText(alarmTime.get(AlarmSharedPref.TIME_PRAYERS[index]));
            tvPrayerAlarmTimes[index].setText(timeEditPref.getAlarmNotifyTime(TimeEditPref.ALARMS_TIME_PRAYERS[index]));
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
        // TODO Auto-generated method stub
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
    public void onResume() {
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
    public void onPause() {
        super.onPause();

        // mActivity.unregisterReceiver(mNetworkStateBroadcast);
        mHandlerLocation.removeCallbacks(mRunnableLocation);
        progressBar.setVisibility(View.VISIBLE);
        btnTransprnt.setVisibility(View.VISIBLE);
        // mUserLocation.dismissDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(mLocationReceiver);
    }

    public void getTimings() {
        isPrayerTimeSet = true;
        prayerTimingsDefault = mCalculatePrayerTime.NamazTimings(mCalender, latitude, longitude);

        for (int index = 0; index < btnBells.length; index++) {
            tvPrayerTimes[index].setText(prayerTimingsDefault.get(index));
            tvPrayerAlarmTimes[index].setText(timeEditPref.getAlarmNotifyTime(TimeEditPref.ALARMS_TIME_PRAYERS[index]));
            mAlarmSharedPref.savePrayerTime(AlarmSharedPref.TIME_PRAYERS[index], prayerTimingsDefault.get(index));
        }

        startPrayerTimer();

        chkAlarms();
        setAlarm();
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
                    breakTimeSetAlarm(alarmIDPrayer, tvPrayerAlarmTimes[index].getText().toString(), checkFajar);
                    mAlarmSharedPref.saveAlarm(AlarmSharedPref.CHK_PRAYERS[index]);
                    timeEditPref.setAlarmNotifyTime(TimeEditPref.ALARMS_TIME_PRAYERS[index], tvPrayerAlarmTimes[index].getText().toString());
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

    private class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            inProcess = false;

            try {
                String city = intent.getStringExtra(TimingsFragment.CITY_NAME);
                double lat = intent.getDoubleExtra(TimingsFragment.LATITUDE, 0);
                double lng = intent.getDoubleExtra(TimingsFragment.LONGITUDE, 0);

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
        // TODO Auto-generated method stub

        inProcess = false;
        int selectedPosTab = 1;

//        if (locationPref.isFirstSalatLaunch() && !locationPref.getCityName().equals("") && !cityName.equals("") && MainActivityNew.selectedPosTab == 1) {
        if (locationPref.isFirstSalatLaunch() && !locationPref.getCityName().equals("") && !cityName.equals("") && selectedPosTab == 1) {
            locationPref.setFirstSalatLauch();
            startActivity(new Intent(mActivity, JuristicDailogActivity.class));
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
        // TODO Auto-generated method stub
        if (title.equals(mActivity.getResources().getString(R.string.remove_ads_heading))) {
            // Toast.makeText(mActivity, title, Toast.LENGTH_SHORT).show();
        }

    }

    private void showTimePicker(int pos) {
        if (isPrayerTimeSet && !inProcess) {
            inProcess = true;
            DialogFragment newFragment = new TimePickerFragment(pos);
            newFragment.show(mActivity.getSupportFragmentManager(), "timePicker");
        }
    }

    @SuppressLint("ValidFragment")
    public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        private int posPrayer;
        private boolean isClickListnerCalled = false;

        public TimePickerFragment(int pos) {
            // TODO Auto-generated constructor stub
            posPrayer = pos;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            // final Calendar c = Calendar.getInstance();
            isClickListnerCalled = false;

            String[] time = getPrayerTimeLabel(posPrayer);
            int hour = Integer.parseInt(time[0]);
            int minute = Integer.parseInt(time[1]);

            // Create a new instance of TimePickerDialog and return it

            TimePickerDialog timeDialog = new TimePickerDialog(mActivity, this, hour, minute, false);
            timeDialog.setTitle(getResources().getString(R.string.set_time));
            // timeDialog.setButton(DialogInterface.BUTTON_POSITIVE,mActivity.getResources().getString(R.string.okay), timeDialog);
            timeDialog.setButton(DialogInterface.BUTTON_NEGATIVE, mActivity.getResources().getString(R.string.cancel), onResetClickListner);
            return timeDialog;
        }

        DialogInterface.OnClickListener onResetClickListner = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // This check is applied as Button Click listners are calling 2 Times

                if (!isClickListnerCalled) {
                    isClickListnerCalled = true;
                }
            }
        };

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            // This check is applied as Button Click listners are calling 2 Times
            if (!isClickListnerCalled) {
                setAlarmViews(posPrayer, true);
                isClickListnerCalled = true;
                String t = TimeFormateConverter.convertTime24To12("" + hourOfDay + ":" + minute);
                timeEditPref.setAlarmNotifyTime(TimeEditPref.ALARMS_TIME_PRAYERS[posPrayer], t);
                getTimings();
            }
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            // TODO Auto-generated method stub
            super.onDismiss(dialog);
            inProcess = false;
        }
    }

    private String[] getPrayerTimeLabel(int posPrayer) {
        // TODO Auto-generated method stub
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
            layoutsTiming[idPrayer].setBackgroundResource(R.drawable.bg_row_hover);
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

	/*
     * private String setHijriTime() {
	 * 
	 * String[] weekdays = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
	 * 
	 * String[] months = mActivity.getResources().getStringArray(R.array.solar_month_names);
	 * 
	 * String[] islamicMonths = mActivity.getResources().getStringArray(R.array.hijri_month_names);
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