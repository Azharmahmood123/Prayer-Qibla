package com.quranreading.qibladirection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.quranreading.helper.Constants;
import com.quranreading.helper.DBManager;
import com.quranreading.listeners.OnLocationSetListner;
import com.quranreading.sharedPreference.LocationPref;
import com.quranreading.sharedPreference.QiblaDirectionPref;

import java.util.HashMap;

public class ManualLocationDialog {

    Context context;
    AlertDialog locationProvider = null;
    ArrayAdapter<String> cityNamesAdapter = null;
    OnLocationSetListner mOnLocationSetListner;

    public ManualLocationDialog(Context ctx, OnLocationSetListner onLocationSetListner) {
        context = ctx;
        mOnLocationSetListner = onLocationSetListner;
    }

    public void showDialog() {
        if (new QiblaDirectionPref(context).isDatabaseCopied()) {
            if (locationProvider != null)
                locationProvider.dismiss();

            ArrayAdapter<String> adapter;

            if (cityNamesAdapter == null) {
                getdata();
            }

            if (cityNamesAdapter == null)
                return;
            else
                adapter = cityNamesAdapter;

            int topPadding = 0, paddingLeftRightBottom = 0;
            String device = context.getResources().getString(R.string.device);
            if (device.equals("large")) {
                topPadding = 90;
                paddingLeftRightBottom = 30;
            } else if (device.equals("medium")) {
                topPadding = 85;
                paddingLeftRightBottom = 25;
            } else {
                topPadding = 70;
                paddingLeftRightBottom = 20;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getResources().getString(R.string.enter_location_manually));
            // builder.setMessage(context.getResources().getString(R.string.enter_location_manually));
            // builder.setMessage("");

            final AutoCompleteTextView autoText = new AutoCompleteTextView(context);
            autoText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO Auto-generated methodIndex stub

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // TODO Auto-generated methodIndex stub

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated methodIndex stub
                    String name = s.toString();
                    // if((name.length() == 1 && name.equals(" ")) || (name.length() == 1 && name.equals(",")) || (name.length() == 2 && name.equals(", ")) || (name.length() == 2 && name.equals(" ,")))
                    if ((name.length() == 1 && name.equals(" ")) || (name.length() == 1 && name.equals(","))) {
                        autoText.setText("");
                    }
                }
            });

            autoText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            autoText.setPadding(paddingLeftRightBottom, topPadding, paddingLeftRightBottom, paddingLeftRightBottom);
            autoText.setGravity(Gravity.TOP);
            autoText.setThreshold(1);
            // autoText.setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ. "));
            autoText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.CITIES_LENGTH_LIMIT)});
            autoText.setAdapter(adapter);
            autoText.setHint("Mecca, Saudi Arabia");
            builder.setView(autoText);

            // builder.setPositiveButton(context.getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
            // public void onClick(DialogInterface dialog, int whichButton) {
            // String city = autoText.getText().toString().trim();
            // if(city.trim().length() == 0)
            // {
            // showDialog();
            // showToast(context.getResources().getString(R.string.toast_enter_city_name));
            // }
            // else
            // {
            // updateCityName(city);
            // InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            // imm.hideSoftInputFromWindow(autoText.getWindowToken(), 0);
            // }
            // }
            // });

            builder.setCancelable(false);
            builder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(autoText.getWindowToken(), 0);

                    useLastSavedLocation();
                }
            });

            builder.setPositiveButton(context.getString(R.string.okay), null);

            builder.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(autoText.getWindowToken(), 0);

                    useLastSavedLocation();
                }
            });

            builder.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                        return true;
                    }
                    return false;
                }
            });

            locationProvider = builder.create();
            locationProvider.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {

                    Button b = locationProvider.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            String city = autoText.getText().toString().trim();
                            if (city.trim().length() == 0) {
                                showToast(context.getResources().getString(R.string.toast_enter_city_name));
                            } else {
                                if (updateCityName(city)) {
                                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(autoText.getWindowToken(), 0);
                                    locationProvider.dismiss();
                                } else {
                                    autoText.setText("");
                                }
                            }
                        }
                    });
                }
            });

            locationProvider.show();

            autoText.requestFocus();

          /*  InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            imm.showSoftInputFromInputMethod(autoText.getWindowToken(), 0);*/
        }
    }

    private void getdata() {
        DBManager dbObj = new DBManager(context);
        dbObj.open();

        Cursor c = dbObj.getAllCities();

        if (c.moveToFirst()) {
            int i = 0;
            String[] citiesData = new String[c.getCount()];

            while (c.moveToNext()) {
                String city = c.getString(c.getColumnIndex(DBManager.FLD_CITY));
                String country = c.getString(c.getColumnIndex(DBManager.FLD_COUNTRY));
                citiesData[i] = city + ", " + country;
                i++;
            }

            String[] citiesData1 = new String[citiesData.length - 1];

            for (int j = 0; j < citiesData.length - 1; j++) {
                citiesData1[j] = citiesData[j];
            }

            citiesData = null;

            cityNamesAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, citiesData1);
        }

        c.close();
        dbObj.close();

        // return cityNamesAdapter;
    }

    public boolean updateCityName(String name) {
        String cityName = "", countryName = "";

        String[] nameArray = new String[3];

        nameArray = name.split(",");

        int length = nameArray.length;

        if (length == 3) {
            cityName = nameArray[0].trim() + ", " + nameArray[1].trim();
            countryName = nameArray[2].trim();
        } else if (length == 2) {
            cityName = nameArray[0].trim();
            countryName = nameArray[1].trim();
        } else if (length == 1) {
            cityName = nameArray[0].trim();
            countryName = "";
        }

        return checkInfo(cityName, countryName);
    }

    private boolean checkInfo(String cityName, String countryName) {
        String city, country, latd, longt, timeZone;

        LocationPref locPref = new LocationPref(context);
        DBManager dbObj = new DBManager(context);
        dbObj.open();

        countryName = countryName.replace("'", "").trim();
        cityName = cityName.replace("'", "").trim();

        if (cityName.length() > 0 && countryName.length() > 0) {


            //**************** Get country code from the table of list
            Cursor cCode = dbObj.getCountryCodes(countryName);
            if (cCode.moveToFirst()) {
                String countryCode = cCode.getString(cCode.getColumnIndex(DBManager.FLD_CODE));
                locPref.setCountryCode(countryCode);
                cCode.close();
            } else {
                cCode.close();
            }
            // **********************************************************


            Cursor c = dbObj.getCityInfo(countryName, cityName);

            if (c.moveToFirst()) {
                city = c.getString(c.getColumnIndex(DBManager.FLD_CITY));
                country = c.getString(c.getColumnIndex(DBManager.FLD_COUNTRY));
                latd = c.getString(c.getColumnIndex(DBManager.FLD_LATITUDE));
                longt = c.getString(c.getColumnIndex(DBManager.FLD_LONGITUDE));
                timeZone = c.getString(c.getColumnIndex(DBManager.FLD_TIME_ZONE));

				/*
                 * int lastIndx = timeZone.indexOf(")"); String zone = timeZone.substring(1, lastIndx); Log.e("Time Zone", timeZone); double zoneValue = 0; if(zone.contains("+")) { String[] arrZone = zone.split("\\+")[1].split(":"); zoneValue = Double.parseDouble(arrZone[0] + "." + arrZone[1]); }
				 * else if(zone.contains("-")) { String[] arrZone = zone.split("-")[1].split(":"); zoneValue = Double.parseDouble("-" + arrZone[0] + "." + arrZone[1]); } else { zoneValue = 0.0; } locPref.setLocation(city, country, latd, longt, String.valueOf(zoneValue));
				 */


                locPref.setLocation(city, latd, longt);

                c.close();
                dbObj.close();


                mOnLocationSetListner.onLocationSet(city, Double.parseDouble(latd), Double.parseDouble(longt));
                return true;
            } else {
                showToast(context.getResources().getString(R.string.toast_correct_city_name));

                c.close();
                dbObj.close();
                return false;
            }
        } else {
            showToast(context.getResources().getString(R.string.toast_correct_city_name));
            return false;
        }

    }

    private void showToast(String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void useLastSavedLocation() {

        LocationPref locPref = new LocationPref(context);
        HashMap<String, String> alarm = locPref.getLocation();

        String Address = alarm.get(LocationPref.CITY_NAME);
        locPref.setFirstLaunch();
        if (!Address.equals("")) {
            double latitude = Double.parseDouble(alarm.get(LocationPref.LATITUDE));
            double longitude = Double.parseDouble(alarm.get(LocationPref.LONGITUDE));
            mOnLocationSetListner.onLocationSet(Address, latitude, longitude);
        } else {
            showToast(context.getString(R.string.toast_connect_to_internet_or_set_location_manually));
            mOnLocationSetListner.onLocationSet("", 0, 0);
        }
    }
}
