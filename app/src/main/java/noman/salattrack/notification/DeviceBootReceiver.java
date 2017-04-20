package noman.salattrack.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;



/**
 * Created by Administrator on 4/19/2017.
 */

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        FiveDayAlarmHelper mAlarm = new FiveDayAlarmHelper(context);
        Calendar updateTime= mAlarm.setAlarmTime(10,00,"am");
        mAlarm.setAlarmFiveDay(updateTime);
    }
}
