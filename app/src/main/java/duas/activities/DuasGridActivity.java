package duas.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;

import duas.adapters.GridViewDuaAdapter;
import duas.db.DBManagerDua;

public class DuasGridActivity extends AppCompatActivity {

    // google ads
    AdView adview;
    ImageView adImage;
    private static final String LOG_TAG = "Ads";
    private final Handler adsHandler = new Handler();
    private int timerValue = 3000, networkRefreshTime = 10000;

    GridView gridview;
    ArrayList<Integer> categoryImages = new ArrayList<Integer>();
    ArrayList<String> searchResultList = new ArrayList<String>();
    ArrayList<String> searchItemCategories = new ArrayList<String>();
    DBManagerDua dbManager;
    GlobalClass mGlobal;

    ArrayList<Integer> namesImages = new ArrayList<>();
    String categories[] = {"Morning/Evening", "Restroom", "Prayer", "Eat/Drink", "Dressing", "Travelling", "Family", "Home", "Blessings", "Protection", "Forgiveness", "Fasting", "Hajj & Umrah", "Funeral/Grave", "40 Rabbanas", "Animal", "Rain", "Random"};
    protected boolean inProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dua_grid_layout);

        initializeAds();

        gridview = (GridView) findViewById(R.id.gridViewDua);
        mGlobal = ((GlobalClass) getApplicationContext());


        LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DuasGridActivity.super.onBackPressed();

            }
        });
        TextView tvHeading = (TextView) findViewById(R.id.txt_toolbar);
        tvHeading.setSelected(true);
        tvHeading.setText(R.string.grid_duas);

        loadGridItems();

        GridViewDuaAdapter gridAdapter = new GridViewDuaAdapter(this, categoryImages, categories);
        gridview.setAdapter(gridAdapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                AnalyticSingaltonClass.getInstance(DuasGridActivity.this).sendEventAnalytics("Duas Categories 4.0", categories[position]);
                if (!inProcess) {
                    inProcess = true;
                    Intent intent = new Intent(DuasGridActivity.this, DuasListActivity.class);
                    intent.putExtra(DuasListActivity.EXTRA_DUA_CATEGORY, categories[position]);
                    startActivity(intent);
                }
            }
        });
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

    private void loadGridItems() {

        categoryImages.add(R.drawable.g_item14);
        categoryImages.add(R.drawable.g_item21);
        categoryImages.add(R.drawable.g_item16);

        categoryImages.add(R.drawable.g_item6);
        categoryImages.add(R.drawable.g_item5);
        categoryImages.add(R.drawable.g_item22);

        categoryImages.add(R.drawable.g_item7);
        categoryImages.add(R.drawable.g_item11);
        categoryImages.add(R.drawable.g_item2);

        categoryImages.add(R.drawable.g_item17);
        categoryImages.add(R.drawable.g_item9);
        categoryImages.add(R.drawable.g_item8);

        categoryImages.add(R.drawable.g_item24);
        categoryImages.add(R.drawable.g_item10);
        categoryImages.add(R.drawable.g_item18);

        categoryImages.add(R.drawable.g_item1);
        categoryImages.add(R.drawable.g_item19);
        categoryImages.add(R.drawable.g_item20);

    }

    public void poulateSearchList(String wordWidSpace, String wordWidoutSpace) {
        searchResultList.clear();
        dbManager = new DBManagerDua(this);
        dbManager.open();
        Cursor c = dbManager.getSearchedItems(wordWidSpace, wordWidoutSpace);
        if (c.moveToFirst()) {
            do {
                searchItemCategories.add(c.getString(c.getColumnIndex("category")));
                searchResultList.add(c.getString(c.getColumnIndex("dua_title")));
            }
            while (c.moveToNext());
        }
        dbManager.close();
    }

    public void onBackButtonClick(View v) {

        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (!inProcess) {
            inProcess = true;
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        inProcess = false;
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