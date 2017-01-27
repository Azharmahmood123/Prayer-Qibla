package names.adapters;

import android.content.Context;

import com.quranreading.qibladirection.R;

import java.util.ArrayList;

/**
 * Created by cyber on 1/3/2017.
 */

public class NamesData {

    private Context mContext;
    private String[] namesArabic;
    private String[] names;
    private String[] namesMeaning;
    private String[] namesDetailMeaning;

    public NamesData(Context context) {
        mContext = context;
        namesArabic = mContext.getResources().getStringArray(R.array.allah_names);
        names = mContext.getResources().getStringArray(R.array.allah_names_transliteration);
        namesMeaning = mContext.getResources().getStringArray(R.array.allah_names_english);
        namesDetailMeaning = mContext.getResources().getStringArray(R.array.allah_names_detail_english);
    }

    private ArrayList<Integer> nameImageArray = new ArrayList<>();


    public void setNamesImage() {

        for (int i = 1; i < 100; i++) {
            String imageName = "name_" + i;
            Integer imgId = mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
            nameImageArray.add(imgId);
        }
    }

    public int getImageId(int position) {
        return nameImageArray.get(position);
    }

    public String getNameEnglish(int position) {
        return names[position];
    }

    public String getNameArabic(int position) {
        return namesArabic[position];
    }

    public String getNameMeaning(int position) {
        return namesMeaning[position];
    }

    public String getNameDetails(int position) {
        return namesDetailMeaning[position];
    }

    public ArrayList<NamesModel> getNamesData() {

        ArrayList<NamesModel> dataList = new ArrayList<>();

        for (int i = 0; i < names.length; i++) {
            NamesModel data = new NamesModel();
            data.setEng(names[i]);
            data.setArabic(namesArabic[i]);
            data.setDetails(namesDetailMeaning[i]);
            data.setMeaning(namesMeaning[i]);
            dataList.add(data);
        }

        return dataList;
    }

    public int getNamesSize() {
        return namesArabic.length;
    }

}
