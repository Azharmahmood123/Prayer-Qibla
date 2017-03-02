package noman.quran.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.Locale;

import noman.Ads.AdIntegration;
import noman.quran.fragment.SearchListFragment;


public class SearchQuranResultActivity extends AdIntegration  {


    public String mActivityTitle;


    public ImageView imgSearchBtn;
    public TextView txtToolbarTitle;

    LinearLayout imgBackBtn;

    SearchListFragment mTopicListFragment;
    String word;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.quran_search_result);
        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linearAd));
        }
        mActivityTitle = getTitle().toString();
        initateToolBarItems();



        Bundle b=getIntent().getExtras();
        word = b.getString("Search");

        mTopicListFragment = SearchListFragment.newInstance(word, SearchQuranResultActivity.this);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //    transaction.add(R.id.container, myf);
        transaction.add(R.id.container, mTopicListFragment);
        transaction.commit();
        setTitleToolbar(word);
    }


    public void initateToolBarItems() {

        imgBackBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        txtToolbarTitle = (TextView) findViewById(R.id.txt_toolbar);
      //  txtToolbarTitle.setText(getString(R.string.grid_quran));
        imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



    public void setTitleToolbar(String titleText) {
        if (txtToolbarTitle != null) {
            txtToolbarTitle.setText(titleText);
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

    }







}
