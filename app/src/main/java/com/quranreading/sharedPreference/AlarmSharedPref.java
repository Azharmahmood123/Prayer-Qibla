package com.quranreading.sharedPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class AlarmSharedPref {
	Editor editor;
	Context _context;
	int PRIVATE_MODE = 0;
	SharedPreferences pref;
	private static final String PREF_NAME = "AlarmPref";

	public static final String CHK_FIRST_TIME = "chkFirstTime";
	public static final String CHK_TONE = "chkTone";
	public static final String CHK_SILENT = "chkSilent";
	public static final String CHK_DEFAULT = "chkDefault";

	public static final String ALARM_OPTION_INDEX = "alarm_index_premium";
	// public static final String ALARM_OPTION_INDEX = "alarm_index";

	private static final String CHK_FAJAR = "chkFajar";
	private static final String CHK_SUNRISE = "chkSunrise";
	private static final String CHK_ZUHAR = "chkZuhar1";
	private static final String CHK_ASAR = "chkAsar";
	private static final String CHK_MAGHRIB = "chkMaghrib";
	private static final String CHK_ISHA = "chkIsha1";

	public static final String[] CHK_PRAYERS = { "chkFajar", "chkSunrise", "chkZuhar1", "chkAsar", "chkMaghrib", "chkIsha1" };

	private static final String TIME_FAJAR = "timeFajar";
	private static final String TIME_SUNRISE = "timeSunrise";
	private static final String TIME_ZUHAR = "timeZuhar";
	private static final String TIME_ASAR = "timeAsar";
	private static final String TIME_MAGHRIB = "timeMaghrib";
	private static final String TIME_ISHA = "timeIsha";
	public static final String[] TIME_PRAYERS = { "timeFajar", "timeSunrise", "timeZuhar", "timeAsar", "timeMaghrib", "timeIsha" };

	public static final String[] ALARM_PRAYERS_SOUND = { "timeFajarSound", "timeSunriseSound", "timeZuharSound", "timeAsarSound", "timeMaghribSound", "timeIshaSound" };

	private static final String ALARM_FAJAR_SOUND = "timeFajarSound";
	private static final String ALARM_SUNRISE_SOUND = "timeSunriseSound";
	private static final String ALARM_ZUHAR_SOUND = "timeZuharSound";
	private static final String ALARM_ASAR_SOUND = "timeAsarSound";
	private static final String ALARM_MAGHRIB_SOUND = "timeMaghribSound";
	private static final String ALARM_ISHA_SOUND = "timeIshaSound";


	public static final String USER_LOC = "userLoc";

	public AlarmSharedPref(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setSilentMode(Boolean chk) {
		editor.putBoolean(CHK_SILENT, chk);
		editor.commit();
	}

	public void setDefaultToneMode(Boolean chk) {
		editor.putBoolean(CHK_DEFAULT, chk);
		editor.commit();
	}

	public void setTone(int tone) {
		editor.putInt(CHK_TONE, tone);
		editor.commit();
	}

	public void setFirstTime() {
		editor.putBoolean(CHK_FIRST_TIME, false);
		editor.commit();
	}

	public Boolean getFirstTime() {
		return pref.getBoolean(CHK_FIRST_TIME, true);
	}

	public Boolean getSilentMode() {
		return pref.getBoolean(CHK_SILENT, false);
	}

	public Boolean getDefaultToneMode() {
		return pref.getBoolean(CHK_DEFAULT, false);
	}

	public int getTone() {
		return pref.getInt(CHK_TONE, 1);
	}

	public String getSavedAlarm(String varTime) {
		return pref.getString(varTime, "");
	}

//	public int getAlarmOptionIndex() {
//		return pref.getInt(ALARM_OPTION_INDEX,0);
//	}
//
//	public void setAlarmOptionIndex(int indexSoundOption) {
//		editor.putInt(ALARM_OPTION_INDEX, indexSoundOption);
//		editor.commit();
//	}

	public void saveAlarm(String varChk) {
		editor.putBoolean(varChk, true);
		editor.commit();
	}

	public void removeAlarm(String varChk) {
		editor.putBoolean(varChk, false);
		editor.commit();
	}

	public void savePrayerTime(String keyTime, String prayerTime) {
		editor.putString(keyTime, prayerTime);
		editor.commit();
	}

	public HashMap<String, Boolean> checkAlarms() {
		HashMap<String, Boolean> alarm = new HashMap<String, Boolean>();
		alarm.put(CHK_FAJAR, pref.getBoolean(CHK_FAJAR, false));
		alarm.put(CHK_SUNRISE, pref.getBoolean(CHK_SUNRISE, false));
		alarm.put(CHK_ZUHAR, pref.getBoolean(CHK_ZUHAR, false));
		alarm.put(CHK_ASAR, pref.getBoolean(CHK_ASAR, false));
		alarm.put(CHK_MAGHRIB, pref.getBoolean(CHK_MAGHRIB, false));
		alarm.put(CHK_ISHA, pref.getBoolean(CHK_ISHA, false));
		return alarm;
	}

	public void setAlarmsState(String varChk, Boolean chk) {
		editor.putBoolean(varChk, chk);
		editor.commit();
	}

	public HashMap<String, String> getPrayerTimes() {
		HashMap<String, String> alarmTime = new HashMap<String, String>();
		alarmTime.put(TIME_FAJAR, pref.getString(TIME_FAJAR, ""));
		alarmTime.put(TIME_SUNRISE, pref.getString(TIME_SUNRISE, ""));
		alarmTime.put(TIME_ZUHAR, pref.getString(TIME_ZUHAR, ""));
		alarmTime.put(TIME_ASAR, pref.getString(TIME_ASAR, ""));
		alarmTime.put(TIME_MAGHRIB, pref.getString(TIME_MAGHRIB, ""));
		alarmTime.put(TIME_ISHA, pref.getString(TIME_ISHA, ""));
		return alarmTime;
	}


	public String getPrayerTime(String key) {
		return pref.getString(key, "");
	}

	public int getAlarmOptionIndex(String key) {
		return pref.getInt(key,0);
	}

	public void setAlarmOptionIndex(String key, int indexSoundOption) {
		editor.putInt(key, indexSoundOption);
		editor.commit();
	}

	public void clearStoredData() {
		editor.clear();
		editor.commit();
	}
}