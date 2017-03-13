package com.quranreading.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.quranreading.qibladirection.R;
import com.quranreading.sharedPreference.QiblaDirectionPref;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import noman.Tasbeeh.model.TasbeehModel;
import noman.searchquran.model.TopicList;
import noman.searchquran.model.TopicModel;


public class DBManager {

    private final Context context;

    public static final String TABLE_URLS = "Urls";
    public static final String FLD_ASIA = "asia";
    public static final String FLD_EU = "eu";
    public static final String FLD_US = "us";
    public static final String FLD_MODULE = "module";
    public static final String MODULE_QURAN = "Quran";
    public static final String MODULE_DUAS = "Duas";
    public static final String MODULE_NAMES = "Names";

    ///////////////////////////// Column Names //////////////////////////////

    public static final String FLD_ID = "_id";
    public static final String FLD_COUNTRY = "country";
    public static final String FLD_CITY = "city";
    public static final String FLD_LATITUDE = "latitude";
    public static final String FLD_LONGITUDE = "longitude";
    public static final String FLD_TIME_ZONE = "time_zone";


    ///////////////////////////// COUNTRY CODE COLOUMNS //////////////////////////////
    public static final String FLD_CODE = "id";
    public static final String FLD_COUNTRY_NAME = "value";
    public static final String FLD_TIME_DIFF = "time_diff";

    ///////////////////////Search Topics Columns//////////////////////////

    //TOPIC NAMES
    public static final String FLD_TOPIC_NAME_ID = "id";
    public static final String FLD_TOPIC_NAME = "name";


    //TOPICS DETAIL
    public static final String FLD_TOPIC_DETAIL_ID = "id";
    public static final String FLD_TOPIC_DETAIL_TOPIC_ID = "name_id";
    public static final String FLD_TOPIC_DETAIL_VERSE = "verse_id";
    public static final String FLD_TOPIC_DETAIL_SURAH = "surah_id";
    public static final String FLD_TOPIC_DETAIL_PARA = "para_id";


    /////////////////////////// Dtabase And Table Name ///////////////////////////

    public static final String DATABASE_NAME = "cities_info_db";
    public static final String TBL_CITIESINFO = "cities_info";
    public static final String TBL_COUNTRY_CODES = "list";

    public static final String TBL_TOPICS_NAME = "tbl_topics";
    public static final String TBL_TOPICS_DETAIL = "tbl_topics_detail";
    ///////////////////////////// Table Create Queries ///////////////////////////


    //public static final int DATABASE_VERSION = 16;//for 4.3 version name
      public static final int DATABASE_VERSION = 17;


	/*
     * private static final String CREATE_TBLLOCATION =
	 * 
	 * "create table " + TBL_LOCATION + "(" + FLD_ID + " integer not null, " + FLD_NAME + " text not null, " + FLD_ALARM + " integer not null);";
	 */

    ///////////////////////// HELPER CLASS TO CREATE DATABASE //////////////////////

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    QiblaDirectionPref qiblaPref;

    public DBManager(Context ctx) {
        this.context = ctx;
        qiblaPref = new QiblaDirectionPref(context);
        DBHelper = new DatabaseHelper(context);
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    ///////////////////////////// Opens Database ////////////////////////////

    public DBManager open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }


    ///////////////////// Get Cities Location Info /////////////////////////

    public Cursor getCountryCodes(String country) throws SQLException {
        return db.query(true, TBL_COUNTRY_CODES, new String[]{FLD_CODE, FLD_COUNTRY_NAME,FLD_TIME_DIFF}, FLD_COUNTRY_NAME + "='" + country + "' COLLATE NOCASE", null, null, null, null, null);
    }
    public Cursor getCountrybyCodes(String code) throws SQLException {
        return db.query(true, TBL_COUNTRY_CODES, new String[]{FLD_CODE, FLD_COUNTRY_NAME,FLD_TIME_DIFF}, FLD_CODE + "='" + code + "' COLLATE NOCASE", null, null, null, null, null);
    }

    ///////////////////// Get Cities Location Info /////////////////////////
    public Cursor getCountry(String lat, String lon) throws SQLException {
        return db.query(true, TBL_CITIESINFO, new String[]{FLD_COUNTRY, FLD_CITY, FLD_LATITUDE, FLD_LONGITUDE, FLD_TIME_ZONE}, FLD_LONGITUDE + " like '" + lon + "%' COLLATE NOCASE AND " + FLD_LATITUDE + " like '" + lat + "%'" + " COLLATE NOCASE", null, null, null, null, null);
    }

