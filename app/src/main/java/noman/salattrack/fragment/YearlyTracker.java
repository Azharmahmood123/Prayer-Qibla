package noman.salattrack.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.quranreading.qibladirection.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import noman.CommunityGlobalClass;
import noman.salattrack.activity.SalatTracking;
import noman.salattrack.database.SalatTrackerDatabase;
import noman.salattrack.model.SalatModel;

/**
 * Created by Administrator on 3/20/2017.
 */

public class YearlyTracker extends Fragment {
    SalatTracking mSalatTracking;
    View rootView;
    int curDate, curMonth, curYear, curUserId;
    Calendar curCalendar;


    int late, missed, pray;


    public static YearlyTracker newInstance(SalatTracking mSalatTracking) {
        YearlyTracker myFragment = new YearlyTracker();
        myFragment.mSalatTracking = mSalatTracking;
        return myFragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_yearly_tracker, container, false);


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

    private DatePickerDialog customDatePicker() {
        DatePickerDialog dpd = new DatePickerDialog(mSalatTracking, calenderDialog,
                curYear, curMonth - 1, curDate);
        try {

            Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField
                            .get(dpd);
                    Field datePickerFields[] = datePickerDialogField.getType()
                            .getDeclaredFields();
                    for (Field datePickerField : datePickerFields) {
                        if ("mDayPicker".equals(datePickerField.getName())
                                || "mDaySpinner".equals(datePickerField
                                .getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                        if ("mMonthpicker".equals(datePickerField.getName()) || "mMonthSpinner".equals(datePickerField
                                .getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }

            }
        } catch (Exception ex) {
        }
        return dpd;
    }

    private void operateDateTextView() {
        LinearLayout dateContainer = (LinearLayout) rootView.findViewById(R.id.ln_prayer_text);
        dateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = customDatePicker();
                datePickerDialog.show();
            }
        });

        refreshData();
    }

    public void refreshData() {
        curYear = curCalendar.get(Calendar.YEAR);
        curMonth = curCalendar.get(Calendar.MONTH) + 1;
        curDate = curCalendar.get(Calendar.DAY_OF_MONTH);


        // TextView tvDate = (TextView) rootView.findViewById(R.id.txt_date);
        TextView tvMonth = (TextView) rootView.findViewById(R.id.txt_month);
        TextView tvYear = (TextView) rootView.findViewById(R.id.txt_year);
        // tvDate.setText("" + curDate);
        tvMonth.setText("" + CommunityGlobalClass.getMonthName(curMonth));
        tvYear.setText("" + curYear);

        pray = 0;
        late = 0;
        missed = curCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
        curUserId = CommunityGlobalClass.mSignInRequests.getUser_id();

        getSingleRecord(curYear, curUserId);
    }

    private void drawPieGraph() {
        BarGraph pg = (BarGraph) rootView.findViewById(R.id.bar_yearly);

        ArrayList<Bar> points = new ArrayList<Bar>();
        //assert points != null;
        Bar d = new Bar();
        d.setColor(getResources().getColor(R.color.prayed_color));
        d.setName(getResources().getString(R.string.txt_prayer));
        d.setValue(pray);


        Bar d2 = new Bar();
        d2.setColor(getResources().getColor(R.color.late_color));
        d2.setName(getResources().getString(R.string.txt_late));
        d2.setValue(late);


        Bar d3 = new Bar();
        d3.setColor(getResources().getColor(R.color.missed_color));
        d3.setName(getResources().getString(R.string.txt_missed));
        d3.setValue(missed);


        points.add(d);
        points.add(d2);
        points.add(d3);
        pg.setShowBarText(false);

        pg.setBars(points);


        TextView tvPray = (TextView) rootView.findViewById(R.id.txt_prayer);
        TextView tvLate = (TextView) rootView.findViewById(R.id.txt_late);
        TextView tvMissed = (TextView) rootView.findViewById(R.id.txt_missed);

        tvPray.setText(getPercentage((float) pray) + " %");
        tvLate.setText(getPercentage((float) late) + " %");
        tvMissed.setText(getPercentage((float) missed) + " %");

    }

    public void getSingleRecord(int yearDB, int userID) {

        SalatTrackerDatabase salatTrackerDatabase = new SalatTrackerDatabase(mSalatTracking);
        SalatModel mSalatModel = salatTrackerDatabase.getPrayedCountYearly(yearDB, userID, 1);//Late
        if (mSalatModel != null) {
            late = late + mSalatModel.getFajar();
            late = late + mSalatModel.getZuhar();
            late = late + mSalatModel.getAsar();
            late = late + mSalatModel.getMagrib();
            late = late + mSalatModel.getIsha();

        }

        mSalatModel = salatTrackerDatabase.getPrayedCountYearly(yearDB, userID, 2);//prayed
        if (mSalatModel != null) {
            pray = pray + mSalatModel.getFajar();
            pray = pray + mSalatModel.getZuhar();
            pray = pray + mSalatModel.getAsar();
            pray = pray + mSalatModel.getMagrib();
            pray = pray + mSalatModel.getIsha();
        }

        missed = missed - (pray + late);
        drawPieGraph();
        calcualteAverage();
    }


    void calcualteAverage() {
        TextView tvAverage = (TextView) rootView.findViewById(R.id.txt_average);
        tvAverage.setText(getPercentage((float) (late + pray)) + " %");
    }

    String getPercentage(float total1) {
        float total = total1;
        float per = total / 365;
        double per1 = per * 100;
        String no = String.format("%.2f", per1);
        return no;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (rootView != null) {
            refreshData();
        }
    }
}
