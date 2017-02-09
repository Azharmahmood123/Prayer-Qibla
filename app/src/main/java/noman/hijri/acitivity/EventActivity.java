package noman.hijri.acitivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import noman.Ads.AdIntegration;
import noman.CommunityGlobalClass;
import noman.hijri.fragment.EventFragment;


/**
 * Created by Administrator on 11/24/2016.
 */

public class EventActivity extends AdIntegration {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommunityGlobalClass.getInstance().mEventActivity = this;

        setContentView(R.layout.activity_event);
        RelativeLayout img_back=(RelativeLayout)findViewById(R.id.layout_drawer_menu_ic) ;
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linearAd));
        }



        moveToEventTab();

        //Send Screen Analytics
      //  CommunityGlobalClass.getInstance().sendAnalyticsScreen("Islamic Event 4.0");
    }

    public void moveToEventTab() {
        setFragment(new EventFragment());
    }

    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
