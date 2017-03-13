package noman.searchquran.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.quranreading.helper.DBManager;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;
import java.util.List;

import noman.searchquran.activity.TopicActivity;
import noman.searchquran.activity.TopicDetailList;
import noman.searchquran.adapter.TopicListAdapter;
import noman.searchquran.model.TopicList;

public class TopicListFragment extends Fragment {

    // Store instance variables

    public Activity mActivity;
    public List<TopicList> dataList = new ArrayList<TopicList>();
    public List<TopicList> surahNamesList = new ArrayList<TopicList>();
    public TopicListAdapter mTopicListAdapter;


    public TopicActivity mSearchTopicActivity;
    View mView;

    boolean inProcess = false;

    @Override
    public void onAttach(Context activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mActivity = getActivity();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        initializeIndexList();
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.noman_fragment_quran, container, false);
        this.mView = view;

        LinearLayout layoutLastRead = (LinearLayout) mView.findViewById(R.id.index_last_read);
        layoutLastRead.setVisibility(View.GONE);


        ListView listViewApps = (ListView) view.findViewById(R.id.listViewSurahsList);
        listViewApps.setAdapter(mTopicListAdapter);
        listViewApps.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (!inProcess) {
                    inProcess = true;
                    sendAnalyticsData();
                    Intent i = new Intent(getActivity(), TopicDetailList.class);
                    i.putExtra("title", dataList.get(position).getTopicName());
                    i.putExtra("topic_id", dataList.get(position).getId());
                    startActivity(i);
                    //    mSearchTopicActivity. setTitleToolbar(dataList.get(position).getTopicName());
                    //     mSearchTopicActivity.initFragement(TopicDetailListFragment.newInstance(dataList.get(position).getId(),mSearchTopicActivity));
                    mSearchTopicActivity.hideSearchBar();

                }

            }

        });


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        inProcess = false;
        mSearchTopicActivity.setTitleToolbar(getString(R.string.grid_search));
        mSearchTopicActivity.imgSearchBtn.setVisibility(View.VISIBLE);
    }

    public void initializeIndexList() {
        dataList.clear();
        surahNamesList.clear();
        DBManager dbObj = new DBManager(getActivity());
        dbObj.open();
        dataList = dbObj.getTopicList();
        surahNamesList = dbObj.getTopicList();
        dbObj.close();

        mTopicListAdapter = new TopicListAdapter(getActivity(), dataList);
    }

    private void sendAnalyticsData() {
        //AnalyticSingaltonClass.getInstance(mActivity.getBaseContext()).sendScreenAnalytics("Surahs Screen");
    }


}
