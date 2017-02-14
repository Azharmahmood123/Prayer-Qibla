package noman.community.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.logger.BuildConfig;
import com.quranreading.qibladirection.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import noman.CommunityGlobalClass;
import noman.community.utility.DebugInfo;
import noman.community.holder.PrayerHolder;
import noman.community.model.DeletePrayerRequest;
import noman.community.model.MoveToTopRequest;
import noman.community.model.Prayer;
import noman.community.model.PrayingResponse;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Administrator on 11/18/2016.
 */

public class MinePrayerAdapter extends RecyclerView.Adapter<PrayerHolder> {

    private List<Prayer> prayerList;
    Activity mActivity;

    int inappropirateCounter = 0;
    private Prayer mPrayerModel;
    String prayerCounter = "";
    TextView txtCounter;
    int pos = 0;

    public MinePrayerAdapter(List<Prayer> prayerList, Activity mActivity) {
        this.prayerList = prayerList;
        this.mActivity = mActivity;
    }

    @Override
    public PrayerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_prayer_list, parent, false);

        return new PrayerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PrayerHolder holder, final int position) {
        final Prayer mPrayer = prayerList.get(position);
        //Remove disable status prayer

        holder.txt_prayer.setText(Html.fromHtml(mPrayer.getContent()));
        holder.txt_counter.setText(mPrayer.getPrayedCounter() + " users have prayed for this");
        String time = mPrayer.getDatePosted().replace("UTC", "");
        //Create issue while converted thats why split it
        String am_pm = " " + time.replaceAll("[^A-Za-z]+", "");
        if (mPrayer.getLocation_status().equals("1")) {
            holder.txt_userInfo.setText(mPrayer.getName() + "\n" + CommunityGlobalClass.getInstance().convertDates(mActivity, time) + am_pm);
        } else {
            holder.txt_userInfo.setText(mPrayer.getName() + " , " + mPrayer.getLocation() + "\n" + CommunityGlobalClass.getInstance().convertDates(mActivity, time) + am_pm);
        }

        holder.txt_prayer_hit.setVisibility(View.GONE);

        holder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // holder.imgMenu.setImageResource(R.drawable.side_arrow_open);
                //Remove disable status prayer
                int status = 0;
                if (mPrayer.getStatus().equals("0")) {
                    status = 1;
                }

                showPopupMenu(holder.imgMenu, status);
                // imgMenuList.add(holder.imgMenu);
                inappropirateCounter = Integer.parseInt(mPrayer.getInappropriate_counter());
                mPrayerModel = mPrayer;
                prayerCounter = holder.txt_counter.getText().toString().replaceAll("\\D+", "");
                txtCounter = holder.txt_counter;
                pos = position;
            }
        });
        if (mPrayer.getStatus().equals("0")) {
            holder.txt_disablePrayer.setVisibility(View.VISIBLE);
            holder.txt_prayer.setTextColor(mActivity.getResources().getColor(R.color.color_gray));
        } else {
            holder.txt_disablePrayer.setVisibility(View.GONE);
            holder.txt_prayer.setTextColor(mActivity.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return prayerList.size();
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, int status) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mActivity, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_mine_list, popup.getMenu());
        if (status == 1) {
            Menu popupMenu = popup.getMenu();
            popupMenu.removeItem(R.id.action_move_top);
            popupMenu.removeItem(R.id.action_share);
            //popupMenu.findItem(R.id.action_move_top).setEnabled(false);

        }
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_delete:
                    CommunityGlobalClass.getInstance().sendAnalyticEvent("Community Prayers 4.0","Delete Prayer");
                    callWebDialogApi("Delete Prayer", "Do you really want to delete this prayer request?");
                    return true;
                case R.id.action_move_top:
                    CommunityGlobalClass.getInstance().sendAnalyticEvent("Community Prayers 4.0","Move To Top Prayer");
                    MoveToTopRequest mPrayingRequest = new MoveToTopRequest();
                    mPrayingRequest.setPrayer_id(mPrayerModel.getPrayerId());
                    mPrayingRequest.setUserId(mPrayerModel.getUserId());
                    mPrayingRequest.setDevice_date_time(CommunityGlobalClass.getInstance().getCurrentDate());
                    callMoveTopApi(mPrayingRequest);
                    return true;
                case R.id.action_share:
                    CommunityGlobalClass.getInstance().sendAnalyticEvent("Community Prayers 4.0","Share Prayer");
                    shareMessage("Please Pray", "\"" + mPrayerModel.getContent() + "\"\nJoin Muslim Community:\nhttps://play.google.com/store/apps/details?id=com.quranreading.qibladirection");
                    return true;

                default:
            }
            return false;
        }
    }

    //Call web
    public void callDeleteApi(final DeletePrayerRequest mPostPrayerRequest) {
        CommunityGlobalClass.getInstance().showLoading(mActivity);
        Call<PrayingResponse> call = CommunityGlobalClass.getRestApi().deletePray(mPostPrayerRequest);
        call.enqueue(new retrofit.Callback<PrayingResponse>() {


            @Override
            public void onResponse(Response<PrayingResponse> response, Retrofit retrofit) {
                CommunityGlobalClass.getInstance().cancelDialog();
                if (response.body().getState() == true) {
                    prayerList.remove(pos);
                    //Also remove current row from the mineList of global
                    CommunityGlobalClass.minePrayerModel = prayerList;
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                CommunityGlobalClass.getInstance().cancelDialog();
                if (BuildConfig.DEBUG)
                    DebugInfo.loggerException("DeleteAPi-Failure" + t.getMessage());
                CommunityGlobalClass.getInstance().showServerFailureDialog(mActivity);
            }
        });


    }

    public void callMoveTopApi(final MoveToTopRequest mPostPrayerRequest) {
        CommunityGlobalClass.getInstance().showLoading(mActivity);
        Call<PrayingResponse> call = CommunityGlobalClass.getRestApi().moveToTop(mPostPrayerRequest);
        call.enqueue(new retrofit.Callback<PrayingResponse>() {

            @Override
            public void onResponse(Response<PrayingResponse> response, Retrofit retrofit) {
                CommunityGlobalClass.getInstance().cancelDialog();
                if (response.body().getState() == true) {
                    CommunityGlobalClass.getInstance().showToast("\"Prayer is moved to top\"\nSet filter \"Most Recent\" to view your prayer.");

                   //Adding current date and time  in move to top request
                    mPrayerModel.setDatePosted(  CommunityGlobalClass.getInstance().getCurrentDate());
                    prayerList.remove(pos);
                    prayerList.add(mPrayerModel);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                CommunityGlobalClass.getInstance().cancelDialog();
                if (BuildConfig.DEBUG)
                    DebugInfo.loggerException("DeleteAPi-Failure" + t.getMessage());
                CommunityGlobalClass.getInstance().showServerFailureDialog(mActivity);
            }
        });


    }


    private void shareMessage(String subject, String body) {

        if (saveShareImage()) {
            shareAppWithAppIcon(subject, body);
        } else {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            shareIntent.putExtra(Intent.EXTRA_TEXT, body);
            mActivity.startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
    }

    private void shareAppWithAppIcon(String subject, String body) {
        String fileName = "ic_launcher.png";
        String completePath = Environment.getExternalStorageDirectory() + "/" + fileName;

        File file = new File(completePath);
        Uri imageUri = Uri.fromFile(file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        // shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        mActivity.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private boolean saveShareImage() {
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.ic_launcher), 300, 300, false);
        File sd = Environment.getExternalStorageDirectory();
        String fileName = "ic_launcher.png";
        File dest = new File(sd, fileName);
        try {
            FileOutputStream out;
            out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public void callWebDialogApi(String title, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                DeletePrayerRequest mPrayingRequest = new DeletePrayerRequest();
                mPrayingRequest.setPrayer_id(mPrayerModel.getPrayerId());
                mPrayingRequest.setUserId(mPrayerModel.getUserId());
                callDeleteApi(mPrayingRequest);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}