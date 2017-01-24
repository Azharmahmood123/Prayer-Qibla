package names.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import names.fragment.NamesDetailFragment;

public class NamesPagerAdapter extends FragmentPagerAdapter {

	int size;
	public NamesPagerAdapter(FragmentManager supportFragmentManager,int s) {
		super(supportFragmentManager);
		size = s;
	}

	@Override
	public Fragment getItem(int position) {
		NamesDetailFragment myFragment = new NamesDetailFragment();
		Bundle args = new Bundle();
		args.putInt("namePosition", position);
		myFragment.setArguments(args);
		return myFragment;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return size;
	}

}
