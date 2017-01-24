package noman.quran.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import noman.quran.JuzConstant;
import noman.quran.adapter.LanguageListAdapter;
import quran.arabicutils.ArabicUtilities;
import quran.sharedpreference.SurahsSharedPref;

/**
 * Created by Administrator on 1/3/2017.
 */

public class TextSettingScreen extends Activity {
    SurahsSharedPref mSurahsSharedPref;

    boolean chkTransliteration=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noman_quran_settings_text_translation);
        mSurahsSharedPref = new SurahsSharedPref(this);
        handleSeekBar();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_language_transaltion);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        LanguageListAdapter adapter = new LanguageListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);

        LinearLayout imgBack = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        translirationSetting();
    }

    public void handleSeekBar() {
        final TextView text = (TextView) findViewById(R.id.txt_example);
        text.setTypeface(((GlobalClass) getApplicationContext()).faceArabic);
        text.setText(ArabicUtilities.reshapeSentence("الفاتحة"));

        final int fontSize_English[] = JuzConstant.fontSize_English;
        final int fontSize_Arabic[] = JuzConstant.fontSize_Arabic;

        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar_text_size);
        seekBar.setProgress(mSurahsSharedPref.getSeekbarPosition());
        text.setTextSize((int) fontSize_Arabic[mSurahsSharedPref.getSeekbarPosition()]);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSurahsSharedPref.setSeekbarPosition(progress);
                mSurahsSharedPref.setEnglishFontSize(fontSize_English[progress]);
                mSurahsSharedPref.setArabicFontSize(fontSize_Arabic[progress]);
                text.setTextSize((int) fontSize_Arabic[mSurahsSharedPref.getSeekbarPosition()]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        ImageView imgBtnTextSizeDecrease = (ImageView) findViewById(R.id.btn_text_size_decrease);
        ImageView imgBtnTextSizeIncreas = (ImageView) findViewById(R.id.btn_text_size_increase);
        imgBtnTextSizeIncreas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = mSurahsSharedPref.getSeekbarPosition();
                if (progress < 5) {
                    progress++;
                    mSurahsSharedPref.setSeekbarPosition(progress);
                    mSurahsSharedPref.setEnglishFontSize(fontSize_English[progress]);
                    mSurahsSharedPref.setArabicFontSize(fontSize_Arabic[progress]);
                    seekBar.setProgress(mSurahsSharedPref.getSeekbarPosition());
                    text.setTextSize((int) fontSize_Arabic[mSurahsSharedPref.getSeekbarPosition()]);


                }
            }
        });
        imgBtnTextSizeDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = mSurahsSharedPref.getSeekbarPosition();
                if (progress > 0) {
                    progress--;
                    mSurahsSharedPref.setSeekbarPosition(progress);
                    mSurahsSharedPref.setEnglishFontSize(fontSize_English[progress]);
                    mSurahsSharedPref.setArabicFontSize(fontSize_Arabic[progress]);
                    seekBar.setProgress(mSurahsSharedPref.getSeekbarPosition());
                    text.setTextSize((int) fontSize_Arabic[mSurahsSharedPref.getSeekbarPosition()]);


                }
            }
        });
    }
public void translirationSetting()
{

    CheckBox btnTransliteration =(CheckBox)findViewById(R.id.check_transliteration_settings);
    chkTransliteration = mSurahsSharedPref.isTransliteration();
    if (chkTransliteration) {
        btnTransliteration.setChecked(true);
    } else {
        btnTransliteration.setChecked(false);
    }
    btnTransliteration.setOnCheckedChangeListener(null);

    btnTransliteration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (chkTransliteration) {
                chkTransliteration = false;
                mSurahsSharedPref.setTransliteration(chkTransliteration);
                mSurahsSharedPref.setLastTranslirationState(chkTransliteration);
               // btnTransliteration.setChecked(false);
            } else {

                chkTransliteration = true;
                mSurahsSharedPref.setTransliteration(chkTransliteration);
                mSurahsSharedPref.setLastTranslirationState(chkTransliteration);
              //  btnTransliteration.setChecked(true);
            }
        }
    });



}

}
