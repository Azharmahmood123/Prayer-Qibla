package com.quranreading.qibladirection;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.helper.TimeFormateConverter;
import com.quranreading.sharedPreference.AlarmSharedPref;
import com.quranreading.sharedPreference.TimeEditPref;

/**
 * Created by cyber on 12/7/2016.
 */

public class SettingsTimeAlarmActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    public static final String EXTRA_ADHAN_INDEX = "adhan_sound";

    public static final String EXTRA_PRAYER_INDEX = "prayer_index";
    public static final String EXTRA_PRAYER_NOTIFICATION_TIME = "prayer_notification_time";
//    public static final String EXTRA_PRAYER_TIME = "prayer_time";

    static int posPrayer;
    static String[] time;
    MediaPlayer mp = new MediaPlayer();
    int value = 0;
    int indexSoundOption = 0;

    int[] adhanSounds = {R.raw.adhan_fajr_madina, R.raw.most_popular_adhan, R.raw.adhan_from_egypt, R.raw.adhan_madina, R.raw.azan_by_nasir_a_qatami, R.raw.azan_mansoural_zahrani, R.raw.mishary_rashid_al_afasy};

    private boolean[] chkPlay = new boolean[7];
    private ImageView[] btnPlay = new ImageView[7];
    ;
    private ImageView[] btnsAdhanSound = new ImageView[9];
    public TextView tvNotificationTimeHead, tvNotificationSoundHead;
    public static TextView tvNotificationTime;
    public TextView[] tvSounds = new TextView[9];


    private static final int btn_Default_tone = 0;
    private static final int btn_Silent = 1;
    private static final int btn_Adhan_1 = 2;
    private static final int btn_Adhan_2 = 3;
    private static final int btn_Adhan_3 = 4;
    private static final int btn_Adhan_4 = 5;
    private static final int btn_Adhan_5 = 6;
    private static final int btn_Adhan_6 = 7;
    private static final int btn_Adhan_7 = 8;

    private static final int btn_play_1 = 0;
    private static final int btn_play_2 = 1;
    private static final int btn_play_3 = 2;
    private static final int btn_play_4 = 3;
    private static final int btn_play_5 = 4;
    private static final int btn_play_6 = 5;
    private static final int btn_play_7 = 6;

    private AlarmSharedPref alarmObj;

    private boolean inProcess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated methodIndex stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timmings_alarm);


        LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        TextView tvHeading = (TextView) findViewById(R.id.txt_toolbar);
        ImageView ivSave = (ImageView) findViewById(R.id.iv_alarm_settings_save);

