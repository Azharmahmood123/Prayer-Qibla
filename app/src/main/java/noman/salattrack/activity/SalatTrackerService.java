package noman.salattrack.activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Calendar;

import noman.CommunityGlobalClass;
import noman.salattrack.database.SalatTrackerDatabase;
import noman.salattrack.model.SalatModel;

/**
 * Created by Administrator on 3/21/2017.
 */

public class SalatTrackerService extends Service {

    int status = 0;
    public static int prayerId = 0; //because i didnot get intent in service
    SalatModel mSalatModel;

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

       // prayerId = intent.getIntExtra(TrackerConstant.ACTION.TIME_PRAYER,0);

        stopForeground(true);
        stopSelf();
        saveDataLocaly();
        return START_STICKY;
    }

    public void saveDataLocaly() {
        SalatTrackerDatabase salatTrackerDatabase = new SalatTrackerDatabase(this);
        Calendar calendar = Calendar.getInstance();
        int yearDB = calendar.get(Calendar.YEAR);
        int monthDB = calendar.get(Calendar.MONTH) + 1;
        int dateDB = calendar.get(Calendar.DAY_OF_MONTH);

      //  String userId = CommunityGlobalClass.mSignInRequests.getUser_id();
        int useId = 0;//Integer.parseInt(userId);

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

        salatTrackerDatabase.insertSalatData(mSalatModel); //in local database

    }

    public void checkStatusPrayer() {
        switch (prayerId) {
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