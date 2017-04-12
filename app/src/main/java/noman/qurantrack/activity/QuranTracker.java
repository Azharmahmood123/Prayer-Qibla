package noman.qurantrack.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
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
import noman.community.model.SignInRequest;
import noman.community.model.SignUpResponse;
import noman.qurantrack.database.QuranTrackerDatabase;
import noman.qurantrack.fragment.Monthly;
import noman.qurantrack.fragment.Weekly;
import noman.qurantrack.fragment.Yearly;
import noman.qurantrack.model.QuranTrackerModel;
import noman.qurantrack.model.TargetModel;
import noman.qurantrack.sharedpreference.QuranTrackerPref;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by Administrator on 3/28/2017.
 */

public class QuranTracker extends AdIntegration {
    private SlidingTabLayout tabLayout;
    ViewPager viewPager;
    CardView containerProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quran_tracker_main);
        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linearAd));
        }

        containerProgress = (CardView) findViewById(R.id.ln_progress_container);
        LinearLayout btnCross = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        btnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        slidingTabs();

        Button btnAddProgress = (Button) findViewById(R.id.btn_add_progress);
        Button btnAddTarget = (Button) findViewById(R.id.btn_add_target);

        btnAddProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuranTracker.this, AddProgress.class));
            }
        });
        btnAddTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuranTracker.this, AddTarget.class));
            }
        });


        callToQuranTrackerData(CommunityGlobalClass.mSignInRequests);
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
        TargetModel model = mPref.getLastSaveEndDatePref();

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
                QuranTrackerDatabase quranTrackerDatabase = new QuranTrackerDatabase(this);

                model = mPref.getLastSaveStartDatePref();
                if (model != null) {
                    int totalRead = quranTrackerDatabase.getSumVerse(model.getDate(), model.getMonth(), model.getYear());
                    ayahLeft.setText(getString(R.string.txt_no_ayah_left) + " " + (6236 - totalRead));
                } else {
                    ayahLeft.setText(getString(R.string.txt_no_ayah_left) + " " + (6236 - 0));
                }
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


        QuranTrackerPref mPref = new QuranTrackerPref(this);
        TargetModel model = mPref.getLastSaveEndDatePref();
        TargetModel model2 = mPref.getLastSaveStartDatePref();

        if (model == null || model2 == null) {
            containerProgress.setVisibility(View.INVISIBLE);

        } else {
            containerProgress.setVisibility(View.VISIBLE);
        }
    }


    //Call webservice
    public void callToQuranTrackerData(final SignInRequest mSignUpRequest) {
        //  CommunityGlobalClass.getInstance().showLoading(this);
        Call<SignUpResponse> call = CommunityGlobalClass.getRestApi().signInUser(mSignUpRequest);
        call.enqueue(new retrofit.Callback<SignUpResponse>() {

            @Override
            public void onResponse(Response<SignUpResponse> response, Retrofit retrofit) {


                if (CommunityGlobalClass.moduleId == 3) {
                    if (response.body().getQuranTrackerList() != null && response.body().getQuranTrackerList().size() > 0) {
                        moveQuranTrackerToDatabase(response.body().getQuranTrackerList());
                    } else {
                        //        CommunityGlobalClass.getInstance().cancelDialog();
                    }
                }


            }

            @Override
            public void onFailure(Throwable t) {
                CommunityGlobalClass.getInstance().cancelDialog();
                CommunityGlobalClass.getInstance().showServerFailureDialog(QuranTracker.this);
            }
        });


    }

    private void moveQuranTrackerToDatabase(List<QuranTrackerModel> mSalatModelList) {

        QuranTrackerDatabase dbTracker = new QuranTrackerDatabase(this);

        for (int i = 0; i < mSalatModelList.size(); i++) {
            dbTracker.insertQuranTrackerData(false,mSalatModelList.get(i));

            if (i == mSalatModelList.size() - 1) {
                //        CommunityGlobalClass.getInstance().cancelDialog();
                setupViewPager(viewPager);
            }
        }


    }
}
