package noman.salattrack.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.quranreading.qibladirection.MainActivityNew;
import com.quranreading.qibladirection.R;

import java.util.Calendar;

import noman.CommunityGlobalClass;
import noman.salattrack.database.SalatTrackerDatabase;
import noman.salattrack.model.SalatModel;

/**
 * Created by Administrator on 4/19/2017.
 */

public class ReceiveAlert extends BroadcastReceiver {


    private Context context;
    private Notification notification;
    int late, pray;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        Calendar curCalendar = Calendar.getInstance();
        int curYear = curCalendar.get(Calendar.YEAR);
        int curMonth = curCalendar.get(Calendar.MONTH) + 1;
        int curDate = curCalendar.get(Calendar.DAY_OF_MONTH);
                         curCalendar.add(Calendar.DATE, -5);
        int startdate=curCalendar.get(Calendar.DAY_OF_MONTH);

                SalatTrackerDatabase salatTrackerDatabase = new SalatTrackerDatabase(context);
        SalatModel mSalatModel = salatTrackerDatabase.getPrayedCountByDate(startdate,curDate,curMonth,curYear, 1);//Missed
        if (mSalatModel != null) {
            late = late + mSalatModel.getFajar();
            late = late + mSalatModel.getZuhar();
            late = late + mSalatModel.getAsar();
            late = late + mSalatModel.getMagrib();
            late = late + mSalatModel.getIsha();

        }

        mSalatModel = salatTrackerDatabase.getPrayedCountByDate(startdate,curDate,curMonth,curYear, 2);//prayed
        if (mSalatModel != null) {
            pray = pray + mSalatModel.getFajar();
            pray = pray + mSalatModel.getZuhar();
            pray = pray + mSalatModel.getAsar();
            pray = pray + mSalatModel.getMagrib();
            pray = pray + mSalatModel.getIsha();
        }

        //Getting missed prayed
        if(late+pray < 15 && CommunityGlobalClass.mSignInRequests !=null) {
            showNotification(context.getResources().getString(R.string.title_notificaiton_exceed_prayer),
                    context.getResources().getString(R.string.msg_notificaiton_exceed_prayer));
        }
    }


    public void showNotification(String eventName, String msg) {

        Intent intent = new Intent(context, MainActivityNew.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new NotificationCompat.Builder(context)
                .setContentTitle(eventName)
                .setContentText(msg)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notification_small)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(101, notification);


    }
}
