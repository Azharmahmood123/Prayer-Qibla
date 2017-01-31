package com.quranreading.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.quranreading.listeners.MagAccelListener;
import com.quranreading.listeners.RotationUpdateDelegate;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;
import com.quranreading.sharedPreference.LocationPref;

import static com.quranreading.helper.UserLocation.LOCATION_SETTINGS_DELAY;

public class CompassMapsFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, RotationUpdateDelegate/*, GoogleMap.OnCameraChangeListener*/ {

    int MAX_ZOOM_MAP = 15;
    int MIN_ZOOM_MAP = 3;
    int POLYLINE_WIDTH = 9;
    double SENSOR_CALLBACK_DELAY = 1.5;

    double makkahLatitude = 21.422510;
    double makkahLongitude = 39.826160;
    double currentLat, currentLng;
    LatLng currentLocation, qiblaLocation;
    LocationReceiver mLocationReceiver;

    boolean isQiblaZoom = false;
    LocationPref locationPref;

    private GoogleMap mMap;
    Context mContext;

    private MagAccelListener mMagAccel;

    private SensorManager mSensorManager;
    private Sensor accelerometer, magnetometer;

    TextView tvDistance;
    ImageView btnMapView;
    int mapViewIndex = 0;

    ImageView ivQiblaLoc, ivCurrentLoc;
    boolean isAnimating = false;
    LinearLayout layoutSettingEnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        locationPref = new LocationPref(mContext);
        mMagAccel = new MagAccelListener(this);
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mLocationReceiver = new LocationReceiver();
        mContext.registerReceiver(mLocationReceiver, new IntentFilter(CompassFragmentIndex.LOCATION_INTENT_FILTER));
    }

    @Override
    public void onDestroy() {
        mContext.unregisterReceiver(mLocationReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_qibla_map, container, false);

        tvDistance = (TextView) v.findViewById(R.id.tv_distance_qibla_map);
        btnMapView = (ImageView) v.findViewById(R.id.btn_change_view);

        ivQiblaLoc = (ImageView) v.findViewById(R.id.iv_map_qibla_loc);
        ivCurrentLoc = (ImageView) v.findViewById(R.id.iv_map_qibla_current_loc);

        layoutSettingEnable = (LinearLayout) v.findViewById(R.id.layout_location_error);
        layoutSettingEnable.setVisibility(View.GONE);

        layoutSettingEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                providerAlertMessage();

            }
        });

        ivCurrentLoc.setOnClickListener(this);
        ivQiblaLoc.setOnClickListener(this);


        btnMapView.setOnClickListener(mapChangeClickListner);

        tvDistance.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoB);

        SupportMapFragment mapFragment = new SupportMapFragment();
        getChildFragmentManager().beginTransaction().add(R.id.frame_qibla, mapFragment).commit();
        mapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_map_qibla_current_loc:
                onCurrentLocationClick();
                break;

            case R.id.iv_map_qibla_loc:
                onQiblaLocationClick();
                break;
            default:

                break;
        }
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled || isNetworkEnabled) {
            return true;
        } else {
            return false;
        }
    }


    private void onCurrentLocationClick() {

        animateMapView(MAX_ZOOM_MAP, 2000, currentLocation);
    }

    private void onQiblaLocationClick() {

        if (!isQiblaZoom) {
            isQiblaZoom = true;

            animateMapView(MIN_ZOOM_MAP, 2000, qiblaLocation);

        } else {

            isQiblaZoom = false;

            animateMapView(MAX_ZOOM_MAP, 2000, currentLocation);
        }


        isAnimating = true;
    }

    @Override
    public void onResume() {
        super.onResume();


        if (accelerometer == null && magnetometer == null) {
//            tvWarning.setText(mContext.getResources().getString(R.string.cannot) + " " + mContext.getResources().getString(R.string.find_qibla_direction) + "\n" + mContext.getResources().getString(R.string.your_device_not_compatible));
//            tvWarning.setVisibility(View.VISIBLE);
        } else if (accelerometer == null) {
//            tvWarning.setText(mContext.getResources().getString(R.string.cannot) + " " + mContext.getResources().getString(R.string.find_qibla_direction) + "\n" + mContext.getResources().getString(R.string.your_device_not_compatible));
//            tvWarning.setVisibility(View.VISIBLE);

        } else if (magnetometer == null) {
//            tvWarning.setText(mContext.getResources().getString(R.string.cannot) + " " + mContext.getResources().getString(R.string.find_qibla_direction) + "\n" + mContext.getResources().getString(R.string.your_device_not_compatible));
//            tvWarning.setVisibility(View.VISIBLE);
        } else {
            mSensorManager.unregisterListener(mMagAccel);
            mSensorManager.registerListener(mMagAccel, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(mMagAccel, magnetometer, SensorManager.SENSOR_DELAY_GAME);

            if (isLocationEnabled()) {
                layoutSettingEnable.setVisibility(View.GONE);
            } else {
                layoutSettingEnable.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        mSensorManager.unregisterListener(mMagAccel);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.getUiSettings().setCompassEnabled(false);

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        btnMapView.setImageResource(R.drawable.ic_map_img_1);
        // mMap.getUiSettings().setZoomControlsEnabled(false);
        //mMap.getUiSettings().setZoomGesturesEnabled(false);

        // Add a marker in Sydney and move the camera
        currentLat = Double.parseDouble(locationPref.getLatitudeCurrent());
        currentLng = Double.parseDouble(locationPref.getLongitudeCurrent());
        currentLocation = new LatLng(currentLat, currentLng);

        String calcDistance = locationPref.getDistance();
        tvDistance.setText(getResources().getString(R.string.distance_from_qibla) + " " + calcDistance + " KM");


        qiblaLocation = new LatLng(makkahLatitude, makkahLongitude);
        // mMap.setOnCameraChangeListener(this);

        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
        mMap.addMarker(new MarkerOptions().position(qiblaLocation).title("Qibla").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_kaaba_map)));
        //  mMap.addMarker(new MarkerOptions().position(qiblaLocation).title("Qibla").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_kaaba_map)));


        mMap.addPolyline(new PolylineOptions().geodesic(true)
                .width(POLYLINE_WIDTH)
                .add(new LatLng(currentLat, currentLng))
                .add(new LatLng(makkahLatitude, makkahLongitude))  // Makkah Qibla Location
        );

        animateMapView(MAX_ZOOM_MAP, 2000, currentLocation);
    }

    private void animateMapView(final float zoom, final int duration, final LatLng latLng) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        latLng, zoom), duration, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {

                        isAnimating = false;
                        ivQiblaLoc.setVisibility(View.VISIBLE);
                        ivCurrentLoc.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancel() {

                        isAnimating = false;
                    }
                });
            }
        });

        isAnimating = true;
        ivQiblaLoc.setVisibility(View.GONE);
        ivCurrentLoc.setVisibility(View.GONE);
    }


