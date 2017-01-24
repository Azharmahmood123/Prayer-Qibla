package com.quranreading.sharedPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class QiblaDirectionPref {

	Editor editor;
	Context _context;
	int PRIVATE_MODE = 0;
	SharedPreferences pref;

	private static final String PREF_NAME = "QiblaPref";
	public static final String DBVERSION = "dbVersion";
	public static final String IS_PURCHASED = "is_purchased";
	public static final String AD_COUNT = "ad_count";
	public static final String RATE_US = "rate_us_popup";
	public static final String DATABASE_COPY = "database_copied";
	public static final String INTERSTITIAL_COUNT = "inter_count";

	public static final String ALARM_FIRST_TIME_KEY = "alarm_link";
	// public static final String ALARM_LINK_KEY = "alarm_link_lilmuslim2";

	public static final String PREMIUM_AD_COUNTER = "premium_ad";

	public QiblaDirectionPref(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setDbVersion(int v) {
		editor.putInt(DBVERSION, v);
		editor.commit();
	}

	public int chkDbVersion() {
		return pref.getInt(DBVERSION, 2);
	}

	public void setDatabaseCopied(boolean isDbCopied) {
		editor.putBoolean(DATABASE_COPY, isDbCopied);
		editor.commit();
	}

	public boolean isDatabaseCopied() {
		return pref.getBoolean(DATABASE_COPY, false);
	}

	public void setPurchased(boolean isPurchased) {
		editor.putBoolean(IS_PURCHASED, isPurchased);
		editor.commit();
	}

	public boolean getPurchased() {
		return pref.getBoolean(IS_PURCHASED, false);
		// return true;
	}

	public void setRateUs() {
		editor.putBoolean(RATE_US, true);
		editor.commit();
	}

	public boolean getRateUs() {
		return pref.getBoolean(RATE_US, false);
	}

	public void setExitCount(int count) {
		editor.putInt(AD_COUNT, count);
		editor.commit();
	}

	public int getExitCount() {
		return pref.getInt(AD_COUNT, 0);
	}

	public void setAlarmCount(int count) {
		editor.putInt(ALARM_FIRST_TIME_KEY, count);
		editor.commit();
	}

	public int getAlarmCount() {
		return pref.getInt(ALARM_FIRST_TIME_KEY, 0);
	}

	// To show Ads with premium features Activity on every 3rd time
	public void setPremiumAdCount(int count) {
		editor.putInt(PREMIUM_AD_COUNTER, count);
		editor.commit();
	}

	public int getPremiumAdCount() {
		return pref.getInt(PREMIUM_AD_COUNTER, 0);
	}

	public void setInterstitialCount(int count) {
		editor.putInt(INTERSTITIAL_COUNT, count);
		editor.commit();
	}

	public int getInterstitialCount() {
		return pref.getInt(INTERSTITIAL_COUNT, 0);
	}

	public void clearStoredData() {
		editor.clear();
		editor.commit();
	}
}