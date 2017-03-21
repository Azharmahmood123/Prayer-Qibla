package noman.salattrack.fragment;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import noman.CommunityGlobalClass;
import noman.salattrack.activity.AddPrayer;
import noman.salattrack.activity.SalatTracking;
import noman.salattrack.database.SalatTrackerDatabase;
import noman.salattrack.model.SalatModel;


public class WeeklyTracker extends Fragment {

    SalatTracking mSalatTracking;
    View rootView;

    int curDate, curMonth, curYear, curUserId;
    Calendar curCalendar;


    int late, missed, pray;

    public static WeeklyTracker newInstance(SalatTracking mSalatTracking) {
        WeeklyTracker myFragment = new WeeklyTracker();
        myFragment.mSalatTracking = mSalatTracking;
        return myFragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_weekly_tracker, container, false);


        curCalendar = Calendar.getInstance();

        operateDateTextView();

        return rootView;
    }

    //Show default date picker android
    DatePickerDialog.OnDateSetListener calenderDialog = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            curCalendar.set(Calendar.YEAR, year);
            curCalendar.set(Calendar.MONTH, monthOfYear);
            curCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            //Update date
            refreshData();

        }

    };

    private void operateDateTextView() {
        LinearLayout dateContainer = (LinearLayout) rootView.findViewById(R.id.ln_prayer_text);
        dateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  new DatePickerDialog(getActivity(), calenderDialog, curCalendar
                        .get(Calendar.YEAR), curCalendar.get(Calendar.MONTH),
                        curCalendar.get(Calendar.DAY_OF_MONTH)).show();*/
                new DatePickerDialog(getActivity(), calenderDialog, curYear, curMonth - 1,
                        curDate).show();
            }
        });

        refreshData();
    }

    public void refreshData() {
        curYear = curCalendar.get(Calendar.YEAR);
        curMonth = curCalendar.get(Calendar.MONTH) + 1;
        curDate = curCalendar.get(Calendar.DAY_OF_MONTH);


        TextView tvDate = (TextView) rootView.findViewById(R.id.txt_date);
        TextView tvMonth = (TextView) rootView.findViewById(R.id.txt_month);
        TextView tvYear = (TextView) rootView.findViewById(R.id.txt_year);
        tvDate.setText("" + curDate);
        tvMonth.setText("" + CommunityGlobalClass.getMonthName(curMonth));
        tvYear.setText("" + curYear);

        operateContainer();
    }

    public void operateContainer() {
        LinearLayout conatinerBar = (LinearLayout) rootView.findViewById(R.id.container_prayer_bar);
        conatinerBar.removeAllViews();

        pray = 0;
        late = 0;
        missed = 0;

        for (int i = -3; i < 4; i++) //loop against dates
        {
            get7DaysBackFromCurrent(conatinerBar, i);

            if (i == 3) {
                calcualteAverage();
            }
        }

    }

    public void get7DaysBackFromCurrent(LinearLayout ln, int date)// (-7 to -1)
    {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.YEAR, curYear);
        now.set(Calendar.MONTH, curMonth - 1);
        now.set(Calendar.DAY_OF_MONTH, curDate);

        now.add(Calendar.DATE, date);

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.US);
        String weekDay = dayFormat.format(now.getTime());

        //Log.e("date", "Day:" + weekDay + " date=" + now.get(Calendar.DATE) + " and month= " + (now.get(Calendar.MONTH) + 1));

        final int calDate, calMonth, calYear, userId;

        calDate = now.get(Calendar.DATE);
        calMonth = now.get(Calendar.MONTH) + 1;
        calYear = now.get(Calendar.YEAR);
        userId = 0;

        //Get record from database
        SalatModel mSalatModel = getSingleRecord(calDate, calMonth, calYear, userId);


        //Add views in layout container
        final View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.dynamic_container_salat_chart, ln, false);
        TextView tv_date = (TextView) layout2.findViewById(R.id.txt_date);
        TextView tv_day = (TextView) layout2.findViewById(R.id.txt_day_name);
        tv_day.setText(weekDay);
        if (calDate == curDate) {
            tv_date.setTextColor(getResources().getColor(R.color.ColorPrimary));
            tv_day.setTextColor(getResources().getColor(R.color.ColorPrimary));
        }
        tv_date.setText("" + calDate);


        //Status of Prayers
        TextView[] tv = new TextView[5];
        tv[0] = (TextView) layout2.findViewById(R.id.txt_fajar_status);
        tv[1] = (TextView) layout2.findViewById(R.id.txt_zuhar_status);
        tv[2] = (TextView) layout2.findViewById(R.id.txt_asar_status);
        tv[3] = (TextView) layout2.findViewById(R.id.txt_magrib_status);
        tv[4] = (TextView) layout2.findViewById(R.id.txt_isha_status);

        if (mSalatModel != null) {
            adjustBackgroundColor(tv[0], mSalatModel.getFajar());
            adjustBackgroundColor(tv[1], mSalatModel.getZuhar());
            adjustBackgroundColor(tv[2], mSalatModel.getAsar());
            adjustBackgroundColor(tv[3], mSalatModel.getMagrib());
            adjustBackgroundColor(tv[4], mSalatModel.getIsha());
        }


        //Clicking the dots cont
        LinearLayout[] container = new LinearLayout[5];
        container[0] = (LinearLayout) layout2.findViewById(R.id.ln_fajar);
        container[1] = (LinearLayout) layout2.findViewById(R.id.ln_zuhar);
        container[2] = (LinearLayout) layout2.findViewById(R.id.ln_asar);
        container[3] = (LinearLayout) layout2.findViewById(R.id.ln_magrib);
        container[4] = (LinearLayout) layout2.findViewById(R.id.ln_isha);

        for (int i = 0; i < container.length; i++) {
            container[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendIntent(calDate, calMonth, calYear
                    );
                }
            });
        }


        ln.addView(layout2);
    }

    void sendIntent(int calDate, int calMonth, int calYear) {
        Intent i = new Intent(getActivity(), AddPrayer.class);
        i.putExtra("date", calDate);
        i.putExtra("month", calMonth);
        i.putExtra("year", calYear);
        getActivity().startActivity(i);

    }

    public SalatModel getSingleRecord(int dateDB, int monthDB, int yearDB, int userID) {
        SalatTrackerDatabase salatTrackerDatabase = new SalatTrackerDatabase(getActivity());
        SalatModel mSalatModel = salatTrackerDatabase.getSalatModel(dateDB, monthDB, yearDB, userID);
        if (mSalatModel != null) {
            return mSalatModel;
        } else {
            return null;
        }

    }

    public void adjustBackgroundColor(TextView txt, int id) {

        txt.setBackgroundColor(0);
        switch (id) {
            case 2:
                txt.setBackgroundColor(getResources().getColor(R.color.prayed_color));
                pray = pray + 1;
                break;

            case 1:
                txt.setBackgroundColor(getResources().getColor(R.color.late_color));
                late = late + 1;
                break;

            case 0:
                txt.setBackgroundColor(getResources().getColor(R.color.missed_color));
                missed = missed + 1;
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (rootView != null) {
            refreshData();
        }
    }

    void calcualteAverage() {
        TextView tvAverage = (TextView) rootView.findViewById(R.id.txt_average);

        tvAverage.setText(getPercentage((float)late+pray) + " %");
    }
    String getPercentage(float total1)
    {
        float total = total1;
        float per = total / 35;
        double    per1 = per * 100;
        String no = String.format("%.2f", per1);
        return no;
    }
}
