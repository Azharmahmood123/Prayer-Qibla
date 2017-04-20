package noman.salattrack.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.quranreading.alarms.AlarmReceiverAyah;
import com.quranreading.alarms.AlarmReceiverPrayers;
import com.quranreading.alarms.PrayerTimeUpdateReciever;

import java.util.Calendar;

import static android.R.attr.id;

public class FiveDayAlarmHelper {
	public Context mContext;

	public FiveDayAlarmHelper(Context context) {
		this.mContext = context;
	}

	public Calendar setAlarmTime(int hour, int min, String am_pm) {
		Calendar time = Calendar.getInstance();

		if(hour == 12)
		{
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
			time.setTimeInMillis(oldTime + add);// Okay, then 2nd Day ...
		}
		return time;
	}

	public void setAlarmFiveDay(Calendar targetCal) {
		Intent intent = new Intent(mContext, ReceiveAlert.class);
		long days= 5*24*60*60*1000; //Repeat every 5th day
		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), days, pendingIntent);
	}

	public void cancelAlarm() {
		Intent intent = new Intent(mContext, ReceiveAlert.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		pendingIntent.cancel();
	}


}
