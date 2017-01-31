package com.quranreading.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quranreading.model.GridItems;
import com.quranreading.qibladirection.CompassActivity;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.MainActivityNew;
import com.quranreading.qibladirection.R;
import com.quranreading.qibladirection.SettingsActivity;
import com.quranreading.qibladirection.TimingsActivity;
import com.quranreading.qibladirection.UpgradeActivity;
import com.quranreading.sharedPreference.LocationPref;

import duas.activities.DuasGridActivity;
import names.activities.NamesListPlayingActivity;
import noman.community.activity.ComunityActivity;
import noman.hijri.acitivity.CalenderActivity;
import noman.quran.QuranModuleActivity;
import places.activities.PlacesListActivity;
import quran.sharedpreference.SurahsSharedPref;

/**
 * Created by cyber on 11/30/2016.
 */

public class MenuGridFragment extends Fragment {

    public static final int MENU_TIMINGS = 0;
    public static final int MENU_QIBLA_MAP_DIRECION = 1;
    public static final int MENU_QURAN = 2;
    public static final int MENU_COMMUNITY = 3;

    public static final int MENU_NAMES = 4;
    public static final int MENU_HIJRI = 5;
    public static final int MENU_MOSQUES = 6;
 /*   public static final int MENU_REMOVE_ADS = 5;*/

    public static final int MENU_HALAL = 7;

    public static final int MENU_DUAS = 8;
    public static final int MENU_SETTINGS = 9;


    public static final String GRID_ITEMS = "grid_items";
    private GridView mGridView;
    private GridAdapter mGridAdapter;
    GridItems[] gridItems = {};
    Context mContext;
    int type = 0;

    LocationPref locationPref;
    String lat, lng;

    boolean inProcess = false;

    LocationManager locationManager;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;

