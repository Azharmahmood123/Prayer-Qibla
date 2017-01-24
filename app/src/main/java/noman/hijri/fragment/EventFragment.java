package noman.hijri.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.quranreading.qibladirection.R;

import noman.hijri.adapter.EventAdapter;
import noman.CommunityGlobalClass;
import noman.community.expandablerecycler.ExpandableRecyclerAdapter;
import noman.hijri.helper.DateConverter;


/**
 * Created by Administrator on 11/11/2016.
 */

public class EventFragment extends Fragment {
    public RecyclerView recycler;
    EventAdapter adapter;
    int hijriYear, minYear, maxYear;
    boolean outChk = false;

    TextView tvHijriYear;
    DateConverter dateConverter;
    ImageButton btnPrevYear, btnNextYear;
    int postion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.layout_events, container, false);

        recycler = (RecyclerView) rootView.findViewById(R.id.main_recycler);

       /* adapter = new EventAdapter(getActivity());
        adapter.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);*/


        tvHijriYear = (TextView) rootView.findViewById(R.id.tv_hijri_year);
        btnPrevYear = (ImageButton) rootView.findViewById(R.id.btn_prev);
        btnNextYear = (ImageButton) rootView.findViewById(R.id.btn_next);

        dateConverter = new DateConverter(getActivity());

        btnPrevYear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                hijriYear = hijriYear - 1;

                if (hijriYear >= minYear) {
                    tvHijriYear.setText(String.valueOf(hijriYear));
                    ((CommunityGlobalClass) getActivity().getApplication()).yearSelected = hijriYear;
                    //Re-Adjust the date and time
                    adapter = new EventAdapter(getActivity(),EventFragment.this);
                    adapter.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);
                     LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                  
                    recycler.setLayoutManager(linearLayoutManager);

                    recycler.setAdapter(adapter);
                }

                if (hijriYear < minYear) {
                    hijriYear = minYear;
                }
            }
        });

        btnNextYear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                hijriYear = hijriYear + 1;

                if (hijriYear <= maxYear) {
                    tvHijriYear.setText(String.valueOf(hijriYear));
                    ((CommunityGlobalClass) getActivity().getApplication()).yearSelected = hijriYear;
                    //Re-Adjust the date and time
                    adapter = new EventAdapter(getActivity(),EventFragment.this);
                    adapter.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                  
                    recycler.setLayoutManager(linearLayoutManager);
                    recycler.setAdapter(adapter);
                }

                if (hijriYear > maxYear) {
                    hijriYear = maxYear;
                }
            }
        });



        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            populateList();
        } else {
               if(adapter !=null)
               {
                    adapter.collapseAll();
                 }

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!outChk)
            populateList();



        outChk = false;
    }

    public void populateList() {

        postion = ((CommunityGlobalClass) getActivity().getApplicationContext()).selectedEvent;
        if(postion > -1)
        {
            hijriYear =  ((CommunityGlobalClass) getActivity().getApplication()).yearEvent;

        }
        else {
            hijriYear = dateConverter.getHijriYear();
        }
        minYear = hijriYear - 100;
        maxYear = hijriYear + 100;

        tvHijriYear.setText(String.valueOf(hijriYear));

        ((CommunityGlobalClass) getActivity().getApplication()).yearSelected = hijriYear;

        //Re-Adjust the date and time
        adapter = new EventAdapter(getActivity(),EventFragment.this);
        adapter.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
      
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(adapter);

        if (postion > -1) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.expandItems(postion, true);

                    postion = -1;
                    ((CommunityGlobalClass) getActivity().getApplicationContext()).selectedEvent = postion;
                }
            }, 500);


        }


        //If open from the intent notificaiton
        if(CommunityGlobalClass.mEventActivity.getIntent().getExtras() !=null)
        {
            if(CommunityGlobalClass.getInstance().isTodayEvent)
            {
                postion = ((CommunityGlobalClass) getActivity().getApplicationContext()).todayEventPostion;
                if (postion > -1) {
                    Log.e("Expanded-- pos", "" + postion);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            adapter.expandItems(postion, true);
                            postion = -1;
                            ((CommunityGlobalClass) getActivity().getApplicationContext()).selectedEvent = postion;
                        }
                    }, 500);

                }
            }
        }

    }


    //Check Automatically is Today is Event
}
