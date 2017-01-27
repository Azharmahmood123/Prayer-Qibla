package noman.hijri.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import org.joda.time.Chronology;
import org.joda.time.LocalDate;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.IslamicChronology;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import noman.CommunityGlobalClass;

@SuppressLint("SimpleDateFormat")
public class DateConverter {
    Context _context;


    String[] iMonthNames = {"Muharram", "Safar", "Rabi-ul-Awwal",
            "Rabi-ul-Thani", "Jumada-ul-Awwal", "Jumada-ul-Thani", "Rajab",
            "Sha'ban", "Ramadan", "Shawwal", "Dhul Qa'ada", "Dhul Hijja"};

    public DateConverter(Context c) {
        _context = c;
    }

    public HashMap<String, Integer> hijriToGregorian(int day, int month, int year, int maxDays,
                                                     boolean applyAdjustment) {
        int adjust = 1;

        if (applyAdjustment) {
            adjust = ((CommunityGlobalClass) _context.getApplicationContext()).dateAdjustment;
        }

        int newDay = day - adjust;
        int newMonth = month + 1;
        int newYear = year;

        newDay = newDay + 2;

        if (newDay <= 0) {
            //newDay = newDay + 2;

            newMonth = newMonth - 1;

            if (newMonth <= 0) {
                newMonth = 12;
                newYear = newYear - 1;
            }

            int newMaxDays = getHijriMonthMaxDay(newMonth - 1, newYear);

            if (adjust < 0)
                newDay = maxDays - (-(adjust));
            else
                newDay = (newMaxDays - ((adjust - day) - 2));
        } else if (newDay > maxDays) {
            if (adjust < 0) {
                newDay = newDay - maxDays;
            } else {
                newDay = adjust;
            }

            newMonth = newMonth + 1;

            if (newMonth > 12) {
                newMonth = 1;
                newYear = newYear + 1;
            }

        }

        HashMap<String, Integer> gregorianDate = new HashMap<String, Integer>();

        Chronology iso = ISOChronology.getInstanceUTC();
        Chronology hijri = IslamicChronology.getInstanceUTC();

        LocalDate todayHijri = new LocalDate(newYear, newMonth, newDay, hijri);
        LocalDate todayIso = new LocalDate(todayHijri.toDateTimeAtStartOfDay(), iso);


        String date[] = todayIso.toString().split("-");

        int dayNo = Integer.parseInt(date[2]);
        int monthNo = Integer.parseInt(date[1]);
        int greYear = Integer.parseInt(date[0]);

        gregorianDate.put("DAY", dayNo);
        gregorianDate.put("MONTH", monthNo);
        gregorianDate.put("YEAR", greYear);

        return gregorianDate;
    }

