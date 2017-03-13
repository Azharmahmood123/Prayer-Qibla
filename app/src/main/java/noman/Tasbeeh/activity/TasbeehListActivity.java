package noman.Tasbeeh.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import noman.Ads.AdIntegration;
import noman.CommunityGlobalClass;
import noman.Tasbeeh.SharedPref;
import noman.Tasbeeh.fragment.TasbeehListFragment;

/**
 * Created by Administrator on 3/2/2017.
 */

public class TasbeehListActivity extends AdIntegration {


    public TextView txtToolbarTitle;

    LinearLayout imgBackBtn;
    TasbeehListFragment tasbeehListFragment;
    TextView tvTasbeehdetail, tvCounter, tvTasbehEng;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);

        setContentView(R.layout.tasbeeh_list_activity);
        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linearAd));
        }
        initateToolBarItems();

        tasbeehListFragment = new TasbeehListFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //    transaction.add(R.id.container, myf);
        transaction.add(R.id.container, tasbeehListFragment);

        transaction.commit();
        //  initFragement(mTopicListFragment);

        initializeTasbeehManual();
        showAnalytics(true,"");
    }

    public void initializeTasbeehManual() {
        tvTasbehEng = (TextView) findViewById(R.id.tvEnglishName);
        tvTasbeehdetail = (TextView) findViewById(R.id.verses);
        tvCounter = (TextView) findViewById(R.id.arabic_name);
        tvTasbehEng.setText(getString(R.string.grid_tasbeeh));
        updateCounterTasbeeh();

        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.index_row);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tasbeehActivity=new Intent(TasbeehListActivity.this,TasbeehActivity.class);
                tasbeehActivity.putExtra("isTasbeeh",true);
                startActivity(tasbeehActivity);
            }
        });
    }

    public void updateCounterTasbeeh() {
        tvTasbeehdetail.setText("Total: " + sharedPref.getSavedTotalReadTasbeehCount());
        tvCounter.setText("" + sharedPref.getSavedTasbeehCountValue() + "/" + sharedPref.getCountMode());
    }

    public void initateToolBarItems() {
        imgBackBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        txtToolbarTitle = (TextView) findViewById(R.id.txt_toolbar);
        txtToolbarTitle.setText(getString(R.string.grid_quran));
        imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setTitleToolbar(getString(R.string.grid_tasbeeh));

    }

    public void setTitleToolbar(String titleText) {
        if (txtToolbarTitle != null) {
            txtToolbarTitle.setText(titleText);
        }
    }


    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void initFragement(Fragment myf) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //    transaction.add(R.id.container, myf);
        transaction.replace(R.id.container, myf);
        transaction.addToBackStack(null);
        transaction.commit();

    }


    @Override
    public void onResume() {
        super.onResume();
        if (tvTasbehEng != null) {
            updateCounterTasbeeh();
        }

       if(tasbeehListFragment !=null) {
           if (tasbeehListFragment.mTopicListAdapter != null) {
               tasbeehListFragment.initializeIndexList();
           }
       }
    }


    @Override
    public void onBackPressed() {


        super.onBackPressed();


    }
    public void showAnalytics(boolean isScreen,String eventNAme) {
        String screenName="Tasbeeh";
        if(!isScreen) {
            CommunityGlobalClass.getInstance().sendAnalyticEvent(screenName,eventNAme);
        }
        else
        {
            CommunityGlobalClass.getInstance().sendAnalyticsScreen(screenName);
        }
    }
}
