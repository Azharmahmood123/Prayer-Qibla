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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;

import names.adapters.NamesData;
import names.adapters.NamesListAdapter;
import names.adapters.NamesModel;

public class NamesListActivity extends AppCompatActivity implements OnItemClickListener {

    // google ads
    AdView adview;
    ImageView adImage;
    private static final String LOG_TAG = "Ads";
    private final Handler adsHandler = new Handler();
    private int timerValue = 3000, networkRefreshTime = 10000;

    ListView gridViewNames;
    // ArrayList<Integer> namesImages = new ArrayList<>();
    NamesListAdapter mGridViewAdapter;
    ArrayList<NamesModel> namesData = new ArrayList<>();

//    String[] nameTime = {"0:00.000"," 0:11.428"," 0:13.348"," 0:14.738"," 0:16.458"," 0:18.168"," 0:19.838"," 0:21.218"," 0:22.718"," 0:24.408"," 0:25.748"," 0:28.128"," 0:29.888"," 0:31.578"," 0:33.165"," 0:34.521"," 0:36.502"," 0:38.142"," 0:39.632"," 0:41.428"," 0:43.068"," 0:44.998"," 0:46.698"," 0:48.112"," 0:50.028"," 0:51.167"," 0:52.397"," 0:53.927"," 0:55.591"," 0:56.606"," 0:57.321"," 0:58.827"," 1:00.107"," 1:01.633"," 1:02.863"," 1:04.355"," 1:05.735"," 1:07.262"," 1:08.402"," 1:09.612"," 1:10.798"," 1:12.438"," 1:13.559"," 1:15.205"," 1:16.575"," 1:18.395"," 1:20.005"," 1:21.255"," 1:22.389"," 1:23.889"," 1:25.856"," 1:27.256"," 1:28.436"," 1:29.656"," 1:31.216"," 1:32.458"," 1:34.005"," 1:35.315"," 1:36.496"," 1:37.621"," 1:38.751"," 1:40.028"," 1:41.253"," 1:42.453"," 1:43.903"," 1:45.743"," 1:47.453"," 1:49.043"," 1:50.243"," 1:51.733"," 1:53.443"," 1:55.023"," 1:56.533"," 1:57.683"," 1:58.863"," 2:00.653"," 2:02.353"," 2:03.493"," 2:04.933"," 2:05.643"," 2:07.353"," 2:08.903"," 2:10.132"," 2:11.312"," 2:13.833"," 2:17.373"," 2:18.676"," 2:20.332"," 2:20.351"," 2:21.551"," 2:22.691"," 2:24.711"," 2:28.501"," 2:30.261"," 2:31.447"," 2:32.637"," 2:34.097"," 2:35.557"," 2:37.527"," 2:38.866"," 2:40.516"," 3:30.941"};
//
//    // 14:59.073
//    public int[] convertToMillisMinutes(String[] time) {
//        int minutes, seconds, milis, miliSeconds;
//        String[] getTime;
//
//        // 14:59.073
//        int[] surahTimes = new int[time.length];
//
//        try
//        {
//            for (int i = 0; i < time.length; i++)
//            {
//                getTime = time[i].trim().split(":");
//                minutes = Integer.parseInt(getTime[0]);
//
//                String[] sec = getTime[1].split("\\.");
//                seconds = Integer.parseInt(sec[0]);
//                milis = Integer.parseInt(sec[1]);
//
//                miliSeconds = (minutes * 60 * 1000) + (seconds * 1000) + (milis);
//
//                surahTimes[i] = miliSeconds;
//
//            }
//        }
//        catch (Exception e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        return surahTimes;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.names_list_activity);

        // Debug here to get Array of Time in milliseconds
        // int[] timess1 = convertToMillisMinutes(nameTime);

        initializeAds();


        LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NamesListActivity.super.onBackPressed();

            }
        });



        gridViewNames = (ListView) findViewById(R.id.listviewNames);
        gridViewNames.setOnItemClickListener(this);

        NamesData data = new NamesData(this);
        namesData = data.getNamesData();

        // loadGridImages();
        mGridViewAdapter = new NamesListAdapter(this, namesData);
        gridViewNames.setAdapter(mGridViewAdapter);
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

//    private void loadGridImages() {
//        namesImages.clear();
//        for (int i = 1; i < 101; i++)
//        {
//            String imageName = "grid_name_" + i;
//            int imageId = getResources().getIdentifier(imageName, "drawable", this.getPackageName());
//            namesImages.add(imageId);
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(this, NamesDetailActivity.class);
        intent.putExtra(NamesDetailActivity.EXTRA_NAMES_POSITION, position);
        this.startActivity(intent);
    }

    // @Override
    // public void onClick(View v) {
    // Intent intent = new Intent(this, NamesListPlayingActivity.class);
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