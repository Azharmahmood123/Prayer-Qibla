package com.quranreading.ads;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.content.Context;

public class AnalyticSingaltonClass {

	private static final String PROPERTY_ID = "UA-7859555-54"; // Property Id.
	private static AnalyticSingaltonClass mAnalyticsSingaltonClass;
	private Context mContext;

	public static AnalyticSingaltonClass getInstance(Context context) {
		if(mAnalyticsSingaltonClass == null)
		{
			mAnalyticsSingaltonClass = new AnalyticSingaltonClass(context);
		}
		return mAnalyticsSingaltonClass;
	}

	private AnalyticSingaltonClass(Context context) {
		mContext = context.getApplicationContext();
	}

	private synchronized Tracker getTracker() {
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(mContext);
		return analytics.newTracker(PROPERTY_ID);
	}

	public void sendScreenAnalytics(String screenName) {
		Tracker screenTracker = getTracker();
		screenTracker.setScreenName(screenName);
		screenTracker.send(new HitBuilders.AppViewBuilder().build());
	}

	public void sendEventAnalytics(String eventCategory, String eventAction) {
		Tracker eventTracker = getTracker();
		eventTracker.send(new HitBuilders.EventBuilder().setCategory(eventCategory).setAction(eventAction).build());
	}
}