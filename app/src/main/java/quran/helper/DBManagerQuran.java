package quran.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManagerQuran {
	private final Context context;

	///////////////////////////// Column Names //////////////////////////////

	public static final String FLD_ID = "_id";
	public static final String FLD_SURAH_NAME = "surah_name";
	public static final String FLD_SURAH_NO = "surah_no";
	public static final String FLD_AYAH_NO = "ayah_no";
	public static final String FLD_TRANSLATION = "translation";
	public static final String FLD_DOWNLOAD_ID = "download_id";
	public static final String FLD_TEMP_NAME = "temp_name";
	public static final String DATABASE_NAME = "quran_now_db";

	/////////////////////////// Database And Table Name ///////////////////////////

	public static final String TBL_BOOKMARKS = "tbl_bookmarks";
	public static final String TBL_DOWNLOADS = "tbl_downloads";
	public static final String TBL_ENG_TRANSLATION = "engTranslationSaheeh";

	///////////////////////////// Table Create Queries ////////////////////////////

	private static final int DATABASE_VERSION = 1;

	/*
	 * private static final String CREATE_TBLLOCATION =
	 * 
	 * "create table " + TBL_LOCATION + "(" + FLD_ID + " integer not null, " + FLD_NAME + " text not null, " + FLD_ALARM + " integer not null);";
	 */

	///////////////////////// HELPER CLASS TO CREATE DATABASE //////////////////////

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBManagerQuran(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String tbl_bookmarks = "CREATE TABLE tbl_bookmarks (_id INTEGER PRIMARY KEY, surah_name text not null, surah_no integer not null, ayah_no integer not null)";
			db.execSQL(tbl_bookmarks);

			String tbl_downloads = "CREATE TABLE tbl_downloads(_id INTEGER PRIMARY KEY  NOT NULL , download_id INTEGER, surah_no INTEGER, surah_name TEXT, temp_name TEXT)";
			db.execSQL(tbl_downloads);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

	///////////////////////////// Opens Database ////////////////////////////

	public DBManagerQuran open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		DBHelper.close();
	}

	///////////////////////// Insert Into Bookmark Table //////////////////////

	public long addBookmark(String surahName, int surahNo, int ayahNo) {
		ContentValues newValues = new ContentValues();
		newValues.put(FLD_SURAH_NAME, surahName);
		newValues.put(FLD_SURAH_NO, surahNo);
		newValues.put(FLD_AYAH_NO, ayahNo);

		return db.insert(TBL_BOOKMARKS, null, newValues);
	}

	////////////////////// Delete From Bookmark Table //////////////////////////

	public boolean deleteOneBookmark(long id) {
		return db.delete(TBL_BOOKMARKS, FLD_ID + "=" + id, null) > 0;
	}

	public boolean deleteAllBookmarks() {
		return db.delete(TBL_BOOKMARKS, null, null) > 0;
	}

	////////////////////// Get Records From Bookmark Table //////////////////////

	public Cursor getAllBookmarks() {
		return db.query(TBL_BOOKMARKS, new String[] { FLD_ID, FLD_SURAH_NAME, FLD_SURAH_NO, FLD_AYAH_NO }, null, null, null, null, FLD_SURAH_NO + "," + FLD_AYAH_NO);
	}

	public Cursor getOneBookmark(long surahNo) throws SQLException {
		return db.query(TBL_BOOKMARKS, new String[] { FLD_ID, FLD_SURAH_NO, FLD_AYAH_NO }, FLD_SURAH_NO + "=" + surahNo, null, null, null, FLD_AYAH_NO, null);

	}

	///////////////////// Update Into Bookmark Table /////////////////////////

	public boolean updateBookmark(long rowId, String surahName, int surahNo, int ayahNo) {
		ContentValues newValues = new ContentValues();
		newValues.put(FLD_SURAH_NAME, surahName);
		newValues.put(FLD_SURAH_NO, surahNo);
		newValues.put(FLD_AYAH_NO, ayahNo);

		return db.update(TBL_BOOKMARKS, newValues, FLD_ID + "=" + rowId, null) > 0;
	}

	///////////////////////// Get Translation for word search from whole Quran ///////

	public Cursor getFullQuranTranslation() {
		// return db.query(TBL_ENG_TRANSLATION, new String[] {FLD_ID, FLD_TRANSLATION, FLD_SURAH_NO, FLD_AYAH_NO}, null, null, null, null, null);
		return db.query(TBL_ENG_TRANSLATION, null, null, null, null, null, FLD_SURAH_NO + "," + FLD_AYAH_NO);
	}

	///////////////////////// Insert Into Download Table //////////////////////

	public long addDownload(int d_Id, int s_No, String s_name, String t_name) {
		ContentValues newValues = new ContentValues();
		newValues.put(FLD_DOWNLOAD_ID, d_Id);
		newValues.put(FLD_SURAH_NO, s_No);
		newValues.put(FLD_SURAH_NAME, s_name);
		newValues.put(FLD_TEMP_NAME, t_name);

		return db.insert(TBL_DOWNLOADS, null, newValues);
	}

	////////////////////// Delete From Download Table //////////////////////////

	public boolean deleteOneDownload(String column, long id) {
		return db.delete(TBL_DOWNLOADS, column + "=" + id, null) > 0;
	}

	public boolean deleteAllDownloads() {
		return db.delete(TBL_DOWNLOADS, null, null) > 0;
	}

	////////////////////// Get Records From Download Table //////////////////////

	public Cursor getAllDownloads() {
		return db.query(TBL_DOWNLOADS, new String[] { FLD_ID, FLD_DOWNLOAD_ID, FLD_SURAH_NO, FLD_SURAH_NAME, FLD_TEMP_NAME }, null, null, null, null, null);
	}

	public Cursor getOneDownload(long d_id) throws SQLException {
		return db.query(TBL_DOWNLOADS, new String[] { FLD_ID, FLD_DOWNLOAD_ID, FLD_SURAH_NO, FLD_SURAH_NAME, FLD_TEMP_NAME }, FLD_DOWNLOAD_ID + "=" + d_id, null, null, null, null, null);

	}

	///////////////////// Update Into Download Table /////////////////////////

	public boolean updateDownload(long rowId, int d_Id, int s_No, String s_name, String t_name) {
		ContentValues newValues = new ContentValues();
		newValues.put(FLD_DOWNLOAD_ID, d_Id);
		newValues.put(FLD_SURAH_NO, s_No);
		newValues.put(FLD_SURAH_NAME, s_name);
		newValues.put(FLD_TEMP_NAME, t_name);

		return db.update(TBL_DOWNLOADS, newValues, FLD_ID + "=" + rowId, null) > 0;
	}
}
