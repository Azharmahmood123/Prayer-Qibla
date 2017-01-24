package com.quranreading.qibladirection;

import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.qibladirection.R;

import android.app.Activity;
import android.os.Bundle;

public class AdvanceInstrctAcivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advance_instrct);

		sendAnalyticsData();

	}

	private void sendAnalyticsData() {

		AnalyticSingaltonClass.getInstance(this).sendScreenAnalytics("Prayer Advance Help Screen");
	}
}