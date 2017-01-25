package noman.quran;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.Locale;

import noman.Ads.AdIntegration;
import noman.quran.adapter.ParentContainerList;
import noman.quran.fragment.QuranListFragment;
import quran.sharedpreference.SurahsSharedPref;


public class QuranModuleActivity extends AdIntegration implements View.OnClickListener {

    public DrawerLayout mDrawerLayout;
    public ActionBarDrawerToggle mDrawerToggle;
    public String mActivityTitle;
    RecyclerView recycler;

    ImageView imgHomeBtn, imgSearchBtn;
    TextView txtToolbarTitle;
    NestedScrollView nestedScrollView;
    ImageView btnSearch, btnSearchCross, btnSearchBack;
    LinearLayout layoutSearch, imgBackBtn;

    EditText etSearchName;
    QuranListFragment mQuranListFragment;
    ParentContainerList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.noman_navigation_main);
       /* InterstitialAd interstitialAd = CommunityGlobalClass.mInterstitialAd.getAd();
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }*/

        //


        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linearAd));
        }


        mDrawerLayout = (DrawerLayout) findViewById(R.id.layoutDrawer);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        mActivityTitle = getTitle().toString();

        recycler = (RecyclerView) findViewById(R.id.nav_list);

        addDrawerItems();
        setupDrawer();

        initateToolBarItems();


        /*Spannable text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);*/
        mQuranListFragment = new QuranListFragment();
        mQuranListFragment.quranModuleActivity = this;
        initFragement(mQuranListFragment);

        final SurahsSharedPref mSurahsSharedPref = new SurahsSharedPref(this);
        if (mSurahsSharedPref.getIsFirstTimeQuranOpen()) {
            showLanguageDailog();
            mSurahsSharedPref.setIsFirstTimeQuranOpen(false);
        } else if (mSurahsSharedPref.getIsSecondTimeQuranOpen()) {
            mSurahsSharedPref.setIsSecondTimeQuranOpen(false);
            new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                    .setTitle(getResources().getString(R.string.aya_of_day))
                    .setMessage("Do you want to turn on Ayah of the Day?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                            mSurahsSharedPref.setAyahNotification(true);


                        }
                    }).setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    }
            )
                    .show();
        }

    }

    public void showLanguageDailog() {

        CharSequence[] translationList = JuzConstant.translationList;
        final SurahsSharedPref mSurahsSharedPref = new SurahsSharedPref(this);

        // new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle(getResources().getString(R.string.txt_translation))
                .setSingleChoiceItems(translationList, mSurahsSharedPref.getTranslationIndex(), null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        mSurahsSharedPref.setTranslationIndex(selectedPosition);


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

    public void initateToolBarItems() {
        final SurahsSharedPref surahsSharedPref = new SurahsSharedPref(this);
        imgBackBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        imgSearchBtn = (ImageView) findViewById(R.id.toolbar_btnSearch);
        imgHomeBtn = (ImageView) findViewById(R.id.toolbar_btnMenu);
        txtToolbarTitle = (TextView) findViewById(R.id.txt_toolbar);
        imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operateDrawerOnButton(true);
            }
        });
        imgHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surahsSharedPref.setIsFirstTimeMenuOpen(false);
                if (!surahsSharedPref.getIsFirstTimeMenuOpen()) {
                    imgHomeBtn.setImageResource(R.drawable.ic_menu_hamburg);
                }
                operateDrawerOnButton(false);
            }
        });
        imgSearchBtn.setOnClickListener(this);

        if (!surahsSharedPref.getIsFirstTimeMenuOpen()) {
            imgHomeBtn.setImageResource(R.drawable.ic_menu_hamburg);
        }

        setTitleToolbar(getString(R.string.quran));
        initateSearchBar();


    }

    public void initateSearchBar() {
        layoutSearch = (LinearLayout) findViewById(R.id.layout_surah_search);
        btnSearch = (ImageView) findViewById(R.id.btn_option_search);
        btnSearchBack = (ImageView) findViewById(R.id.btn_search_back);
        btnSearchCross = (ImageView) findViewById(R.id.btn_search_cross);
        etSearchName = (EditText) findViewById(R.id.edit_search);

        btnSearchBack.setOnClickListener(this);
        btnSearchCross.setOnClickListener(this);

        etSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = etSearchName.getText().toString().trim().toLowerCase(Locale.US);
                int textlength = searchText.length();

                mQuranListFragment.dataList.clear();

                int size = mQuranListFragment.surahNamesList.size();

                for (int i = 0; i < size; i++) {
                    String value = mQuranListFragment.surahNamesList.get(i).getEngSurahName().trim();

                    if (textlength <= value.length()) {
                        if (value.toLowerCase(Locale.US).contains(searchText)) {
                            mQuranListFragment.dataList.add(mQuranListFragment.surahNamesList.get(i));
                        } else {
                            value = value.replace("'", "");
                            value = value.replace("-", "");
                            value = value.replace(" ", "");

                            if (value.toLowerCase(Locale.US).contains(searchText)) {
                                mQuranListFragment.dataList.add(mQuranListFragment.surahNamesList.get(i));
                            }
                        }
                    }
                }

                mQuranListFragment.adapterSurahList.notifyDataSetChanged();
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


    //Drawer Handling operation
    public void operateDrawerOnButton(Boolean isBackBtn) {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            closeDrawer();
        } else {
            if (!isBackBtn) {
                openDrawer();
            } else {
                finish();
            }

        }
    }

    public void closeDrawer() {
        new Handler().postDelayed(closeDrawerRunnable(), 200);
    }

    public void openDrawer() {
        new Handler().postDelayed(openDrawerRunnable(), 200);
    }

    private Runnable openDrawerRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                hideKeyBoard();
                mDrawerLayout.openDrawer(GravityCompat.END);
            }
        };
    }

    private Runnable closeDrawerRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                adapter.selectorParentNav(-1);
                mDrawerLayout.closeDrawer(nestedScrollView);
            }
        };
    }
// ************** End *************


    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void addDrawerItems() {
        adapter = new ParentContainerList(this, mQuranListFragment, this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    private void initFragement(Fragment myf) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.container, myf);
        transaction.commit();

    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_DRAGGING) {
                    adapter.selectorParentNav(-1);
                }

            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mQuranListFragment != null) {
            mQuranListFragment.lastReadContainer();
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Make SEarch Code
    public void hideSearchBar() {
        hideKeyBoard();
        mQuranListFragment.dataList.clear();
        mQuranListFragment.dataList.addAll(mQuranListFragment.surahNamesList);
        etSearchName.setText("");
        mQuranListFragment.adapterSurahList.notifyDataSetChanged();
        layoutSearch.setVisibility(View.GONE);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        if (mQuranListFragment.layoutLastRead != null) {
            SurahsSharedPref settngPref = new SurahsSharedPref(QuranModuleActivity.this);
            if (settngPref.getLastRead() >= 0) {
                mQuranListFragment.layoutLastRead.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showSearchBar() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            closeDrawer();
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, nestedScrollView);
        }
        showSoftKeyboard();
        layoutSearch.setVisibility(View.VISIBLE);

        if (mQuranListFragment.layoutLastRead != null) {
            mQuranListFragment.layoutLastRead.setVisibility(View.GONE);
        }

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
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            closeDrawer();
            return;
        } else {
            super.onBackPressed();
        }
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

}
