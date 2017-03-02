package com.quranreading.qibladirection;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import com.quranreading.helper.DBManager;
import com.quranreading.helper.MyService;
import com.quranreading.qibladirection.util.IabHelper;
import com.quranreading.qibladirection.util.IabResult;
import com.quranreading.qibladirection.util.Inventory;
import com.quranreading.qibladirection.util.Purchase;
import com.quranreading.sharedPreference.QiblaDirectionPref;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import noman.Ads.PreLoadIntersitial;
import noman.CommunityGlobalClass;
import noman.Tasbeeh.activity.TasbeehListActivity;
import noman.quran.dbconnection.DataBaseHelper;
import quran.activities.ServiceClass;
import quran.helper.DBManagerQuran;
import quran.helper.FileUtils;
import noman.sharedpreference.SurahsSharedPref;

public class SplashActivity extends AppCompatActivity {

    InterstitialAd mInterstitialAd;
    Context context = this;

    boolean isShowInterstitial = true;

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            startNextActivity();
        }
    };

    private int SPLASH_TIME;
    /*private final int SPLASH_TIME_LONG = 5000;
    private final int SPLASH_TIME_SHORT = 500;*/
    private final int SPLASH_TIME_LONG = 5000;
    private final int SPLASH_TIME_SHORT = 1500;
    QiblaDirectionPref mQiblaDirectionPref;

    private Handler myHandler = new Handler();
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);
        }

        isAppPurchase();

        //Load Default Ad here
        CommunityGlobalClass.getInstance().mInterstitialAd = new PreLoadIntersitial(this);
        CommunityGlobalClass.mMainActivityNew = this;


        //Copy JuzzData from Database
        new copyJuzDatabase().execute();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int height = dm.heightPixels;
        int width = dm.widthPixels;

	/*	if((width == 720 && height == 1280) || (width >= 1080 && height <= 2560) || (width == 540 && height == 960))
        {
			// Samsung S3 && s4
			((GlobalClass) getApplication()).deviceS3 = true;
			setContentView(R.layout.activity_splash_s3);
		}
		else
		{
			((GlobalClass) getApplication()).deviceS3 = false;
			setContentView(R.layout.activity_splash);
		}*/
        setContentView(R.layout.splash_animation);
        needleAnimation();


        index = 0;
        mQiblaDirectionPref = new QiblaDirectionPref(this);

        if (mQiblaDirectionPref.chkDbVersion() > DBManager.DATABASE_VERSION) {
            SPLASH_TIME = SPLASH_TIME_SHORT;
        } else {
            mQiblaDirectionPref.setDatabaseCopied(false);
            SPLASH_TIME = SPLASH_TIME_LONG;
        }

        if (mQiblaDirectionPref.getInterstitialCount() == 0) {
            //	mQiblaDirectionPref.setInterstitialCount(1);
            myHandler.postDelayed(mRunnable, SPLASH_TIME);
        } else {
            mQiblaDirectionPref.setInterstitialCount(0);
            if (!isNetworkConnected() || ((GlobalClass) getApplication()).isPurchase) {
                myHandler.postDelayed(mRunnable, SPLASH_TIME);
            } else {
                //		showInterstitial();
                myHandler.postDelayed(mRunnable, SPLASH_TIME_LONG);
            }
        }
        chkDownloadStatus();

        startAsyncTask();


    }




    public void showTextAnimation() {
      /*  HTextView hTextView = (HTextView) findViewById(R.id.text);
        hTextView.setTypeface(((GlobalClass) getApplicationContext()).faceRobotoR);
        hTextView.setAnimateType(HTextViewType.EVAPORATE);
        hTextView.animateText(getString(R.string.app_name)); // animate
*/
        /*chkDownloadStatus();
        startAsyncTask();
*/
    }

    public void needleAnimation() {
        final ImageView view = (ImageView) findViewById(R.id.img_rotate_needle);
        RotateAnimation aRotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        aRotate.setStartOffset(0);
        aRotate.setDuration(1400);
        aRotate.setFillAfter(true);
        aRotate.setInterpolator(this, android.R.anim.decelerate_interpolator);

        view.startAnimation(aRotate);
        aRotate.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                showTextAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {


                view.setImageResource(R.drawable.ic_kaba_neddle);
               /* chkDownloadStatus();
                startAsyncTask();*/
            }
        });

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated methodIndex stub
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated methodIndex stub
        super.onResume();

        if (index > 0) {
            startNextActivity();
        } else {
            index++;
        }

        isShowInterstitial = true;

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated methodIndex stub
        super.onPause();

        isShowInterstitial = false;
        myHandler.removeCallbacks(mRunnable);
    }

    // //////////////////////// Async Function to execute long process ////////////////

    private void startAsyncTask() {

        Intent intent = new Intent(context, MyService.class);
        startService(intent);
    }

    private void chkDownloadStatus() {

        SurahsSharedPref pref = new SurahsSharedPref(SplashActivity.this);

        int reciter = pref.getReciter();
        FileUtils.checkCompleteAudioFile(SplashActivity.this, reciter);

        DBManagerQuran dbObj = new DBManagerQuran(SplashActivity.this);
        dbObj.open();

        Cursor c = dbObj.getAllDownloads();

        if (c != null) {
            if (c.moveToFirst()) {
                if (!((GlobalClass) getApplication()).isServiceRunning()) {
                    Intent serviceIntent = new Intent(SplashActivity.this, ServiceClass.class);
                    startService(serviceIntent);
                }
            }

            c.close();
        }
        dbObj.close();
    }

    /////////////////
    ///////////////
    /////////////
    private boolean isNetworkConnected() {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
    }

    /*private void showInterstitial() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                startNextActivity();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if(isShowInterstitial)
                {
                    myHandler.removeCallbacks(mRunnable);
                    mInterstitialAd.show();
                }
            }
        });

        requestNewInterstitial();
    }
*/
    private void startNextActivity() {
        isShowInterstitial = false;
        myHandler.removeCallbacks(mRunnable);

        Intent intent = new Intent(context, TasbeehListActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated methodIndex stub

        if (mInterstitialAd != null)
            mInterstitialAd.setAdListener(null);
        mInterstitialAd = null;
        super.onDestroy();

    }

    /**
     * Async Task to make http call
     */
    private class copyJuzDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            copyDataBaseFromAssets();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

    private void copyDataBaseFromAssets() {
        //Copying DB
        DataBaseHelper helper = new DataBaseHelper(this);
        try {
            helper.createDataBase();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void isAppPurchase()
    {
        final IabHelper mHelper = new IabHelper(this, getValue());

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if(!result.isSuccess())
                {
                    return;
                }
                if(mHelper == null)
                    return;

                Log.e("Billing", "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

    }

    private String getValue() {
        String keyValue = "";

        try
        {
            String key = "NinSolIslamicKey";

            // String s = getString(R.string.key);
            // byte[] ciphertext = encrypt(key, s);

            byte[] theByteArray = { 29, -39, -49, 44, 58, 28, -121, -70, 94, 70, -3, 34, -50, -14, 29, -95, -15, 125, 90, -58, 16, -18, -17, -118, 16, 83, 99, 7, -21, 59, 57, -101, -30, -33, 42, 16, -86, 2, 125, 96, -118, 96, -94, -45, 40, 89, -71, -124, 89, -90, -8, 60, -52, 5, 66, -93, -111, 74,
                    47, -59, -87, 72, -26, -4, 31, 11, 90, -80, -5, 70, -68, 81, -100, -40, 117, 47, 91, 37, -37, -23, -34, 70, 114, 100, -128, 95, 28, -28, -86, 96, 58, -10, -99, -29, 43, -3, -78, -25, -57, -77, 4, 70, 120, -16, 17, -8, 4, 42, 66, -73, 1, 11, -66, -39, 44, -56, -119, -50, -21, 87,
                    -79, 104, -36, -8, 66, -107, -40, 14, -6, 103, 83, -60, 75, -18, -72, -35, 122, -47, 25, 89, -109, -82, 35, 76, 87, -10, 110, -114, -15, 41, -110, -71, 37, 99, 94, -87, -64, -29, 117, -66, -51, -25, -90, -56, -115, 102, 100, -61, -52, 80, -90, 127, 119, -15, -64, -45, -1, 78, 21,
                    119, -7, 87, 126, -3, -86, -102, -27, -92, 1, -48, 42, -10, -125, 4, -1, 17, -46, -7, 36, -100, -53, 51, -14, 15, 24, -100, -56, 93, -6, 79, 5, 84, -27, 127, 117, -16, 71, -62, 39, -41, -124, 93, -4, 29, 37, -107, 5, -14, 36, -51, 9, 54, 121, -53, 26, 116, 36, 3, 112, -18, 86,
                    -8, 47, -27, -91, 12, -33, 30, -110, 99, -59, -98, 57, 77, -96, -10, 34, 87, 80, 30, 104, -127, -16, -21, -17, 124, -9, 91, -4, 1, 13, -73, -26, -66, 18, 76, 119, 89, 107, -80, 93, -47, -89, 13, 59, -17, -19, 100, -80, -68, -128, -23, 40, 96, 58, -88, -10, 97, 78, -33, -45, -37,
                    50, 68, 115, 47, 22, -29, -120, 61, -50, -84, -52, 32, -41, -93, 5, 124, 80, -83, -91, -43, -31, -27, -85, 1, 44, 111, -65, -88, 61, -22, -74, 65, -102, -50, -111, 16, 82, 3, -62, -3, -77, 14, -30, 48, 46, 95, -21, 120, -16, 72, -34, -56, 64, -75, -28, -111, -79, 73, -1, -82, 19,
                    -55, 106, -50, -70, -89, 42, 71, 38, -72, 91, -14, 53, 23, 97, 59, -54, 62, -104, 75, -91, -52, 18, 12, -101, 26, 90, 109, -83, 77, -59, 83, -49, -17, 100, 67, -3, -115 };

            byte[] raw = key.getBytes(Charset.forName("US-ASCII"));
            if(raw.length != 16)
            {
                return keyValue;
            }

            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
            byte[] original = cipher.doFinal(theByteArray);
            keyValue = new String(original, Charset.forName("US-ASCII"));
            return keyValue;
        }
        catch (GeneralSecurityException e)
        {
            e.printStackTrace();
            return keyValue;
        }
    }
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            String SKU_UNLOCK = "sku_unlock";
            Purchase unlockPurchase = inventory.getPurchase(SKU_UNLOCK);
            boolean isUpgrade = (unlockPurchase != null && verifyDeveloperPayload(unlockPurchase));
            Log.e("Billing", "Kalmas " + (isUpgrade ? "Locked" : "Unlocked"));
            if(isUpgrade)
            {
                Log.e("Billing", "Qibla is purchased");
                Toast.makeText(SplashActivity.this,"APP is already purchased",Toast.LENGTH_LONG).show();
                ((GlobalClass) getApplication()).isPurchase = true;
                ((GlobalClass) getApplication()).purchasePref.setPurchased(true);
            }
        }
    };

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }


}
