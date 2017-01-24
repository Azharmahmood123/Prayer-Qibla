package quran.adapter;

import java.util.List;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import quran.model.BookmarksListModel;

public class BookmarksListAdapter extends BaseAdapter {
	private Context mContext;
	private List<BookmarksListModel> dataArray;
	private SparseBooleanArray mSelectedItemsIds;

	public BookmarksListAdapter(Context context, List<BookmarksListModel> dataArray) {
		this.mContext = context;
		this.dataArray = dataArray;
		mSelectedItemsIds = new SparseBooleanArray();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataArray.size();
	}

	@Override
	public BookmarksListModel getItem(int position) {
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

	public void remove(BookmarksListModel object) {
		dataArray.remove(object);
		notifyDataSetChanged();
	}

	public List<BookmarksListModel> getWorldPopulation() {
		return dataArray;
	}

	public void toggleSelection(int position) {
		selectView(position, !mSelectedItemsIds.get(position));
	}

	public void removeSelection() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	public void selectView(int position, boolean value) {
		if(value)
			mSelectedItemsIds.put(position, value);
		else
			mSelectedItemsIds.delete(position);
		notifyDataSetChanged();
	}

	public int getSelectedCount() {
		return mSelectedItemsIds.size();
	}

	public SparseBooleanArray getSelectedIds() {
		return mSelectedItemsIds;
	}

}
