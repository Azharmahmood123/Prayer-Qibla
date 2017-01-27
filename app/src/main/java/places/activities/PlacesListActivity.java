package places.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.helper.GeoCoderVolley;
import com.quranreading.listeners.OnCurrentLocationFoundListner;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;
import com.quranreading.sharedPreference.LocationPref;

import java.util.ArrayList;

import places.adapters.AdapterHalalPlaces;
import places.helper.DBManagerPlaces;
import places.models.PlacesModel;
import places.webtask.OnPlacesLoadedListner;
import places.webtask.TaskFetchPlaces;

public class PlacesListActivity extends AppCompatActivity implements OnPlacesLoadedListner, OnItemClickListener, ConnectionCallbacks, OnConnectionFailedListener, OnCurrentLocationFoundListner {

	public static final String EXTRA_PLACE_TYPE = "type";

	public static final int TYPE_MOSQUE = 0;
	public static final int TYPE_HALAL_PLACES = 1;

	// google ads
	AdView adview;
	ImageView adImage;
	private static final String LOG_TAG = "Ads";
	private final Handler adsHandler = new Handler();
	private int timerValue = 3000, networkRefreshTime = 10000;

	ArrayList<PlacesModel> mPlacesList = new ArrayList<>();
	ListView mListViewPlaces;
	ProgressBar progressBar;
	TextView tvNoData, tvTitle, tvCurrentAddress;
	LinearLayout layoutSettingEnable;
	AdapterHalalPlaces mAdapter;
	String[] arrTitles = new String[2];

	int type;

	GoogleApiClient mGoogleApiClient;
	LocationPref mLocationPref;
	String currentLocation = "";
	boolean isSettingsOpened = false;