    public HashMap<String, Integer> gregorianToHijri(int day, int month, int year,
                                                     boolean applyAdjustment) {
        HashMap<String, Integer> hijriDate = new HashMap<String, Integer>();
        int adjust = 1;

        if (applyAdjustment) {
            adjust = ((CommunityGlobalClass) _context.getApplicationContext()).dateAdjustment;
        }

        String dtStart = String.valueOf(day + adjust) + "-"
                + String.valueOf(month) + "-" + String.valueOf(year);

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date d = null;

        try {
            d = format.parse(dtStart);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Calendar calenderNow = new GregorianCalendar();
        calenderNow.setTime(d);

        int newDay = calenderNow.get(Calendar.DAY_OF_MONTH);
        int newMonth = calenderNow.get(Calendar.MONTH) + 1;
        int newYear = calenderNow.get(Calendar.YEAR);

        Chronology iso = ISOChronology.getInstanceUTC();
        Chronology hijri = IslamicChronology.getInstanceUTC();
        LocalDate todayIso = new LocalDate(newYear, newMonth, newDay, iso);
        LocalDate todayHijri = new LocalDate(todayIso.toDateTimeAtStartOfDay(), hijri);

        String date[] = todayHijri.toString().split("-");

        int dayNo = Integer.parseInt(date[2]);
        int monthNo = Integer.parseInt(date[1]);
        int hijriYear = Integer.parseInt(date[0]);

        hijriDate.put("DAY", dayNo);
        hijriDate.put("MONTH", monthNo - 1);
        hijriDate.put("YEAR", hijriYear);
        return hijriDate;
    }


    public String getCompleteHijriDate(int day, int month, int year) {
        int adjust = ((CommunityGlobalClass) _context.getApplicationContext()).dateAdjustment;

        String dtStart = String.valueOf(day + adjust) + "-"
                + String.valueOf(month) + "-" + String.valueOf(year);

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date d = null;

        try {
            d = format.parse(dtStart);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Calendar calenderNow = new GregorianCalendar();
        calenderNow.setTime(d);

        int newDay = calenderNow.get(Calendar.DAY_OF_MONTH);
        int newMonth = calenderNow.get(Calendar.MONTH) + 1;
        int newYear = calenderNow.get(Calendar.YEAR);

        Chronology iso = ISOChronology.getInstanceUTC();
        Chronology hijri = IslamicChronology.getInstanceUTC();

        LocalDate todayIso = new LocalDate(newYear, newMonth, newDay, iso);
        LocalDate todayHijri = new LocalDate(todayIso.toDateTimeAtStartOfDay(), hijri);

        String date[] = todayHijri.toString().split("-");

        int dayNo = Integer.parseInt(date[2]);
        int monthNo = Integer.parseInt(date[1]);
        int hijriYear = Integer.parseInt(date[0]);

        String outputIslamicDate = String.valueOf(dayNo) + " " + iMonthNames[monthNo - 1]
                + ", " + String.valueOf(hijriYear);
        return outputIslamicDate;
    }

    public String getHijriDate(int day, int month, int year) {
        int adjust = ((CommunityGlobalClass) _context.getApplicationContext()).dateAdjustment;

        String dtStart = String.valueOf(day + adjust) + "-"
                + String.valueOf(month) + "-" + String.valueOf(year);

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date d = null;

        try {
            d = format.parse(dtStart);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Calendar calenderNow = new GregorianCalendar();
        calenderNow.setTime(d);

        int newDay = calenderNow.get(Calendar.DAY_OF_MONTH);
        int newMonth = calenderNow.get(Calendar.MONTH) + 1;
        int newYear = calenderNow.get(Calendar.YEAR);

        Chronology iso = ISOChronology.getInstanceUTC();
        Chronology hijri = IslamicChronology.getInstanceUTC();

        LocalDate todayIso = new LocalDate(newYear, newMonth, newDay, iso);
        LocalDate todayHijri = new LocalDate(todayIso.toDateTimeAtStartOfDay(), hijri);

        String date[] = todayHijri.toString().split("-");

        int dayNo = Integer.parseInt(date[2]);

        return String.valueOf(dayNo);
    }

    public int getHijriMonthMaxDay(int month, int year) {
        Chronology hijri = IslamicChronology.getInstanceUTC();

        int dayNo = 0;

        try {
            @SuppressWarnings("unused")
            LocalDate todayIso = new LocalDate(year, month + 1, 30, hijri);
            dayNo = 30;
        } catch (org.joda.time.IllegalFieldValueException e) {
            dayNo = 29;
        }

        return dayNo;
    }


    public int getHijriYear() {
        Calendar calendar = Calendar.getInstance();

        int newDay = calendar.get(Calendar.DAY_OF_MONTH);
        int newMonth = calendar.get(Calendar.MONTH) + 1;
        int newYear = calendar.get(Calendar.YEAR);

        Chronology iso = ISOChronology.getInstanceUTC();
        Chronology hijri = IslamicChronology.getInstanceUTC();

        LocalDate todayIso = new LocalDate(newYear, newMonth, newDay, iso);
        LocalDate todayHijri = new LocalDate(todayIso.toDateTimeAtStartOfDay(), hijri);

        String date[] = todayHijri.toString().split("-");

        int year = Integer.parseInt(date[0]);

        return year;
    }

    public int getHijriYearFromDate(int day, int month, int year) {
        int adjust = ((CommunityGlobalClass) _context.getApplicationContext()).dateAdjustment;

        String dtStart = String.valueOf(day + adjust) + "-"
                + String.valueOf(month) + "-" + String.valueOf(year);

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date d = null;

        try {
            d = format.parse(dtStart);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Calendar calenderNow = new GregorianCalendar();
        calenderNow.setTime(d);

        int newDay = calenderNow.get(Calendar.DAY_OF_MONTH);
        int newMonth = calenderNow.get(Calendar.MONTH) + 1;
        int newYear = calenderNow.get(Calendar.YEAR);

        Chronology iso = ISOChronology.getInstanceUTC();
        Chronology hijri = IslamicChronology.getInstanceUTC();

        LocalDate todayIso = new LocalDate(newYear, newMonth, newDay, iso);
        LocalDate todayHijri = new LocalDate(todayIso.toDateTimeAtStartOfDay(), hijri);

        String date[] = todayHijri.toString().split("-");

        int dayNo = Integer.parseInt(date[2]);
        int monthNo = Integer.parseInt(date[1]);
        int hijriYear = Integer.parseInt(date[0]);

        String outputIslamicDate = String.valueOf(dayNo) + " " + iMonthNames[monthNo - 1]
                + ", " + String.valueOf(hijriYear);

        return hijriYear;
    }


    //New function with ummual qurra calender
    public int[] ummalQuraCalendar(int d, int m, int y) {

        Calendar uCal = new UmmalquraCalendar(y, m, (d + 1));
        GregorianCalendar gCal = new GregorianCalendar();
        gCal.setTime(uCal.getTime());
        int year = gCal.get(Calendar.YEAR);
        int month = gCal.get(Calendar.MONTH);
        int day = gCal.get(Calendar.DAY_OF_MONTH);
        // double weekday = cal.get(Calendar.DAY_OF_WEEK);
        return new int[]{day, month, year};

    }

   // islamic leap year
    public final static boolean civilLeapYear(int year)
    {
        return (14 + 11 * year) % 30 < 11;
    }

    //New function with ummual qurra calender
    public String getGerorgerianDate(int d, int m, int y) {
        Calendar uCal = new UmmalquraCalendar(y, m, (d + 1));
        GregorianCalendar gCal = new GregorianCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("", Locale.ENGLISH);
        dateFormat.setCalendar(gCal);
        gCal.setTime(uCal.getTime());
        dateFormat.applyPattern("d MMMM, y");
        dateFormat.format(gCal.getTime());

        return dateFormat.format(uCal.getTime());

    }

    public int[] getHijrDateUmmalQura(int year, int month, int day) {

        GregorianCalendar gCal = new GregorianCalendar(year, month, (day - 1));
        Calendar uCal = new UmmalquraCalendar();


        uCal.setTime(gCal.getTime());

        int d = uCal.get(Calendar.DAY_OF_MONTH);
        int y = uCal.get(Calendar.YEAR);
        int m = uCal.get(Calendar.MONTH);
        return new int[]{d, m, y};
    }
}