//        tvHeading.setTypeface(((GlobalClass) getApplication()).faceRobotoR);
        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveNotification(v);
            }
        });

        String[] prayerNames = {getString(R.string.txt_fajr), getString(R.string.txt_sunrise), getString(R.string.txt_zuhr), getString(R.string.txt_asar), getString(R.string.txt_maghrib), getString(R.string.txt_isha)};

        alarmObj = new AlarmSharedPref(this);

        Intent intent = getIntent();
        time = intent.getStringArrayExtra(EXTRA_PRAYER_NOTIFICATION_TIME);
        // String[] timePrayer = intent.getStringArrayExtra(EXTRA_PRAYER_TIME);
        posPrayer = intent.getIntExtra(EXTRA_PRAYER_INDEX, -1);

        tvHeading.setText(prayerNames[posPrayer] + " " + getString(R.string.notificaion));


        initializeViews();

        String t = TimeFormateConverter.convertTime24To12("" + time[0] + ":" + time[1]);
        tvNotificationTime.setText(t);

        initializeSettings();
    }

    private void initializeSettings() {

        indexSoundOption = alarmObj.getAlarmOptionIndex(AlarmSharedPref.ALARM_PRAYERS_SOUND[posPrayer], posPrayer);
        if (indexSoundOption == -1) {
            useOldAdhanSettings();
        }

        adjustSoundViews();
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

    private void initializeViews() {

        tvSounds[0] = (TextView) findViewById(R.id.tv_default_tone);
        tvSounds[1] = (TextView) findViewById(R.id.tv_silent);
        tvSounds[2] = (TextView) findViewById(R.id.tv_adhan_1);
        tvSounds[3] = (TextView) findViewById(R.id.tv_adhan_2);
        tvSounds[4] = (TextView) findViewById(R.id.tv_adhan_3);
        tvSounds[5] = (TextView) findViewById(R.id.tv_adhan_4);
        tvSounds[6] = (TextView) findViewById(R.id.tv_adhan_5);
        tvSounds[7] = (TextView) findViewById(R.id.tv_adhan_6);
        tvSounds[8] = (TextView) findViewById(R.id.tv_adhan_7);
//        tvSounds[9] = (TextView) findViewById(R.id.tv_adhan_8);

        tvNotificationTimeHead = (TextView) findViewById(R.id.tv_notification_time_header);
        tvNotificationSoundHead = (TextView) findViewById(R.id.tv_tone_settings_header);
        tvNotificationTime = (TextView) findViewById(R.id.tv_notification_time);

        tvNotificationTimeHead.setTypeface(((GlobalClass) getApplication()).faceRobotoB);
        tvNotificationSoundHead.setTypeface(((GlobalClass) getApplication()).faceRobotoB);
        tvNotificationTime.setTypeface(((GlobalClass) getApplication()).faceRobotoR);

        for (int index = 0; index < tvSounds.length; index++) {
            tvSounds[index].setTypeface(((GlobalClass) getApplication()).faceRobotoR);
        }

        if (!((GlobalClass) getApplication()).isPurchase) {
            for (int index = 3; index < tvSounds.length; index++) {
                tvSounds[index].setTextColor(getResources().getColor(R.color.disable_text_color));
            }
        }

        tvNotificationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAlarmTimeClickListner(v);
            }
        });

        btnsAdhanSound[btn_Default_tone] = (ImageView) findViewById(R.id.img_default_tone);
        btnsAdhanSound[btn_Silent] = (ImageView) findViewById(R.id.img_silent);
        btnsAdhanSound[btn_Adhan_1] = (ImageView) findViewById(R.id.img_adhan_1_opt);
        btnsAdhanSound[btn_Adhan_2] = (ImageView) findViewById(R.id.img_adhan_2_opt);
        btnsAdhanSound[btn_Adhan_3] = (ImageView) findViewById(R.id.img_adhan_3_opt);
        btnsAdhanSound[btn_Adhan_4] = (ImageView) findViewById(R.id.img_adhan_4_opt);
        btnsAdhanSound[btn_Adhan_5] = (ImageView) findViewById(R.id.img_adhan_5_opt);
        btnsAdhanSound[btn_Adhan_6] = (ImageView) findViewById(R.id.img_adhan_6_opt);
        btnsAdhanSound[btn_Adhan_7] = (ImageView) findViewById(R.id.img_adhan_7_opt);
//        btnsAdhanSound[btn_Adhan_8] = (ImageView) findViewById(R.id.img_adhan_8_opt);

        btnPlay[0] = (ImageView) findViewById(R.id.img_adhan_1_play);
        btnPlay[1] = (ImageView) findViewById(R.id.img_adhan_2_play);
        btnPlay[2] = (ImageView) findViewById(R.id.img_adhan_3_play);
        btnPlay[3] = (ImageView) findViewById(R.id.img_adhan_4_play);
        btnPlay[4] = (ImageView) findViewById(R.id.img_adhan_5_play);
        btnPlay[5] = (ImageView) findViewById(R.id.img_adhan_6_play);
        btnPlay[6] = (ImageView) findViewById(R.id.img_adhan_7_play);
