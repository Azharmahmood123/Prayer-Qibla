package noman.qurantrack.fragment;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.R;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import noman.CommunityGlobalClass;
import noman.qurantrack.activity.QuranTracker;
import noman.qurantrack.adapter.QuranTrackerAdapter;
import noman.qurantrack.database.QuranTrackerDatabase;
import noman.qurantrack.model.QuranTrackerModel;


/**
 * Created by Administrator on 3/28/2017.
 */

public class Monthly extends Fragment {
    QuranTrackerDatabase quranTrackerDatabase;
    QuranTracker mQuranTracker;
    View rootView;
    int curDate, curMonth, curYear, curUserId;
    Calendar curCalendar;
    ImageView imgGraph, imgTabular;

    private View mChart;

    RecyclerView mQuranRec;

    List<QuranTrackerModel> mQuranTrackerModelList;
    View tabularContainer;
    ArrayList<Integer> ayahReads;

    public static Monthly newInstance(QuranTracker mQuranTracker) {
        Monthly myFragment = new Monthly();
        myFragment.mQuranTracker = mQuranTracker;
        myFragment.curCalendar = Calendar.getInstance();
        myFragment.quranTrackerDatabase = new QuranTrackerDatabase(mQuranTracker);
        myFragment.mQuranTrackerModelList = new ArrayList<>();
        myFragment.ayahReads = new ArrayList<>();
        myFragment.curUserId = CommunityGlobalClass.mSignInRequests.getUser_id();
        return myFragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_monthly_quran_track, container, false);


        mQuranRec = (RecyclerView) rootView.findViewById(R.id.rec_quran_tracker);
        tabularContainer = (View) rootView.findViewById(R.id.tabular_container);
        imgGraph = (ImageView) rootView.findViewById(R.id.img_graph);
        imgTabular = (ImageView) rootView.findViewById(R.id.img_tabular);

        imgGraph.setColorFilter(getResources().getColor(R.color.colorPrimary));

        imgGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgGraph.setColorFilter(getResources().getColor(R.color.colorPrimary));
                imgTabular.setColorFilter(getResources().getColor(R.color.gray_dark));

