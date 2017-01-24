package noman.quran.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.quranreading.qibladirection.R;

/**
 * Created by Administrator on 1/3/2017.
 */

public class LanguageHolder extends RecyclerView.ViewHolder {
    public TextView languageText;
    public ImageView imgIcon;
    public RadioButton radioButton;

    public LanguageHolder(View view) {
        super(view);
        languageText = (TextView) view.findViewById(R.id.text_language);
        imgIcon = (ImageView) view.findViewById(R.id.icon_language);
        radioButton = (RadioButton) view.findViewById(R.id.radio_language);
    }


}
