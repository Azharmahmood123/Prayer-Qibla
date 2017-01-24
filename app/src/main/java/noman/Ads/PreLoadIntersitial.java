package noman.Ads;/*
   Created by Noman and  Email = nomanarif.cdz@gmail.com on 7/21/2016.
*/

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.quranreading.qibladirection.R;


public class PreLoadIntersitial {
    // Static fields are shared between all instances.
    static InterstitialAd interstitialAd;
    static Context context;
    private final int timeSeconds = 5000;//sec to repeat the add request

    public PreLoadIntersitial(Context context) {
        this.context = context;
        createAd(context);
    }

    public void createAd(Context context) {
        // Create an ad.
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getResources().getString(R.string.admob_interstitial));

        final AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        // Load the interstitial ad.
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Log.e("Refresh-Inersitail", "ads");
                        interstitialAd.loadAd(adRequest);
                        handler.postDelayed(this, timeSeconds);
                    }
                }, timeSeconds);
                //    interstitialAd.loadAd(adRequest);
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

            }
        });
    }

    public InterstitialAd getAd() {
        return interstitialAd;
    }


    //Call method for preload intersiition

   /* InterstitialAd ad = admanager.getAd();
    if (ad.isLoaded) {
        ad.show();
    }*/
}