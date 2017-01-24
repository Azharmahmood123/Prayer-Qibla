package com.quranreading.sharedPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DialPref 
{
	Editor editor;
	Context _context;
	int PRIVATE_MODE = 0;
	SharedPreferences pref;
	private static final String PREF_NAME = "DialPref";
	public static final String IS_VALUE_SET = "IsValueSet";
	public static final String DIAL_VALUE = "DialValue";
	public static final String CURRENCY_VALUE = "CurrencyValue";
	
	public DialPref(Context context)
	{
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	public void setDialValue(int value)
	{
		editor.putInt(DIAL_VALUE, value);
		editor.putBoolean(IS_VALUE_SET, true);
		editor.commit();
	}
	
	public boolean chkValueSet()
	{
		return pref.getBoolean(IS_VALUE_SET, false);
	}
	
	public int getDialValue()
	{
		return pref.getInt(DIAL_VALUE, 1);
	}

	public void clearStoredData()
	{
		editor.clear();
		editor.commit();
	}
}