                operateDateTextView();
                tabularContainer.setVisibility(View.GONE);
            }
        });
        imgTabular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgTabular.setColorFilter(getResources().getColor(R.color.colorPrimary));
                imgGraph.setColorFilter(getResources().getColor(R.color.gray_dark));

                tabularContainer.setVisibility(View.VISIBLE);
                createTabularData();


            }
        });
        operateDateTextView();

        return rootView;
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
                    }
                }

            }
        } catch (Exception ex) {
        }
        return dpd;
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


    public void createTabularData() {

        final LinearLayoutManager layoutManager = new LinearLayoutManager(mQuranTracker);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mQuranRec.setLayoutManager(layoutManager);
        mQuranRec.setItemAnimator(new DefaultItemAnimator());

        QuranTrackerAdapter mAdapter = new QuranTrackerAdapter(mQuranTracker, mQuranTrackerModelList);
        mQuranRec.setAdapter(mAdapter);
    }

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


        //Clear previous date data
        mQuranTrackerModelList.clear();
        ayahReads.clear();

        //Draw Chart
        openChart();

        //Refresh Adapter in recyclerlist

        QuranTrackerAdapter mAdapter = new QuranTrackerAdapter(mQuranTracker, mQuranTrackerModelList);
        mQuranRec.setAdapter(mAdapter);


    }

    public int get7DaysBackFromCurrent(int start, int end) {
        //Get record from database
        int sumWeeklyVerse = quranTrackerDatabase.getWeeklySumVerse(start, end, curMonth, curYear, curUserId);
        ayahReads.add(sumWeeklyVerse);
      /*  if (mQuranTrackerModel != null) {
            ayahReads.add(mQuranTrackerModel.getVerses());
            mQuranTrackerModelList.add(mQuranTrackerModel);
        } else {
            ayahReads.add(0);

            //Add default values to just fill up the dates in recycler view
            mQuranTrackerModel = new QuranTrackerModel();
            mQuranTrackerModel.setSurahNo(0);
            mQuranTrackerModel.setAyahNo(0);
            mQuranTrackerModel.setVerses(0);
            mQuranTrackerModel.setDate(end);
            mQuranTrackerModel.setMonth(curMonth);
            mQuranTrackerModel.setYear(curYear);
            mQuranTrackerModelList.add(mQuranTrackerModel);
        }*/

        return end;
    }

    public int daysInMonth(int monthNumber, boolean isLeapYear) {
        if (monthNumber < 1 || monthNumber > 12)
            return 0;
        switch (monthNumber) {
            case 2:
                return isLeapYear ? 29 : 28;
            case 4:
            case 6:
            case 9:
            case 11:// intentional fall-through
                return 30;
            default:
                return 31;
        }// end switch
    }// end method days in month

    public ArrayList<Integer> getMonthlyDates() {

        Calendar now = Calendar.getInstance();
        now.set(Calendar.YEAR, curYear);
        now.set(Calendar.MONTH, curMonth - 1);
        now.add(Calendar.DATE, 1);
        int maxDays = daysInMonth(curMonth,false);

        int maxWeeks = now.getActualMaximum(Calendar.WEEK_OF_MONTH);
        ArrayList<Integer> x = new ArrayList<Integer>();


        for (int i = 7; i <= maxDays; i = i + 7) //loop against dates
        {
            if (i == 28 && curMonth !=2)//28 is the end of the week date so we have to show upto maxDays
            {

                for (int i1 = 29; i1 <= maxDays; i1 = i++) {
                    x.add(get7DaysBackFromCurrent(i1, i1));
                }
            } else {
                x.add(get7DaysBackFromCurrent(i - 7, i));
            }
        }
        return x;
    }

    private void openChart() {
        ArrayList<Integer> x = getMonthlyDates();
        ArrayList<Integer> income = ayahReads;
        XYSeries incomeSeries = new XYSeries("Days");
        for (int i = 0; i < x.size(); i++) {
            incomeSeries.add(i, income.get(i));
        }
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(incomeSeries);
        // Ceating XYSeriesRenderer to customize incomeSeries
        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(getResources().getColor(R.color.colorPrimary));
        incomeRenderer.setPointStyle(PointStyle.CIRCLE);
        incomeRenderer.setFillPoints(true);
        incomeRenderer.setLineWidth(2);
        incomeRenderer.setAnnotationsTextSize(20);
        incomeRenderer.setDisplayChartValues(true);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setZoomButtonsVisible(false);
        multiRenderer.setPanEnabled(false, false);
        multiRenderer.setZoomEnabled(false, false);
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(getResources().getColor(R.color.color_background));
        multiRenderer.setAxisTitleTextSize(20);
        multiRenderer.setChartTitleTextSize(20);
        multiRenderer.setLabelsTextSize(20);
        multiRenderer.setLegendTextSize(20);
        multiRenderer.setShowGrid(true);
        multiRenderer.setMargins(new int[]{50, 25, 25, 25});
        multiRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));//transparent
        //     multiRenderer.setAxesColor(getResources().getColor(R.color.missed_color));
        //    multiRenderer.setLabelsColor(getResources().getColor(R.color.colorPrimary));
        multiRenderer.setXLabelsColor(getResources().getColor(R.color.black));
        multiRenderer.setYLabelsColor(0, getResources().getColor(R.color.black));
        multiRenderer.setXAxisColor(getResources().getColor(R.color.black));
        multiRenderer.setYAxisColor(getResources().getColor(R.color.black));
        //  multiRenderer.setXAxisMax(x[6]);//get End date of the week
        //  multiRenderer.setXAxisMin(x[0]);//get Start date of the week

        multiRenderer.setYAxisMin(0);
        multiRenderer.setYAxisMax(6236);//Total ayah of quran
        multiRenderer.setYLabelsAngle(270);

        for (int i = 0; i < x.size(); i++) {
            multiRenderer.addXTextLabel(i, "" + x.get(i));//adding points value diffrents
        }
        multiRenderer.addSeriesRenderer(incomeRenderer);


        LinearLayout chartContainer = (LinearLayout) rootView.findViewById(R.id.chart);
        chartContainer.removeAllViews();

        mChart = ChartFactory.getLineChartView(mQuranTracker, dataset, multiRenderer);
        chartContainer.addView(mChart);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rootView != null) {
            refreshData();
        }
    }
}