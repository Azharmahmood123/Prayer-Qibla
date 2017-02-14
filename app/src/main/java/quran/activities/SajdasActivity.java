package quran.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.MainActivityNew;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;

import quran.adapter.SajdasListAdapter;
import quran.model.BookmarksListModel;

public class SajdasActivity extends AppCompatActivity {

    // google ads
    AdView adview;
    ImageView adImage;
    private static final String LOG_TAG = "Ads";
    private final Handler adsHandler = new Handler();
    private int timerValue = 3000, networkRefreshTime = 10000;

    TextView tvHeader, tvName;
    ListView bookmarksListView;
    Boolean setListener = false, checkAsian = false;
    int resrcTimeArray;
    String[] engNamesData = null;
    ArrayList<BookmarksListModel> bookmarksList = new ArrayList<BookmarksListModel>();
    SajdasListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO Auto-generated method stub
        setContentView(R.layout.activity_bookmarks);
        initializeAds();

        tvHeader = (TextView) findViewById(R.id.tv_header);
        bookmarksListView = (ListView) findViewById(R.id.listView);

        tvHeader.setTypeface(((GlobalClass) getApplication()).faceRobotoR);

        bookmarksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                int surahNo = bookmarksList.get(position).getsurahNo();
                int ayahNo = bookmarksList.get(position).getAyahNo();

                Intent end_actvty = new Intent(SajdasActivity.this, SurahActivity.class);
                end_actvty.putExtra(SurahActivity.KEY_EXTRA_SURAH_NO, surahNo);
                // if(surahNo == 9 || surahNo == 1)
                // ayahNo = ayahNo - 1;
                end_actvty.putExtra(SurahActivity.KEY_EXTRA_AYAH_NO, ayahNo);
                startActivity(end_actvty);

            }
        });

        initializeSajdahsList();
        sendAnalyticsData("Sajdahs Screen");

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

    private void showInterstitialAd() {
        // if(!((GlobalClass) getApplication()).isPurchase)
        // {
        // InterstitialAdSingleton mInterstitialAdSingleton = InterstitialAdSingleton.getInstance(this);
        // mInterstitialAdSingleton.showInterstitial();
        // }
    }

    private void sendAnalyticsData(String screen) {
        AnalyticSingaltonClass.getInstance(this).sendScreenAnalytics(screen);
    }

    public void initializeSajdahsList() {
        tvHeader.setText(getResources().getString(R.string.txt_sajdahs));
        String surahName;
        int id, surahNo, ayahNo;

        String[] surahNamesArr = {"Al-A'raf", "Ar-Ra'd", "An-Nahl", "Al-Israa", "Maryam", "Al-Hajj", "Al-Hajj (As Shafee - Optional)", "Al-Furqan", "An-Naml", "As-Sajdah", "Saad (Hanafi)", "Ha Mim/Fussilat", "An-Najm", "Al-Inshiqaq", "Al-'Alaq"};
        int[] surahNoArr = {7, 13, 16, 17, 19, 22, 22, 25, 27, 32, 38, 41, 53, 84, 96};
        int[] ayahNoArr = {206, 15, 50, 109, 58, 18, 77, 60, 26, 15, 24, 38, 62, 21, 19};

        for (int pos = 0; pos < surahNoArr.length; pos++) {
            id = pos + 1;
            surahName = surahNamesArr[pos];
            surahNo = surahNoArr[pos];
            ayahNo = ayahNoArr[pos];

            BookmarksListModel model = new BookmarksListModel(id, surahName, surahNo, ayahNo);
            bookmarksList.add(model);
        }

        adapter = new SajdasListAdapter(this, bookmarksList);
        bookmarksListView.setAdapter(adapter);
    }

    public void showToast(String msg) {
        Toast toast = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (!((GlobalClass) getApplication()).isPurchase) {
            startAdsCall();
        } else {
            adImage.setVisibility(View.GONE);
            adview.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (!((GlobalClass) getApplication()).isPurchase) {
            stopAdsCall();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (!((GlobalClass) getApplication()).isPurchase) {
            destroyAds();
        }
    }

    public void onBackButtonClick(View v) {
        onBackPressed();
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
