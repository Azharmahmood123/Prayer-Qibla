package com.quranreading.qibladirection;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.helper.Constants;

public class AboutInstructionActivity extends AppCompatActivity {
    // google ads
    AdView adview;
    ImageView adImage;
    private static final String LOG_TAG = "Ads";
    private final Handler adsHandler = new Handler();
    private int timerValue = 3000, networkRefreshTime = 10000;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String from = getIntent().getStringExtra(MainActivityNew.ACTIVITY_SELECTION);

        if (from.equals(MainActivityNew.ACTIVITY_INSTRUCTION)) {
            setContentView(R.layout.activity_instruction);

            setFontsForInstruction();

        } else if (from.equals(MainActivityNew.ACTIVITY_ABOUT)) {
            if (((GlobalClass) getApplication()).deviceS3) {
                // Samsung S3 && s4 etc.
                setContentView(R.layout.about_layout_s3);
            } else {
                setContentView(R.layout.about_layout);
            }

            setFontsForAbout();
        }

        sendAnalyticsData();

        initializeAds();
    }

    public void onBackButtonClick(View v) {
        onBackPressed();
    }

    private void setFontsForInstruction() {
        // TODO Auto-generated methodIndex stub

        TextView tvHeader, tvHeading, tvShake, tvDevice, tvMetalic;

        tvHeader = (TextView) findViewById(R.id.tv_instruction_head);
        tvHeading = (TextView) findViewById(R.id.tvInstruct_heading);
        tvDevice = (TextView) findViewById(R.id.tvDevice);
        tvShake = (TextView) findViewById(R.id.tvShake);
        tvMetalic = (TextView) findViewById(R.id.tvMetalic);

        tvHeader.setTypeface(((GlobalClass) getApplication()).faceRobotoR);
        tvHeading.setTypeface(((GlobalClass) getApplication()).faceRobotoR);
        tvDevice.setTypeface(((GlobalClass) getApplication()).faceRobotoL);
        tvShake.setTypeface(((GlobalClass) getApplication()).faceRobotoL);
        tvMetalic.setTypeface(((GlobalClass) getApplication()).faceRobotoL);

        // TextView tvHeading;
        // TextView[] tvInstructions = new TextView[13];
        // tvHeading = (TextView) findViewById(R.id.tv_instruction_head);
        //
        // tvInstructions[0] = (TextView) findViewById(R.id.tvInstruction1);
        // tvInstructions[1] = (TextView) findViewById(R.id.tvInstruction2);
        // tvInstructions[2] = (TextView) findViewById(R.id.tvInstruction2_1);
        // tvInstructions[3] = (TextView) findViewById(R.id.tvInstruction2_2);
        // tvInstructions[4] = (TextView) findViewById(R.id.tvInstruction2_3);
        // tvInstructions[5] = (TextView) findViewById(R.id.tvInstruction2_4);
        // tvInstructions[6] = (TextView) findViewById(R.id.tvInstruction2_5);
        // tvInstructions[7] = (TextView) findViewById(R.id.tvInstruction2_6);
        // tvInstructions[8] = (TextView) findViewById(R.id.tvInstruction2_7);
        // tvInstructions[12] = (TextView) findViewById(R.id.tvInstruction2_8);
        //
        // tvInstructions[9] = (TextView) findViewById(R.id.tvInstruction3);
        // tvInstructions[10] = (TextView) findViewById(R.id.tvInstruction4);
        // tvInstructions[11] = (TextView) findViewById(R.id.tvInstruction5);
        //
        // tvHeading.setTypeface(((GlobalClass) getApplication()).faceRobotoB);
        // for (int index = 0; index < tvInstructions.length; index++)
        // {
        //
        // tvInstructions[index].setTypeface(((GlobalClass) getApplication()).faceRobotoR);
        // }
    }

    private void setFontsForAbout() {
        // TODO Auto-generated methodIndex stub

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView tvHeading = (TextView) findViewById(R.id.tv_main_heading);
        tvHeading.setTypeface(((GlobalClass) getApplication()).faceRobotoR);
        tvHeading.setText(R.string.about_us);

        TextView tvAppName, tvWebLink, tvCopyRight, tvVersion, tvReservedRights;
        tvAppName = (TextView) findViewById(R.id.tv_app_name);
        tvWebLink = (TextView) findViewById(R.id.tv_link);
        tvCopyRight = (TextView) findViewById(R.id.tv_copy_right);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvReservedRights = (TextView) findViewById(R.id.tv_rights);

        tvAppName.setTypeface(((GlobalClass) getApplication()).faceRobotoL);
        tvWebLink.setTypeface(((GlobalClass) getApplication()).faceRobotoL);
        tvCopyRight.setTypeface(((GlobalClass) getApplication()).faceRobotoL);
        tvVersion.setTypeface(((GlobalClass) getApplication()).faceRobotoB);
        tvReservedRights.setTypeface(((GlobalClass) getApplication()).faceRobotoL);

        tvCopyRight.setText(getResources().getString(R.string.copyright) + getString(R.string.copyright_year) + "\n" + getString(R.string.app_name));
        tvVersion.setText(getResources().getString(R.string.version) + Constants.VERSION_NUMBER);
        tvReservedRights.setText(Constants.WEB_URL + " \n" + getResources().getString(R.string.all_rights_reserved));
    }

    private void sendAnalyticsData() {
        AnalyticSingaltonClass.getInstance(context).sendScreenAnalytics("About/App Instructions Screen");
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

    public void onClickVisitSite(View v) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.quranreading.com/?utm_source=QiblaApp&utm_medium=Android&utm_campaign=QiblaApp"));
        startActivity(intent);
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