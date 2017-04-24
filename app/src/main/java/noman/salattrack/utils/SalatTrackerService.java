package noman.salattrack.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.quranreading.alarms.AlarmReceiverPrayers;

import noman.CommunityGlobalClass;
import noman.salattrack.database.SalatTrackerDatabase;
import noman.salattrack.model.SalatModel;

/**
 * Created by Administrator on 3/21/2017.
 */

public class SalatTrackerService extends Service {

    int status = 0;

    SalatModel mSalatModel;
    int notifyId = 0;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(TrackerConstant.ACTION.PRAY_ACTION)) {
            status = 2;
        } else if (intent.getAction().equals(TrackerConstant.ACTION.LATE_ACTION)) {
            status = 1;
        }


        //notifyId = Integer.parseInt(intent.getData().toString());
        String data = intent.getData().toString();
        String[] parts = data.split("/");

        notifyId  = Integer.parseInt(parts[0]);
        int date  = Integer.parseInt(parts[1]);
        int month = Integer.parseInt(parts[2]);
        int year  = Integer.parseInt(parts[3]);

        //Log.e("here is 0", "" + date + "/" + month + "/" + year);
        stopForeground(true);
        stopSelf();
        saveDataLocaly(date, month, year);
        return START_STICKY;
    }

    public void saveDataLocaly(int dateDB, int monthDB, int yearDB) {
        SalatTrackerDatabase salatTrackerDatabase = new SalatTrackerDatabase(this);
        /*Calendar calendar = Calendar.getInstance();
        int yearDB = calendar.get(Calendar.YEAR);
        int monthDB = calendar.get(Calendar.MONTH) + 1;
        int dateDB = calendar.get(Calendar.DAY_OF_MONTH);
*/
        int useId = CommunityGlobalClass.mSignInRequests.getUser_id();
        mSalatModel = salatTrackerDatabase.getSalatModel(dateDB, monthDB, yearDB, useId);

        if (mSalatModel != null) {

        } else {
            mSalatModel = new SalatModel();
            mSalatModel.setUser_id(useId);
            mSalatModel.setDate(dateDB);
            mSalatModel.setMonth(monthDB);
            mSalatModel.setYear(yearDB);
        }

        checkStatusPrayer();

        salatTrackerDatabase.insertSalatData(true, mSalatModel); //in local database


        AlarmReceiverPrayers.cancelNotification(notifyId);

    }

    public void checkStatusPrayer() {
        switch (notifyId) {
            case 1:
                mSalatModel.setFajar(status);
                break;
            case 2:
                mSalatModel.setZuhar(status);
                break;
            case 3:
                mSalatModel.setAsar(status);
                break;
            case 4:
                mSalatModel.setMagrib(status);
                break;
            case 5:
                mSalatModel.setIsha(status);
                break;


        }
    }

}