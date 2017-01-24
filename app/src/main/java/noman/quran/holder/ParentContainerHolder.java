package noman.quran.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.R;

/**
 * Created by Administrator on 11/18/2016.
 */

public class ParentContainerHolder extends RecyclerView.ViewHolder {
    public TextView name, counter;
    public  ImageView img_icon;
    public  LinearLayout containerLinear;
public  RecyclerView mRecyclerView;
    public ParentContainerHolder(View view) {
        super(view);
        counter = (TextView) view.findViewById(R.id.txt_nav_counter);
        name = (TextView) view.findViewById(R.id.item_name);
        img_icon = (ImageView) view.findViewById(R.id.img_nav_icon);
        containerLinear = (LinearLayout) view.findViewById(R.id.containerNavParent);
        mRecyclerView=(RecyclerView)view.findViewById(R.id.nav_child_list);
    }

}
