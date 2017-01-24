package com.quranreading.sharedPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class PrayerTimeSettingsPref {
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    SharedPreferences pref;
    private static final String PREF_NAME = "NamazTimeettingsPref";
    public static final String JURISTIC = "Juristic";
    public static final String JURISTIC_DEFAULT = "Juristic_default";
    public static final String CALCULATION_METHOD = "CalculationMethod";
    public static final String CALCULATION_METHOD_INDEX = "CalculationMethodIndex";
    public static final String LATITUDE_ADJUSTMENT = "LatitudeAdjustment";
    public static final String DAYLIGHT_SAVING = "DaylightSaving";
    public static final String AUTO_SETTINGS = "auto_settings";
    public static final String AUTO_EDIT_TIME = "auto_edit_time";


    public static final String CORRECTIONS_FAJR = "corrections_fajr";
    public static final String CORRECTIONS_SUNRIZE = "corrections_sunrize";
    public static final String CORRECTIONS_ZUHAR = "corrections_zuhr";
    public static final String CORRECTIONS_ASAR = "corrections_asar";
    public static final String CORRECTIONS_MAGHRIB = "corrections_maghrib";
    public static final String CORRECTIONS_ISHA = "corrections_isha";


    public PrayerTimeSettingsPref(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveSettings(int juristic, int method, int latdAdjst) {
        editor.putInt(JURISTIC, juristic);
        editor.putInt(CALCULATION_METHOD, method);
        editor.putInt(LATITUDE_ADJUSTMENT, latdAdjst);

        editor.commit();
    }

    public void setJuristicDefault(int juristicDefault) {
        editor.putInt(JURISTIC_DEFAULT, juristicDefault);

        editor.commit();
    }


    public int getJuristicDefault() {
        return pref.getInt(JURISTIC_DEFAULT, 2);
    }

    public void setJuristic(int juristic) {
        editor.putInt(JURISTIC, juristic);

        editor.commit();
    }

    public int getJuristic() {
        return pref.getInt(JURISTIC, 2);
    }

    public int getCalculationMethod() {
        return pref.getInt(CALCULATION_METHOD, 3);
    }

    public void setCalculationMethod(int method) {
        editor.putInt(CALCULATION_METHOD, method);
        editor.commit();
    }

    public int getCalculationMethodIndex() {
        return pref.getInt(CALCULATION_METHOD_INDEX, 4);
    }

    public void setCalculationMethodIndex(int index) {
        editor.putInt(CALCULATION_METHOD_INDEX, index);
        editor.commit();
    }

    public void setsLatdAdjst(int latdAdjst) {
        editor.putInt(LATITUDE_ADJUSTMENT, latdAdjst);
        editor.commit();
    }

    public HashMap<String, Integer> getSettings() {
        HashMap<String, Integer> settingsData = new HashMap<String, Integer>();
        settingsData.put(JURISTIC, pref.getInt(JURISTIC, 2));
        settingsData.put(CALCULATION_METHOD, pref.getInt(CALCULATION_METHOD, 3));
        settingsData.put(LATITUDE_ADJUSTMENT, pref.getInt(LATITUDE_ADJUSTMENT, 2));
        return settingsData;
    }

    public void setDaylightSaving(boolean isDaylightSaving) {
        editor.putBoolean(DAYLIGHT_SAVING, isDaylightSaving);
        editor.commit();
    }

    public boolean isDaylightSaving() {
        return pref.getBoolean(DAYLIGHT_SAVING, false);
    }


    public void setAutoSettings(boolean isAutoSettings) {
        editor.putBoolean(AUTO_SETTINGS, isAutoSettings);
        editor.commit();
    }

    public boolean isAutoSettings() {
        return pref.getBoolean(AUTO_SETTINGS, true);
    }

    public int getAutoEditTime() {
        return pref.getInt(AUTO_EDIT_TIME, 0);
    }

    public void setAutoEditTime(int autoEditTime) {
        editor.putInt(AUTO_EDIT_TIME, autoEditTime);
        editor.commit();
    }

    //////////////////
    //////////////////////
    //////////////////////////

    //
    public void setCorrectionsFajr(int corrections) {
        editor.putInt(CORRECTIONS_FAJR, corrections);
        editor.commit();
    }

    //
    public int getCorrectionsFajr() {
        return pref.getInt(CORRECTIONS_FAJR, 0);
    }


    //
    public void setCorrectionsSunrize(int corrections) {
        editor.putInt(CORRECTIONS_SUNRIZE, corrections);
        editor.commit();
    }

    //
    public int getCorrectionsSunrize() {
        return pref.getInt(CORRECTIONS_SUNRIZE, 0);
    }


    //
    public void setCorrectionsZuhar(int corrections) {
        editor.putInt(CORRECTIONS_ZUHAR, corrections);
        editor.commit();
    }

    //
    public int getCorrectionsZuhar() {
        return pref.getInt(CORRECTIONS_ZUHAR, 0);
    }


    //
    public void setCorrectionsAsar(int corrections) {
        editor.putInt(CORRECTIONS_ASAR, corrections);
        editor.commit();
    }

    //
    public int getCorrectionsAsar() {
        return pref.getInt(CORRECTIONS_ASAR, 0);
    }


    //
    public void setCorrectionsMaghrib(int corrections) {
        editor.putInt(CORRECTIONS_MAGHRIB, corrections);
        editor.commit();
    }

    //
    public int getCorrectionsMaghrib() {
        return pref.getInt(CORRECTIONS_MAGHRIB, 0);
    }


    //
    public void setCorrectionsIsha(int corrections) {
        editor.putInt(CORRECTIONS_ISHA, corrections);
        editor.commit();
    }

    //
    public int getCorrectionsIsha() {
        return pref.getInt(CORRECTIONS_ISHA, 0);
    }


///////////////////
    //////////////////
    //////////////////
    //////////////////


    ////////////////////////
    //////////////////////////////
    ///////////////////////////

    public void clearStoredData() {
        editor.clear();
        editor.commit();
    }
}