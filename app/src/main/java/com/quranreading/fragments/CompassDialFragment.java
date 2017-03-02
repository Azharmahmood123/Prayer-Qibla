package com.quranreading.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.helper.ManualDialogCustom;
import com.quranreading.helper.UserLocation;
import com.quranreading.listeners.MagAccelListener;
import com.quranreading.listeners.OnLocationSetListner;
import com.quranreading.listeners.RotationUpdateDelegate;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;
import com.quranreading.sharedPreference.DialPref;
import com.quranreading.sharedPreference.LocationPref;

import static com.quranreading.fragments.CompassDialMenuFragment.LOCATION_REQUEST_DELAY;

public class CompassDialFragment extends Fragment implements RotationUpdateDelegate, OnLocationSetListner {

    LocationReceiver mLocationReceiver;
    LocationPref locationPref;
    ManualDialogCustom manualDialog;

    private UserLocation mUserLocation;

    // These will be used to handle location access scenario when user try to turn on location access from Settings
    Runnable mRunnableLocation;
    Handler mHandlerLocation;
    public boolean inProcess = false;

    private ProgressBar progressBar;
    private DialPref dialPref;
    private ImageView imageCompass;
    private double latitude;
    private double longitude;
    private MagAccelListener mMagAccel;
    private RelativeLayout layoutCompass;
    private SensorManager mSensorManager;
    private static ImageView imagePointer;
    private static int imgPointerResrc = 0;
    private Sensor accelerometer, magnetometer;
    private TextView tvCity, tvHeading, tvWarning;
    //private TextView tvDegree;
    private LinearLayout lacationNameLayout;
    private RelativeLayout calibrationLayout;
    private boolean locChk = false, chkBlankPin = false;
    private float degree2 = 0, currentDegree = 0f, currentDegree2 = 0f, currentDegree3 = 0f;

    int dialValue;
    public boolean isActivityOpened;


    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        mContext = getContext();

        mUserLocation = new UserLocation(mContext);
        // It will be used to handle location access when user try to turn on location access from Settings
        mHandlerLocation = new Handler();
        mRunnableLocation = new Runnable() {
            @Override
            public void run() {
                inProcess = false;
                mUserLocation.setOnLocationSetListner(CompassDialFragment.this);
                mUserLocation.checkLocation(false, true);
            }
        };

        mLocationReceiver = new LocationReceiver();
        mContext.registerReceiver(mLocationReceiver, new IntentFilter(CompassDialMenuFragment.LOCATION_INTENT_FILTER));

        locationPref = new LocationPref(mContext);
        dialPref = new DialPref(mContext);

        mMagAccel = new MagAccelListener(this);
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sendAnalyticsData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;

        if (((GlobalClass) mContext.getApplicationContext()).deviceS3) {
            // Samsung S3 && s4 etc.
            view = inflater.inflate(R.layout.fragment_compass_s3, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_compass, container, false);
        }

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tvHeading = (TextView) getActivity().findViewById(R.id.txt_toolbar);
        tvHeading.setText(R.string.qibla_direction);

        intializeDialViews(view);


        calibrationLayout = (RelativeLayout) getActivity().findViewById(R.id.calibration_layout);

