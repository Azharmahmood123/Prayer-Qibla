package names.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quranreading.qibladirection.R;

import names.adapters.NamesData;

public class NamesDetailFragment extends Fragment {
    private int namePos;
    private TextView nameTv, meaningTv, detailMeaningTv, tvNameArabic;
    //    private ImageView nameIv;
    NamesData namesData;
    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        namesData = new NamesData(mContext);
        namesData.setNamesImage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View nameView = inflater.inflate(R.layout.name_detail_fragment, null);

        namePos = getArguments().getInt("namePosition", 0);

        nameTv = (TextView) nameView.findViewById(R.id.nameTv);
        meaningTv = (TextView) nameView.findViewById(R.id.meaingTv);
        detailMeaningTv = (TextView) nameView.findViewById(R.id.detailTv);
        tvNameArabic = (TextView) nameView.findViewById(R.id.tv_name_arabic);

//        nameIv = (ImageView) nameView.findViewById(R.id.nameImage);
//        nameIv.setImageResource(namesData.getImageId(namePos));

        if(namePos == 85)//Large Arabic Text
        {
            tvNameArabic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40.f);
        }

        tvNameArabic.setText(namesData.getNameArabic(namePos));
        nameTv.setText(namesData.getNameEnglish(namePos));
        meaningTv.setText(namesData.getNameMeaning(namePos));
        detailMeaningTv.setText(namesData.getNameDetails(namePos));

        return nameView;
    }
}