package com.quranreading.sharedPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LanguagePref {

	Editor editor;
	Context _context;
	int PRIVATE_MODE = 0;
	SharedPreferences pref;
	private static final String PREF_NAME = "LanguagePrefs";

	// public static String LANGUAGE_CODE = "lang_code";
	public static String LANGUAGE_CODE = "lang_code_premium";

	public static String LANGUAGE_FIRST_TIME = "lang_first_time";

	public LanguagePref(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setFirstTimeLanguage(boolean isPurchased) {
		editor.putBoolean(LANGUAGE_FIRST_TIME, isPurchased);
		editor.commit();
	}

	public boolean getFirstTimeLanguage() {
		return pref.getBoolean(LANGUAGE_FIRST_TIME, false);
		// return true;
	}

	public void setLanguage(int index) {
		editor.putInt(LANGUAGE_CODE, index);
		editor.commit();
	}

	public int getLanguage() {
		return pref.getInt(LANGUAGE_CODE, 0);
	}

	public void clearStoredData() {
		editor.clear();
		editor.commit();
	}
}
