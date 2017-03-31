package noman.qurantrack.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.quranreading.alarms.AlarmReceiverAyah;

import noman.community.model.SignInRequest;
import noman.quran.model.JuzConstant;
import noman.qurantrack.model.TargetModel;

import static com.facebook.FacebookSdk.getApplicationContext;

public class QuranTrackerPref {

    private Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;
    private SharedPreferences pref;
    private static final String PREF_NAME = "QuranTrackerPref";

    private static final String LAST_TARGET_DATE = "target_date";
    private static final String LAST_DATE = "DATE";

    public QuranTrackerPref(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public TargetModel getDataFromSharedPreferences() {
        String PREFS_TAG = LAST_TARGET_DATE;
        String PRODUCT_TAG = LAST_DATE;
        Gson gson = new Gson();
        TargetModel userFromShared = null;
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(PRODUCT_TAG, "");
        userFromShared = gson.fromJson(jsonPreferences, TargetModel.class);
        return userFromShared;
    }

    public void setDataFromSharedPreferences(TargetModel mSignInRequest) {
        Gson gson = new Gson();
        String jsonCurProduct = gson.toJson(mSignInRequest);
        String PREFS_TAG = LAST_TARGET_DATE;
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String PRODUCT_TAG =LAST_DATE;
        editor.putString(PRODUCT_TAG, jsonCurProduct);
        editor.commit();
    }
}