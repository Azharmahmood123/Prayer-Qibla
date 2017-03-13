package com.quranreading.helper;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.telephony.TelephonyManager;

import com.quranreading.model.PrayerTimeModel;
import com.quranreading.sharedPreference.LocationPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by cyber on 12/19/2016.
 */

public class AutoSettingsJsonParser {

    private static final String JSON_FILE_NAME = "QiblaAutoSettings.json";

    private static final String HIJRI = "hijri";
    private static final String DST = "dst";
    private static final String JURISTIC = "juristic";
    private static final String JURISTICINDEX = "juristic_index";
    private static final String CONVENTION = "convention";
    private static final String CONVENTIONINDEX = "convention_index";
    private static final String CONVENTION_POSITION = "convention_position";
    private static final String CORRECTIONS = "corrections";
    private static final String ANGLEFAJR = "angle-fajr";
    private static final String ANGLEISHA = "angle-isha";

    public String getCountryName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        String countryCode = "";
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                countryCode = addresses.get(0).getCountryCode();
                if (!countryCode.isEmpty()) {
                    DBManager dbObj = new DBManager(context);
                    dbObj.open();
                    LocationPref locationPref = new LocationPref(context);
                    if (countryCode.length() > 0) {
                        Cursor cCode = dbObj.getCountryCodes(countryCode);
                        if (cCode.moveToFirst()) {
                            String countryCode2 = cCode.getString(cCode.getColumnIndex(DBManager.FLD_CODE));
                            countryCode = countryCode2;
                            locationPref.setCountryCode(countryCode2);
                            cCode.close();
                            dbObj.close();
                            return countryCode;
                        } else {
                            cCode.close();
                            dbObj.close();
                        }

                    }

                }
            }

        } catch (IOException ignored) {
            //do something
        }
        return countryCode;

    }

    public String operateFromDataBase(Context mContext) {
        String countryCode = "";

        LocationPref locationPref = new LocationPref(mContext);
        DBManager dbObj = new DBManager(mContext);
        dbObj.open();
            //Split string to get the only point before value lat and long
      //  Cursor c = dbObj.getCountry(locationPref.getLatitude().split("\\.")[0] + ".", locationPref.getLongitude().split("\\.")[0] + ".");
        Cursor c = dbObj.getCountry(locationPref.getLatitude().split("\\.")[0] + ".", locationPref.getLongitude().split("\\.")[0] + ".");


        if (c != null) {
            if (c.moveToFirst()) {
                String countryCode2 = c.getString(c.getColumnIndex(DBManager.FLD_COUNTRY));
                if (countryCode2.length() > 0) {
                    Cursor cCode = dbObj.getCountryCodes(countryCode2);
                    if (cCode.moveToFirst()) {
                        countryCode = cCode.getString(cCode.getColumnIndex(DBManager.FLD_CODE));
                        locationPref.setCountryCode(countryCode2);
                        cCode.close();
                    } else {
                        cCode.close();
                    }
                    c.close();
                    dbObj.close();
                }
            }
            c.close();
            dbObj.close();


        } else {
            c.close();
            dbObj.close();
        }
        return countryCode;
    }

    public PrayerTimeModel getAutoSettings(Context mContext) {

        String countryCode = "";

        //So work on db method
        countryCode = operateFromDataBase(mContext);

        //  code = locationPref.getCountryCode();
        //This will only when internet works
        // countryCode = getCountryName(mContext, Double.parseDouble(locationPref.getLatitudeCurrent()), Double.parseDouble(locationPref.getLongitudeCurrent()));

        //If db returns null or inexitance data occur than move to this code
        if (countryCode.isEmpty() || countryCode ==null) {
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            countryCode = tm.getSimCountryIso();

            if (countryCode.equals("")) {
                countryCode = tm.getNetworkCountryIso();
            }
            countryCode = countryCode.trim().toLowerCase();
        }
        //countryCode = "GB";

        PrayerTimeModel data = new PrayerTimeModel();
        JSONObject obj;
        countryCode = countryCode.toUpperCase();

        String d = loadJSONFromAsset(mContext);

        try {
            obj = new JSONObject(d);
            JSONObject objData = obj.getJSONObject(countryCode);

            if (objData != null) {

                if (objData.has(ANGLEFAJR)) {
                    data.setAngleFajr(objData.getDouble(ANGLEFAJR));
                }

                if (objData.has(ANGLEISHA)) {
                    data.setAngleFajr(objData.getDouble(ANGLEISHA));
                }

                JSONArray arrayCorrections = objData.getJSONArray(CORRECTIONS);
                int[] corrections = new int[6];
                for (int i = 0; i < arrayCorrections.length(); i++) {
                    corrections[i] = arrayCorrections.getInt(i);
                }

                data.setCorrections(corrections);
                data.setConvention(objData.getString(CONVENTION));
                data.setConventionNumber(objData.getInt(CONVENTIONINDEX));
                data.setDst(objData.getInt(DST));
                data.setHijri(objData.getInt(HIJRI));
                data.setJuristicIndex(objData.getInt(JURISTICINDEX));
                data.setJuristic(objData.getString(JURISTIC));
                data.setConventionPosition(objData.getInt(CONVENTION_POSITION));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }


    private String loadJSONFromAsset(Context mContext) {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open(JSON_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
