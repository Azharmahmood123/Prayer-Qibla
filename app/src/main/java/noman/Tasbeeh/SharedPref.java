package noman.Tasbeeh;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toolbar;


public class SharedPref {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";




    public static final String TASBEEH_COUNT_VALUE = "tasbeeh_count";
    public static final String TOTAL_READ_TASBEEH_COUNT = "total_read_tasbeeh";
    public static final String TASBEEH_COUNT_MODE = "count_mode";
    public static final String TASBEEH_SOUND_MODE = "sound_mode";
    public static final String TASBEEH_VIBRATION_MODE = "vibration_mode";


    public SharedPref(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }



    public int getSavedTasbeehCountValue() {
        return pref.getInt(TASBEEH_COUNT_VALUE, 0);
    }

    public void saveTasbeehCountValue(int tasbeehCount) {
        editor.putInt(TASBEEH_COUNT_VALUE, tasbeehCount);
        editor.commit();
    }

    public int getSavedTotalReadTasbeehCount() {
        return pref.getInt(TOTAL_READ_TASBEEH_COUNT, 0);
    }

    public void setSavedTotalTasbeehCount(int totalReadTasbeeh) {
        editor.putInt(TOTAL_READ_TASBEEH_COUNT, totalReadTasbeeh);
        editor.commit();
    }

    public int getCountMode() {
        return pref.getInt(TASBEEH_COUNT_MODE, 33);
    }

    public void setCountMode (int mode) {
        editor.putInt(TASBEEH_COUNT_MODE, mode);
        editor.commit();
    }

    public Boolean getSoundMode() {
        return pref.getBoolean(TASBEEH_SOUND_MODE, false);
    }

    public void setSoundMode(Boolean soundMode) {
        editor.putBoolean(TASBEEH_SOUND_MODE, soundMode);
        editor.commit();
    }

    public Boolean getVibrationMode() {
        return pref.getBoolean(TASBEEH_VIBRATION_MODE, false);
    }

    public void setVibrationMode(Boolean vibrationMode) {
        editor.putBoolean(TASBEEH_VIBRATION_MODE, vibrationMode);
        editor.commit();
    }


}
