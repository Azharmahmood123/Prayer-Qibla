package names.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;

import names.adapters.GridViewAdapter;

public class NamesGridActivity extends AppCompatActivity implements OnItemClickListener {

    // google ads
    AdView adview;
    ImageView adImage;
    private static final String LOG_TAG = "Ads";
    private final Handler adsHandler = new Handler();
    private int timerValue = 3000, networkRefreshTime = 10000;

    GridView gridViewNames;
    ArrayList<Integer> namesImages = new ArrayList<Integer>();
    GridViewAdapter mGridViewAdapter;
    public String[] names = new String[]{"Allah", "Ar-Rahman", "Ar-Rahim", "Al-Malik", "Al-Quddus", "As-Salam", "Al-Mu'min", "Al-Muhaymin", "Al-Aziz", "Al-Jabbar", "Al-Mutakabbir", // 10
            "Al-Khaliq", "Al-Bari", "Al-Musawwir", "Al-Ghaffar", "Al-Qahhar", "Al-Wahhab", "Ar-Razzaq", "Al-Fattah", "Al-Aleem", "Al-Qabid", // 20
            "Al-Basit", "Al-Khafid", "Ar-Rafi", "Al-Mu'iz", "Al-Muzil", "As-Sami", "Al-Basir", "Al-Hakam", "Al-Adl", "Al-Latif", "Al-Khabir", "Al-Halim", "Al-Azim", "Al-Ghafoor", "Ash-Shakur", "Al-Ali", "Al-Kabir", "Al-Hafiz", "Al-Muqit", "Al-Hasib", "Aj-Jalil", "Al-Karim", "Ar-Raqib", "Al-Mujib",
            "Al-Wasi", "Al-Hakim", "Al-Wadud", "Al-Majid", "Al-Ba'ith", "Ash-Shahid", // 50
            "Al-Haqq", "Al-Wakil", "Al-Qawee", "Al-Matin", "Al-Walee", "Al-Hamid", "Al-Muhsi", "Al-Mubdi", "Al-Mu'eed", "Al-Muhyee", "Al-Mumeet", "Al-Hayy", "Al-Qayyum", "Al-Wajid", "Al-Majid", "Al-Wahid", "As-Samad", "Al-Qadir", "Al-Muqtadir", "Al-Muqaddim", // 70 67 "Al-Ahad "
            "Al-Mu'akhkhir", "Al-Awwal", "Al-Akhir", "Az-Zahir", "Al-Batin", "Al-Wali", "Al-Muta'ali", "Al-Barr", "At-Tawwab", "Al-Muntaqim", "Al-Afuww", "Ar-Ra'uf", "Malik Al-Mulk", "Zul-l-Jalal wal-Ikram", "Al-Muqsit", "Aj-Jami", "Al-Ghanee", "Al-Mughnee", "Al-Mani", "Ad-Darr", // 90
            "An-Nafi", "An-Nur", "Al-Hadi", "Al-Badi", "Al-Baqi", "Al-Warith", "Ar-Rashid", "As-Sabur"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.names_grid_activity);

        GlobalClass globalObject = (GlobalClass) getApplicationContext();
        LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NamesGridActivity.super.onBackPressed();

            }
        });
        TextView tvHeading = (TextView) findViewById(R.id.txt_toolbar);


        initializeAds();

        gridViewNames = (GridView) findViewById(R.id.gridviewNames);
        gridViewNames.setOnItemClickListener(this);

        loadGridImages();
        mGridViewAdapter = new GridViewAdapter(this, namesImages, names);
        gridViewNames.setAdapter(mGridViewAdapter);
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

    private void loadGridImages() {
        namesImages.clear();
        for (int i = 1; i < 101; i++) {
            String imageName = "grid_name_" + i;
            int imageId = getResources().getIdentifier(imageName, "drawable", this.getPackageName());
            namesImages.add(imageId);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(this, NamesDetailActivity.class);
        intent.putExtra(NamesDetailActivity.EXTRA_NAMES_POSITION, position);
        this.startActivity(intent);
    }

    // @Override
    // public void onClick(View v) {
    // Intent intent = new Intent(this, NamesPlayingActivity.class);
    // startActivity(intent);
    // }

    public void onBackButtonClick(View v) {
        onBackPressed();
    }

    /////////////////////
    ///////////////////////
    ///////////////////////////////////////
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
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