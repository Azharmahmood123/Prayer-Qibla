package quran.activities;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class StopSignsActivity extends AppCompatActivity {
	// google ads
	AdView adview;
	ImageView adImage;
	private static final String LOG_TAG = "Ads";
	private final Handler adsHandler = new Handler();
	private int timerValue = 3000, networkRefreshTime = 10000;

	private TextView[] tvArabicWord = new TextView[13];
	private TextView[] tvDetail = new TextView[13];
	GlobalClass mGlobal;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Auto-generated method stub
		// setContentView(R.layout.stopsigns_activity);
		mGlobal = ((GlobalClass) getApplicationContext());
		setContentView(R.layout.activity_stopsigns);
		initializeAds();

		TextView tvHeader = (TextView) findViewById(R.id.tv_header);

		tvHeader.setTypeface(((GlobalClass) getApplication()).faceRobotoR);

		tvArabicWord[0] = (TextView) findViewById(R.id.tv_word_1);
		tvArabicWord[1] = (TextView) findViewById(R.id.tv_word_2);
		tvArabicWord[2] = (TextView) findViewById(R.id.tv_word_3);
		tvArabicWord[3] = (TextView) findViewById(R.id.tv_word_4);
		tvArabicWord[4] = (TextView) findViewById(R.id.tv_word_5);
		tvArabicWord[5] = (TextView) findViewById(R.id.tv_word_6);
		tvArabicWord[6] = (TextView) findViewById(R.id.tv_word_7);
		tvArabicWord[7] = (TextView) findViewById(R.id.tv_word_8);
		tvArabicWord[8] = (TextView) findViewById(R.id.tv_word_9);
		tvArabicWord[9] = (TextView) findViewById(R.id.tv_word_10);
		tvArabicWord[10] = (TextView) findViewById(R.id.tv_word_11);
		tvArabicWord[11] = (TextView) findViewById(R.id.tv_word_12);
		tvArabicWord[12] = (TextView) findViewById(R.id.tv_word_13);

		tvDetail[0] = (TextView) findViewById(R.id.tv_detail_1);
		tvDetail[1] = (TextView) findViewById(R.id.tv_detail_2);
		tvDetail[2] = (TextView) findViewById(R.id.tv_detail_3);
		tvDetail[3] = (TextView) findViewById(R.id.tv_detail_4);
		tvDetail[4] = (TextView) findViewById(R.id.tv_detail_5);
		tvDetail[5] = (TextView) findViewById(R.id.tv_detail_6);
		tvDetail[6] = (TextView) findViewById(R.id.tv_detail_7);
		tvDetail[7] = (TextView) findViewById(R.id.tv_detail_8);
		tvDetail[8] = (TextView) findViewById(R.id.tv_detail_9);
		tvDetail[9] = (TextView) findViewById(R.id.tv_detail_10);
		tvDetail[10] = (TextView) findViewById(R.id.tv_detail_11);
		tvDetail[11] = (TextView) findViewById(R.id.tv_detail_12);
		tvDetail[12] = (TextView) findViewById(R.id.tv_detail_13);

		// tvHeader.setTypeface(((GlobalClass) getApplication()).faceHeading);

		for (int pos = 0; pos < 13; pos++)
		{
			tvArabicWord[pos].setTypeface(((GlobalClass) getApplication()).faceArabic);
			tvDetail[pos].setTypeface(((GlobalClass) getApplication()).faceRobotoL);
		}

		/*
		 * adImg = (ImageView)findViewById(R.id.adimg); adview = (AdView)findViewById(R.id.adView); adImg.setVisibility(View.GONE); adview.setVisibility(View.GONE);
		 * 
		 * googleAds = new GoogleAds(StopSignsActivity.this, adview);
		 */
		sendAnalyticsData();
	}

	private void sendAnalyticsData() {
		AnalyticSingaltonClass.getInstance(this).sendScreenAnalytics("StopSign Screen");
	}

	private void initializeAds() {
		adview = (AdView) findViewById(R.id.adView);
		adImage = (ImageView) findViewById(R.id.adimg);
		adImage.setVisibility(View.GONE);
		adview.setVisibility(View.GONE);

		if(isNetworkConnected())
		{
			this.adview.setVisibility(View.VISIBLE);
		}
		else
		{
			this.adview.setVisibility(View.GONE);
		}
		setAdsListener();

	}

	public void onBackButtonClick(View v) {
		onBackPressed();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if(!((GlobalClass) getApplication()).isPurchase)
		{
			startAdsCall();
		}
		else
		{
			adImage.setVisibility(View.GONE);
			adview.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		if(!((GlobalClass) getApplication()).isPurchase)
		{
			stopAdsCall();
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(!((GlobalClass) getApplication()).isPurchase)
		{
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
		if(isNetworkConnected())
		{
			AdRequest adRequest = new AdRequest.Builder().build();
			adview.loadAd(adRequest);
		}
		else
		{
			timerValue = networkRefreshTime;
			adsHandler.removeCallbacks(sendUpdatesAdsToUI);
			adsHandler.postDelayed(sendUpdatesAdsToUI, timerValue);
		}
	}

	public void startAdsCall() {
		Log.i(LOG_TAG, "Starts");
		if(isNetworkConnected())
		{
			this.adview.setVisibility(View.VISIBLE);
		}
		else
		{
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
		switch (errorCode)
		{
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
