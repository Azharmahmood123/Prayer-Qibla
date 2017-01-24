package com.quranreading.helper;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.quranreading.model.PrayerTimeModel;
import com.quranreading.sharedPreference.LocationPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

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
    private static final String CONVENTION_POSITION= "convention_position";
    private static final String CORRECTIONS = "corrections";
    private static final String ANGLEFAJR = "angle-fajr";
    private static final String ANGLEISHA = "angle-isha";


    public PrayerTimeModel getAutoSettings(Context mContext) {

        String countryCode, code;
        LocationPref locationPref = new LocationPref(mContext);
        code = locationPref.getCountryCode();

        if (code.isEmpty()) {
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            countryCode = tm.getSimCountryIso();

            if (countryCode.equals("")) {
                countryCode = tm.getNetworkCountryIso();
            }

            countryCode = countryCode.trim().toLowerCase();
        } else {
            countryCode = code;
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
