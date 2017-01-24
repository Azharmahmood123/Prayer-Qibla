package noman.hijri.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import noman.hijri.acitivity.ConverterDialog;
import noman.hijri.adapter.CalendarAdapter;
import noman.CommunityGlobalClass;
import noman.hijri.acitivity.CalenderActivity;
import noman.hijri.acitivity.EventActivity;
import noman.hijri.helper.DateConverter;
import noman.hijri.model.DateAccessModel;

@SuppressLint("SimpleDateFormat")
public class CalenderFragment extends Fragment {
    DateConverter dateConverter;

    TextView tvMonthHeader, tvGeorgianDate, tvHijriDDate;

    GridView gridview;
    CalendarAdapter adapter;

    int month, year, currentPosition;

    ImageButton btnPrevMonth, btnNextMonth;
    LinearLayout onToday, onEvents;

    public boolean outChk = false;
    String currentDate;
    SimpleDateFormat sdf = new SimpleDateFormat("M");

    private List<DateAccessModel> list;
    private Calendar _calendar;
    private Calendar _calendarLocal;
    private static final String dateTemplate = "MMMM, yyyy";

    public int currentMonth, currentYear, daysInMonth, currentDayOfMonth, currentWeekDay;

    private static final int DAY_OFFSET = 1;

    private String[] weekdays;

    private String[] months, gergorianMonth;
    int[] daysOfMonth = {30, 29, 30, 30, 30, 29, 29, 30, 29, 29, 30, 29};
    //private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public boolean isOutSideGridCall = false;
    CalenderActivity mActivity;

