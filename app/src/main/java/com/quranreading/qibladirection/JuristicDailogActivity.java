package com.quranreading.qibladirection;

import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.helper.DailogsClass;
import com.quranreading.listeners.OnDailogButtonSelectionListner;
import com.quranreading.sharedPreference.LocationPref;
import com.quranreading.sharedPreference.PrayerTimeSettingsPref;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class JuristicDailogActivity extends Activity implements OnDailogButtonSelectionListner {

	// 1 is default value for option Shafi/malki/hambli
	int juristic = 2;
	String[] dataJuristic;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.transperant_layout);
		dataJuristic = getResources().getStringArray(R.array.arr_juristic);

		DailogsClass dailogOptions = new DailogsClass(this, getResources().getString(R.string.juristic), "", dataJuristic, juristic, this, getResources().getString(R.string.okay), getResources().getString(R.string.cancel));
		dailogOptions.showOptionsDialogOneButton();
	}

	@Override
	public void onDailogButtonSelectionListner(String title, int savedIndex, boolean selection) {

		LocationPref locationPref = new LocationPref(this);
		if(title.equals(getResources().getString(R.string.juristic)) && selection)
		{
			sendAnalyticEvent("Salah Juristic- " + dataJuristic[savedIndex]);
			PrayerTimeSettingsPref namazPrefs = new PrayerTimeSettingsPref(this);
			juristic = savedIndex + 1;
			namazPrefs.setJuristic(juristic);
			namazPrefs.setJuristicDefault(juristic);
		}

		locationPref.setFirstSalatLauch();
		finish();
	}

	private void sendAnalyticEvent(String eventAction) {
		AnalyticSingaltonClass.getInstance(this).sendEventAnalytics("Settings-Qibla", eventAction);
	}
}
