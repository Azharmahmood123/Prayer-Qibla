package noman.quran.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.MainActivityNew;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;

import noman.CommunityGlobalClass;
import noman.quran.JuzConstant;
import noman.quran.QuranModuleActivity;
import noman.quran.activity.QuranReadActivity;
import noman.quran.dbmanager.JuzDataManager;
import noman.quran.fragment.QuranListFragment;
import noman.quran.holder.ChildContainerHolder;
import noman.quran.model.JuzModel;
import quran.activities.SurahActivity;
import quran.helper.DBManagerQuran;
import quran.model.BookmarksListModel;

import static quran.activities.SurahActivity.KEY_EXTRA_SURAH_NO;


/**
 * Created by Administrator on 11/18/2016.
 */

public class ChildContainerList extends RecyclerView.Adapter<ChildContainerHolder> {


    Context mContext;

    String[] engParah;
    String[] urduParah;

    String[] engStopSign;
    String[] urduStopSign;
    String[] urduSurrahName;

    String[] reveledPlacesSurrah;
    int childType;
    int sizeList = 0;
    ArrayList<BookmarksListModel> sajdahModelList = new ArrayList<BookmarksListModel>();
    ArrayList<BookmarksListModel> favModelList = new ArrayList<BookmarksListModel>();

    QuranModuleActivity quranModuleActivity;
    ParentContainerList parentContainerList;
    QuranListFragment quranListFragment;


    private final int[][] juzzIndex = JuzConstant.juzzIndex;

    public ChildContainerList(QuranModuleActivity quranModuleActivity, QuranListFragment quranListFragment,
                              Context mContext, ParentContainerList parentContainerList, int childType) {
        this.childType = childType;
        this.mContext = mContext;
        this.quranModuleActivity = quranModuleActivity;
        this.parentContainerList = parentContainerList;
        this.quranListFragment = quranListFragment;
        engParah = mContext.getResources().getStringArray(R.array.eng_chapters);
        urduParah = mContext.getResources().getStringArray(R.array.urdu_chapters);
        urduSurrahName = mContext.getResources().getStringArray(R.array.surahNamesArabic);
        engStopSign = mContext.getResources().getStringArray(R.array.eng_sign_detail);
        urduStopSign = mContext.getResources().getStringArray(R.array.urdu_sign_detail);
        reveledPlacesSurrah = mContext.getResources().getStringArray(R.array.revealedPlaces);

        if (childType == 0) {
            sizeList = engParah.length;
        } else if (childType == 1) {
            initFavouriteSurahList();
            sizeList = favModelList.size();
        } else if (childType == 2) {
            initSajadaList();
            sizeList = sajdahModelList.size();
        } else if (childType == 3) {
            sizeList = engStopSign.length;
        }

    }

    public void initSajadaList() {
        String surahName;
        int id, surahNo, ayahNo;

        String[] surahNamesArr = {"Al-A'raf", "Ar-Ra'd", "An-Nahl", "Al-Israa", "Maryam", "Al-Hajj", "Al-Hajj (As Shafee - Optional)", "Al-Furqan", "An-Naml", "As-Sajdah", "Saad (Hanafi)", "Ha Mim/Fussilat", "An-Najm", "Al-Inshiqaq", "Al-'Alaq"};
        int[] surahNoArr = {7, 13, 16, 17, 19, 22, 22, 25, 27, 32, 38, 41, 53, 84, 96};
        int[] ayahNoArr = {206, 15, 50, 109, 58, 18, 77, 60, 26, 15, 24, 38, 62, 21, 19};

        for (int pos = 0; pos < surahNoArr.length; pos++) {
            id = pos + 1;
            surahName = surahNamesArr[pos];
            surahNo = surahNoArr[pos];
            ayahNo = ayahNoArr[pos];

            BookmarksListModel model = new BookmarksListModel(id, surahName, surahNo, ayahNo);
            sajdahModelList.add(model);
        }
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }

