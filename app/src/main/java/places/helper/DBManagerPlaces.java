package places.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import places.models.PlacesModel;

public class DBManagerPlaces {

	public static final String DATABASE_NAME = "PlacesDB";
	// Table name
	public static final String TABLE_PLACES = "Places";

	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String ADDRESS = "address";
	public static final String LATITUDE = "lat";
	public static final String LONGITUDE = "lng";
	public static final String RATING = "rating";
	public static final String DISTANCE = "distance";
	public static final String TYPE = "type";

	public static final int DATABASE_VERSION = 1;

	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBManagerPlaces(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			String query = "create table " + TABLE_PLACES + "(" + ID + " integer primary key, " + NAME + " text," + ADDRESS + " text," + LATITUDE + " text, " + LONGITUDE + " text," + RATING + " text, " + DISTANCE + " text, " + TYPE + " text)";
			db.execSQL(query);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

	// ---opens the database---
	public DBManagerPlaces open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}

	public void saveData(PlacesModel data, String type) {

		ContentValues values = new ContentValues();

		values.put(NAME, data.getName());
		values.put(ADDRESS, data.getAddress());
		values.put(LATITUDE, data.getLat());
		values.put(LONGITUDE, data.getLng());
		values.put(DISTANCE, data.getDistance());
		values.put(RATING, data.getRating());
		values.put(TYPE, type);

		db.insert(TABLE_PLACES, null, values);
	}

	public void deleteData(String type) {
		db.delete(TABLE_PLACES, TYPE + "=?", new String[] { type });
	}

	public Cursor getData(String type) {

		String query = "Select * from " + TABLE_PLACES + " where " + TYPE + "='" + type + "'";
		return db.rawQuery(query, null);
	}
}