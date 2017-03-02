package noman.Tasbeeh.fragment;

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

import noman.Tasbeeh.activity.TasbeehActivity;
import noman.Tasbeeh.adapter.TasbeehListAdapter;
import noman.Tasbeeh.model.TasbeehModel;

public class TasbeehListFragment extends Fragment {

    // Store instance variables

    public Activity mActivity;
    public List<TasbeehModel> dataList = new ArrayList<TasbeehModel>();

    public TasbeehListAdapter mTopicListAdapter;

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

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.noman_fragment_quran, container, false);
        this.mView = view;

        LinearLayout layoutLastRead = (LinearLayout) mView.findViewById(R.id.index_last_read);
        layoutLastRead.setVisibility(View.GONE);

        initializeIndexList();
        ListView listViewApps = (ListView) view.findViewById(R.id.listViewSurahsList);
        listViewApps.setAdapter(mTopicListAdapter);
        listViewApps.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (!inProcess) {
                    inProcess = true;
                    sendAnalyticsData();
                    Intent tasbeh = new Intent(getActivity(), TasbeehActivity.class);
                    tasbeh.putExtra("id", dataList.get(position).getId());
                    startActivity(tasbeh);

                }

            }

        });


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        inProcess = false;


    }

    public void initializeIndexList() {
        dataList.clear();
        DBManager dbObj = new DBManager(getActivity());
        dbObj.open();
        dataList = dbObj.getTasbeehList();
        dbObj.close();

        mTopicListAdapter = new TasbeehListAdapter(getActivity(), dataList);
        ListView listViewApps = (ListView) mView.findViewById(R.id.listViewSurahsList);
        listViewApps.setAdapter(mTopicListAdapter);
    }

    private void sendAnalyticsData() {
        //AnalyticSingaltonClass.getInstance(mActivity.getBaseContext()).sendScreenAnalytics("Surahs Screen");
    }


}