    @Override
    public ChildContainerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.noman_nav_recycler_child, parent, false);

        return new ChildContainerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChildContainerHolder holder, final int position) {

        setFadeAnimation(holder.itemView);
        holder.txt_juzz_english_para.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
        holder.txt_juzz_urdu_para.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);

        holder.txt_sign_detail.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
        holder.txt_urdu_sign_detail.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);


        if (childType == 0) {
            CommunityGlobalClass.getInstance().sendAnalyticEvent("Quran 4.0","Quran Menu Juzz");
            juzzContainerOperate(holder, position);
        } else if (childType == 1) {
            CommunityGlobalClass.getInstance().sendAnalyticEvent("Quran 4.0","Quran Menu Favourite");
            favouriteContainerOperate(holder, position);
        } else if (childType == 2) {
            CommunityGlobalClass.getInstance().sendAnalyticEvent("Quran 4.0","Quran Menu Sajdahs");
            holder.linearView.setVisibility(View.GONE);
            sajdahContainerOperate(holder, position);
        } else if (childType == 3) {
            CommunityGlobalClass.getInstance().sendAnalyticEvent("Quran 4.0","Quran Menu StopSign");
            holder.linearView.setVisibility(View.VISIBLE);
            signContainerOperate(holder, position);
        }
    }


    public void hideAllContainer(ChildContainerHolder holder) {
        holder.containerJuzz.setVisibility(View.GONE);
        holder.containerFavourite.setVisibility(View.GONE);
        holder.containerStopSign.setVisibility(View.GONE);

    }

    public void juzzContainerOperate(ChildContainerHolder holder, final int position) {
        hideAllContainer(holder);
        holder.containerJuzz.setVisibility(View.VISIBLE);
        holder.txt_juzz_english_para.setText(engParah[position]);
        holder.txt_juzz_urdu_para.setText(urduParah[position]);


        holder.containerJuzz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int[] selectedArray = juzzIndex[position];

                Intent end_actvty = new Intent(mContext, QuranReadActivity.class);
                end_actvty.putExtra(KEY_EXTRA_SURAH_NO, selectedArray[0]);
                end_actvty.putExtra(QuranReadActivity.KEY_EXTRA_AYAH_NO, selectedArray[1]);
                mContext.startActivity(end_actvty);

                if (quranModuleActivity.mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    quranModuleActivity.closeDrawer();
                }


            }
        });
    }


    public void favouriteContainerOperate(ChildContainerHolder holder, final int position) {

        hideAllContainer(holder);
        holder.containerFavourite.setVisibility(View.VISIBLE);

        String surahName = favModelList.get(position).getsurahName();
        int ayahNo = favModelList.get(position).getAyahNo();

        String name;
        if (surahName.equals("Al-Fatihah") || surahName.equals("At-Taubah")) {
            name = surahName + ", " + String.valueOf(ayahNo + 1);
        } else {
            name = surahName + ", " + String.valueOf(ayahNo);
        }

        holder.txt_fav_english_surah.setText(surahName);
        holder.txt_fav_urdu_surah.setText(urduSurrahName[(favModelList.get(position).getsurahNo()) - 1]);

        String placeOfSurrah = reveledPlacesSurrah[(favModelList.get(position).getsurahNo()) - 1];
        JuzDataManager juzDataManager = new JuzDataManager(mContext);
        JuzModel juzModel = juzDataManager.getJuzNumber(favModelList.get(position).getsurahNo(),ayahNo);
        //Move to fav
       final int verseNo = favModelList.get(position).getAyahNo();
        if (favModelList.get(position).getsurahNo() == 9 || favModelList.get(position).getsurahNo() == 1) {

            holder.txt_fav_detail.setText("Verse: " +  (verseNo + 1) + ", Juz: "+juzModel.getParaId());
        }
        else
        {
            holder.txt_fav_detail.setText("Verse: " + verseNo + ", Juz: "+juzModel.getParaId());
        }

        holder.linearDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookmarksListModel model = favModelList.get(position);
                deleteFavouriteSurrah(model);
                favModelList.remove(position);
                notifyItemRemoved(position);
                //Re-Adjjust the list count
                sizeList = favModelList.size();
                notifyDataSetChanged();


            }
        });

        holder.llEnglishFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent end_actvty = new Intent(mContext, QuranReadActivity.class);
                end_actvty.putExtra(SurahActivity.KEY_EXTRA_SURAH_NO, favModelList.get(position).getsurahNo());
                end_actvty.putExtra(SurahActivity.KEY_EXTRA_AYAH_NO, verseNo);
                mContext.startActivity(end_actvty);


                if (quranModuleActivity.mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    quranModuleActivity.closeDrawer();
                }


            }
        });
        holder.relUrduFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent end_actvty = new Intent(mContext, QuranReadActivity.class);
                end_actvty.putExtra(SurahActivity.KEY_EXTRA_SURAH_NO, favModelList.get(position).getsurahNo());
                end_actvty.putExtra(SurahActivity.KEY_EXTRA_AYAH_NO, verseNo);
                mContext.startActivity(end_actvty);

                if (quranModuleActivity.mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    quranModuleActivity.closeDrawer();
                }


            }
        });
    }

    public void sajdahContainerOperate(ChildContainerHolder holder, final int position) {

        hideAllContainer(holder);
        holder.containerStopSign.setVisibility(View.VISIBLE);
        holder.txt_urdu_sign_detail.setVisibility(View.GONE);
        String surahName = sajdahModelList.get(position).getsurahName();
        int ayahNo = sajdahModelList.get(position).getAyahNo();
        String name;
        if (surahName.equals("Al-Fatihah") || surahName.equals("At-Taubah")) {
            name = surahName + ", " + String.valueOf(ayahNo + 1);
        } else {
            name = surahName + ", " + String.valueOf(ayahNo);
        }
        holder.txt_sign_detail.setText(name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int surahNo = sajdahModelList.get(position).getsurahNo();
                int ayahNo = sajdahModelList.get(position).getAyahNo();

                Intent end_actvty = new Intent(mContext, QuranReadActivity.class);
                end_actvty.putExtra(SurahActivity.KEY_EXTRA_SURAH_NO, surahNo);
                end_actvty.putExtra(SurahActivity.KEY_EXTRA_AYAH_NO, ayahNo);
                mContext.startActivity(end_actvty);

                if (quranModuleActivity.mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    quranModuleActivity.closeDrawer();
                }
            }
        });

    }


    public void signContainerOperate(ChildContainerHolder holder, int position) {

        hideAllContainer(holder);
        holder.containerStopSign.setVisibility(View.VISIBLE);
        holder.txt_sign_detail.setText(engStopSign[position]);
        holder.txt_urdu_sign_detail.setText(urduStopSign[position]);
    }

    @Override
    public int getItemCount() {
        return sizeList;
    }

    public void initFavouriteSurahList() {

        favModelList.clear();
        DBManagerQuran dbObj = new DBManagerQuran(quranModuleActivity);
        dbObj.open();
        String surahName;
        int id, surahNo, ayahNo;
        Cursor c = dbObj.getAllBookmarks();
        if (c.moveToFirst()) {
            do {
                id = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_ID));
                surahName = c.getString(c.getColumnIndex(DBManagerQuran.FLD_SURAH_NAME));
                surahNo = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_SURAH_NO));
                ayahNo = c.getInt(c.getColumnIndex(DBManagerQuran.FLD_AYAH_NO));
                BookmarksListModel model = new BookmarksListModel(id, surahName, surahNo, ayahNo);
                favModelList.add(model);

            }
            while (c.moveToNext());

        }
        c.close();
        dbObj.close();
    }

    public void deleteFavouriteSurrah(BookmarksListModel selecteditem) {
        DBManagerQuran dbObj = new DBManagerQuran(quranModuleActivity);
        dbObj.open();
        dbObj.deleteOneBookmark(selecteditem.getBookMarkId());
        // Close CAB
        dbObj.close();
    }
}