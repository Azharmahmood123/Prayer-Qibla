package com.quranreading.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.quranreading.sharedPreference.AlarmSharedPref;
import com.quranreading.sharedPreference.TimeEditPref;

import java.util.Calendar;
import java.util.HashMap;

import quran.sharedpreference.SurahsSharedPref;

public class BootCompleteBroadcastReciever extends BroadcastReceiver {



	AlarmHelper mAlarmHelper;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		mAlarmHelper = new AlarmHelper(context);
		Calendar c = mAlarmHelper.setAlarmTime(10,30,"am");
		mAlarmHelper.setAlarmResetTimeReciever(c,12);

		// Set Ayah of the Notification
		SurahsSharedPref mSurahsSharedPref = new SurahsSharedPref(context);
		if(mSurahsSharedPref.isAyahNotification())
		{

			AlarmHelper mAlarmHelper = new AlarmHelper(context);
			mAlarmHelper.setAlarmAyahNotification(mAlarmHelper.setAlarmTime(mSurahsSharedPref.getAlarmHours(), mSurahsSharedPref.getAlarmMints(), ""), AlarmReceiverAyah.NOTIFY_AYAH_ALARM_ID);
		}
		else
		{
			AlarmHelper mAlarmHelper = new AlarmHelper(context);
			mAlarmHelper.cancelAlarmAyahNotification(AlarmReceiverAyah.NOTIFY_AYAH_ALARM_ID);
		}

		String[] prayerAlarmTimes = new String[6];
		boolean[] chkSavedAlarms = new boolean[6];

		AlarmSharedPref mAlarmSharedPref = new AlarmSharedPref(context);
		TimeEditPref timeEditPref = new TimeEditPref(context);

		HashMap<String, Boolean> alarm = mAlarmSharedPref.checkAlarms();
		HashMap<String, String> alarmTime = mAlarmSharedPref.getPrayerTimes();

		for (int index = 0; index < chkSavedAlarms.length; index++)
		{
			chkSavedAlarms[index] = alarm.get(AlarmSharedPref.CHK_PRAYERS[index]);
			prayerAlarmTimes[index] = timeEditPref.getAlarmNotifyTime(TimeEditPref.ALARMS_TIME_PRAYERS[index]);
		}

		int alarmIDPrayer;
		boolean checkFajar;

		for (int index = 0; index < chkSavedAlarms.length; index++)
		{
			checkFajar = false;
			// Alarm id 6 is used for Sunrize in previous versions and other like
			// fajar =1, zuhur= 2, asar =3, maghrib=4, isha = 5, sunrize = 6
			if(index == 0)
			{
				alarmIDPrayer = 1;
				checkFajar = true;
			}
			else if(index == 1)
			{
				alarmIDPrayer = 6;
			}
			else
			{
				alarmIDPrayer = index;
			}

			if(chkSavedAlarms[index])
			{
				breakTimeSetAlarm(alarmIDPrayer, prayerAlarmTimes[index], checkFajar);
			}
			else
			{
				mAlarmHelper.cancelAlarm(alarmIDPrayer);
			}
		}



		//Set Daily Alarm to Reset Prayer Time
		mAlarmHelper.setAlarmResetTimeReciever(mAlarmHelper.setAlarmTime(PrayerTimeUpdateReciever.ALARM_RESET_PRAYER_HOUR,PrayerTimeUpdateReciever.ALARM_RESET_PRAYER_MINUTES,PrayerTimeUpdateReciever.ALARM_RESET_PRAYER_AM_PM),PrayerTimeUpdateReciever.ALARM_RESET_PRAYER_ID);

	}

	private void breakTimeSetAlarm(int index, String time, boolean chkFajar) {

		if(time.split("\\s|:").length == 3) // Valid Time in
		{
			String hour = "";
			String min = "";
			String am_pm = "";
			String[] timeArray = time.split("\\s|:");
			if(timeArray.length == 3)
			{
				hour = timeArray[0];
				min = timeArray[1];
				am_pm = timeArray[2];

				Log.v("Alarm Time " + index, hour + ":" + min + " " + am_pm);
				Calendar c1 = mAlarmHelper.setAlarmTime(Integer.parseInt(hour.trim()), Integer.parseInt(min.trim()), am_pm);
				mAlarmHelper.setAlarmEveryDay(c1, index, chkFajar);
			}
			else
			{
				mAlarmHelper.cancelAlarm((index + 1));
			}
		}
		else
		{
			mAlarmHelper.cancelAlarm((index + 1));
		}
	}
}