	private boolean inProcess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.places_list_activity);

		initializeAds();

		type = getIntent().getIntExtra(EXTRA_PLACE_TYPE, 0);
		arrTitles[TYPE_MOSQUE] = getString(R.string.mosques);
		arrTitles[TYPE_HALAL_PLACES] = getString(R.string.halal_places);

		if(type == TYPE_MOSQUE)
		{
			AnalyticSingaltonClass.getInstance(this).sendScreenAnalytics("Mosque Finder");
		}
		else
		{
			AnalyticSingaltonClass.getInstance(this).sendScreenAnalytics("Halal Finder");
		}

		mLocationPref = new LocationPref(this);

		mListViewPlaces = (ListView) findViewById(R.id.listViewDuaTitles);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		layoutSettingEnable = (LinearLayout) findViewById(R.id.layout_palces_list_location_error);
		layoutSettingEnable.setVisibility(View.GONE);

		tvNoData = (TextView) findViewById(R.id.tv_no_data);
		tvTitle = (TextView) findViewById(R.id.tv_head_dua_list);
		tvCurrentAddress = (TextView) findViewById(R.id.tv_places_address);
		tvCurrentAddress.setVisibility(View.GONE);

		tvNoData.setTypeface(((GlobalClass) getApplication()).faceRobotoR);
		tvTitle.setTypeface(((GlobalClass) getApplication()).faceRobotoR);
		tvCurrentAddress.setTypeface(((GlobalClass) getApplication()).faceRobotoL);
		tvCurrentAddress.setSelected(true);

		tvTitle.setText(arrTitles[type]);

		progressBar.setVisibility(View.VISIBLE);
		tvNoData.setVisibility(View.GONE);
		mListViewPlaces.setOnItemClickListener(this);

		buildGoogleApiClient();
		mGoogleApiClient.connect();
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

	private synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub

		Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=" + currentLocation + "&daddr=" + mPlacesList.get(position).getName());
		Intent intnt = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
		intnt.setPackage("com.google.android.apps.maps");

		if(intnt.resolveActivity(getPackageManager()) != null)
		{
			if(!inProcess)
			{
				inProcess = true;
				startActivity(intnt);
			}
		}
		else
		{
			Intent intent = new Intent(PlacesListActivity.this, MapWebViewActivity.class);
			Bundle b = new Bundle();
			b.putSerializable(MapsViewActivity.EXTRA_PLACE_DATA, mPlacesList.get(position));
			intent.putExtras(b);
			if(!inProcess)
			{
				inProcess = true;
				startActivity(intent);
			}
		}
	}

	public void onBackButtonClick(View v) {

		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(!inProcess)
		{
			inProcess = true;
			super.onBackPressed();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

		Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if(mLastLocation != null)
		{
			mLocationPref.setLatitude(String.valueOf(mLastLocation.getLatitude()));
			mLocationPref.setLongitude(String.valueOf(mLastLocation.getLongitude()));
			if(isNetworkConnected())
			{
				GeoCoderVolley geoCoderVolley = new GeoCoderVolley(this);
				geoCoderVolley.setListener(this);
				geoCoderVolley.fetchCityFromCoordinates(this, mLastLocation);
				// findPlacesFromNetwork();
			}
			else
			{
				findPlacesOffline();
			}
		}
		else
		{
			findPlacesOffline();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	private void findPlacesFromNetwork(String code) {

		String lat, lng;

		lat = mLocationPref.getLatitude();
		lng = mLocationPref.getLongitude();
		String url;
		if(type == 0)
		{
			url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=4000&type=mosque&key=AIzaSyACMnpsQA5xwXpAMBJI3z6Y_GqUuHAbp_I";
		}
		else
		{
			if(isIslamicCountry(code))
			{
				url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=4000&type=restaurant&key=AIzaSyACMnpsQA5xwXpAMBJI3z6Y_GqUuHAbp_I";
			}
			else
			{
				url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=4000&type=restaurant&keyword=halal&key=AIzaSyACMnpsQA5xwXpAMBJI3z6Y_GqUuHAbp_I";
			}
		}

		TaskFetchPlaces mFetchPlaces = new TaskFetchPlaces(this, lat, lng, String.valueOf(type));
		mFetchPlaces.setListener(this);
		mFetchPlaces.getPlacesData(url);
	}

	private boolean isLocationEnabled() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if(isGPSEnabled || isNetworkEnabled)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private void findPlacesOffline() {

		if(!isLocationEnabled())
		{
			layoutSettingEnable.setVisibility(View.VISIBLE);
		}
		else
		{
			layoutSettingEnable.setVisibility(View.GONE);
		}
		DBManagerPlaces db = new DBManagerPlaces(this);
		db.open();
		Cursor cursor = db.getData(String.valueOf(type));
		mPlacesList.clear();
		if(cursor != null)
		{
			if(cursor.moveToFirst())
			{
				do
				{
					PlacesModel data = new PlacesModel();

					data.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex(DBManagerPlaces.ID))));
					data.setName(String.valueOf(cursor.getString(cursor.getColumnIndex(DBManagerPlaces.NAME))));
					data.setAddress(String.valueOf(cursor.getString(cursor.getColumnIndex(DBManagerPlaces.ADDRESS))));
					data.setLat(String.valueOf(cursor.getString(cursor.getColumnIndex(DBManagerPlaces.LATITUDE))));
					data.setLng(String.valueOf(cursor.getString(cursor.getColumnIndex(DBManagerPlaces.LONGITUDE))));
					data.setRating(String.valueOf(cursor.getString(cursor.getColumnIndex(DBManagerPlaces.RATING))));
					data.setDistance(String.valueOf(cursor.getString(cursor.getColumnIndex(DBManagerPlaces.DISTANCE))));

					mPlacesList.add(data);
				}
				while (cursor.moveToNext());
			}
			cursor.close();
		}
		db.close();

		progressBar.setVisibility(View.GONE);
		if(mPlacesList != null)
		{
			if(mPlacesList.size() > 0)
			{
				currentLocation = mLocationPref.getCurrentLocation();
				if(!currentLocation.isEmpty())
				{
					tvCurrentAddress.setVisibility(View.VISIBLE);
					tvCurrentAddress.setText(currentLocation);
				}
				else
				{
					tvCurrentAddress.setVisibility(View.GONE);
				}
				mAdapter = new AdapterHalalPlaces(PlacesListActivity.this, mPlacesList);
				mListViewPlaces.setAdapter(mAdapter);
			}
			else
			{
				tvCurrentAddress.setVisibility(View.GONE);
				tvNoData.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			tvCurrentAddress.setVisibility(View.GONE);
			tvNoData.setVisibility(View.VISIBLE);
		}
	}

	private boolean isIslamicCountry(String code) {

		String countryCode;

		boolean isTrue = false;
		String[] arr = { "mv", " sa", "so ", "tr ", "af ", "ma ", "dz ", "tn ", "pk ", "iq ", "ly ", "tj ", "jo ", "sn ", "dj ", "az ", "om ", "eg ", "ne ", "ml ", "gm ", "bd ", "tm ", "uz ", "id ", "kw ", "bh", "ae" };

		if(code.isEmpty())
		{
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			countryCode = tm.getSimCountryIso();

			if(countryCode.equals(""))
			{
				countryCode = tm.getNetworkCountryIso();
			}

			countryCode = countryCode.trim().toLowerCase();
		}
		else
		{
			countryCode = code;
		}

		if(countryCode.isEmpty())
		{
			return isTrue;
		}
		else
		{
			for (int index = 0; index < arr.length; index++)
			{
				if(arr[index].trim().equals(countryCode))
				{
					isTrue = true;
					break;
				}
			}
		}
		return isTrue;
	}

	@Override
	public void onPlacesLoaded(ArrayList<PlacesModel> mPlacesList) {
		// TODO Auto-generated method stub

		progressBar.setVisibility(View.GONE);
		if(mPlacesList != null)
		{
			if(mPlacesList.size() > 0)
			{
				if(type == TYPE_HALAL_PLACES)
				{
					mLocationPref.setHalalPlacesSaved(true);
				}
				else
				{
					mLocationPref.setMosquePlacesSaved(true);
				}
				this.mPlacesList = mPlacesList;
				mAdapter = new AdapterHalalPlaces(PlacesListActivity.this, mPlacesList);
				mListViewPlaces.setAdapter(mAdapter);
			}
			else
			{
				findPlacesOffline();
			}
		}
		else
		{
			findPlacesOffline();
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

		if(!isLocationEnabled())
		{
			layoutSettingEnable.setVisibility(View.VISIBLE);
			providerAlertMessage();
			Toast.makeText(this, R.string.toast_unable_detect_location, Toast.LENGTH_SHORT).show();
		}
		else
		{
			layoutSettingEnable.setVisibility(View.GONE);
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
		mGoogleApiClient.disconnect();
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

	@Override
	public void onCurrentLocationFoundListner(String Address, String code, double latitude, double longitude) {
		// TODO Auto-generated method stub

		if(Address != null)
		{
			if(!Address.isEmpty())
			{
				currentLocation = Address;
				mLocationPref.setCurrentLocation(currentLocation);
				tvCurrentAddress.setVisibility(View.VISIBLE);
				tvCurrentAddress.setText(Address);
				findPlacesFromNetwork(code);
			}
			else
			{
				tvCurrentAddress.setVisibility(View.GONE);
			}
		}
		else
		{
			tvCurrentAddress.setVisibility(View.GONE);
		}
	}

	public void onSettingsLocationClick(View v) {
		providerAlertMessage();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		if(isSettingsOpened)
		{
			isSettingsOpened = false;
			mGoogleApiClient.connect();
		}
		super.onRestart();
	}

	private void providerAlertMessage() {

		mGoogleApiClient.disconnect();
		isSettingsOpened = true;
		Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(settingsIntent);
//
//
//		AlertDialog alertProvider = null;
//
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle(getResources().getString(R.string.unable_to_find_location));
//		builder.setMessage(getResources().getString(R.string.enable_provider));
//		builder.setCancelable(false);
//
//		builder.setPositiveButton(getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//
//				mGoogleApiClient.disconnect();
//				isSettingsOpened = true;
//				Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//				startActivity(settingsIntent);
//			}
//		});
//
//		builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//
//			}
//		});
//
//		builder.setOnCancelListener(new OnCancelListener() {
//
//			@Override
//			public void onCancel(DialogInterface dialog) {
//			}
//		});
//
//		builder.setOnKeyListener(new Dialog.OnKeyListener() {
//			@Override
//			public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
//				// TODO Auto-generated method stub
//				if(keyCode == KeyEvent.KEYCODE_BACK)
//				{
//
//					return true;
//				}
//				return false;
//			}
//		});
//
//		alertProvider = builder.create();
//		alertProvider.show();
	}
}