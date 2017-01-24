package duas.db;

import com.quranreading.helper.DBManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManagerDua {
	// Table name
	private static final String DUA_TABLE = "Dua";

	// Table Columns names
	public static final String ID = "id";
	public static final String DUA_ID = "dua_id";
	public static final String DUA_TITLE = "dua_title";
	public static final String DUA_ENG_TRANSLATION = "english_translation";
	public static final String DUA_URDU_TRANSLATION = "urdu_translation";
	public static final String DUA_TRANSLITERATION = "dua_transliteration";
	public static final String DUA_AUDIO_NAME = "audio_name";
	public static final String DUA_ARABIC = "dua_arabic";
	public static final String FAVOURITE = "favourite";
	public static final String CATEGORY = "category";
	public static final String ADDITIONAL_INFO = "additional_info";
	public static final String REFERENCE = "reference";
	public static final String DUA_COUNTER = "counter";

	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBManagerDua(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DBManager.DATABASE_NAME, null, DBManager.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

	// ---opens the database---
	public DBManagerDua open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}

	// DISPLAY ALL RECORDS FUNCTIONS
	public Cursor GetDuasByCategory(String categoryName) {
		return db.query(DUA_TABLE, null, CATEGORY + "='" + categoryName + "'", null, null, null, null, null);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// DISPLAY ALL RECORDS FUNCTIONS
	public Cursor GetAllDuaRecords(String categoryName) {
		return db.query(DUA_TABLE, new String[] { DUA_TITLE }, CATEGORY + "='" + categoryName + "'", null, null, null, null, null);
	}

	// With Translation
	public Cursor GetTransByDuaTitle(String duaTitle) throws SQLException {
		return db.query(true, DUA_TABLE, new String[] { DUA_TRANSLITERATION, DUA_ENG_TRANSLATION, DUA_URDU_TRANSLATION, DUA_AUDIO_NAME, DUA_ARABIC, DUA_ID, FAVOURITE, CATEGORY }, DUA_TITLE + "='" + duaTitle + "'", null, null, null, null, null);
	}

	// With Translation
	public Cursor GetCategoryByTitle(String duaTitle) throws SQLException {
		return db.query(true, DUA_TABLE, new String[] { CATEGORY }, DUA_TITLE + "='" + duaTitle + "'", null, null, null, null, null);
	}

	public Cursor getSearchedItems(String word, String word1) throws SQLException {
		return db.query(DUA_TABLE, new String[] { DUA_TITLE, CATEGORY }, DUA_TITLE + " LIKE '%" + word + "%' or " + ADDITIONAL_INFO + " LIKE '%" + word + "%' or " + DUA_ENG_TRANSLATION + " LIKE '%" + word + "%' or " + DUA_TRANSLITERATION + " LIKE '%" + word + "%' or " +

		DUA_TITLE + " LIKE '%" + word1 + "%' or " + ADDITIONAL_INFO + " LIKE '%" + word1 + "%' or " + DUA_ENG_TRANSLATION + " LIKE '%" + word1 + "%' or " + DUA_TRANSLITERATION + " LIKE '%" + word1 + "%' ", null, null, null, null);

	}

	/*
	 * SELECT dua_title FROM Dua WHERE (dua_title LIKE '% prayer%') or (additional_info LIKE '% prayer%') or (english_translation LIKE '% prayer%')
	 * 
	 * 
	 * mDb.query(true, DATABASE_NAMES_TABLE, new String[] { KEY_ROWID, KEY_NAME }, KEY_NAME + " LIKE ?", new String[] { filter }, null, null, null, null);
	 * 
	 * Cursor cursor=mDb.query(true, DATABASE_NAMES_TABLE, null,"row LIKE '%"+filter+"%' or name LIKE '%"+filter+"%'",null, null, null, null);
	 * 
	 */

	public Cursor GetDuaById(int id) throws SQLException {
		return db.query(true, DUA_TABLE, new String[] { DUA_TITLE, CATEGORY }, ID + "='" + id + "'", null, null, null, null, null);
	}

	/*
	 * // With Translation public Cursor GetTransByDuaTitle(String duaTitle, String language_column) throws SQLException { return db.query(true, DUA_TABLE, new String[] { DUA_TRANSLITERATION, language_column, IMAGE_NAME, DUA_ARABIC, DUA_ID, FAVOURITE }, DUA_TITLE + "='" + duaTitle + "'", null, null,
	 * null, null, null); }
	 */

	// DISPLAY ONE RECORD FROM DUA TABLE
	public Cursor GetTransByDuaId(int duaId) throws SQLException {
		return db.query(true, DUA_TABLE, new String[] { DUA_TITLE, DUA_TRANSLITERATION, DUA_ENG_TRANSLATION, DUA_URDU_TRANSLATION, DUA_AUDIO_NAME, DUA_ARABIC, FAVOURITE }, DUA_ID + "=" + duaId, null, null, null, null, null);
	}
	// /////////////////////////////////////////

	// DISPLAY ONE RECORD FROM DUA TABLE
	public Cursor GetFavouriteDetail(String duaTitle) throws SQLException {
		return db.query(true, DUA_TABLE, new String[] { FAVOURITE }, DUA_TITLE + "='" + duaTitle + "'", null, null, null, null, null);
	}
	// /////////////////////////////////////////

	// DISPLAY ONE RECORD FROM DUA TABLE
	public Cursor GetCounter(String duaTitle) throws SQLException {
		return db.query(true, DUA_TABLE, new String[] { DUA_COUNTER }, DUA_TITLE + "='" + duaTitle + "'", null, null, null, null, null);
	}
	// /////////////////////////////////////////

	// DISPLAY ONE RECORD FROM DUA TABLE
	public Cursor GetAdditionalInfo(String duaTitle) throws SQLException {
		return db.query(true, DUA_TABLE, new String[] { ADDITIONAL_INFO, REFERENCE }, DUA_TITLE + "='" + duaTitle + "'", null, null, null, null, null);
	}
	// /////////////////////////////////////////

	// UPDATE FUNCTIONS
	public boolean UpdateFavourite(String Fav, String duaTitle) {
		ContentValues newValues = new ContentValues();
		newValues.put(FAVOURITE, Fav);
		return db.update(DUA_TABLE, newValues, DUA_TITLE + "='" + duaTitle + "'", null) > 0;
	}

	public boolean UpdateCounter(int counter, String duaTitle) {
		ContentValues newValues = new ContentValues();
		newValues.put(DUA_COUNTER, counter);
		return db.update(DUA_TABLE, newValues, DUA_TITLE + "='" + duaTitle + "'", null) > 0;
	}

	// ///////////////////// :: Favourite Dua Functions ::

	// DISPLAY ALL RECORDS FUNCTIONS
	public Cursor GetAllFavDuaRecords() {
		return db.query(DUA_TABLE, new String[] { DUA_TITLE }, FAVOURITE + "='yes'", null, null, null, null, null);
	}
}
