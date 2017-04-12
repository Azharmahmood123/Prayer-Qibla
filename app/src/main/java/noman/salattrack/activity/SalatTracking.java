package noman.salattrack.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.orhanobut.logger.BuildConfig;
import com.quranreading.helper.SlidingTabLayout;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;
import java.util.List;

import noman.Ads.AdIntegration;
import noman.CommunityGlobalClass;
import noman.community.activity.FacebookActivity;
import noman.community.activity.PostActivity;
import noman.community.model.SignInRequest;
import noman.community.model.SignUpResponse;
import noman.community.prefrences.SavePreference;
import noman.community.utility.DebugInfo;
import noman.salattrack.database.SalatTrackerDatabase;
import noman.salattrack.fragment.MonthlyTracker;
import noman.salattrack.fragment.WeeklyTracker;
import noman.salattrack.fragment.YearlyTracker;
import noman.salattrack.model.SalatModel;
import noman.sharedpreference.SurahsSharedPref;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Administrator on 3/16/2017.
 */

public class SalatTracking extends AdIntegration {
    private SlidingTabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker_main);
        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linearAd));
        }


        Button addPrayer = (Button) findViewById(R.id.btn_add_salat);
        addPrayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SalatTracking.this, AddPrayer.class));
            }
        });

        LinearLayout btnCross = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        btnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        slidingTabs();
        LinearLayout btnTracker = (LinearLayout) findViewById(R.id.tg_tracker);
        btnTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SurahsSharedPref surahsSharedPref=new SurahsSharedPref(SalatTracking.this);
                if( surahsSharedPref.getIsSalatTracking()) {
                    surahsSharedPref.setSalatTracking(false);
                }
                else
                {
                    surahsSharedPref.setSalatTracking(true);
                }

                trackerToogle();
            }
        });
        trackerToogle();


        //call service to get user salat data
        callToSalatData(CommunityGlobalClass.mSignInRequests);

    }

    public void trackerToogle() {
        ImageView imgTracker=(ImageView)findViewById(R.id.img_tracker);
        imgTracker.setImageResource(R.drawable.ic_tracker_off);
        SurahsSharedPref surahsSharedPref=new SurahsSharedPref(this);
       if( surahsSharedPref.getIsSalatTracking())
       {
           imgTracker.setImageResource(R.drawable.ic_tracker_on);
       }
    }

    public void slidingTabs() {
        viewPager = (ViewPager) findViewById(R.id.traker_view_pager);
        setupViewPager(viewPager);

        tabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        tabLayout.setViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        WeeklyTracker weeklyTracker = WeeklyTracker.newInstance(SalatTracking.this);
        MonthlyTracker monthlyTracker = MonthlyTracker.newInstance(SalatTracking.this);
        YearlyTracker yearlyTracker = YearlyTracker.newInstance(SalatTracking.this);


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

   //Call webservice
    public void callToSalatData(final SignInRequest mSignUpRequest) {
      //  CommunityGlobalClass.getInstance().showLoading(this);
        Call<SignUpResponse> call = CommunityGlobalClass.getRestApi().signInUser(mSignUpRequest);
        call.enqueue(new retrofit.Callback<SignUpResponse>() {

            @Override
            public void onResponse(Response<SignUpResponse> response, Retrofit retrofit) {


                if(CommunityGlobalClass.moduleId == 2)
                {
                    if(response.body().getSalats() != null && response.body().getSalats().size() > 0) {
                        moveSalatToDatabase(response.body().getSalats());
                    }
                    else
                    {
                //        CommunityGlobalClass.getInstance().cancelDialog();
                    }
                }



            }

            @Override
            public void onFailure(Throwable t) {
                CommunityGlobalClass.getInstance().cancelDialog();
                if (BuildConfig.DEBUG) DebugInfo.loggerException("SignUp-Failure" + t.getMessage());
                CommunityGlobalClass.getInstance().showServerFailureDialog(SalatTracking.this);
            }
        });


    }

    private void moveSalatToDatabase(List<SalatModel> mSalatModelList) {

        SalatTrackerDatabase dbTracker=new SalatTrackerDatabase(this);

        for(int i=0;i<mSalatModelList.size();i++) {
            dbTracker.insertSalatData(false,mSalatModelList.get(i));

            if(i == mSalatModelList.size()-1)
            {
        //        CommunityGlobalClass.getInstance().cancelDialog();
                setupViewPager(viewPager);
            }
        }


    }
}
