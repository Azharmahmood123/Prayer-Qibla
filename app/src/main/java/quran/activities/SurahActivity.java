package quran.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.alarms.AlarmReceiverPrayers;
import com.quranreading.helper.Constants;
import com.quranreading.listeners.OnSurahDownloadComplete;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;
import com.quranreading.qibladirection.SettingsActivity;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import quran.adapter.AudioTimeXMLParser;
import quran.adapter.XMLParser;
import quran.arabicutils.ArabicUtilities;
import quran.helper.DBManagerQuran;
import quran.helper.FileUtils;
import quran.model.SurahModel;
import noman.sharedpreference.SurahsSharedPref;

public class SurahActivity extends AppCompatActivity implements OnCompletionListener, OnSurahDownloadComplete {

    private static final String RECITER_MP3 = "_a.mp3";
    public static final String KEY_EXTRA_SURAH_NO = "surah_no";
    public static final String KEY_EXTRA_AYAH_NO = "ayah_no";
    public static final int requestDownload = 1;

    private static final int LANGUAGE_Off = 0;
    private static final int LANGUAGE_English_Saheeh = 1;
    private static final int LANGUAGE_English_Pickthal = 2;
    private static final int LANGUAGE_English_Shakir = 3;
    private static final int LANGUAGE_English_Maududi = 4;
    private static final int LANGUAGE_English_Daryabadi = 5;
    private static final int LANGUAGE_English_YusufAli = 6;
    private static final int LANGUAGE_Urdu = 7;
    private static final int LANGUAGE_Spanish = 8;
    private static final int LANGUAGE_French = 9;
    private static final int LANGUAGE_Chinese = 10;
    private static final int LANGUAGE_Persian = 11;
    private static final int LANGUAGE_Italian = 12;
    private static final int LANGUAGE_Dutch = 13;
    private static final int LANGUAGE_Indonesian = 14;
    private static final int LANGUAGE_Melayu = 15;
    private static final int LANGUAGE_Hindi = 16;
    private static final int LANGUAGE_Bangla = 17;
    private static final int LANGUAGE_TURKISH = 18;

    // google ads
    AdView adview;
    ImageView adImage;
    private static final String LOG_TAG = "Ads";
    private final Handler adsHandler = new Handler();
    private int timerValue = 3000, networkRefreshTime = 10000;
    InterstitialAd mInterstitialAd;

    private Runnable mRunnableInterstitial;
    boolean isInterstitialShown = false;
    private int INTERSTITIAL_DELAY = 30000;

    AlertDialog alertGoto;
    private SurahsSharedPref settngPref;
    private MediaPlayer mp;
    private boolean isAudioFound = false;
    private File audioFilePath;
    private ImageView btnAudio, btnRepeat, btnPrevious, btnNext;
    TextView tvHeading;
    private Button btnTransprnt;
    private XmlPullParser xpp;
    private String bismillahText;
    private ProgressBar progressBar;
    private RelativeLayout mainLayout, ayahOptionsLayout;
    private ListView ayahListView;
    private AyaListAdapter customAdapter;
    private final Handler handler = new Handler();

    private static Activity activity = null;

    private boolean isRepeatON = false;

    private int surahNumber = 0, play = 0, delayIndex = 0, topPadding = 15, bookmarkAyahPos = -1, translation = 1;

    // String fontColor, bgColor;
    private int f_index, reciter = 1, prev_Reciter = 0;
    private boolean callCheck = false, chkTransliteration = false, rowClick = false;

    private ArrayList<Integer> timeAyahSurah = new ArrayList<Integer>();
    private String[] surahNames;
    String audioFile, surahName;
    private int[] totalVerses;
    int minLimit, maxLimit;

    List<SurahModel> surahList = new ArrayList<SurahModel>();

    private boolean inProcess = false;

    private TelephonyManager telephonyManeger;
    private PhoneStateListener phoneStateListener;

