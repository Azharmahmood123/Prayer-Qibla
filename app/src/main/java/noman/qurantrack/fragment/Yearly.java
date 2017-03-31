package noman.qurantrack.fragment;

import android.app.DatePickerDialog;
import android.graphics.Color;
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
import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
import com.quranreading.qibladirection.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import noman.CommunityGlobalClass;
import noman.qurantrack.activity.QuranTracker;
import noman.qurantrack.database.QuranTrackerDatabase;


/**
 * Created by Administrator on 3/28/2017.
 */

public class Yearly extends Fragment {

    QuranTracker mQuranTracker;
    View rootView;
    int curDate, curMonth, curYear, curUserId;
    Calendar curCalendar;
String[] months =new String[]{"jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"};

    public static Yearly newInstance(QuranTracker mQuranTracker) {
        Yearly myFragment = new Yearly();
        myFragment.mQuranTracker = mQuranTracker;
        return myFragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_yearly_quran_track, container, false);
        curCalendar = Calendar.getInstance();
        operateDateTextView();


        drawPieGraph();
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
        LinearLayout dateContainer = (LinearLayout) rootView.findViewById(R.id.ln_date_container_current);
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


        TextView tvDate = (TextView) rootView.findViewById(R.id.txt_date);
        TextView tvMonth = (TextView) rootView.findViewById(R.id.txt_month);
        TextView tvYear = (TextView) rootView.findViewById(R.id.txt_year);
        tvDate.setText("" + curDate);
        tvMonth.setText("" + CommunityGlobalClass.getMonthName(curMonth));
        tvYear.setText("" + curYear);

        drawPieGraph();
    }


    private DatePickerDialog customDatePicker() {
        DatePickerDialog dpd = new DatePickerDialog(mQuranTracker, calenderDialog,
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

    private void drawPieGraph() {
        QuranTrackerDatabase mQuranTrackerDatabase=new QuranTrackerDatabase(mQuranTracker);

        BarGraph pg = (BarGraph) rootView.findViewById(R.id.graph);
        pg.setQuranTracker(true);
        ArrayList<Bar> points = new ArrayList<Bar>();
        //assert points != null;

        for(int i=0;i<12;i++) {
            Bar d = new Bar();
            d.setColor(getResources().getColor(R.color.prayed_color));
            d.setName(months[i].toUpperCase());
          d.setValue(mQuranTrackerDatabase.getMonthSumVerse(i + 1, curYear, curUserId));



                points.add(d);
        }

        pg.setShowBarText(false);
        pg.setBars(points);


    }

    @Override
    public void onResume() {
        super.onResume();
        if(rootView!=null)
        {
            refreshData();
        }
    }
}