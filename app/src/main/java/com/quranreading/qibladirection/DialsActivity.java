package com.quranreading.qibladirection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.sharedPreference.DialPref;

public class DialsActivity extends AppCompatActivity {
	// google ads
	AdView adview;
	ImageView adImage;
	private static final String LOG_TAG = "Ads";
	private final Handler adsHandler = new Handler();
	private int timerValue = 3000, networkRefreshTime = 10000;

	int dialValue = 1;
	DialPref dialPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dials);

		intialize();
		sendAnalyticsData();
	}

	private void sendAnalyticsData() {
		//AnalyticSingaltonClass.getInstance(this).sendScreenAnalytics("Dails Compass Screen");
	}

	public void onBackButtonClick(View v) {
		onBackPressed();
	}

	public void intialize() {
		//TextView tvDialHead;
		TextView[] dialtexts = new TextView[6];
		LinearLayout[] dialRows = new LinearLayout[6];

		dialPref = new DialPref(this);
		dialValue = dialPref.getDialValue();

		dialRows[0] = (LinearLayout) findViewById(R.id.dial_row_1);
		dialRows[1] = (LinearLayout) findViewById(R.id.dial_row_2);
		dialRows[2] = (LinearLayout) findViewById(R.id.dial_row_3);
		dialRows[3] = (LinearLayout) findViewById(R.id.dial_row_4);
		dialRows[4] = (LinearLayout) findViewById(R.id.dial_row_5);
		dialRows[5] = (LinearLayout) findViewById(R.id.dial_row_6);

		//tvDialHead = (TextView) findViewById(R.id.tv_dial_head);
		dialtexts[0] = (TextView) findViewById(R.id.dial_text_1);
		dialtexts[1] = (TextView) findViewById(R.id.dial_text_2);
		dialtexts[2] = (TextView) findViewById(R.id.dial_text_3);
		dialtexts[3] = (TextView) findViewById(R.id.dial_text_4);
		dialtexts[4] = (TextView) findViewById(R.id.dial_text_5);
		dialtexts[5] = (TextView) findViewById(R.id.dial_text_6);

		//tvDialHead.setTypeface(((GlobalClass) getApplication()).faceRobotoR);

		for (int pos = 0; pos < 6; pos++)
		{
			dialtexts[pos].setTypeface(((GlobalClass) getApplication()).faceRobotoL);
		}

		initializeAds();
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

	public void setDial(int dial) {
		dialPref.setDialValue(dial);
		finish();
	}

	public void onDialRow(View view) {
		Integer btnClick = Integer.parseInt(view.getTag().toString());
		setDial(btnClick);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated methodIndex stub
		super.onResume();

		if(((GlobalClass) getApplication()).isPurchase)
		{
			adImage.setVisibility(View.GONE);
			adview.setVisibility(View.GONE);
		}
		else
		{
			startAdsCall();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated methodIndex stub
		super.onPause();

		if(!((GlobalClass) getApplication()).isPurchase)
		{
			stopAdsCall();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated methodIndex stub
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
