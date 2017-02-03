package noman.community.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.logger.BuildConfig;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;
import java.util.List;

import noman.community.activity.ComunityActivity;
import noman.community.adapter.PrayerAdapter;
import noman.CommunityGlobalClass;
import noman.community.prefrences.SavePreference;
import noman.community.utility.DebugInfo;
import noman.community.model.AllPrayerResponse;
import noman.community.model.GetAllPrayerRequest;
import noman.community.model.Prayer;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Administrator on 11/18/2016.
 */

public class PrayerFragment extends Fragment {

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    PrayerAdapter mPrayerAdapter;
    boolean isInternetAvailable = false;

    ComunityActivity mComunityActivity;
    TextView noData;
    public static PrayerFragment newInstance(ComunityActivity mComunityActivity) {
        PrayerFragment myFragment = new PrayerFragment();

        myFragment.mComunityActivity = mComunityActivity;

        return myFragment;

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CommunityGlobalClass.mPrayerFragment = this;
        View rootView = inflater.inflate(R.layout.layout_prayer_tab, container, false);

        CommunityGlobalClass.getInstance().sendAnalyticsScreen("Community Prayer");

        isInternetAvailable = CommunityGlobalClass.getInstance().isInternetOn();
        noData = (TextView) rootView.findViewById(R.id.txt_no);
        noData.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_prayer);
      //  mSwipeRefreshLayout.setColorScheme(getResources().getColor(android.R.color.holo_blue_bright), getResources().getColor(android.R.color.holo_green_light),getResources().getColor(android.R.color.holo_orange_light),getResources().getColor(android.R.color.holo_red_light));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                if (isInternetAvailable) {
                    refreshItems();
                } else {
                    //want to disable pull to refresh gestures and progress animations
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }
        });

        //For the first time
        CommunityGlobalClass.getInstance().showLoading(getActivity());
        //For most reset
        callToLoadPrayer("" + SavePreference.getMenuOption(getActivity()));


        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_filter).setVisible(true);
        mComunityActivity.menu_filter.setVisibility(View.VISIBLE);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        mComunityActivity.menu_filter.setVisibility(View.VISIBLE);
        if (mSwipeRefreshLayout != null) {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.destroyDrawingCache();
                mSwipeRefreshLayout.clearAnimation();
            }

        }

    }

    public void refreshItems() {
        //For most reset
        callToLoadPrayer("" + SavePreference.getMenuOption(getActivity()));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Because it show nullpointer in scrollview in recycler due to in tab fragment
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_prayer);

        onItemsLoadComplete();

    }

    public void onItemsLoadComplete() {
        // Update the adapter and notify data set changed

        if (getPrayerList().size() > 0) {
            noData.setVisibility(View.GONE);
        } else {
            noData.setVisibility(View.VISIBLE);
        }

        mPrayerAdapter = new PrayerAdapter(getPrayerList(), getActivity());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mPrayerAdapter);
        // Stop refresh animation
        // Stop refresh animation
        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.destroyDrawingCache();
                mSwipeRefreshLayout.clearAnimation();
            }

        }


    }

    public List<Prayer> getPrayerList() {
        List<Prayer> mList=new ArrayList<>();
        for(int i = 0; i< CommunityGlobalClass.mPrayerModel.size(); i++)
        {
            if(CommunityGlobalClass.mPrayerModel.get(i).getStatus().equals("1"))
            {
                mList.add(CommunityGlobalClass.mPrayerModel.get(i));
            }
        }


         return mList;
    }

    //Call web
    public void callToLoadPrayer(String isCounter) {
        //  CommunityGlobalClass.getInstance().showLoading(getActivity());
        GetAllPrayerRequest getAllPrayerRequest = new GetAllPrayerRequest();
        getAllPrayerRequest.setUserId(isCounter);
        Call<AllPrayerResponse> call = CommunityGlobalClass.getRestApi().getPrayers(getAllPrayerRequest);
        call.enqueue(new retrofit.Callback<AllPrayerResponse>() {


            @Override
            public void onResponse(Response<AllPrayerResponse> response, Retrofit retrofit) {
                CommunityGlobalClass.getInstance().cancelDialog();
                response.body();
                CommunityGlobalClass.mPrayerModel = response.body().getPrayers();
                onItemsLoadComplete();
            }

            @Override
            public void onFailure(Throwable t) {
                CommunityGlobalClass.getInstance().cancelDialog();
                if (BuildConfig.DEBUG)
                    DebugInfo.loggerException("Get Prayers-Failure" + t.getMessage());
                CommunityGlobalClass.getInstance().showServerFailureDialog(getActivity());
                // Stop refresh animation
                // Stop refresh animation
                if (mSwipeRefreshLayout.isRefreshing()) {
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.destroyDrawingCache();
                        mSwipeRefreshLayout.clearAnimation();
                    }

                }
            }
        });


    }
}
