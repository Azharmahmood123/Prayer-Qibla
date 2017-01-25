package com.quranreading.sharedPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class LocationPref {
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    SharedPreferences pref;
    private static final String PREF_NAME = "LocationPref";
    // public static final String MANUAL_LOCATION = "IsValueSet";
    public static final String CITY_NAME = "CityName";
    public static final String COUNTRY_CODE = "country_code";
    // public static final String COUNTRY_NAME = "Country_Name";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";


    public static final String LATITUDE_CURRENT = "Latitude_current";
    public static final String LONGITUDE_CURRENT = "Longitude_current";

    // public static final String TIMEZONE = "TimeZoneValue";
    public static final String FIRST_TIME_LAUNCH = "FirstTimeLaunch";
    // public static final String FIRST_TIME_SALAT_TIME = "FirstSalatTime";
    public static final String FIRST_TIME_SALAT_TIME = "FirstSalatTimeJuristic";
    public static final String SALAT_MSG_COUNTER = "salat_msg_counter";

    public static final String DISTANCE = "distance";
    public static final String ANGLE = "angle";

    public static final String CALIBRATION = "calibrations";

    public static final String HALAL_PLACES_OFFLINE = "places_offline";
    public static final String MOSQUE_OFFLINE = "places_offline";
    public static final String CURRENT_LOCATION = "current_location";

    public LocationPref(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLocation(String city, /* String country, */ String latd, String longit/* , String timezone */) {
        // editor.putBoolean(MANUAL_LOCATION, true);
        editor.putString(CITY_NAME, city);
        // editor.putString(COUNTRY_NAME, country);
        editor.putString(LATITUDE, latd);
        editor.putString(LONGITUDE, longit);
        // editor.putString(TIMEZONE, timezone);

        editor.commit();
    }

    // public void setManualLocationOff() {
    // editor.putBoolean(MANUAL_LOCATION, false);
    // editor.commit();
    // }
    //
    // public boolean isManualLocaionSet() {
    // return pref.getBoolean(MANUAL_LOCATION, false);
    // }

    public HashMap<String, String> getLocation() {
        HashMap<String, String> location = new HashMap<String, String>();
        location.put(CITY_NAME, pref.getString(CITY_NAME, ""));
        // location.put(COUNTRY_NAME, pref.getString(COUNTRY_NAME, ""));
        location.put(LATITUDE, pref.getString(LATITUDE, "0"));
        location.put(LONGITUDE, pref.getString(LONGITUDE, "0"));
        // location.put(TIMEZONE, pref.getString(TIMEZONE, ""));
        return location;
    }

    public String getCityName() {
        return pref.getString(CITY_NAME, "");
    }

    public void setCountryCode(String code) {
        editor.putString(COUNTRY_CODE, code);
        editor.commit();
    }

    public String getCountryCode() {
        return pref.getString(COUNTRY_CODE, "");
    }

    public String getCurrentLocation() {
        return pref.getString(CURRENT_LOCATION, "");
    }

    public void setCurrentLocation(String name) {
        editor.putString(CURRENT_LOCATION, name);
        editor.commit();
    }

    ////////////
    //////////////
    ///////////////

    public String getLatitudeCurrent() {
        return pref.getString(LATITUDE_CURRENT, "0");
    }

    public String getLongitudeCurrent() {
        return pref.getString(LONGITUDE_CURRENT, "0");
    }

    public void setLatitudeCurrent(String lat) {
        editor.putString(LATITUDE_CURRENT, lat);
        editor.commit();
    }

    public void setLongitudeCurrent(String lng) {
        editor.putString(LONGITUDE_CURRENT, lng);
        editor.commit();
    }

    //////////////
    //////////////
    ////////////////

    public String getLatitude() {
        return pref.getString(LATITUDE, "0");
    }

    public String getLongitude() {
        return pref.getString(LONGITUDE, "0");
    }

    public void setLatitude(String lat) {
        editor.putString(LATITUDE, lat);
        editor.commit();
    }

    public void setLongitude(String lng) {
        editor.putString(LONGITUDE, lng);
        editor.commit();
    }

	/*
     * public String getTimeZone() { return pref.getString(TIMEZONE, ""); }
	 */

    public void setFirstLaunch() {
        editor.putBoolean(FIRST_TIME_LAUNCH, false);
        editor.commit();
    }

    public boolean isFirstLaunch() {
        return pref.getBoolean(FIRST_TIME_LAUNCH, true);
    }

    public void setFirstSalatLauch() {
        editor.putBoolean(FIRST_TIME_SALAT_TIME, false);
        editor.commit();
    }

    public boolean isFirstSalatLaunch() {
        return pref.getBoolean(FIRST_TIME_SALAT_TIME, true);
    }

    public int getSalatMsgCounter() {
        return pref.getInt(SALAT_MSG_COUNTER, 0);
    }

    public void setSalatMsgCounter(int msgCounter) {
        editor.putInt(SALAT_MSG_COUNTER, msgCounter);
        editor.commit();
    }

    public void setDistance(String distance) {
        editor.putString(DISTANCE, distance);
        editor.commit();
    }

    public String getDistance() {
        return pref.getString(DISTANCE, "");
    }

    public void setAngle(String angle) {
        editor.putString(ANGLE, angle);
        editor.commit();
    }

    public String getAngle() {
        return pref.getString(ANGLE, "");
    }

    public void setCalibrationShown() {
        editor.putBoolean(CALIBRATION, true);
        editor.commit();
    }

    public boolean isCalibrationShown() {
        return pref.getBoolean(CALIBRATION, false);
    }

    public void setHalalPlacesSaved(boolean isSaved) {
        editor.putBoolean(HALAL_PLACES_OFFLINE, isSaved);
        editor.commit();
    }

    public boolean isHalalPlacesSaved() {
        return pref.getBoolean(HALAL_PLACES_OFFLINE, false);
    }

    public void setMosquePlacesSaved(boolean isSaved) {
        editor.putBoolean(MOSQUE_OFFLINE, isSaved);
        editor.commit();
    }

    public boolean isMosquePlacesSaved() {
        return pref.getBoolean(MOSQUE_OFFLINE, false);
    }

    public void clearStoredData() {
        editor.clear();
        editor.commit();
    }
}