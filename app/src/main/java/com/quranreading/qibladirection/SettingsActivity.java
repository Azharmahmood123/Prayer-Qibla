package com.quranreading.qibladirection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.alarms.AlarmHelper;
import com.quranreading.alarms.AlarmReceiverAyah;
import com.quranreading.helper.AutoSettingsJsonParser;
import com.quranreading.helper.DailogsClass;
import com.quranreading.helper.TimeFormateConverter;
import com.quranreading.listeners.OnDailogButtonSelectionListner;
import com.quranreading.model.PrayerTimeModel;
import com.quranreading.sharedPreference.AlarmSharedPref;
import com.quranreading.sharedPreference.LanguagePref;
import com.quranreading.sharedPreference.LocationPref;
import com.quranreading.sharedPreference.PrayerTimeSettingsPref;

import java.util.HashMap;

import duas.sharedprefs.DuasSharedPref;
import noman.quran.JuzConstant;
import noman.quran.activity.TextSettingScreen;
import quran.sharedpreference.SurahsSharedPref;

public class SettingsActivity extends AppCompatActivity implements OnDailogButtonSelectionListner {

    public static final int REQUEST_ADHAN_SOUND = 1;
    public static final int REQUEST_CALCULATION_METHOD = 2;
    public static final int REQUEST_MANUAL_CORRECTIONS = 3;

    String[] translationList = {"Off", "English (Saheeh)", "English (Pickthal)", "English (Shakir)", "English (Maududi)", "English (Daryabadi)", "English (Yusuf Ali)", "Urdu", "Spanish", "French", "Chinese", "Persian", "Italian", "Dutch", "Indonesian", "Melayu", "Hindi", "Bangla", "Turkish"};

    // google ads
    AdView adview;
    ImageView adImage;
    private static final String LOG_TAG = "Ads";
    private final Handler adsHandler = new Handler();
    private int timerValue = 3000, networkRefreshTime = 10000;

    Context context = this;

    LocationPref locPref;
    TextView[] settingsRowtexts = new TextView[24];
    int indexSoundOption = 0;

    Button btnTransprnt;
    ProgressBar progressBar;


    public static final int AUTO_SETTINGS = 0;
    public static final int DAYLIGHT_SAVING = 1;
    public static final int JURISTIC = 2;
    public static final int CALULATION_METHOD = 3;
    public static final int LATITUDE_ADJUSTMENT = 4;
    public static final int ALARM_SOUND = 5;
    public static final int ADVANCED_HELP = 6;
    public static final int TRANSLATION = 7;
    public static final int TRANSLITERATION = 8;
    public static final int RESET_ALL = 9;
    public static final int AYAH_NOTIFICAION = 10;
    public static final int TRANSLATION_DUAS = 11;
    public static final int TRANSLITERATION_DUAS = 12;
    public static final int MANUAL_CORRECTIONS = 13;
    public static final int AUTO_EDIT_SETTINGS = 14;

    private boolean inProcess = false;

    AppCompatCheckBox btnAutoSettings, btnDaylightSaving, btnTransliteration, btnAyahNotification, btnTransliterationDuas, btnTranslationDuas;
    TextView tvTranslations, tvAyahNotifyTime;
    LinearLayout layoutSalatSettings;
    String ayahNotifyTime;
    AlarmSharedPref alarmObj;

    PrayerTimeSettingsPref salatSharedPref;
    SurahsSharedPref mSurahsSharedPref;
    DuasSharedPref mDuasSharedPref;

    AlertDialog dailogLanguage = null;
    int juristic = 1, methodIndex = 1, adjustment = 1, autoEditTime = 0, translation = 1, indexTranlationTemp = 1;
    boolean chkAutoSettings, chkDaylightSaving = false, chkTransliteration = true, chkAyahNotification = false, chkTransliterationDua = true, chkTranslationDua = true;
    String jHeader, mHeader, aHeader, tHeader;

    String[] dataJuristic;
    String[] dataMethod;
    String[] dataAdjustment;
    String[] dataAutoEditTime = new String[2];

