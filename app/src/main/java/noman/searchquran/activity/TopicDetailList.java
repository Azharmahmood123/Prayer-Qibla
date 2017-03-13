package noman.searchquran.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import noman.Ads.AdIntegration;
import noman.CommunityGlobalClass;
import noman.searchquran.fragment.TopicDetailListFragment;

/**
 * Created by Administrator on 3/6/2017.
 */

public class TopicDetailList  extends AdIntegration {
    TextView txtToolbarTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);
        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linearAd));
        }

        initateToolBarItems();

//Getiing intent bundle

        int id=getIntent().getExtras().getInt("topic_id",0);
        String title=getIntent().getExtras().getString("title");
        txtToolbarTitle.setText(title);


        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //    transaction.add(R.id.container, myf);
        transaction.add(R.id.container, TopicDetailListFragment.newInstance(id));

        transaction.commit();
    }

    public void initateToolBarItems() {

        LinearLayout imgBackBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
         txtToolbarTitle = (TextView) findViewById(R.id.txt_toolbar);
       // txtToolbarTitle.setText(getString(R.string.grid_quran));
        imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }


}
