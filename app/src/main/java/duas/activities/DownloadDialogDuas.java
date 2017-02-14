package duas.activities;

import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.helper.Constants;
import com.quranreading.qibladirection.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import duas.download.service.ServiceDownloadDua;
import duas.sharedprefs.DuasSharedPref;
import names.activities.DownloadDialogNames;
import quran.helper.FileUtils;

public class DownloadDialogDuas extends Activity {

    // Button btnSurah, btnQuran, btnCancel, btnoky, btnback;
    // LinearLayout btnLayout;
    // TextView tvBody, header;
    String packageName = "com.android.providers.downloads";
    double sizeRequired;

    String msg = null;
    DuasSharedPref mDownloadingDuasPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transperant_layout);
        mDownloadingDuasPref = new DuasSharedPref(this);

        IntentFilter surahDownloadComplete = new IntentFilter(ServiceDownloadDua.ACTION_DUA_DOWNLOAD_COMPLETED);
        registerReceiver(downloadComplete, surahDownloadComplete);

        if (checkDownloadStatus()) {
            if (msg != null) {
                showDownloadProcessingDialog();
            } else {
                msg = getString(R.string.download_msg) + " " + getString(R.string.duas) + "?";
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
        if (!checkDownloadManagerState()) {
            return false;
        }
        String refId = mDownloadingDuasPref.getReferenceId();

        if (!refId.equals("")) {
            String status = FileUtils.checkDownloadStatus(this, Long.parseLong(refId.trim()));
            if (status == FileUtils.CHK_SUCCESSFUL) {
                Toast.makeText(this, R.string.already_downloaded, Toast.LENGTH_SHORT).show();
                finish();
                return false;
            } else if (status == FileUtils.CHK_FAILED) {
                mDownloadingDuasPref.clearStoredData();
            } else if (status == FileUtils.CHK_PAUSED) {
                msg = "";
            } else if (status == FileUtils.CHK_RUNNING) {
                msg = "";
            } else if (status == FileUtils.CHK_PENDING) {
                msg = "";
            }
        }
        return true;
    }

    public boolean checkDownloadManagerState() {
        boolean status = false;

        int state = this.getPackageManager().getApplicationEnabledSetting(packageName);

        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER || state == 4) {
            status = false;
        } else {
            status = true;
        }

        return status;
    }

    public void onDownloadSurah() {

        FileUtils.deleteTempFiles(Constants.rootPathDuas.getAbsolutePath());

        if (!checkDownloadManagerState()) {
            alertMessage();
            return;
        }

        if (isNetworkConnected()) {
            Intent serviceIntent = new Intent(DownloadDialogDuas.this, ServiceDownloadDua.class);
            startService(serviceIntent);
        } else {
            Toast.makeText(DownloadDialogDuas.this, R.string.toast_network_error, Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void alertMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert);
        builder.setMessage("Download Manager Disabled");

        builder.setPositiveButton(getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    // Open the specific App Info page:
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
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
        builder.setMessage(R.string.downloading_in_progress);

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
                AnalyticSingaltonClass.getInstance(DownloadDialogDuas.this).sendEventAnalytics("Downloads 4.0", "Duas Download Ok");
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

            if (intent != null) {
                DownloadDialogDuas.this.finish();
            }
        }
    };

}
