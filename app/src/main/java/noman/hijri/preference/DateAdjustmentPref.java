package noman.hijri.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DateAdjustmentPref 
{
	Editor editor;
	Context _context;
	int PRIVATE_MODE = 0;
	SharedPreferences pref;
	private static final String PREF_NAME = "DateAdjustPref";
	public static final String ADJUST_VALUE = "AdjustValue";
	
	public DateAdjustmentPref(Context context)
	{
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	public void setAdjustmentValue(int value)
	{
		editor.putInt(ADJUST_VALUE, value);
		editor.commit();
	}
	
	public int getAdjustmentValue()
	{
		return pref.getInt(ADJUST_VALUE, 1);
	}

	public void clearStoredData()
	{
		editor.clear();
		editor.commit();
	}
}