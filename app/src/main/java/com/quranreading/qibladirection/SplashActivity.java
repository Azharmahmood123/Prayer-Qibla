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

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.quranreading.helper.DBManager;
import com.quranreading.helper.MyService;
import com.quranreading.sharedPreference.QiblaDirectionPref;

import java.io.IOException;

import noman.CommunityGlobalClass;
import noman.quran.dbconnection.DataBaseHelper;
import quran.activities.ServiceClass;
import quran.helper.DBManagerQuran;
import quran.helper.FileUtils;
import quran.sharedpreference.SurahsSharedPref;

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
	private final int SPLASH_TIME_SHORT = 2000;
	QiblaDirectionPref mQiblaDirectionPref;

	private Handler myHandler = new Handler();
	private int index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
		{
			FacebookSdk.sdkInitialize(getApplicationContext());
			AppEventsLogger.activateApp(this);
		}

		// FileUtils.test(this);
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

		if(mQiblaDirectionPref.chkDbVersion() > DBManager.DATABASE_VERSION)
		{
			SPLASH_TIME = SPLASH_TIME_SHORT;
		}
		else
		{
			mQiblaDirectionPref.setDatabaseCopied(false);
			SPLASH_TIME = SPLASH_TIME_LONG;
		}

		if(mQiblaDirectionPref.getInterstitialCount() == 0)
		{
		//	mQiblaDirectionPref.setInterstitialCount(1);
			myHandler.postDelayed(mRunnable, SPLASH_TIME);
		}
		else
		{
			mQiblaDirectionPref.setInterstitialCount(0);
			if(!isNetworkConnected() || ((GlobalClass) getApplication()).isPurchase)
			{
				myHandler.postDelayed(mRunnable, SPLASH_TIME);
			}
			else
			{
		//		showInterstitial();
				myHandler.postDelayed(mRunnable, SPLASH_TIME_LONG);
			}
		}

	/*	chkDownloadStatus();

		startAsyncTask();*/
	}

	public void showTextAnimation() {
		HTextView hTextView = (HTextView) findViewById(R.id.text);
		hTextView.setTypeface(((GlobalClass) getApplicationContext()).faceRobotoR);
		hTextView.setAnimateType(HTextViewType.TYPER);
		hTextView.animateText(getString(R.string.app_name)); // animate

		chkDownloadStatus();
		startAsyncTask();

	}

	public void needleAnimation() {
		final ImageView view = (ImageView) findViewById(R.id.img_rotate_needle);
		RotateAnimation aRotate = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

		aRotate.setStartOffset(0);
		aRotate.setDuration(1500);
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

		/*if(index > 0)
		{
			startNextActivity();
		}
		else
		{
			index++;
		}

		isShowInterstitial = true;*/

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

		if(c != null)
		{
			if(c.moveToFirst())
			{
				if(!((GlobalClass) getApplication()).isServiceRunning())
				{
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

		Intent intent = new Intent(context, MainActivityNew.class);
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

		if(mInterstitialAd != null)
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
}
