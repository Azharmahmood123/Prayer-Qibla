package noman.quran.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.R;

/**
 * Created by Administrator on 11/18/2016.
 */

public class ChildContainerHolder extends RecyclerView.ViewHolder {
    public TextView txt_juzz_english_para, txt_juzz_urdu_para;

    public TextView txt_fav_english_surah, txt_fav_urdu_surah, txt_fav_detail;
    public LinearLayout linearDeleteBtn;

    public TextView txt_sign_detail, txt_urdu_sign_detail;

    public LinearLayout containerJuzz, containerFavourite, containerStopSign, linearView, llEnglishFav;
    public RelativeLayout relUrduFav;

    public ChildContainerHolder(View view) {
        super(view);
        relUrduFav = (RelativeLayout) view.findViewById(R.id.relUrdFav);
        llEnglishFav = (LinearLayout) view.findViewById(R.id.llEnglishFav);
        containerJuzz = (LinearLayout) view.findViewById(R.id.containerJuzz);
        containerFavourite = (LinearLayout) view.findViewById(R.id.containerFavourite);
        containerStopSign = (LinearLayout) view.findViewById(R.id.containerStopSign);
        linearView = (LinearLayout) view.findViewById(R.id.view_sign);
        //Juzz
        txt_juzz_english_para = (TextView) view.findViewById(R.id.txt_eng_para_nav);
        txt_juzz_urdu_para = (TextView) view.findViewById(R.id.txt_urdu_para_nav);
        //Fav
        txt_fav_english_surah = (TextView) view.findViewById(R.id.txt_eng_surah_nav_child);
        txt_fav_urdu_surah = (TextView) view.findViewById(R.id.txt_urdu_surah_nav_child);
        txt_fav_detail = (TextView) view.findViewById(R.id.txt_detail_nav_child);
        linearDeleteBtn = (LinearLayout) view.findViewById(R.id.ll_delete_fav);
        //Stop
        txt_sign_detail = (TextView) view.findViewById(R.id.txt_sign_detail);
        txt_urdu_sign_detail = (TextView) view.findViewById(R.id.txt_urdu_sign_detail);

    }


}
