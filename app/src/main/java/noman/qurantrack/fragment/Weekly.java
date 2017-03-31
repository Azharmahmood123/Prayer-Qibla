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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import noman.CommunityGlobalClass;
import noman.qurantrack.activity.QuranTracker;
import noman.qurantrack.adapter.QuranTrackerAdapter;
import noman.qurantrack.database.QuranTrackerDatabase;
import noman.qurantrack.model.QuranTrackerModel;
import noman.qurantrack.model.TargetModel;
import noman.qurantrack.sharedpreference.QuranTrackerPref;


/**
 * Created by Administrator on 3/28/2017.
 */

public class Weekly extends Fragment {
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

    public static Weekly newInstance(QuranTracker mQuranTracker) {
        Weekly myFragment = new Weekly();
        myFragment.mQuranTracker = mQuranTracker;
        myFragment.curCalendar = Calendar.getInstance();
        myFragment.quranTrackerDatabase = new QuranTrackerDatabase(mQuranTracker);
        myFragment.mQuranTrackerModelList = new ArrayList<>();
        myFragment.ayahReads = new ArrayList<>();

        return myFragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_weekly_quran_track, container, false);


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


        //Clear previous date data
        mQuranTrackerModelList.clear();
        ayahReads.clear();

        //Draw Chart
        openChart();

        //Refresh Adapter in recyclerlist

        QuranTrackerAdapter mAdapter = new QuranTrackerAdapter(mQuranTracker, mQuranTrackerModelList);
        mQuranRec.setAdapter(mAdapter);


    }

    public int get7DaysBackFromCurrent(int date)// (-7 to -1)
    {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.YEAR, curYear);
        now.set(Calendar.MONTH, curMonth - 1);
        now.set(Calendar.DAY_OF_MONTH, curDate);

        now.add(Calendar.DATE, date);

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.US);
        String weekDay = dayFormat.format(now.getTime());

        final int calDate, calMonth, calYear, userId;
        calDate = now.get(Calendar.DATE);
        calMonth = now.get(Calendar.MONTH) + 1;
        calYear = now.get(Calendar.YEAR);
        userId = 0;//CommunityGlobalClass.mSignInRequests.getUser_id();

        //Get record from database
        QuranTrackerModel mQuranTrackerModel = quranTrackerDatabase.getQuranTrackModel(calDate, calMonth, calYear, userId);
        if (mQuranTrackerModel != null) {
            ayahReads.add(mQuranTrackerModel.getVerses());
            mQuranTrackerModelList.add(mQuranTrackerModel);
        } else {
            ayahReads.add(0);

            //Add default values to just fill up the dates in recycler view
            mQuranTrackerModel = new QuranTrackerModel();
            mQuranTrackerModel.setSurahNo(0);
            mQuranTrackerModel.setAyahNo(0);
            mQuranTrackerModel.setVerses(0);
            mQuranTrackerModel.setDate(calDate);
            mQuranTrackerModel.setMonth(calMonth);
            mQuranTrackerModel.setYear(calYear);
            mQuranTrackerModelList.add(mQuranTrackerModel);
        }

        return calDate;
    }

    public int[] getDates() {
        int[] x = new int[7];
        for (int i = -3; i < 4; i++) //loop against dates
        {
            if (i == -3)
                x[0] = get7DaysBackFromCurrent(i);
            else if (i == -2)
                x[1] = get7DaysBackFromCurrent(i);
            else if (i == -1)
                x[2] = get7DaysBackFromCurrent(i);
            else
                x[i + 3] = get7DaysBackFromCurrent(i);


        }
        return x;
    }

    private void openChart() {
        int[] x = getDates();
        ArrayList<Integer> income = ayahReads;
        XYSeries incomeSeries = new XYSeries("");
        for (int i = 0; i < x.length; i++) {
            incomeSeries.add(i, income.get(i));
        }
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(incomeSeries);
        // Ceating XYSeriesRenderer to customize incomeSeries
        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(getResources().getColor(R.color.black));
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

        for (int i = 0; i < x.length; i++) {
            multiRenderer.addXTextLabel(i, "" + x[i]);//adding points value diffrents
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