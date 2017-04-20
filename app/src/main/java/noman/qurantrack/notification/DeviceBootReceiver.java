package noman.qurantrack.notification;

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

        DailyAlarmHelper mAlarm = new DailyAlarmHelper(context);
        Calendar updateTime= mAlarm.setAlarmTime(8,00,"pm");
        mAlarm.setAlarmDailyDay(updateTime);
    }
}