    public Cursor getCityInfo(String country, String city) throws SQLException {
        return db.query(true, TBL_CITIESINFO, new String[]{FLD_COUNTRY, FLD_CITY, FLD_LATITUDE, FLD_LONGITUDE, FLD_TIME_ZONE}, FLD_COUNTRY + "='" + country + "' COLLATE NOCASE AND " + FLD_CITY + "='" + city + "'" + " COLLATE NOCASE", null, null, null, null, null);
    }

    public Cursor getTimeZone(String city, String lat, String lon) throws SQLException {
        return db.query(true, TBL_CITIESINFO, new String[]{FLD_COUNTRY, FLD_CITY, FLD_LATITUDE, FLD_LONGITUDE, FLD_TIME_ZONE}, FLD_CITY + "='" + city + "' COLLATE NOCASE  AND " + FLD_LONGITUDE + " like '" + lon + "%' COLLATE NOCASE AND " + FLD_LATITUDE + " like '" + lat + "%'" + " COLLATE NOCASE", null, null, null, null, null);
    }
    ///////////////////// Get Cities/Countries For AutoCompleteText /////////////////////////

    public Cursor getAllCities() {
        return db.query(true, TBL_CITIESINFO, new String[]{FLD_CITY, FLD_COUNTRY}, null, null, null, null, null, null);
    }


    public Cursor getMatchingStates(String constraint, String from) {
        if (constraint != null) {
            // Query for any rows where the state name begins with the
            // string specified in constraint.
            //
            // NOTE:
            // If wildcards are to be used in a rawQuery, they must appear
            // in the query parameters, and not in the query string proper.
            // See http://code.google.com/p/android/issues/detail?id=3153
            constraint = constraint.trim() + "%";
        }

        String params[] = {constraint};

        if (constraint == null) {
            // If no parameters are used in the query,
            // the params arg must be null.
            params = null;
        }

        try {
            Cursor cursor = db.query(true, TBL_CITIESINFO, new String[]{FLD_ID, from}, from + " LIKE ?", params, from, null, null, null);

            if (cursor != null) {
                // startManagingCursor(cursor);
                // cursor.moveToFirst();
                return cursor;
            }
        } catch (Exception e) {
            Log.e("AutoCompleteDbAdapter", e.toString());
        }

        return null;
    }

    ///////////////////// Get Info /////////////////////////

    public String getUrl(String area, String module) throws SQLException {

        String url = "";

        Cursor c = db.query(TABLE_URLS, new String[]{area}, FLD_MODULE + "='" + module + "' COLLATE NOCASE ", null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            url = c.getString(0);
            c.close();
        }

        return url;
    }

    ////////////////////////////////////////
    ///////////////////////////////////////


    ///////////////////// Get Cities/Countries For AutoCompleteText /////////////////////////


    ////////////////////////////// FUNCTIONS ////////////////////////////////

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        int version = qiblaPref.chkDbVersion();

