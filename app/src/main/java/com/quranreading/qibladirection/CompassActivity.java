package com.quranreading.qibladirection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.fragments.CompassInnerFragment;
import com.quranreading.fragments.CompassMapsFragment;

/**
 * Created by cyber on 12/5/2016.
 */

public class CompassActivity extends AppCompatActivity {

    public static final String EXTRA_IS_SHOW_MAP = "show_map";

    // google ads
    AdView adview;
    ImageView adImage;
    private static final String LOG_TAG = "Ads";
    private final Handler adsHandler = new Handler();
    private int timerValue = 3000, networkRefreshTime = 10000;

    FragmentManager fm;
    FragmentTransaction ft;
    boolean isMapView = false;
    CompassInnerFragment compassInnerFragment;
    CompassMapsFragment compassMapsFragment;
    FrameLayout frameMap, frameCompass;
    ImageView btnViewNavigation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qibla);

        initializeAds();


        frameMap = (FrameLayout) findViewById(R.id.frame_map);
        frameCompass = (FrameLayout) findViewById(R.id.frame_compass);
        btnViewNavigation = (ImageView) findViewById(R.id.btn_compass_view_navigation);

        isMapView = getIntent().getBooleanExtra(EXTRA_IS_SHOW_MAP, false);


        if (isMapView) {
            frameCompass.setVisibility(View.GONE);
            frameMap.setVisibility(View.VISIBLE);
            btnViewNavigation.setImageResource(R.drawable.ic_compass_mapview);
        } else {
            frameCompass.setVisibility(View.VISIBLE);
            frameMap.setVisibility(View.GONE);
            btnViewNavigation.setImageResource(R.drawable.ic_compass_compassview);
        }

        LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompassActivity.super.onBackPressed();

            }
        });


//        tvHeading.setText(R.string.languages);

        RelativeLayout layout_image_qibla = (RelativeLayout) findViewById(R.id.layout_image_qibla);
        layout_image_qibla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMapView) {
                    frameCompass.setVisibility(View.VISIBLE);
                    frameMap.setVisibility(View.GONE);
                    isMapView = false;
                    btnViewNavigation.setImageResource(R.drawable.ic_compass_mapview);

                } else {
                    //  if (isNetworkConnected()) {
                    frameCompass.setVisibility(View.GONE);
                    frameMap.setVisibility(View.VISIBLE);
                    isMapView = true;
                    btnViewNavigation.setImageResource(R.drawable.ic_compass_compassview);
                    //      } else {
                    //          ToastClass.showShortToast(CompassActivity.this, getString(R.string.toast_network_error), 500, Gravity.CENTER);
                    //     }
                }
            }
        });


        if (savedInstanceState == null) {

            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            compassMapsFragment = new CompassMapsFragment();
            ft.add(R.id.frame_map, compassMapsFragment).commit();

            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            compassInnerFragment = new CompassInnerFragment();
            ft.add(R.id.frame_compass, compassInnerFragment).commit();
        }

        if (isMapView) {
            frameCompass.setVisibility(View.GONE);
            frameMap.setVisibility(View.VISIBLE);
            btnViewNavigation.setImageResource(R.drawable.ic_compass_compassview);
        }
        else
        {
            btnViewNavigation.setImageResource(R.drawable.ic_compass_mapview);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initializeAds() {

        adview = (AdView) findViewById(R.id.adView);
        adImage = (ImageView) findViewById(R.id.adimg);
        adImage.setVisibility(View.GONE);
        adview.setVisibility(View.GONE);

        if (isNetworkConnected()) {
            this.adview.setVisibility(View.VISIBLE);
        } else {
            this.adview.setVisibility(View.GONE);
        }
        setAdsListener();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated methodIndex stub
        super.onResume();

        if (((GlobalClass) getApplication()).isPurchase) {
            adImage.setVisibility(View.GONE);
            adview.setVisibility(View.GONE);
        } else {
            startAdsCall();
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated methodIndex stub
        super.onPause();

        if (!((GlobalClass) getApplication()).isPurchase) {
            stopAdsCall();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated methodIndex stub
        super.onDestroy();

        if (!((GlobalClass) getApplication()).isPurchase) {
            destroyAds();
        }
    }

    ///////////////////////////
    //////////////////////////
    //////////////////////////
    public void onClickAdImage(View view) {

    }

    private boolean isNetworkConnected() {
        ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
    }

    private Runnable sendUpdatesAdsToUI = new Runnable() {
        public void run() {
            Log.v(LOG_TAG, "Recall");
            updateUIAds();
        }
    };

    private final void updateUIAds() {
        if (isNetworkConnected()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adview.loadAd(adRequest);
        } else {
            timerValue = networkRefreshTime;
            adsHandler.removeCallbacks(sendUpdatesAdsToUI);
            adsHandler.postDelayed(sendUpdatesAdsToUI, timerValue);
        }
    }

    public void startAdsCall() {
        Log.i(LOG_TAG, "Starts");
        if (isNetworkConnected()) {
            this.adview.setVisibility(View.VISIBLE);
        } else {
            this.adview.setVisibility(View.GONE);
        }

        adview.resume();
        adsHandler.removeCallbacks(sendUpdatesAdsToUI);
        adsHandler.postDelayed(sendUpdatesAdsToUI, 0);
    }

    public void stopAdsCall() {
        Log.e(LOG_TAG, "Ends");
        adsHandler.removeCallbacks(sendUpdatesAdsToUI);
        adview.pause();
    }

    public void destroyAds() {
        Log.e(LOG_TAG, "Destroy");
        adview.destroy();
        adview = null;
    }

    private void setAdsListener() {
        adview.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(LOG_TAG, "onAdClosed");
            }

            @Override
            public void onAdFailedToLoad(int error) {
                String message = "onAdFailedToLoad: " + getErrorReason(error);
                Log.d(LOG_TAG, message);
                adview.setVisibility(View.GONE);
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(LOG_TAG, "onAdLeftApplication");
            }

            @Override
            public void onAdOpened() {
                Log.d(LOG_TAG, "onAdOpened");
            }

            @Override
            public void onAdLoaded() {
                Log.d(LOG_TAG, "onAdLoaded");
                adview.setVisibility(View.VISIBLE);

            }
        });
    }

    private String getErrorReason(int errorCode) {
        String errorReason = "";
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No fill";
                break;
        }
        return errorReason;
    }
}
