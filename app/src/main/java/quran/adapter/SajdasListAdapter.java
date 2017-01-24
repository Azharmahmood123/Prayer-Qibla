package quran.adapter;

import java.util.List;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import quran.model.BookmarksListModel;

public class SajdasListAdapter extends BaseAdapter {
	private Context mContext;
	private List<BookmarksListModel> dataArray;

	public SajdasListAdapter(Context context, List<BookmarksListModel> dataArray) {
		this.mContext = context;
		this.dataArray = dataArray;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataArray.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return dataArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/* private view holder class */
	private class ViewHolder {
		TextView tvName;
		TextView tvIndexNo;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		String surahName = dataArray.get(position).getsurahName();
		int ayahNo = dataArray.get(position).getAyahNo();

		String name;
		if(surahName.equals("Al-Fatihah") || surahName.equals("At-Taubah"))
		{
			name = surahName + ", " + String.valueOf(ayahNo + 1);
		}
		else
		{
			name = surahName + ", " + String.valueOf(ayahNo);
		}

		ViewHolder holder = null;
		LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if(convertView == null)
		{
			convertView = mInflater.inflate(R.layout.row_bookmarks, null);
			holder = new ViewHolder();

			holder.tvName = (TextView) convertView.findViewById(R.id.tv_index);
			holder.tvIndexNo = (TextView) convertView.findViewById(R.id.tv_index_no);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();

		holder.tvIndexNo.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
		holder.tvName.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);

		holder.tvIndexNo.setText(String.valueOf(position + 1));
		holder.tvName.setText(name);
		// if(BookmarksActivity.changeItemColor == 1)
		// {
		// holder.tvName.setBackgroundColor(Color.parseColor("#EFEFEF"));
		// }
		return convertView;
	}
}
