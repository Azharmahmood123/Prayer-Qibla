package noman.qurantrack.holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.quranreading.qibladirection.R;


/**
 * Created by Administrator on 11/18/2016.
 */

public class QuranTrackerHolder extends RecyclerView.ViewHolder {
    public TextView txt_date,txt_surrah,txt_ayah;
public View line;

    public QuranTrackerHolder(View view) {
        super(view);
        txt_date = (TextView) view.findViewById(R.id.txt_date);
        txt_surrah = (TextView) view.findViewById(R.id.txt_surrah_name);
        txt_ayah = (TextView) view.findViewById(R.id.txt_ayahs);
        line=(View)view.findViewById(R.id.line);

    }
}
