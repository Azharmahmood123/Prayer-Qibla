package com.quranreading.helper;

import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;

import com.quranreading.model.PrayerTimeModel;
import com.quranreading.sharedPreference.LocationPref;
import com.quranreading.sharedPreference.PrayerTimeSettingsPref;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatePrayerTime {

    Context contxt;
    private double timezone;
    PrayTime prayers = new PrayTime();
    PrayerTimeSettingsPref mPrayerTimeSettingsPref;
    int juristic = 1, method = 1, adjustment = 1;

    public CalculatePrayerTime(Context ctx) {
        this.contxt = ctx;
        mPrayerTimeSettingsPref = new PrayerTimeSettingsPref(ctx);
    }

    public ArrayList<String> NamazTimings(Calendar cal, double latitude, double longitude) {

        ArrayList<String> prayerTimes;

        // TimeZone tz = TimeZone.getDefault();
        TimeZone tz;
        tz = TimeZone.getTimeZone(getTimeZone());
        if (getTimeZone().isEmpty() || getTimeZone() == null) {
            tz = TimeZone.getDefault();

        }

        long timeNow = new Date().getTime();
        timezone = (double) ((tz.getOffset(timeNow) / 1000) / 60) / 60;
        HashMap<String, Integer> settingsData = mPrayerTimeSettingsPref.getSettings();

        prayers.setTimeFormat(prayers.Time12);

        boolean isAutoSettings = mPrayerTimeSettingsPref.isAutoSettings();


        if (isAutoSettings) {
            AutoSettingsJsonParser autoSettingsJsonParser = new AutoSettingsJsonParser();
            PrayerTimeModel data = autoSettingsJsonParser.getAutoSettings(contxt);


            juristic = data.getJuristicIndex();
            method = data.getConventionNumber();


            // prayers.setAsrJuristic(juristic);
             prayers.setAsrJuristic(juristic-1);  //because asar timing is incorrect show it show jursitic asar timings
            prayers.setCalcMethod(method);
            prayers.setAdjustHighLats(prayers.AngleBased);
            mPrayerTimeSettingsPref.setCalculationMethod(method);
           /* if (method == prayers.Custom) {
                double fajrAngle = data.getAngleFajr();
                double ishaAngle = data.getAngleIsha();

                //MWL Method params with Custom Angles for Fajr and Isha
                double[] Cvalues = {fajrAngle, 1, 0, 0, ishaAngle};

                prayers.setCustomParams(Cvalues);
            }*/

            int daylightSaving = prayers.detectDaylightSaving();

            int asar = data.getCorrections()[3];


            if (mPrayerTimeSettingsPref.isDaylightSaving()) {
                int[] offsets = {daylightSaving + data.getCorrections()[0], daylightSaving + data.getCorrections()[1], daylightSaving + data.getCorrections()[2], daylightSaving + asar, daylightSaving + data.getCorrections()[4], daylightSaving + data.getCorrections()[4], daylightSaving + data.getCorrections()[5]}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
                prayers.tune(offsets);
            } else {
                int[] offsets = {data.getCorrections()[0], data.getCorrections()[1], data.getCorrections()[2],asar, data.getCorrections()[4], data.getCorrections()[4], data.getCorrections()[5]}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
                prayers.tune(offsets);
            }
        } else {
            juristic = settingsData.get(PrayerTimeSettingsPref.JURISTIC);
            method = settingsData.get(PrayerTimeSettingsPref.CALCULATION_METHOD);
            adjustment = settingsData.get(PrayerTimeSettingsPref.LATITUDE_ADJUSTMENT);

            int fajr, sunrize, zuhar, asar, maghrib, isha;
            fajr = mPrayerTimeSettingsPref.getCorrectionsFajr();
            sunrize = mPrayerTimeSettingsPref.getCorrectionsSunrize();
            zuhar = mPrayerTimeSettingsPref.getCorrectionsZuhar();
            asar = mPrayerTimeSettingsPref.getCorrectionsAsar();
            maghrib = mPrayerTimeSettingsPref.getCorrectionsMaghrib();
            isha = mPrayerTimeSettingsPref.getCorrectionsIsha();



            // prayers.setAsrJuristic(juristic);  //becayse
            prayers.setAsrJuristic(juristic-1);  //because asar timing is incorrect show it show jursitic asar timings

            prayers.setCalcMethod(method);
            prayers.setAdjustHighLats(prayers.AngleBased);





            if (mPrayerTimeSettingsPref.isDaylightSaving()) {
                int[] offsets = {60 + fajr, 60 + sunrize, 60 + zuhar, 60 + asar, 60 + maghrib, 60 + maghrib, 60 + isha}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
                prayers.tune(offsets);
            } else {
                int[] offsets = {fajr, sunrize, zuhar, asar, maghrib, maghrib, isha}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
                prayers.tune(offsets);
            }

//            switch (juristic) {
//                case 1: {
//                    prayers.setAsrJuristic(prayers.Shafii);
//                }
//                break;
//                case 2: {
//                    prayers.setAsrJuristic(prayers.Hanafi);
//                }
//                break;
//            }
//
//            switch (method) {
//                case 1: {
//                    prayers.setCalcMethod(prayers.MWL);
//                }
//                break;
//                case 2: {
//                    prayers.setCalcMethod(prayers.Makkah);
//                }
//                break;
//                case 3: {
//                    prayers.setCalcMethod(prayers.Karachi);
//                }
//                break;
//                case 4: {
//                    prayers.setCalcMethod(prayers.ISNA);
//                }
//                break;
//                case 5: {
//                    prayers.setCalcMethod(prayers.Tehran);
//                }
//                break;
//                case 6: {
//                    prayers.setCalcMethod(prayers.Egypt);
//                }
//                break;
//                case 7: {
//                    prayers.setCalcMethod(prayers.Jafari);
//                }
//                break;
//            }
//
//            switch (adjustment) {
//                case 1: {
//                    prayers.setAdjustHighLats(prayers.None);
//                }
//                break;
//                case 2: {
//                    prayers.setAdjustHighLats(prayers.AngleBased);
//                }
//                break;
//                case 3: {
//                    prayers.setAdjustHighLats(prayers.MidNight);
//                }
//                break;
//                case 4: {
//                    prayers.setAdjustHighLats(prayers.OneSeventh);
//                }
//                break;
//            }

        }

        prayerTimes = prayers.getPrayerTimes(cal, latitude, longitude, timezone);

        // prayerTimes.add(0, "1");

        // Remove Sun Set time from List as it is equal to Maghrib time
        prayerTimes.remove(4);

        return prayerTimes;
    }


    public String getTimeZone() {
        DBManager dbObj = new DBManager(contxt);
        dbObj.open();
        String timeZone = "";
        LocationPref locationPref = new LocationPref(contxt);
        HashMap<String, String> alarm = locationPref.getLocation();


     Cursor c = dbObj.getTimeZone(alarm.get(LocationPref.CITY_NAME), locationPref.getLatitude().split("\\.")[0] + ".", locationPref.getLongitude().split("\\.")[0] + ".");

        if (c.moveToFirst()) {
            timeZone = c.getString(c.getColumnIndex(DBManager.FLD_TIME_ZONE));
            c.close();
            dbObj.close();
        }


        //Extract only paranthesis data
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(timeZone);
        while (m.find()) {
            timeZone = m.group(1);
            break;
        }

        return timeZone;
    }



   /* public int getAsarTimeDiffManual() {
        DBManager dbObj = new DBManager(contxt);
        dbObj.open();
        int timeZone = 0;
        LocationPref locationPref = new LocationPref(contxt);
        HashMap<String, String> alarm = locationPref.getLocation();


        Cursor c = dbObj.getTimeZone(alarm.get(LocationPref.CITY_NAME), locationPref.getLatitude().split("\\.")[0] + ".", locationPref.getLongitude().split("\\.")[0] + ".");

        if (c.moveToFirst()) {
           String country = c.getString(c.getColumnIndex(DBManager.FLD_COUNTRY));
            Cursor cc = dbObj.getCountryCodes(country);
            if (cc.moveToFirst()) {
                timeZone = cc.getInt(cc.getColumnIndex(DBManager.FLD_TIME_DIFF));
                cc.close();
            }
            c.close();

        }
        dbObj.close();


        return timeZone;
    }


    //If lat long and city not found in Database then use country code to get the difference
    public int getAsarTimeDiffAuto(Context mContext) {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
         String  countryCode = tm.getSimCountryIso();

        if (countryCode.equals("")) {
            countryCode = tm.getNetworkCountryIso();
        }
        countryCode = countryCode.trim().toLowerCase();
        DBManager dbObj = new DBManager(contxt);
        dbObj.open();
        int timeZone = 0;
        Cursor c = dbObj.getCountrybyCodes(countryCode);
        if (c.moveToFirst()) {
            timeZone = c.getInt(c.getColumnIndex(DBManager.FLD_TIME_DIFF));
            c.close();
        }
        dbObj.close();
        return timeZone;
    }*/
}
