package names.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DownloadingNamesPref {

    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    SharedPreferences pref;
    private static final String PREF_NAME = "DuaDownloadPref";

    public static String REFERENCE_ID = "reference_id";
    public static String FIRST_TIME = "first_time_names";

    public DownloadingNamesPref(Context context) {
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
    }


    public void setFirstTimeNames() {
        editor.putBoolean(FIRST_TIME, false);
        editor.commit();
    }

    public boolean isFirstTimeNames() {
        return pref.getBoolean(FIRST_TIME, true);
    }

    public void clearStoredData() {
        editor.clear();
        editor.commit();
    }
}
