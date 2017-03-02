package noman.community.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.BuildConfig;
import com.quranreading.qibladirection.R;

import java.util.List;

import noman.CommunityGlobalClass;
import noman.community.activity.ComunityActivity;
import noman.community.prefrences.SavePreference;
import noman.community.utility.DebugInfo;
import noman.community.holder.PrayerHolder;
import noman.community.model.Prayer;
import noman.community.model.PrayingRequest;
import noman.community.model.PrayingResponse;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Administrator on 11/18/2016.
 */

public class PrayerAdapter extends RecyclerView.Adapter<PrayerHolder> {

    private List<Prayer> prayerList;
    Activity mActivity;
    private List<ImageView> imgMenuList;
    int inappropirateCounter = 0;
    private Prayer mPrayerModel;
    String prayerCounter = "";
    TextView txtCounter;
    int pos = 0;
    private long lastClick = 0;
    //to acces this counter for post new prayer
    int clickingCounter = 0;

    public PrayerAdapter(List<Prayer> prayerList, Activity mActivity) {
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
        holder.txt_prayer.setText(Html.fromHtml(mPrayer.getContent()));
        holder.txt_counter.setText(mPrayer.getPrayedCounter() + " users have prayed for this");
        String time=mPrayer.getDatePosted().replace("UTC", "");
        //Create issue while converted thats why split it
        String am_pm = " " + time.replaceAll("[^A-Za-z]+", "");
        if (mPrayer.getLocation_status().equals("1")) {
            holder.txt_userInfo.setText(mPrayer.getName() + "\n" + CommunityGlobalClass.getInstance().convertDates(mActivity, time) +am_pm);

        } else {
            holder.txt_userInfo.setText(mPrayer.getName() + " , " + mPrayer.getLocation() + "\n" + CommunityGlobalClass.getInstance().convertDates(mActivity,time)+ am_pm);

        }

        holder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // holder.imgMenu.setImageResource(R.drawable.side_arrow_open);
                showPopupMenu(holder.imgMenu );
                // imgMenuList.add(holder.imgMenu);
                inappropirateCounter = Integer.parseInt(mPrayer.getInappropriate_counter());
                mPrayerModel = mPrayer;
                prayerCounter = holder.txt_counter.getText().toString().replaceAll("\\D+", "");
                txtCounter = holder.txt_counter;

                pos = position;


            }
        });

        holder.txt_prayer_hit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClick < 2000) {
                    return;
                } else {
                    clickingCounter = clickingCounter + 1;
                    //Save counter in Preferences
                    CommunityGlobalClass.prayerCounter=clickingCounter;


                    inappropirateCounter = Integer.parseInt(mPrayer.getInappropriate_counter());
                    mPrayerModel = mPrayer;
                    prayerCounter = holder.txt_counter.getText().toString().replaceAll("\\D+", "");
                    txtCounter = holder.txt_counter;
                    callWebDialogApi("Prayer Request","Have you prayed for the request by " + mPrayer.getName()+" ?", false);

                    CommunityGlobalClass.getInstance().sendAnalyticEvent("Community Prayers 4.0","Pray for this");
                }
                lastClick = SystemClock.elapsedRealtime();


            }
        });




    }

    @Override
    public int getItemCount() {
        return prayerList.size();
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mActivity, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_prayer_list, popup.getMenu());
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
                case R.id.action_report:
                    CommunityGlobalClass.getInstance().sendAnalyticEvent("Community Prayers 4.0","Report");
                    callWebDialogApi("Report Prayer","Are you sure you want to report this prayer request as inappropriate?", true);
                    return true;

                default:
            }
            return false;
        }
    }

    public void unSetImageMenu() {
        for (int i = 0; i < imgMenuList.size(); i++) {
            imgMenuList.get(i).setImageResource(R.drawable.side_arrow_close);
        }
        imgMenuList.clear();
    }

    //Call web
    public void callCounterApi(final PrayingRequest mPostPrayerRequest, final TextView txtCounter, final boolean report) {
        CommunityGlobalClass.getInstance().showLoading(mActivity);
        Call<PrayingResponse> call = CommunityGlobalClass.getRestApi().prayForUser(mPostPrayerRequest);
        call.enqueue(new retrofit.Callback<PrayingResponse>() {


            @Override
            public void onResponse(Response<PrayingResponse> response, Retrofit retrofit) {
                CommunityGlobalClass.getInstance().cancelDialog();
                if (response.body().getState() == true) {
                    if (!report) {
                        Toast.makeText(mActivity, "Thank you for praying.", Toast.LENGTH_SHORT).show();
                        txtCounter.setText(mPostPrayerRequest.getPrayedCounter() + " users have prayed for this");
                    } else {
                        //Update the list
                        Prayer pray = prayerList.get(pos);
                        pray.setInappropriate_counter(mPostPrayerRequest.getInappropriateCounter());
                        prayerList.remove(pos);
                        prayerList.add(pos, pray);
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                CommunityGlobalClass.getInstance().cancelDialog();
                if (BuildConfig.DEBUG) DebugInfo.loggerException("Post-Failure" + t.getMessage());
                CommunityGlobalClass.getInstance().showServerFailureDialog(mActivity);
            }
        });


    }

  /*  public void callWebDialogApi(String message, final boolean isReport) {

        final SweetAlertDialog taskCompDialog = new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE);
        taskCompDialog
                .setTitleText("Are you sure?")
                .setContentText(message)
                .setConfirmText("Yes")
                .setCancelText("Cancel")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        notifyDataSetChanged();
                        //          taskCompDialog.dismiss();
                        taskCompDialog.dismissWithAnimation();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {
                        // sDialog.dismiss();
                        sDialog.dismissWithAnimation();
                        PrayingRequest mPrayingRequest = new PrayingRequest();
                        if (isReport) {
                            inappropirateCounter = inappropirateCounter + 1;
                            mPrayingRequest.setInappropriateCounter("" + inappropirateCounter);
                            mPrayingRequest.setPrayedCounter("" + (Integer.parseInt(prayerCounter)));

                        } else {
                            mPrayingRequest.setInappropriateCounter("" + inappropirateCounter);
                            mPrayingRequest.setPrayedCounter("" + (Integer.parseInt(prayerCounter) + 1));
                        }

                        mPrayingRequest.setPrayerId(mPrayerModel.getPrayerId());
                        mPrayingRequest.setUserId(mPrayerModel.getUserId());
                        callCounterApi(mPrayingRequest, txtCounter, isReport);
                    }
                });
        taskCompDialog.setCancelable(false);
        taskCompDialog.show();
    }*/
  public void callWebDialogApi(String title,String message, final boolean isReport) {
      final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity,R.style.MyAlertDialogStyle);
      builder.setTitle(title);
      builder.setMessage(message);
      builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
              PrayingRequest mPrayingRequest = new PrayingRequest();
              if (isReport) {
                  inappropirateCounter = inappropirateCounter + 1;
                  mPrayingRequest.setInappropriateCounter("" + inappropirateCounter);
                  mPrayingRequest.setPrayedCounter("" + (Integer.parseInt(prayerCounter)));

              } else {
                  mPrayingRequest.setInappropriateCounter("" + inappropirateCounter);
                  mPrayingRequest.setPrayedCounter("" + (Integer.parseInt(prayerCounter) + 1));
              }

              mPrayingRequest.setPrayerId(mPrayerModel.getPrayerId());
              mPrayingRequest.setUserId(mPrayerModel.getUserId());
              callCounterApi(mPrayingRequest, txtCounter, isReport);
          }
      });
      builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
          }
      });
      builder.show();
  }
}