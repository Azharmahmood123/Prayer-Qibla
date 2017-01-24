package com.quranreading.fragments;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import duas.activities.DuasGridActivity;

public class MoreAppsGridFragment extends Fragment {

	LinearLayout layoutMosque, layoutHalal, layoutDuas/* ,layout99Names */;
	int type = 0;
	private boolean inProcess;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.fragment_more, container, false);

		TextView tvMosque, tvHalal, tvDuas, tvMosqueSub, tvHalalSub, tvDuasSub;

		tvMosque = (TextView) view.findViewById(R.id.tvMosqueFinder);
		tvHalal = (TextView) view.findViewById(R.id.tvHalalFinder);
		tvDuas = (TextView) view.findViewById(R.id.tvDuas);
		tvMosqueSub = (TextView) view.findViewById(R.id.tvMosqueFinderSub);
		tvHalalSub = (TextView) view.findViewById(R.id.tvHalalFinderSub);
		tvDuasSub = (TextView) view.findViewById(R.id.tvDuasSub);

		tvMosque.setTypeface(((GlobalClass) getContext().getApplicationContext()).faceRobotoL);
		tvHalal.setTypeface(((GlobalClass) getContext().getApplicationContext()).faceRobotoL);
		tvDuas.setTypeface(((GlobalClass) getContext().getApplicationContext()).faceRobotoL);
		tvMosqueSub.setTypeface(((GlobalClass) getContext().getApplicationContext()).faceRobotoL);
		tvHalalSub.setTypeface(((GlobalClass) getContext().getApplicationContext()).faceRobotoL);
		tvDuasSub.setTypeface(((GlobalClass) getContext().getApplicationContext()).faceRobotoL);

		layoutMosque = (LinearLayout) view.findViewById(R.id.more_mosque_index);
		layoutHalal = (LinearLayout) view.findViewById(R.id.more_halal_index);
		layoutDuas = (LinearLayout) view.findViewById(R.id.more_duas_index);
		// layout99Names = (LinearLayout) view.findViewById(R.id.more_names99_index);

		// layoutMosque.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		//
		// type = 0;
		// if(isNetworkConnected())
		// {
		// LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		// boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		//
		// if(isGPSEnabled || isNetworkEnabled)
		// {
		// Intent intent = new Intent(getActivity(), PlacesListActivity.class);
		// intent.putExtra(PlacesListActivity.EXTRA_PLACE_TYPE, type);
		// startActivity(intent);
		// }
		// else
		// {
		// LocationPref mLocationPref = new LocationPref(getActivity());
		//
		// if(mLocationPref.isMosquePlacesSaved())
		// {
		// Intent intent = new Intent(getActivity(), PlacesListActivity.class);
		// intent.putExtra(PlacesListActivity.EXTRA_PLACE_TYPE, type);
		// startActivity(intent);
		// }
		// else
		// {
		// providerAlertMessage();
		// }
		// }
		// }
		// else
		// {
		// Toast.makeText(getActivity(), R.string.toast_network_error, 0).show();
		// }
		// }
		// });
		//
		// layoutHalal.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// type = 1;
		// if(isNetworkConnected())
		// {
		// LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		// boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		//
		// if(isGPSEnabled || isNetworkEnabled)
		// {
		// Intent intent = new Intent(getActivity(), PlacesListActivity.class);
		// intent.putExtra(PlacesListActivity.EXTRA_PLACE_TYPE, type);
		// startActivity(intent);
		// }
		// else
		// {
		// LocationPref mLocationPref = new LocationPref(getActivity());
		// if(mLocationPref.isHalalPlacesSaved())
		// {
		// Intent intent = new Intent(getActivity(), PlacesListActivity.class);
		// intent.putExtra(PlacesListActivity.EXTRA_PLACE_TYPE, type);
		// startActivity(intent);
		// }
		// else
		// {
		// providerAlertMessage();
		// }
		// }
		// }
		// else
		// {
		// Toast.makeText(getActivity(), R.string.toast_network_error, 0).show();
		// }
		// }
		// });

		layoutDuas.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(!inProcess)
				{
					inProcess = true;
					startActivity(new Intent(getActivity(), DuasGridActivity.class));
				}

			}
		});

		// layout99Names.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// startActivity(new Intent(getActivity(), NamesGridActivity.class));
		//
		// }
		// });

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		inProcess = false;
	}

	private boolean isNetworkConnected() {
		ConnectivityManager mgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = mgr.getActiveNetworkInfo();

		return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
	}

	private void providerAlertMessage() {

		AlertDialog alertProvider = null;

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getActivity().getResources().getString(R.string.unable_to_find_location));
		builder.setMessage(getActivity().getResources().getString(R.string.enable_provider));
		builder.setCancelable(false);

		builder.setPositiveButton(getActivity().getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				getActivity().startActivity(settingsIntent);
			}
		});

		builder.setNegativeButton(getActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});

		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});

		builder.setOnKeyListener(new Dialog.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode == KeyEvent.KEYCODE_BACK)
				{
					return true;
				}
				return false;
			}
		});

		alertProvider = builder.create();
		alertProvider.show();
	}
}
