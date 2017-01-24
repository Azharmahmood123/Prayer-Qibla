package noman.community.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.quranreading.helper.SlidingTabLayout;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;
import java.util.List;

import noman.Ads.AdIntegration;
import noman.CommunityGlobalClass;
import noman.community.adapter.PrayerAdapter;
import noman.community.fragment.MineFragment;
import noman.community.fragment.PrayerFragment;
import noman.community.prefrences.SavePreference;


public class ComunityActivity extends AdIntegration {

    private SlidingTabLayout tabLayout;
    ViewPager viewPager;
    Menu menu;

    private long lastClick = 0;
    public RelativeLayout menu_filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linearAd));
        }
        CommunityGlobalClass.mCommunityActivity = this;
        //get PreFerence in the list
        SavePreference savePreference = new SavePreference();
        CommunityGlobalClass.mSignInRequests = savePreference.getDataFromSharedPreferences();
        CommunityGlobalClass.getInstance().getHashKey(getPackageName());

        RelativeLayout img_back = (RelativeLayout) findViewById(R.id.layout_drawer_menu_ic);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        menu_filter=(RelativeLayout) findViewById(R.id.menu_filter);
        menu_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogFilter();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        tabLayout.setViewPager(viewPager);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClick < 2000) {
                    return;
                } else {
                    if( CommunityGlobalClass.mPrayerModel.size() > 1) {
                        if (PrayerAdapter.clickingCounter == 3) {
                            if (CommunityGlobalClass.mSignInRequests == null) {
                                startActivity(new Intent(ComunityActivity.this, LoginActivity.class));
                            } else {
                                moveToMineTab();
                                startActivity(new Intent(ComunityActivity.this, PostActivity.class));
                            }
                        } else {
                            CommunityGlobalClass.getInstance().showToast("Please Pray for " + (3 - PrayerAdapter.clickingCounter) + " other users to submit your own request to pray");
                        }
                    }
                    else
                    {
                        if (CommunityGlobalClass.mSignInRequests == null) {
                            startActivity(new Intent(ComunityActivity.this, LoginActivity.class));
                        } else {
                            moveToMineTab();
                            startActivity(new Intent(ComunityActivity.this, PostActivity.class));
                        }
                    }
                }
                lastClick = SystemClock.elapsedRealtime();


            }
        });

    }

    public void moveToMineTab() {
        viewPager.setCurrentItem(1, true);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        MineFragment prayerMine = MineFragment.newInstance(ComunityActivity.this);
        PrayerFragment prayer = PrayerFragment.newInstance(ComunityActivity.this);
        adapter.addFragment(prayer, getResources().getString(R.string.tab_Prayer));
        adapter.addFragment(prayerMine, getResources().getString(R.string.tab_mine));
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

  /*  @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_community, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            showDialogFilter();
            return true;
        }
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
*/

    public void showDialogFilter() {

        CharSequence[] array = {"Most Recent", "Most Prayed"};
        AlertDialog a=  new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle("Select Option")
                .setSingleChoiceItems(array, SavePreference.getMenuOption(ComunityActivity.this), null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        SavePreference.setMenuOption(ComunityActivity.this, selectedPosition);
                        CommunityGlobalClass.getInstance().showLoading(ComunityActivity.this);
                        CommunityGlobalClass.mPrayerFragment.refreshItems();
                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
        )
                .show();


    }
}
