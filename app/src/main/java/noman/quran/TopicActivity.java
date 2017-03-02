package noman.quran;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.Locale;

import noman.Ads.AdIntegration;
import noman.CommunityGlobalClass;
import noman.quran.activity.SearchQuranResultActivity;
import noman.quran.fragment.TopicListFragment;


public class TopicActivity extends AdIntegration implements View.OnClickListener {


    public String mActivityTitle;
    RecyclerView recycler;

   public LinearLayout imgSearchBtn;
    public TextView txtToolbarTitle;
    ImageView btnSearch, btnSearchCross, btnSearchBack;
    LinearLayout layoutSearch, imgBackBtn;

    EditText etSearchName;
    TopicListFragment mTopicListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_topic_quran);
        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linearAd));
        }
        mActivityTitle = getTitle().toString();

        recycler = (RecyclerView) findViewById(R.id.nav_list);

        initateToolBarItems();

        mTopicListFragment = new TopicListFragment();
        mTopicListFragment.mSearchTopicActivity = this;

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //    transaction.add(R.id.container, myf);
        transaction.add(R.id.container, mTopicListFragment);

        transaction.commit();
        //  initFragement(mTopicListFragment);


    }


    public void initateToolBarItems() {

        imgBackBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        imgSearchBtn = (LinearLayout) findViewById(R.id.toolbar_btnSearch);

        txtToolbarTitle = (TextView) findViewById(R.id.txt_toolbar);
        txtToolbarTitle.setText(getString(R.string.grid_quran));
        imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgSearchBtn.setOnClickListener(this);

        setTitleToolbar(getString(R.string.grid_search));
        initateSearchBar();
        initateQuranSearchBar();

    }


    public void initateQuranSearchBar() {


       final EditText   etSearchQuran = (EditText) findViewById(R.id.edit_search_quran);
        etSearchQuran.setText("");
        // Handle Done Button of keyboard
        etSearchQuran.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {

                    String searchText = etSearchQuran.getText().toString().trim();

                    if (searchText.length() <= 0) {
                        CommunityGlobalClass.getInstance().showShortToast(getResources().getString(R.string.no_text_search), 500, Gravity.CENTER);
                         hideKeyBoard();
                    }
                    else
                    {
                        hideKeyBoard();
                        searchText = searchText.replace("'", "");
                        searchText = searchText.replace("-", "");
                        searchText = searchText.replace(" ", "");

                        Intent searchWord=new Intent(TopicActivity.this, SearchQuranResultActivity.class);
                        searchWord.putExtra("Search",searchText);
                        startActivity(searchWord);
                    }
                }
                return false;
            }
        });
        final Button searchQuran=(Button)findViewById(R.id.btn_search_quran) ;
        searchQuran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = etSearchQuran.getText().toString().trim();
                if (searchText.length() <= 0) {
                    CommunityGlobalClass.getInstance().showShortToast(getResources().getString(R.string.no_text_search), 500, Gravity.CENTER);
                    hideKeyBoard();
                }
                else
                {
                    hideKeyBoard();
                    searchText = searchText.replace("'", "");
                    searchText = searchText.replace("-", "");
                    searchText = searchText.replace(" ", "");

                    Intent searchWord=new Intent(TopicActivity.this, SearchQuranResultActivity.class);
                    searchWord.putExtra("Search",searchText);
                    startActivity(searchWord);
                }
            }
        });


    }



    public void initateSearchBar() {
        layoutSearch = (LinearLayout) findViewById(R.id.layout_surah_search);
        btnSearch = (ImageView) findViewById(R.id.btn_option_search);
        btnSearchBack = (ImageView) findViewById(R.id.btn_search_back);
        btnSearchCross = (ImageView) findViewById(R.id.btn_search_cross);
        etSearchName = (EditText) findViewById(R.id.edit_search);
        btnSearchBack.setOnClickListener(this);
        btnSearchCross.setOnClickListener(this);
        // Handle Done Button of keyboard
        etSearchName.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (mTopicListFragment.dataList.size() <= 0) {
                        CommunityGlobalClass.getInstance().showShortToast(getResources().getString(R.string.no_topic_found), 500, Gravity.CENTER);
                        hideSearchBar();
                    }
                }
                return false;
            }
        });


        etSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = etSearchName.getText().toString().trim().toLowerCase(Locale.US);
                int textlength = searchText.length();
                mTopicListFragment.dataList.clear();
                int size = mTopicListFragment.surahNamesList.size();
                for (int i = 0; i < size; i++) {
                    String value = mTopicListFragment.surahNamesList.get(i).getTopicName().trim();

                    if (textlength <= value.length()) {
                        if (value.toLowerCase(Locale.US).contains(searchText)) {
                            mTopicListFragment.dataList.add(mTopicListFragment.surahNamesList.get(i));
                        } else {
                            value = value.replace("'", "");
                            value = value.replace("-", "");
                            value = value.replace(" ", "");

                            if (value.toLowerCase(Locale.US).contains(searchText)) {
                                mTopicListFragment.dataList.add(mTopicListFragment.surahNamesList.get(i));
                            }
                        }
                    }
                }

                mTopicListFragment.mTopicListAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
        initateQuranSearchBar();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toolbar_btnSearch:
                showSearchBar();

                break;

            case R.id.btn_search_back:
                hideSearchBar();
                break;

            case R.id.btn_search_cross:
                hideSearchBar();
                break;

            default:
                break;
        }
    }


    //Make SEarch Code
    public void hideSearchBar() {
        hideKeyBoard();
        mTopicListFragment.dataList.clear();
        mTopicListFragment.dataList.addAll(mTopicListFragment.surahNamesList);
        etSearchName.setText("");
        mTopicListFragment.mTopicListAdapter.notifyDataSetChanged();
        layoutSearch.setVisibility(View.GONE);


    }

    public void showSearchBar() {

        //CommunityGlobalClass.getInstance().sendAnalyticEvent("Quran 4.0", "Quran Search Icon");
        showSoftKeyboard();
        layoutSearch.setVisibility(View.VISIBLE);


    }

    public void showSoftKeyboard() {

        etSearchName.requestFocus();
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(etSearchName, 0);
    }

    @Override
    public void onBackPressed() {

        if (layoutSearch.getVisibility() == View.VISIBLE) {
            hideSearchBar();
            return;
        }
        super.onBackPressed();


    }

}