        lacationNameLayout = (LinearLayout) view.findViewById(R.id.layout_locationCompass);
        RelativeLayout layoutMoreApps = (RelativeLayout) view.findViewById(R.id.layout_more_apps);
        tvCity = (TextView) view.findViewById(R.id.tv_city);
        //tvDegree = (TextView) findViewById(R.id.tv_Degree);
        tvWarning = (TextView) view.findViewById(R.id.tv_Warning);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarCompass);
        progressBar.setVisibility(View.VISIBLE);

        tvWarning.setText(mContext.getString(R.string.warning) + ": " + mContext.getString(R.string.warning_msg));

        imageCompass = (ImageView) view.findViewById(R.id.imageViewCompass);
        imagePointer = (ImageView) view.findViewById(R.id.imageViewPointer);
        layoutCompass = (RelativeLayout) view.findViewById(R.id.image_layout);

        imageCompass.setDrawingCacheEnabled(true);
        imagePointer.setDrawingCacheEnabled(true);
        layoutCompass.setDrawingCacheEnabled(true);


        tvCity.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
        tvHeading.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoR);
        tvWarning.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);

        imageCompass.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onDialClick(v);
            }

            ;
        });
        layoutMoreApps.setVisibility(View.GONE);

        layoutMoreApps.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!isActivityOpened) {
                    isActivityOpened = true;

                    String s = "market://search?q=pub:Quran+Reading";
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                    startActivity(browserIntent);
                }
            }
        });

        lacationNameLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (manualDialog != null) {
                    if (!manualDialog.isShowing()) {
                        manualDialog = new ManualDialogCustom(mContext, CompassDialFragment.this);
                        manualDialog.show();
                    }
                } else {
                    manualDialog = new ManualDialogCustom(mContext, CompassDialFragment.this);
                    manualDialog.show();
                }
            }
        });

        if (locationPref.getCityName().equals("")) {
            tvCity.setText(mContext.getString(R.string.set) + " " + mContext.getString(R.string.location));
            // showToast(mContext.getString(R.string.set) + " " + mContext.getString(R.string.location));
            tvHeading.setText("--------");
            // tvDistance.setText(getResources().getString(R.string.distance_from_qibla) + " " + "------ KM");
        } else {
            tvCity.setText(locationPref.getCityName());
            tvHeading.setText(mContext.getString(R.string.qibla_direction) + ": " + locationPref.getAngle() + "\u00b0");
            // tvDistance.setText(getResources().getString(R.string.distance_from_qibla) + " " + locationPref.getDistance() + " KM");
        }

        return view;
    }

    private void sendAnalyticsData() {

        AnalyticSingaltonClass.getInstance(mContext).sendScreenAnalytics("Qibla Compass 4.0");
    }

    private void requestLocaion() {
        mUserLocation.setOnLocationSetListner(this);
        mUserLocation.checkLocation(false, true);
    }

    private void requestLocationDelayed() {
        mHandlerLocation.removeCallbacks(mRunnableLocation);
        mHandlerLocation.postDelayed(mRunnableLocation, LOCATION_REQUEST_DELAY);
        LOCATION_REQUEST_DELAY = 0;
    }

    @Override
    public void onResume() {
        super.onResume();

        inProcess = false;
        if (LOCATION_REQUEST_DELAY > 0) {
            requestLocationDelayed();
        } else {
            requestLocaion();
        }

        if (manualDialog != null) {
            manualDialog.onResumeLocationDialog();
        }

        Log.e("onResume", "CompassDialMenuFragment");
        isActivityOpened = false;

        dialValue = dialPref.getDialValue();
        setDial(dialValue);
        if (accelerometer == null && magnetometer == null) {
            // showToastSensor(mContext.getResources().getString(R.string.toast_no_sensor));
            chkBlankPin = true;
            imagePointer.setImageResource(R.drawable.blank_pin);

            tvWarning.setText(mContext.getResources().getString(R.string.cannot) + " " + mContext.getResources().getString(R.string.find_qibla_direction) + "\n" + mContext.getResources().getString(R.string.your_device_not_compatible));
            // tvWarning.setVisibility(View.GONE);
        } else if (accelerometer == null) {
            // showToastSensor(mContext.getResources().getString(R.string.toast_no_accelerometer));
            chkBlankPin = true;
            imagePointer.setImageResource(R.drawable.blank_pin);
            tvWarning.setText(mContext.getResources().getString(R.string.cannot) + " " + mContext.getResources().getString(R.string.find_qibla_direction) + "\n" + mContext.getResources().getString(R.string.your_device_not_compatible));
            // tvWarning.setVisibility(View.GONE);
        } else if (magnetometer == null) {
            // showToastSensor(mContext.getResources().getString(R.string.toast_no_magnetometer));
            chkBlankPin = true;
            imagePointer.setImageResource(R.drawable.blank_pin);
            tvWarning.setText(mContext.getResources().getString(R.string.cannot) + " " + mContext.getResources().getString(R.string.find_qibla_direction) + "\n" + mContext.getResources().getString(R.string.your_device_not_compatible));
            // tvWarning.setVisibility(View.GONE);
        } else {
            mSensorManager.unregisterListener(mMagAccel);
            mSensorManager.registerListener(mMagAccel, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(mMagAccel, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        }
        if (manualDialog != null) {
            manualDialog.onResumeLocationDialog();
        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        mHandlerLocation.removeCallbacks(mRunnableLocation);
        progressBar.setVisibility(View.VISIBLE);
        // mUserLocation.dismissDialog();
        mSensorManager.unregisterListener(mMagAccel);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mContext.unregisterReceiver(mLocationReceiver);
    }

    public Float getBearing() {
        // Al Masjid Al Haram Makkah

        // Old Values
        // double mLatitude = 21.42208028884839;
        // double mLongitude = 39.82639700174332;

        // New Vlaues
        double mLatitude = 21.422510;
        double mLongitude = 39.826160;

        double pi = 4 * Math.atan2(1, 1);

        double DtoR = pi / 180;

        double RtoD = 180 / pi;

        // Converte to radians for calculations

		/*
         * double latitude = 40.735812; double longitude= -74.004121;
		 */

		/*
         * double latitude = 51.618017; double longitude= 64.308472;
		 */

        double L1 = latitude * DtoR;

        double L2 = mLatitude * DtoR;

        double I1 = longitude * DtoR;

        double I2 = mLongitude * DtoR;

        // Distance

        double distance = Math.acos(Math.cos(L1 - L2) - (1 - Math.cos(I1 - I2)) * Math.cos(L1) * Math.cos(L2));

        // To convert this distance to nautical miles multiply by 60;

        // Then by 1.852 to convert to Kms.

        double distancKms = distance * 60 * 1.852 * RtoD;
        double distanceMeters = distancKms * 1000.0;

        String[] arrDistance = String.valueOf(distancKms).split("\\.");
        String calcDistance = arrDistance[0] + "." + arrDistance[1].substring(0, 2);

        // String calcDistance1 = new DecimalFormat("####.##").format(distancKms);

        if (distanceMeters < 1000) {
            // tvDistance.setText(getResources().getString(R.string.near_qibla));
        } else {
            // tvDistance.setText(getResources().getString(R.string.distance_from_qibla) + " " + calcDistance + " KM");
            locationPref.setDistance("" + calcDistance);
        }
        // Log.v("Distance", String.valueOf(distancKms));
        // NSLog(@"Approx distance is %f",distancKms);

        // Now the bearing

        double Dlong = I1 - I2;

        double A = Math.sin(L2) - Math.cos(distance + L1 - pi / 2);

        double B = Math.acos(A / (Math.cos(L1) * Math.sin(distance)) + 1);

        double bearing;

        if (Dlong < pi && Dlong > 0)
            bearing = (2 * pi) - B;
        else
            bearing = B;

        // Convert to degress

        bearing = bearing * RtoD;

        String[] arrBearing = String.valueOf(bearing).split("\\.");
        String calcBearing = arrBearing[0] + "." + arrBearing[1].substring(0, 2);

        // String calcBearing1 = new DecimalFormat("##.##").format(bearing);

        tvHeading.setText(mContext.getString(R.string.qibla_direction) + ": " + calcBearing + "\u00b0");
        locationPref.setAngle("" + calcBearing);

        return Float.parseFloat(calcBearing);
    }

    @Override
    public void onRotationUpdate(float[] newMatrix) {
        // TODO Auto-generated method stub
        double azimut = 0;
        float orientation[] = new float[3];

        SensorManager.getOrientation(newMatrix, orientation);

        azimut = orientation[0]; // orientation contains: azimut, pitch and roll

        azimut = azimut * 360 / (2 * 3.14159f);

        float degree = Math.round(azimut);

        if (locChk) {
            setComapsswithLocation(degree);
            if (isFlatDevice(orientation)) {
                calibrationLayout.setVisibility(View.GONE);
            } else {
                calibrationLayout.setVisibility(View.VISIBLE);
            }
        } else {
            setComapsswithoutLocation(degree);
        }
    }

    private boolean isFlatDevice(float[] g) {

        double norm_Of_g = Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);

// Normalize the accelerometer vector
        double[] d = new double[3];
        d[0] = d[0] / norm_Of_g;
        d[1] = d[1] / norm_Of_g;
        d[2] = d[2] / norm_Of_g;

        //Vertical Movement
        int inclination = (int) Math.round(Math.toDegrees(Math.acos(g[1])));

        //Horizental Movement
        // int inclination2 = (int) Math.round(Math.toDegrees(Math.acos(g[2])));

        if (inclination < 30 || inclination > 150/* || inclination2 < 60 || inclination2 > 120*/) {
            return false;
        } else {
            return true;
        }
    }

    public void setComapsswithLocation(float degree) {

        if (chkBlankPin) {
            if (imgPointerResrc > 0) {
                chkBlankPin = false;
                imagePointer.setImageResource(imgPointerResrc);
            }
        }

        int rotationTiming = 1000;
        double radianConst = 3.14 / 180.0;
        float degree3 = 0;

        RotateAnimation ra = new RotateAnimation(currentDegree, -degree2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setInterpolator(new LinearInterpolator());
        ra.setDuration(rotationTiming);
        ra.setFillAfter(true);

        imageCompass.startAnimation(ra);
        currentDegree = -degree2;

        degree3 = (float) ((currentDegree + degree2) * radianConst);

        RotateAnimation rb = new RotateAnimation(currentDegree2, -degree3, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rb.setInterpolator(new LinearInterpolator());
        rb.setDuration(rotationTiming);
        rb.setFillAfter(true);

        imagePointer.startAnimation(rb);
        currentDegree2 = -degree3;

        degree = degree - degree2;

        RotateAnimation rc = new RotateAnimation(currentDegree3, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rc.setInterpolator(new LinearInterpolator());
        rc.setDuration(rotationTiming);
        rc.setFillAfter(true);

        layoutCompass.startAnimation(rc);
        currentDegree3 = -degree;
    }

    private void setComapsswithoutLocation(float degree) {

        if (locationPref.getCityName().equals("")) {
            tvCity.setText(mContext.getString(R.string.set) + " " + mContext.getString(R.string.location));
            // showToast(mContext.getString(R.string.set) + " " + mContext.getString(R.string.location));
            tvHeading.setText("--------");
            // tvDistance.setText(getResources().getString(R.string.distance_from_qibla) + " " + "------ KM");

        } else {
            tvCity.setText(locationPref.getCityName());
            tvHeading.setText(mContext.getString(R.string.qibla_direction) + ": " + locationPref.getAngle() + "\u00b0");
            // tvDistance.setText(getResources().getString(R.string.distance_from_qibla) + " " + locationPref.getDistance() + " KM");
        }

        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);

        imageCompass.startAnimation(ra);
        currentDegree = -degree;
    }

    public void onDialClick(View v) {
        if (dialValue < 6) {
            dialValue++;
        } else {
            dialValue = 1;
        }
        setDial(dialValue);
    }


    View.OnClickListener onDialsClickListner = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onDialRow(v);
        }
    };

    public void onDialRow(View view) {
        Integer btnClick = Integer.parseInt(view.getTag().toString());
        setDial(btnClick);
    }

    public void intializeDialViews(View view) {
        //TextView tvDialHead;
        TextView[] dialtexts = new TextView[6];
        LinearLayout[] dialRows = new LinearLayout[6];

        dialRows[0] = (LinearLayout) view.findViewById(R.id.dial_row_1);
        dialRows[1] = (LinearLayout) view.findViewById(R.id.dial_row_2);
        dialRows[2] = (LinearLayout) view.findViewById(R.id.dial_row_3);
        dialRows[3] = (LinearLayout) view.findViewById(R.id.dial_row_4);
        dialRows[4] = (LinearLayout) view.findViewById(R.id.dial_row_5);
        dialRows[5] = (LinearLayout) view.findViewById(R.id.dial_row_6);

        //tvDialHead = (TextView) view.findViewById(R.id.tv_dial_head);
        dialtexts[0] = (TextView) view.findViewById(R.id.dial_text_1);
        dialtexts[1] = (TextView) view.findViewById(R.id.dial_text_2);
        dialtexts[2] = (TextView) view.findViewById(R.id.dial_text_3);
        dialtexts[3] = (TextView) view.findViewById(R.id.dial_text_4);
        dialtexts[4] = (TextView) view.findViewById(R.id.dial_text_5);
        dialtexts[5] = (TextView) view.findViewById(R.id.dial_text_6);

        //tvDialHead.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoR);

        for (int pos = 0; pos < dialRows.length; pos++) {
            dialtexts[pos].setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
            dialRows[pos].setOnClickListener(onDialsClickListner);
        }
    }

    public void setDial(int dialValue) {

        String deviceSize = getResources().getString(R.string.device);

        // String deviceSize = "small";
        if (deviceSize.equals("large")) {
            Log.e("Large Device", "Large Device");

            String uriDial = "drawable/dial_" + dialValue + "_l_t";
            String uriPointer = "drawable/dial_pin_" + dialValue + "_l_t";

            int resrcDial = getResources().getIdentifier(uriDial, null, mContext.getPackageName());
            imgPointerResrc = getResources().getIdentifier(uriPointer, null, mContext.getPackageName());

            if (resrcDial > 0 && imgPointerResrc > 0) {
                imageCompass.setImageResource(resrcDial);
                imagePointer.setImageResource(imgPointerResrc);
            }
        } else if (deviceSize.equals("medium")) {
            Log.e("Large Device", "Mini-Large Device");

            String uriDial = "drawable/dial_" + dialValue + "_l";
            String uriPointer = "drawable/dial_pin_" + dialValue + "_l";

            int resrcDial = getResources().getIdentifier(uriDial, null, mContext.getPackageName());
            imgPointerResrc = getResources().getIdentifier(uriPointer, null, mContext.getPackageName());

            if (resrcDial > 0 && imgPointerResrc > 0) {
                imageCompass.setImageResource(resrcDial);
                imagePointer.setImageResource(imgPointerResrc);
            }
        } else {
            String uriDial = "drawable/dial_" + dialValue + "_s";
            String uriPointer = "drawable/dial_pin_" + dialValue + "_s";

            int resrcDial = getResources().getIdentifier(uriDial, null, mContext.getPackageName());
            imgPointerResrc = getResources().getIdentifier(uriPointer, null, mContext.getPackageName());

            if (resrcDial > 0 && imgPointerResrc > 0) {
                imageCompass.setImageResource(resrcDial);
                imagePointer.setImageResource(imgPointerResrc);
            }
        }

        if (!locChk || accelerometer == null || magnetometer == null) {
            chkBlankPin = true;
            imagePointer.setImageResource(R.drawable.blank_pin);
        }

        dialPref.setDialValue(dialValue);
    }

    public void showToast(String msg) {
        Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void showToastSensor(String msg) {
        Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            progressBar.setVisibility(View.GONE);

            try {
                String city = intent.getStringExtra(CompassDialMenuFragment.CITY_NAME);
                double lat = intent.getDoubleExtra(CompassDialMenuFragment.LATITUDE, 0);
                double lng = intent.getDoubleExtra(CompassDialMenuFragment.LONGITUDE, 0);

                if (lat != 0 && lat != -2 && lng != 0 && lng != -2) {
                    locChk = true;
//                    mContext.showCalibration();
                } else {
                    locChk = false;
                    chkBlankPin = true;
                    imagePointer.setImageResource(R.drawable.blank_pin);
                }

                latitude = lat;
                longitude = lng;
                degree2 = getBearing();

                if (city.equals("")) {
                    city = mContext.getString(R.string.set) + " " + mContext.getString(R.string.location);
                    // showToast(mContext.getString(R.string.set) + " " + mContext.getString(R.string.location));
                }

                tvCity.setText(city);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onLocationSet(String cityName, double latitud, double longitud) {

        progressBar.setVisibility(View.GONE);

        try {
            if (latitud != 0 && latitud != -2 && longitud != 0 && longitud != -2) {
                locChk = true;
//                mContext.showCalibration();
            } else {
                locChk = false;
                chkBlankPin = true;
                imagePointer.setImageResource(R.drawable.blank_pin);
            }

            latitude = latitud;
            longitude = longitud;
            degree2 = getBearing();
            if (cityName.equals("")) {
                cityName = mContext.getString(R.string.set) + " " + mContext.getString(R.string.location);
                showToast(mContext.getString(R.string.set) + " " + mContext.getString(R.string.location));
            } else {
                saveLatestLocation(cityName, "" + latitude, "" + longitude);
            }
            tvCity.setText(cityName);

            // send Broadcast to Timings Fragment when user select location from Manual Dailog
            // When tap on City name TextView
            Intent intnet = new Intent(CompassDialMenuFragment.LOCATION_INTENT_FILTER);
            intnet.putExtra(TimingsFragment.CITY_NAME, cityName);
            intnet.putExtra(TimingsFragment.LATITUDE, latitude);
            intnet.putExtra(TimingsFragment.LONGITUDE, longitude);
            mContext.sendBroadcast(intnet);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onNewLocationDetected(String newCityName, String oldCityName, double latitude, double longitude) {

        //  showNewLocationAlert(newCityName, oldCityName, latitude, longitude);


        if (locationPref.getLocationMethodPref() == 2) {
            showNewAlertLocationDialog(newCityName, oldCityName, latitude, longitude);
        } else if (locationPref.getLocationMethodPref() == 0) {
            onLocationSet(newCityName, latitude, longitude);
        } else {
            String city = locationPref.getCityName();
            double lat = Double.parseDouble(locationPref.getLatitude());
            double lng = Double.parseDouble(locationPref.getLongitude());
            onLocationSet(city, lat, lng);

        }
    }

    private void showNewAlertLocationDialog(final String newCityName, String oldCityName, final double latitude, final double longitude) {

        Spanned message = Html.fromHtml(getString(R.string.new_location_detected_msg1) + " <b>" + oldCityName + "</b> " + getString(R.string.to) + " <b>" + newCityName + "</b> " + getString(R.string.and) + " " + getString(R.string.new_location_detected_msg2) + "?");
        // custom dialog
        final Dialog dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.custom_loc_dialog);

        dialog.setTitle(getString(R.string.new_location_detected));
        TextView msgText = (TextView) dialog.findViewById(R.id.text_locaiton_dectection_msg);
        msgText.setText(message);
        dialog.setCancelable(false);
        final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.chk_dialog_loc);
        Button dialogYesButton = (Button) dialog.findViewById(R.id.loc_yes_btn);
        dialogYesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (checkBox.isChecked()) {
                    locationPref.setLocationMethodPref(0);
                }
                onLocationSet(newCityName, latitude, longitude);
            }
        });

        Button dialogNoButton = (Button) dialog.findViewById(R.id.loc_no_btn);
        dialogNoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (checkBox.isChecked()) {
                    locationPref.setLocationMethodPref(1);
                }
                progressBar.setVisibility(View.GONE);
                String city = locationPref.getCityName();
                double lat = Double.parseDouble(locationPref.getLatitude());
                double lng = Double.parseDouble(locationPref.getLongitude());
                onLocationSet(city, lat, lng);
            }
        });
        dialog.show();
    }


    private void showNewLocationAlert(final String newCityName, String oldCityName, final double latitude, final double longitude) {

        Spanned message = Html.fromHtml(getString(R.string.new_location_detected_msg1) + " <b>" + oldCityName + "</b> " + getString(R.string.to) + " <b>" + newCityName + "</b> " + getString(R.string.and) + " " + getString(R.string.new_location_detected_msg2) + "?");

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.new_location_detected));
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton(mContext.getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                onLocationSet(newCityName, latitude, longitude);
            }
        });

        builder.setNegativeButton(mContext.getResources().getString(R.string.no_thanks), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                progressBar.setVisibility(View.GONE);

                String city = locationPref.getCityName();
                double lat = Double.parseDouble(locationPref.getLatitude());
                double lng = Double.parseDouble(locationPref.getLongitude());
                onLocationSet(city, lat, lng);


                dialog.dismiss();
            }
        });
//
//        builder.setOnKeyListener(new Dialog.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
//                // TODO Auto-generated method stub
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//                    return true;
//                }
//                return false;
//            }
//        });
//
        AlertDialog alertProvider = builder.create();
//
//        alertProvider.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//
//            }
//        });
        alertProvider.show();
    }

    private void saveLatestLocation(String address, String lat, String lng) {

        locationPref.setLocation(address, lat, lng);
    }

}