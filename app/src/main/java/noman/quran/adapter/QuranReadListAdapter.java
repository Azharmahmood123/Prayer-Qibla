package noman.quran.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import noman.CommunityGlobalClass;
import noman.quran.JuzConstant;
import noman.quran.activity.QuranReadActivity;
import noman.quran.activity.TextSettingScreen;
import noman.quran.dbmanager.JuzDataManager;
import noman.quran.model.JuzModel;
import quran.arabicutils.ArabicUtilities;
import quran.helper.DBManagerQuran;
import quran.model.SurahModel;
import quran.sharedpreference.SurahsSharedPref;

import static android.text.Html.fromHtml;

/**
 * Created by Administrator on 12/23/2016.
 */

public class QuranReadListAdapter extends BaseAdapter {

    private List<SurahModel> surahList;
    int surahPosition;
    String device;
    private int transPos = 1;

    HashMap<Integer, ImageView> mImageMenuList = new HashMap<Integer, ImageView>();
    HashMap<Integer, FrameLayout> mFrameContainerList = new HashMap<Integer, FrameLayout>();

    HashMap<Integer, TextView> mTransaltionText = new HashMap<Integer, TextView>();
    HashMap<Integer, TextView> mTransliraltionText = new HashMap<Integer, TextView>();
    HashMap<Integer, TextView> mArabicText = new HashMap<Integer, TextView>();


    HashMap<Integer, LinearLayout> mInnerMenuContainer = new HashMap<Integer, LinearLayout>();


    HashMap<Integer, Boolean> saveStates = new HashMap<Integer, Boolean>();
    QuranReadActivity mContext;
    String[] urduParah;


    public int textSize = 0, engTextSize = 0;
    SurahsSharedPref mSurahsSharedPref;
    Typeface faceMeQuran;
    List<JuzModel> juzModelList;


    public QuranReadListAdapter(QuranReadActivity context, List<SurahModel> surahList, int surahPosition, int transPosition) {
        this.mContext = context;
        this.surahList = surahList;
        this.surahPosition = surahPosition;
        device = mContext.getResources().getString(R.string.device);
        transPos = transPosition;
        urduParah = mContext.getResources().getStringArray(R.array.urdu_chapters);
        mSurahsSharedPref = new SurahsSharedPref(context);
        //get juzz data against surrah number
        JuzDataManager juzDataManager = new JuzDataManager(mContext);
        this.juzModelList = juzDataManager.getJuzList(mContext.surahNumber);
        faceMeQuran = Typeface.createFromAsset(mContext.getAssets(), "me_quran.ttf");
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return surahList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return surahList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView imgMenuOption;
        TextView tvAyahNo;
        TextView tvArabic;
        TextView tvTransliteration;
        TextView tvTranslation;
        LinearLayout ayahRow;
        LinearLayout ayahNo;
        LinearLayout menuOptionsLayout;
        FrameLayout mFrameAnimaitonLayout;
        LinearLayout innerMenuContainer;
        ImageView imgMenuShare;
        ImageView imgMenuFav;

        TextView tvEngJuzzNo;
        TextView tvArabicJuzzName;
        TextView tvArabicJuzzNo;
        LinearLayout continerJuzzTitle;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        String arabic = surahList.get(position).getArabicAyah();
        // String arabicAyah = ArabicUtilities.reshapeSentence(arabic);
        String translation = surahList.get(position).getTranslation();
        String transliteration = surahList.get(position).getTransliteration();

        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.noman_quran_read_row, null);

            holder = new ViewHolder();

            holder.imgMenuOption = (ImageView) convertView.findViewById(R.id.img_row_menu);
            holder.tvAyahNo = (TextView) convertView.findViewById(R.id.tv_ayah_no);
            holder.tvArabic = (TextView) convertView.findViewById(R.id.tv_ayah);
            holder.tvTranslation = (TextView) convertView.findViewById(R.id.text_translation);
            holder.tvTransliteration = (TextView) convertView.findViewById(R.id.text_transliteration);
            holder.ayahNo = (LinearLayout) convertView.findViewById(R.id.layout_ayah_no);
            holder.ayahRow = (LinearLayout) convertView.findViewById(R.id.ayah_row);

