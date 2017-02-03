package com.quranreading.qibladirection;

/**
 * Created by cyber on 12/22/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.fragments.CompassDialMenuFragment;
import com.quranreading.helper.TimeFormateConverter;
import com.quranreading.sharedPreference.AlarmSharedPref;
import com.quranreading.sharedPreference.PrayerTimeSettingsPref;
import com.quranreading.sharedPreference.TimeEditPref;

import java.util.ArrayList;
import java.util.HashMap;

import noman.CommunityGlobalClass;

/**
 * Created by cyber on 12/20/2016.
 */

public class SettingsPrayerListSoundActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // google ads
    AdView adview;
    ImageView adImage;
    private static final String LOG_TAG = "Ads";
    private final Handler adsHandler = new Handler();
    private int timerValue = 3000, networkRefreshTime = 10000;
    Context context = this;

    ListView listView;
    CalculationMethodAdapter adapter;
    int selectedPosition = -1;
    boolean inProccess = false;
    ArrayList<String> options = new ArrayList<>();
    String[] prayerTimes = new String[6];
    String[] prayerTimesNotify = new String[6];

    PrayerTimeSettingsPref prayerTimeSettingsPref;
    TimeEditPref timeEditPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated methodIndex stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);

        prayerTimeSettingsPref = new PrayerTimeSettingsPref(this);
        timeEditPref = new TimeEditPref(this);

        LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        TextView tvHeading = (TextView) findViewById(R.id.txt_toolbar);
        tvHeading.setText(R.string.settings);

        tvHeading.setText(R.string.tone_settings);

        // Ads
        initializeAds();

        listView = (ListView) findViewById(R.id.listview_language);
        listView.setOnItemClickListener(this);


    }


    private void populateData() {

        AlarmSharedPref mAlarmSharedPref = new AlarmSharedPref(context);

        // HashMap<String, Boolean> alarm = mAlarmSharedPref.checkAlarms();
        HashMap<String, String> alarmTime = mAlarmSharedPref.getPrayerTimes();


        for (int index = 0; index < prayerTimes.length; index++) {
            prayerTimes[index] = alarmTime.get(AlarmSharedPref.TIME_PRAYERS[index]);
            prayerTimesNotify[index] = timeEditPref.getAlarmNotifyTime(TimeEditPref.ALARMS_TIME_PRAYERS[index]);
        }

        int fajr, sunrize, zuhar, asar, maghrib, isha;
        fajr = prayerTimeSettingsPref.getCorrectionsFajr();
        sunrize = prayerTimeSettingsPref.getCorrectionsSunrize();
        zuhar = prayerTimeSettingsPref.getCorrectionsZuhar();
        asar = prayerTimeSettingsPref.getCorrectionsAsar();
        maghrib = prayerTimeSettingsPref.getCorrectionsMaghrib();
        isha = prayerTimeSettingsPref.getCorrectionsIsha();

        String[] methodNames = {getString(R.string.txt_fajr), getString(R.string.txt_sunrise), getString(R.string.txt_zuhr), getString(R.string.txt_asar), getString(R.string.txt_maghrib), getString(R.string.txt_isha)};

        String[] soundOption = {getResources().getString(R.string.default_tone), getResources().getString(R.string.silent), getResources().getString(R.string.adhan1_new), getResources().getString(R.string.adhan2_new), getResources().getString(R.string.adhan3_new), getResources().getString(R.string.adhan4_new), getResources().getString(R.string.adhan5_new), getResources().getString(R.string.adhan6_new), getResources().getString(R.string.adhan7_new)};
        String[] alarmTones = new String[6];

        AlarmSharedPref alarmObj = new AlarmSharedPref(this);

        for (int i = 0; i < alarmTones.length; i++) {
            alarmTones[i] = soundOption[alarmObj.getAlarmOptionIndex(AlarmSharedPref.ALARM_PRAYERS_SOUND[i], i)];
        }


        adapter = new CalculationMethodAdapter(context, methodNames, alarmTones);
        listView.setAdapter(adapter);
    }


    private String[] getPrayerNotificationTime(int posPrayer) {
        // TODO Auto-generated methodIndex stub
        String[] timeArray = new String[2];
        String time = "";

        if (!prayerTimesNotify[posPrayer].isEmpty()) {
            timeArray = TimeFormateConverter.convertTime12To24(prayerTimesNotify[posPrayer]).split("\\s|:");
        } else if (!prayerTimes[posPrayer].isEmpty()) {
            timeArray = TimeFormateConverter.convertTime12To24(prayerTimes[posPrayer]).split("\\s|:");
        } else {
            Time now = new Time();
            now.setToNow();
            timeArray[0] = String.valueOf(now.hour);
            timeArray[1] = String.valueOf(now.minute);
        }
        return timeArray;
    }

    private void initializeAds() {

        adview = (AdView) findViewById(R.id.adView);
        adImage = (ImageView) findViewById(R.id.adimg);
        adImage.setVisibility(View.GONE);
        adview.setVisibility(View.GONE);

        if (isNetworkConnected()) {
            this.adview.setVisibility(View.VISIBLE);
        } else {
            this.adview.setVisibility(View.GONE);
        }
        setAdsListener();
    }

    public void onBackButtonClick(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {

        setResult(RESULT_OK, new Intent());
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        //check if location not set in menu index
        if (CompassDialMenuFragment.tvCity.getText().toString().equals(getString(R.string.set) + " " + getString(R.string.location))) {
            CommunityGlobalClass.getInstance().showShortToast(getString(R.string.set) + " " + getString(R.string.location),500, Gravity.CENTER);
        } else {
            selectedPosition = position;
            //String[] timeNotification = getPrayerNotificationTime(selectedPosition);
            Intent intent = new Intent(this, SettingsTimeAlarmActivity.class);
            intent.putExtra(SettingsTimeAlarmActivity.EXTRA_PRAYER_INDEX, selectedPosition);
            //  intent.putExtra(SettingsTimeAlarmActivity.EXTRA_PRAYER_NOTIFICATION_TIME, timeNotification);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated methodIndex stub
        super.onResume();
        inProccess = false;
        if (((GlobalClass) getApplication()).isPurchase) {
            adImage.setVisibility(View.GONE);
            adview.setVisibility(View.GONE);
        } else {
            startAdsCall();
        }
        populateData();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated methodIndex stub
        super.onPause();

        if (!((GlobalClass) getApplication()).isPurchase) {
            stopAdsCall();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated methodIndex stub
        super.onDestroy();

        if (!((GlobalClass) getApplication()).isPurchase) {

            destroyAds();
        }

        setResult(RESULT_OK, new Intent());
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

    ///////////////////////////
    //////////////////////////
    //////////////////////////
    public void onClickAdImage(View view) {

    }

    private boolean isNetworkConnected() {
        ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
    }

    private Runnable sendUpdatesAdsToUI = new Runnable() {
        public void run() {
            Log.v(LOG_TAG, "Recall");
            updateUIAds();
        }
    };

    private final void updateUIAds() {
        if (isNetworkConnected()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adview.loadAd(adRequest);
        } else {
            timerValue = networkRefreshTime;
            adsHandler.removeCallbacks(sendUpdatesAdsToUI);
            adsHandler.postDelayed(sendUpdatesAdsToUI, timerValue);
        }
    }

    public void startAdsCall() {
        Log.i(LOG_TAG, "Starts");
        if (isNetworkConnected()) {
            this.adview.setVisibility(View.VISIBLE);
        } else {
            this.adview.setVisibility(View.GONE);
        }

        adview.resume();
        adsHandler.removeCallbacks(sendUpdatesAdsToUI);
        adsHandler.postDelayed(sendUpdatesAdsToUI, 0);
    }

    public void stopAdsCall() {
        Log.e(LOG_TAG, "Ends");
        adsHandler.removeCallbacks(sendUpdatesAdsToUI);
        adview.pause();
    }

    public void destroyAds() {
        Log.e(LOG_TAG, "Destroy");
        adview.destroy();
        adview = null;
    }

    private void setAdsListener() {
        adview.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(LOG_TAG, "onAdClosed");
            }

            @Override
            public void onAdFailedToLoad(int error) {
                String message = "onAdFailedToLoad: " + getErrorReason(error);
                Log.d(LOG_TAG, message);
                adview.setVisibility(View.GONE);
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(LOG_TAG, "onAdLeftApplication");
            }

            @Override
            public void onAdOpened() {
                Log.d(LOG_TAG, "onAdOpened");
            }

            @Override
            public void onAdLoaded() {
                Log.d(LOG_TAG, "onAdLoaded");
                adview.setVisibility(View.VISIBLE);

            }
        });
    }

    private String getErrorReason(int errorCode) {
        String errorReason = "";
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No fill";
                break;
        }
        return errorReason;
    }


    class CalculationMethodAdapter extends BaseAdapter {

        private Context mContext;
        String[] methods;
        String[] corrections;

        public CalculationMethodAdapter(Context context, String[] dataList, String[] corrections) {
            this.mContext = context;
            this.methods = dataList;
            this.corrections = corrections;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated methodIndex stub
            return methods.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated methodIndex stub
            return methods[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated methodIndex stub
            return position;
        }

        /* private view holder class */
        private class ViewHolder {
            public ImageView imgSelection;
            public TextView tvMehtods;
            public TextView tvMehtodsDegree;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated methodIndex stub

            ViewHolder holder = null;
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.row_calculation_methods, null);

                holder = new ViewHolder();

                holder.tvMehtods = (TextView) convertView.findViewById(R.id.tv_mehtod_names);
                holder.tvMehtodsDegree = (TextView) convertView.findViewById(R.id.tv_mehtod_names_degree);
                holder.imgSelection = (ImageView) convertView.findViewById(R.id.img_selection);

                holder.tvMehtods.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoR);
                holder.tvMehtodsDegree.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoR);
                holder.imgSelection.setVisibility(View.GONE);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvMehtods.setText(methods[position]);
            holder.tvMehtodsDegree.setText("" + corrections[position]);

            return convertView;
        }
    }
}
