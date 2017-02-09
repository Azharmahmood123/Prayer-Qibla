package duas.fragments;

import java.util.ArrayList;

import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import duas.activities.DuasListActivity;
import duas.adapters.GridViewDuaAdapter;
import duas.db.DBManagerDua;

public class DuasGridFragment extends Fragment {
	GridView gridview;
	ArrayList<Integer> categoryImages = new ArrayList<Integer>();
	ArrayList<String> searchResultList = new ArrayList<String>();
	ArrayList<String> searchItemCategories = new ArrayList<String>();
	DBManagerDua dbManager;
	GlobalClass mGlobal;
	Context mContext;

	ArrayList<Integer> namesImages = new ArrayList<Integer>();
	String categories[] = { "Morning/Evening", "Restroom", "Prayer", "Eat/Drink", "Dressing", "Travelling", "Family", "Home", "Blessings", "Protection", "Forgiveness", "Fasting", "Hajj & Umrah", "Funeral/Grave", "40 Rabbanas", "Animal", "Rain", "Random" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mContext = getContext();
		AnalyticSingaltonClass.getInstance(mContext).sendScreenAnalytics("Duas Categories 4.0");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View gridView = inflater.inflate(R.layout.dua_grid_layout, container, false);

		gridview = (GridView) gridView.findViewById(R.id.gridViewDua);
		mGlobal = ((GlobalClass) getActivity().getApplicationContext());

		loadGridItems();

		GridViewDuaAdapter gridAdapter = new GridViewDuaAdapter(mContext, categoryImages, categories);
		gridview.setAdapter(gridAdapter);

		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				Intent intent = new Intent(getActivity(), DuasListActivity.class);
				intent.putExtra(DuasListActivity.EXTRA_DUA_CATEGORY, categories[position]);
				startActivity(intent);
			}
		});
		return gridView;
	}

	private void loadGridItems() {

		categoryImages.add(R.drawable.g_item14);
		categoryImages.add(R.drawable.g_item21);
		categoryImages.add(R.drawable.g_item16);

		categoryImages.add(R.drawable.g_item6);
		categoryImages.add(R.drawable.g_item5);
		categoryImages.add(R.drawable.g_item22);

		categoryImages.add(R.drawable.g_item7);
		categoryImages.add(R.drawable.g_item11);
		categoryImages.add(R.drawable.g_item2);

		categoryImages.add(R.drawable.g_item17);
		categoryImages.add(R.drawable.g_item9);
		categoryImages.add(R.drawable.g_item8);

		categoryImages.add(R.drawable.g_item24);
		categoryImages.add(R.drawable.g_item10);
		categoryImages.add(R.drawable.g_item18);

		categoryImages.add(R.drawable.g_item1);
		categoryImages.add(R.drawable.g_item19);
		categoryImages.add(R.drawable.g_item20);

	}

	public void poulateSearchList(String wordWidSpace, String wordWidoutSpace) {
		searchResultList.clear();
		dbManager = new DBManagerDua(mContext);
		dbManager.open();
		Cursor c = dbManager.getSearchedItems(wordWidSpace, wordWidoutSpace);
		if(c.moveToFirst())
		{
			do
			{
				searchItemCategories.add(c.getString(c.getColumnIndex("category")));
				searchResultList.add(c.getString(c.getColumnIndex("dua_title")));
			}
			while (c.moveToNext());
		}
		dbManager.close();
	}
}