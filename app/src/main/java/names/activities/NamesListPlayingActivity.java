package names.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.alarms.AlarmReceiverPrayers;
import com.quranreading.helper.Constants;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.io.File;
import java.util.ArrayList;

import names.adapters.NamesData;
import names.adapters.NamesListAdapter;
import names.adapters.NamesModel;
import names.download.service.ServiceDownloadNames;
import names.sharedprefs.DownloadingNamesPref;

public class NamesListPlayingActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SeekBar.OnSeekBarChangeListener {

    // google ads
    AdView adview;
    ImageView adImage;
    private static final String LOG_TAG = "Ads";
    private final Handler adsHandler = new Handler();
    private int timerValue = 3000, networkRefreshTime = 10000;

    public static final int requestDownload = 3;

    private int delayIndex = 0, currentPosition = -1;
    private Handler handler = new Handler();

    private boolean inProcess = false;


    private MediaPlayer mp;
    private boolean isAudioFound = false;
    private File audioFilePath;
    private int play = 0;
    private String audioFile = "allah_full.mp3";
    private boolean callCheck = false;
    PhoneStateListener phoneStateListener;
    TelephonyManager telephonyManeger;
    ImageView btnAudio;
    SeekBar seekBarNames;
    TextView tvTotalTime;
    String audioTotalTime = "00:00";
    int totalDuration = 0;
    DownloadingNamesPref downloadingNamesPref;

    private int nameTiming[] = {0, 1310, 3290, 4846, 6343, 8037, 9737, 10950, 12526, 14396, 15766, 17960, 19750, 21210, 23140, 24484, 26334, 28034, 29591, 31351, 32481, 35081, 36672, 38028, 39758, 41158, 42349, 43852, 45502, 46659, 47376, 48706, 50136, 51516, 52916, 54223, 55683, 57213, 58383,
            59563, 60973, 62393, 63503, 65047, 66640, 68273, 69833, 71024, 72324, 73934, 75834, 77074, 78277, 79717, 81217, 82517, 83771, 85201, 86418, 87418, 88718, 89928, 91328, 92208, 93948, 95442, 97198, 99015, 100322, 101729, 103309, 104879, 106489, 107602, 108849, 110509, 112119, 113559,
            115079, 115763, 117303, 118873, 120063, 121393, 123786, 127294, 128620, 130320, 131617, 132487, 134707, 138707, 140367, 141425, 142618, 143735, 145555, 147535, 148535, 1590000};


