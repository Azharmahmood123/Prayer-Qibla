package noman.qurantrack.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.Calendar;

import noman.Ads.AdIntegration;
import noman.CommunityGlobalClass;
import noman.quran.model.JuzConstant;
import noman.qurantrack.database.MarkUpManager;
import noman.qurantrack.database.QuranTrackerDatabase;
import noman.qurantrack.model.QuranTrackerModel;
import noman.qurantrack.model.TargetModel;
import noman.qurantrack.sharedpreference.QuranTrackerPref;


public class AddProgress extends AdIntegration implements View.OnClickListener {
    Calendar calendar;
    int curYear, curMonth, curDate;
    int userID = 0;
    TextView tvDate, tvSurrah, tvAyah;
    int surrahId, ayahId;
    String[] array;
    MarkUpManager markUpManager;
    QuranTrackerDatabase quranTrackerDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran_tracker_progress);

        array = JuzConstant.engSurahName;
        markUpManager = new MarkUpManager(this);
        quranTrackerDatabase = new QuranTrackerDatabase(this);


        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linear_ad));
        }

        LinearLayout btnCross = (LinearLayout) findViewById(R.id.btn_back);
        btnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        calendar = Calendar.getInstance();

        userID = CommunityGlobalClass.mSignInRequests.getUser_id();


        curYear = calendar.get(Calendar.YEAR);
        curMonth = calendar.get(Calendar.MONTH) + 1;
        curDate = calendar.get(Calendar.DAY_OF_MONTH);


        //Set Current Date in boxes
        tvDate = (TextView) findViewById(R.id.txt_date);
        TextView tvMonth = (TextView) findViewById(R.id.txt_month);
        TextView tvYear = (TextView) findViewById(R.id.txt_year);
        tvDate.setText("" + curDate);
        tvMonth.setText("" + CommunityGlobalClass.getMonthName(curMonth));
        tvYear.setText("" + curYear);

        LinearLayout btnSubmit = (LinearLayout) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);

        tvSurrah = (TextView) findViewById(R.id.btn_surah_read);

        tvSurrah.setOnClickListener(this);
        tvAyah = (TextView) findViewById(R.id.txt_ayah_read);
        tvAyah.setOnClickListener(this);


        tvSurrah.setText(getResources().getString(R.string.txt_default_date));
        tvAyah.setText(getResources().getString(R.string.txt_default_date));


        LinearLayout btnDatePicker = (LinearLayout) findViewById(R.id.ln_date_container_current);
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   new DatePickerDialog(AddProgress.this, calenderDialog, curYear, curMonth - 1,
                        curDate).show();*/
            }
        });

        refreshData();

        getLastMaxSurahAndAyah();
    }

    void getLastMaxSurahAndAyah() {

        CardView cardViewLastSave = (CardView) findViewById(R.id.card_last_save);
        cardViewLastSave.setVisibility(View.GONE);
        TextView txtLastSura = (TextView) findViewById(R.id.txt_last_read_surrah);
        TextView txtLastAya = (TextView) findViewById(R.id.txt_last_read_aya);


        QuranTrackerPref mPref = new QuranTrackerPref(this);
        TargetModel modelSaveStartDates = mPref.getLastSaveStartDatePref();
        int lastSaveSurahNo = quranTrackerDatabase.getMaxSurrah(modelSaveStartDates.getDate(), modelSaveStartDates.getMonth(), modelSaveStartDates.getYear()); //also have to check ayah of max surrah number which already read--contiune this work hmmm
        // int lastSaveSurahNo = quranTrackerDatabase.getMaxSurrah();

        int lastSaveAyah = quranTrackerDatabase.getLastAyah(lastSaveSurahNo);

        if (lastSaveSurahNo > 0) {
            cardViewLastSave.setVisibility(View.VISIBLE);
            txtLastSura.setText(getString(R.string.txt_last_surah_save) + " " + JuzConstant.engSurahName[lastSaveSurahNo - 1]);
            txtLastAya.setText(getString(R.string.txt_last_ayah_save) + " " + +lastSaveAyah);
        }


    }


    //Show default date picker android

    DatePickerDialog.OnDateSetListener calenderDialog = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            refreshData();
        }

    };

    public void refreshData() {
        Bundle b = getIntent().getExtras();
        if (b == null) {
            curYear = calendar.get(Calendar.YEAR);
            curMonth = calendar.get(Calendar.MONTH) + 1;
            curDate = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            curYear = b.getInt("year");
            curMonth = b.getInt("month");
            curDate = b.getInt("date");
        }
        QuranTrackerModel model = quranTrackerDatabase.getQuranTrackModel(curDate, curMonth, curYear, userID);
        if (model != null) {
            surrahId = model.getSurahNo() - 1;
            ayahId = model.getAyahNo();

            tvSurrah.setText(array[surrahId]);
            tvAyah.setText("" + ayahId);


        } else {
            surrahId = 0;
            ayahId = 1;


            tvSurrah.setText(getResources().getString(R.string.txt_default_date));
            tvAyah.setText(getResources().getString(R.string.txt_default_date));
        }


        //Testing to save dates
        //tvDate.setText(CommunityGlobalClass.getMonthName(curMonth) + " - " + curDate + " - " + curYear);

    }

    public void getSurrahDialog() {

        AlertDialog a = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle(getString(R.string.txt_select_surrah))
                .setSingleChoiceItems(array, surrahId, null)
                .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        surrahId = selectedPosition;
                        tvSurrah.setText(array[surrahId]);

                        ayahId = 1;
                        // tvAyah.setText("" + 1);
                        tvAyah.setText(getResources().getString(R.string.txt_default_date));

                    }
                }).setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .show();
    }

    public void getSurrahVersesDialog() {

        int endloop = markUpManager.getSurahCount(surrahId + 1);
        CharSequence[] array = new CharSequence[endloop];
        for (int i = 0; i < endloop; i++) {
            array[i] = "Verse: " + (i + 1);
        }
        AlertDialog a = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle(getString(R.string.txt_select_surrah))
                .setSingleChoiceItems(array, ayahId - 1, null)
                .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        ayahId = selectedPosition + 1;
                        tvAyah.setText("" + ayahId);
                    }
                }).setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .show();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_surah_read:


                tvSurrah.setText(getResources().getString(R.string.txt_default_date));
                tvAyah.setText(getResources().getString(R.string.txt_default_date));

                getSurrahDialog();
                break;
            case R.id.txt_ayah_read:
                getSurrahVersesDialog();
                break;
            case R.id.btn_submit:
                String defaultVal = getString(R.string.txt_default_date);
                if (tvSurrah.getText().toString().equals(defaultVal) || tvAyah.getText().toString().equals(defaultVal)) {
                    CommunityGlobalClass.getInstance().showShortToast(getString(R.string.txt_error_no_fill), 800, Gravity.CENTER);
                } else {
                    saveData();
                }
                break;
        }
    }


    public void saveData() {
        int surah = surrahId + 1;

        int curSaveMarker = markUpManager.getQuery1(surah, ayahId);
        int lastSaveMarker = markUpManager.getQuery2();
        int todayCounter = curSaveMarker - lastSaveMarker;

        //Log.e("Today Reading", "" + (todayCounter));
        QuranTrackerPref mPref = new QuranTrackerPref(this);
        TargetModel modelSaveStartDates = mPref.getLastSaveStartDatePref();
        int lastSaveSurahNo = quranTrackerDatabase.getMaxSurrah(modelSaveStartDates.getDate(), modelSaveStartDates.getMonth(), modelSaveStartDates.getYear()); //also have to check ayah of max surrah number which already read-

        if (surah > lastSaveSurahNo) {
            saveInDatabase(surah, curSaveMarker);
        } else if (surah == lastSaveSurahNo) {
            if (ayahId > quranTrackerDatabase.getLastAyah(lastSaveSurahNo)) {
                saveInDatabase(surah, curSaveMarker);
            } else {
                CommunityGlobalClass.getInstance().showShortToast("Already surah saved", 500, Gravity.CENTER);
            }
        } else {
            CommunityGlobalClass.getInstance().showShortToast("Already surah saved", 500, Gravity.CENTER);
        }


    }

    void saveInDatabase(int surah, int todayCounter)

    {
        //Now 0 marker position
        markUpManager.getQuery3();
        //Now save marker position
        markUpManager.getQuery4(surah, ayahId);

        QuranTrackerModel model = new QuranTrackerModel();
        model.setVerses(todayCounter);
        model.setDate(curDate);
        model.setMonth(curMonth);
        model.setYear(curYear);
        model.setUser_id(userID);
        model.setAyahNo(ayahId);
        model.setSurahNo(surah);
        quranTrackerDatabase.insertQuranTrackerData(true, model);

        this.finish();
    }

}
