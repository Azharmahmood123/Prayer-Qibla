package names.download.service;

import java.util.Date;
import java.util.TimeZone;

import com.quranreading.helper.Constants;
import com.quranreading.helper.DBManager;
import com.quranreading.qibladirection.R;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import names.sharedprefs.DownloadingNamesPref;

public class ServiceDownloadNames extends Service implements UnzipListenerNames {

	public static final String ACTION_NAMES_DOWNLOAD_COMPLETED = "action.download.names.completed";

	DownloadingNamesPref mDownloadingNamesPref;
	private long downloadReference;
	public DownloadManager downloadManager;

	@Override
	public void onCreate() {
		super.onCreate();
		downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		mDownloadingNamesPref = new DownloadingNamesPref(this);
		IntentFilter surahDownloadFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		registerReceiver(downloadReceiver, surahDownloadFilter);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("Service", "onStartCommand");

		try
		{
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

	public void extractValues(Intent intent) {

		downloadNames();
	}

	private void downloadNames() {
		String url = getLink();

		Uri Download_Uri = Uri.parse(url);
		DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

		// Restrict the types of networks over which this download may proceed.
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

		// Set whether this download may proceed over a roaming connection.
		request.setAllowedOverRoaming(false);

		// Set the title of this download, to be displayed in notifications (if enabled).
		request.setTitle("Downloading . . . ");

		// Set a description of this download, to be displayed in notifications (if enabled)
		request.setDescription(getString(R.string.downloading));

		// Set the local destination for the downloaded file to a path within the application's external files directory
		request.setDestinationInExternalPublicDir(Constants.rootFolderNames, Constants.NAMES_ZIP_TEMP);

		// Enqueue a new download and same the referenceId
		try
		{
			downloadReference = downloadManager.enqueue(request);
			String refId = downloadReference + "";

			mDownloadingNamesPref.setReferenceId(refId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getLink() {

		DBManager db = new DBManager(this);
		db.open();
		String url;
		TimeZone tz = TimeZone.getDefault();
		long timeNow = new Date().getTime();
		double timezone = (double) ((tz.getOffset(timeNow) / 1000) / 60) / 60;
		if(timezone >= 4.0 && timezone <= 13.0) // Asia
		{
			url = db.getUrl(DBManager.FLD_ASIA, DBManager.MODULE_NAMES);
		}
		else if(timezone >= -13.0 && timezone <= -4.0) // US
		{
			url = db.getUrl(DBManager.FLD_US, DBManager.MODULE_NAMES);
		}
		else if(timezone >= -3.5 && timezone <= 3.5) // EU
		{
			url = db.getUrl(DBManager.FLD_EU, DBManager.MODULE_NAMES);
		}
		else
		{
			url = db.getUrl(DBManager.FLD_EU, DBManager.MODULE_NAMES);
		}

		db.close();

		return url;
	}

	private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			// check if the broadcast message is for our Enqueued download
			long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

			if(checkDownloadStatus(referenceId))
			{
				UnZipUtilNames mUnZipUtil = new UnZipUtilNames();
				mUnZipUtil.setListener(ServiceDownloadNames.this);
				mUnZipUtil.execute();
			}
		}
	};

	@Override
	public void onDestroy() {

		String id = mDownloadingNamesPref.getReferenceId();

		if(!id.equals(""))
		{
			downloadManager.remove(Long.parseLong(id));
			mDownloadingNamesPref.clearStoredData();
		}
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

	@Override
	public void unzipStatus(boolean status) {
		// TODO Auto-generated method stub

		String id = mDownloadingNamesPref.getReferenceId();

		if(status)
		{
			Intent intent = new Intent(ACTION_NAMES_DOWNLOAD_COMPLETED);
			sendBroadcast(intent);
		}

		if(!id.equals(""))
		{
			downloadManager.remove(Long.parseLong(id));
			mDownloadingNamesPref.clearStoredData();
		}

		stopSelf();
		Toast.makeText(this, getString(R.string.download) + " " + getString(R.string.completed), Toast.LENGTH_SHORT).show();
	}
}