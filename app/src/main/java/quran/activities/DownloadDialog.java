package quran.activities;

import java.util.HashMap;

import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.helper.Constants;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import quran.helper.DBManagerQuran;
import quran.helper.FileUtils;

public class DownloadDialog extends Activity {

	int position, reciter;
	String name = "", audioName = "";
	// Button btnSurah, btnQuran, btnCancel, btnoky, btnback;
	// LinearLayout btnLayout;
	// TextView tvBody, header;
	String packageName = "com.android.providers.downloads";
	double sizeRequired;

	String msg = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transperant_layout);

		IntentFilter surahDownloadComplete = new IntentFilter(Constants.BroadcastActionComplete);
		registerReceiver(downloadComplete, surahDownloadComplete);

		name = getIntent().getStringExtra("SURAHNAME");
		position = getIntent().getIntExtra("POSITION", -1);
		audioName = getIntent().getStringExtra("ANAME");
		reciter = getIntent().getIntExtra("RECITER", -1);

		if(checkDownloadStatus())
		{
			if(msg != null)
			{
				showDownloadProcessingDialog();
			}
			else
			{
				msg = getString(R.string.download_msg) + " Surah " + name + "?";
				showDownloadDialog();
			}
		}

		sendAnalyticsData();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(downloadComplete);
	}

	private void sendAnalyticsData() {
		AnalyticSingaltonClass.getInstance(this).sendScreenAnalytics("Download Dialog Screen");
	}

	public boolean checkDownloadStatus() {

		msg = null;

		if(!checkDownloadManagerState())
		{
			return false;
		}

		HashMap<String, Boolean> chkDownload = new HashMap<String, Boolean>();
		boolean chkDownloadStatus = false;
		DBManagerQuran dbObj = new DBManagerQuran(DownloadDialog.this);
		dbObj.open();

		Cursor c = dbObj.getAllDownloads();

		if(c.moveToFirst())
		{
			do
			{
				int refId = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_DOWNLOAD_ID));
				int surahPos = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_SURAH_NO));
				String name = c.getString(c.getColumnIndex(DBManagerQuran.FLD_SURAH_NAME));
				String tempName = c.getString(c.getColumnIndex(DBManagerQuran.FLD_TEMP_NAME));

				if(name.equals(audioName) || name.equals(Constants.QuranFile2))
				{
					try
					{
						Query myDownloadQuery = new Query();
						// set the query filter to our previously Enqueued download
						myDownloadQuery.setFilterById(refId);

						DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
						// Query the download manager about downloads that have been requested.
						Cursor cursor = downloadManager.query(myDownloadQuery);

						if(cursor.moveToFirst())
						{
							chkDownload = FileUtils.chkDownloadStatus(DownloadDialog.this, cursor, tempName, refId, surahPos);
						}
						else
						{
							chkDownloadStatus = FileUtils.checkOneAudioFile(DownloadDialog.this, tempName, surahPos);
						}

						if(chkDownload.get(FileUtils.CHK_RUNNING))
						{
							if(name.contains(".mp3"))
							{
								msg = getString(R.string.downloading_in_progress);

								// btnback.setVisibility(View.VISIBLE);
								// // btnSurah.setEnabled(false);
								// tvBody.setText(R.string.downloading_in_progress);
								// btnLayout.setVisibility(View.GONE);
							}
							else
							{
								// btnQuran.setEnabled(false);
								// btnQuran.setText("Downloading Quran In Progress");
							}
						}

						if(chkDownloadStatus || chkDownload.get(FileUtils.CHK_SUCCESSFUL))
						{
							if(name.contains(".mp3"))
							{
								// btnSurah.setEnabled(false);
								// btnSurah.setText("Already Downloaded");

								Intent end_actvty = new Intent();
								setResult(RESULT_OK, end_actvty);
								finish();
							}
							else
							{
								// btnQuran.setEnabled(false);
								// btnQuran.setText(R.string.already_downloaded);
							}
						}
					}
					catch (NullPointerException e)
					{
						e.printStackTrace();
						chkDownloadStatus = FileUtils.checkOneAudioFile(DownloadDialog.this, tempName, surahPos);

						if(chkDownloadStatus) // || chkDownload.get(Constants.CHK_SUCCESSFUL))
						{
							if(name.contains(".mp3"))
							{
								// btnSurah.setEnabled(false);
								// btnSurah.setText("Already Downloaded");

								Intent end_actvty = new Intent();
								setResult(RESULT_OK, end_actvty);
								finish();
							}
							else
							{
								// btnQuran.setEnabled(false);
								// btnQuran.setText(R.string.already_downloaded);
							}
						}
					}
				}

			}
			while (c.moveToNext());
		}

		c.close();
		dbObj.close();

		return true;
	}

	public boolean checkDownloadManagerState() {
		boolean status = false;

		int state = this.getPackageManager().getApplicationEnabledSetting(packageName);

		if(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER || state == 4)
		{
			status = false;
		}
		else
		{
			status = true;
		}

		return status;
	}

	public void onDownloadSurah() {
		String[] sizesAlfasay = getResources().getStringArray(R.array.surah_sizes_alfasay);
		double fileSize = 0;
		if(reciter == 1)
		{
			fileSize = Double.parseDouble(sizesAlfasay[position - 1].toString());
		}
		else if(reciter == 2)
		{

		}

		if(!checkDownloadManagerState())
		{
			alertMessage();
			return;
		}

		if(isNetworkConnected())
		{
			if(!EnoughMemory(fileSize))
			{
				Toast toast = Toast.makeText(DownloadDialog.this,"Not Enough Memory" + "(" + String.format("%.2f", sizeRequired * 1E-6) + "MB Wajib)", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			else
			{
				if(!((GlobalClass) getApplication()).isServiceRunning())
				{
					Intent serviceIntent = new Intent(DownloadDialog.this, ServiceClass.class);
					serviceIntent.putExtra("NAME", name);
					serviceIntent.putExtra("POSITION", position);
					serviceIntent.putExtra("ANAME", audioName);
					serviceIntent.putExtra("RECITER", reciter);
					startService(serviceIntent);
				}
				else
				{
					Intent broadcastIntent = new Intent(Constants.BroadcastActionDownload);
					broadcastIntent.putExtra("NAME", name);
					broadcastIntent.putExtra("POSITION", position);
					broadcastIntent.putExtra("ANAME", audioName);
					broadcastIntent.putExtra("RECITER", reciter);
					sendBroadcast(broadcastIntent);
				}
			}
		}
		else
		{
			Toast toast = Toast.makeText(DownloadDialog.this,R.string.toast_network_error, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
				}

		finish();
	}

	@SuppressWarnings("deprecation")
	private boolean EnoughMemory(double fileSize) {
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		stat.restat(Environment.getDataDirectory().getPath());
		double bytesAvailable;
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
			bytesAvailable = (double) stat.getAvailableBytes();
		else
			bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
		if(bytesAvailable >= fileSize)
		{
			return true;
		}
		else
		{
			sizeRequired = fileSize - bytesAvailable;
			return false;
		}
	}

	private void alertMessage() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert);
		builder.setMessage("Download Manager Disabled");

		builder.setPositiveButton(getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				try
				{
					// Open the specific App Info page:
					Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					intent.setData(Uri.parse("package:" + packageName));
					startActivity(intent);
				}
				catch (ActivityNotFoundException e)
				{
					// e.printStackTrace();
					// Open the generic Apps page:
					Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
					startActivity(intent);
				}
			}
		});

		builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	public void onCancel(View view) {
		finish();
	}

	private void showDownloadProcessingDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.download);
		builder.setMessage(msg);

		builder.setPositiveButton(getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showDownloadDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.download);
		builder.setMessage(msg);

		builder.setPositiveButton(getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				onDownloadSurah();
			}
		});

		builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();

	}

	public boolean isNetworkConnected() {
		ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = mgr.getActiveNetworkInfo();

		return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
	}

	private BroadcastReceiver downloadComplete = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if(intent != null)
			{
				int pos;
				boolean status = false;

				status = intent.getBooleanExtra("STATUS", false);
				pos = intent.getIntExtra("POSITION", -1);

				if(status)
				{
					if(pos == position)
					{
						DownloadDialog.this.finish();
					}
				}
			}
		}
	};

}