            //Menu Options:
            holder.menuOptionsLayout = (LinearLayout) convertView.findViewById(R.id.menu_options_layout);
            holder.mFrameAnimaitonLayout = (FrameLayout) convertView.findViewById(R.id.child_view_container);
            holder.innerMenuContainer = (LinearLayout) convertView.findViewById(R.id.inner_container_menu);
            holder.imgMenuShare = (ImageView) convertView.findViewById(R.id.img_quran_read_share_row);
            holder.imgMenuFav = (ImageView) convertView.findViewById(R.id.img_quran_read_fav_row);
            // *****************

            // ************** Juzz Container ****************
            holder.continerJuzzTitle = (LinearLayout) convertView.findViewById(R.id.containerJuzzView);
            holder.continerJuzzTitle.setTag(position);
            holder.continerJuzzTitle.setVisibility(View.GONE);

            holder.tvArabicJuzzNo = (TextView) convertView.findViewById(R.id.txt_urdu_juzz_no);
            holder.tvEngJuzzNo = (TextView) convertView.findViewById(R.id.txt_eng_juzz_no);
            holder.tvArabicJuzzName = (TextView) convertView.findViewById(R.id.txt_urdu_para_name);

            //Set font face
            holder.tvArabicJuzzName.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
            holder.tvEngJuzzNo.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);
            holder.tvArabicJuzzNo.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);


            // ******** END *****************


            if (transPos == 7 || transPos == 11) {
                holder.tvTranslation.setGravity(Gravity.RIGHT);
            }

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        // holder.tvArabic.setTextColor(Color.parseColor(((CommunityGlobalClass) mContext.getApplicationContext()).fontColor));
        // holder.tvTranslation.setTextColor(Color.parseColor(((CommunityGlobalClass) mContext.getApplicationContext()).fontColor));
        // holder.tvTransliteration.setTextColor(Color.parseColor(((CommunityGlobalClass) mContext.getApplicationContext()).fontColor));

        holder.tvArabic.setTextColor(Color.parseColor("#000000"));
        holder.tvArabic.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceArabic);
        holder.tvTranslation.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoR);

        holder.tvTransliteration.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoR,Typeface.ITALIC);
        holder.tvTranslation.setTextColor(Color.parseColor("#000000"));
        holder.tvTransliteration.setTextColor(mContext.getResources().getColor(R.color.transliration_color));


        //Adding juzz index
        mContext.tvJuzNumber.setText("Juz: " + juzModelList.get(position).getParaId());

        int juzIndex = surahList.get(position).getJuzzIndex();
        int paraIndex = surahList.get(position).getParaIndex();
        if (juzIndex > -1) {

            holder.continerJuzzTitle.setVisibility(View.VISIBLE);
            holder.tvEngJuzzNo.setText("Juz: " + (juzModelList.get(position).getParaId()));
            holder.tvArabicJuzzName.setText(urduParah[juzModelList.get(position).getParaId() - 1]);
            holder.tvArabicJuzzNo.setText(JuzConstant.arabicCounting[juzModelList.get(position).getParaId()] + " :" + ArabicUtilities.reshapeSentence("جزء"));

        } else {
            holder.continerJuzzTitle.setVisibility(View.GONE);
        }

        String arabicAyat = "";


        if (surahPosition == 9) {
            holder.ayahNo.setVisibility(View.VISIBLE);
            holder.tvAyahNo.setText(JuzConstant.arabicCounting[position + 1]);
            String ayaNumber = "﴿" + JuzConstant.arabicCounting[position + 1] + "﴾";
            arabicAyat = ArabicUtilities.reshapeSentence(arabic);
            // doAyahNumberColor(holder.tvArabic,arabicAyat,position);
            String styledText = arabicAyat + "<font color='#805D01'>" + ayaNumber + "</font>";
            holder.tvArabic.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
        } else {
            //only surrah Fatiah
            if (surahPosition == 1) {

                //holder.tvAyahNo.setText(String.valueOf(position + 1));
                holder.ayahNo.setVisibility(View.VISIBLE);
                holder.tvAyahNo.setText(JuzConstant.arabicCounting[position + 1]);
                String ayaNumber = "\uFD3F" + JuzConstant.arabicCounting[position + 1] + "\uFD3E";
                arabicAyat = ArabicUtilities.reshapeSentence(arabic);
                String styledText = arabicAyat + "<font color='#805D01'>" + ayaNumber + "</font>";
                holder.tvArabic.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
            } else {
                if (position == 0) {
                    holder.ayahNo.setVisibility(View.VISIBLE);
                    arabicAyat = ArabicUtilities.reshapeSentence(arabic);
                    holder.tvArabic.setText(arabicAyat);
                } else {
                    holder.ayahNo.setVisibility(View.VISIBLE);
                    holder.tvAyahNo.setText(JuzConstant.arabicCounting[position]);
                    String ayaNumber = "﴿" + JuzConstant.arabicCounting[position] + "﴾";
                    arabicAyat = ArabicUtilities.reshapeSentence(arabic);

                    String styledText = arabicAyat + "<font color='#805D01'>" + ayaNumber + "</font>";
                    holder.tvArabic.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
                }
            }

       }


        // holder.tvArabic.setText(arabic);
        holder.tvTranslation.setText(translation);
        holder.tvTransliteration.setText(fromHtml(transliteration));

        holder.imgMenuOption.setVisibility(View.VISIBLE);
        int id = surahList.get(position).getBookMarkId();
        if (id > -1) {
            holder.imgMenuOption.setBackgroundResource(R.drawable.fav_mark);
        } else {
            holder.imgMenuOption.setBackgroundResource(R.drawable.side_arrow_open);
        }

        final ViewHolder mViewHolder = holder;
        mViewHolder.mFrameAnimaitonLayout.setVisibility(View.INVISIBLE);
        //save boolean hashmap
        if (!saveStates.containsKey(position)) {
            saveStates.put(position, false);
        }


        if (!mInnerMenuContainer.containsKey(position)) {
            mInnerMenuContainer.put(position, holder.innerMenuContainer);
        }


        holder.imgMenuOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open=true -> close = false
            //   resetMenus(position);

                int pos = position;
                int id = surahList.get(position).getBookMarkId();

                boolean state = false;
                if (saveStates.size() > 0) {
                    if (saveStates.get(pos) != null) {
                        state = saveStates.get(pos);
                    }
                }


                if (state == true) {
                    mViewHolder.mFrameAnimaitonLayout.setVisibility(View.VISIBLE);
                    if (id > -1) {
                        mViewHolder.imgMenuOption.setBackgroundResource(R.drawable.fav_mark);
                      //  mImageMenuList.get(pos).setBackgroundResource(R.drawable.fav_mark);
                    } else {
                        mViewHolder.imgMenuOption.setBackgroundResource(R.drawable.side_arrow_open);
                       // mImageMenuList.get(pos).setBackgroundResource(R.drawable.side_arrow_open);
                    }
                    rightToLeftAnimation(mViewHolder.innerMenuContainer, mViewHolder.mFrameAnimaitonLayout);


                    changeTextColor(mViewHolder.tvTransliteration,mContext.getResources().getColor(R.color.light_gray), mContext.getResources().getColor(R.color.transliration_color));
                    changeTextColor(mViewHolder.tvArabic, mContext.getResources().getColor(R.color.light_gray), mContext.getResources().getColor(R.color.black));
                    changeTextColor(mViewHolder.tvTranslation,mContext.getResources().getColor(R.color.light_gray), mContext.getResources().getColor(R.color.black));
                    doAyahNumberColor(mViewHolder.tvArabic, mViewHolder.tvArabic.getText().toString(), position, mContext.getResources().getColor(R.color.color_ayah_number));


                    state = false;

                } else if (state == false) {
                    mViewHolder.mFrameAnimaitonLayout.setVisibility(View.VISIBLE);
                    if (id > -1) {
                        mViewHolder.imgMenuOption.setBackgroundResource(R.drawable.fav_mark);
                      //  mImageMenuList.get(pos).setBackgroundResource(R.drawable.fav_mark);
                    } else {
                        mViewHolder.imgMenuOption.setBackgroundResource(R.drawable.side_arrow_close);
                    //    mImageMenuList.get(pos).setBackgroundResource(R.drawable.side_arrow_close);
                    }
                    leftToRightAnimation(mViewHolder.innerMenuContainer, mViewHolder.mFrameAnimaitonLayout);


                    changeTextColor(mViewHolder.tvTransliteration, mContext.getResources().getColor(R.color.transliration_color), mContext.getResources().getColor(R.color.light_gray));
                    changeTextColor(mViewHolder.tvArabic, mContext.getResources().getColor(R.color.black),mContext.getResources().getColor(R.color.light_gray));
                    changeTextColor(mViewHolder.tvTranslation, mContext.getResources().getColor(R.color.black), mContext.getResources().getColor(R.color.light_gray));
                    doAyahNumberColor(mViewHolder.tvArabic, mViewHolder.tvArabic.getText().toString(), position, mContext.getResources().getColor(R.color.light_gray));


                    state = true;
                }

                //Save index state
                if (saveStates.size() > 0) {

                    //doing for save old postion state to false
                    for (int i = 0; i < saveStates.size(); i++) {
                        saveStates.remove(i);
                        saveStates.put(i, false);
                    }
                    //Now reset the save state
                    if (pos <= saveStates.size()) {
                        if (saveStates.get(pos) != null) {
                            saveStates.remove(pos);
                            saveStates.put(pos, state);
                        }

                    }
                }

            }
        });

        //Add in list
        if (!mImageMenuList.containsKey(position)) {
            mImageMenuList.put(position, holder.imgMenuOption);
            mFrameContainerList.put(position, mViewHolder.mFrameAnimaitonLayout);
            mTransaltionText.put(position, holder.tvTranslation);
            mTransliraltionText.put(position, holder.tvTransliteration);
            mArabicText.put(position, holder.tvArabic);

        }

        if (((GlobalClass) mContext.getApplicationContext()).isTranslation) {
            holder.tvTranslation.setVisibility(View.GONE);
        } else {
            holder.tvTranslation.setVisibility(View.VISIBLE);
        }

        if (!mSurahsSharedPref.isTransliteration()) {
            holder.tvTransliteration.setVisibility(View.GONE);
        } else {
            holder.tvTransliteration.setVisibility(View.VISIBLE);
        }

        // holder.ayahRow.setBackgroundColor(Color.parseColor(((CommunityGlobalClass) mContext.getApplicationContext()).bgcolor));
        holder.ayahRow.setBackgroundColor(Color.parseColor("#FFFFFF"));

        if (position == ((GlobalClass) mContext.getApplicationContext()).ayahPos) {
            holder.ayahRow.setBackgroundResource(R.drawable.selection_color);
        }

        //Favourit Image Click
        holder.imgMenuFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAnalyticEvent("Favorite Ayah");

                saveFavouriteAyat(mContext.surahName, mContext.surahNumber, position);
                mViewHolder.imgMenuOption.performClick();

            }
        });
        holder.imgMenuShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move to fav
                //To pause the recite audio while is playing
                sendAnalyticEvent("Share Ayah");
                if (!mContext.inProcess) {
                    mContext.inProcess = true;
                    mContext.isSettings = true;
                }
                shareAyah(position);

            }
        });

        //Rum time font size changes
        engTextSize = mSurahsSharedPref.getEnglishFontSize();
        textSize = mSurahsSharedPref.getArabicFontSize();
        //   holder.tvArabic.setPadding(20, ((GlobalClass) mContext.getApplicationContext()).ayahPadding, 20, 5);
        // holder.tvArabic.setPadding(5, 2, 20, 2);
        holder.tvArabic.setTextSize(textSize);

        holder.tvTranslation.setTextSize(engTextSize);
        holder.tvTransliteration.setTextSize(engTextSize);
        // ************************
        //For text Setting


        //  textColorDim(position, false, true);
        // ************************

        return convertView;
    }

    private void shareAyah(int position) {
        SurahModel surahModel = surahList.get(position);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, surahModel.getArabicAyah() + "\n" + surahModel.getTranslation());
        mContext.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }


    //Animate-Right toleft and exit view
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void leftToRightAnimation(LinearLayout childView, View containerView) {
        TranslateAnimation animate = new TranslateAnimation(-containerView.getWidth(), 0, 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        childView.startAnimation(animate);
    }

    //Animate - Right to left enter view:
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void rightToLeftAnimation(LinearLayout childView, View containerView) {
        TranslateAnimation animate = new TranslateAnimation(0, -containerView.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        childView.startAnimation(animate);
    }

    public void resetMenus(int pos) {

        Iterator myVeryOwnIterator = mImageMenuList.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            Integer key = (Integer) myVeryOwnIterator.next();
            // String value=(String)meMap.get(key);

            mFrameContainerList.get(key).setVisibility(View.INVISIBLE);
            if (pos != key) {
                rightToLeftAnimation(mInnerMenuContainer.get(key), mFrameContainerList.get(key));
                coloredAyahText(false, key, "#000000");
            }
          /*  if (key < surahList.size()) {
                int id = surahList.get(key).getBookMarkId();
                if (id > -1) {
                    Log.e("BookMar","haio=="+key);
                    mImageMenuList.get(key).setBackgroundResource(R.drawable.fav_mark);
                }
                else
                {
                    Log.e("nae","jeee=="+key);
                    mImageMenuList.get(key).setBackgroundResource(R.drawable.side_arrow_open);
                }
            }*/
        }
    }


    public void saveFavouriteAyat(String surahName, int surahNo, int ayahNo) {

        DBManagerQuran dbObj = new DBManagerQuran(mContext);
        dbObj.open();

        SurahModel model = surahList.get(ayahNo);
        int id = model.getBookMarkId();
        if (id == -1) {
            id = (int) dbObj.addBookmark(surahName, surahNo, ayahNo);
            model.setBookMarkId(id);
            CommunityGlobalClass.getInstance().showShortToast(mContext.getResources().getString(R.string.txt_add_fav), 500, Gravity.CENTER);
            mImageMenuList.get(ayahNo).setBackgroundResource(R.drawable.fav_mark);
        } else {
            if (dbObj.deleteOneBookmark(id)) {
                model.setBookMarkId(-1);
                CommunityGlobalClass.getInstance().showShortToast(mContext.getResources().getString(R.string.txt_remove_fav), 500, Gravity.CENTER);
                mImageMenuList.get(ayahNo).setBackgroundResource(R.drawable.side_arrow_open);
            }
        }
        dbObj.close();
        // resetMenus(true,ayahNo);
        surahList.set(ayahNo, model);
        notifyDataSetChanged();


    }



    void doAyahNumberColor(TextView tvArabic, String arabicAyat, int position, int color) {
int length=0;
        ForegroundColorSpan fcs = new ForegroundColorSpan(color);
        SpannableStringBuilder sb = new SpannableStringBuilder(arabicAyat.trim());
        if (position > 100) {
            sb.setSpan(fcs, arabicAyat.trim().length() - 5, arabicAyat.trim().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            length=5;
        } else if (position > 10) {
            sb.setSpan(fcs, arabicAyat.trim().length() - 4, arabicAyat.trim().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            length=10;
        } else {
            if (position != 0) {
                sb.setSpan(fcs, arabicAyat.trim().length() - 3, arabicAyat.trim().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                length = 3;
            } else {
                if (surahPosition == 9 || surahPosition == 1) {
                    sb.setSpan(fcs, arabicAyat.trim().length() - 3, arabicAyat.trim().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    length = 3;
                }
            }
        }

            // String  path = arabicAyat.substring(Math.max(0, arabicAyat.length() - length));//Remove aya index

        tvArabic.setText(sb);



    }


    public void coloredAyahText(boolean isLightGreyText, int key, String colorBlack) {


        mTransaltionText.get(key).setTextColor(Color.parseColor(colorBlack));
        if (isLightGreyText) {
            mTransliraltionText.get(key).setTextColor(Color.parseColor(colorBlack));
            mArabicText.get(key).setTextColor(Color.parseColor(colorBlack));
            doAyahNumberColor(mArabicText.get(key), mArabicText.get(key).getText().toString(), key, mContext.getResources().getColor(R.color.light_gray));
        } else {
            mTransliraltionText.get(key).setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            mArabicText.get(key).setTextColor(Color.parseColor(colorBlack));
            doAyahNumberColor(mArabicText.get(key), mArabicText.get(key).getText().toString(), key, mContext.getResources().getColor(R.color.color_ayah_number));
        }


    }


    public void changeTextColor(final TextView textView, int startColor, int endColor) {
        if (textView == null) return;
        int colorFrom = startColor;
        int colorTo = endColor;
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                textView.setTextColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    private void sendAnalyticEvent(String eventAction) {
        AnalyticSingaltonClass.getInstance(mContext).sendEventAnalytics("Quran 4.0", eventAction);
    }
}