    DailogsClass dailogOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.super.onBackPressed();

            }
        });
        TextView tvHeading = (TextView) findViewById(R.id.txt_toolbar);
        tvHeading.setText(R.string.settings);

        alarmObj = new AlarmSharedPref(context);
        salatSharedPref = new PrayerTimeSettingsPref(context);
        mSurahsSharedPref = new SurahsSharedPref(context);
        mDuasSharedPref = new DuasSharedPref(context);
        locPref = new LocationPref(context);

        dataJuristic = getResources().getStringArray(R.array.arr_juristic);
        dataMethod = getResources().getStringArray(R.array.array_calculation_methods_new);
        dataAdjustment = getResources().getStringArray(R.array.arr_latitude_adjustment);
        dataAutoEditTime[0] = getResources().getString(R.string.auto_edit_alarm_option_1);
        dataAutoEditTime[1] = getResources().getString(R.string.auto_edit_alarm_option_2);

        sendAnalyticsData("Settings Screen");
        intializeViews();
        initializeSettings();
        //Surrah Reset Settings
        LinearLayout  resetSurrahSettings = (LinearLayout) findViewById(R.id.surah_reset_setting);
        // resetSurrahSettings.setTypeface(((GlobalClass) getApplication()).faceRobotoL);
        resetSurrahSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsActivity.this).setMessage("Are you sure to reset Quran Settings?").setPositiveButton("Yes", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mSurahsSharedPref.setSeekbarPosition(1);
                        mSurahsSharedPref.setEnglishFontSize(JuzConstant.fontSize_English[1]);
                        mSurahsSharedPref.setArabicFontSize(JuzConstant.fontSize_Arabic[1]);
                        mSurahsSharedPref.setTranslationIndex(1);
                        mSurahsSharedPref.setTransliteration(true);
                        mSurahsSharedPref.setLastTranslirationState(true);
                        mSurahsSharedPref.setLastSaveTransaltion(1);
                        mSurahsSharedPref.setReadModeState(false);
                        mSurahsSharedPref.setAyahNotification(false);

                        initializeSettings();

                    }
                }).setNegativeButton("No", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

            }
        });
    }

    public void intializeViews() {
        Button btnResetAll;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnTransprnt = (Button) findViewById(R.id.btn_transparent);

        layoutSalatSettings = (LinearLayout) findViewById(R.id._layout_salat_settings);

        tvTranslations = (TextView) findViewById(R.id.tv_translation);
        tvAyahNotifyTime = (TextView) findViewById(R.id.tv_ayah_notify_time);
        btnResetAll = (Button) findViewById(R.id.btn_reset_all);
        btnResetAll.setTypeface(((GlobalClass) getApplication()).faceRobotoR);

        settingsRowtexts[0] = (TextView) findViewById(R.id.heading_salat_settings);
        settingsRowtexts[1] = (TextView) findViewById(R.id.tv_daylight_saving);
        settingsRowtexts[2] = (TextView) findViewById(R.id.tv_juristic_opt);
        settingsRowtexts[3] = (TextView) findViewById(R.id.tv_calculatn_opt);
        settingsRowtexts[4] = (TextView) findViewById(R.id.tv_latd_adjst_opt);
        settingsRowtexts[5] = (TextView) findViewById(R.id.tv_tone_opt);
        settingsRowtexts[6] = (TextView) findViewById(R.id.tv_juristic_head);
        settingsRowtexts[7] = (TextView) findViewById(R.id.tv_calculatn_head);
        settingsRowtexts[8] = (TextView) findViewById(R.id.tv_latd_adjst_head);
        settingsRowtexts[9] = (TextView) findViewById(R.id.tv_tone_settings_head);
        settingsRowtexts[10] = (TextView) findViewById(R.id.tv_advance_help);
        settingsRowtexts[11] = (TextView) findViewById(R.id.row_general_settings_head);
        settingsRowtexts[12] = (TextView) findViewById(R.id.heading_surahs_settings);
        settingsRowtexts[13] = (TextView) findViewById(R.id.tv_transliteration_settings);
        settingsRowtexts[14] = (TextView) findViewById(R.id.tv_translation_settings);
        settingsRowtexts[15] = (TextView) findViewById(R.id.tv_ayah_of_day);

        settingsRowtexts[16] = (TextView) findViewById(R.id.heading_duas_settings);
        settingsRowtexts[17] = (TextView) findViewById(R.id.tv_translation_settings_dua);
        settingsRowtexts[18] = (TextView) findViewById(R.id.tv_transliteration_settings_dua);
        settingsRowtexts[19] = (TextView) findViewById(R.id.tv_auto_settings);
        settingsRowtexts[20] = (TextView) findViewById(R.id.tv_corrections);
        settingsRowtexts[21] = (TextView) findViewById(R.id.tv_corrections_opt);
        settingsRowtexts[22] = (TextView) findViewById(R.id.tv_auto_selection_head);
        settingsRowtexts[23] = (TextView) findViewById(R.id.tv_auto_selection_opt);

        for (int pos = 0; pos < settingsRowtexts.length; pos++) {
            if (pos == 0 || pos == 11 || pos == 12 || pos == 16) {
                settingsRowtexts[pos].setTypeface(((GlobalClass) getApplication()).faceRobotoB);
            } else {
                settingsRowtexts[pos].setTypeface(((GlobalClass) getApplication()).faceRobotoR);
            }
        }

        showManualCorrectionData();

        tvAyahNotifyTime.setTypeface(((GlobalClass) getApplication()).faceRobotoL);
        tvTranslations.setTypeface(((GlobalClass) getApplication()).faceRobotoL);

        // settingsRowtexts[0].setText(getResources().getString(R.string.salat_timings).toUpperCase());

        btnAutoSettings = (AppCompatCheckBox) findViewById(R.id.img_auto_settings);
        btnDaylightSaving = (AppCompatCheckBox) findViewById(R.id.img_daylight_saving);
        btnTransliteration = (AppCompatCheckBox) findViewById(R.id.img_transliteration_settings);
        btnTransliterationDuas = (AppCompatCheckBox) findViewById(R.id.img_transliteration_settings_dua);
        btnTranslationDuas = (AppCompatCheckBox) findViewById(R.id.img_translation_settings_dua);
        btnAyahNotification = (AppCompatCheckBox) findViewById(R.id.img_ayah_of_day);

        initializeAds();
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

    public void showManualCorrectionData() {
        int fajr, sunrize, zuhar, asar, maghrib, isha;
        fajr = salatSharedPref.getCorrectionsFajr();
        sunrize = salatSharedPref.getCorrectionsSunrize();
        zuhar = salatSharedPref.getCorrectionsZuhar();
        asar = salatSharedPref.getCorrectionsAsar();
        maghrib = salatSharedPref.getCorrectionsMaghrib();
        isha = salatSharedPref.getCorrectionsIsha();
        String correctionText = fajr + ", " + sunrize + ", " + zuhar + ", " + asar + ", " + maghrib + ", " + isha;
        settingsRowtexts[21].setText(correctionText);
    }

    public void onBackButtonClick(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated methodIndex stub
        if (!inProcess) {
            inProcess = true;
            super.onBackPressed();
        }
    }

    public void onRowClick(View view) {
        if (!inProcess) {
            Integer btnClick = Integer.parseInt(view.getTag().toString());

            switch (btnClick) {

                case AUTO_SETTINGS: {
                    if (chkAutoSettings) {
                        sendAnalyticEvent("Salat Auto Settings Off");
                        chkAutoSettings = false;
                        salatSharedPref.setAutoSettings(chkAutoSettings);
                        showShortToast(getResources().getString(R.string.auto_settings) + " " + getResources().getString(R.string.off), 400);
                        btnAutoSettings.setChecked(false);
                        layoutSalatSettings.setVisibility(View.VISIBLE);
                        saveAutoSettings();
                        initializeSalatSettings();
                    } else {
                        sendAnalyticEvent("Salat Auto Settings ON");
                        chkAutoSettings = true;
                        salatSharedPref.setAutoSettings(chkAutoSettings);
                        showShortToast(getResources().getString(R.string.auto_settings) + " " + getResources().getString(R.string.on), 400);
                        btnAutoSettings.setChecked(true);
                        layoutSalatSettings.setVisibility(View.GONE);
                    }
                }

                break;

                case DAYLIGHT_SAVING: {
                    if (chkDaylightSaving) {
                        sendAnalyticEvent("Daylight Saving Off");
                        chkDaylightSaving = false;
                        salatSharedPref.setDaylightSaving(chkDaylightSaving);
                        showShortToast(getResources().getString(R.string.daylight_saving) + " " + getResources().getString(R.string.off), 400);
                        btnDaylightSaving.setChecked(false);
                    } else {
                        sendAnalyticEvent("Daylight Saving ON");
                        chkDaylightSaving = true;
                        salatSharedPref.setDaylightSaving(chkDaylightSaving);
                        showShortToast(getResources().getString(R.string.daylight_saving) + " " + getResources().getString(R.string.on), 400);
                        btnDaylightSaving.setChecked(true);
                    }
                }
                break;

                case JURISTIC: {
                    inProcess = true;
                    dailogOptions = new DailogsClass(context, getResources().getString(R.string.juristic), "", dataJuristic, juristic, this, getResources().getString(R.string.okay), getResources().getString(R.string.cancel));
                    dailogOptions.showOptionsDialogTwoButton();
                }
                break;

                case CALULATION_METHOD: {
                    inProcess = true;

                    startActivityForResult(new Intent(this, SettingsCalculationMethodActivity.class), REQUEST_CALCULATION_METHOD);
//                    dailogOptions = new DailogsClass(context, getResources().getString(R.string.calculation_method), "", dataMethod, methodIndex, this, getResources().getString(R.string.okay), getResources().getString(R.string.cancel));
//                    dailogOptions.showOptionsDialogTwoButton();
                }
                break;

                case ALARM_SOUND: {
                    inProcess = true;


                    startActivity(new Intent(SettingsActivity.this, SettingsPrayerListSoundActivity.class));

//                    startActivityForResult(new Intent(SettingsActivity.this, SettingSoundsActivityOld.class), REQUEST_ADHAN_SOUND);
                }
                break;

                case LATITUDE_ADJUSTMENT: {
                    inProcess = true;
                    dailogOptions = new DailogsClass(context, getResources().getString(R.string.latitude_adjustment), "", dataAdjustment, adjustment, this, getResources().getString(R.string.okay), getResources().getString(R.string.cancel));
                    dailogOptions.showOptionsDialogTwoButton();
                }
                break;

                case TRANSLATION: {
                    //  showLanguageDailog();
                    startActivity(new Intent(this, TextSettingScreen.class));
                }
                break;

                case TRANSLITERATION: {
                    if (chkTransliteration) {
                        sendAnalyticEvent("Surahs Transliteration Off");
                        chkTransliteration = false;
                        mSurahsSharedPref.setTransliteration(chkTransliteration);
                        btnTransliteration.setChecked(false);
                    } else {
                        sendAnalyticEvent("Surahs Transliteration ON");
                        chkTransliteration = true;
                        mSurahsSharedPref.setTransliteration(chkTransliteration);
                        btnTransliteration.setChecked(true);
                    }
                }
                break;

                case AYAH_NOTIFICAION: {
                    AlarmHelper mAlarmHelper = new AlarmHelper(context);
                    if (chkAyahNotification) {
                        sendAnalyticEvent("Ayah Notification Off");
                        chkAyahNotification = false;
                        mSurahsSharedPref.setAyahNotification(chkAyahNotification);
                        btnAyahNotification.setChecked(false);
                        mAlarmHelper.cancelAlarmAyahNotification(AlarmReceiverAyah.NOTIFY_AYAH_ALARM_ID);
                        tvAyahNotifyTime.setVisibility(View.GONE);
                    } else {
                        showAyahNotificationDialog();
                    }
                }
                break;

                case RESET_ALL: {
                    inProcess = true;
                    dailogOptions = new DailogsClass(context, getResources().getString(R.string.reset_all), getResources().getString(R.string.reset_msg), this, getResources().getString(R.string.reset), getResources().getString(R.string.cancel));
                    dailogOptions.showTwoButtonDialog();
                }
                break;
                case ADVANCED_HELP: {
                    sendAnalyticEvent("Advance Help");
                    inProcess = true;
                    dailogOptions = new DailogsClass(context, getResources().getString(R.string.advance_help), R.layout.advance_instrct, this);
                    dailogOptions.showCustomDailog();
                }
                break;

                case TRANSLATION_DUAS: {

                    if (chkTranslationDua) {
                        sendAnalyticEvent("Duas Translation Off");
                        chkTranslationDua = false;
                        mDuasSharedPref.setTranslation(chkTranslationDua);
                        btnTranslationDuas.setChecked(false);
                    } else {
                        sendAnalyticEvent("Duas Translation ON");
                        chkTranslationDua = true;
                        mDuasSharedPref.setTranslation(chkTranslationDua);
                        btnTranslationDuas.setChecked(true);
                    }
                }
                break;

                case TRANSLITERATION_DUAS: {
                    if (chkTransliterationDua) {
                        sendAnalyticEvent("Duas Transliteration Off");
                        chkTransliterationDua = false;
                        mDuasSharedPref.setTransliteration(chkTransliterationDua);
                        btnTransliterationDuas.setChecked(false);
                    } else {
                        sendAnalyticEvent("Duas Transliteration ON");
                        chkTransliterationDua = true;
                        mDuasSharedPref.setTransliteration(chkTransliterationDua);
                        btnTransliterationDuas.setChecked(true);
                    }
                }
                break;

                case MANUAL_CORRECTIONS: {
                    inProcess = true;
                    startActivityForResult(new Intent(this, SettingsManualCorrectionsActivity.class), REQUEST_MANUAL_CORRECTIONS);
                }

                break;

                case AUTO_EDIT_SETTINGS: {
                    inProcess = true;
                    dailogOptions = new DailogsClass(context, getResources().getString(R.string.auto_edit_alarm), "", dataAutoEditTime, (autoEditTime + 1), this, getResources().getString(R.string.okay), getResources().getString(R.string.cancel));
                    dailogOptions.showOptionsDialogTwoButton();
                }

                break;

                default:
                    return;
            }
        }
    }


    public void saveAutoSettings() {
        AutoSettingsJsonParser autoSettingsJsonParser = new AutoSettingsJsonParser();
        PrayerTimeModel data = autoSettingsJsonParser.getAutoSettings(this);

        salatSharedPref.setJuristic(data.getJuristicIndex());
        salatSharedPref.setCalculationMethod(data.getConventionNumber());
        salatSharedPref.setCalculationMethodIndex(data.getConventionPosition());
        salatSharedPref.setDaylightSaving(false); // Daylight Saving OFF
        salatSharedPref.setJuristic(data.getJuristicIndex());
        salatSharedPref.setsLatdAdjst(2); // Angle Based

        int[] arrCorrections = data.getCorrections();
        salatSharedPref.setCorrectionsFajr(arrCorrections[0]);
        salatSharedPref.setCorrectionsSunrize(arrCorrections[1]);
        salatSharedPref.setCorrectionsZuhar(arrCorrections[2]);
        salatSharedPref.setCorrectionsAsar(arrCorrections[3]);
        salatSharedPref.setCorrectionsMaghrib(arrCorrections[4]);
        salatSharedPref.setCorrectionsIsha(arrCorrections[5]);

    }

    private void showAyahNotificationDialog() {

        TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                AlarmHelper mAlarmHelper = new AlarmHelper(context);
                sendAnalyticEvent("Ayah Notification ON");
                chkAyahNotification = true;
                mSurahsSharedPref.setAyahNotification(chkAyahNotification);

                mSurahsSharedPref.setAlarmHours(selectedHour);
                mSurahsSharedPref.setAlarmMints(selectedMinute);

                ayahNotifyTime = mSurahsSharedPref.getAlarmHours() + ":" + mSurahsSharedPref.getAlarmMints();
                ayahNotifyTime = TimeFormateConverter.convertTime24To12(ayahNotifyTime);
                tvAyahNotifyTime.setText(ayahNotifyTime);

                btnAyahNotification.setChecked(true);
                mAlarmHelper.setAlarmAyahNotification(mAlarmHelper.setAlarmTime(selectedHour, selectedMinute, ""), AlarmReceiverAyah.NOTIFY_AYAH_ALARM_ID);
                tvAyahNotifyTime.setVisibility(View.VISIBLE);
            }
        }, mSurahsSharedPref.getAlarmHours(), mSurahsSharedPref.getAlarmMints(), false);// Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void showLanguageDailog() {

        if (!inProcess) {
            inProcess = true;

            if (dailogLanguage != null)
                dailogLanguage.dismiss();

            indexTranlationTemp = translation;
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            // Set the dialog title
            builder.setTitle(getResources().getString(R.string.txt_translation))
                    // Specify the list array, the items to be selected by
                    // default (null for none),
                    // and the listener through which to receive callbacks when
                    // items are selected
                    .setPositiveButton(getResources().getString(R.string.okay), new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            // TODO Auto-generated methodIndex stub

                            inProcess = false;
                            translation = indexTranlationTemp;
                            mSurahsSharedPref.setTranslationIndex(translation);
                            tvTranslations.setText(translationList[translation]);
                            dialog.dismiss();
                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel), new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    inProcess = false;
                    dialog.dismiss();
                }
            })
                    // Specify the list array, the items to be selected by
                    // default (null for none),
                    // and the listener through which to receive callbacks when
                    // items are selected
                    .setOnKeyListener(new Dialog.OnKeyListener() {

                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            // TODO Auto-generated methodIndex stub
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                // finish();
                                inProcess = false;
                                dialog.dismiss();
                            }
                            return true;
                        }
                    })

                    .setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // TODO Auto-generated methodIndex stub
                            inProcess = false;
                            dialog.dismiss();
                        }
                    }).setSingleChoiceItems(translationList, translation, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int pos) {

                    indexTranlationTemp = pos;
                }
            });

            dailogLanguage = builder.create();
            dailogLanguage.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    // TODO Auto-generated methodIndex stub

                    inProcess = false;
                }
            });
            dailogLanguage.show();
        }
    }


    private void initializeSalatSettings() {
        chkDaylightSaving = salatSharedPref.isDaylightSaving();

        if (chkDaylightSaving) {
            btnDaylightSaving.setChecked(true);
        } else {
            btnDaylightSaving.setChecked(false);
        }

        HashMap<String, Integer> settingsData = salatSharedPref.getSettings();

        juristic = settingsData.get(PrayerTimeSettingsPref.JURISTIC);
        methodIndex = salatSharedPref.getCalculationMethodIndex();
        adjustment = settingsData.get(PrayerTimeSettingsPref.LATITUDE_ADJUSTMENT);

        settingsRowtexts[JURISTIC].setText(dataJuristic[juristic - 1]);
        settingsRowtexts[CALULATION_METHOD].setText(dataMethod[methodIndex]);
        settingsRowtexts[LATITUDE_ADJUSTMENT].setText(dataAdjustment[adjustment - 1]);

        showManualCorrectionData();
    }

    public void initializeSettings() {

        chkAutoSettings = salatSharedPref.isAutoSettings();
        chkDaylightSaving = salatSharedPref.isDaylightSaving();
        chkTransliteration = mSurahsSharedPref.isTransliteration();
        chkAyahNotification = mSurahsSharedPref.isAyahNotification();
        translation = mSurahsSharedPref.getTranslationIndex();
        autoEditTime = salatSharedPref.getAutoEditTime();

        ayahNotifyTime = mSurahsSharedPref.getAlarmHours() + ":" + mSurahsSharedPref.getAlarmMints();
        ayahNotifyTime = TimeFormateConverter.convertTime24To12(ayahNotifyTime);
        tvAyahNotifyTime.setText(ayahNotifyTime);

        chkTranslationDua = mDuasSharedPref.isTranslation();
        chkTransliterationDua = mDuasSharedPref.isTransliteration();

        if (chkAutoSettings) {
            btnAutoSettings.setChecked(true);
            layoutSalatSettings.setVisibility(View.GONE);
        } else {
            btnAutoSettings.setChecked(false);
            layoutSalatSettings.setVisibility(View.VISIBLE);
        }

        if (chkDaylightSaving) {
            btnDaylightSaving.setChecked(true);
        } else {
            btnDaylightSaving.setChecked(false);
        }

        if (chkTransliteration) {
            btnTransliteration.setChecked(true);
        } else {
            btnTransliteration.setChecked(false);
        }

        if (chkTransliterationDua) {
            btnTransliterationDuas.setChecked(true);
        } else {
            btnTransliterationDuas.setChecked(false);
        }

        if (chkTranslationDua) {
            btnTranslationDuas.setChecked(true);
        } else {
            btnTranslationDuas.setChecked(false);
        }

        if (chkAyahNotification) {
            btnAyahNotification.setChecked(true);
            tvAyahNotifyTime.setVisibility(View.VISIBLE);
        } else {
            btnAyahNotification.setChecked(false);
            tvAyahNotifyTime.setVisibility(View.GONE);
        }

        settingsRowtexts[23].setText(dataAutoEditTime[autoEditTime]);
        tvTranslations.setText(translationList[translation]);

        HashMap<String, Integer> settingsData = salatSharedPref.getSettings();

        juristic = settingsData.get(PrayerTimeSettingsPref.JURISTIC);
        methodIndex = salatSharedPref.getCalculationMethodIndex();
        adjustment = settingsData.get(PrayerTimeSettingsPref.LATITUDE_ADJUSTMENT);

        settingsRowtexts[JURISTIC].setText(dataJuristic[juristic - 1]);
        settingsRowtexts[CALULATION_METHOD].setText(dataMethod[methodIndex]);
        settingsRowtexts[LATITUDE_ADJUSTMENT].setText(dataAdjustment[adjustment - 1]);
//        indexSoundOption = alarmObj.getAlarmOptionIndex();
        if (indexSoundOption == -1) {
            useOldAdhanSettings();
        }


        String[] soundOption = {getResources().getString(R.string.default_tone), getResources().getString(R.string.silent), getResources().getString(R.string.adhan1), getResources().getString(R.string.adhan2), getResources().getString(R.string.adhan3)};
        settingsRowtexts[ALARM_SOUND].setText(soundOption[indexSoundOption]);

        // initializeAudios();

        showManualCorrectionData();
    }

    private void useOldAdhanSettings() {
        int adhanIndex = alarmObj.getTone();
        boolean chkSilent = alarmObj.getSilentMode();
        boolean chkDefault = alarmObj.getDefaultToneMode();
        if (chkSilent) {
            indexSoundOption = 0;
        } else if (chkDefault) {
            indexSoundOption = 1;
        } else {
            indexSoundOption = adhanIndex + 1;
        }

        for (int i = 0; i < 6; i++) {
            alarmObj.setAlarmOptionIndex(AlarmSharedPref.ALARM_PRAYERS_SOUND[i], indexSoundOption);
        }
    }

    private void resetSettings() {
        int language = -1;

        LanguagePref languagePref = new LanguagePref(this);
        language = languagePref.getLanguage();

        mSurahsSharedPref.clearStoredData();
        mDuasSharedPref.clearStoredData();

        indexSoundOption = 2;
        juristic = salatSharedPref.getJuristicDefault();
        methodIndex = 1;
        adjustment = 1;
        salatSharedPref.saveSettings(juristic, methodIndex, adjustment);
//        alarmObj.setAlarmOptionIndex(indexSoundOption);
        salatSharedPref.setDaylightSaving(false);

        salatSharedPref.setAutoSettings(true);

        mSurahsSharedPref.setSeekbarPosition(2);
        mSurahsSharedPref.setEnglishFontSize(JuzConstant.fontSize_English[2]);
        mSurahsSharedPref.setArabicFontSize(JuzConstant.fontSize_Arabic[2]);
        mSurahsSharedPref.setTranslationIndex(1);
        mSurahsSharedPref.setTransliteration(true);
        mSurahsSharedPref.setLastTranslirationState(true);
        mSurahsSharedPref.setLastSaveTransaltion(1);
        mSurahsSharedPref.setReadModeState(false);
        mSurahsSharedPref.setAyahNotification(false);

        // locPref.setManualLocationOff();
        // initializeSettings();

        if (language != 0) {
            languagePref.setLanguage(0);
            ((GlobalClass) getApplication()).setLocale(0);

            MainActivityNew.finishActivity();
            startActivity(new Intent(context, MainActivityNew.class));
        }
        finish();
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
initializeSettings();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated methodIndex stub
        super.onPause();

        // manualLocDialog.dismissDialog();
        if (!((GlobalClass) getApplication()).isPurchase) {
            stopAdsCall();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated methodIndex stub
        super.onDestroy();
        if (!((GlobalClass) getApplication()).isPurchase) {
            destroyAds();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated methodIndex stub

        inProcess = false;
        if (requestCode == REQUEST_ADHAN_SOUND && resultCode == RESULT_OK) {
            String[] soundOption = {getResources().getString(R.string.default_tone), getResources().getString(R.string.silent), getResources().getString(R.string.adhan1), getResources().getString(R.string.adhan2), getResources().getString(R.string.adhan3)};
            settingsRowtexts[ALARM_SOUND].setText(soundOption[data.getIntExtra(SettingSoundsActivityOld.EXTRA_ADHAN_INDEX, 0)]);
        } else if (requestCode == REQUEST_CALCULATION_METHOD && resultCode == RESULT_OK) {

            methodIndex = salatSharedPref.getCalculationMethodIndex();
            sendAnalyticEvent("Salah Method- " + dataMethod[methodIndex]);
            settingsRowtexts[CALULATION_METHOD].setText(dataMethod[methodIndex]);
        } else if (requestCode == REQUEST_MANUAL_CORRECTIONS && resultCode == RESULT_OK) {
            showManualCorrectionData();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDailogButtonSelectionListner(String title, int savedIndex, boolean selection) {
        // TODO Auto-generated methodIndex stub

        inProcess = false;

        if (title.equals(getResources().getString(R.string.juristic)) && selection) {

            sendAnalyticEvent("Salah Juristic- " + dataJuristic[savedIndex]);
            juristic = savedIndex + 1;
            salatSharedPref.setJuristic(juristic);
            settingsRowtexts[JURISTIC].setText(dataJuristic[savedIndex]);

        } else if (title.equals(getResources().getString(R.string.calculation_method)) && selection) {
            sendAnalyticEvent("Salah Method- " + dataMethod[savedIndex]);
            methodIndex = savedIndex + 1;
            salatSharedPref.setCalculationMethod(savedIndex + 1);
            settingsRowtexts[CALULATION_METHOD].setText(dataMethod[savedIndex]);
        } else if (title.equals(getResources().getString(R.string.latitude_adjustment)) && selection) {
            sendAnalyticEvent("Salah Adjustment- " + dataAdjustment[savedIndex]);
            adjustment = savedIndex + 1;
            salatSharedPref.setsLatdAdjst(savedIndex + 1);
            settingsRowtexts[LATITUDE_ADJUSTMENT].setText(dataAdjustment[savedIndex]);
        } else if (title.equals(getResources().getString(R.string.reset_all)) && selection) {
            sendAnalyticEvent("Reset");
            resetSettings();
        } else if (title.equals(getResources().getString(R.string.auto_edit_alarm)) && selection) {
            autoEditTime = savedIndex;
            salatSharedPref.setAutoEditTime(autoEditTime);
            settingsRowtexts[23].setText(dataAutoEditTime[autoEditTime]);

        }
    }

    private void showShortToast(String message, int milliesTime) {
        final Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, milliesTime);
    }

    private void sendAnalyticsData(String name) {
        AnalyticSingaltonClass.getInstance(this).sendScreenAnalytics(name);
    }

    private void sendAnalyticEvent(String eventAction) {
        AnalyticSingaltonClass.getInstance(this).sendEventAnalytics("Settings-Qibla", eventAction);
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
