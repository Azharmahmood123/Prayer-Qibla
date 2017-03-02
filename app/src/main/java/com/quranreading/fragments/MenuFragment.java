package com.quranreading.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quranreading.model.GridItem;
import com.quranreading.model.GridItems;
import com.quranreading.qibladirection.R;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by cyber on 11/30/2016.
 */

public class MenuFragment extends Fragment {

    Context mContext;

    public CirclePageIndicator mIndicator;
    private ViewPager awesomePager;
    private PagerAdapter pm;

    ArrayList<GridItem> codeCategory;
    public static boolean isSmallDevice = false;
    int type = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();


        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;

        if ((width > 1080 && height <= 2560)) {
            isSmallDevice = false;
        } else {
            //(width == 720 && height == 1280) || (width == 540 && height == 960)
            isSmallDevice = true;
        }

        ArrayList<String> menuItems = new ArrayList<>();
        menuItems.clear();
        menuItems.add(mContext.getString(R.string.grid_salat));
        menuItems.add(mContext.getString(R.string.grid_direction));
        menuItems.add(mContext.getString(R.string.grid_quran));
        menuItems.add(mContext.getString(R.string.grid_community));
        menuItems.add(mContext.getString(R.string.grid_search));

        menuItems.add(mContext.getString(R.string.grid_hijri));
        menuItems.add(mContext.getString(R.string.grid_mosque));
        menuItems.add(mContext.getString(R.string.grid_halal));
        menuItems.add(mContext.getString(R.string.grid_duas));
        menuItems.add(mContext.getString(R.string.grid_names));
        menuItems.add(mContext.getString(R.string.grid_settings));

        ArrayList<String> items = new ArrayList<>();

        GridItem m = new GridItem();
        for (int i = 0; i < menuItems.size(); i++) {
            items.add(i, menuItems.get(i));
            m.name = items.get(i);
        }

        codeCategory = new ArrayList<>();
        codeCategory.add(m);

        Iterator<String> it = items.iterator();

        List<MenuGridItemFragment> gridFragments = new ArrayList<>();

        int i = 0;
        while (it.hasNext()) {
            ArrayList<GridItems> itmLst = new ArrayList<>();

            GridItems itm = new GridItems(i, it.next());
            itmLst.add(itm);
            i = i + 1;

            if (it.hasNext()) {
                GridItems itm1 = new GridItems(i, it.next());
                itmLst.add(itm1);
                i = i + 1;
            }

            if (it.hasNext()) {
                GridItems itm2 = new GridItems(i, it.next());
                itmLst.add(itm2);
                i = i + 1;
            }

            if (it.hasNext()) {
                GridItems itm3 = new GridItems(i, it.next());
                itmLst.add(itm3);
                i = i + 1;
            }

            if (it.hasNext()) {
                GridItems itm4 = new GridItems(i, it.next());
                itmLst.add(itm4);
                i = i + 1;
            }

            if (it.hasNext()) {
                GridItems itm5 = new GridItems(i, it.next());
                itmLst.add(itm5);
                i = i + 1;
            }
            if (!isSmallDevice) {
                if (it.hasNext()) {
                    GridItems itm6 = new GridItems(i, it.next());
                    itmLst.add(itm6);
                    i = i + 1;
                }
                if (it.hasNext()) {
                    GridItems itm7 = new GridItems(i, it.next());
                    itmLst.add(itm7);
                    i = i + 1;
                }
                if (it.hasNext()) {
                    GridItems itm8 = new GridItems(i, it.next());
                    itmLst.add(itm8);
                    i = i + 1;
                }
              /*  if (it.hasNext()) {//Settings icon move next screen
                    GridItems itm9 = new GridItems(i, it.next());
                    itmLst.add(itm9);
                    i = i + 1;
                }*/
            }
            GridItems[] gp = {};
            GridItems[] gridPage = itmLst.toArray(gp);
            MenuGridItemFragment fragment = new MenuGridItemFragment();
            fragment.getInstance(gridPage, fragment);
            gridFragments.add(fragment);
        }

        pm = new PagerAdapter(getActivity().getSupportFragmentManager(), gridFragments);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_main, container, false);

        awesomePager = (ViewPager) view.findViewById(R.id.pager);
        mIndicator = (CirclePageIndicator) view.findViewById(R.id.pagerIndicator);

        awesomePager.setAdapter(pm);
        mIndicator.setViewPager(awesomePager);

        return view;
    }


    private class PagerAdapter extends FragmentStatePagerAdapter {
        private List<MenuGridItemFragment> fragments;

        public PagerAdapter(FragmentManager fm, List<MenuGridItemFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }


}
