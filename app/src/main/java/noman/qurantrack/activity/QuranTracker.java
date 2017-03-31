package noman.qurantrack.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.helper.SlidingTabLayout;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import noman.Ads.AdIntegration;
import noman.CommunityGlobalClass;
import noman.qurantrack.database.QuranTrackerDatabase;
import noman.qurantrack.fragment.Monthly;
import noman.qurantrack.fragment.Weekly;
import noman.qurantrack.fragment.Yearly;
import noman.qurantrack.model.TargetModel;
import noman.qurantrack.sharedpreference.QuranTrackerPref;


/**
 * Created by Administrator on 3/28/2017.
 */

public class QuranTracker extends AdIntegration {
    private SlidingTabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quran_tracker_main);
        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linearAd));
        }


        LinearLayout btnCross = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        btnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        slidingTabs();

        Button btnAddProgress=(Button)findViewById(R.id.btn_add_progress);
        Button btnAddTarget=(Button)findViewById(R.id.btn_add_target);

        btnAddProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuranTracker.this,AddProgress.class));
            }
        });
        btnAddTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuranTracker.this,AddTarget.class));
            }
        });

    }


    public void slidingTabs() {
        viewPager = (ViewPager) findViewById(R.id.traker_view_pager);
        setupViewPager(viewPager);

        tabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        tabLayout.setViewPager(viewPager);



    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Weekly weeklyTracker = Weekly.newInstance(this);
        Monthly monthlyTracker = Monthly.newInstance(this);
        Yearly yearlyTracker = Yearly.newInstance(this);


        adapter.addFragment(weeklyTracker, getResources().getString(R.string.tab_weekly));
        adapter.addFragment(monthlyTracker, getResources().getString(R.string.tab_monthly));
        adapter.addFragment(yearlyTracker, getResources().getString(R.string.tab_yearly));

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void timeLeftContainer() {
        TextView daysLeft = (TextView) findViewById(R.id.txt_days_left);
        TextView ayahLeft = (TextView) findViewById(R.id.txt_ayah_left);



        QuranTrackerPref mPref = new QuranTrackerPref(this);
        TargetModel model = mPref.getDataFromSharedPreferences();

        if (model != null) {
            Calendar curCalendar = Calendar.getInstance();
            int curYear = curCalendar.get(Calendar.YEAR);
            int curMonth = curCalendar.get(Calendar.MONTH) + 1;
            int curDate = curCalendar.get(Calendar.DAY_OF_MONTH);

            int[] sDates = {curDate, curMonth, curYear};
            int[] eDates = {model.getDate(), model.getMonth(), model.getYear()};
            int daysBetween = CommunityGlobalClass.getInstance().findDaysDiff(sDates, eDates);
            daysLeft.setText(this.getString(R.string.txt_no_days_left) + " " + daysBetween);

            //Check current dates and end dates
            if (curDate > model.getDate() && curMonth >= model.getMonth() && curYear >= model.getYear()) {
                //Target dates end expect current date
                ayahLeft.setText(getString(R.string.txt_no_ayah_left));
                daysLeft.setText(getString(R.string.txt_no_days_left));
            } else {
                QuranTrackerDatabase quranTrackerDatabase=new QuranTrackerDatabase(this);
                int totalRead = quranTrackerDatabase.getSumVerse();
                ayahLeft.setText(getString(R.string.txt_no_ayah_left) + " " + (6236 - totalRead));
            }
        } else {
            daysLeft.setText(getString(R.string.txt_no_days_left));
            ayahLeft.setText(getString(R.string.txt_no_ayah_left));
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        timeLeftContainer();
    }
}
