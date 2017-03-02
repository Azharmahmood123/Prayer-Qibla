package noman.quran.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.quranreading.helper.DBManager;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;
import java.util.List;

import noman.quran.activity.QuranReadActivity;
import noman.quran.activity.SearchQuranResultActivity;
import noman.quran.adapter.SearchListAdapter;
import noman.quran.model.TopicModel;

import static quran.activities.SurahActivity.KEY_EXTRA_SURAH_NO;

public class SearchListFragment extends Fragment {

    // Store instance variables

    public SearchQuranResultActivity mActivity;
    public List<TopicModel> dataList = new ArrayList<TopicModel>();
    //   public List<TopicModel> surahNamesList = new ArrayList<TopicModel>();
    public SearchListAdapter mTopicListAdapter;
    boolean inProcess = false;
    String topicID = "";
    View mView;


    ListView listViewApps;

    public static final SearchListFragment newInstance(String word, SearchQuranResultActivity mActivity) {
        SearchListFragment f = new SearchListFragment();
        f.topicID = word;
        f.mActivity = mActivity;
        return f;
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.noman_fragment_quran, container, false);
        this.mView = view;

        LinearLayout layoutLastRead = (LinearLayout) mView.findViewById(R.id.index_last_read);
        layoutLastRead.setVisibility(View.GONE);


        listViewApps = (ListView) view.findViewById(R.id.listViewSurahsList);
        listViewApps.setAdapter(mTopicListAdapter);
        initializeIndexList(topicID);
        listViewApps.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (!inProcess) {
                    inProcess = true;
                    sendAnalyticsData();
                    Intent end_actvty = new Intent(mActivity, QuranReadActivity.class);

                    end_actvty.putExtra(KEY_EXTRA_SURAH_NO, dataList.get(position).getSurahNo());
                    if (dataList.get(position).getSurahNo() == 1 || dataList.get(position).getSurahNo() == 9) {
                        end_actvty.putExtra(QuranReadActivity.KEY_EXTRA_AYAH_NO, dataList.get(position).getVersesNo() - 1);
                    } else {
                        end_actvty.putExtra(QuranReadActivity.KEY_EXTRA_AYAH_NO, dataList.get(position).getVersesNo());
                    }
                    end_actvty.putExtra(QuranReadActivity.KEY_EXTRA_IS_TOPIC, true);
                    startActivity(end_actvty);
                }
            }
        });


        return view;
    }


    public void initializeIndexList(String word) {
        dataList.clear();
        DBManager dbObj = new DBManager(getActivity());
        dbObj.open();
        dataList = dbObj.getSearchList(word);
        dbObj.close();

        if (dataList.size() == 0) {
            TextView noResult = (TextView) mView.findViewById(R.id.text_no_result);
            noResult.setVisibility(View.VISIBLE);

        }
        mTopicListAdapter = new SearchListAdapter(getActivity(), dataList);
        listViewApps.setAdapter(mTopicListAdapter);

    }

    private void sendAnalyticsData() {
        //AnalyticSingaltonClass.getInstance(mActivity.getBaseContext()).sendScreenAnalytics("Surahs Screen");
    }


    @Override
    public void onResume() {
        super.onResume();
        inProcess = false;

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }


}
