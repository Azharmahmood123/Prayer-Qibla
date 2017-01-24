package com.quranreading.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeFormateConverter {

	public static String convertTime12To24(String _12Time) {
		SimpleDateFormat displayFormat, parseFormat;
		Date date = null;
		displayFormat = new SimpleDateFormat("HH:mm", Locale.US);
		parseFormat = new SimpleDateFormat("hh:mm a", Locale.US);
		try
		{
			date = parseFormat.parse(_12Time);
			return displayFormat.format(date);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String convertTime24To12(String _24Time) {
		try
		{
			SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm", Locale.US);
			SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a", Locale.US);
			Date _24HourDt = _24HourSDF.parse(_24Time);

			return _12HourSDF.format(_24HourDt).toLowerCase();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
