package noman.quran.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;
import java.util.List;

import noman.CommunityGlobalClass;
import noman.quran.activity.QuranModuleActivity;
import noman.quran.fragment.QuranListFragment;
import noman.quran.holder.ParentContainerHolder;
import quran.helper.DBManagerQuran;
import quran.model.BookmarksListModel;


/**
 * Created by Administrator on 11/18/2016.
 */

public class ParentContainerList extends RecyclerView.Adapter<ParentContainerHolder> {

    List<ImageView> listImages_parent = new ArrayList<>();
    List<LinearLayout> listContainer_parent = new ArrayList<>();
    List<RecyclerView> listRecycler_parent = new ArrayList<>();


    Context mContext;
    ParentContainerList parentContainerList;

    String[] navItemParent = {"Juz", "Favourite", "Sajdahs", "Stop Sign"};
    int[] imgId = {R.drawable.juz_nav, R.drawable.fav_nav, R.drawable.sajdah_nav, R.drawable.stopsign_nav};
    int[] imgId_hover = {R.drawable.juz_nav_hover, R.drawable.fav_nav_hover, R.drawable.sajdah_nav_hover, R.drawable.stopsign_nav_hover};
    int parentPos = -1;
    QuranModuleActivity quranModuleActivity;
    QuranListFragment quranListFragment;

    public ParentContainerList(QuranModuleActivity quranModuleActivity, QuranListFragment quranListFragment, Context mContext) {
        this.mContext = mContext;
        this.quranModuleActivity = quranModuleActivity;
        parentContainerList=this;
        this.quranListFragment=quranListFragment;
    }

    @Override
    public ParentContainerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.noman_nav_recycler_parent, parent, false);

        return new ParentContainerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ParentContainerHolder holder, final int position) {
        listImages_parent.add(holder.img_icon);
        listContainer_parent.add(holder.containerLinear);
        listRecycler_parent.add(holder.mRecyclerView);

        holder.name.setText(navItemParent[position]);
        holder.img_icon.setImageResource(imgId[position]);

        holder.name.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);

        holder.containerLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (parentPos != position) {
                    parentPos=position;
                    selectorParentNav(position);

                    ChildContainerList adapter = new ChildContainerList(quranModuleActivity,quranListFragment,mContext,parentContainerList, position);
                    holder.mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    holder.mRecyclerView.setAdapter(adapter);
                    if(position == 1)
                    {
                        if(initFavouriteSurahList().size()==0)
                        {
                            CommunityGlobalClass.getInstance().showShortToast(mContext.getResources().getString(R.string.txt_no_fav), 500, Gravity.CENTER);
                        }
                    }

                }
                else
                {
                    parentPos=-1;
                    selectorParentNav(-1);
                }
            }
        });


    }

    public void selectorParentNav(int position) {
        //Reset background
        for (int i = 0; i < 4; i++) {
            if (position == i) {
                //Selector
                listRecycler_parent.get(i).setAdapter(null);
                listRecycler_parent.get(i).setVisibility(View.VISIBLE);

                listContainer_parent.get(i).setBackgroundColor(mContext.getResources().getColor(R.color.gray_activated));
                listImages_parent.get(i).setImageResource(imgId_hover[i]);
            } else {
                //Unselector
                listRecycler_parent.get(i).setVisibility(View.GONE);
                listRecycler_parent.get(i).setAdapter(null);
                listContainer_parent.get(i).setBackgroundColor(mContext.getResources().getColor(R.color.white));
                listImages_parent.get(i).setImageResource(imgId[i]);
            }
        }

    }


    @Override
    public int getItemCount() {
        return navItemParent.length;
    }

    public List<BookmarksListModel> initFavouriteSurahList() {
        List<BookmarksListModel>favModelList=new ArrayList<>();
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
        return favModelList;
    }
}