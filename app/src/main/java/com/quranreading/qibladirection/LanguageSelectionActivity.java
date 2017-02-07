package com.quranreading.qibladirection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.adapter.LanguagesListAdapter;
import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.helper.DailogsClass;
import com.quranreading.listeners.OnDailogButtonSelectionListner;
import com.quranreading.sharedPreference.LanguagePref;

import noman.quran.JuzConstant;

public class LanguageSelectionActivity extends AppCompatActivity implements OnItemClickListener, OnDailogButtonSelectionListner {

    // google ads
    AdView adview;
    ImageView adImage;
    private static final String LOG_TAG = "Ads";
    private final Handler adsHandler = new Handler();
    private int timerValue = 3000, networkRefreshTime = 10000;
    Context context = this;

    ListView listView;
    LanguagesListAdapter adapter;
    String[] languagesData;
    GlobalClass mGlobalClass;
    int selectedPosition;
    LanguagePref mLanguagePref;
    boolean inProccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated methodIndex stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);

        LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguageSelectionActivity.super.onBackPressed();

            }
        });

        // Ads
        initializeAds();

        mLanguagePref = new LanguagePref(context);
        mGlobalClass = (GlobalClass) getApplication();
        listView = (ListView) findViewById(R.id.listview_language);

        languagesData = getResources().getStringArray(R.array.language_array);
        adapter = new LanguagesListAdapter(context, languagesData);

        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
        sendAnalyticsData("Language Change Screen");
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

    public void onBackButtonClick(View v) {
        onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//
//        if (!((GlobalClass) getApplication()).isPurchase && position > 0) {
//            if (!inProccess) {
//                inProccess = true;
//                startActivity(new Intent(context, UpgradeActivity.class));
//            }
//        } else if (mGlobalClass.languagePref.getLanguage() != position) {
//            if (!inProccess) {
//                inProccess = true;
//                selectedPosition = position;
//                DailogsClass dailogShow = new DailogsClass(context, getResources().getString(R.string.languages), getResources().getString(R.string.laguage_alert_msg), this, getResources().getString(R.string.okay), getResources().getString(R.string.cancel));
//                dailogShow.showTwoButtonDialog();
//            }
//        }
        if(mGlobalClass.languagePref.getLanguage() != position) {
            if (!inProccess) {
                inProccess = true;
                selectedPosition = position;
               // DailogsClass dailogShow = new DailogsClass(context, getResources().getString(R.string.languages), getResources().getString(R.string.laguage_alert_dialog), this, getResources().getString(R.string.okay), getResources().getString(R.string.cancel));
              //  dailogShow.showTwoButtonDialog();

                new AlertDialog.Builder(LanguageSelectionActivity.this, R.style.MyAlertDialogStyle)
                        .setTitle(getResources().getString(R.string.languages))
                        .setMessage(getResources().getString(R.string.laguage_alert_dialog))
                        .setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                inProccess = false;


                                    sendAnalyticEvent(languagesData[selectedPosition]);
                                    mGlobalClass.setLocale(selectedPosition);

                                    if (!mLanguagePref.getFirstTimeLanguage()) {
                                        mLanguagePref.setFirstTimeLanguage(true);
                                    }
                                    Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//Remove all previeous activity
                                    startActivity(intent);
                                }

                        }).setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

            }
        }
    }

    @Override
    public void onDailogButtonSelectionListner(String title, int selectedIndex, boolean selection) {
        // TODO Auto-generated methodIndex stub





       /* inProccess = false;
        if (selection) {
            sendAnalyticEvent(languagesData[selectedPosition]);
            mGlobalClass.setLocale(selectedPosition);

            if (!mLanguagePref.getFirstTimeLanguage()) {
                mLanguagePref.setFirstTimeLanguage(true);
            }

            MainActivityNew.finishActivity();
            finish();
            startActivity(new Intent(context, MainActivityNew.class));

            //MainActivityNew.finishActivity();
            Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//Remove all previeous activity
            startActivity(intent);
        }*/
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated methodIndex stub
        super.onResume();
        inProccess = false;

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

    private void sendAnalyticsData(String name) {
        AnalyticSingaltonClass.getInstance(this).sendScreenAnalytics(name);
    }

    private void sendAnalyticEvent(String eventAction) {
        AnalyticSingaltonClass.getInstance(this).sendEventAnalytics("Language Selection", eventAction);
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
