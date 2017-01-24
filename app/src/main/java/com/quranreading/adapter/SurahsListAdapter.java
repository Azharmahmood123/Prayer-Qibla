package com.quranreading.adapter;

import java.util.ArrayList;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import quran.model.IndexListModel;

public class SurahsListAdapter extends BaseAdapter {
	private Context mContext;

	ArrayList<IndexListModel> listSurahs;
	int selected;
	GlobalClass mGlobalClass;

	public SurahsListAdapter(Context context, ArrayList<IndexListModel> dataList) {
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
		TextView tvVerses;
		TextView tvNameArabic;
		TextView tvSurahNo;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if(convertView == null)
		{
			convertView = mInflater.inflate(R.layout.row_surah_list, null);

			holder = new ViewHolder();

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
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvSurahNo.setText("" + (listSurahs.get(position).getItemPosition() + 1));
		holder.tvMakkiMadni.setText(listSurahs.get(position).getPlaceOfRevelation().trim());
		holder.tvNameArabic.setText(listSurahs.get(position).getArabicSurahName());
		holder.tvNameEnglish.setText(listSurahs.get(position).getEngSurahName());
		holder.tvVerses.setText(mContext.getResources().getString(R.string.verses) + ": " + listSurahs.get(position).getSurahSize() + ", ");

		return convertView;
	}
}
