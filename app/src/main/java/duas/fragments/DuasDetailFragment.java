package duas.fragments;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import duas.activities.DuaDetailsActivity;
import duas.db.DuaModel;
import duas.sharedprefs.DuasSharedPref;

public class DuasDetailFragment extends Fragment {

	DuaModel data;

	DuasSharedPref duasSharedPref;
	boolean isTranslation, isTransliteration;
	TextView tvTitle, tvArabic, tvTranslation, tvTransliteration;
	View vTranslation, vTransliteration;

	GlobalClass mGlobalClass;
	Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mContext = getContext();
		mGlobalClass = (GlobalClass) mContext.getApplicationContext();
		duasSharedPref = new DuasSharedPref(mContext);

		data = (DuaModel) getArguments().getSerializable(DuaDetailsActivity.EXTRA_DUA_DATA_OBJ);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View v = inflater.inflate(R.layout.dua_detail_fragment, container, false);

		tvTitle = (TextView) v.findViewById(R.id.tv_dua_title);
		tvArabic = (TextView) v.findViewById(R.id.tv_dua_arabic);
		tvTranslation = (TextView) v.findViewById(R.id.tv_dua_translation);
		tvTransliteration = (TextView) v.findViewById(R.id.tv_dua_transliteration);

		vTranslation = v.findViewById(R.id.view_translation);
		vTransliteration = v.findViewById(R.id.view_transliteration);

		tvTitle.setTypeface(mGlobalClass.faceRobotoR);
		tvArabic.setTypeface(mGlobalClass.faceArabic2);
		tvTranslation.setTypeface(mGlobalClass.faceRobotoL);
		tvTransliteration.setTypeface(mGlobalClass.faceRobotoL);

		tvTitle.setText(data.getDuaTitle().trim());
		tvArabic.setText(data.getDuaArabic().trim());
		tvTranslation.setText(data.getDuaEnglish().trim());
		tvTransliteration.setText(data.getDuaTransliteration().trim());

		return v;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isTranslation = duasSharedPref.isTranslation();
		isTransliteration = duasSharedPref.isTransliteration();

		if(!isTranslation)
		{
			tvTranslation.setVisibility(View.GONE);
			vTranslation.setVisibility(View.GONE);
		}
		else
		{
			tvTranslation.setVisibility(View.VISIBLE);
			vTranslation.setVisibility(View.VISIBLE);

		}

		if(!isTransliteration)
		{
			tvTransliteration.setVisibility(View.GONE);
			vTransliteration.setVisibility(View.GONE);
		}
		else
		{
			tvTransliteration.setVisibility(View.VISIBLE);
			vTransliteration.setVisibility(View.VISIBLE);
		}
	}

}
