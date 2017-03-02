package noman.Ads;/*
   Created by Noman and  Email = nomanarif.cdz@gmail.com on 6/24/2016.
*/

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.quranreading.qibladirection.R;


public class AdIntegration2 extends AppCompatActivity {
    AdView mAdView;
    LinearLayout adLayout;
    private final int timeSeconds = 20000;//sec to repeat the add request

    public AdIntegration2() {
        super();
    }

    public void showBannerAd(Context context, LinearLayout adLayout) {

        this.adLayout = adLayout;
        mAdView = new AdView(context);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(context.getResources().getString(R.string.admob_id));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.adLayout.addView(mAdView, params);
        adsDisplay();


    }

    public void adsDisplay() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
              //  Log.e("Refresh", "ads");
                AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build();
                mAdView.loadAd(adRequest);
                handler.postDelayed(this, timeSeconds);
            }
        }, timeSeconds);


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdClosed() {
                adLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                adLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAdLeftApplication() {
                adLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                adLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (mAdView != null) {
            mAdView.resume();
        }
        super.onResume();
    }



}
