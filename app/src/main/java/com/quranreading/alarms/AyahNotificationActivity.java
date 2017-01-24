package com.quranreading.alarms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.qibladirection.MainActivityNew;

import quran.activities.SurahActivity;

public class AyahNotificationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		AnalyticSingaltonClass.getInstance(this).sendEventAnalytics("Notification", "AyahOfTheDayTapped");
		Intent end_actvty = new Intent(this, MainActivityNew.class);

		end_actvty.putExtra(SurahActivity.KEY_EXTRA_SURAH_NO, getIntent().getIntExtra(SurahActivity.KEY_EXTRA_SURAH_NO, -1));
		end_actvty.putExtra(SurahActivity.KEY_EXTRA_AYAH_NO, getIntent().getIntExtra(SurahActivity.KEY_EXTRA_AYAH_NO, -1));
		startActivity(end_actvty);

		finish();
	}
}