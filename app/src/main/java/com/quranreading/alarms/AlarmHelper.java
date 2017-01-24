package com.quranreading.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmHelper {
	public Context mContext;

	public AlarmHelper(Context context) {
		this.mContext = context;
	}

	public Calendar setAlarmTime(int hour, int min, String am_pm) {
		Calendar time = Calendar.getInstance();

		if(hour == 12)
		{
			// time.set(Calendar.HOUR_OF_DAY, hour);
			time.set(Calendar.HOUR, 0);
		}
		else
		{
			time.set(Calendar.HOUR, hour);
		}

		time.set(Calendar.MINUTE, min);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);

		if(!am_pm.isEmpty())
		{
			if(am_pm.toLowerCase().equals("am"))
			{
				time.set(Calendar.AM_PM, Calendar.AM);
			}
			else
			{
				time.set(Calendar.AM_PM, Calendar.PM);
			}
		}

		if(System.currentTimeMillis() > time.getTimeInMillis())
		{
			long add = AlarmManager.INTERVAL_DAY;
			long oldTime = time.getTimeInMillis();
			time.setTimeInMillis(oldTime + add);// Okay, then tomorrow ...
		}

		return time;
	}

	public void setAlarmEveryDay(Calendar targetCal, int id, boolean chkFajar) {
		Intent intent = new Intent(mContext, AlarmReceiverPrayers.class);
		intent.putExtra("ID", String.valueOf(id));
		intent.putExtra("CHKFAJAR", chkFajar);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
	}

	public void cancelAlarm(int id) {
		Intent intent = new Intent(mContext, AlarmReceiverPrayers.class);
		intent.putExtra("ID", String.valueOf(id));

		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		pendingIntent.cancel();
	}

	//////////////////////////
	///////////////////////////
	//////////////////////////////////
	/////////////////////////////////////
	///////////////////////////////////

	public void setAlarmAyahNotification(Calendar targetCal, int id) {
		Intent intent = new Intent(mContext, AlarmReceiverAyah.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
	}

	public void cancelAlarmAyahNotification(int id) {
		Intent intent = new Intent(mContext, AlarmReceiverAyah.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		pendingIntent.cancel();
	}




	public void setAlarmResetTimeReciever(Calendar targetCal, int id) {
		Intent intent = new Intent(mContext, PrayerTimeUpdateReciever.class);


		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
	}


	public void cancelAlarmResetTimeReciever(int id) {
		Intent intent = new Intent(mContext, AlarmReceiverAyah.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		pendingIntent.cancel();
	}
}