    public static CalenderFragment newInstance(CalenderActivity mActivity) {
        CalenderFragment myFragment = new CalenderFragment();
        myFragment.mActivity = mActivity;
        return myFragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        CommunityGlobalClass.mCalenderFragment = this;

        // months = getResources().getStringArray(R.array.month_name);
        months = getResources().getStringArray(R.array.islamic_month_name);//For testing
        gergorianMonth = getResources().getStringArray(R.array.month_name);
        weekdays = getResources().getStringArray(R.array.days_name);

        View rootView = inflater.inflate(R.layout.layout_calender, container, false);

        dateConverter = new DateConverter(getActivity());

        list = new ArrayList<DateAccessModel>();

        _calendarLocal = Calendar.getInstance(Locale.getDefault());
        month = _calendarLocal.get(Calendar.MONTH);
        year = _calendarLocal.get(Calendar.YEAR);
        //_calendarLocal.set(Calendar.DAY_OF_WEEK_IN_MONTH,(_calendarLocal.get(Calendar.DAY_OF_WEEK_IN_MONTH)+ 1));
        _calendarLocal.set(Calendar.DAY_OF_MONTH, (_calendarLocal.get(Calendar.DAY_OF_MONTH) - 1)); //For date issue

        //New changes here
        GregorianCalendar gCal = new GregorianCalendar(year, month, _calendarLocal.get(Calendar.DAY_OF_MONTH));

        Calendar uCal = new UmmalquraCalendar();
        uCal.setTime(gCal.getTime());
        _calendar = uCal;
        year = _calendar.get(Calendar.YEAR);
        month = _calendar.get(Calendar.MONTH);

        //********************************

        //**** Save Hijri current date here with following
        SimpleDateFormat dateFormat = new SimpleDateFormat("", Locale.ENGLISH);
        dateFormat.setCalendar(_calendar);
        dateFormat.applyPattern("d");
        int cDate = Integer.parseInt(dateFormat.format(_calendar.getTime()));
        dateFormat.applyPattern("y");
        int cYear = Integer.parseInt(dateFormat.format(_calendar.getTime()));
        ConverterDialog.CalenderHijriDate=cDate;
        currentDate = cDate + " " + months[month] + ", " + cYear;
        Log.e("curent date", currentDate);
        //**********************************


        gridview = (GridView) rootView.findViewById(R.id.gridview);
        tvMonthHeader = (TextView) rootView.findViewById(R.id.header_title);
        tvGeorgianDate = (TextView) rootView.findViewById(R.id.tv_georgian_date);
        tvHijriDDate = (TextView) rootView.findViewById(R.id.tv_hijri_date);
        btnPrevMonth = (ImageButton) rootView.findViewById(R.id.btn_prev);
        btnNextMonth = (ImageButton) rootView.findViewById(R.id.btn_next);
        onToday = (LinearLayout) rootView.findViewById(R.id.layoutToday);
        onEvents = (LinearLayout) rootView.findViewById(R.id.layoutEvents);

        tvMonthHeader.setTypeface(((GlobalClass) getActivity().getApplicationContext()).faceRobotoL);
        tvGeorgianDate.setTypeface(((GlobalClass) getActivity().getApplicationContext()).faceRobotoL);
        tvHijriDDate.setTypeface(((GlobalClass) getActivity().getApplicationContext()).faceRobotoL);


        currentMonth = month;
        currentYear = year;


        printMonth(month, year);

        CommunityGlobalClass.getInstance().selected = currentPosition;

        adapter = new CalendarAdapter(getActivity(), list, this);//sending fragment of this class
        gridview.setAdapter(adapter);

        tvMonthHeader.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
        tvGeorgianDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mActivity.showDatePicker();
            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.get(position).eventIndex != -1) {
                    outChk = true;
                    CommunityGlobalClass.getInstance().selected = position;
                    gregorianToHijri(Integer.parseInt(list.get(position).date), list.get(position).monthNo, Integer.parseInt(list.get(position).year));
                    CommunityGlobalClass.getInstance().selectedEvent = list.get(position).eventIndex;
                    int date[] = dateConverter.ummalQuraCalendar(Integer.parseInt(list.get(position).date), list.get(position).monthNo, Integer.parseInt(list.get(position).year));
                    CommunityGlobalClass.getInstance().yearEvent = Integer.parseInt(list.get(position).year);
                    startActivity(new Intent(getActivity(), EventActivity.class));
                    CommunityGlobalClass.mCalenderFragment.isOutSideGridCall = false;

                  /*  gregorianToHijri(Integer.parseInt(list.get(position).date), list.get(position).monthNo, Integer.parseInt(list.get(position).year));
                    CommunityGlobalClass.getInstance().selectedEvent = list.get(position).eventIndex;
                    CommunityGlobalClass.getInstance().yearEvent = dateConverter.getHijriYearFromDate(Integer.parseInt(list.get(position).date), list.get(position).monthNo, Integer.parseInt(list.get(position).year));
                    startActivity(new Intent(getActivity(), EventActivity.class));*/

                } else if (!list.get(position).status.equals("notWithin")) {
                    CommunityGlobalClass.getInstance().selected = position;
                    gregorianToHijri(Integer.parseInt(list.get(position).date),
                            list.get(position).monthNo, Integer.parseInt(list.get(position).year));
                    CommunityGlobalClass.mCalenderFragment.isOutSideGridCall = false;
                } else {

                    CommunityGlobalClass.getInstance().dialogDate = Integer.parseInt(list.get(position).date);
                    CommunityGlobalClass.mCalenderFragment.outChk = true;
                    int _month = list.get(position).monthNo;
                    int _year = Integer.parseInt(list.get(position).year);
                    CommunityGlobalClass.mCalenderFragment.setGridCellAdapterToDate(_month, _year, "");


                }
            }
        });

        btnPrevMonth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setPreviousMonth();
            }
        });

        btnNextMonth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setNextMonth();
            }
        });

        onToday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setCurrentMonth();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

        //CommunityGlobalClass.getInstance().selected = -1;

        if (!outChk) {
            gregorianToHijri(Integer.parseInt(list.get(CommunityGlobalClass.getInstance().selected).date),
                    list.get(CommunityGlobalClass.getInstance().selected).monthNo, Integer.parseInt(list.get(CommunityGlobalClass.getInstance().selected).year));
        }

        outChk = false;
    }

    public void gregorianToHijri(int day, int month, int year) {
        tvGeorgianDate.setText(dateConverter.getGerorgerianDate(day, month, year));
        tvHijriDDate.setText(day + " " + months[month] + ", " + year);
        adapter.notifyDataSetChanged();
    }

    public void setCurrentMonth() {
        month = currentMonth;
        year = currentYear;
        setGridCellAdapterToDate(month, year, "current");
    }

    public void setPreviousMonth() {
        CommunityGlobalClass.getInstance().selected = -1;

        if (month <= 0) {
            month = 11;
            year--;
        } else {
            month--;
        }

        setGridCellAdapterToDate(month, year, "");
        CommunityGlobalClass.mCalenderFragment.isOutSideGridCall = false;
    }

    public void setNextMonth() {
        CommunityGlobalClass.getInstance().selected = -1;

        if (month > 10) {
            month = 0;
            year++;
        } else {
            month++;
        }

        setGridCellAdapterToDate(month, year, "");
        CommunityGlobalClass.mCalenderFragment.isOutSideGridCall = false;
    }

    public void setGridCellAdapterToDate(int month, int year, String from) {
        list.clear();
// it only handle in just outside the fragment calling function
        if (from.isEmpty()) {
            CommunityGlobalClass.mCalenderFragment.isOutSideGridCall  = true;
        }

        printMonth(month, year);

        int position = 0;

        if (from.equals("current")) {
            position = currentPosition;
        } else if (month == currentMonth && year == currentYear) {
            position = currentPosition;
        } else {
            for (int pos = 0; pos < list.size(); pos++) {
                if (CommunityGlobalClass.getInstance().selectDateOutSide > -1) {
                    if (Integer.parseInt(list.get(pos).date) == CommunityGlobalClass.getInstance().selectDateOutSide) {
                        position = pos;
                        CommunityGlobalClass.getInstance().selectDateOutSide = -1;
                        break;
                    }
                } else {
                    if (Integer.parseInt(list.get(pos).date) == 1) {
                        position = pos;
                        break;
                    }
                }
            }
        }

        CommunityGlobalClass.getInstance().selected = position;
        gregorianToHijri(Integer.parseInt(list.get(position).date), list.get(position).monthNo, Integer.parseInt(list.get(position).year));
    }

    //////////////////////////// Get Month ////////////////////////////


    private void printMonth(int mm, int yy) {
        setCurrentDayOfMonth(_calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(_calendar.get(Calendar.DAY_OF_WEEK));
        _calendar.set(Calendar.YEAR, yy);
        _calendar.set(Calendar.MONTH, mm);
        _calendar.set(Calendar.DAY_OF_MONTH, 1);


        //Trailing in Monthss

        int trailingSpaces = 0;
        int prevMonth = 0;
        int prevYear = 0;
        int nextMonth = 0;
        int nextYear = 0;
        int daysInPrevMonth = 0;
        int currentMonth = mm;

        if (currentMonth == 11) {
            prevMonth = 0;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 1;
            prevYear = yy;
            nextYear = yy + 1;

        } else if (currentMonth == 0) {
            prevMonth = 11;
            prevYear = yy - 1;
            nextYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 1;
        } else {
            prevMonth = currentMonth - 1;
            nextMonth = currentMonth + 1;
            nextYear = yy;
            prevYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);

        }

        daysInMonth = daysOfMonth[currentMonth];

        int currentWeekDay = _calendar.get(Calendar.DAY_OF_WEEK); // if we change here all the calender is going to distrub in createing
        trailingSpaces = currentWeekDay;
         /*  if (dateConverter.civilLeapYear(_calendar.get(Calendar.YEAR))) {
               if (mm == 2)
                   ++daysInMonth;
               else if (mm == 3)
                   ++daysInPrevMonth;
           }
        */
        // Trailing Month days
        if (trailingSpaces < 7) {
            for (int i = 0; i < trailingSpaces; i++) {

                DateAccessModel model = new DateAccessModel(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
                        + i), prevMonth, getMonthAsString(prevMonth), String.valueOf(prevYear), "notWithin", -1);//within//notWithin

                list.add(model);
            }
        }
        // Current Month Days
        int totalDays = daysInMonth;
        String month = "";//currentMonth; //fro array position
        for (int i = 1; i <= totalDays; i++) {
            int eventIndex = 0;
            month = getMonthAsString(currentMonth);
            //if (month.equals(getCurrentDayOfMonth())) {
            String date = String.valueOf(i) + " " + month + ", " + yy;

            String status = "";
            if (currentDate.equals(date)) {
                status = "current";
                int size = list.size();
                currentPosition = size;
                eventIndex = chkEvent(i, currentMonth);
                DateAccessModel model = new DateAccessModel(String.valueOf(i), currentMonth, month, String.valueOf(yy), status, eventIndex);
                list.add(model);
            } else {
                status = "within";
                eventIndex = chkEvent(i, currentMonth);
                DateAccessModel model = new DateAccessModel(String.valueOf(i), currentMonth, month, String.valueOf(yy), status, eventIndex);
                list.add(model);
            }

            /*    eventIndex = chkEvent(i, currentMonth);
                DateAccessModel model = new DateAccessModel(String.valueOf(i), currentMonth, month, String.valueOf(yy), status, eventIndex);
                list.add(model);
            } else {
                eventIndex = chkEvent(i, currentMonth);
                DateAccessModel model = new DateAccessModel(String.valueOf(i), currentMonth, getMonthAsString(currentMonth), String.valueOf(yy), "within", eventIndex);
                list.add(model);
            }*/
        }


        //For adding white space after the date end of the month
        for (int i = 0; i < list.size() % 7; i++) {
          /*  DateAccessModel model = new DateAccessModel(String.valueOf(i + 1), nextMonth + 1,
                    getMonthAsString(nextMonth), String.valueOf(nextYear), "notWithin", -1);
*/

            DateAccessModel model = new DateAccessModel(String.valueOf(i + 1), nextMonth,
                    getMonthAsString(nextMonth), String.valueOf(nextYear), "notWithin", -1);

            list.add(model);
        }

    }

    public int chkEvent(int day, int month) {
        int eventIndex = -1;

        String[] dateArray = {"1-0", "10-0", "12-2", "1-8", "27-8", "1-9", "10-11", "11-11"};

        //  HashMap<String, Integer> dateConverted = dateConverter.gregorianToHijri(day,month, year, true);

        // int curDay = dateConverted.get("DAY");
        //  int curMonth = dateConverted.get("MONTH");

        int curMonth = month;
        int curDay = day;
        if (curMonth == 0 || curMonth == 2 || curMonth == 8 || curMonth == 9 || curMonth == 11) {
            String date = String.valueOf(curDay) + "-" + String.valueOf(curMonth);

            for (int pos = 0; pos < dateArray.length; pos++) {
                if (date.equals(dateArray[pos])) {
                    eventIndex = pos;

                    break;
                }
            }
        }


        return eventIndex;
    }

    public int getCurrentDayOfMonth() {
        return currentDayOfMonth;
    }
    public int getCurrentMonth() {
        return currentMonth;
    }

    private void setCurrentDayOfMonth(int currentDayOfMonth) {
        this.currentDayOfMonth = currentDayOfMonth;
    }

    public void setCurrentWeekDay(int currentWeekDay) {
        this.currentWeekDay = currentWeekDay;
    }

    public int getCurrentWeekDay() {
        return currentWeekDay;
    }

    private String getMonthAsString(int i) {
        return months[i];
        // return gergorianMonth[i];
    }

    @SuppressWarnings("unused")
    private String getWeekDayAsString(int i) {
        return weekdays[i];
    }

    private int getNumberOfDaysOfMonth(int i) {
        return daysOfMonth[i];
    }


    public void updateDateFromPicker(int month, int year) {
        setGridCellAdapterToDate(month, year, "");
    }


}
