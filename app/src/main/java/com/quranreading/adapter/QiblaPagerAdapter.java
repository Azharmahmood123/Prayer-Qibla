package com.quranreading.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.quranreading.fragments.CompassFragmentIndex;
import com.quranreading.fragments.MoreAppsGridFragment;
import com.quranreading.fragments.TimingsFragment;
import com.quranreading.qibladirection.R;

import quran.fragments.SurahListFragment;

public class QiblaPagerAdapter extends FragmentPagerAdapter {
	protected static String[] CONTENT;
	private int mCount;

	public QiblaPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		init(context);
	}

	void init(Context context) {
		String qiblaDirection = context.getResources().getString(R.string.qibla_direction);
		String salatTimings = context.getResources().getString(R.string.salat_timings);
		String quran = context.getResources().getString(R.string.quran);
		String more = context.getResources().getString(R.string.more);

		CONTENT = new String[] { qiblaDirection, salatTimings, quran, more };

		mCount = CONTENT.length;
	}

	@Override
	public Fragment getItem(int position) {
		if(position == 0)
		{
			return new CompassFragmentIndex();
		}
		else if(position == 1)
		{
			return new TimingsFragment();
		}
		else if(position == 2)
		{
			return new SurahListFragment();
		}
		else
		{
			return new MoreAppsGridFragment();
		}
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return CONTENT[position % CONTENT.length].toUpperCase();
	}
}
