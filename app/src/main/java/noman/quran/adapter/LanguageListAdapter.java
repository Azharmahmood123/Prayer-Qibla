package noman.quran.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.quranreading.qibladirection.R;

import java.util.ArrayList;
import java.util.List;

import noman.quran.JuzConstant;
import noman.quran.holder.LanguageHolder;
import quran.sharedpreference.SurahsSharedPref;

/**
 * Created by Administrator on 1/3/2017.
 */

public class LanguageListAdapter extends RecyclerView.Adapter<LanguageHolder> {
    int[] flag_images;
    CharSequence[] translationList;
     List<RadioButton> radioButtonList = new ArrayList<RadioButton>();
    Context context;
    SurahsSharedPref mSurahsSharedPref;

    public LanguageListAdapter(Context context) {
        this.context = context;
        this.mSurahsSharedPref = new SurahsSharedPref(context);
        this.flag_images= JuzConstant.flag_images;
        this.translationList=JuzConstant.translationList;
    }

    @Override
    public LanguageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.noman_setting_lanuage_row_item, parent, false);

        return new LanguageHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LanguageHolder holder, final int position) {
        holder.languageText.setText(translationList[position]);
        holder.imgIcon.setImageResource(flag_images[position]);


        int checkState = mSurahsSharedPref.getTranslationIndex();
        if (position == checkState) {
            holder.radioButton.setChecked(true);
        } else {
            holder.radioButton.setChecked(false);
        }
        holder.radioButton.setOnCheckedChangeListener(null);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSurahsSharedPref.setTranslationIndex(position);
                notifyDataSetChanged();
            }
        });
        radioButtonList.add(holder.radioButton);
           holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSurahsSharedPref.setTranslationIndex(position);
                setRadioButtonsCheckState(mSurahsSharedPref.getTranslationIndex());
            }
        });

    }


    @Override
    public int getItemCount() {
        return translationList.length;
    }

    public void setRadioButtonsCheckState(int pos) {
        for (int i = 0; i < radioButtonList.size(); i++) {
            if (i == pos) {
                radioButtonList.get(i).setChecked(true);
            } else {
                radioButtonList.get(i).setChecked(false);
            }
        }
    }
}


