package com.quranreading.qibladirection;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.SwitchCompat;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.quranreading.sharedPreference.AlarmSharedPref;
import com.quranreading.sharedPreference.LocationPref;
import com.quranreading.sharedPreference.PrayerTimeSettingsPref;

import java.util.HashMap;

public class FirstOptionsActivity extends AppCompatActivity {

    private boolean[] chkAlarmsSaved = new boolean[6];
    private SwitchCompat[] switchPrayers = new SwitchCompat[6];
    AlarmSharedPref mAlarmSharedPref;

    RadioGroup rgJuristic;
    int juristic = 2;
    AlertDialog dailog;
    LocationPref locationPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated methodIndex stub
        super.onCreate(savedInstanceState);

        mAlarmSharedPref = new AlarmSharedPref(this);
        locationPref = new LocationPref(FirstOptionsActivity.this);
        HashMap<String, Boolean> alarm = mAlarmSharedPref.checkAlarms();

        for (int index = 0; index < chkAlarmsSaved.length; index++) {
            // chkAlarmsSaved[index] = alarm.get(AlarmSharedPref.CHK_PRAYERS[index]);

            // to show Maghrib alarm time as Enabled
            if (index == 4) {
                chkAlarmsSaved[index] = true;
            } else {
                chkAlarmsSaved[index] = false;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.layout_first_options);
        builder.setPositiveButton(R.string.okay, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated methodIndex stub

                for (int i = 0; i < switchPrayers.length; i++) {
                    chkAlarmsSaved[i] = switchPrayers[i].isChecked();
                    mAlarmSharedPref.setAlarmsState(AlarmSharedPref.CHK_PRAYERS[i], chkAlarmsSaved[i]);
                }

                AppCompatRadioButton rb = (AppCompatRadioButton) dailog.findViewById(rgJuristic.getCheckedRadioButtonId());
                juristic = Integer.parseInt(rb.getTag().toString());

                PrayerTimeSettingsPref namazPrefs = new PrayerTimeSettingsPref(FirstOptionsActivity.this);
                namazPrefs.setJuristic(juristic);
                namazPrefs.setJuristicDefault(juristic);

                locationPref.setFirstSalatLauch();
                finish();
            }
        });

        builder.setNegativeButton(R.string.cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dailog = builder.show();

        switchPrayers[0] = (SwitchCompat) dailog.findViewById(R.id.switch_1);
        switchPrayers[1] = (SwitchCompat) dailog.findViewById(R.id.switch_2);
        switchPrayers[2] = (SwitchCompat) dailog.findViewById(R.id.switch_3);
        switchPrayers[3] = (SwitchCompat) dailog.findViewById(R.id.switch_4);
        switchPrayers[4] = (SwitchCompat) dailog.findViewById(R.id.switch_5);
        switchPrayers[5] = (SwitchCompat) dailog.findViewById(R.id.switch_6);

        rgJuristic = (RadioGroup) dailog.findViewById(R.id.rg_juristic);
        RadioButton rb = (RadioButton) dailog.findViewById(rgJuristic.getCheckedRadioButtonId());
        juristic = Integer.parseInt(rb.getTag().toString());

        for (int i = 0; i < switchPrayers.length; i++) {
            switchPrayers[i].setChecked(chkAlarmsSaved[i]);
        }
    }
}
