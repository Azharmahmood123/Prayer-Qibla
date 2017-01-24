package com.quranreading.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.quranreading.helper.CalculatePrayerTime;
import com.quranreading.helper.TimeFormateConverter;
import com.quranreading.sharedPreference.AlarmSharedPref;
import com.quranreading.sharedPreference.LocationPref;
import com.quranreading.sharedPreference.TimeEditPref;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by cyber on 12/27/2016.
 */

public class PrayerTimeUpdateReciever extends BroadcastReceiver {

    public static final int ALARM_RESET_PRAYER_ID = 55;
    public static final int ALARM_RESET_PRAYER_HOUR = 12;
    public static final int ALARM_RESET_PRAYER_MINUTES = 3;
    public static final String ALARM_RESET_PRAYER_AM_PM = "am";


    private ArrayList<String> prayerTimingsDefault;

    private AlarmHelper mAlarmHelper;
    private CalculatePrayerTime mCalculatePrayerTime;
    AlarmSharedPref mAlarmSharedPref;
    TimeEditPref timeEditPref;

    private boolean[] chkAlarmsSaved = new boolean[6];
    private boolean[] chkAlarmsTemp = new boolean[6];
    String[] prayerTimes = new String[6];
    String[] prayerAlarmTimes = new String[6];

    @Override
    public void onReceive(Context context, Intent intent) {

        mAlarmHelper = new AlarmHelper(context);
        mCalculatePrayerTime = new CalculatePrayerTime(context);
        mAlarmSharedPref = new AlarmSharedPref(context);
        timeEditPref = new TimeEditPref(context);

        LocationPref locationPref = new LocationPref(context);

        double latitude = Double.parseDouble(locationPref.getLatitude());
        double longitude = Double.parseDouble(locationPref.getLongitude());

        getTimings(latitude, longitude);
    }

    public void getTimings(double latitude, double longitude) {

        Date now = new Date();
        Calendar mCalender = Calendar.getInstance();
        mCalender.setTime(now);

        prayerTimingsDefault = mCalculatePrayerTime.NamazTimings(mCalender, latitude, longitude);

        for (int index = 0; index < 6; index++) {

            String timePrayer, timeNotification;

            String lastTime = mAlarmSharedPref.getPrayerTime(AlarmSharedPref.TIME_PRAYERS[index]);
            prayerTimes[index] = lastTime;
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


            prayerAlarmTimes[index] = timeNotification;
            ////////////////////////////
            /////////////////////////////
            ////////////////////////////

            mAlarmSharedPref.savePrayerTime(AlarmSharedPref.TIME_PRAYERS[index], prayerTimingsDefault.get(index));

        }

        chkAlarms();
        setAlarm();
    }


    public void chkAlarms() {
        HashMap<String, Boolean> alarm = mAlarmSharedPref.checkAlarms();

        for (int index = 0; index < 6; index++) {
            chkAlarmsSaved[index] = alarm.get(AlarmSharedPref.CHK_PRAYERS[index]);
            chkAlarmsTemp[index] = chkAlarmsSaved[index];
        }
    }

    public void setAlarm() {
        int alarmIDPrayer;
        boolean checkFajar = false;

        for (int index = 0; index < 6; index++) {
            if (prayerAlarmTimes[index].length() > 0 && !prayerAlarmTimes[index].contains("-----")) {
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

                    String prayerTime = prayerTimes[index];
                    String alarmTime = prayerAlarmTimes[index];

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
}
