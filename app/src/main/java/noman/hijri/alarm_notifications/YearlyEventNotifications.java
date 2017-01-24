package noman.hijri.alarm_notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class YearlyEventNotifications {

    private Context context;
    private final int alarmId = 2233;
    int[] dateArray = {1, 10, 12, 1, 27, 1, 10, 11};
    int[] monthArray = {0, 0, 2, 8, 8, 9, 11, 11};

    public YearlyEventNotifications(Context context) {

        this.context = context;

    }

    public void setDailyAlarm1(int date, int month, int geo_year, int geo_month, int geo_date) {

        if ((date == 10 && month == 0) || (date == 27 && month == 8)) {
            Calendar c1 = setAlarmTime(6, 00, "pm", geo_year, geo_date, geo_month);
            setAlarmEveryDay(c1, date, month);

            //Its set all year dates and times
            Log.e("Evening Set", geo_year + "-" + date + "-" + month);
        } else {
            Calendar c1 = setAlarmTime(6, 00, "am", geo_year, geo_date, geo_month);
            setAlarmEveryDay(c1, date, month);
            //Its set all year dates and times
            Log.e("Morning Set", geo_year + "-" + geo_date + "-" + geo_month);
        }
    }



    public Calendar setAlarmTime(int hour, int min, String am_pm, int year, int date, int month) {
        Calendar time = Calendar.getInstance();
        if (hour == 12) {
            time.set(Calendar.HOUR, 0);
        } else {
            time.set(Calendar.HOUR, hour);
        }

        time.set(Calendar.MINUTE, min);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        time.set(Calendar.YEAR, year);
        time.set(Calendar.MONTH, month);
        time.set(Calendar.DAY_OF_MONTH, date);

        if (am_pm.equals("am")) {
            time.set(Calendar.AM_PM, Calendar.AM);
        } else {
            time.set(Calendar.AM_PM, Calendar.PM);
        }

        if (System.currentTimeMillis() > time.getTimeInMillis()) {
            long add = AlarmManager.INTERVAL_DAY;
            long oldTime = time.getTimeInMillis();
            time.setTimeInMillis(oldTime + add);// Okay, then tomorrow ...
        }
        return time;
    }


    public void cancelAlarm(int Id) {
        Intent intent = new Intent(context, AlarmReciever.class);
        intent.putExtra("alarmID", String.valueOf(Id));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public void setAlarmEveryDay(Calendar targetCal, int Id, int month) {

        Intent intent = new Intent(context, AlarmReciever.class);
        intent.putExtra("date", Id + "-" + month);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (Id + month), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}


