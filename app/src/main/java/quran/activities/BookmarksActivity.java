package quran.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

import quran.adapter.BookmarksListAdapter;
import quran.helper.DBManagerQuran;
import quran.model.BookmarksListModel;

public class BookmarksActivity extends AppCompatActivity {
	// google ads
	AdView adview;
	ImageView adImage;
	private static final String LOG_TAG = "Ads";
	private final Handler adsHandler = new Handler();
	private int timerValue = 3000, networkRefreshTime = 10000;

	String from;
	TextView tvHeader, tvName;
	ListView bookmarksListView;
	Boolean setListener = false, checkAsian = false;
	int resrcTimeArray;
	String[] engNamesData = null;
	ArrayList<BookmarksListModel> bookmarksList = new ArrayList<BookmarksListModel>();
	BookmarksListAdapter customAdapter;

	RelativeLayout headerLayout;
	TextView tvNoBookmarks;

	boolean isResetFavList = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_bookmarks);
		initializeAds();

		headerLayout = (RelativeLayout) findViewById(R.id.headerlayout);

		tvHeader = (TextView) findViewById(R.id.tv_header);
		bookmarksListView = (ListView) findViewById(R.id.listView);
		tvNoBookmarks = (TextView) findViewById(R.id.tv_no_bookmarks);
		tvNoBookmarks.setVisibility(View.GONE);

		tvHeader.setTypeface(((GlobalClass) getApplication()).faceRobotoR);
		tvNoBookmarks.setTypeface(((GlobalClass) getApplication()).faceRobotoR);

		bookmarksListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		bookmarksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				isResetFavList = true;

				int surahNo = bookmarksList.get(position).getsurahNo();
				int ayahNo = bookmarksList.get(position).getAyahNo();

				Intent end_actvty = new Intent(BookmarksActivity.this, SurahActivity.class);
				end_actvty.putExtra(SurahActivity.KEY_EXTRA_SURAH_NO, surahNo);
				// if(surahNo == 9 || surahNo == 1)
				// ayahNo = ayahNo - 1;
				end_actvty.putExtra(SurahActivity.KEY_EXTRA_AYAH_NO, ayahNo);
				startActivity(end_actvty);


			}
		});

		bookmarksListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// TODO Auto-generated method stub
				customAdapter.removeSelection();
				headerLayout.setVisibility(View.VISIBLE);
				if(bookmarksList.size() == 0)
				{
					tvNoBookmarks.setVisibility(View.VISIBLE);
					bookmarksListView.setVisibility(View.GONE);
				}
				else
				{
					tvNoBookmarks.setVisibility(View.GONE);
					bookmarksListView.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				headerLayout.setVisibility(View.GONE);
				mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// TODO Auto-generated method stub
				switch (item.getItemId())
				{
				case R.id.delete:
				{
					DBManagerQuran dbObj = new DBManagerQuran(BookmarksActivity.this);
					dbObj.open();

					// Calls getSelectedIds method from ListViewAdapter Class
					SparseBooleanArray selected = customAdapter.getSelectedIds();
					// Captures all selected ids with a loop
					for (int i = (selected.size() - 1); i >= 0; i--)
					{
						if(selected.valueAt(i))
						{
							BookmarksListModel selecteditem = customAdapter.getItem(selected.keyAt(i));
							// Remove selected items following the ids
							customAdapter.remove(selecteditem);
							dbObj.deleteOneBookmark(selecteditem.getBookMarkId());
						}
					}
					// Close CAB
					dbObj.close();
					mode.finish();
					return true;
				}
				default:
					return false;
				}
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
				// Capture total checked items
				final int checkedCount = bookmarksListView.getCheckedItemCount();
				// Set the CAB title according to total checked items
				mode.setTitle(checkedCount + " Selected");
				// Calls toggleSelection method from ListViewAdapter Class
				customAdapter.toggleSelection(position);

			}
		});

		initializeBookmarksList();
		sendAnalyticsData("Bookmark Screen");
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

	private void sendAnalyticsData(String screen) {
		AnalyticSingaltonClass.getInstance(this).sendScreenAnalytics(screen);
	}

	public void initializeBookmarksList() {

		bookmarksList.clear();

		tvHeader.setText(getResources().getString(R.string.txt_bookmarks));

		DBManagerQuran dbObj = new DBManagerQuran(this);
		dbObj.open();

		String surahName;
		int id, surahNo, ayahNo;

		Cursor c = dbObj.getAllBookmarks();

		if(c.moveToFirst())
		{
			do
			{
				id = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_ID));
				surahName = c.getString(c.getColumnIndex(DBManagerQuran.FLD_SURAH_NAME));
				surahNo = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_SURAH_NO));

				ayahNo = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_AYAH_NO));
				// if(surahNo == 9 || surahNo == 1)
				// ayahNo = ayahNo + 1;

				BookmarksListModel model = new BookmarksListModel(id, surahName, surahNo, ayahNo);
				bookmarksList.add(model);

			}
			while (c.moveToNext());

			tvNoBookmarks.setVisibility(View.GONE);
			bookmarksListView.setVisibility(View.VISIBLE);
		}
		else
		{
			tvNoBookmarks.setVisibility(View.VISIBLE);
			bookmarksListView.setVisibility(View.GONE);
		}

		customAdapter = new BookmarksListAdapter(BookmarksActivity.this, bookmarksList);
		bookmarksListView.setAdapter(customAdapter);

		c.close();
		dbObj.close();
	}

	public void showToast(String msg) {
		Toast toast = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	private void showInterstitialAd() {
		// if(!((GlobalClass) getApplication()).isPurchase)
		// {
		// InterstitialAdSingleton mInterstitialAdSingleton = InterstitialAdSingleton.getInstance(this);
		// mInterstitialAdSingleton.showInterstitial();
		// }
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if(isResetFavList)
		{
			isResetFavList = false;
			initializeBookmarksList();
		}

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