    boolean isSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surah_quran);

        IntentFilter surahDownloadComplete = new IntentFilter(Constants.BroadcastActionComplete);
        registerReceiver(downloadComplete, surahDownloadComplete);

        activity = this;

        registerReceiver(mAlarmBroadcastReceiver, new IntentFilter(AlarmReceiverPrayers.STOP_SOUND));

        settngPref = new SurahsSharedPref(this);
        surahNames = getResources().getStringArray(R.array.surah_names);
        totalVerses = getResources().getIntArray(R.array.noOfVerses);

        ayahOptionsLayout = (RelativeLayout) findViewById(R.id.ayah_options_layout);
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnTransprnt = (Button) findViewById(R.id.btn_transparent);
        ayahListView = (ListView) findViewById(R.id.listView);
        btnAudio = (ImageView) findViewById(R.id.btn_audio);
        btnRepeat = (ImageView) findViewById(R.id.btn_repeat);
        btnPrevious = (ImageView) findViewById(R.id.btn_previous);
        btnNext = (ImageView) findViewById(R.id.btn_next);
        tvHeading = (TextView) findViewById(R.id.tvHeadingSurah);
        tvHeading.setTypeface(((GlobalClass) getApplication()).faceRobotoR);
        TextView tvGoto = (TextView) findViewById(R.id.goToTxt);
        tvGoto.setTypeface(((GlobalClass) getApplication()).faceRobotoR);

        surahNumber = getIntent().getIntExtra(KEY_EXTRA_SURAH_NO, 1);

        audioFile = surahNumber + RECITER_MP3;
        surahName = surahNames[surahNumber - 1];

        initializeAds();

        ((GlobalClass) getApplication()).ayahPos = getIntent().getIntExtra(KEY_EXTRA_AYAH_NO, 0);

        ayahListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (ayahOptionsLayout.getVisibility() == View.VISIBLE) {
                    bookmarkAyahPos = position;
                }
                chkSelection(position, false);
            }
        });

        ayahListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                bookmarkAyahPos = position;
                // ayahOptionsLayout.setVisibility(View.VISIBLE);
                customAdapter.showAyahMenuOptions(bookmarkAyahPos);
                chkSelection(position, false);
                return true;
            }
        });

        telephonyCheck();

        AnalyticSingaltonClass.getInstance(this).sendScreenAnalytics("Quran Playing Screen");
        startAsyncTask(false);
    }

    public void onSettingsSurahClick(View veiw) {
        if (!inProcess) {
            AnalyticSingaltonClass.getInstance(this).sendEventAnalytics("Settings", "Settings_Surah");
            inProcess = true;
            isSettings = true;
            startActivity(new Intent(this, SettingsActivity.class));
        }
    }

    private void telephonyCheck() {
        telephonyManeger = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (mp != null && isAudioFound) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        // Incoming call: Pause Audio
                        handler.removeCallbacks(sendUpdatesToUI);
                        if (play == 1) {
                            callCheck = true;
                            handler.removeCallbacks(sendUpdatesToUI);
                            mp.pause();
                        }
                    } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                        // Not in call: Play Audio
                        if (callCheck && mp != null) {
                            callCheck = false;
                            if (play == 1) {
                                handler.removeCallbacks(sendUpdatesToUI);
                                handler.postDelayed(sendUpdatesToUI, 0);
                                mp.start();
                            }
                        }
                    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        // A call is dialing, active or on hold: Pause Audio
                        handler.removeCallbacks(sendUpdatesToUI);
                        if (play == 1) {
                            callCheck = true;
                            handler.removeCallbacks(sendUpdatesToUI);
                            mp.pause();
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

    private void initializeAds() {
        adview = (AdView) findViewById(R.id.adView);
        adImage = (ImageView) findViewById(R.id.adimg);
        adImage.setVisibility(View.GONE);
        adview.setVisibility(View.GONE);

        if (!((GlobalClass) getApplication()).isPurchase) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial));

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                }
            });

            requestNewInterstitial();

            if (isNetworkConnected()) {
                this.adview.setVisibility(View.VISIBLE);
            } else {
                this.adview.setVisibility(View.GONE);
            }
            setAdsListener();
        }

        mRunnableInterstitial = new Runnable() {

            @Override
            public void run() {
                isInterstitialShown = true;
                showInterstitialAd();
            }
        };
    }

    private void requestNewInterstitial() {

        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void showInterstitialAd() {
        if (!((GlobalClass) getApplication()).isPurchase) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }

    public void initializeSettings() {
        int fontSize_A_1[] = {};
        int fontSize_A_2[] = {};

        String device = getResources().getString(R.string.device);
        isRepeatON = settngPref.isRepeatSurah();

        chkTransliteration = settngPref.isTransliteration();
        translation = settngPref.getTranslationIndex();
        f_index = Constants.ARABIC_TEXT_SIZE_INDEX;
        reciter = settngPref.getReciter();

        if (device.equals("small")) {
            topPadding = 40;
            fontSize_A_1 = ((GlobalClass) getApplication()).fontSize_A_small;
            fontSize_A_2 = ((GlobalClass) getApplication()).fontSize_A_small_1;

            ((GlobalClass) getApplication()).font_size_eng = ((GlobalClass) getApplication()).fontSize_E_small[f_index];
        } else if (device.equals("medium")) {
            topPadding = 50;
            fontSize_A_1 = ((GlobalClass) getApplication()).fontSize_A_med;
            fontSize_A_2 = ((GlobalClass) getApplication()).fontSize_A_med_1;

            ((GlobalClass) getApplication()).font_size_eng = ((GlobalClass) getApplication()).fontSize_E_med[f_index];
        } else if (device.equals("large")) {
            topPadding = 30;
            fontSize_A_1 = ((GlobalClass) getApplication()).fontSize_A_large;
            fontSize_A_2 = ((GlobalClass) getApplication()).fontSize_A_large_1;

            ((GlobalClass) getApplication()).font_size_eng = ((GlobalClass) getApplication()).fontSize_E_large[f_index];
        }

        ((GlobalClass) getApplication()).ayahPadding = topPadding;
        ((GlobalClass) getApplication()).font_size_eng = ((GlobalClass) getApplication()).font_size_eng;
        ((GlobalClass) getApplication()).font_size_arabic = fontSize_A_1[f_index];

        ((GlobalClass) getApplication()).isTransliteration = chkTransliteration;
        if (translation > 0) {
            ((GlobalClass) getApplication()).isTranslation = false;
        } else {
            ((GlobalClass) getApplication()).isTranslation = true;
        }
    }

    public void onBackButtonClick(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        if (!inProcess) {
            inProcess = true;
            handler.removeCallbacks(sendUpdatesToUI);
            if (mp != null && isAudioFound) {
                mp.release();
                mp = null;
            }

            super.onBackPressed();
        }
    }

    public void initializaSurahData() {

        setNextPreiousButtons();
        surahList.clear();

        settngPref.setLastReadSurah(surahNumber);

        // Getting Arabic Ayas
        xpp = this.getResources().getXml(R.xml.quran_uthmani);
        ArrayList<String> arabicList = new ArrayList<String>();
        arabicList = XMLParser.getTranslatedAyaList(this, xpp, surahNumber, getString(R.string.bismillah));

        // Getting English Translation
        getTranslation();
        ArrayList<String> translationList = new ArrayList<String>();
        translationList = XMLParser.getTranslatedAyaList(this, xpp, surahNumber, bismillahText);

        // Gating English Pronunciation
        xpp = this.getResources().getXml(R.xml.english_transliteration);
        ArrayList<String> transliterationList = new ArrayList<String>();
        transliterationList = XMLParser.getTranslatedAyaList(this, xpp, surahNumber, "Bismi Allahi arrahmani arraheem");

        for (int pos = 0; pos < arabicList.size(); pos++) {
            SurahModel model = new SurahModel(-1, arabicList.get(pos), translationList.get(pos), transliterationList.get(pos));
            surahList.add(model);
        }

        chkSurahBookmarks();

        arabicList.clear();
        translationList.clear();
        transliterationList.clear();

        if (surahNumber == 9) {
            surahList.remove(0);
        }
    }

    private void setNextPreiousButtons() {
        if (surahNumber == 1) {
            btnPrevious.setImageResource(R.drawable.previous);
            btnNext.setImageResource(R.drawable.next_bg);
        } else if (surahNumber == 114) {
            btnPrevious.setImageResource(R.drawable.previous_bg);
            btnNext.setImageResource(R.drawable.next);
        } else {
            btnPrevious.setImageResource(R.drawable.previous_bg);
            btnNext.setImageResource(R.drawable.next_bg);
        }
    }

    public void chkSurahBookmarks() {
        DBManagerQuran dbObj = new DBManagerQuran(this);
        dbObj.open();

        int id = -1, ayahPos = -1;

        Cursor c = dbObj.getOneBookmark(surahNumber);

        if (c.moveToFirst()) {
            do {
                id = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_ID));
                ayahPos = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_AYAH_NO));

                if (surahNumber == 9) {
                    ayahPos++;
                }

                SurahModel model = surahList.get(ayahPos);
                model.setBookMarkId(id);

                surahList.set(ayahPos, model);

            }
            while (c.moveToNext());
        }

        c.close();
        dbObj.close();
    }

    public void initializeAudios() {
        isAudioFound = false;
        inProcess = true;
        audioFilePath = new File(Constants.rootPathQuran.getAbsolutePath(), audioFile);
        if (audioFilePath.exists()) {
            if (FileUtils.checkAudioFileSize(this, audioFile, surahNumber, reciter)) {
                if (mp != null) {
                    mp.release();
                    mp = null;
                }

                int audioTimings;

                if (reciter == 0) {
                    audioTimings = R.xml.audio_timings_afasy;
                } else if (reciter == 1) {
                    audioTimings = R.xml.audio_timings_afasy;
                } else {
                    audioTimings = R.xml.audio_timings_afasy;
                }

                timeAyahSurah.clear();
                xpp = this.getResources().getXml(audioTimings);
                timeAyahSurah = AudioTimeXMLParser.getTranslatedAyaList(this, xpp, surahNumber);

                Uri audioUri = Uri.parse(audioFilePath.getPath());
                mp = MediaPlayer.create(SurahActivity.this, audioUri);
                if (mp != null) {
                    mp.setOnCompletionListener(SurahActivity.this);
                    mp.seekTo(timeAyahSurah.get(0));
                    isAudioFound = true;
                } else {
                    isAudioFound = false;
                }
            } else {
                isAudioFound = false;
            }
        } else {
            isAudioFound = false;
        }

        inProcess = false;
    }

    public void onPlayClick(View view) {
        if (isAudioFound && !inProcess) {
            if (play == 0 && mp != null) {
                play = 1;

                if (rowClick) {
                    rowClick = false;
                    mp.seekTo(timeAyahSurah.get(delayIndex));
                    mp.start();
                    ayahListView.setSelection(delayIndex);
                    delayIndex++;
                } else {
                    mp.start();
                }

                handler.removeCallbacks(sendUpdatesToUI);
                handler.postDelayed(sendUpdatesToUI, 0);

                btnAudio.setImageResource(R.drawable.pause_btn);
            } else {
                handler.removeCallbacks(sendUpdatesToUI);

                if (play == 1) {
                    mp.pause();
                }

                play = 0;

                btnAudio.setImageResource(R.drawable.play_btn);
            }
        } else {
            if (isNetworkConnected()) {
                if (!Constants.rootPathQuran.exists()) {
                    Constants.rootPathQuran.mkdirs();
                    audioFilePath = new File(Constants.rootPathQuran.getAbsolutePath(), audioFile);
                }

                if (!inProcess) {
                    inProcess = true;
                    Intent downloadDialog = new Intent(SurahActivity.this, DownloadDialogQuran.class);
                    downloadDialog.putExtra("SURAHNAME", surahName);
                    downloadDialog.putExtra("POSITION", surahNumber);
                    downloadDialog.putExtra("ANAME", audioFile);
                    downloadDialog.putExtra("RECITER", reciter);
                    startActivityForResult(downloadDialog, requestDownload);
                }
            } else {
                showShortToast(getResources().getString(R.string.toast_network_error), 500, Gravity.BOTTOM);
            }
        }
    }

    public void onRepeatClick(View view) {
        if (isRepeatON) {
            isRepeatON = false;
            btnRepeat.setImageResource(R.drawable.bg_repeat_off);

            showShortToast(getResources().getString(R.string.repeat_off), 500, Gravity.BOTTOM);
        } else {
            isRepeatON = true;
            btnRepeat.setImageResource(R.drawable.bg_repeat_on);

            showShortToast(getResources().getString(R.string.repeat_on), 500, Gravity.BOTTOM);
        }
        settngPref.setRepeatSurah(isRepeatON);
    }

    public void onStopClick(View view) {
        reset();
    }

    public void reset() {
        handler.removeCallbacks(sendUpdatesToUI);
        if (ayahOptionsLayout.getVisibility() == View.VISIBLE) {
            bookmarkAyahPos = 0;
        }
        if (mp != null && isAudioFound) {
            if (play == 1) {
                mp.seekTo(timeAyahSurah.get(0));
                mp.pause();
            }else
            {
                mp.seekTo(timeAyahSurah.get(0));
            }
        }

        hideGotoDialog();
        ((GlobalClass) getApplication()).ayahPos = 0;
        customAdapter.notifyDataSetChanged();
        btnAudio.setImageResource(R.drawable.play_btn);
        delayIndex = 0;
        ayahListView.setSelection(0);
        play = 0;
    }

    private void hideGotoDialog() {
        if (alertGoto != null) {
            if (alertGoto.isShowing()) {
                hideKeyboard();
                alertGoto.dismiss();
            }
        }
    }

    private void hideKeyboard() {
        View view = SurahActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
        hideKeyboardForce();
    }

    private final void focusOnView() {
        if (mp != null) {
            if (mp.getCurrentPosition() >= timeAyahSurah.get(delayIndex)) {
                Log.v(String.valueOf(mp.getCurrentPosition()), String.valueOf(timeAyahSurah.get(delayIndex)) + "---" + String.valueOf(delayIndex));

                ((GlobalClass) getApplication()).ayahPos = delayIndex;
                bookmarkAyahPos = delayIndex;
                customAdapter.notifyDataSetChanged();
                ayahListView.setSelection(((GlobalClass) getApplication()).ayahPos);
                delayIndex++;

                settngPref.setLastRead(((GlobalClass) getApplication()).ayahPos);

            }
        } else {
            handler.removeCallbacks(sendUpdatesToUI);
        }
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            focusOnView();
            handler.postDelayed(this, 0);
        }
    };

    public void onTransliteration(View view) {
        if (((GlobalClass) getApplication()).isTransliteration) {
            ((GlobalClass) getApplication()).isTransliteration = false;
            customAdapter.notifyDataSetChanged();
        } else {
            ((GlobalClass) getApplication()).isTransliteration = true;
            customAdapter.notifyDataSetChanged();
        }
    }

	/*
     * public void onLastRead(View view) { if(lastRead == -1) { Toast.makeText(getBaseContext(), R.string.last_read_not_saved, Toast.LENGTH_SHORT).show(); } else { chkSelection(lastRead); ayahListView.setSelection(lastRead); } }
	 */

    public void chkSelection(int index, boolean isSelection) {
        if (mp != null && isAudioFound) {
            if (play == 1) {
                handler.removeCallbacks(sendUpdatesToUI);
                mp.pause();

                delayIndex = index;
                play = 1;

                ((GlobalClass) getApplication()).ayahPos = delayIndex;
                customAdapter.notifyDataSetChanged();
                if (isSelection) {
                    ayahListView.setSelection(((GlobalClass) getApplication()).ayahPos);
                }
                delayIndex++;

                mp.seekTo(timeAyahSurah.get(index));
                mp.start();

                handler.removeCallbacks(sendUpdatesToUI);
                handler.postDelayed(sendUpdatesToUI, 0);
            } else {
                rowClick = true;
                delayIndex = index;
                ((GlobalClass) getApplication()).ayahPos = index;
                customAdapter.notifyDataSetChanged();
                // ayahListView.setSelection(((GlobalClass) getApplication()).ayahPos);
            }
        } else {
            rowClick = true;
            delayIndex = index;
            ((GlobalClass) getApplication()).ayahPos = index;
            customAdapter.notifyDataSetChanged();
            // ayahListView.setSelection(((GlobalClass) getApplication()).ayahPos);
        }
        if (isSelection) {
            ayahListView.setSelection(((GlobalClass) getApplication()).ayahPos);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub

        if (surahNumber == 114) {
            reset();
            settngPref.setLastRead(((GlobalClass) getApplication()).ayahPos);
        } else {
            reset();
            settngPref.setLastRead(((GlobalClass) getApplication()).ayahPos);
            playNextSurah(true);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        inProcess = false;
        if (((GlobalClass) getApplication()).isPurchase) {
            adImage.setVisibility(View.GONE);
            adview.setVisibility(View.GONE);
        } else {
            if (!isInterstitialShown) {
                // handler.removeCallbacks(mRunnableInterstitial);
                // handler.postDelayed(mRunnableInterstitial, INTERSTITIAL_DELAY);
            } else {
                // handler.removeCallbacks(mRunnableInterstitial);
            }

            startAdsCall();
        }

        if (isSettings) {
            isSettings = false;
            initializeSettings();
            initializaSurahData();
            customAdapter.notifyDataSetChanged();
        }

        if (mp != null && isAudioFound) {
            if (play == 1) {
                handler.removeCallbacks(sendUpdatesToUI);
                handler.postDelayed(sendUpdatesToUI, 0);
            }
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        hideKeyboard();
        super.onPause();

        if (isSettings) {
            if (mp != null && isAudioFound) {
                if (play == 1) {
                    handler.removeCallbacks(sendUpdatesToUI);
                    mp.pause();
                    play = 0;
                    btnAudio.setImageResource(R.drawable.play_btn);
                }
            }
        }

        ayahOptionsLayout.setVisibility(View.GONE);
        settngPref.setLastRead(((GlobalClass) getApplication()).ayahPos);

        if (!((GlobalClass) getApplication()).isPurchase) {
            stopAdsCall();
            // handler.removeCallbacks(mRunnableInterstitial);
        }
    }

    @Override
    protected void onDestroy() {

        if (!((GlobalClass) getApplication()).isPurchase) {
            destroyAds();
            if (mInterstitialAd != null)
                mInterstitialAd.setAdListener(null);
            mInterstitialAd = null;
        }

        handler.removeCallbacks(sendUpdatesToUI);
        unregisterReceiver(downloadComplete);
        reset();

        if (mp != null && isAudioFound) {
            mp.release();
            mp = null;
        }

        unregisterReceiver(mAlarmBroadcastReceiver);

        if (telephonyManeger != null) {
            telephonyManeger.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        super.onDestroy();
    }

    // ////////////////////// Async Function to execute long process ///////////////////////

    public void startAsyncTask(boolean isAutoPlay) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            AsyncTaskHandler task = new AsyncTaskHandler();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, isAutoPlay);
        } else {
            AsyncTaskHandler task = new AsyncTaskHandler();
            task.execute(isAutoPlay);
        }

    }

    private class AsyncTaskHandler extends AsyncTask<Boolean, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
            btnTransprnt.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Boolean... values) {
            boolean isautoPlay = values[0];
            try {
                audioFile = surahNumber + RECITER_MP3;
                surahName = surahNames[surahNumber - 1];

                initializeSettings();
                if (prev_Reciter != reciter)
                    initializeAudios();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return isautoPlay;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            bookmarkAyahPos = -1;
            ayahOptionsLayout.setVisibility(View.GONE);

            initializaSurahData();

            setNextPreiousButtons();
            // Passing Values to ListAdapter
            customAdapter = new AyaListAdapter(SurahActivity.this, surahList, surahNumber, translation);
            ayahListView.setAdapter(customAdapter);
            tvHeading.setText(surahName);

            if (isRepeatON) {
                btnRepeat.setImageResource(R.drawable.bg_repeat_on);
            } else {
                btnRepeat.setImageResource(R.drawable.bg_repeat_off);
            }

            if (prev_Reciter != reciter && ((GlobalClass) getApplication()).ayahPos <= 0) {
                bookmarkAyahPos = -1;
                delayIndex = 0;
                ayahListView.setSelection(0);
            } else {
                delayIndex = ((GlobalClass) getApplication()).ayahPos;
                bookmarkAyahPos = delayIndex;
                ayahListView.setSelection(((GlobalClass) getApplication()).ayahPos);
                chkSelection(delayIndex, true);
            }

            mainLayout.setBackgroundColor(Color.parseColor("#E1E8ED"));
            progressBar.setVisibility(View.GONE);
            btnTransprnt.setVisibility(View.GONE);
            if (result) {
                onPlayClick(null);
            }
        }
    }

    private void getTranslation() {

        switch (translation) {

            case LANGUAGE_Off: {
                xpp = this.getResources().getXml(R.xml.english_translation);
                bismillahText = getResources().getString(R.string.bismillahTextEngSaheeh);
            }
            break;

            case LANGUAGE_English_Saheeh: {
                xpp = this.getResources().getXml(R.xml.english_translation);
                bismillahText = getResources().getString(R.string.bismillahTextEngSaheeh);
            }
            break;

            case LANGUAGE_English_Pickthal: {
                xpp = this.getResources().getXml(R.xml.eng_translation_pickthal);
                bismillahText = getResources().getString(R.string.bismillahTextEngPickthal);
            }
            break;

            case LANGUAGE_English_Shakir: {
                xpp = this.getResources().getXml(R.xml.eng_translation_shakir);
                bismillahText = getResources().getString(R.string.bismillahTextEngShakir);
            }
            break;

            case LANGUAGE_English_Maududi: {
                xpp = this.getResources().getXml(R.xml.eng_translation_maududi);
                bismillahText = getResources().getString(R.string.bismillahTextEngMadudi);
            }
            break;

            case LANGUAGE_English_Daryabadi: {
                xpp = this.getResources().getXml(R.xml.eng_translation_daryabadi);
                bismillahText = getResources().getString(R.string.bismillahTextEngDarayabadi);
            }
            break;

            case LANGUAGE_English_YusufAli: {
                xpp = this.getResources().getXml(R.xml.eng_translation_yusufali);
                bismillahText = getResources().getString(R.string.bismillahTextEngYusaf);
            }
            break;

            case LANGUAGE_Urdu: {
                xpp = this.getResources().getXml(R.xml.urdu_translation_jhalandhry);
                bismillahText = getResources().getString(R.string.bismillahTextUrdu);
            }
            break;

            case LANGUAGE_Spanish: {
                xpp = this.getResources().getXml(R.xml.spanish_cortes_trans);
                bismillahText = getResources().getString(R.string.bismillahTextSpanishCortes);
            }
            break;

            case LANGUAGE_French: {
                xpp = this.getResources().getXml(R.xml.french_trans);
                bismillahText = getResources().getString(R.string.bismillahTextFrench);
            }
            break;

            case LANGUAGE_Chinese: {
                xpp = this.getResources().getXml(R.xml.chinese_trans);
                bismillahText = getResources().getString(R.string.bismillahTextChinese);
            }
            break;

            case LANGUAGE_Persian: {
                xpp = this.getResources().getXml(R.xml.persian_ghoomshei_trans);
                bismillahText = getResources().getString(R.string.bismillahTextPersianGhommshei);
            }
            break;

            case LANGUAGE_Italian: {
                xpp = this.getResources().getXml(R.xml.italian_trans);
                bismillahText = getResources().getString(R.string.bismillahTextItalian);
            }
            break;

            case LANGUAGE_Dutch: {
                xpp = this.getResources().getXml(R.xml.dutch_trans_keyzer);
                bismillahText = getResources().getString(R.string.bismillahTextDutchKeyzer);
            }
            break;

            case LANGUAGE_Indonesian: {
                xpp = this.getResources().getXml(R.xml.indonesian_bhasha_trans);
                bismillahText = getResources().getString(R.string.bismillahTextIndonesianBahasha);
            }
            break;

            case LANGUAGE_Melayu: {
                xpp = this.getResources().getXml(R.xml.malay_basmeih);
                bismillahText = getResources().getString(R.string.bismillahTextIndonesianBahasha);
            }
            break;

            case LANGUAGE_Hindi: {
                xpp = this.getResources().getXml(R.xml.hindi_suhel_farooq_khan_and_saifur_rahman_nadwi);
                bismillahText = getResources().getString(R.string.bismillahTextHindi);
            }
            break;

            case LANGUAGE_Bangla: {
                xpp = this.getResources().getXml(R.xml.bangali_zohurul_hoque);
                bismillahText = getResources().getString(R.string.bismillahTextBengali);
            }
            break;

            case LANGUAGE_TURKISH: {
                xpp = this.getResources().getXml(R.xml.turkish_diyanet_isleri);
                bismillahText = getResources().getString(R.string.bismillahTextTurkish);
            }
            break;

            default: {
                xpp = this.getResources().getXml(R.xml.english_translation);
                bismillahText = getResources().getString(R.string.bismillahTextEngSaheeh);
            }
        }
    }

    public static void finishActivity() {
        if (activity != null) {
            activity.finish();
        }
    }

    public void openGoTo(View v) {
        if (!inProcess) {
            inProcess = true;
            maxLimit = totalVerses[surahNumber - 1];

            // For Surah Taubah
            if (surahNumber == 9 || surahNumber == 1) {
                minLimit = 1;
            } else {
                minLimit = 0;
            }

            final String versesCount = "" + maxLimit;
            String hint = getResources().getString(R.string.txt_enter_between_0_83).replaceAll("0-83", minLimit + "-" + maxLimit);

            final EditText input = new EditText(this);
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(versesCount.length());
            input.setFilters(FilterArray);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint(hint);

            alertGoto = new AlertDialog.Builder(this).setCancelable(false).setTitle(R.string.goto_ayah).setMessage(R.string.txt_enter_ayah_number).setView(input).setPositiveButton(getString(R.string.okay), null).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                }
            }).create();

            alertGoto.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {

                    Button b = alertGoto.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            String value = input.getText().toString().trim();

                            if (value.length() > 0 && value.length() <= versesCount.length()) {
                                int temp = Integer.parseInt(value);

                                if (temp < minLimit || temp > maxLimit) {
                                    input.setText("");
                                    showShortToast(getString(R.string.txt_enter_between_0_83).replaceAll("0-83", minLimit + "-" + versesCount), 1000, Gravity.CENTER);
                                } else {
                                    if (surahNumber == 9 || surahNumber == 1) {
                                        temp = temp - 1;
                                    }
                                    bookmarkAyahPos = temp;
                                    chkSelection(temp, true);
                                    ayahListView.setSelection(temp);
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                    alertGoto.dismiss();
                                }
                            } else {
                                input.setText("");
                                showShortToast(getString(R.string.txt_enter_between_0_83).replaceAll("0-83", minLimit + "-" + versesCount), 1000, Gravity.CENTER);
                            }
                        }
                    });
                }
            });

            alertGoto.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    // TODO Auto-generated method stub

                    inProcess = false;
                }
            });

            alertGoto.show();
            input.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private void showShortToast(String message, int milliesTime, int gravity) {

        if (getString(R.string.device).equals("large")) {
            final Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            final Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
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
    public void onSurahDownloadComplete(boolean status) {
        // TODO Auto-generated method stub
        isAudioFound = status;

        if (status) {
            initializeAudios();
        }
    }

    private BroadcastReceiver mAlarmBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (mp != null && isAudioFound) {
                if (play == 1) {
                    handler.removeCallbacks(sendUpdatesToUI);
                    mp.pause();
                    play = 0;
                    btnAudio.setImageResource(R.drawable.play_btn);
                }
            }
        }
    };

    public void onPreviousClick(View view) {

        if (!inProcess)
            playPrevSurah(false);
    }

    public void onNextClick(View view) {
        if (!inProcess)
            playNextSurah(false);
    }

    private void playPrevSurah(boolean isAutoPlay) {

        if (surahNumber == 1) {
        } else {
            if (!((GlobalClass) getApplication()).isPurchase) {
                isInterstitialShown = false;
                // handler.removeCallbacks(mRunnableInterstitial);
                // handler.postDelayed(mRunnableInterstitial, INTERSTITIAL_DELAY);
            }
            surahNumber--;
            reset();
            startAsyncTask(isAutoPlay);

        }
    }

    private void playNextSurah(boolean isAutoPlay) {

        if (surahNumber == 114) {
        } else {
            if (!((GlobalClass) getApplication()).isPurchase) {
                isInterstitialShown = false;
                // handler.removeCallbacks(mRunnableInterstitial);
                // handler.postDelayed(mRunnableInterstitial, INTERSTITIAL_DELAY);
            }

            surahNumber++;
            reset();
            startAsyncTask(isAutoPlay);
        }
    }

    public void onClickAyahOptions(View v) {

        switch (v.getId()) {

            case R.id.btn_ayah_share: {
                String arabic = surahList.get(bookmarkAyahPos).getArabicAyah();
                String trans = surahList.get(bookmarkAyahPos).getTranslation();

                String bodyText = "-------------------------------------------\nQibla Connect� - Download Here: \nhttps://play.google.com/store/apps/details?id=com.quranreading.qibladirection";

                String aya = surahName + "\n\n" + arabic + "\n\n" + trans + "\n\n" + bodyText;

                shareMessage(getString(R.string.app_name), aya);

                sendAnalyticEvent("Share_Ayah");
            }
            break;
            case R.id.btn_bookmark: {
                addRemoveSurahBookmarks(surahNumber, bookmarkAyahPos);
                sendAnalyticEvent("Bookmark_Ayah");
            }
            break;
            case R.id.btn_close: {
                bookmarkAyahPos = -1;
                ayahOptionsLayout.setVisibility(View.GONE);
            }
        }
    }

    private void shareMessage(String subject, String body) {

        // if(saveShareImage())
        // {
        // shareAppWithAppIcon(subject, body);
        // }
        // else
        // {
        if (!inProcess) {
            inProcess = true;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            shareIntent.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
        // }
    }

    private void shareAppWithAppIcon(String subject, String body) {
        String fileName = "ic_launcher.png";
        String completePath = Environment.getExternalStorageDirectory() + "/" + fileName;

        File file = new File(completePath);
        Uri imageUri = Uri.fromFile(file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        // shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

        if (!inProcess) {
            inProcess = true;
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
    }

    private boolean saveShareImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        File sd = Environment.getExternalStorageDirectory();
        String fileName = "ic_launcher.png";
        File dest = new File(sd, fileName);
        try {
            FileOutputStream out;
            out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public void addRemoveSurahBookmarks(int surahNo, int ayahNo) {

        DBManagerQuran dbObj = new DBManagerQuran(this);
        dbObj.open();

        SurahModel model = surahList.get(ayahNo);
        int id = model.getBookMarkId();

        if (id == -1) {
            id = (int) dbObj.addBookmark(surahName, surahNo, ayahNo);
            model.setBookMarkId(id);
            showShortToast(getResources().getString(R.string.added_to_bookmarks), 500, Gravity.CENTER);
        } else {
            dbObj.deleteOneBookmark(id);
            model.setBookMarkId(-1);
            showShortToast(getResources().getString(R.string.removed_from_bookmark), 500, Gravity.CENTER);
        }

        surahList.set(ayahNo, model);
        customAdapter.notifyDataSetChanged();

        dbObj.close();
    }

    // public void showToast(String msg) {
    // Toast toast = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT);
    // toast.setGravity(Gravity.CENTER, 0, 0);
    // toast.show();
    // }

    private void hideKeyboardForce() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private BroadcastReceiver downloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                int position;
                boolean status = false;
                String from = "", name = "";

                status = intent.getBooleanExtra("STATUS", false);
                from = intent.getStringExtra("FROM");
                name = intent.getStringExtra("NAME");
                position = intent.getIntExtra("POSITION", -1);
                // reciter = intent.getIntExtra("RECITER", -1);

                if (status) {
                    if (position == surahNumber) {
                        initializeAudios();
                    }
                }
            }

        }
    };

    private void sendAnalyticEvent(String eventAction) {
        AnalyticSingaltonClass.getInstance(this).sendEventAnalytics("Ayah Options", eventAction);
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

    ////////////////////////////
    ////////////////////////////////////
    //////////////////////////////////////////
    //////////////////////////////////////////////
    ///////////////////////////////////////////

    public class AyaListAdapter extends BaseAdapter {
        private Context mContext;
        private List<SurahModel> surahList;
        int surahPosition;
        String device;
        private int transPos = 1;
        private int menuPosition = -1;

        // private static final String FONTS_LOCATION_PATH = "AnjaliOldLipi.ttf";

        public AyaListAdapter(Context context, List<SurahModel> surahList, int surahPosition, int transPosition) {
            this.mContext = context;
            this.surahList = surahList;
            this.surahPosition = surahPosition;
            device = mContext.getResources().getString(R.string.device);
            transPos = transPosition;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return surahList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return surahList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        /* private view holder class */
        private class ViewHolder {
            ImageView bookmarkImg;
            TextView tvAyahNo;
            TextView tvArabic;
            TextView tvTransliteration;
            TextView tvTranslation;
            LinearLayout ayahRow;
            LinearLayout ayahNo;
            LinearLayout menuOptionsLayout;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            int id = surahList.get(position).getBookMarkId();
            String arabic = surahList.get(position).getArabicAyah();
            // String arabicAyah = ArabicUtilities.reshapeSentence(arabic);
            String translation = surahList.get(position).getTranslation();
            String transliteration = surahList.get(position).getTransliteration();

            ViewHolder holder = null;
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.row_ayah, null);

                holder = new ViewHolder();

                holder.bookmarkImg = (ImageView) convertView.findViewById(R.id.img_bookmark);
                holder.tvAyahNo = (TextView) convertView.findViewById(R.id.tv_ayah_no);
                holder.tvArabic = (TextView) convertView.findViewById(R.id.tv_ayah);
                holder.tvTranslation = (TextView) convertView.findViewById(R.id.text_translation);
                holder.tvTransliteration = (TextView) convertView.findViewById(R.id.text_transliteration);
                holder.ayahNo = (LinearLayout) convertView.findViewById(R.id.layout_ayah_no);
                holder.ayahRow = (LinearLayout) convertView.findViewById(R.id.ayah_row);
                holder.menuOptionsLayout = (LinearLayout) convertView.findViewById(R.id.menu_options_layout);

                holder.tvArabic.setPadding(20, ((GlobalClass) mContext.getApplicationContext()).ayahPadding, 20, 5);

                holder.tvArabic.setTextSize(((GlobalClass) mContext.getApplicationContext()).font_size_arabic);
                // holder.tvArabic.setTextColor(Color.parseColor(((GlobalClass) mContext.getApplicationContext()).fontColor));
                holder.tvArabic.setTextColor(Color.parseColor("#000000"));

                holder.tvArabic.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceArabic);
                holder.tvTranslation.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
                holder.tvTransliteration.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
                holder.tvTranslation.setTextSize(((GlobalClass) mContext.getApplicationContext()).font_size_eng);
                holder.tvTransliteration.setTextSize(((GlobalClass) mContext.getApplicationContext()).font_size_eng);
                // holder.tvTranslation.setTextColor(Color.parseColor(((GlobalClass) mContext.getApplicationContext()).fontColor));
                // holder.tvTransliteration.setTextColor(Color.parseColor(((GlobalClass) mContext.getApplicationContext()).fontColor));

                holder.tvTranslation.setTextColor(Color.parseColor("#000000"));
                holder.tvTransliteration.setTextColor(Color.parseColor("#000000"));

                if (transPos == 7 || transPos == 11) {
                    holder.tvTranslation.setGravity(Gravity.RIGHT);
                }

                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            if (surahPosition == 9) {
                // holder.tvAyahNo.setTextColor(Color.parseColor(((GlobalClass) mContext.getApplicationContext()).fontColor));
                holder.ayahNo.setVisibility(View.VISIBLE);
                holder.tvAyahNo.setText(String.valueOf(position + 1));
            } else {
                if (surahPosition == 1) {
                    holder.ayahNo.setVisibility(View.VISIBLE);
                    holder.tvAyahNo.setText(String.valueOf(position + 1));
                } else {
                    if (position == 0) {
                        holder.ayahNo.setVisibility(View.GONE);
                    } else {
                        holder.ayahNo.setVisibility(View.VISIBLE);
                        // holder.tvAyahNo.setTextColor(Color.parseColor(((GlobalClass) mContext.getApplicationContext()).fontColor));
                        holder.tvAyahNo.setText(String.valueOf(position));
                    }
                }
            }

            if (device.equals("large")) {
                holder.tvArabic.setText(" " + ArabicUtilities.reshapeSentence(arabic));
            } else {
                holder.tvArabic.setText(ArabicUtilities.reshapeSentence(arabic));
            }

            // holder.tvArabic.setText(arabic);
            holder.tvTranslation.setText(translation);
            holder.tvTransliteration.setText(Html.fromHtml(transliteration));

            if (id == -1) {
                holder.bookmarkImg.setVisibility(View.GONE);
            } else {
                holder.bookmarkImg.setVisibility(View.VISIBLE);
            }

            if (((GlobalClass) mContext.getApplicationContext()).isTranslation) {
                holder.tvTranslation.setVisibility(View.GONE);
            } else {
                holder.tvTranslation.setVisibility(View.VISIBLE);
            }

            if (!((GlobalClass) mContext.getApplicationContext()).isTransliteration) {
                holder.tvTransliteration.setVisibility(View.GONE);
            } else {
                holder.tvTransliteration.setVisibility(View.VISIBLE);
            }

            // holder.ayahRow.setBackgroundColor(Color.parseColor(((GlobalClass) mContext.getApplicationContext()).bgcolor));
            holder.ayahRow.setBackgroundColor(Color.parseColor("#FFFFFF"));

            if (position == ((GlobalClass) mContext.getApplicationContext()).ayahPos) {
                holder.ayahRow.setBackgroundResource(R.drawable.selection_color);
            }

            if (menuPosition == position) {
                holder.menuOptionsLayout.setVisibility(View.VISIBLE);
            } else {
                holder.menuOptionsLayout.setVisibility(View.GONE);
            }

            return convertView;
        }

        public void showAyahMenuOptions(int position) {
            menuPosition = position;
            notifyDataSetChanged();
        }

        public void hideMenuOptions() {
            menuPosition = -1;
            notifyDataSetChanged();
        }
    }
}
