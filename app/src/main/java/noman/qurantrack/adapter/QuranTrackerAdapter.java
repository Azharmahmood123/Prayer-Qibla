package noman.qurantrack.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quranreading.qibladirection.R;

import java.util.List;

import noman.CommunityGlobalClass;
import noman.quran.model.JuzConstant;
import noman.qurantrack.holder.QuranTrackerHolder;
import noman.qurantrack.model.QuranTrackerModel;

/**
 * Created by Administrator on 11/18/2016.
 */

public class QuranTrackerAdapter extends RecyclerView.Adapter<QuranTrackerHolder> {


    Activity mActivity;

    List<QuranTrackerModel> mQuranTrackerModels;

    public QuranTrackerAdapter(Activity mActivity, List<QuranTrackerModel> mQuranTrackerModels) {
        this.mQuranTrackerModels = mQuranTrackerModels;
        this.mActivity = mActivity;
    }

    @Override
    public QuranTrackerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quran_tracker, parent, false);

        return new QuranTrackerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final QuranTrackerHolder holder, final int position) {
        final QuranTrackerModel mPrayer = mQuranTrackerModels.get(position);
        holder.txt_date.setText(CommunityGlobalClass.getMonthName(mPrayer.getMonth()) + " - " + mPrayer.getDate() + " - " + mPrayer.getYear());

        if (mPrayer.getSurahNo() == 0) {
            holder.txt_surrah.setText("");
            holder.txt_ayah.setText("");
            //holder.txt_surrah.setText(mActivity.getString(R.string.txt_default_date));
            //  holder.txt_ayah.setText(mActivity.getString(R.string.txt_default_date));
        } else {
            holder.txt_surrah.setText(JuzConstant.engSurahName[mPrayer.getSurahNo() - 1]);
            holder.txt_ayah.setText("" + mPrayer.getAyahNo());
        }
        if (position == getItemCount()-1) {
            holder.line.setVisibility(View.GONE);
        }
        else
        {
            holder.line.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mQuranTrackerModels.size();
    }


}