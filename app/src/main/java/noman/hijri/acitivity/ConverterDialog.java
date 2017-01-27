package noman.hijri.acitivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import noman.CommunityGlobalClass;
import noman.hijri.helper.DateConverter;
import noman.hijri.wheelpicker.ArrayWheelAdapter;
import noman.hijri.wheelpicker.NumericWheelAdapter;
import noman.hijri.wheelpicker.OnWheelScrollListener;
import noman.hijri.wheelpicker.WheelView;

public class ConverterDialog extends AppCompatActivity {
    int hijriYear, curHijriDay, curHijriMonth, curHijriYear, curGregorianDay, curGregorianMonth, curGregorianYear;
    DateConverter dateConverter;
    WheelView gregorian_day, gregorian_month, gregorian_year,
            hijri_day, hijri_month, hijri_year;
    private int NoOfYear = 50, prevMax = 0, newMax = 0;
    boolean g_scroll = false, h_scroll = false;
    int textSize;
    Button btnConvert, btnCancel;
    RadioGroup groupRadio;
    LinearLayout linearGeogrian, linearHijri;
    int HijriDay, HijriMonth, HijriYear;
public static int CalenderHijriDate=0;
int currentDate=0;
    boolean isGregorianUpdate = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_converter);
        textSize = (int) (getResources().getDimension(R.dimen._14sdp) / getResources().getDisplayMetrics().density);

        gregorian_day = (WheelView) findViewById(R.id.wheelView_day_g);
        gregorian_month = (WheelView) findViewById(R.id.wheelView_month_g);
        gregorian_year = (WheelView) findViewById(R.id.wheelView_year_g);

        hijri_day = (WheelView) findViewById(R.id.wheelView_day_h);
        hijri_month = (WheelView) findViewById(R.id.wheelView_month_h);
        hijri_year = (WheelView) findViewById(R.id.wheelView_year_h);

        dateConverter = new DateConverter(ConverterDialog.this);
        hijriYear = dateConverter.getHijriYear();
        setGregorianWheelDate();
        setHijriWheelDate();

        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnConvert = (Button) findViewById(R.id.btn_convert);
        groupRadio = (RadioGroup) findViewById(R.id.myRadioGroup);
        linearGeogrian = (LinearLayout) findViewById(R.id.linearGerogerian);
        linearHijri = (LinearLayout) findViewById(R.id.linearHijri);

        btnCancel.setTypeface(((GlobalClass) this.getApplicationContext()).faceRobotoL);
        btnConvert.setTypeface(((GlobalClass) this.getApplicationContext()).faceRobotoL);

        linearGeogrian.setVisibility(View.GONE);
        linearHijri.setVisibility(View.VISIBLE);

        groupRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.hijri) {
                    linearGeogrian.setVisibility(View.GONE);
                    linearHijri.setVisibility(View.VISIBLE);
                    isGregorianUpdate = false;
                } else if (checkedId == R.id.gerogian) {
                    linearGeogrian.setVisibility(View.VISIBLE);
                    linearHijri.setVisibility(View.GONE);
                    isGregorianUpdate = true;
                }

            }

        });

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move calender to selected date which converted
                Log.e("getDate", HijriDay + "==" + HijriMonth + "==" + HijriYear);

                CommunityGlobalClass.getInstance().selectDateOutSide = HijriDay;
                CommunityGlobalClass.getInstance().dialogDate = HijriDay;
                CommunityGlobalClass.mCalenderFragment.outChk = true;
                CommunityGlobalClass.mCalenderFragment.setGridCellAdapterToDate(HijriMonth, HijriYear, "");

                ConverterDialog.this.finish();
            }
        });

    }


    public void setGregorianWheelDate() {
        final Calendar calendar = Calendar.getInstance(Locale.US);

        // month
        int curMonth = calendar.get(Calendar.MONTH);
        String months[] = this.getResources().getStringArray(R.array.month_name);

        gregorian_month.setViewAdapter(new DateArrayAdapter(this, months, curMonth));
        gregorian_month.setCurrentItem(curMonth);

        // year
        int curYear = calendar.get(Calendar.YEAR);

        curGregorianYear = curYear;

        gregorian_year.setViewAdapter(new DateNumericAdapter(this, curYear - NoOfYear,
                curYear + NoOfYear, NoOfYear));
        gregorian_year.setCurrentItem(curYear - (curYear - NoOfYear));

        // day
        updateGregorianDays(gregorian_year, gregorian_month, gregorian_day, false);
        int day = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        gregorian_day.setCurrentItem(day);
        currentDate = day+1;

//changes here
        //HashMap<String, Integer> dateConverted = dateConverter.gregorianToHijri(day + 1, curMonth + 1, curYear, false);
        HashMap<String, Integer> dateConverted = dateConverter.gregorianToHijri(day + 1, curMonth + 1, curYear, true);

        curHijriDay = dateConverted.get("DAY");
        curHijriMonth = dateConverted.get("MONTH");
        curHijriYear = dateConverted.get("YEAR");

        gregorian_day.addScrollingListener(gre);
        gregorian_month.addScrollingListener(gre);
        gregorian_year.addScrollingListener(gre);
    }

    public void setHijriWheelDate() {
        // month
        int curMonth = curHijriMonth;

        String[] months = this.getResources().getStringArray(R.array.islamic_month_name);

        hijri_month.setViewAdapter(new DateArrayAdapter(this, months, curMonth));
        hijri_month.setCurrentItem(curMonth);

        // year
        int curYear = hijriYear;

        hijri_year.setViewAdapter(new DateNumericAdapter(this, curYear - NoOfYear,
                curYear + NoOfYear, NoOfYear));

        hijri_year.setCurrentItem(curHijriYear - (curYear - NoOfYear));

        //Log.e("Hijri Year", String.valueOf(curHijriYear));

        // day
        updateHIjriDays(hijri_year, hijri_month, hijri_day, false);


        hijri_day.setCurrentItem(curHijriDay - 1);
        //  hijri_day.setCurrentItem(curHijriDay );

        hijri_day.addScrollingListener(hijri);
        hijri_month.addScrollingListener(hijri);
        hijri_year.addScrollingListener(hijri);
    }

    public void setGregorianWheelDateOnListener() {
        final Calendar calendar = Calendar.getInstance(Locale.US);

        // month
        int curMonth = curGregorianMonth - 1;
        gregorian_month.setCurrentItem(curMonth);

        // year
        int curYear = calendar.get(Calendar.YEAR);
        int index = curGregorianYear - (curYear - NoOfYear);
        gregorian_year.setCurrentItem(index);

        //Log.i("Gregorian Year", String.valueOf(curGregorianYear));

        // day
        updateGregorianDays(gregorian_year, gregorian_month, gregorian_day, false);
        int day = curGregorianDay - 1;
        gregorian_day.setCurrentItem(day);
    }

    public void setHijriWheelDateOnListener() {
        // month
        int curMonth = curHijriMonth;
        hijri_month.setCurrentItem(curMonth);

        // year
        int curYear = hijriYear;
        hijri_year.setCurrentItem(curHijriYear - (curYear - NoOfYear));

        //Log.e("Hijri Year", String.valueOf(curHijriYear));

        // day
        updateHIjriDays(hijri_year, hijri_month, hijri_day, false);
        hijri_day.setCurrentItem(curHijriDay - 1);
        /// hijri_day.setCurrentItem(curHijriDay );
    }

    OnWheelScrollListener hijri = new OnWheelScrollListener() {

        @Override
        public void onScrollingStarted(WheelView wheel) {
            // TODO Auto-generated method stub
            h_scroll = true;
        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            // TODO Auto-generated method stub

            updateHIjriDays(hijri_year, hijri_month, hijri_day, true);
            h_scroll = false;
        }

    };

    OnWheelScrollListener gre = new OnWheelScrollListener() {

        @Override
        public void onScrollingStarted(WheelView wheel) {
            // TODO Auto-generated method stub
           g_scroll = true;
        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            // TODO Auto-generated method stub

            updateGregorianDays(gregorian_year, gregorian_month, gregorian_day, true);
            g_scroll = false;
          }

    };

    public void updateHIjriDays(WheelView year, WheelView month, WheelView day, boolean updateGregorian) {
        int yearNow, monthNow, dayNow, maxDays;

        if (!g_scroll) {
            yearNow = hijriYear + (year.getCurrentItem() - NoOfYear);
        } else {
            yearNow = curHijriYear;
        }

        monthNow = month.getCurrentItem();

        maxDays = dateConverter.getHijriMonthMaxDay(monthNow, yearNow);

        day.setViewAdapter(new DateNumericAdapter(this, 1, maxDays, 7));

        if (prevMax == 30 && maxDays == 29) {
            dayNow = 1;
        } else if (prevMax == 29 && maxDays == 30) {
            dayNow = Math.min(maxDays, day.getCurrentItem() + 1);
        } else {
            dayNow = Math.min(maxDays, day.getCurrentItem() + 1);

        }

        prevMax = dayNow;

        if (updateGregorian) {
            //Changes
            // HashMap<String, Integer> dateConverted = dateConverter.hijriToGregorian(dayNow , monthNow, yearNow, maxDays, false);
            //My changes
            HashMap<String, Integer> dateConverted = dateConverter.hijriToGregorian(dayNow, monthNow, yearNow, maxDays, false);
            curGregorianDay = dateConverted.get("DAY");
            curGregorianMonth = dateConverted.get("MONTH");
            curGregorianYear = dateConverted.get("YEAR");
            setGregorianWheelDateOnListener();
        }

        // HashMap<String, Integer> dateConverted = dateConverter.gregorianToHijri(dayNow + 1, monthNow + 1, yearNow, false);

        if (!isGregorianUpdate) {

            HijriDay = dayNow;
            HijriMonth = monthNow;
            HijriYear = yearNow;

            if((dayNow==1) && (!h_scroll))
            {
                HijriDay = CalenderHijriDate;
            }
        }
    }

    public void updateGregorianDays(WheelView year, WheelView month, WheelView day, boolean updateHijri) {
        int yearNow, monthNow, dayNow;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month.getCurrentItem());

        if (!h_scroll) {
            yearNow = calendar.get(Calendar.YEAR) + (year.getCurrentItem() - NoOfYear);
        } else {
            yearNow = curGregorianYear;
        }

        calendar.set(Calendar.YEAR, yearNow);

        monthNow = month.getCurrentItem();

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        int i = calendar.get(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(new DateNumericAdapter(this, 1, maxDays, i - 1));

        int j = day.getCurrentItem();

        dayNow = Math.min(maxDays, j + 1);

     /*   if (maxDays == 28 && newMax != maxDays) {
            dayNow = 3;
        } else if (maxDays == 29 && newMax != maxDays) {
            dayNow = 2;
        } else if (newMax == 31 && maxDays == 30) {
            dayNow = 1;
        } else if (newMax == 30 && maxDays == 31) {
            dayNow = Math.min(maxDays, day.getCurrentItem() + 1);
        } else {*/
            dayNow = Math.min(maxDays, day.getCurrentItem() + 1);

      //  }

        newMax = maxDays;

        if (updateHijri) {

            //Changes
            //  HashMap<String, Integer> dateConverted = dateConverter.gregorianToHijri(dayNow, monthNow + 1, yearNow, false);

            //My changes
            HashMap<String, Integer> dateConverted = dateConverter.gregorianToHijri(dayNow + 1, monthNow + 1, yearNow, false);
            curHijriDay = dateConverted.get("DAY");
            curHijriMonth = dateConverted.get("MONTH");
            curHijriYear = dateConverted.get("YEAR");



            setHijriWheelDateOnListener();

        }

        //work when gregorian radio true
        if (isGregorianUpdate) {
            /*HashMap<String, Integer> dateConverted = dateConverter.gregorianToHijri(dayNow +1, monthNow + 1, yearNow, false);
            HijriDay = dateConverted.get("DAY");
            HijriMonth = dateConverted.get("MONTH");
            HijriYear = dateConverted.get("YEAR");*/
            int date[] = dateConverter.getHijrDateUmmalQura(yearNow, monthNow, dayNow);
            HijriDay = date[0];
            HijriMonth = date[1];
            HijriYear = date[2];

            if((dayNow==1) && (!g_scroll))
            {
                HijriDay = CalenderHijriDate;
            }
        }
    }

    private class DateNumericAdapter extends NumericWheelAdapter {
        int currentItem;
        int currentValue;

        public DateNumericAdapter(Context context, int minValue, int maxValue,
                                  int current) {
            super(context, minValue, maxValue);
            this.currentValue = current;
            setTextSize(textSize);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(Color.BLACK);
            }

        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;

            return super.getItem(index, cachedView, parent);
        }
    }

    private class DateArrayAdapter extends ArrayWheelAdapter<String> {
        int currentItem;
        int currentValue;

        public DateArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
            setTextSize(textSize);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {

                view.setTextColor(Color.BLACK);
            }

        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;

            return super.getItem(index, cachedView, parent);
        }
    }

}
