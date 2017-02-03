package quran.activities;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.quranreading.helper.Constants;
import com.quranreading.helper.DBManager;
import com.quranreading.qibladirection.R;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import quran.helper.DBManagerQuran;
import quran.helper.FileUtils;
import quran.helper.ServiceAlarmReceiver;

public class ServiceClass extends Service {

	private int alarmId = 1234;
	private long downloadReference;
	public DownloadManager downloadManager;

	@Override
	public void onCreate() {
		super.onCreate();
		downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

		IntentFilter surahDownloadFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		registerReceiver(downloadReceiver, surahDownloadFilter);

		IntentFilter newDownloadFilter = new IntentFilter(Constants.BroadcastActionDownload);
		registerReceiver(newSurahDownload, newDownloadFilter);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("Service", "onStartCommand");

		downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

		try
		{
			chkServiceRunning();

			if(intent != null)
			{
				extractValues(intent);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return START_STICKY;
	}

	public void chkServiceRunning() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 5);

		Intent intent = new Intent(getApplicationContext(), ServiceAlarmReceiver.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 15000, pendingIntent);
	}

	public void cancelChkServiceRunning() {
		Intent intent = new Intent(getApplicationContext(), ServiceAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		pendingIntent.cancel();
	}

	public void extractValues(Intent intent) {

		int surahPosition, reciter;
		String url = "", audioName = "", fullName = "", tempName = "temp_";

		fullName = intent.getStringExtra("NAME");
		surahPosition = intent.getIntExtra("POSITION", -1);
		audioName = intent.getStringExtra("ANAME");
		reciter = intent.getIntExtra("RECITER", -1);

		tempName = tempName + audioName;
		// if reciter is abdul basit
		if(reciter == 0)
		{
			url = getLink() + audioName;
		}
		// if reciter is alfasay
		else if(reciter == 1)
		{
			url = getLink() + audioName;
		}
		// if reciter is sudais
		else
		{
			url = getLink() + audioName;
		}

		if(!url.equals(""))
		{
			Log.i("Audio Name", fullName);
			Log.i("Temp Audio Name", tempName);
			Log.i("Audio Url", url);

			// url += API_KEY_CLOUD_STORAGE;

			downloadSurah(surahPosition, fullName, audioName, tempName, url, reciter);
		}
	}

	private String getLink() {

		DBManager db = new DBManager(this);
		db.open();
		String url;
		TimeZone tz = TimeZone.getDefault();
		long timeNow = new Date().getTime();
		double timezone = (double) ((tz.getOffset(timeNow) / 1000) / 60) / 60;
		if(timezone >= 4.0 && timezone <= 13.0) // Asia
		{
			url = db.getUrl(DBManager.FLD_ASIA, DBManager.MODULE_QURAN);
		}
		else if(timezone >= -13.0 && timezone <= -4.0) // US
		{
			url = db.getUrl(DBManager.FLD_US, DBManager.MODULE_QURAN);
		}
		else if(timezone >= -3.5 && timezone <= 3.5) // EU
		{
			url = db.getUrl(DBManager.FLD_EU, DBManager.MODULE_QURAN);
		}
		else
		{
			url = db.getUrl(DBManager.FLD_EU, DBManager.MODULE_QURAN);
		}

		db.close();

		return url;
	}

	private void downloadSurah(int pos, String fullName, String name, String tempName, String url, int reciter) {
		DBManagerQuran dbObj = new DBManagerQuran(getApplicationContext());
		dbObj.open();

		// Uri Download_Uri = Uri.parse("https://www.dropbox.com/s/wlzjgly26p6eucf/surah_rehman_0.mp3?dl=1");
		Uri Download_Uri = Uri.parse(url);
		DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

		// Restrict the types of networks over which this download may proceed.
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

		// Set whether this download may proceed over a roaming connection.
		request.setAllowedOverRoaming(false);

		// Set the title of this download, to be displayed in notifications (if enabled).
		request.setTitle("Downloading . . . ");

		// Set a description of this download, to be displayed in notifications (if enabled)
		request.setDescription(fullName);

		// Set the local destination for the downloaded file to a path within the application's external files directory
		request.setDestinationInExternalPublicDir(Constants.rootFolderQuran, tempName);

		// Enqueue a new download and same the referenceId
		try
		{
			downloadReference = downloadManager.enqueue(request);

			long refId = downloadReference;

			Log.i("New Download", String.valueOf(refId));
			dbObj.addDownload((int) refId, pos, name, tempName);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			sendDataToActivity(false, name, fullName, pos, reciter);
			Cursor c = dbObj.getAllDownloads();

			if(!c.moveToFirst())
			{
				Log.e("On Service", "stopSelf");
				stopSelf();
			}
		}

		dbObj.close();
	}

	private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int index = -1, position = -1;
			String name = "", tempName = "";

			// check if the broadcast message is for our Enqueued download
			long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

			if(checkDownloadStatus(referenceId))
			{
				DBManagerQuran dbObj = new DBManagerQuran(getApplicationContext());
				dbObj.open();

				Cursor c = dbObj.getAllDownloads();

				if(c.moveToFirst())
				{
					do
					{
						int id = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_DOWNLOAD_ID));

						if(id == (int) referenceId)
						{
							index = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_ID));
							position = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_SURAH_NO));
							name = c.getString(c.getColumnIndex(DBManagerQuran.FLD_SURAH_NAME));
							tempName = c.getString(c.getColumnIndex(DBManagerQuran.FLD_TEMP_NAME));

							break;
						}

					}
					while (c.moveToNext());
				}

				c.close();

				if(index != -1)
				{
					if(name.contains(".mp3"))
					{

						FileUtils.renameAudioFile(tempName, name);

						Log.e("On Service", "Downloading Complete");

						sendDataToActivity(true, "surah", name, position, -1);
					}

				}

				dbObj.deleteOneDownload(DBManagerQuran.FLD_ID, index);

				c = dbObj.getAllDownloads();

				if(!c.moveToFirst())
				{
					Log.e("On Service", "stopSelf");
					stopSelf();
				}

				c.close();
				dbObj.close();
			}
			else
			{
				DBManagerQuran dbObj = new DBManagerQuran(getApplicationContext());
				dbObj.open();

				Cursor c = dbObj.getAllDownloads();

				if(c.moveToFirst())
				{
					do
					{
						int id = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_DOWNLOAD_ID));

						if(id == (int) referenceId)
						{
							index = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_ID));
							position = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_SURAH_NO));
							name = c.getString(c.getColumnIndex(DBManagerQuran.FLD_SURAH_NAME));
							tempName = c.getString(c.getColumnIndex(DBManagerQuran.FLD_TEMP_NAME));

							break;
						}

					}
					while (c.moveToNext());
				}

				c.close();

				if(index != -1)
				{
					if(name.contains(".mp3"))
					{
						dbObj.deleteOneDownload(DBManagerQuran.FLD_ID, index);

						c = dbObj.getAllDownloads();

						if(!c.moveToFirst())
						{
							Log.e("On Service", "stopSelf");
							stopSelf();
						}
					}
				}

				c.close();
				dbObj.close();
			}
		}

	};

	private BroadcastReceiver newSurahDownload = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent != null)
			{
				extractValues(intent);
			}
		}
	};

	@Override
	public void onDestroy() {
		cancelChkServiceRunning();

		DBManagerQuran dbObj = new DBManagerQuran(getApplicationContext());
		dbObj.open();

		Cursor c = dbObj.getAllDownloads();

		if(c.moveToFirst())
		{
			do
			{
				int id = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_DOWNLOAD_ID));
				int pos = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_SURAH_NO));
				String tempName = c.getString(c.getColumnIndex(DBManagerQuran.FLD_TEMP_NAME));

				downloadManager.remove(id);
				FileUtils.checkOneAudioFile(getApplicationContext(), tempName, pos);

			}
			while (c.moveToNext());
		}

		c.close();
		dbObj.close();

		unregisterReceiver(newSurahDownload);
		unregisterReceiver(downloadReceiver);

		FileUtils.deleteTempFiles(Constants.rootPathQuran.getAbsolutePath());

		super.onDestroy();

		Log.d("Service", "onDestroy");
	}

	public void sendDataToActivity(boolean status, String from, String name, int position, int reciter) {

		if(status)
		{
			String[] arrNames = getResources().getStringArray(R.array.surah_names);
			 Toast toast = Toast.makeText(ServiceClass.this, getString(R.string.download) + " " + getString(R.string.completed) + "\n" + arrNames[position - 1], Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}

		Intent broadcastIntent = new Intent(Constants.BroadcastActionComplete);
		broadcastIntent.putExtra("STATUS", status);
		broadcastIntent.putExtra("FROM", from);
		broadcastIntent.putExtra("NAME", name);
		broadcastIntent.putExtra("POSITION", position);
		sendBroadcast(broadcastIntent);
	}

	public boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
		{
			if(ServiceClass.class.getName().equals(service.service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}

	private boolean checkDownloadStatus(long id) {

		boolean isSuccessful = false;

		// TODO Auto-generated method stub
		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterById(id);
		Cursor cursor = downloadManager.query(query);
		if(cursor.moveToFirst())
		{
			int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
			int status = cursor.getInt(columnIndex);

			if(status == DownloadManager.STATUS_SUCCESSFUL)
			{
				isSuccessful = true;
			}
		}

		return isSuccessful;
	}
}