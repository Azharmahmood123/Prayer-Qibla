package duas.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.alarms.AlarmReceiverPrayers;
import com.quranreading.helper.Constants;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;
import com.quranreading.qibladirection.SettingsActivity;

import java.io.File;
import java.util.ArrayList;

import duas.db.DuaModel;
import duas.download.service.ServiceDownloadDua;
import duas.fragments.DuasDetailFragment;
import noman.CommunityGlobalClass;

public class DuaDetailsActivity extends AppCompatActivity implements OnCompletionListener, OnClickListener {

    private static final String MP3 = ".mp3";
    public static final int requestDownload = 1;
    public static final String EXTRA_DUA_DATA_OBJ = "dua_object";
    public static final String EXTRA_DUA_ARRAY_LIST = "dualist";
    public static final String EXTRA_DUA_POSITION = "duaPos";
    public static final String EXTRA_DUA_CATEGORY = "duaCategory";

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
    private String audioFile, duaTitle, duaCategory;
    private boolean callCheck = false;
    PhoneStateListener phoneStateListener;
    TelephonyManager telephonyManeger;
    ImageView btnAudio, btnStop;

    private boolean inProcess = false;

    ArrayList<DuaModel> duasList;
    DuaModel mDuaModel;
    int duaPosition = 0;
    ImageView btnShare, btnInfo;
    TextView tvHeading;

    ViewPager vp;
    DuaViewPagerAdapter mDuaViewPagerAdapter;
    boolean isSettings = false;

    int currentPagerPos=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dua_details_activity);

        AnalyticSingaltonClass.getInstance(this).sendScreenAnalytics("Duas_Detail");

        registerReceiver(mAlarmBroadcastReceiver, new IntentFilter(AlarmReceiverPrayers.STOP_SOUND));
        IntentFilter surahDownloadComplete = new IntentFilter(ServiceDownloadDua.ACTION_DUA_DOWNLOAD_COMPLETED);
        registerReceiver(downloadComplete, surahDownloadComplete);

        initializeAds();
        init();

        telephonyCheck();
    }

    private void init() {

        GlobalClass globalObject = (GlobalClass) getApplicationContext();

        LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DuaDetailsActivity.super.onBackPressed();

            }
        });
        tvHeading = (TextView) findViewById(R.id.txt_toolbar);

