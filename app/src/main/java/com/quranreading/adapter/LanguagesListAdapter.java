package com.quranreading.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

public class LanguagesListAdapter extends BaseAdapter {
	private Context mContext;
	String[] languageList;
	int selected;
	GlobalClass mGlobalClass;

	public LanguagesListAdapter(Context context, String[] dataList) {
		this.mContext = context;
		this.languageList = dataList;
		mGlobalClass = (GlobalClass) context.getApplicationContext();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return languageList.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return languageList[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/* private view holder class */
	private class ViewHolder {
		ImageView imgSelection;
		TextView tvLanguage;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if(convertView == null)
		{
			if(((GlobalClass) mContext.getApplicationContext()).deviceS3 == true)
				convertView = mInflater.inflate(R.layout.row_languages_s3, null);
			else
				convertView = mInflater.inflate(R.layout.row_languages, null);

			holder = new ViewHolder();

			holder.tvLanguage = (TextView) convertView.findViewById(R.id.tv_language);
			holder.imgSelection = (ImageView) convertView.findViewById(R.id.img_selection);

			holder.tvLanguage.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);

			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvLanguage.setText(languageList[position]);
		if(mGlobalClass.languagePref.getLanguage() == position)
		{
			holder.imgSelection.setImageResource(R.drawable.ic_radio_chk);
		}
		else
		{
			holder.imgSelection.setImageResource(R.drawable.ic_radio_uncheck);
		}

		return convertView;
	}
}
