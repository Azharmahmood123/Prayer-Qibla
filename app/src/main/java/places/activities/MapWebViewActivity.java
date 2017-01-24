package places.activities;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;
import com.quranreading.sharedPreference.LocationPref;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import places.models.PlacesModel;

public class MapWebViewActivity extends AppCompatActivity {

	// google ads
	AdView adview;
	ImageView adImage;
	private static final String LOG_TAG = "Ads";
	private final Handler adsHandler = new Handler();
	private int timerValue = 3000, networkRefreshTime = 10000;

	PlacesModel mPlacesModel;
	WebView mWebView;
	ProgressBar mProgressbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.halal_webview);

		initializeAds();

		mPlacesModel = (PlacesModel) getIntent().getSerializableExtra(MapsViewActivity.EXTRA_PLACE_DATA);

		LocationPref mLocationPref = new LocationPref(this);

		String lat = mLocationPref.getLatitude();
		String lng = mLocationPref.getLongitude();

		mProgressbar = (ProgressBar) findViewById(R.id.progressBarWebView);
		mWebView = (WebView) findViewById(R.id.webView1);
		mWebView.setWebViewClient(new WebViewClient() {

			public void onPageFinished(WebView view, String url) {
				mProgressbar.setVisibility(View.GONE);
			}
		});
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl("http://maps.google.com/maps?" + "saddr=" + lat + "," + lng + "&daddr=" + mPlacesModel.getName() + ", " + mPlacesModel.getAddress());

		TextView tvHeading = (TextView) findViewById(R.id.tv_head_maps);
		tvHeading.setTypeface(((GlobalClass) getApplication()).faceRobotoR);
		tvHeading.setSelected(true);
		tvHeading.setText(mPlacesModel.getName());

		// Header in this Activity is in Gone state, If want to show Header Comment these lines
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.header_layout);
		layout.setVisibility(View.GONE);
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
		if(mWebView.canGoBack())
		{
			mWebView.goBack();
		}
		else
		{
			onBackPressed();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN)
		{
			switch (keyCode)
			{
			case KeyEvent.KEYCODE_BACK:
				if(mWebView.canGoBack())
				{
					mWebView.goBack();
				}
				else
				{
					finish();
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
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