//        btnPlay[7] = (ImageView) findViewById(R.id.img_adhan_8_play);
    }

    private void adjustSoundViews() {
        for (int i = 0; i < btnsAdhanSound.length; i++) {
            btnsAdhanSound[i].setImageResource(0);
        }
        btnsAdhanSound[indexSoundOption].setImageResource(R.drawable.tick_gray);
    }

    public void onAlarmTimeClickListner(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void onAlarmOptionsClick(View view) {
        int index = Integer.parseInt(view.getTag().toString());
        if (!((GlobalClass) getApplication()).isPurchase) {
            if (index < 4) {
                indexSoundOption = index;
                adjustSoundViews();
            } else {
                if (!inProcess) {
                    inProcess = true;
                    startActivity(new Intent(SettingsTimeAlarmActivity.this, UpgradeActivity.class));
                }
            }
        } else {
            indexSoundOption = index;
            adjustSoundViews();
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
    protected void onResume() {
        // TODO Auto-generated methodIndex stub
        super.onResume();
        inProcess = false;
    }

    public void onPlayClick(View view) {
        int index = Integer.parseInt(view.getTag().toString());

        if (!chkPlay[index]) {
            value = -1;
            resetAudios();

            chkPlay[index] = true;
            btnPlay[index].setImageResource(R.drawable.btn_stop);

            value = index;
            resetAudios();
        } else {
            value = -1;
            resetAudios();
        }
    }

    public void initializeAudios(int pos) {

//        String uriAudio = "azan_" + (pos);
//
//        int resrcAudio = getResources().getIdentifier(uriAudio, "raw", getPackageName());
        if (mp != null) {
            mp.release();
        }

//        if (resrcAudio > 0) {
        mp = MediaPlayer.create(this, adhanSounds[pos]);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
//        }
    }

    private void resetAudios() {
        try {
            if (mp != null) {

                mp.release();

//                if (mp.isPlaying()) {
//                    mp.pause();
//                    mp.seekTo(0);
//                }
            }

            if (value != -1) {
                initializeAudios(value);
                mp.start();
            } else {

                for (int index = 0; index < btnPlay.length; index++) {
                    chkPlay[index] = false;
                    btnPlay[index].setImageResource(R.drawable.btn_play);
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated methodIndex stub
        value = -1;
        resetAudios();
    }

    @Override
    public boolean onError(MediaPlayer mp1, int what, int extra) {
        // TODO Auto-generated methodIndex stub
        boolean result = false;

        try {
            if (mp != null) {
                mp.stop();
                mp.reset();
            }

            String uriAudio = "azan_" + (value + 1);

            int resrcAudio = getResources().getIdentifier(uriAudio, "raw", getPackageName());

            if (resrcAudio > 0) {
                mp = MediaPlayer.create(this, resrcAudio);
                mp.setOnCompletionListener(this);
                mp.setOnErrorListener(this);

                mp.start();
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated methodIndex stub
        super.onPause();
        value = -1;
        resetAudios();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated methodIndex stub
        super.onDestroy();
        if (mp != null) {
            mp.release();
        }
    }

//    private void showSoundSettingsDailog() {
//        AlertDialog alertDialog = null;
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        // .setTitle(R.string.tone_settings);
//        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                finish();
//            }
//        });
//
//        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                sendAnalyticEvent(indexSoundOption);
//                alarmObj.setAlarmOptionIndex(indexSoundOption);
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra(EXTRA_ADHAN_INDEX, indexSoundOption);
//                setResult(RESULT_OK, resultIntent);
//                finish();
//            }
//        });
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//        });
//
//        alertDialog = builder.create();
//        alertDialog.setView(viewSoundSettings);
//        alertDialog.show();
//    }

    private void sendAnalyticEvent(int indexSoundOption) {
        String[] arrEvents = {"Adhan Default", "Adhan Silent", "Adhan 1", "Adhan 2", "Adhan 3"};
        AnalyticSingaltonClass.getInstance(this).sendEventAnalytics("Settings-Qibla", arrEvents[indexSoundOption]);
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        private boolean isClickListnerCalled = false;

        Context mContext;
        TimeEditPref timeEditPref;
        boolean inProcess;

        public TimePickerFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            // final Calendar c = Calendar.getInstance();
            isClickListnerCalled = false;
            mContext = getContext();
            int hour = Integer.parseInt(time[0]);
            int minute = Integer.parseInt(time[1]);

            // Create a new instance of TimePickerDialog and return it

            TimePickerDialog timeDialog = new TimePickerDialog(mContext, this, hour, minute, false);
            timeDialog.setTitle(getResources().getString(R.string.set_time));
            // timeDialog.setButton(DialogInterface.BUTTON_POSITIVE,mContext.getResources().getString(R.string.okay), timeDialog);
            timeDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), onResetClickListner);
            return timeDialog;
        }

        DialogInterface.OnClickListener onResetClickListner = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // This check is applied as Button Click listners are calling 2 Times

                if (!isClickListnerCalled) {
                    isClickListnerCalled = true;
                }
            }
        };

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            // This check is applied as Button Click listners are calling 2 Times
            if (!isClickListnerCalled) {
                isClickListnerCalled = true;
                String t = TimeFormateConverter.convertTime24To12("" + hourOfDay + ":" + minute);
                tvNotificationTime.setText(t);
            }
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            // TODO Auto-generated methodIndex stub
            super.onDismiss(dialog);
            inProcess = false;
        }
    }

    public void onSaveNotification(View view) {

        alarmObj.setAlarmOptionIndex(AlarmSharedPref.ALARM_PRAYERS_SOUND[posPrayer], indexSoundOption);

        String t = tvNotificationTime.getText().toString();
        TimeEditPref timeEditPref = new TimeEditPref(this);
        timeEditPref.setAlarmNotifyTime(TimeEditPref.ALARMS_TIME_PRAYERS[posPrayer], t);

        AlarmSharedPref mAlarmSharedPref = new AlarmSharedPref(this);
        mAlarmSharedPref.saveAlarm(AlarmSharedPref.CHK_PRAYERS[posPrayer]);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_PRAYER_INDEX, posPrayer);
        setResult(RESULT_OK, intent);
        finish();
    }


    public void onCancelNotification(View view) {
        finish();
    }

}
