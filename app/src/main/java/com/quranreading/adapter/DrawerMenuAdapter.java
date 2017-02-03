package com.quranreading.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quranreading.model.MenuDrawerModel;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.List;

import noman.CommunityGlobalClass;
import noman.community.prefrences.SavePreference;

public class DrawerMenuAdapter extends BaseAdapter {
    private Context mContext;
    List<MenuDrawerModel> detailList;
    int selected;

    public DrawerMenuAdapter(Context context, List<MenuDrawerModel> dataList) {
        this.mContext = context;
        this.detailList = dataList;
        SavePreference savePreference = new SavePreference();
        CommunityGlobalClass.mSignInRequests = savePreference.getDataFromSharedPreferences();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return detailList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return detailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView imgMenu, fb_img;
        LinearLayout rowWithoutIcons;
        TextView tvRow1, tvRow2, tvHeader1, tv_user_name, tv_user_email;
        RelativeLayout seprator, hearderView, rowWithIcons;
        LinearLayout linearUserContainer;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        boolean showViewHeading = detailList.get(position).isViewHeading();
        boolean showViewIcon = detailList.get(position).isViewIcon();
        boolean showViewLine = detailList.get(position).isViewLine();
        String rowText = detailList.get(position).getRowText();
        int imageNamePosition = detailList.get(position).getPosition();

        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            /*if(((GlobalClass) mContext.getApplicationContext()).deviceS3 == true)
				convertView = mInflater.inflate(R.layout.menu_drawer_s3, null);
			else
*/
            convertView = mInflater.inflate(R.layout.menu_drawer, null);

            holder = new ViewHolder();

            holder.tvRow1 = (TextView) convertView.findViewById(R.id.tv_row_1);
            holder.tvRow2 = (TextView) convertView.findViewById(R.id.tv_row_2);
            holder.imgMenu = (ImageView) convertView.findViewById(R.id.img_menu);
            holder.fb_img = (ImageView) convertView.findViewById(R.id.fb_img);
            holder.tvHeader1 = (TextView) convertView.findViewById(R.id.tv_header_1);
            holder.seprator = (RelativeLayout) convertView.findViewById(R.id.seprator_line);
            holder.hearderView = (RelativeLayout) convertView.findViewById(R.id.header_layout);
            holder.rowWithIcons = (RelativeLayout) convertView.findViewById(R.id.content_layout_with_icons);
            holder.rowWithoutIcons = (LinearLayout) convertView.findViewById(R.id.content_layout_without_icons);
            holder.tv_user_email = (TextView) convertView.findViewById(R.id.tv_user_email);
            holder.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
            holder.linearUserContainer = (LinearLayout) convertView.findViewById(R.id.linear_user_container);
            holder.tvRow1.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoR);
            holder.tvRow2.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoR);

            holder.tvHeader1.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoR);
            holder.tv_user_name.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoR);
            holder.tv_user_email.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoR);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        if (showViewHeading) {
            holder.seprator.setVisibility(View.GONE);
            holder.hearderView.setVisibility(View.VISIBLE);
            holder.rowWithIcons.setVisibility(View.GONE);
            holder.rowWithoutIcons.setVisibility(View.GONE);
            holder.fb_img.setVisibility(View.GONE);
        } else if (showViewIcon) {
            String uriImage = "drawable/" + "ic_menu_" + imageNamePosition;
            int imageResource = mContext.getResources().getIdentifier(uriImage, null, mContext.getPackageName());

            holder.tvRow1.setText(rowText);
            holder.imgMenu.setImageResource(imageResource);

            holder.seprator.setVisibility(View.GONE);
            holder.hearderView.setVisibility(View.GONE);
            holder.rowWithIcons.setVisibility(View.VISIBLE);
            holder.rowWithoutIcons.setVisibility(View.GONE);
            holder.fb_img.setVisibility(View.GONE);
        } else if (showViewLine) {
            holder.seprator.setVisibility(View.VISIBLE);
            holder.hearderView.setVisibility(View.GONE);
            holder.rowWithIcons.setVisibility(View.GONE);
            holder.rowWithoutIcons.setVisibility(View.GONE);
            holder.fb_img.setVisibility(View.GONE);
        } else if (rowText != null) {
            holder.tvRow2.setText(rowText);
            holder.seprator.setVisibility(View.GONE);
            holder.hearderView.setVisibility(View.GONE);
            holder.rowWithIcons.setVisibility(View.GONE);
            holder.rowWithoutIcons.setVisibility(View.VISIBLE);
            holder.fb_img.setVisibility(View.GONE);
        } else if (rowText == null) {
            holder.tvRow2.setVisibility(View.GONE);
            holder.seprator.setVisibility(View.GONE);
            holder.hearderView.setVisibility(View.GONE);
            holder.rowWithIcons.setVisibility(View.GONE);
            holder.rowWithoutIcons.setVisibility(View.GONE);
            holder.fb_img.setVisibility(View.VISIBLE);
        }

        if (CommunityGlobalClass.mSignInRequests == null) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen._115sdp));
            holder.hearderView.setLayoutParams(params);
            holder.linearUserContainer.setVisibility(View.GONE);
        } else {
            holder.linearUserContainer.setVisibility(View.VISIBLE);
            holder.tv_user_name.setText(CommunityGlobalClass.mSignInRequests.getName());
            holder.tv_user_email.setText(CommunityGlobalClass.mSignInRequests.getEmail());
        }


        return convertView;
    }
}
