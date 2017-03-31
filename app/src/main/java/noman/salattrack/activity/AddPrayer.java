package noman.salattrack.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.Calendar;

import noman.Ads.AdIntegration;
import noman.CommunityGlobalClass;
import noman.community.model.DeletePrayerRequest;
import noman.salattrack.database.SalatTrackerDatabase;
import noman.salattrack.model.SalatModel;


/**
 * Created by Administrator on 3/16/2017.
 */

public class AddPrayer extends AdIntegration {
    private Calendar calendar;
    TextView tvDate;
    int dateDB, monthDB, yearDB;
    int status_prayer = 0;
    SalatModel mSalatModel;
    SalatTrackerDatabase salatTrackerDatabase;
    int userID = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prayer_track);

        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linear_ad));
        }

        LinearLayout btnCross = (LinearLayout) findViewById(R.id.btn_cross);
        btnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        LinearLayout btnSave = (LinearLayout) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInDatabase();
            }
        });

        calendar = Calendar.getInstance();
        mSalatModel = new SalatModel();
        salatTrackerDatabase = new SalatTrackerDatabase(this);

        userID = CommunityGlobalClass.mSignInRequests.getUser_id();
        dateContainer();
        refreshData();
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

            //Update date
            refreshData();

        }

    };

    public void dateContainer() {
        tvDate = (TextView) findViewById(R.id.txt_date);

        LinearLayout lnDate = (LinearLayout) findViewById(R.id.ln_date);
        lnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  new DatePickerDialog(AddPrayer.this, calenderDialog, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();*/
                new DatePickerDialog(AddPrayer.this, calenderDialog, yearDB,monthDB -1,
                       dateDB).show();

            }
        });
    }
    public void dynamicContainer(LinearLayout ln, String name, final int tag) {

        final View layout2 = LayoutInflater.from(this).inflate(R.layout.dynamic_conatiner_add_prayer_salat, ln, false);

        TextView tvName = (TextView) layout2.findViewById(R.id.txt_namaz_name);
        tvName.setText(name);

        LinearLayout btnPrayed = (LinearLayout) layout2.findViewById(R.id.ln_prayed);
        LinearLayout btnLate = (LinearLayout) layout2.findViewById(R.id.ln_late);
        LinearLayout btnMissed = (LinearLayout) layout2.findViewById(R.id.ln_missed);

        if (mSalatModel != null) {

            //get value against stored data like fajar:2 //PRayed

            status_prayer = whichContainerClick(tag);
            adjustBackgroundPostion(layout2, status_prayer);
            setEntriesNamaz(tag, status_prayer); //in db values

        } else {
            adjustBackgroundPostion(layout2, 0);//in first time
            setEntriesNamaz(tag, 0);//set Default
        }


        btnPrayed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status_prayer = 2;
                adjustBackgroundPostion(layout2, status_prayer);
                setEntriesNamaz(tag, status_prayer);
            }
        });

        btnLate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status_prayer = 1;
                adjustBackgroundPostion(layout2, status_prayer);
                setEntriesNamaz(tag, status_prayer);

            }
        });
        btnMissed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                status_prayer = 0;
                adjustBackgroundPostion(layout2, status_prayer);
                setEntriesNamaz(tag, status_prayer);
            }
        });


        ln.addView(layout2);

    }
    public void adjustBackgroundPostion(View layout2, int pos) {
        LinearLayout btnPrayed = (LinearLayout) layout2.findViewById(R.id.ln_prayed);
        LinearLayout btnLate = (LinearLayout) layout2.findViewById(R.id.ln_late);
        LinearLayout btnMissed = (LinearLayout) layout2.findViewById(R.id.ln_missed);

        btnPrayed.setBackgroundColor(0);
        btnLate.setBackgroundColor(0);
        btnMissed.setBackgroundColor(0);


        switch (pos) {
            case 2:
                btnPrayed.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                break;

            case 1:
                btnLate.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                break;

            case 0:
                btnMissed.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                break;
        }

    }
    public void containerNamazDynamic() {
        LinearLayout containerPayer = (LinearLayout) findViewById(R.id.container_prayer);
        String[] namaz = {getString(R.string.txt_fajr), getString(R.string.txt_zuhr), getString(R.string.txt_asar),
                getString(R.string.txt_maghrib), getString(R.string.txt_isha)};

        containerPayer.removeAllViews();
        for (int i = 0; i < namaz.length; i++) {
            dynamicContainer(containerPayer, namaz[i], i);
        }
    }
    public void checkIfRecordExit() {


        mSalatModel = salatTrackerDatabase.getSalatModel(dateDB, monthDB, yearDB, userID);


        if (mSalatModel != null) {
            tvDate.setText(CommunityGlobalClass.getMonthName(monthDB) + " - " + dateDB + " - " + yearDB);
        } else {
            tvDate.setText(CommunityGlobalClass.getMonthName(monthDB) + " - " + dateDB + " - " + yearDB);
            mSalatModel = new SalatModel();
        }

    }
    public int whichContainerClick(int tag) {
        int id = 0;
        if (mSalatModel != null) {
            switch (tag) {
                case 0:
                    id = mSalatModel.getFajar();
                    break;
                case 1:
                    id = mSalatModel.getZuhar();
                    break;
                case 2:
                    id = mSalatModel.getAsar();
                    break;
                case 3:
                    id = mSalatModel.getMagrib();
                    break;
                case 4:
                    id = mSalatModel.getIsha();
                    break;

            }
        }

        return id;
    }
    public void setEntriesNamaz(int tag, int sec_id) {


        switch (tag) {
            case 0:
                mSalatModel.setFajar(sec_id);
                break;
            case 1:
                mSalatModel.setZuhar(sec_id);
                break;
            case 2:
                mSalatModel.setAsar(sec_id);
                break;
            case 3:
                mSalatModel.setMagrib(sec_id);
                break;
            case 4:
                mSalatModel.setIsha(sec_id);
                break;


        }

    }
    public void refreshData(){

        Bundle b=getIntent().getExtras();
        if(b == null) {
            yearDB = calendar.get(Calendar.YEAR);
            monthDB = calendar.get(Calendar.MONTH) + 1;
            dateDB = calendar.get(Calendar.DAY_OF_MONTH);
        }
        else
        {
            yearDB=b.getInt("year");
            monthDB=b.getInt("month");
            dateDB=b.getInt("date");
        }

        checkIfRecordExit();
        containerNamazDynamic();
    }
    public void saveInDatabase() {
        mSalatModel.setDate(dateDB);
        mSalatModel.setMonth(monthDB);
        mSalatModel.setYear(yearDB);
        mSalatModel.setUser_id(CommunityGlobalClass.mSignInRequests.getUser_id());//Not connected to database


        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle(getString(R.string.dilog_title_salat));
        builder.setMessage(getString(R.string.dilog_msg_salat));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            sendDataToServer();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();


    }

    void sendDataToServer()
    {
        salatTrackerDatabase.insertSalatData(mSalatModel); //in local database
        AddPrayer.this.finish();
    }
}
