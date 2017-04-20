package noman.salattrack.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


import com.orhanobut.logger.BuildConfig;
import com.quranreading.helper.DataBaseHelper;

import noman.CommunityGlobalClass;
import noman.community.model.SignUpResponse;
import noman.community.utility.DebugInfo;
import noman.salattrack.activity.SalatTracking;
import noman.salattrack.model.SalatModel;
import noman.salattrack.model.SalatResponse;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Administrator on 3/17/2017.
 */

public class SalatTrackerDatabase {

    public static final String TBL_SALAT_TRACKER = "salat_tracker";
    private SQLiteDatabase db;

    private DataBaseHelper databaseHelper;


    private Context mContext;

    public SalatTrackerDatabase(Context context) {
        this.databaseHelper = new DataBaseHelper(context);
        this.mContext = context;
    }
    /// **********************  commmon used methods
    public ContentValues getContentValues(SalatModel model) {
        ContentValues cv = new ContentValues();
        // cv.put("id",model.getId());
        cv.put("user_id", model.getUser_id());
        cv.put("date", model.getDate());
        cv.put("month", model.getMonth());
        cv.put("year", model.getYear());
        cv.put("fajar", model.getFajar());
        cv.put("zuhar", model.getZuhar());
        cv.put("asar", model.getAsar());
        cv.put("magrib", model.getMagrib());
        cv.put("isha", model.getIsha());

        return cv;
    }
    public SalatModel getCursorModel(Cursor c) {
        SalatModel model = new SalatModel();
        model.setId(c.getInt(c.getColumnIndex("id")));
        model.setUser_id(c.getInt(c.getColumnIndex("user_id")));
        model.setDate(c.getInt(c.getColumnIndex("date")));
        model.setMonth(c.getInt(c.getColumnIndex("month")));
        model.setYear(c.getInt(c.getColumnIndex("year")));
        model.setFajar(c.getInt(c.getColumnIndex("fajar")));
        model.setZuhar(c.getInt(c.getColumnIndex("zuhar")));
        model.setAsar(c.getInt(c.getColumnIndex("asar")));
        model.setMagrib(c.getInt(c.getColumnIndex("magrib")));
        model.setIsha(c.getInt(c.getColumnIndex("isha")));
        return model;
    }
    public SalatModel getYearCursorModel(Cursor c) {
        SalatModel model = new SalatModel();

        model.setFajar(c.getInt(c.getColumnIndex("fajar")));
        model.setZuhar(c.getInt(c.getColumnIndex("zuhar")));
        model.setAsar(c.getInt(c.getColumnIndex("asar")));
        model.setMagrib(c.getInt(c.getColumnIndex("magrib")));
        model.setIsha(c.getInt(c.getColumnIndex("isha")));
        return model;
    }


//*********************************************************************888  Close Database

    public boolean insertSalatData(boolean isServer,SalatModel model) {

        if(isServer) {
            uploadToServer(model);
        }

        if (getSalatModel(model.getDate(), model.getMonth(), model.getYear(), model.getUser_id()) != null) {
            return updateSalatTracker(model);
        } else {
            //insert new row
            //  model.setId(getMaxID()+1);//set maximum id in row
            SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
            if (db == null)
                return false;


            long id = db.insert(TBL_SALAT_TRACKER, null, getContentValues(model));
            db.close();
            if (id > -1) {
                return true;
            } else {
                return false;
            }
        }



    }

     void uploadToServer(SalatModel model) {
         //  CommunityGlobalClass.getInstance().showLoading(this);
         Call<SalatResponse> call = CommunityGlobalClass.getRestApi().saveSalatData(model);
         call.enqueue(new retrofit.Callback<SalatResponse>() {

             @Override
             public void onResponse(Response<SalatResponse> response, Retrofit retrofit) {
                 if(response.body().getState() == true)
                 {
                     Log.e("Post","Data to server");
                 }
             }

             @Override
             public void onFailure(Throwable t) {
                 CommunityGlobalClass.getInstance().cancelDialog();
                 if (BuildConfig.DEBUG) DebugInfo.loggerException("salat insert -Failure" + t.getMessage());
                // CommunityGlobalClass.getInstance().showServerFailureDialog(mContext);
             }
         });



     }


    public SalatModel getSalatModel(int date, int month, int year, int userID) {
        SalatModel salatModelList = new SalatModel();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return null;
        Cursor c = db.rawQuery("SELECT * FROM " + TBL_SALAT_TRACKER + " where year = " + year +" and date = "+date +" and month = "+month +" and user_id ="+userID , null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    salatModelList = getCursorModel(c);
                } while (c.moveToNext());
            } else {
                return null;
            }
        }
        c.close();
        db.close();
        return salatModelList;
    }

    public boolean updateSalatTracker(SalatModel model) {
        ContentValues cv = getContentValues(model);
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return false;
        int va = db.update(TBL_SALAT_TRACKER, cv, "id =" + model.getId(), null);
        db.close();
        if (va < -1)
            return false;
        else
            return true;
    }



    public SalatModel getPrayedCountYearly(int year, int userID,int status) {
        SalatModel salatModelList = new SalatModel();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return null;

       Cursor c=db.rawQuery("select count(case when fajar = "+status+"  then 1 end) as fajar, count(case when zuhar ="+status+" then 1 end) as zuhar,count(case when asar ="+status+" then 1 end) as asar" +
                ",count(case when magrib ="+status+" then 1 end) as magrib,count(case when isha ="+status+" then 1 end) as isha from "+TBL_SALAT_TRACKER+" where year = "+year +" and user_id = "+userID,null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    salatModelList = getYearCursorModel(c);
                } while (c.moveToNext());
            } else {
                return null;
            }
        }
        c.close();
        db.close();
        return salatModelList;
    }


    public SalatModel getPrayedCountMonthly(int month,int year, int userID,int status) {
        SalatModel salatModelList = new SalatModel();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return null;

        Cursor c=db.rawQuery("select count(case when fajar = "+status+"  then 1 end) as fajar, count(case when zuhar ="+status+" then 1 end) as zuhar,count(case when asar ="+status+" then 1 end) as asar" +
                ",count(case when magrib ="+status+" then 1 end) as magrib,count(case when isha ="+status+" then 1 end) as isha from "+TBL_SALAT_TRACKER+" where year = "+year +" and user_id = "+userID +" and month="+month,null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    salatModelList = getYearCursorModel(c);
                } while (c.moveToNext());
            } else {
                return null;
            }
        }
        c.close();
        db.close();
        return salatModelList;
    }




    public SalatModel getPrayedCountByDate(int start,int end,int month ,int year,int status) {
        SalatModel salatModelList = new SalatModel();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return null;

        Cursor c=db.rawQuery("select count(case when fajar = "+status+"  then 1 end) as fajar, count(case when zuhar ="+status+" then 1 end) as zuhar,count(case when asar ="+status+" then 1 end) as asar" +
                ",count(case when magrib ="+status+" then 1 end) as magrib,count(case when isha ="+status+" then 1 end) as isha from "+TBL_SALAT_TRACKER+" where year = " + year +" and month = "+month +" and date BETWEEN "+start +" and "+end,null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    salatModelList = getYearCursorModel(c);
                } while (c.moveToNext());
            } else {
                return null;
            }
        }
        c.close();
        db.close();
        return salatModelList;
    }
}

