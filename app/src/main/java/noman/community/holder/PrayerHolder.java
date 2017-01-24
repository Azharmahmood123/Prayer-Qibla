package noman.community.holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.quranreading.qibladirection.R;


/**
 * Created by Administrator on 11/18/2016.
 */

public class PrayerHolder extends RecyclerView.ViewHolder {
    public TextView txt_userInfo, txt_counter, txt_prayer,txt_prayer_hit,txt_disablePrayer;
    public ImageView imgMenu;
    public CardView mCardContainer;

    public PrayerHolder(View view) {
        super(view);
        txt_userInfo = (TextView) view.findViewById(R.id.txt_info_user);
        txt_counter = (TextView) view.findViewById(R.id.txt_count_prayer);
        txt_prayer = (TextView) view.findViewById(R.id.txt_prayer);
        txt_prayer_hit = (TextView) view.findViewById(R.id.txt_prayer_hit);
        imgMenu=(ImageView) view.findViewById(R.id.img_menu);
        txt_disablePrayer=(TextView)view.findViewById(R.id.txt_disable);
        mCardContainer=(CardView)view.findViewById(R.id.cardContainer);
    }
}
