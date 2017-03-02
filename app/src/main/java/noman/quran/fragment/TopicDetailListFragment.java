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

import com.quranreading.helper.DBManager;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;
import java.util.List;

import noman.quran.TopicActivity;
import noman.quran.activity.QuranReadActivity;
import noman.quran.adapter.TopicDetailListAdapter;
import noman.quran.model.TopicModel;

import static quran.activities.SurahActivity.KEY_EXTRA_SURAH_NO;

public class TopicDetailListFragment extends Fragment {

    // Store instance variables

    public TopicActivity mActivity;
    public List<TopicModel> dataList = new ArrayList<TopicModel>();
    //   public List<TopicModel> surahNamesList = new ArrayList<TopicModel>();
    public TopicDetailListAdapter mTopicListAdapter;
    boolean inProcess = false;
    int topicID = 0;
    View mView;

    public static final TopicDetailListFragment newInstance(int topicId, TopicActivity mActivity) {
        TopicDetailListFragment f = new TopicDetailListFragment();
        f.topicID = topicId;
        f.mActivity=mActivity;
        return f;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                    Intent end_actvty = new Intent(mActivity, QuranReadActivity.class);

                    end_actvty.putExtra(KEY_EXTRA_SURAH_NO,dataList.get(position).getSurahNo());
                    if(dataList.get(position).getSurahNo()==1 || dataList.get(position).getSurahNo()==9) {
                        end_actvty.putExtra(QuranReadActivity.KEY_EXTRA_AYAH_NO, dataList.get(position).getVersesNo()-1);
                    }
                    else
                    {
                        end_actvty.putExtra(QuranReadActivity.KEY_EXTRA_AYAH_NO, dataList.get(position).getVersesNo());
                    }
                    end_actvty.putExtra(QuranReadActivity.KEY_EXTRA_IS_TOPIC,true);
                    startActivity(end_actvty);
                }
            }
        });


        return view;
    }


    public void initializeIndexList() {
        dataList.clear();
        //   surahNamesList.clear();
        DBManager dbObj = new DBManager(getActivity());
        dbObj.open();
        dataList = dbObj.getTopicDetailList(topicID);
        //  surahNamesList = dbObj.getTopicDetailList();
        dbObj.close();

        mTopicListAdapter = new TopicDetailListAdapter(getActivity(), dataList);
    }

    private void sendAnalyticsData() {
        //AnalyticSingaltonClass.getInstance(mActivity.getBaseContext()).sendScreenAnalytics("Surahs Screen");
    }


    @Override
    public void onResume() {
        super.onResume();
        inProcess = false;
        mActivity.imgSearchBtn.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }


}
