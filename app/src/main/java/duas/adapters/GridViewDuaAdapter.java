package duas.adapters;

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

public class GridViewDuaAdapter extends BaseAdapter {
	Context context;
	ArrayList<Integer> catImages = new ArrayList<Integer>();
	String[] categories;
	LayoutInflater layoutInflator;
	GlobalClass globalClass;

	public GridViewDuaAdapter(Context context1, ArrayList<Integer> catImages1, String[] categories1) {
		this.context = context1;
		this.categories = categories1;
		this.catImages = catImages1;
		layoutInflator = LayoutInflater.from(this.context);
		globalClass = ((GlobalClass) context1.getApplicationContext());
	}

	@Override
	public int getCount() {
		return categories.length;
	}

	@Override
	public Object getItem(int position) {
		return categories[position];
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
			convertView = layoutInflator.inflate(R.layout.grid_item, null);
			viewHolder.catImag = (ImageView) convertView.findViewById(R.id.imageView1);
			viewHolder.catText = (TextView) convertView.findViewById(R.id.textView1);
			viewHolder.catText.setTypeface(globalClass.faceRobotoL);
			convertView.setTag(viewHolder);

		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.catImag.setImageResource(catImages.get(position));
		viewHolder.catText.setText(categories[position]);
		return convertView;
	}

	public class ViewHolder {
		ImageView catImag;
		TextView catText;
	}
}