        if (dbExist) {
            if (version <= DATABASE_VERSION) {
                copyDataBase();
                qiblaPref.setDbVersion(DATABASE_VERSION + 1);
            } else if (DATABASE_VERSION > version) {
                copyDataBase();
                qiblaPref.setDbVersion(DATABASE_VERSION + 1);
            }
        } else {
            // By calling this method and empty database will be created into the default system path
            // of your application so we are gonna be able to overwrite that database with our database.
            DBHelper.getReadableDatabase();

            try {
                copyDataBase();
                qiblaPref.setDbVersion(DATABASE_VERSION + 1);
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = context.getDatabasePath(DATABASE_NAME).getPath().toString();
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

            // checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
            // database does't exist yet.
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    public void copyDataBase() throws IOException {
        String DB_PATH = context.getDatabasePath(DATABASE_NAME).getPath().toString();

        // Open your local db as the input stream
        String filePath = setDatabase();
        if (filePath != null) {
            File d = new File(filePath);
            InputStream myInput = new FileInputStream(d);

            // Path to the just created empty db
            String outFileName = DB_PATH;

            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            qiblaPref.setDatabaseCopied(true);
            Log.e("TimeZone", "Cities copied");
            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        deleteFiles();
    }

    private String setDatabase() {
        String name = "";//context.getString(R.string.admob_id_testing);
        String path = null;
        String filePath = context.getFilesDir().toString() + "/myfiles/cities_info_db";
        try {
            AssetManager am = context.getAssets();
            InputStream inputStream = am.open("cities_info_db.zip");
            File src = createFileFromInputStream(inputStream);

            ZipFile zipFile = new ZipFile(src);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(name);
            }
            String dest = new String(context.getFilesDir().toString() + "/myfiles");
            zipFile.extractAll(dest);
            path = filePath;
        } catch (IOException e) {
            path = null;
            deleteFiles();
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ZipException e) {
            path = null;
            deleteFiles();
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return path;
    }

    private File createFileFromInputStream(InputStream inputStream) {

        File myDoc = new File(context.getFilesDir().toString() + "/cities_info_db.zip");

        try {
            OutputStream outputStream = new FileOutputStream(myDoc);
            byte buffer[] = new byte[1024];
            int length = 0;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return myDoc;
        } catch (IOException e) {
            if (myDoc.exists()) {
                myDoc.delete();
            }
            e.printStackTrace();
        }

        return null;
    }

    private void deleteFiles() {
        // TODO Auto-generated method stub
        String filePath = context.getFilesDir().toString() + "/myfiles/cities_info_db";
        String folderPath = context.getFilesDir().toString() + "/myfiles";
        String zipPath = context.getFilesDir().toString() + "/cities_info_db.zip";

        File file = new File(filePath);
        File folder = new File(folderPath);
        File zip = new File(zipPath);

        if (file != null) {
            if (file.exists()) {
                file.delete();
            }
        }

        if (folder != null) {

            if (folder.exists()) {
                folder.delete();
            }
        }

        if (zip != null) {
            if (zip.exists()) {
                zip.delete();
            }
        }
    }


    //SErach topic list data operation

    public List<TopicList> getTopicList() {

        List<TopicList> topicModelList = new ArrayList<>();


        Cursor c = db.rawQuery("SELECT * FROM " + TBL_TOPICS_NAME, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    TopicList model = new TopicList();
           /* int id = cCode.getInt(cCode.getColumnIndex(FLD_TOPIC_DETAIL_ID));
            int topicId = cCode.getInt(cCode.getColumnIndex(FLD_TOPIC_DETAIL_TOPIC_ID));
            int paraId = cCode.getInt(cCode.getColumnIndex(FLD_TOPIC_DETAIL_PARA));
            int surahId = cCode.getInt(cCode.getColumnIndex(FLD_TOPIC_DETAIL_SURAH));
            int verseId = cCode.getInt(cCode.getColumnIndex(FLD_TOPIC_DETAIL_VERSE));*/

                    model.setId(c.getInt(c.getColumnIndex(FLD_TOPIC_NAME_ID)));
                    model.setTopicName(c.getString(c.getColumnIndex(FLD_TOPIC_NAME)));
                    topicModelList.add(model);

                } while (c.moveToNext());
            }
        }
        return topicModelList;
    }

    public List<TopicModel> getTopicDetailList(int topicID) {
        List<TopicModel> topicModelList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + TBL_TOPICS_DETAIL + " where " + FLD_TOPIC_DETAIL_TOPIC_ID + " = " + topicID, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    TopicModel model = new TopicModel();
                    model.setId(c.getInt(c.getColumnIndex(FLD_TOPIC_DETAIL_ID)));
                    model.setTopicId(c.getInt(c.getColumnIndex(FLD_TOPIC_DETAIL_TOPIC_ID)));
                    model.setParaNo(c.getInt(c.getColumnIndex(FLD_TOPIC_DETAIL_PARA)));
                    model.setSurahNo(c.getInt(c.getColumnIndex(FLD_TOPIC_DETAIL_SURAH)));
                    model.setVersesNo(c.getInt(c.getColumnIndex(FLD_TOPIC_DETAIL_VERSE)));

                    topicModelList.add(model);

                } while (c.moveToNext());
            }
        }
        return topicModelList;
    }


    //Search form quran
    public List<TopicModel> getSearchList(String word) {
        List<TopicModel> topicModelList = new ArrayList<>();
        Cursor c = db.rawQuery("select * from tbl_search_quran where English_words like '%" + word + "%' and only_aayat_no != 0", null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    TopicModel model = new TopicModel();

                    model.setParaNo(c.getInt(c.getColumnIndex("para_ID")));
                    model.setSurahNo(c.getInt(c.getColumnIndex("surat_ID")));
                    model.setVersesNo(c.getInt(c.getColumnIndex("only_aayat_no")));

                    topicModelList.add(model);

                } while (c.moveToNext());
            }
        }
        return topicModelList;
    }

    //Tasbeeh list Qurery
    public List<TasbeehModel> getTasbeehList() {
        List<TasbeehModel> topicModelList = new ArrayList<>();
        Cursor c = db.rawQuery("select * from zikar", null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    TasbeehModel model = new TasbeehModel();

                    model.setId(c.getInt(c.getColumnIndex("id")));
                    model.setTotal(c.getInt(c.getColumnIndex("total")));
                    model.setCount(c.getInt(c.getColumnIndex("counter")));
                    model.setTotalCounterUpto(c.getInt(c.getColumnIndex("no_total_count")));
                    model.setTasbeehArabic(c.getString(c.getColumnIndex("arabic")));
                    model.setTasbeehEng(c.getString(c.getColumnIndex("transliteration")));
                    model.setTranslation(c.getString(c.getColumnIndex("english_translation")));
                    model.setReference(c.getString(c.getColumnIndex("reference")));
                    topicModelList.add(model);

                } while (c.moveToNext());
            }
        }
        return topicModelList;
    }

