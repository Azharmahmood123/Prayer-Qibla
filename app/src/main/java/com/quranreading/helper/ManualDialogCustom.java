package com.quranreading.helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.quranreading.fragments.CompassDialMenuFragment;
import com.quranreading.listeners.OnCurrentLocationFoundListner;
import com.quranreading.listeners.OnLocationSetListner;
import com.quranreading.qibladirection.ManualLocationDialog;
import com.quranreading.qibladirection.R;

import noman.CommunityGlobalClass;

public class ManualDialogCustom extends Dialog implements OnCurrentLocationFoundListner, ConnectionCallbacks, OnConnectionFailedListener {

    private Context context;
    private TextView okay, cancel, auto, manual, autoLocationText;
    private AutoCompleteTextView editLocation;
    private RelativeLayout autoLocationImg;
    private boolean isManualSelected;
    ProgressBar progressBar;

    AlertDialog alertProvider = null;
    Location mCurrentLocation;
    GoogleApiClient mGoogleApiClient;

    double latitude, longitude;
    String cityName = "";

    private boolean isGPSEnabled = false, isNetworkEnabled = false, isSettingAlertShown = false;

    OnLocationSetListner mOnLocationSetListner;
    private GeoCoderVolley mGeoCodeHelper;

    public ManualDialogCustom(Context context, OnLocationSetListner onLocationSetListner) {
        super(context);
        this.context = context;
        mOnLocationSetListner = onLocationSetListner;
        isSettingAlertShown = false;
        buildGoogleApiClient();
        mGeoCodeHelper = new GeoCoderVolley(context);
        mGeoCodeHelper.setListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_location);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);

        editLocation = (AutoCompleteTextView) findViewById(R.id.dialog_manual_view);
        autoLocationImg = (RelativeLayout) findViewById(R.id.location_layout);
        autoLocationText = (TextView) findViewById(R.id.location_txt);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        auto = (TextView) findViewById(R.id.dialog_location_auto);
        manual = (TextView) findViewById(R.id.dialog_location_manual);
        okay = (TextView) findViewById(R.id.dialog_okay);
        cancel = (TextView) findViewById(R.id.dialog_cancel);

        auto.setOnClickListener(listener);
        manual.setOnClickListener(listener);
        okay.setOnClickListener(listener);
        cancel.setOnClickListener(listener);
        autoLocationImg.setOnClickListener(listener);

        editLocation.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                String name = s.toString();
                // if((name.length() == 1 && name.equals(" ")) || (name.length() == 1 && name.equals(",")) || (name.length() == 2 && name.equals(", ")) || (name.length() == 2 && name.equals(" ,")))

                if ((name.length() == 1 && name.equals(" ")) || (name.length() == 1 && name.equals(","))) {
                    editLocation.setText("");
                }
            }
        });

        getdata();
    }

    @Override
    protected void onStart() {
        super.onStart();
        manualSelected();
        showSoftKeyboard();
    }

    public void showSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void getdata() {
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

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, citiesData1);
            editLocation.setThreshold(1);
            editLocation.setAdapter(adapter);
        }

        c.close();
        dbObj.close();
    }

    public View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.dialog_okay:
                    if (isManualSelected) {
                        String city = editLocation.getText().toString();
                        ManualLocationDialog d = new ManualLocationDialog(context, mOnLocationSetListner);
                        if (city.trim().length() == 0) {
                            ToastClass.showShortToast(context, context.getResources().getString(R.string.toast_enter_city_name), 500, Gravity.CENTER);
                        } else {
                            if (d.updateCityName(city)) {
                                hideKeyboard();
                                editLocation.setText("");
                                ManualDialogCustom.this.dismiss();
                            } else {
                                editLocation.setText("");
                            }
                        }

                        CommunityGlobalClass.getInstance().sendAnalyticEvent("Location 4.0","Manual Location");

                    } else {
                        hideKeyboard();
                        ManualDialogCustom.this.dismiss();
                        if (!cityName.isEmpty()) {
                            mOnLocationSetListner.onLocationSet(cityName, latitude, longitude);
                        }

                        CommunityGlobalClass.getInstance().sendAnalyticEvent("Location 4.0","Current Location");
                    }
                    break;
                case R.id.dialog_cancel:
                    hideKeyboard();
                    ManualDialogCustom.this.dismiss();
                    break;
                case R.id.dialog_location_auto:
                    editLocation.setText("");
                    hideKeyboard();
                    autoSelected();
                    findCurrentLocation();
                    break;

                case R.id.location_layout:
                    editLocation.setText("");
                    hideKeyboard();
                    autoSelected();
                    findCurrentLocation();
                    break;

                case R.id.dialog_location_manual:
                    manualSelected();
                    break;
                default:
            }
        }
    };

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editLocation.getWindowToken(), 0);
    }

    public void autoSelected() {
        progressBar.setVisibility(View.VISIBLE);
        isManualSelected = false;
        autoLocationText.setText("");
        // autoViewSelected.setBackgroundColor(getResources().getColor(R.color.actionbar_bg));
        // manualViewSelected.setBackgroundColor(getResources().getColor(R.color.bg));
        auto.setBackgroundColor(context.getResources().getColor(R.color.actionbar_bg));
        manual.setBackgroundResource(R.drawable.below_tab_selector);
        auto.setTextColor(context.getResources().getColor(R.color.bg));
        manual.setTextColor(context.getResources().getColorStateList(R.color.color_selector));
        autoLocationImg.setVisibility(View.VISIBLE);
        autoLocationText.setVisibility(View.VISIBLE);
        editLocation.setVisibility(View.GONE);
    }

    public void manualSelected() {
        isManualSelected = true;
        // autoViewSelected.setBackgroundColor(getResources().getColor(R.color.bg));
        // manualViewSelected.setBackgroundColor(getResources().getColor(R.color.actionbar_bg));
        manual.setBackgroundColor(context.getResources().getColor(R.color.actionbar_bg));
        auto.setBackgroundResource(R.drawable.below_tab_selector);
        manual.setTextColor(context.getResources().getColor(R.color.bg));
        auto.setTextColor(context.getResources().getColorStateList(R.color.color_selector));
        autoLocationImg.setVisibility(View.GONE);
        autoLocationText.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        editLocation.setVisibility(View.VISIBLE);
    }

    private void findCurrentLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isNetworkConnected()) {
            if (!isGPSEnabled && !isNetworkEnabled) {
                if (!isSettingAlertShown) {
                    providerAlertMessage();
                    isSettingAlertShown = true;
                } else {
                    autoLocationText.setText(context.getResources().getString(R.string.error_location_settings_manual_dialog));
                    progressBar.setVisibility(View.GONE);
                }
            } else {
                if (!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                } else {
                    findUserLocation();
                }
            }
        } else {
            progressBar.setVisibility(View.GONE);
            autoLocationText.setText(R.string.toast_network_error);
        }
    }

    private void findUserLocation() {

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation != null) {
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();

            if (isNetworkConnected()) {

                mGeoCodeHelper.fetchAddressFromCoordinates(context, mCurrentLocation);
            } else {
                progressBar.setVisibility(View.GONE);
                autoLocationText.setText(R.string.toast_network_error);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            autoLocationText.setText(R.string.toast_location_error);
        }
    }

    private void providerAlertMessage() {

        dismissDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.unable_to_find_location));
        builder.setMessage(context.getResources().getString(R.string.enable_provider));
        builder.setCancelable(false);

        builder.setPositiveButton(context.getResources().getString(R.string.settings), new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                CompassDialMenuFragment.LOCATION_REQUEST_DELAY = UserLocation.LOCATION_SETTINGS_DELAY;
                Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(settingsIntent);
            }
        });

        builder.setNegativeButton(context.getResources().getString(R.string.cancel), new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                autoLocationText.setText(context.getResources().getString(R.string.error_location_settings_manual_dialog));
                progressBar.setVisibility(View.GONE);
                CompassDialMenuFragment.LOCATION_REQUEST_DELAY = 0;
                // manualSelected();
            }
        });

        builder.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

                autoLocationText.setText(context.getResources().getString(R.string.error_location_settings_manual_dialog));
                progressBar.setVisibility(View.GONE);
                CompassDialMenuFragment.LOCATION_REQUEST_DELAY = 0;
                // manualSelected();
            }
        });

        builder.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    return true;
                }
                return false;
            }
        });

        alertProvider = builder.create();
        alertProvider.show();
    }

    private void dismissDialog() {
        if (alertProvider != null)
            alertProvider.dismiss();
    }

    public void onResumeLocationDialog() {
        if (context != null) {
            if (isShowing()) {
                if (!isManualSelected) {
                    editLocation.setText("");
                    hideKeyboard();
                    autoSelected();
                    findCurrentLocation();
                }
            }
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        findUserLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub
        mGoogleApiClient.connect();
    }

    @Override
    public void onCurrentLocationFoundListner(String Address, String code, double latitude, double longitude) {
        // TODO Auto-generated method stub

        if (Address == null) {
            Address = "";
        }

        if (Address.equals("") || latitude == 0 || longitude == 0 || latitude == -2 || longitude == -2) {
            cityName = Address;
            progressBar.setVisibility(View.GONE);
            autoLocationText.setText(R.string.toast_location_error);
        } else if (!Address.equals("")) {
            progressBar.setVisibility(View.GONE);
            autoLocationText.setText(Address);
            cityName = Address;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}