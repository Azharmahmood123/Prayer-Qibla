package names.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;

/**
 * Created by cyber on 1/2/2017.
 */

public class NamesListAdapter extends BaseAdapter {
    private Context mContext;

    ArrayList<NamesModel> listSurahs;
    int highlightPosition = 0;

    GlobalClass mGlobalClass;

    public NamesListAdapter(Context context, ArrayList<NamesModel> dataList) {
        this.mContext = context;
        this.listSurahs = dataList;
        mGlobalClass = (GlobalClass) context.getApplicationContext();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listSurahs.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listSurahs.get(position);
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
        TextView tvNameArabic;
        TextView tvSurahNo;
        RelativeLayout ayahRow;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_list_name, null);

            holder = new ViewHolder();

            holder.tvSurahNo = (TextView) convertView.findViewById(R.id.tv_surah_no);
            holder.tvMakkiMadni = (TextView) convertView.findViewById(R.id.maki_madni);
            holder.tvNameArabic = (TextView) convertView.findViewById(R.id.arabic_name);
            holder.tvNameEnglish = (TextView) convertView.findViewById(R.id.tvEnglishName);
            holder.ayahRow = (RelativeLayout)  convertView.findViewById(R.id.index_row);

            holder.tvSurahNo.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
            holder.tvMakkiMadni.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
            holder.tvNameArabic.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
            holder.tvNameEnglish.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvSurahNo.setText("" + (position + 1));
        holder.tvMakkiMadni.setText(listSurahs.get(position).getMeaning().trim());
        holder.tvNameArabic.setText(listSurahs.get(position).getArabic());
        holder.tvNameEnglish.setText(listSurahs.get(position).getEng());


        holder.ayahRow.setBackgroundResource(R.drawable.bg_row_resource);
        if(position == highlightPosition)
        {
            holder.ayahRow.setBackgroundResource(R.drawable.bg_row_hover);
        }

        return convertView;
    }

    public void hilightListItem(int position) {
        highlightPosition = position;
        notifyDataSetChanged();
    }

    public void removeHighlight() {
        highlightPosition = 0;
        notifyDataSetChanged();
    }
}