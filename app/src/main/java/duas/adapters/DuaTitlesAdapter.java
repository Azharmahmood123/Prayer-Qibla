package duas.adapters;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DuaTitlesAdapter extends BaseAdapter {
	Context context;
	String[] duaTitles;
	LayoutInflater layoutInflator;
	GlobalClass globalClass;

	public DuaTitlesAdapter(Context context, String[] duaTitles) {
		this.context = context;
		this.duaTitles = duaTitles;
		layoutInflator = LayoutInflater.from(this.context);
		globalClass = ((GlobalClass) context.getApplicationContext());
	}

	@Override
	public int getCount() {
		return duaTitles.length;
	}

	@Override
	public Object getItem(int position) {
		return duaTitles[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		if(convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = layoutInflator.inflate(R.layout.dua_row_title, null);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvDuaTitle);
			viewHolder.tvTitle.setTypeface(globalClass.faceRobotoL);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvTitle.setText(duaTitles[position]);
		return convertView;
	}

	public class ViewHolder {
		TextView tvTitle;
	}
}