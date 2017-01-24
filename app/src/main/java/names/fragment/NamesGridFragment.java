package names.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.quranreading.qibladirection.R;

import java.util.ArrayList;

import names.activities.NamesDetailActivity;
import names.activities.NamesListPlayingActivity;
import names.adapters.GridViewAdapter;

public class NamesGridFragment extends Fragment implements OnItemClickListener, OnClickListener {

	GridView gridViewNames;
	FloatingActionButton fabPlayNames;
	ArrayList<Integer> namesImages = new ArrayList<Integer>();
	GridViewAdapter mGridViewAdapter;
	public String[] names = new String[] { "Allah", "Ar-Rahman", "Ar-Rahim", "Al-Malik", "Al-Quddus", "As-Salam", "Al-Mu'min", "Al-Muhaymin", "Al-Aziz", "Al-Jabbar", "Al-Mutakabbir", // 10
			"Al-Khaliq", "Al-Bari", "Al-Musawwir", "Al-Ghaffar", "Al-Qahhar", "Al-Wahhab", "Ar-Razzaq", "Al-Fattah", "Al-Aleem", "Al-Qabid", // 20
			"Al-Basit", "Al-Khafid", "Ar-Rafi", "Al-Mu'iz", "Al-Muzil", "As-Sami", "Al-Basir", "Al-Hakam", "Al-Adl", "Al-Latif", "Al-Khabir", "Al-Halim", "Al-Azim", "Al-Ghafoor", "Ash-Shakur", "Al-Ali", "Al-Kabir", "Al-Hafiz", "Al-Muqit", "Al-Hasib", "Aj-Jalil", "Al-Karim", "Ar-Raqib", "Al-Mujib",
			"Al-Wasi", "Al-Hakim", "Al-Wadud", "Al-Majid", "Al-Ba'ith", "Ash-Shahid", // 50
			"Al-Haqq", "Al-Wakil", "Al-Qawee", "Al-Matin", "Al-Walee", "Al-Hamid", "Al-Muhsi", "Al-Mubdi", "Al-Mu'eed", "Al-Muhyee", "Al-Mumeet", "Al-Hayy", "Al-Qayyum", "Al-Wajid", "Al-Majid", "Al-Wahid", "As-Samad", "Al-Qadir", "Al-Muqtadir", "Al-Muqaddim", // 70 67 "Al-Ahad "
			"Al-Mu'akhkhir", "Al-Awwal", "Al-Akhir", "Az-Zahir", "Al-Batin", "Al-Wali", "Al-Muta'ali", "Al-Barr", "At-Tawwab", "Al-Muntaqim", "Al-Afuww", "Ar-Ra'uf", "Malik Al-Mulk", "Zul-l-Jalal wal-Ikram", "Al-Muqsit", "Aj-Jami", "Al-Ghanee", "Al-Mughnee", "Al-Mani", "Ad-Darr", // 90
			"An-Nafi", "An-Nur", "Al-Hadi", "Al-Badi", "Al-Baqi", "Al-Warith", "Ar-Rashid", "As-Sabur" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		loadGridImages();
		mGridViewAdapter = new GridViewAdapter(getActivity(), namesImages, names);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.fragment_names, null);
		gridViewNames = (GridView) view.findViewById(R.id.gridviewNames);
		fabPlayNames = (FloatingActionButton) view.findViewById(R.id.fabPlayNames);
		fabPlayNames.setOnClickListener(this);
		gridViewNames.setOnItemClickListener(this);
		gridViewNames.setAdapter(mGridViewAdapter);

		return view;
	}

	private void loadGridImages() {
		namesImages.clear();
		for (int i = 1; i < 101; i++)
		{
			String imageName = "grid_name_" + i;
			int imageId = getResources().getIdentifier(imageName, "drawable", getActivity().getPackageName());
			namesImages.add(imageId);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Intent intent = new Intent(getActivity(), NamesDetailActivity.class);
		intent.putExtra(NamesDetailActivity.EXTRA_NAMES_POSITION, position);
		getActivity().startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getActivity(), NamesListPlayingActivity.class);
		getActivity().startActivity(intent);
	}
}