    public void getInstance(GridItems[] gridItems, MenuGridFragment fragment) {

        Bundle bundle = new Bundle();
        bundle.putSerializable(GRID_ITEMS, gridItems);
        fragment.setArguments(bundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gridItems = (GridItems[]) getArguments().getSerializable(GRID_ITEMS);
        mContext = getContext();

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        locationPref = new LocationPref(mContext);
        lat = locationPref.getLatitudeCurrent();
        lng = locationPref.getLongitudeCurrent();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_menu_grid, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridView);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mGridAdapter = new GridAdapter(mContext, gridItems);
        if (mGridView != null) {
            mGridView.setAdapter(mGridAdapter);
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                onGridItemClick((GridView) parent, view, position, id);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        inProcess = false;
    }

    public void onGridItemClick(GridView g, View v, int position, long id) {
        final SurahsSharedPref mSurahsSharedPref = new SurahsSharedPref(getActivity());
        if (!inProcess) {
            inProcess = true;

            Intent intent;
            int pos = gridItems[position].id;

            switch (pos) {
                case MENU_TIMINGS:
                    if (!((GlobalClass) mContext.getApplicationContext()).isPurchase) {
                        mContext.sendBroadcast(new Intent(MainActivityNew.ACTION_INTERSTITIAL_ADS_SHOW));
                    }
                    intent = new Intent(mContext, TimingsActivity.class);
                    startActivity(intent);
                    break;


                case MENU_QIBLA_MAP_DIRECION:
                    if (isNetworkConnected()) {

                        if (lat.isEmpty()) {
                            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                            if (isGPSEnabled || isNetworkEnabled) {
                                inProcess = false;
                                showShortToast(getString(R.string.toast_location_error), 800, 0);
                            } else {
                                providerAlertMessage();
                            }

                        } else {
                            if (!((GlobalClass) mContext.getApplicationContext()).isPurchase) {
                                mContext.sendBroadcast(new Intent(MainActivityNew.ACTION_INTERSTITIAL_ADS_SHOW));
                            }
                            intent = new Intent(mContext, CompassActivity.class);
                            intent.putExtra(CompassActivity.EXTRA_IS_SHOW_MAP, true);
                            startActivity(intent);
                        }
                    } else {
                        inProcess = false;
                        showShortToast(getString(R.string.toast_network_error), 800, 0);
                    }
                    break;


                case MENU_QURAN:
                    //  intent = new Intent(mContext, SurahListActivity.class);
                    intent = new Intent(mContext, QuranModuleActivity.class);
                    startActivity(intent);
                    break;

                case MENU_HIJRI:
                    //////////////

                    mGridAdapter.notifyDataSetChanged();
                    if (!((GlobalClass) mContext.getApplicationContext()).isPurchase) {
                        mContext.sendBroadcast(new Intent(MainActivityNew.ACTION_INTERSTITIAL_ADS_SHOW));
                    }

                    if (mSurahsSharedPref.getIsFirstTimeHijriOpen()) {
                        mSurahsSharedPref.setIsFirstTimeHijriOpen(false);
                    }
                    intent = new Intent(mContext, CalenderActivity.class);
                    startActivity(intent);
                    ////////////
                    /////////////

                    break;

                case MENU_COMMUNITY:
                    //////////////

                    if (isNetworkConnected()) {

                        if (mSurahsSharedPref.getIsFirstTimeCommunityOpen()) {
                            mSurahsSharedPref.setIsFirstTimeCommunityOpen(false);
                        }
                        mGridAdapter.notifyDataSetChanged();
                        intent = new Intent(mContext, ComunityActivity.class);
                        startActivity(intent);

                    } else {
                        inProcess = false;
                        showShortToast(getString(R.string.toast_network_error), 800, 0);
                    }

                    ////////////
                    /////////////

                    break;

                case MENU_NAMES:
                    mGridAdapter.notifyDataSetChanged();
                    if (mSurahsSharedPref.getIsFirstTimeNamesOpen()) {
                        mSurahsSharedPref.setIsFirstTimeNamesOpen(false);
                    }

                    if (!((GlobalClass) mContext.getApplicationContext()).isPurchase) {
                        mContext.sendBroadcast(new Intent(MainActivityNew.ACTION_INTERSTITIAL_ADS_SHOW));
                    }

                    intent = new Intent(mContext, NamesListPlayingActivity.class);
                    startActivity(intent);
                    break;

                case MENU_DUAS:
                    intent = new Intent(mContext, DuasGridActivity.class);
                    startActivity(intent);
                    break;

                case MENU_HALAL:

                    onHalalPlacesClick();
                    break;

                case MENU_MOSQUES:
                    onMosquesClick();
                    break;

                case MENU_SETTINGS:
                    intent = new Intent(mContext, SettingsActivity.class);
                    startActivity(intent);
                    break;

             /*   case MENU_REMOVE_ADS:
                    intent = new Intent(mContext, UpgradeActivity.class);
                    startActivity(intent);
                    break;*/

//            case MENU_ABOUT_US:
//                intent = new Intent(mContext, AboutInstructionActivity.class);
//                intent.putExtra(MainActivityNew.ACTIVITY_SELECTION, MainActivityNew.ACTIVITY_ABOUT);
//                startActivity(intent);
//                break;

                default:
                    break;
            }
        }
    }

    class GridAdapter extends BaseAdapter {

        Context context;
        int images[] = {
                R.drawable.grid_bg_timings, R.drawable.grid_bg_direction, R.drawable.grid_bg_quran,
                R.drawable.grid_bg_old_community, R.drawable.grid_bg_names, R.drawable.grid_bg_calendar,
                R.drawable.grid_bg_mosque, R.drawable.grid_bg_halal, R.drawable.grid_bg_duas,
                R.drawable.grid_bg_settings};


        public class ViewHolder {
            public ImageView imageView;
            public TextView textTitle;
        }

        private GridItems[] items;
        private LayoutInflater mInflater;

        public GridAdapter(Context context, GridItems[] locations) {

            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.context = context;
            items = locations;

        }

        public GridItems[] getItems() {
            return items;
        }

        public void setItems(GridItems[] items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            if (items != null) {
                return items.length;
            }
            return 0;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            if (items != null && position >= 0 && position < getCount()) {
                return items[position];
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            if (items != null && position >= 0 && position < getCount()) {
                return items[position].id;
            }
            return 0;
        }

        public void setItemsList(GridItems[] locations) {
            this.items = locations;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            ViewHolder viewHolder;

            if (view == null) {

                view = mInflater.inflate(R.layout.row_menu_grid, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) view
                        .findViewById(R.id.grid_item_image);
                viewHolder.textTitle = (TextView) view
                        .findViewById(R.id.grid_item_label);
                viewHolder.textTitle.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoR);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            GridItems gridItems = items[position];
            setCatImage(gridItems.id, viewHolder, gridItems.title);
            return view;
        }

        private void setCatImage(int pos, ViewHolder viewHolder, String catTitle) {

            final SurahsSharedPref mSurahsSharedPref = new SurahsSharedPref(getActivity());

            //for check which device is runing

            if (!MenuMainFragment.isSmallDevice) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) context.getResources().getDimension(R.dimen._50sdp), (int) context.getResources().getDimension(R.dimen._50sdp));
                viewHolder.imageView.setLayoutParams(layoutParams);
                viewHolder.textTitle.setTextSize(12);
            }
            viewHolder.imageView.setImageResource(images[pos]);
            if (pos == 3) {//Compuntiy
                if (mSurahsSharedPref.getIsFirstTimeCommunityOpen()) {
                    viewHolder.imageView.setImageResource(R.drawable.grid_bg_community);
                }
            }
            if (pos == 4) {//Names
                if (mSurahsSharedPref.getIsFirstTimeNamesOpen()) {
                    viewHolder.imageView.setImageResource(R.drawable.grid_names_new);
                }

            }
            if (pos == 5) {//Hijri
                if (mSurahsSharedPref.getIsFirstTimeHijriOpen()) {
                    viewHolder.imageView.setImageResource(R.drawable.grid_hijri_new);
                }

            }


            //    viewHolder.imageView.setImageResource(images[pos]);
            viewHolder.textTitle.setText(catTitle);
        }
    }

    /////////////////////////
    ///////////////////////////
    //////////////////////////////

    private void onHalalPlacesClick() {
        type = 1;
        if (isNetworkConnected()) {

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled) {

                Intent intent = new Intent(mContext, PlacesListActivity.class);
                intent.putExtra(PlacesListActivity.EXTRA_PLACE_TYPE, type);
                startActivity(intent);
                if (!((GlobalClass) mContext.getApplicationContext()).isPurchase) {
                    mContext.sendBroadcast(new Intent(MainActivityNew.ACTION_INTERSTITIAL_ADS_SHOW));
                }

            } else {
                LocationPref mLocationPref = new LocationPref(mContext);
                if (mLocationPref.isHalalPlacesSaved()) {

                    Intent intent = new Intent(mContext, PlacesListActivity.class);
                    intent.putExtra(PlacesListActivity.EXTRA_PLACE_TYPE, type);
                    startActivity(intent);
                    if (!((GlobalClass) mContext.getApplicationContext()).isPurchase) {
                        mContext.sendBroadcast(new Intent(MainActivityNew.ACTION_INTERSTITIAL_ADS_SHOW));
                    }

                } else {
                    providerAlertMessage();
                }
            }
        } else {
            inProcess = false;
            showShortToast(getResources().getString(R.string.toast_network_error), 500, Gravity.BOTTOM);
        }
    }

    private void onMosquesClick() {

        type = 0;
        if (isNetworkConnected()) {

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled) {
                Intent intent = new Intent(mContext, PlacesListActivity.class);
                intent.putExtra(PlacesListActivity.EXTRA_PLACE_TYPE, type);
                startActivity(intent);
                if (!((GlobalClass) mContext.getApplicationContext()).isPurchase) {
                    mContext.sendBroadcast(new Intent(MainActivityNew.ACTION_INTERSTITIAL_ADS_SHOW));
                }

            } else {
                LocationPref mLocationPref = new LocationPref(mContext);

                if (mLocationPref.isMosquePlacesSaved()) {
                    Intent intent = new Intent(mContext, PlacesListActivity.class);
                    intent.putExtra(PlacesListActivity.EXTRA_PLACE_TYPE, type);
                    startActivity(intent);
                    if (!((GlobalClass) mContext.getApplicationContext()).isPurchase) {
                        mContext.sendBroadcast(new Intent(MainActivityNew.ACTION_INTERSTITIAL_ADS_SHOW));
                    }

                } else {
                    providerAlertMessage();
                }
            }
        } else {
            inProcess = false;
            showShortToast(getResources().getString(R.string.toast_network_error), 500, Gravity.BOTTOM);
        }
    }

    private void providerAlertMessage() {

        AlertDialog alertProvider = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getResources().getString(R.string.unable_to_find_location));
        builder.setMessage(getResources().getString(R.string.enable_provider));
        builder.setCancelable(false);

        builder.setPositiveButton(getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(settingsIntent);
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                inProcess = false;
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                inProcess = false;
            }
        });

        builder.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });

        alertProvider = builder.create();
        alertProvider.show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager mgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
    }

    private void showShortToast(String message, int milliesTime, int gravity) {

        if (getString(R.string.device).equals("large")) {
            final Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            final Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
            toast.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, milliesTime);
        }
    }
}
