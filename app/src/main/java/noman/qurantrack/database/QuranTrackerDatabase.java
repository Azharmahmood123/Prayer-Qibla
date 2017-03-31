package noman.qurantrack.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.orhanobut.logger.BuildConfig;
import com.quranreading.helper.DataBaseHelper;

import noman.CommunityGlobalClass;
import noman.community.utility.DebugInfo;
import noman.quran.model.JuzModel;
import noman.qurantrack.model.QuranTrackerModel;
import noman.salattrack.model.SalatModel;
import noman.salattrack.model.SalatResponse;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Administrator on 3/17/2017.
 */

public class QuranTrackerDatabase {

    public static final String TBL_QURAN_TRACKER = "quran_tracker";
    private SQLiteDatabase db;

    private DataBaseHelper databaseHelper;


    private Context mContext;

    public QuranTrackerDatabase(Context context) {
        this.databaseHelper = new DataBaseHelper(context);
        this.mContext = context;
    }
    /// **********************  commmon used methods
    public ContentValues getContentValues(QuranTrackerModel model) {
        ContentValues cv = new ContentValues();
        // cv.put("id",model.getId());
        cv.put("user_id", model.getUser_id());
        cv.put("date", model.getDate());
        cv.put("month", model.getMonth());
        cv.put("year", model.getYear());
        cv.put("surah_no", model.getSurahNo());
        cv.put("ayah_no", model.getAyahNo());
        cv.put("count", model.getVerses());

        return cv;
    }
    public QuranTrackerModel getCursorModel(Cursor c) {
        QuranTrackerModel model = new QuranTrackerModel();
        model.setId(c.getInt(c.getColumnIndex("id")));
        model.setUser_id(c.getInt(c.getColumnIndex("user_id")));
        model.setDate(c.getInt(c.getColumnIndex("date")));
        model.setMonth(c.getInt(c.getColumnIndex("month")));
        model.setYear(c.getInt(c.getColumnIndex("year")));
        model.setAyahNo(c.getInt(c.getColumnIndex("ayah_no")));
        model.setSurahNo(c.getInt(c.getColumnIndex("surah_no")));
        model.setVerses(c.getInt(c.getColumnIndex("count")));
        return model;
    }



//*********************************************************************888  Close Database

    public boolean insertSalatData(QuranTrackerModel model) {
        if (getQuranTrackModel(model.getDate(), model.getMonth(), model.getYear(), model.getUser_id()) != null) {
            model.setId(getQuranTrackModel(model.getDate(), model.getMonth(), model.getYear(), model.getUser_id()).getId());
            return updateSalatTracker(model);
        } else {
            //insert new row
            SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
            if (db == null)
                return false;

            long id = db.insert(TBL_QURAN_TRACKER, null, getContentValues(model));
            db.close();
            if (id > -1) {
                return true;
            } else {
                return false;
            }
        }
    }
    public QuranTrackerModel getQuranTrackModel(int date, int month, int year, int userID) {
        QuranTrackerModel salatModelList = new QuranTrackerModel();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return null;
        Cursor c = db.rawQuery("SELECT * FROM " + TBL_QURAN_TRACKER + " where year = " + year +" and date = "+date +" and month = "+month +" and user_id ="+userID , null);
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
    public boolean updateSalatTracker(QuranTrackerModel model) {
        ContentValues cv = getContentValues(model);
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return false;
        int va = db.update(TBL_QURAN_TRACKER, cv, "id =" + model.getId(), null);
        db.close();
        if (va < -1)
            return false;
        else
            return true;
    }
    public int getMaxSurrah() {
        int countr = 0;

        String sqlStatement = "select MAX(surah_no) as surah from quran_tracker";
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return 0;

        Cursor c = db.rawQuery(sqlStatement, null);

        if (c.moveToFirst()) {
            do {
                countr =  c.getInt(c.getColumnIndex("surah"));

            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return countr;
    }
    public int getLastAyah(int surahNo) {
        int countr = 0;

        String sqlStatement = "select MAX(ayah_no) as aya from quran_tracker where surah_no ="+surahNo;
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return 0;

        Cursor c = db.rawQuery(sqlStatement, null);

        if (c.moveToFirst()) {
            do {
                countr =  c.getInt(c.getColumnIndex("aya"));

            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return countr;
    }

    public int getSumVerse() {
        int countr = 0;

        String sqlStatement = "select SUM(count) as count from quran_tracker";
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return 0;

        Cursor c = db.rawQuery(sqlStatement, null);

        if (c.moveToFirst()) {
            do {
                countr =  c.getInt(c.getColumnIndex("count"));

            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return countr;
    }


    public int getWeeklySumVerse(int start,int end,int month,int year,int userID) {
        int countr = 0;

        String sqlStatement = "select SUM(count) as count from quran_tracker where year = " + year +" and month = "+month +" and user_id ="+userID+" and date BETWEEN "+start +" and "+end ;

        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return 0;

        Cursor c = db.rawQuery(sqlStatement, null);

        if (c.moveToFirst()) {
            do {
                countr =  c.getInt(c.getColumnIndex("count"));

            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return countr;
    }

    public int getMonthSumVerse(int month,int year,int userID) {
        int countr = 0;

        String sqlStatement = "select SUM(count) as count from quran_tracker where year = " + year +" and month = "+month +" and user_id ="+userID;

        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return 0;

        Cursor c = db.rawQuery(sqlStatement, null);

        if (c.moveToFirst()) {
            do {
                countr =  c.getInt(c.getColumnIndex("count"));

            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return countr;
    }
}