//Tasbeeh view pager add swipe item
    public List<TasbeehModel> getTasbeehList1st() {
        List<TasbeehModel> topicModelList = new ArrayList<>();
        Cursor c = db.rawQuery("select * from zikar", null);

       //Add dummy row here
        TasbeehModel model2 = new TasbeehModel();
        model2.setId(-1);
        topicModelList.add(model2);
        //*******************************


        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    TasbeehModel model = new TasbeehModel();

                    model.setId(c.getInt(c.getColumnIndex("id")));
                    model.setTotal(c.getInt(c.getColumnIndex("total")));
                    model.setCount(c.getInt(c.getColumnIndex("counter")));
                    model.setTotalCounterUpto(c.getInt(c.getColumnIndex("no_total_count")));
                    model.setTasbeehArabic(c.getString(c.getColumnIndex("arabic")));
                    model.setTasbeehEng(c.getString(c.getColumnIndex("transliteration")));
                    model.setTranslation(c.getString(c.getColumnIndex("english_translation")));
                    model.setReference(c.getString(c.getColumnIndex("reference")));
                    topicModelList.add(model);

                } while (c.moveToNext());
            }
        }
        return topicModelList;
    }



    public TasbeehModel getTasbeehUsingId(int id) {
        TasbeehModel topicModelList = null;
        Cursor c = db.rawQuery("select * from zikar where id =" + id, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    TasbeehModel model = new TasbeehModel();

                    model.setId(c.getInt(c.getColumnIndex("id")));
                    model.setTotal(c.getInt(c.getColumnIndex("total")));
                    model.setCount(c.getInt(c.getColumnIndex("counter")));
                    model.setTotalCounterUpto(c.getInt(c.getColumnIndex("no_total_count")));
                    model.setTasbeehArabic(c.getString(c.getColumnIndex("arabic")));
                    model.setTasbeehEng(c.getString(c.getColumnIndex("transliteration")));
                    model.setTranslation(c.getString(c.getColumnIndex("english_translation")));
                    model.setReference(c.getString(c.getColumnIndex("reference")));
                    //   topicModelList.add(model);
                    topicModelList = model;

                } while (c.moveToNext());
            }
        }
        return topicModelList;
    }


    public boolean updateTasbeehUsingId(TasbeehModel model) {


        ContentValues cv = new ContentValues();
        cv.put("total", model.getTotal());
        cv.put("counter", model.getCount());
        cv.put("no_total_count", model.getTotalCounterUpto());

        int va = db.update("zikar", cv, "id =" + model.getId(), null);
        if (va < -1)
            return false;
        else
            return true;
    }
}