//        tvHeading.setText(R.string.languages);


        btnAudio = (ImageView) findViewById(R.id.btn_audio_duas);
        btnStop = (ImageView) findViewById(R.id.btn_stop_duas);
        btnShare = (ImageView) findViewById(R.id.btn_share_duas);
        btnInfo = (ImageView) findViewById(R.id.btn_info_duas);


        btnAudio.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnInfo.setOnClickListener(this);

        duasList = (ArrayList<DuaModel>) getIntent().getSerializableExtra(EXTRA_DUA_ARRAY_LIST);
        duaPosition = getIntent().getIntExtra(EXTRA_DUA_POSITION, 0);
        duaCategory = getIntent().getStringExtra(EXTRA_DUA_CATEGORY);
        setDuaData(duaPosition);

        setAdapter(duaPosition);
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

    private void setDuaData(int pos) {
        duaPosition = pos;
        mDuaModel = duasList.get(duaPosition);
        audioFile = mDuaModel.getAudioName() + MP3;
        duaTitle = mDuaModel.getDuaTitle();
        tvHeading.setText(duaCategory);

        initializeAudios();
    }

    public void onBackButtonClick(View v) {
        onBackPressed();
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

    private void telephonyCheck() {
        telephonyManeger = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (mp != null && isAudioFound) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        // Incoming call: Pause Audio
                        if (play == 1) {
                          //  callCheck = true;
                           // mp.pause();
                            onPlayClick();
                        }
                    } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                        // Not in call: Play Audio
                        if (callCheck && mp != null) {
                            callCheck = false;
                         //   mp.start();
                            onPlayClick();
                        }
                    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        // A call is dialing, active or on hold: Pause Audio
                        if (play == 1) {
                           // callCheck = true;
                          //  mp.pause();
                            onPlayClick();
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

    public void initializeAudios() {

        isAudioFound = false;
        inProcess = true;
        audioFilePath = new File(Constants.rootPathDuas.getAbsolutePath(), audioFile);
        if (audioFilePath.exists()) {
            // if(FileUtils.checkAudioFileSize(this, audioFile, surahNumber, reciter))
            // {
            if (mp != null) {
                mp.release();
                mp = null;
            }

            Uri audioUri = Uri.parse(audioFilePath.getPath());
            mp = MediaPlayer.create(DuaDetailsActivity.this, audioUri);
            if (mp != null) {
                mp.setOnCompletionListener(DuaDetailsActivity.this);
                isAudioFound = true;
            } else {
                isAudioFound = false;
            }
            // }
            // else
            // {
            // isAudioFound = false;
            // }
        } else {
            isAudioFound = false;
        }

        inProcess = false;
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
                if (!Constants.rootPathDuas.exists()) {
                    Constants.rootPathDuas.mkdirs();
                    audioFilePath = new File(Constants.rootPathDuas.getAbsolutePath(), audioFile);
                }

                if (!inProcess) {
                    inProcess = true;
                    Intent downloadDialog = new Intent(DuaDetailsActivity.this, DownloadDialogDuas.class);
                    startActivityForResult(downloadDialog, requestDownload);
                }

            } else {
                showShortToast(getResources().getString(R.string.toast_network_error), 500, Gravity.CENTER);
            }
        }
    }

    private void onStopClick() {
        reset();
    }

    private void shareDua() {
        String duaArabic = "", duaTranslation = "", duaTransliteration = "";

        duaTranslation = mDuaModel.getDuaEnglish();
        duaTransliteration = mDuaModel.getDuaTransliteration();
        duaArabic = mDuaModel.getDuaArabic();

        Intent actionIntent = new Intent(Intent.ACTION_SEND);
        actionIntent.setType("text/plain");
        actionIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        actionIntent.putExtra(Intent.EXTRA_TEXT, "Title: " + duaTitle + "\n\n" + duaArabic + " \n" + "." + "\n\n" + duaTranslation + "\n\nFor more duas, Download our free app: \nhttps://play.google.com/store/apps/details?id=" + getPackageName());

        if (!inProcess) {
            inProcess = true;
            startActivity(Intent.createChooser(actionIntent, "Share via"));
        }

    }

    private void additionalInfo() {
        if (!inProcess) {
            inProcess = true;
            String addInfo = "";
            String reference = "";

            addInfo = mDuaModel.getAdditionalInfo();
            reference = mDuaModel.getReference();

            String info;

            // ======================

            if (!addInfo.equals("na")) {
                info = addInfo + "\n" + "\n" + "REFERENCE: " + "\n" + reference;
            } else {
                info = "REFERENCE: " + "\n" + reference;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(info).setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    inProcess = false;
                }
            });

            AlertDialog alert = builder.create();
            alert.setTitle(duaTitle);
            alert.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    inProcess = false;
                }
            });
            alert.show();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (isSettings) {
          if(mDuaViewPagerAdapter !=null)
          {
              setAdapter(currentPagerPos);
          }
        }
        inProcess = false;
        isSettings = false;

        if (((GlobalClass) getApplication()).isPurchase) {
            adImage.setVisibility(View.GONE);
            adview.setVisibility(View.GONE);
        } else {
            startAdsCall();
        }
    }
    public void setAdapter(int duaPosition) {
        vp = (ViewPager) findViewById(R.id.viewpager_duas);
        mDuaViewPagerAdapter = new DuaViewPagerAdapter(getSupportFragmentManager());
        vp.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                reset();
                setDuaData(position);
                currentPagerPos=position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });

        vp.setAdapter(mDuaViewPagerAdapter);
        vp.setCurrentItem(duaPosition);

    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        if (isSettings) {
            if (mp != null && isAudioFound) {
                if (play == 1) {
                    mp.pause();
                    play = 0;
                    btnAudio.setImageResource(R.drawable.play_btn);
                }
            }
        }

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

        if (!inProcess) {
            inProcess = true;
            if (mp != null && isAudioFound) {
                mp.release();
                mp = null;
            }

            super.onBackPressed();
        }
    }

    private void showShortToast(String message, int milliesTime, int gravity) {

        CommunityGlobalClass.getInstance().showShortToast(message, milliesTime, gravity);
    }

    class DuaViewPagerAdapter extends FragmentPagerAdapter {

        public DuaViewPagerAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Fragment getItem(int position) {
            // TODO Auto-generated method stub

            DuaModel data = duasList.get(position);

            Fragment fr = new DuasDetailFragment();
            Bundle b = new Bundle();
            b.putSerializable(EXTRA_DUA_DATA_OBJ, data);
            fr.setArguments(b);

            return fr;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return duasList.size();
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

    private BroadcastReceiver downloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            initializeAudios();
        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub
        reset();

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        switch (v.getId()) {
            case R.id.btn_audio_duas:
                onPlayClick();
                break;
            case R.id.btn_stop_duas:
                onStopClick();
                break;

            case R.id.btn_share_duas:
                shareDua();
                break;

            case R.id.btn_info_duas:
                additionalInfo();
                break;

            default:
                break;
        }

    }

    public void onSettingsDuasClick(View view) {

        if (!inProcess) {
            AnalyticSingaltonClass.getInstance(this).sendEventAnalytics("Settings", "Settings_Duas");
            inProcess = true;
            isSettings = true;
            startActivity(new Intent(this, SettingsActivity.class));
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
