package duas.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DuasSharedPref {

	Editor editor;
	Context _context;
	int PRIVATE_MODE = 0;
	SharedPreferences pref;
	private static final String PREF_NAME = "DuasSharedPref";
	public static String REFERENCE_ID = "reference_id";

	private static final String TRANSLATION = "translation_duas";
	private static final String TRANSLITERATION = "transliteration_duas";

	public DuasSharedPref(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setReferenceId(String refId) {
		editor.putString(REFERENCE_ID, refId);
		editor.commit();
	}

	public String getReferenceId() {
		return pref.getString(REFERENCE_ID, "");
		// return true;
	}

	public boolean isTransliteration() {
		return pref.getBoolean(TRANSLITERATION, true);
	}

	public void setTransliteration(boolean isTransliteration) {
		editor.putBoolean(TRANSLITERATION, isTransliteration);
		editor.commit();
	}

	public void setTranslation(boolean translation) {
		editor.putBoolean(TRANSLATION, translation);
		editor.commit();
	}

	public boolean isTranslation() {
		return pref.getBoolean(TRANSLATION, true);
	}

	public void clearStoredData() {
		editor.clear();
		editor.commit();
	}
}
