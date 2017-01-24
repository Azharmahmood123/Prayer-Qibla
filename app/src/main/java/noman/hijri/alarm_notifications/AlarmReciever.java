package noman.hijri.alarm_notifications;

/**
 * Created by Aamir Riaz on 7/22/2015.
 */


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;
import com.quranreading.qibladirection.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import noman.hijri.acitivity.CalenderActivity;

public class AlarmReciever extends BroadcastReceiver {

    private Context context;
    private Notification notification;

    int[] dateArray = {1, 10, 12, 1, 27, 1, 10, 11};
    int[] monthArray = {0, 0, 2, 8, 8, 9, 11, 11};
    String[] hijriEventsArray;
    String[] description = {
            "\"Islamic New Year\" starts today",
            "Tomorrow is the day of \"Ashura\"",
            "Happy Eid Milad-un-Nabi",
            "Ramadan Mubarak",
            "Tonight could be the great night of \"Laylat-ul-Qadar\"",
            "Eid Mubarak",
            "Today is the great day of \"Hajj\"",
            "Eid-ul-Adha Mubarak"
    };
    int Hijri_month, Hijri_date;//Hijri's

    public void getCurrentHijriDate() {
        String[] months = context.getResources().getStringArray(R.array.islamic_month_name);//For testing
        Calendar _calendarLocal = Calendar.getInstance(Locale.getDefault());
        Hijri_month = _calendarLocal.get(Calendar.MONTH);
        int year = _calendarLocal.get(Calendar.YEAR);
        //_calendarLocal.set(Calendar.DAY_OF_WEEK_IN_MONTH,(_calendarLocal.get(Calendar.DAY_OF_WEEK_IN_MONTH)+ 1));
        _calendarLocal.set(Calendar.DAY_OF_MONTH, (_calendarLocal.get(Calendar.DAY_OF_MONTH) - 1)); //For date issue

        //New changes here
        GregorianCalendar gCal = new GregorianCalendar(year, Hijri_month, _calendarLocal.get(Calendar.DAY_OF_MONTH));

        Calendar uCal = new UmmalquraCalendar();
        uCal.setTime(gCal.getTime());
        Calendar _calendar = uCal;
        year = _calendar.get(Calendar.YEAR);
        Hijri_month = _calendar.get(Calendar.MONTH);

        //********************************

        //**** Save Hijri current date here with following
        SimpleDateFormat dateFormat = new SimpleDateFormat("", Locale.ENGLISH);
        dateFormat.setCalendar(_calendar);
        dateFormat.applyPattern("d");
        int cDate = Integer.parseInt(dateFormat.format(_calendar.getTime()));
        dateFormat.applyPattern("y");
        int cYear = Integer.parseInt(dateFormat.format(_calendar.getTime()));
        Hijri_date=cDate;
        String currentDate = cDate + " " + months[Hijri_month] + ", " + cYear;
      //  Log.e("Alaram date", currentDate);
        //**********************************
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        hijriEventsArray = context.getResources().getStringArray(R.array.event_name);
        String date = intent.getExtras().getString("date");
        String[] dateSplit = date.split("-");
        getCurrentHijriDate();
        for (int i = 0; i < dateArray.length; i++) {
           // if (Hijri_date.equals("" + dateArray[i]) && Hijri_month.equals("" + monthArray[i])) {
            if ( Hijri_date == dateArray[i] && Hijri_month == monthArray[i]) {
                showNotification(hijriEventsArray[i], description[i]);
            }
        }

    }


    public void showNotification(String eventName, String msg) {

        Intent intent = new Intent(context, CalenderActivity.class);
        intent.putExtra("from", "notificaton");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new NotificationCompat.Builder(context)
                .setContentTitle(eventName)
                .setContentText(msg)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true).setSmallIcon(R.drawable.ic_launcher)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, notification);


    }


}

