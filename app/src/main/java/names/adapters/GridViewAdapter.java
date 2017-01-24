package names.adapters;

import java.util.ArrayList;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Integer> imgIds = new ArrayList<Integer>();
	private String[] names;
	private LayoutInflater layoutInflator;
	private GlobalClass globalClass;

	public GridViewAdapter(Context ctx, ArrayList<Integer> imgIds, String[] names) {
		this.context = ctx;
		this.names = names;
		this.imgIds = imgIds;
		layoutInflator = LayoutInflater.from(this.context);
		globalClass = ((GlobalClass) ctx.getApplicationContext());
	}

	@Override
	public int getCount() {
		return names.length;
	}

	@Override
	public Object getItem(int position) {
		return names[position];
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
			convertView = layoutInflator.inflate(R.layout.row_grid_names, null);

			viewHolder.catImag = (ImageView) convertView.findViewById(R.id.ivNames);
			viewHolder.catText = (TextView) convertView.findViewById(R.id.tvNames);
			viewHolder.catText.setTypeface(globalClass.faceRobotoR);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.catImag.setImageResource(imgIds.get(position));
		viewHolder.catText.setText(names[position]);

		return convertView;
	}

	public class ViewHolder {
		ImageView catImag;
		TextView catText;
	}
}
