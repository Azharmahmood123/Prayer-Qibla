package noman.quran.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.List;

import noman.quran.model.JuzConstant;
import noman.searchquran.model.TopicModel;


/**
 * Created by Administrator on 2/22/2017.
 */

public class SearchListAdapter extends BaseAdapter {
    private Context mContext;

    List<TopicModel> mTopicList;
    int selected;
    GlobalClass mGlobalClass;

    public SearchListAdapter(Context context, List<TopicModel> dataList) {
        this.mContext = context;
        this.mTopicList = dataList;
        mGlobalClass = (GlobalClass) context.getApplicationContext();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mTopicList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mTopicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /* private view holder class */
    private class ViewHolder {
        TextView tvMakkiMadni;
        TextView tvNameEnglish;
        TextView tvVerses;
        TextView tvNameArabic;
        TextView tvSurahNo;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        SearchListAdapter.ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_surah_list, null);

            holder = new SearchListAdapter.ViewHolder();

            holder.tvSurahNo = (TextView) convertView.findViewById(R.id.tv_surah_no);
            holder.tvMakkiMadni = (TextView) convertView.findViewById(R.id.maki_madni);
            holder.tvNameArabic = (TextView) convertView.findViewById(R.id.arabic_name);
            holder.tvNameEnglish = (TextView) convertView.findViewById(R.id.tvEnglishName);
            holder.tvVerses = (TextView) convertView.findViewById(R.id.verses);

            holder.tvSurahNo.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
            holder.tvMakkiMadni.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
            holder.tvNameArabic.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
            holder.tvNameEnglish.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
            holder.tvVerses.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);

            convertView.setTag(holder);
        } else {
            holder = (SearchListAdapter.ViewHolder) convertView.getTag();
        }

            holder.tvSurahNo.setText("" + (position+1));
            holder.tvMakkiMadni.setVisibility(View.GONE);
            holder.tvNameArabic.setText(JuzConstant.arabicSurahName[mTopicList.get(position).getSurahNo() - 1]);
            holder.tvNameEnglish.setText(JuzConstant.engSurahName[mTopicList.get(position).getSurahNo() - 1]);
            holder.tvVerses.setText(mContext.getResources().getString(R.string.text_verse_no) + ": " + (mTopicList.get(position).getVersesNo()) + ", "+
            "Juz: "+(mTopicList.get(position).getParaNo()));



        return convertView;
    }


}
