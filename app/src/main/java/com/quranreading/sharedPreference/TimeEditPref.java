package com.quranreading.sharedPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TimeEditPref {

	Editor editor;
	Context _context;
	int PRIVATE_MODE = 0;
	SharedPreferences pref;
	private static final String PREF_NAME = "EditTimePref";

	// Keys to store state of EDIT_TIME
	// private static final String EDIT_TIME_FAJR = "fajr_edit_time";
	// private static final String EDIT_TIME_SUNRISE = "sunrize_edit_time";
	// private static final String EDIT_TIME_DUHR = "duhr_edit_time";
	// private static final String EDIT_TIME_ASR = "asr_edit_time";
	// private static final String EDIT_TIME_MAGHRIB = "maghrib_edit_time";
	// private static final String EDIT_TIME_ISHA = "isha_edit_time";

	public static final String[] ALARMS_TIME_PRAYERS = { "fajr_edit_time", "sunrize_edit_time", "duhr_edit_time", "asr_edit_time", "maghrib_edit_time", "isha_edit_time" };

	public static final String[] ALARMS_TIME_PRAYERS_DIFFERENCE = { "fajr_edit_time_diff", "sunrize_edit_time_diff", "duhr_edit_time_diff", "asr_edit_time_diff", "maghrib_edit_time_diff", "isha_edit_time_diff" };

	// public static final String[] IS_EDITED_TIME = { "fajr_edited", "sunrize_edited", "duhr_edited", "asr_edited", "maghrib_edited", "isha_edited" };

	// Keys to store the Option to Stop the EDIT_TIME
	// public static final String IS_EDIT_TIME_FAJR = "IS_fajr_edit_time";
	// public static final String IS_EDIT_TIME_SUNRISE = "IS_ishraq_edit_time";
	// public static final String IS_EDIT_TIME_DUHR = "IS_duhr_edit_time";
	// public static final String IS_EDIT_TIME_ASR = "IS_asr_edit_time";
	// public static final String IS_EDIT_TIME_MAGHRIB = "IS_maghrib_edit_time";
	// public static final String IS_EDIT_TIME_ISHA = "IS_isha_edit_time";

	public TimeEditPref(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	// For storing EDIT_TIME for Prayers
	public void setAlarmNotifyTime(String key, String option) {
		editor.putString(key, option);
		editor.commit();
	}

	// For get EDIT_TIME for prayers
	public String getAlarmNotifyTime(String key) {
		return pref.getString(key, "");
	}

	// For storing EDIT_TIME for Prayers
	public void setPrayerAlarmDifference(String key, int minutes) {
		editor.putInt(key, minutes);
		editor.commit();
	}

	// For get EDIT_TIME for prayers
	public int getPrayerAlarmDifference(String key) {
		return pref.getInt(key, 0);
	}




	// //////////////////////////////////

	// For checking EDIT_TIME for Prayers
	// public void setAlarmTimeEdited(String key, boolean option) {
	// editor.putBoolean(key, option);
	// editor.commit();
	// }
	//
	// public boolean isAlarmTimeEdited(String key) {
	// return pref.getBoolean(key, false);
	// }

	// ///////////////////////////////

	// to Clear all data from pref file
	public void clearStoredData() {
		editor.clear();
		editor.commit();
	}
}