//    private float getMaximumTilt(float zoom) {
//        // for tilt values, see:
//        // https://developers.google.com/maps/documentation/android/reference/com/google/android/gms/maps/model/CameraPosition.Builder?hl=fr
//
//        float tilt = 30.0f;
//
//        if (zoom > 15.5f) {
//            tilt = 67.5f;
//        } else if (zoom >= 14.0f) {
//            tilt = (((zoom - 14.0f) / 1.5f) * (67.5f - 45.0f)) + 45.0f;
//        } else if (zoom >= 10.0f) {
//            tilt = (((zoom - 10.0f) / 4.0f) * (45.0f - 30.0f)) + 30.0f;
//        }
//
//        return tilt;
//    }

    @Override
    public void onRotationUpdate(float[] newMatrix) {
        // TODO Auto-generated method stub

        if (!isAnimating) {
            double azimut = 0;
            float orientation[] = new float[3];

            SensorManager.getOrientation(newMatrix, orientation);

            azimut = orientation[0]; // orientation contains: azimut, pitch and roll

            azimut = azimut * 360 / (2 * 3.14159f);

            float degree = Math.round(azimut);
            updateCamera(degree);
        }
    }

//    @Override
//    public void onCameraChange(CameraPosition position) {
//        float maxZoom = 17.0f;
//        float minZoom = 10.0f;
//        if (position.zoom > maxZoom) {
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom));
//        } else if (position.zoom < minZoom) {
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(minZoom));
//        }
//    }

    private void updateCamera(float bearing) {
        if (mMap != null) {
            CameraPosition oldPos = mMap.getCameraPosition();

            CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();

            float diff = pos.bearing - oldPos.bearing;

            if (diff > SENSOR_CALLBACK_DELAY || diff < -SENSOR_CALLBACK_DELAY) {
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
            }
        }
    }

    View.OnClickListener mapChangeClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (mapViewIndex == 0) { // MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                btnMapView.setImageResource(R.drawable.ic_map_img_2);
                mapViewIndex++;
            } else if (mapViewIndex == 1) { // MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                btnMapView.setImageResource(R.drawable.ic_map_img_3);
                mapViewIndex++;
            } else { // MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                btnMapView.setImageResource(R.drawable.ic_map_img_1);
                mapViewIndex = 0;
            }
        }
    };


    private void providerAlertMessage() {

        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
        CompassFragmentIndex.LOCATION_REQUEST_DELAY = LOCATION_SETTINGS_DELAY;


//        AlertDialog alertProvider = null;
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setTitle(getResources().getString(R.string.unable_to_find_location));
//        builder.setMessage(getResources().getString(R.string.enable_provider));
//        builder.setCancelable(false);
//
//        builder.setPositiveButton(getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(settingsIntent);
//                CompassFragmentIndex.LOCATION_REQUEST_DELAY = LOCATION_SETTINGS_DELAY;
//            }
//        });
//
//        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//            }
//        });
//
//        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//
//            @Override
//            public void onCancel(DialogInterface dialog) {
//            }
//        });
//
//        builder.setOnKeyListener(new Dialog.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
//                // TODO Auto-generated method stub
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        alertProvider = builder.create();
//        alertProvider.show();

    }


    private class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            try {

                currentLat = Double.parseDouble(locationPref.getLatitudeCurrent());
                currentLng = Double.parseDouble(locationPref.getLongitudeCurrent());
                currentLocation = new LatLng(currentLat, currentLng);

                if (currentLat != 0 && currentLat != -2 && currentLng != 0 && currentLng != -2) {

                } else {
                    SupportMapFragment mapFragment = new SupportMapFragment();
                    getChildFragmentManager().beginTransaction().add(R.id.frame_qibla, mapFragment).commit();
                    mapFragment.getMapAsync(CompassMapsFragment.this);
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
