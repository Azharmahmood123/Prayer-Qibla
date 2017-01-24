package places.adapters;

import java.util.ArrayList;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import places.models.PlacesModel;

public class AdapterHalalPlaces extends BaseAdapter {
	Context context;
	ArrayList<PlacesModel> mPlacesList;
	LayoutInflater layoutInflator;
	GlobalClass globalClass;

	public AdapterHalalPlaces(Context context, ArrayList<PlacesModel> mPlacesList) {
		this.context = context;
		this.mPlacesList = mPlacesList;
		layoutInflator = LayoutInflater.from(this.context);
		globalClass = ((GlobalClass) context.getApplicationContext());
	}

	@Override
	public int getCount() {
		return mPlacesList.size();
	}

	@Override
	public Object getItem(int position) {
		return mPlacesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		PlacesModel data = mPlacesList.get(position);

		ViewHolder viewHolder;
		if(convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = layoutInflator.inflate(R.layout.row_places_list, null);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_names);
			viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
			viewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
			viewHolder.tvKms = (TextView) convertView.findViewById(R.id.tv_kms);
			viewHolder.tvTitle.setTypeface(globalClass.faceRobotoL);
			viewHolder.tvAddress.setTypeface(globalClass.faceRobotoL);
			viewHolder.tvDistance.setTypeface(globalClass.faceRobotoL);
			viewHolder.tvKms.setTypeface(globalClass.faceRobotoL);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvTitle.setText(data.getName());
		viewHolder.tvAddress.setText(data.getAddress());
		viewHolder.tvDistance.setText(data.getDistance());
		return convertView;
	}

	public class ViewHolder {
		TextView tvTitle;
		TextView tvAddress;
		TextView tvDistance;
		TextView tvKms;
	}
}