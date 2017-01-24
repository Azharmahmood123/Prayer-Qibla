package com.quranreading.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.R;
import com.quranreading.sharedPreference.LanguagePref;

public class SlidingTabLayout extends HorizontalScrollView {

	public interface TabColorizer {

		int getIndicatorColor(int position);

		int getDividerColor(int position);
	}

	private static int TITLE_OFFSET_DIPS = 24;
	private static int TAB_VIEW_PADDING_DIPS = 10;
	private static int TAB_VIEW_TEXT_SIZE_SP = 18;

	private String SMALL = "small";
	private String MEDIUM = "medium";
	private String LARGE = "large";

	private static final String TAB_VIEW_TEXT_SELECTED_COLOR = "#88FFFFFF";

	private int mTitleOffset;
	private int mTabViewLayoutId;
	private int mTabViewTextViewId;

	private ViewPager mViewPager;
	private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

	private final SlidingTabStrip mTabStrip;

	String device;

	public SlidingTabLayout(Context context) {
		this(context, null);
	}

	public SlidingTabLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		device = getResources().getString(R.string.device);

		// Disable the Scroll Bar
		setHorizontalScrollBarEnabled(false);
		// Make sure that the Tab Strips fills this View
		setFillViewport(true);

		if(device.equals(SMALL))
		{
			TITLE_OFFSET_DIPS = 24;
		}
		else if(device.equals(MEDIUM))
		{
			TITLE_OFFSET_DIPS = 36;
		}
		else if(device.equals(LARGE))
		{
			TITLE_OFFSET_DIPS = 48;
		}

		mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);

		mTabStrip = new SlidingTabStrip(context);
		addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}

	public void setCustomTabColorizer(TabColorizer tabColorizer) {
		mTabStrip.setCustomTabColorizer(tabColorizer);
	}

	public void setSelectedIndicatorColors(int... colors) {
		mTabStrip.setSelectedIndicatorColors(colors);
	}

	public void setDividerColors(int... colors) {
		mTabStrip.setDividerColors(colors);
	}

	public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
		mViewPagerPageChangeListener = listener;
	}

	public void setCustomTabView(int layoutResId, int textViewId) {
		mTabViewLayoutId = layoutResId;
		mTabViewTextViewId = textViewId;
	}

	public void setViewPager(ViewPager viewPager) {
		mTabStrip.removeAllViews();

		mViewPager = viewPager;
		if(viewPager != null)
		{
			viewPager.addOnPageChangeListener(new InternalViewPagerListener());
			populateTabStrip();
		}
	}

	protected TextView createDefaultTabView(Context context) {
		TextView textView = new TextView(context);
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(getResources().getColor(R.color.white));

		if(device.equals(SMALL))
		{
			TAB_VIEW_TEXT_SIZE_SP = 18;
		}
		else if(device.equals(MEDIUM))
		{
			TAB_VIEW_TEXT_SIZE_SP = 22;
		}
		else if(device.equals(LARGE))
		{
			TAB_VIEW_TEXT_SIZE_SP = 26;
		}

		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);

		// textView.setTypeface(Typeface.DEFAULT_BOLD);
		// for tabs adjustment on screens
		textView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
		LanguagePref languagePref = new LanguagePref(context);
		int languageIndex = languagePref.getLanguage();

		/*if(languageIndex == 0)
		{
			textView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
		}
		else
		{
			textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		}*/

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			// If we're running on Honeycomb or newer, then we can use the Theme's
			// selectableItemBackground to ensure that the View has a pressed state
			TypedValue outValue = new TypedValue();
			getContext().getTheme().resolveAttribute(R.attr.selectableItemBackground, outValue, true);
			textView.setBackgroundResource(outValue.resourceId);
		}

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		{
			// If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
			textView.setAllCaps(true);
		}

		if(device.equals(SMALL))
		{
			TAB_VIEW_PADDING_DIPS = 10;
		}
		else if(device.equals(MEDIUM))
		{
			TAB_VIEW_PADDING_DIPS = 15;
		}
		else if(device.equals(LARGE))
		{
			TAB_VIEW_PADDING_DIPS = 20;
		}

		int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);

		if(languageIndex == 0)
		{
			textView.setPadding(30, padding, 30, padding);
		}
		else
		{
			textView.setPadding(40, padding, 40, padding);
		}

		return textView;
	}

	private void populateTabStrip() {
		final PagerAdapter adapter = mViewPager.getAdapter();
		final OnClickListener tabClickListener = new TabClickListener();

		for (int i = 0; i < adapter.getCount(); i++)
		{
			View tabView = null;
			TextView tabTitleView = null;

			if(mTabViewLayoutId != 0)
			{
				// If there is a custom tab view layout id set, try and inflate it
				tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mTabStrip, false);
				tabTitleView = (TextView) tabView.findViewById(mTabViewTextViewId);
			}

			if(tabView == null)
			{
				tabView = createDefaultTabView(getContext());
			}

			if(tabTitleView == null && TextView.class.isInstance(tabView))
			{
				tabTitleView = (TextView) tabView;
			}

			tabTitleView.setText(adapter.getPageTitle(i));
			tabView.setOnClickListener(tabClickListener);

			mTabStrip.addView(tabView);
		}
		for (int i = 0; i < mTabStrip.getChildCount(); i++)
		{
			TextView tv = (TextView) mTabStrip.getChildAt(i);

			tv.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Roboto_Regular.ttf"));
			if(i == 0)
			{
				tv.setTextColor(Color.WHITE);
			}
			else
			{
				tv.setTextColor(Color.parseColor(TAB_VIEW_TEXT_SELECTED_COLOR));
			}
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if(mViewPager != null)
		{
			scrollToTab(mViewPager.getCurrentItem(), 0);
		}
	}

	private void scrollToTab(int tabIndex, int positionOffset) {
		final int tabStripChildCount = mTabStrip.getChildCount();
		if(tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount)
		{
			return;
		}

		View selectedChild = mTabStrip.getChildAt(tabIndex);
		if(selectedChild != null)
		{
			int targetScrollX = selectedChild.getLeft() + positionOffset;

			if(tabIndex > 0 || positionOffset > 0)
			{
				// If we're not at the first child and are mid-scroll, make sure we obey the offset
				targetScrollX -= mTitleOffset;
			}

			scrollTo(targetScrollX, 0);
		}
	}

	private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
		private int mScrollState;

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			int tabStripChildCount = mTabStrip.getChildCount();
			if((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount))
			{
				return;
			}

			mTabStrip.onViewPagerPageChanged(position, positionOffset);

			View selectedTitle = mTabStrip.getChildAt(position);
			int extraOffset = (selectedTitle != null) ? (int) (positionOffset * selectedTitle.getWidth()) : 0;
			scrollToTab(position, extraOffset);

			if(mViewPagerPageChangeListener != null)
			{
				mViewPagerPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			mScrollState = state;

			if(mViewPagerPageChangeListener != null)
			{
				mViewPagerPageChangeListener.onPageScrollStateChanged(state);
			}
		}

		@Override
		public void onPageSelected(int position) {
			if(mScrollState == ViewPager.SCROLL_STATE_IDLE)
			{
				mTabStrip.onViewPagerPageChanged(position, 0f);
				scrollToTab(position, 0);
			}
			for (int i = 0; i < mTabStrip.getChildCount(); i++)
			{
				TextView tv = (TextView) mTabStrip.getChildAt(i);
				if(i == position)
				{
					tv.setTextColor(Color.WHITE);
				}
				else
				{
					tv.setTextColor(Color.parseColor(TAB_VIEW_TEXT_SELECTED_COLOR));
				}
			}

			if(mViewPagerPageChangeListener != null)
			{
				mViewPagerPageChangeListener.onPageSelected(position);
			}
		}

	}

	private class TabClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			for (int i = 0; i < mTabStrip.getChildCount(); i++)
			{
				if(v == mTabStrip.getChildAt(i))
				{
					mViewPager.setCurrentItem(i);
					return;
				}
			}
		}
	}
}
