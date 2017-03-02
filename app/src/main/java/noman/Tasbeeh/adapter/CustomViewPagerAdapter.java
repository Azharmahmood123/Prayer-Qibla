package noman.Tasbeeh.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.List;

import noman.Tasbeeh.model.TasbeehModel;
import quran.arabicutils.ArabicUtilities;

/**
 * Created by Administrator on 3/2/2017.
 */

public class CustomViewPagerAdapter extends PagerAdapter {

    int NumberOfPages = 0;
    Context mContext;
    LayoutInflater mLayoutInflater;
    List<TasbeehModel> tasbeehModelList;

    public CustomViewPagerAdapter(Context context, List<TasbeehModel> tasbeehModelList) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tasbeehModelList = tasbeehModelList;
        NumberOfPages = tasbeehModelList.size();
    }


    @Override
    public int getCount() {
        return NumberOfPages;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = mLayoutInflater.inflate(R.layout.viewpager_tasbeeh, container, false);

        TextView tvEngTasbeeh = (TextView) itemView.findViewById(R.id.tv_translation_tasbeeh);
        TextView tvArabicTasbeeh = (TextView) itemView.findViewById(R.id.tv_arabic_tasbeeh);
        TextView tvRefTasbeeh = (TextView) itemView.findViewById(R.id.tv_ref_tasbeeh);
        TextView tvTranslitraitonTasbeeh = (TextView) itemView.findViewById(R.id.tv_eng_tasbeeh);

        tvTranslitraitonTasbeeh.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
        tvRefTasbeeh.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
        tvEngTasbeeh.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
        tvArabicTasbeeh.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceArabic);


        tvArabicTasbeeh.setText(ArabicUtilities.reshapeSentence(tasbeehModelList.get(position).getTasbeehArabic()));
        tvTranslitraitonTasbeeh.setText(tasbeehModelList.get(position).getTasbeehEng());
        tvEngTasbeeh.setText(tasbeehModelList.get(position).getTranslation());
        tvRefTasbeeh.setText(tasbeehModelList.get(position).getReference());

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

}

