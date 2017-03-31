package names.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import names.adapters.NamesData;
import names.adapters.NamesPagerAdapter;
import names.download.service.ServiceDownloadNames;
import noman.CommunityGlobalClass;

public class NamesDetailActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    private static final String MP3 = ".mp3";

    // google ads
    AdView adview;
    ImageView adImage;
    private static final String LOG_TAG = "Ads";
    private final Handler adsHandler = new Handler();
    private int timerValue = 3000, networkRefreshTime = 10000;

    private MediaPlayer mp;
    private boolean isAudioFound = false;
    private File audioFilePath;
    private int play = 0;
    private String audioFile;
    private boolean callCheck = false;
    PhoneStateListener phoneStateListener;
    TelephonyManager telephonyManeger;
    ImageView btnAudio, btnStop, btnShare;

    private boolean inProcess = false;

    public static final int requestDownload = 3;
    public static final String EXTRA_NAMES_POSITION = "position";

    private ViewPager viewPager;
    private NamesPagerAdapter namesPageAdapter;
    private GlobalClass globalObject;
    private int namePosition = 0;
    TextView tvHeading;

    NamesData namesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_names_detail);

        registerReceiver(mAlarmBroadcastReceiver, new IntentFilter(AlarmReceiverPrayers.STOP_SOUND));
        IntentFilter surahDownloadComplete = new IntentFilter(ServiceDownloadNames.ACTION_NAMES_DOWNLOAD_COMPLETED);
        registerReceiver(downloadComplete, surahDownloadComplete);

        namePosition = getIntent().getIntExtra(EXTRA_NAMES_POSITION, 0);

        namesData = new NamesData(this);

        initializeAds();
        initialize();
        initTelephonyCheck();


        //Send Screen analytic
        CommunityGlobalClass.getInstance().sendAnalyticsScreen("Allah Detail 4.0");
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

    // ///////////////////////
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

    private void initialize() {
        globalObject = (GlobalClass) getApplicationContext();

        btnAudio = (ImageView) findViewById(R.id.playButon);
        btnStop = (ImageView) findViewById(R.id.stopButton);
        btnShare = (ImageView) findViewById(R.id.shareButton);
        viewPager = (ViewPager) findViewById(R.id.NamesViewpager);

        LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NamesDetailActivity.super.onBackPressed();

            }
        });
        tvHeading = (TextView) findViewById(R.id.txt_toolbar);
//        tvHeading.setSelected(true);

        setNamesData();
        namesPageAdapter = new NamesPagerAdapter(getSupportFragmentManager(), namesData.getNamesSize());
        viewPager.setAdapter(namesPageAdapter);
        viewPager.setCurrentItem(namePosition);

        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                namePosition = arg0;
                reset();
                setNamesData();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    private void setNamesData() {
        tvHeading.setText(namesData.getNameEnglish(namePosition));
        audioFile = "a_" + (namePosition + 1) + MP3;
        initializeAudios();
    }

    public void buttonClick(View view) {

        if (!callCheck) {
            int tag = Integer.parseInt(view.getTag().toString());
            switch (tag) {
                case 1: // play
                    onPlayClick();
                    break;

                case 2: // stop_r
                    reset();
                    break;

                case 3: // share
                    if (!inProcess) {
                        inProcess = true;
                        share();

                        //Send  analytic
                        CommunityGlobalClass.getInstance().sendAnalyticEvent("Allah Single Name","Share");
                    }
                    break;
            }
        }
    }

    private void share() {
        String arabicName = "", name = "", nameMeaning = "", nameDetail = "";

        arabicName = namesData.getNameArabic(namePosition);
        name = namesData.getNameEnglish(namePosition);
        nameMeaning = namesData.getNameMeaning(namePosition);
        nameDetail = namesData.getNameDetails(namePosition);

        Intent actionIntent = new Intent(Intent.ACTION_SEND);
        actionIntent.setType("text/plain");
        actionIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        actionIntent.putExtra(Intent.EXTRA_TEXT,
                "Name Of Allah:\n" + "\n" + "Arabic Name:\n" + arabicName + "\n\n" + "Name:\n" + name + "\n\n" + "Meaning:\n" + nameMeaning + "\n\n" + "Detail:\n" + nameDetail + "\nLearn to recite Allah Names Download:" + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());

        startActivity(Intent.createChooser(actionIntent, "Share via"));

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        if (mp != null && isAudioFound) {
            mp.release();
            mp = null;
        }

        super.onBackPressed();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        reset();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestDownload && resultCode == RESULT_OK) {
            // playAudio();
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
                            onPlayClick();
                         //   callCheck = true;
                          //  mp.pause();
                        }
                    } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                        // Not in call: Play Audio
                        if (callCheck && mp != null) {
                            onPlayClick();
                         //   callCheck = false;
                         //   mp.start();
                        }
                    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        // A call is dialing, active or on hold: Pause Audio
                        if (play == 1) {
                            onPlayClick();
                           // callCheck = true;
                          //  mp.pause();
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
            } else {
                mp.seekTo(0);
            }
        }

        btnAudio.setImageResource(R.drawable.play_btn);
        play = 0;
    }

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
            mp = MediaPlayer.create(NamesDetailActivity.this, audioUri);
            if (mp != null) {
                mp.setOnCompletionListener(NamesDetailActivity.this);
                isAudioFound = true;
            } else {
                isAudioFound = false;
            }
        } else {
            isAudioFound = false;
        }

        inProcess = false;
    }

    private void onPlayClick() {
        if (isAudioFound && !inProcess) {
            if (play == 0 && mp != null) {
                play = 1;

                mp.start();

                btnAudio.setImageResource(R.drawable.pause_btn);
            } else {

                if (play == 1) {
                    mp.pause();
                }

                play = 0;

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
                    Intent downloadDialog = new Intent(NamesDetailActivity.this, DownloadDialogNames.class);
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
                    btnAudio.setImageResource(R.drawable.play_btn);
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

    private BroadcastReceiver downloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            initializeAudios();
        }
    };

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
