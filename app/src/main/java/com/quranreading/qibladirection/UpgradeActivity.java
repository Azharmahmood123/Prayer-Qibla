package com.quranreading.qibladirection;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.qibladirection.util.IabHelper;
import com.quranreading.qibladirection.util.IabResult;
import com.quranreading.qibladirection.util.Inventory;
import com.quranreading.qibladirection.util.Purchase;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class UpgradeActivity extends Activity {

	// private Handler mHandler = new Handler();
	// private final int DELAY_TIME_ADS = 5000;
	// private Runnable mRunnableAds = new Runnable() {
	//
	// @Override
	// public void run() {
	// if(((GlobalClass) getApplication()).isPurchase)
	// {
	// adview.setVisibility(View.GONE);
	// }
	// else
	// {
	// if(isShowAds)
	// googleAds.startAdsCall();
	// }
	// }
	// };
	//
	// AdView adview;
	// GoogleAdsClass googleAds;

	LinearLayout premiumContainer;

	TextView tvPremuim[] = new TextView[8];
	// TextView tvHeading, tvFeature, tvFeature1, tvFeature2, tvFeature3, tvFeature4, tvFeature5, tvUpgrade, tvNoThanks;

	Context context = this;

	static final String TAG = "Qibla Connect";
	boolean isUpgrade = false, inappbuillingsetup = false, inPurchase = false;
	static final String SKU_UNLOCK = "sku_unlock";
	static final int RC_REQUEST = 10001;

	IabHelper mHelper;
	boolean isExitCall = false;
	boolean isShowAds = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_premium);
		this.setFinishOnTouchOutside(false);
		isExitCall = getIntent().getBooleanExtra("Exit", false);

		initViews();
		// initializeAds();

		if(isExitCall)
		{
			tvPremuim[7].setText(getString(R.string.exit));
		}

		String base64EncodedPublicKey = getValue();

		mHelper = new IabHelper(this, base64EncodedPublicKey);

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				Log.d(TAG, "Setup finished.");

				if(!result.isSuccess())
				{
					alert("Problem setting up in-app billing");
					return;
				}

				if(mHelper == null)
					return;

				Log.d(TAG, "Setup successful. Querying inventory.");
				inappbuillingsetup = true;
				if(inappbuillingsetup)
					mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});

		/*
		 * if(isExitCall) { showTwoButtonDialog(getResources().getString(R.string.exit)); } else { showTwoButtonDialog(getResources().getString(R.string.cancel)); }
		 */
	}

	// private void initializeAds() {
	//
	// adview = (AdView) findViewById(R.id.adView);
	// adview.setVisibility(View.GONE);
	// if(isShowAds)
	// googleAds = new GoogleAdsClass(this, adview, null);
	// }

	private void initViews() {

		int count = ((GlobalClass) getApplication()).purchasePref.getPremiumAdCount();
		if(count == 1)
		{
			((GlobalClass) getApplication()).purchasePref.setPremiumAdCount(0);
			isShowAds = false;
		}
		else
		{
			((GlobalClass) getApplication()).purchasePref.setPremiumAdCount(++count);
			isShowAds = true;
		}

		premiumContainer = (LinearLayout) findViewById(R.id.premium_container);
		// premiumContainer.setVisibility(View.GONE);

		tvPremuim[0] = (TextView) findViewById(R.id.tv_app_name);
		tvPremuim[1] = (TextView) findViewById(R.id.tv_premium_1);
		tvPremuim[2] = (TextView) findViewById(R.id.tv_premium_2);
		tvPremuim[3] = (TextView) findViewById(R.id.tv_premium_3);
		tvPremuim[4] = (TextView) findViewById(R.id.tv_premium_4);
		tvPremuim[5] = (TextView) findViewById(R.id.tv_premium_5);
		tvPremuim[6] = (TextView) findViewById(R.id.tv_premium_upgrade);
		tvPremuim[7] = (TextView) findViewById(R.id.tv_no_thanks);

		for (int i = 0; i < tvPremuim.length; i++)
		{
			tvPremuim[i].setTypeface(((GlobalClass) getApplication()).faceRobotoR);
		}

	}

	private void sendAnalyticsData() {

		AnalyticSingaltonClass.getInstance(this).sendEventAnalytics("Remove Ads", "Ads Removed");
	}

	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

			Log.d(TAG, "Query inventory finished.");

			// Have we been disposed of in the meantime? If so, quit.
			if(mHelper == null)
				return;

			// Is it a failure?
			if(result.isFailure())
			{
				// alert("Failed to query inventory");
				return;
			}

			Log.d(TAG, "Query inventory was successful.");

			/*
			 * Check for items we own. Notice that for each purchase, we check the developer payload to see if it's correct! See verifyDeveloperPayload().
			 */

			Purchase unlockPurchase = inventory.getPurchase(SKU_UNLOCK);
			isUpgrade = (unlockPurchase != null && verifyDeveloperPayload(unlockPurchase));
			Log.d(TAG, "Kalmas " + (isUpgrade ? "Locked" : "Unlocked"));

			if(isUpgrade)
			{
				showToast(context.getResources().getString(R.string.toast_already_purchased));
				savePurchase();

				// alert("Kalmas Unlocked");
			}
			/*
			 * else { alert("Kalmas Locked"); }
			 */
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
		if(mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		if(!mHelper.handleActivityResult(requestCode, resultCode, data))
		{
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		}
		else
		{
			Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
	}

	/** Verifies the developer payload of a purchase. */
	@SuppressWarnings("unused")
	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		/*
		 * TODO: verify that the developer payload of the purchase is correct. It will be the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase and verifying it here might seem like a good approach, but this will fail in the case where the user purchases an item on one device and then uses your app on a different device, because on the other device you will not
		 * have access to the random string you originally generated.
		 * 
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different between them, so that one user's purchase can't be replayed to another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app wasn't the one who initiated the purchase flow (so that items purchased by the user on one device work on other devices owned by the user).
		 * 
		 * Using your own server to store and verify developer payloads across app installations is recommended.
		 */

		return true;
	}

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

			// if we were disposed of in the meantime, quit.
			if(mHelper == null)
			{
				inPurchase = false;
				finish();
				return;
			}

			if(result.isFailure())
			{
				inPurchase = false;
				// alert("Error purchasing");
				finish();
				return;
			}
			if(!verifyDeveloperPayload(purchase))
			{
				inPurchase = false;
				// alert("Error purchasing. Authenticity verification failed.");
				finish();
				return;
			}

			Log.d(TAG, "Purchase successful.");

			if(purchase.getSku().equals(SKU_UNLOCK))
			{
				Log.d(TAG, "Purchased.");
				isUpgrade = true;

				sendAnalyticsData();
				showToast(context.getResources().getString(R.string.toast_purchase_successful));
				savePurchase();
				finish();
			}

			inPurchase = false;

			finish();
		}
	};

	/*
	 * // Called when consumption is complete IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() { public void onConsumeFinished(Purchase purchase, IabResult result) { Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " +
	 * result);
	 * 
	 * // if we were disposed of in the meantime, quit. if (mHelper == null) { inPurchase = false; progressBar.setVisibility(View.GONE); btnTransprnt.setVisibility(View.GONE); return; }
	 * 
	 * 
	 * 
	 * // We know this is the "gas" sku because it's the only one we consume, // so we don't check which sku was consumed. If you have more than one // sku, you probably should check... if (result.isSuccess()) { // successfully consumed, so we apply the effects of the item in our // game world's
	 * logic, which in our case means filling the gas tank a bit Log.d(TAG, "Consumption successful. Provisioning."); //saveData(); alert("Purchase Successful"); } else { complain("Error while consuming: " + result); }
	 * 
	 * inPurchase = false; progressBar.setVisibility(View.GONE); btnTransprnt.setVisibility(View.GONE); Log.d(TAG, "End consumption flow."); } };
	 */

	/*
	 * void complain(String message) { Log.e(TAG, "****Error: " + message); //alert("Error: " + message); }
	 */

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}

	public void showToast(String msg) {
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public void onSave(View view) {
		if(!inPurchase)
		{
			if(isUpgrade)
			{
				alert("No need! You're subscribed");
				return;
			}

			if(inappbuillingsetup || mHelper != null)
			{
				inPurchase = true;

				String payload = "";

				mHelper.launchPurchaseFlow(this, SKU_UNLOCK, RC_REQUEST, mPurchaseFinishedListener, payload);
			}
			else
			{
				alert("In-app builling not setup");
			}
		}
	}

	public void onRemoveAddsClick(View v) {
		if(!inPurchase)
		{
			if(isUpgrade)
			{
				alert("No need! You're subscribed");
				return;
			}

			if(inappbuillingsetup || mHelper != null)
			{
				inPurchase = true;

				String payload = "";

				mHelper.launchPurchaseFlow(this, SKU_UNLOCK, RC_REQUEST, mPurchaseFinishedListener, payload);
			}
			else
			{
				alert("In-app builling not setup");
			}
		}
	}

	public void savePurchase() {

		((GlobalClass) getApplication()).isPurchase = true;
		((GlobalClass) getApplication()).purchasePref.setPurchased(true);

		Intent end_actvty = new Intent();
		setResult(RESULT_OK, end_actvty);
		finish();
	}

	public void onCancel(View view) {

		if(isExitCall)
		{
			MainActivityNew.finishActivity();
		}

		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated methodIndex stub
		if(!inPurchase)
		{
			super.onBackPressed();
		}

//		if(isExitCall)
//		{
//			MainActivityNew.finishActivity();
//		}

	}

	public void showTwoButtonDialog(String negativeText) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(getResources().getString(R.string.remove_ads_heading)).setMessage(getResources().getString(R.string.remove_ads_msg)).setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User clicked OK button
				onRemoveAddsClick(null);
			}
		}).setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
				onCancel(null);
			}
		}).setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

				if(keyCode == KeyEvent.KEYCODE_BACK)
				{
					finish();
					dialog.dismiss();
				}
				return true;
			}
		}).setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated methodIndex stub

				finish();
			}
		});

		builder.show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated methodIndex stub
		super.onResume();
		// mHandler.postDelayed(mRunnableAds, DELAY_TIME_ADS);

		// if(((GlobalClass) getApplication()).isPurchase)
		// {
		// adview.setVisibility(View.GONE);
		// }
		// else
		// {
		// if(isShowAds)
		// googleAds.startAdsCall();
		// }
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated methodIndex stub
		super.onPause();
		// mHandler.removeCallbacks(mRunnableAds);

		// if(!((GlobalClass) getApplication()).isPurchase)
		// {
		// if(isShowAds)
		// googleAds.stopAdsCall();
		// }
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "Destroying helper.");

		if(mHelper != null)
		{
			mHelper.dispose();
			mHelper = null;
		}

		// if(!((GlobalClass) getApplication()).isPurchase)
		// {
		// if(isShowAds)
		// googleAds.destroyAds();
		// }
	}

	private class GoogleAdsClass {

		private AdView adview;
		private Context context;
		private static final String LOG_TAG = "Ads";
		private final Handler adsHandler = new Handler();
		private int timerValue = 3000, networkRefreshTime = 10000;

		public GoogleAdsClass(Context context, AdView adview, ImageView adImage) {
			super();
			this.context = context;
			this.adview = adview;
			if(!((GlobalClass) context.getApplicationContext()).isPurchase)
			{
				if(isNetworkConnected())
				{
					premiumContainer.setVisibility(View.VISIBLE);
				}
				else
				{
					this.adview.setVisibility(View.GONE);
				}
				setAdsListener();
			}

		}

		public void startAdsCall() {
			Log.i(LOG_TAG, "Starts");

			adview.resume();
			adsHandler.removeCallbacks(sendUpdatesToUI);
			adsHandler.postDelayed(sendUpdatesToUI, 0);
		}

		public void stopAdsCall() {
			Log.e(LOG_TAG, "Ends");
			adsHandler.removeCallbacks(sendUpdatesToUI);
			adview.pause();
		}

		public void destroyAds() {
			Log.e(LOG_TAG, "Destroy");
			adview.destroy();
			adview = null;
		}

		private final void updateUIAds() {
			if(isNetworkConnected())
			{
				AdRequest adRequest = new AdRequest.Builder().build();
				adview.loadAd(adRequest);
			}
			else
			{
				timerValue = networkRefreshTime;
				adview.setVisibility(View.GONE);
				adsHandler.removeCallbacks(sendUpdatesToUI);
				adsHandler.postDelayed(sendUpdatesToUI, timerValue);
			}
		}

		private void setAdsListener() {
			adview.setAdListener(new AdListener() {
				@Override
				public void onAdClosed() {
					Log.d(LOG_TAG, "onAdClosed");
				}

				@Override
				public void onAdFailedToLoad(int error) {

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
					premiumContainer.setVisibility(View.GONE);
					adview.setVisibility(View.VISIBLE);
				}
			});
		}

		private Runnable sendUpdatesToUI = new Runnable() {
			public void run() {
				Log.v(LOG_TAG, "Recall");
				updateUIAds();
			}
		};

		public boolean isNetworkConnected() {
			ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo netInfo = mgr.getActiveNetworkInfo();

			return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
		}
	}

	private String getValue() {
		String keyValue = "";

		try
		{
			String key = "NinSolIslamicKey";

			// String s = getString(R.string.key);
			// byte[] ciphertext = encrypt(key, s);

			byte[] theByteArray = { 29, -39, -49, 44, 58, 28, -121, -70, 94, 70, -3, 34, -50, -14, 29, -95, -15, 125, 90, -58, 16, -18, -17, -118, 16, 83, 99, 7, -21, 59, 57, -101, -30, -33, 42, 16, -86, 2, 125, 96, -118, 96, -94, -45, 40, 89, -71, -124, 89, -90, -8, 60, -52, 5, 66, -93, -111, 74,
					47, -59, -87, 72, -26, -4, 31, 11, 90, -80, -5, 70, -68, 81, -100, -40, 117, 47, 91, 37, -37, -23, -34, 70, 114, 100, -128, 95, 28, -28, -86, 96, 58, -10, -99, -29, 43, -3, -78, -25, -57, -77, 4, 70, 120, -16, 17, -8, 4, 42, 66, -73, 1, 11, -66, -39, 44, -56, -119, -50, -21, 87,
					-79, 104, -36, -8, 66, -107, -40, 14, -6, 103, 83, -60, 75, -18, -72, -35, 122, -47, 25, 89, -109, -82, 35, 76, 87, -10, 110, -114, -15, 41, -110, -71, 37, 99, 94, -87, -64, -29, 117, -66, -51, -25, -90, -56, -115, 102, 100, -61, -52, 80, -90, 127, 119, -15, -64, -45, -1, 78, 21,
					119, -7, 87, 126, -3, -86, -102, -27, -92, 1, -48, 42, -10, -125, 4, -1, 17, -46, -7, 36, -100, -53, 51, -14, 15, 24, -100, -56, 93, -6, 79, 5, 84, -27, 127, 117, -16, 71, -62, 39, -41, -124, 93, -4, 29, 37, -107, 5, -14, 36, -51, 9, 54, 121, -53, 26, 116, 36, 3, 112, -18, 86,
					-8, 47, -27, -91, 12, -33, 30, -110, 99, -59, -98, 57, 77, -96, -10, 34, 87, 80, 30, 104, -127, -16, -21, -17, 124, -9, 91, -4, 1, 13, -73, -26, -66, 18, 76, 119, 89, 107, -80, 93, -47, -89, 13, 59, -17, -19, 100, -80, -68, -128, -23, 40, 96, 58, -88, -10, 97, 78, -33, -45, -37,
					50, 68, 115, 47, 22, -29, -120, 61, -50, -84, -52, 32, -41, -93, 5, 124, 80, -83, -91, -43, -31, -27, -85, 1, 44, 111, -65, -88, 61, -22, -74, 65, -102, -50, -111, 16, 82, 3, -62, -3, -77, 14, -30, 48, 46, 95, -21, 120, -16, 72, -34, -56, 64, -75, -28, -111, -79, 73, -1, -82, 19,
					-55, 106, -50, -70, -89, 42, 71, 38, -72, 91, -14, 53, 23, 97, 59, -54, 62, -104, 75, -91, -52, 18, 12, -101, 26, 90, 109, -83, 77, -59, 83, -49, -17, 100, 67, -3, -115 };

			byte[] raw = key.getBytes(Charset.forName("US-ASCII"));
			if(raw.length != 16)
			{
				return keyValue;
			}

			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
			byte[] original = cipher.doFinal(theByteArray);
			keyValue = new String(original, Charset.forName("US-ASCII"));
			return keyValue;
		}
		catch (GeneralSecurityException e)
		{
			e.printStackTrace();
			return keyValue;
		}
	}

	private byte[] encrypt(String key, String value) throws GeneralSecurityException {
		byte[] raw = key.getBytes(Charset.forName("US-ASCII"));

		if(raw.length != 16)
		{
			return null;
		}

		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
		return cipher.doFinal(value.getBytes(Charset.forName("US-ASCII")));
	}

}
