package duas.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.MainActivityNew;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;

import duas.adapters.DuaTitlesAdapter;
import duas.db.DBManagerDua;
import duas.db.DuaModel;

public class DuasListActivity extends AppCompatActivity implements OnItemClickListener {

	public static final String EXTRA_DUA_CATEGORY = "category";

	// google ads
	AdView adview;
	ImageView adImage;
	private static final String LOG_TAG = "Ads";
	private final Handler adsHandler = new Handler();
	private int timerValue = 3000, networkRefreshTime = 10000;
	// InterstitialAd mInterstitialAd;

	String[] duaTitles;
	ArrayList<DuaModel> duasList = new ArrayList<DuaModel>();
	String duaCategory;
	DBManagerDua dbManger;
	ListView mListViewDuas;
	Context mContext = this;

	private boolean inProcess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dua_activity_list);

		AnalyticSingaltonClass.getInstance(mContext).sendScreenAnalytics("Duas Categories 4.0");

		initializeAds();

		mListViewDuas = (ListView) findViewById(R.id.listViewDuaTitles);

		mListViewDuas.setOnItemClickListener(this);

		duaCategory = getIntent().getStringExtra(EXTRA_DUA_CATEGORY);
		dbManger = new DBManagerDua(this);
		LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DuasListActivity.super.onBackPressed();

			}
		});
		TextView tvHeading = (TextView) findViewById(R.id.txt_toolbar);
		tvHeading.setSelected(true);
		tvHeading.setText(duaCategory);

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub

				dbManger.open();
				Cursor c = dbManger.GetDuasByCategory(duaCategory);

				if(c.moveToFirst())
				{
					int index = 0;
					duasList.clear();
					duaTitles = new String[c.getCount()];
					do
					{
						String id = c.getInt(c.getColumnIndex(DBManagerDua.ID)) + "";
						String duaId = c.getString(c.getColumnIndex(DBManagerDua.DUA_ID));
						String duaTitle = c.getString(c.getColumnIndex(DBManagerDua.DUA_TITLE));
						String duaEnglish = c.getString(c.getColumnIndex(DBManagerDua.DUA_ENG_TRANSLATION));
						String duaUrdu = c.getString(c.getColumnIndex(DBManagerDua.DUA_URDU_TRANSLATION));
						String duaTransliteration = c.getString(c.getColumnIndex(DBManagerDua.DUA_TRANSLITERATION));
						String audioName = c.getString(c.getColumnIndex(DBManagerDua.DUA_AUDIO_NAME));
						String duaArabic = c.getString(c.getColumnIndex(DBManagerDua.DUA_ARABIC));
						String favourite = c.getString(c.getColumnIndex(DBManagerDua.FAVOURITE));
						String category = c.getString(c.getColumnIndex(DBManagerDua.CATEGORY));
						String additionalInfo = c.getString(c.getColumnIndex(DBManagerDua.ADDITIONAL_INFO));
						String reference = c.getString(c.getColumnIndex(DBManagerDua.REFERENCE));
						String counter = c.getString(c.getColumnIndex(DBManagerDua.DUA_COUNTER));

						DuaModel data = new DuaModel(id, duaId, duaTitle, duaEnglish, duaUrdu, duaTransliteration, audioName, duaArabic, favourite, category, additionalInfo, reference, counter);
						duasList.add(data);

						duaTitles[index++] = c.getString(c.getColumnIndex(DBManagerDua.DUA_TITLE));
					}
					while (c.moveToNext());
				}
				dbManger.close();

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				DuaTitlesAdapter adapter = new DuaTitlesAdapter(DuasListActivity.this, duaTitles);
				mListViewDuas.setAdapter(adapter);

			}
		};

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
		}
		else
		{
			task.execute((Void) null);
		}
	} // ///////////////////////



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

		if(!((GlobalClass) getApplication()).isPurchase)
		{
			// mInterstitialAd = new InterstitialAd(this);
			// mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial));
			//
			// mInterstitialAd.setAdListener(new AdListener() {
			// @Override
			// public void onAdClosed() {
			// requestNewInterstitial();
			// }
			//
			// });
			//
			// requestNewInterstitial();

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
	}

	// private void requestNewInterstitial() {
	//
	// AdRequest adRequest = new AdRequest.Builder().build();
	//
	// mInterstitialAd.loadAd(adRequest);
	// }
	//
	// private void showInterstitialAd() {
	// if(!((GlobalClass) getApplication()).isPurchase)
	// {
	// if(mInterstitialAd.isLoaded())
	// {
	// mInterstitialAd.show();
	// }
	// }
	// }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		if(!inProcess)
		{
			inProcess = true;

			Intent intent = new Intent(this, DuaDetailsActivity.class);

			Bundle b = new Bundle();
			b.putSerializable(DuaDetailsActivity.EXTRA_DUA_ARRAY_LIST, duasList);
			b.putInt(DuaDetailsActivity.EXTRA_DUA_POSITION, position);
			b.putString(DuaDetailsActivity.EXTRA_DUA_CATEGORY, duaCategory);

			intent.putExtras(b);
			startActivity(intent);


			// showInterstitialAd();
		}
	}

	public void onBackButtonClick(View v) {
		if(!inProcess)
		{
			onBackPressed();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		inProcess = false;
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
		if(!((GlobalClass) getApplication()).isPurchase)
		{
			destroyAds();
			// if(mInterstitialAd != null)
			// mInterstitialAd.setAdListener(null);
			// mInterstitialAd = null;
		}
		super.onDestroy();
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