    ListView gridViewNames;
    // ArrayList<Integer> namesImages = new ArrayList<>();
    NamesListAdapter mGridViewAdapter;
    ArrayList<NamesModel> namesData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_names_playing);

        downloadingNamesPref = new DownloadingNamesPref(this);
        if (downloadingNamesPref.isFirstTimeNames()) {
            showFirstAlert();
            downloadingNamesPref.setFirstTimeNames();
        }

        LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NamesListPlayingActivity.super.onBackPressed();

            }
        });

        initializeAds();

        tvTotalTime = (TextView) findViewById(R.id.tv_names_total_time);

        registerReceiver(mAlarmBroadcastReceiver, new IntentFilter(AlarmReceiverPrayers.STOP_SOUND));
        IntentFilter surahDownloadComplete = new IntentFilter(ServiceDownloadNames.ACTION_NAMES_DOWNLOAD_COMPLETED);
        registerReceiver(downloadComplete, surahDownloadComplete);

        btnAudio = (ImageView) findViewById(R.id.btn_play_names_full);

        gridViewNames = (ListView) findViewById(R.id.listviewNames);
        gridViewNames.setOnItemClickListener(this);

        seekBarNames = (SeekBar) findViewById(R.id.seekBarNames);
        seekBarNames.setMax(98);
        seekBarNames.setEnabled(false);

        seekBarNames.setOnSeekBarChangeListener(this);

        NamesData data = new NamesData(this);
        namesData = data.getNamesData();

        // loadGridImages();
        mGridViewAdapter = new NamesListAdapter(this, namesData);
        gridViewNames.setAdapter(mGridViewAdapter);

        initTelephonyCheck();
        initializeAudios();
    }

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

        if (isNetworkConnected()) {
            this.adview.setVisibility(View.VISIBLE);
        } else {
            this.adview.setVisibility(View.GONE);
        }
        setAdsListener();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (play == 1) {

            currentPosition = position;
            handler.removeCallbacks(sendUpdatesToUI);
            handler.post(sendUpdatesToUI);


            handler.removeCallbacks(sendUpdatesToUI);
            mp.pause();

            delayIndex = currentPosition;
            mGridViewAdapter.hilightListItem(currentPosition);
            gridViewNames.setSelection(currentPosition);
            delayIndex++;

            mp.seekTo(nameTiming[currentPosition]);
            mp.start();
            seekBarNames.setEnabled(true);
            seekBarNames.setProgress(currentPosition);

            handler.removeCallbacks(runnableTimeUpdate);
            handler.post(runnableTimeUpdate);
            handler.removeCallbacks(sendUpdatesToUI);
            handler.postDelayed(sendUpdatesToUI, 0);


        } else {
            Intent intent = new Intent(this, NamesDetailActivity.class);
            intent.putExtra(NamesDetailActivity.EXTRA_NAMES_POSITION, position);
            this.startActivity(intent);
        }
    }


    private final void focusOnView() {

        if (mp != null) {
            int currentDuration = mp.getCurrentPosition();
            if (currentDuration >= nameTiming[delayIndex]) {

                Log.e("Names:", String.valueOf(delayIndex));


//                audioTotalTime = "" + milliSecondsToTimer(totalDuration - currentDuration);
//                tvTotalTime.setText(audioTotalTime);

                currentPosition = delayIndex;
                mGridViewAdapter.hilightListItem(currentPosition);
                gridViewNames.setSelection(currentPosition);
                seekBarNames.setProgress(currentPosition);
                if (delayIndex < 99) {
                    delayIndex++;
                }
            }

        } else {
            handler.removeCallbacks(sendUpdatesToUI);
        }
    }

    public Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            focusOnView();
            handler.postDelayed(this, 0);
        }
    };


    public Runnable runnableTimeUpdate = new Runnable() {
        public void run() {
            if (mp != null) {
                handler.removeCallbacks(this);
                int currentDuration = mp.getCurrentPosition();
                audioTotalTime = "" + milliSecondsToTimer(totalDuration - currentDuration);
                tvTotalTime.setText(audioTotalTime);
                handler.postDelayed(this, 1000);
            }
        }
    };

    public void initializeAudios() {

        isAudioFound = false;
        inProcess = true;
        audioFilePath = new File(Constants.rootPathNames.getAbsolutePath(), audioFile);
        if (audioFilePath.exists()) {
            if (mp != null) {
                mp.release();
                mp = null;
            }

            Uri audioUri = Uri.parse(audioFilePath.getPath());
            mp = MediaPlayer.create(NamesListPlayingActivity.this, audioUri);
            if (mp != null) {

                totalDuration = mp.getDuration();
                audioTotalTime = "" + milliSecondsToTimer(totalDuration);
                tvTotalTime.setText(audioTotalTime);

                mp.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer m) {
                        reset();
                    }
                });
                isAudioFound = true;
            } else {
                isAudioFound = false;
            }
        } else {
            isAudioFound = false;
        }

        inProcess = false;
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public void onBackButtonClick(View v) {
        onBackPressed();
    }

    private void initTelephonyCheck() {
        telephonyManeger = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (mp != null && isAudioFound) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        // Incoming call: Pause Audio
                        if (play == 1) {
                            callCheck = true;
                            mp.pause();
                            handler.removeCallbacks(runnableTimeUpdate);
                        }
                    } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                        // Not in call: Play Audio
                        if (callCheck && mp != null) {
                            callCheck = false;
                            mp.start();
                            handler.removeCallbacks(runnableTimeUpdate);
                            handler.post(runnableTimeUpdate);
                        }
                    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        // A call is dialing, active or on hold: Pause Audio
                        if (play == 1) {
                            callCheck = true;
                            mp.pause();
                            handler.removeCallbacks(runnableTimeUpdate);
                        }
                    }
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        if (telephonyManeger != null) {
            telephonyManeger.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    public void reset() {

        if (mp != null && isAudioFound) {
            if (play == 1) {
                mp.seekTo(0);
                mp.pause();
                seekBarNames.setEnabled(false);
            } else {
                mp.seekTo(0);
            }
        }

        tvTotalTime.setText(audioTotalTime);

        btnAudio.setImageResource(R.drawable.play_btn);
        play = 0;
        delayIndex = 0;
        currentPosition = -1;
        mGridViewAdapter.removeHighlight();
        gridViewNames.post(new Runnable() {
            @Override
            public void run() {
                gridViewNames.setSelection(0);
            }
        });

        seekBarNames.setProgress(0);
        seekBarNames.setEnabled(false);
        handler.removeCallbacks(runnableTimeUpdate);
        handler.removeCallbacks(sendUpdatesAdsToUI);
    }

    public void onPlayClick(View v) {
        if (isAudioFound && !inProcess) {
            if (play == 0 && mp != null) {
                play = 1;
                handler.removeCallbacks(sendUpdatesToUI);
                handler.postDelayed(sendUpdatesToUI, 0);
                handler.removeCallbacks(runnableTimeUpdate);
                handler.post(runnableTimeUpdate);
                mp.start();
                seekBarNames.setEnabled(true);
                btnAudio.setImageResource(R.drawable.pause_btn);

            } else {

                if (play == 1) {
                    handler.removeCallbacks(sendUpdatesToUI);
                    handler.removeCallbacks(runnableTimeUpdate);
                    mp.pause();
                }
//                mGridViewAdapter.removeHighlight();
                play = 0;
                seekBarNames.setEnabled(false);

                btnAudio.setImageResource(R.drawable.play_btn);
            }
        } else {
            if (isNetworkConnected()) {
                if (!Constants.rootPathNames.exists()) {
                    Constants.rootPathNames.mkdirs();
                    audioFilePath = new File(Constants.rootPathNames.getAbsolutePath(), audioFile);
                }

                if (!inProcess) {
                    inProcess = true;
                    Intent downloadDialog = new Intent(NamesListPlayingActivity.this, DownloadDialogNames.class);
                    startActivityForResult(downloadDialog, requestDownload);
                }
            } else {
                showShortToast(getResources().getString(R.string.toast_network_error), 500, Gravity.BOTTOM);
            }
        }
    }

    private BroadcastReceiver mAlarmBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (mp != null && isAudioFound) {
                if (play == 1) {
                    mp.pause();
                    play = 0;
                    seekBarNames.setEnabled(false);
                    btnAudio.setImageResource(R.drawable.play_btn);
                    handler.removeCallbacks(runnableTimeUpdate);
                    handler.removeCallbacks(sendUpdatesAdsToUI);
                }
            }
        }
    };

    private void showShortToast(String message, int milliesTime, int gravity) {

        if (getString(R.string.device).equals("large")) {
            final Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.setGravity(gravity, 0, 0);
            toast.show();
        } else {
            final Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.setGravity(gravity, 0, 0);
            toast.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, milliesTime);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!((GlobalClass) getApplication()).isPurchase) {
            stopAdsCall();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mAlarmBroadcastReceiver);
        unregisterReceiver(downloadComplete);

        reset();

        if (mp != null && isAudioFound) {
            mp.release();
            mp = null;
        }
        if (telephonyManeger != null) {
            telephonyManeger.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        if (!((GlobalClass) getApplication()).isPurchase) {

            destroyAds();
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        handler.removeCallbacks(sendUpdatesToUI);
        if (mp != null) {
            if (mp != null) {
                mp.release();
                mp = null;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {

        super.onResume();
        inProcess = false;

        if (((GlobalClass) getApplication()).isPurchase) {
            adImage.setVisibility(View.GONE);
            adview.setVisibility(View.GONE);

        } else {
            startAdsCall();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (play == 1 && fromUser) {

            currentPosition = progress;
            handler.removeCallbacks(sendUpdatesToUI);
            handler.post(sendUpdatesToUI);

            handler.removeCallbacks(runnableTimeUpdate);
            handler.post(runnableTimeUpdate);

            mp.pause();

            delayIndex = currentPosition;
            mGridViewAdapter.hilightListItem(currentPosition);
            gridViewNames.setSelection(currentPosition);
            delayIndex++;

            mp.seekTo(nameTiming[currentPosition]);
            mp.start();
            seekBarNames.setEnabled(true);

            handler.removeCallbacks(runnableTimeUpdate);
            handler.post(runnableTimeUpdate);
            handler.removeCallbacks(sendUpdatesToUI);
            handler.postDelayed(sendUpdatesToUI, 0);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void showFirstAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("English Language Only");
        builder.setCancelable(false);
        builder.setMessage("App language for 99 names is English");

        builder.setPositiveButton(getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private BroadcastReceiver downloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            initializeAudios();
        }
    };

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
        if (isNetworkConnected()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adview.loadAd(adRequest);
        } else {
            timerValue = networkRefreshTime;
            adsHandler.removeCallbacks(sendUpdatesAdsToUI);
            adsHandler.postDelayed(sendUpdatesAdsToUI, timerValue);
        }
    }

    public void startAdsCall() {
        Log.i(LOG_TAG, "Starts");
        if (isNetworkConnected()) {
            this.adview.setVisibility(View.VISIBLE);
        } else {
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
        switch (errorCode) {
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
