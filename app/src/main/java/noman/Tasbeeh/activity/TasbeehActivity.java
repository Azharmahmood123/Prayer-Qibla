package noman.Tasbeeh.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.quranreading.helper.DBManager;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.List;

import noman.Ads.AdIntegration;
import noman.CommunityGlobalClass;
import noman.Tasbeeh.SharedPref;
import noman.Tasbeeh.adapter.CustomViewPagerAdapter;
import noman.Tasbeeh.model.TasbeehModel;


/**
 * Created by Ninesol on 12/9/2016.
 */

public class TasbeehActivity extends AdIntegration implements View.OnClickListener {

    int thirtyThreeCounter = 0;
    int nintyNineCounter = 0;

    public TextView txtToolbarTitle;
    LinearLayout imgBackBtn;
    private TextView countValue;
    private TextView totalCount;
    private TextView totalUptoCount;
    private TextView numberTasbeehMenu;
    private ImageView tasbeeh;
    private ImageView soundModeIcon;
    private RelativeLayout mainLayout;
    private RelativeLayout soundButton;
    private RelativeLayout resetButton;
    private RelativeLayout counterModeButton;
    private AnimationDrawable frameAnimation;
    private GestureDetectorCompat mDetector;

    private SharedPref sharedPref;


    private boolean soundMode = true;
    private boolean vibrateMode = false;

    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayerForBackward;

    private Vibrator vibrator;


    private int totalTasbeehCountValue = 0;
    private int counter = 0;



    boolean isTasbeehContianer = false;
    ViewPager viewPager;
    int tasbeehId = 0;


    CustomViewPagerAdapter customViewPagerAdapter;
    List<TasbeehModel> mTasbeehList;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasbeeh);
        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.ads_layout));
        }
        initateToolBarItems();

        showAnalytics(true, "");

        sharedPref = new SharedPref(this);


        isTasbeehContianer = getIntent().getExtras().getBoolean("isTasbeeh", false); //its used before when database not given
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        numberTasbeehMenu = (TextView) findViewById(R.id.countdown_mode_text);
        totalUptoCount = (TextView) findViewById(R.id.total_counter_value);
        soundModeIcon = (ImageView) findViewById(R.id.sound_mode_btn_image);
        viewPager = (ViewPager) findViewById(R.id.viewpager_tasbeeh);


        if (sharedPref.getSoundMode()) {
            soundModeIcon.setImageResource(R.drawable.sound_on);
        } else if (!sharedPref.getSoundMode() && sharedPref.getVibrationMode()) {
            soundModeIcon.setImageResource(R.drawable.vibrate);
        } else if (!sharedPref.getSoundMode() && !sharedPref.getVibrationMode()) {
            soundModeIcon.setImageResource(R.drawable.sound_off);
        }

        countValue = (TextView) findViewById(R.id.counter_value);
        totalCount = (TextView) findViewById(R.id.total_value);


        tasbeeh = (ImageView) findViewById(R.id.tasbeeh_animation_view);
        tasbeeh.setBackground(getResources().getDrawable(R.drawable.tasbeeh_increment_animation));

        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        soundButton = (RelativeLayout) findViewById(R.id.sound_mode_btn);
        resetButton = (RelativeLayout) findViewById(R.id.reset_btn);
        counterModeButton = (RelativeLayout) findViewById(R.id.countdown_mode_btn);

        mediaPlayer = MediaPlayer.create(TasbeehActivity.this, R.raw.tasbih_inc);
        mediaPlayerForBackward = MediaPlayer.create(TasbeehActivity.this, R.raw.tasbih_dec);

        mainLayout.setOnClickListener(this);
        soundButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        counterModeButton.setOnClickListener(this);

        if (sharedPref.getSoundMode() != null) {
            soundMode = sharedPref.getSoundMode();
        }

        if (sharedPref.getVibrationMode() != null) {
            vibrateMode = sharedPref.getVibrationMode();
        }

        //Iniitalze list
        initializeIndexList();
        customViewPagerAdapter = new CustomViewPagerAdapter(this, mTasbeehList);
        viewPager.setAdapter(customViewPagerAdapter);
        //Set Prefernce when its
        if (isTasbeehContianer) {
            getTasbeehPref();
        } else {

            tasbeehId = getIntent().getExtras().getInt("id");
            getTashbeehDataFromDatabase(tasbeehId);

        }


        viewPager.setCurrentItem(tasbeehId);

        mDetector = new GestureDetectorCompat(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {


                tasbeeh.setBackground(getResources().getDrawable(R.drawable.tasbeeh_increment_animation));
                frameAnimation = (AnimationDrawable) tasbeeh.getBackground();
                frameAnimation.start();
                if (soundMode) {
                    mediaPlayer.start();
                } else if (!soundMode && vibrateMode) {
                    vibrator.vibrate(100);
                } else if (!soundMode && !vibrateMode) {

                }
                incrementTasbeeh();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                float deltaX = motionEvent1.getX() - motionEvent.getX();
                if (deltaX > 0) {

                    tasbeeh.setBackground(getResources().getDrawable(R.drawable.tasbeeh_increment_animation));
                    frameAnimation = (AnimationDrawable) tasbeeh.getBackground();
                    frameAnimation.start();
                    if (soundMode) {
                        mediaPlayer.start();
                    } else if (!soundMode && vibrateMode) {
                        vibrator.vibrate(100);
                    }
                    incrementTasbeeh();


                } else if (deltaX < 0) {

                    totalCount.setText(Integer.toString(totalTasbeehCountValue));
                    tasbeeh.setBackground(getResources().getDrawable(R.drawable.tasbeeh_decrement_animation));
                    frameAnimation = (AnimationDrawable) tasbeeh.getBackground();
                    frameAnimation.start();
                    if (soundMode) {
                        mediaPlayerForBackward.start();
                    } else if (!soundMode && vibrateMode) {
                        vibrator.vibrate(100);
                    }

                    if (totalTasbeehCountValue > 0)
                    {
                        decrementTasbeeh();
                    }

                }
                return true;
            }
        });

    }


    public void decrementTasbeeh() {
        totalTasbeehCountValue = totalTasbeehCountValue - 1;
        totalCount.setText(Integer.toString(totalTasbeehCountValue));
            if (thirtyThreeCounter > 1) {
                thirtyThreeCounter = thirtyThreeCounter - 1;
            } else if (thirtyThreeCounter == 1) {
                if (totalTasbeehCountValue > 33) {
                    thirtyThreeCounter = 33;//set default value
                } else {
                    thirtyThreeCounter = totalTasbeehCountValue;
                }
            }

            //***********************************/////////////////

            if (nintyNineCounter > 1) {
                nintyNineCounter = nintyNineCounter - 1;
            }
            else if (nintyNineCounter == 1) {
                if (totalTasbeehCountValue > 99) {
                    nintyNineCounter = 99;//set default value
                } else {
                    nintyNineCounter = totalTasbeehCountValue;
                }
            }
        //***********************************/////////////////
        //Now set value to counter
        int upToValue = Integer.parseInt(totalUptoCount.getText().toString());
        if (upToValue == 33)
            countValue.setText("" + thirtyThreeCounter);
        else
            countValue.setText("" + nintyNineCounter);

        }
    public void incrementTasbeeh() {

        totalTasbeehCountValue = totalTasbeehCountValue + 1;
        totalCount.setText(Integer.toString(totalTasbeehCountValue));
        if (thirtyThreeCounter < 33) {
            thirtyThreeCounter = thirtyThreeCounter + 1;
        } else if (thirtyThreeCounter == 33) {
            thirtyThreeCounter = 1;
        }

        if (nintyNineCounter < 99) {
            nintyNineCounter = nintyNineCounter + 1;
        } else if (nintyNineCounter == 99) {
            nintyNineCounter = 1;
        }

        if (totalUptoCount.getText().toString().equals("33")) {
            countValue.setText("" + thirtyThreeCounter);
        } else {
            countValue.setText("" + nintyNineCounter);
        }
    }

    public void initateToolBarItems() {
        imgBackBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        txtToolbarTitle = (TextView) findViewById(R.id.txt_toolbar);
        txtToolbarTitle.setText(getString(R.string.grid_quran));
        txtToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setTitleToolbar(getString(R.string.grid_tasbeeh));

    }

    public void setTitleToolbar(String titleText) {
        if (txtToolbarTitle != null) {
            txtToolbarTitle.setText(titleText);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_layout:
                break;
            case R.id.sound_mode_btn:
                if (soundMode) {
                    soundMode = false;
                    vibrateMode = true;
                    soundModeIcon.setImageResource(R.drawable.vibrate);
                } else if (!soundMode && vibrateMode) {
                    soundMode = false;
                    vibrateMode = false;
                    soundModeIcon.setImageResource(R.drawable.sound_off);
                } else if (!soundMode && !vibrateMode) {
                    soundMode = true;
                    vibrateMode = false;
                    soundModeIcon.setImageResource(R.drawable.sound_on);
                }
                break;
            case R.id.reset_btn:
                if(totalTasbeehCountValue > 0) {
                    AlertDialog.Builder confirmActionDialog = new AlertDialog.Builder(this);
                    confirmActionDialog.setTitle(getString(R.string.text_reset_title));
                    confirmActionDialog.setMessage(getString(R.string.text_rest_msg));
                    confirmActionDialog.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            counter = 0;
                            countValue.setText("0");
                            totalTasbeehCountValue = 0;
                            nintyNineCounter = 0;
                            thirtyThreeCounter = 0;
                            totalCount.setText("0");
                        }
                    });

                    confirmActionDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = confirmActionDialog.create();
                    alertDialog.show();
                }
                break;

            case R.id.countdown_mode_btn:
                if (numberTasbeehMenu.getText().toString().equals("33")) {
                    numberTasbeehMenu.setText("99");
                    totalUptoCount.setText("99");
                    countValue.setText("" + nintyNineCounter);
                } else if (numberTasbeehMenu.getText().toString().equals("99")) {
                    numberTasbeehMenu.setText("33");
                    totalUptoCount.setText("33");
                    countValue.setText("" + thirtyThreeCounter);
                }


                break;
        }
    }

    private int mod(int x, int y) {

        int result = x % y;
        if (result < 0) {
            result += y;
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (isTasbeehContianer) {
            saveTasbeehPref();
        } else {
            saveTasbeehDBValue(tasbeehId);

        }
        sharedPref.setSoundMode(soundMode);
        sharedPref.setVibrationMode(vibrateMode);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isTasbeehContianer) {
            saveTasbeehPref();
        } else {
            saveTasbeehDBValue(tasbeehId);

        }
        sharedPref.setSoundMode(soundMode);
        sharedPref.setVibrationMode(vibrateMode);
    }

    public void saveTasbeehPref() {
        sharedPref.saveTasbeehCountValue(Integer.valueOf(countValue.getText().toString()));
        sharedPref.setCountMode(Integer.valueOf(numberTasbeehMenu.getText().toString()));
        sharedPref.setSavedTotalTasbeehCount(Integer.valueOf(totalCount.getText().toString()));
    }

    public void getTasbeehPref() {

        counter = sharedPref.getSavedTasbeehCountValue();
        if (sharedPref.getSavedTotalReadTasbeehCount() != 0) {
            totalCount.setText(Integer.toString(sharedPref.getSavedTotalReadTasbeehCount()));
            totalTasbeehCountValue = sharedPref.getSavedTotalReadTasbeehCount();
        }
        if (sharedPref.getCountMode() != 0) {
            totalUptoCount.setText(Integer.toString(sharedPref.getCountMode()));
        }
        if (sharedPref.getCountMode() != 0) {
            numberTasbeehMenu.setText(Integer.toString(sharedPref.getCountMode()));
        }
        countValue.setText(Integer.toString(counter));

        //Save initial value
        if(counter > 33) {
            thirtyThreeCounter = counter;
            thirtyThreeCounter= mod(thirtyThreeCounter,33);
        }
        else
        {
            thirtyThreeCounter = counter;
        }
            nintyNineCounter=counter;
    }

    public void getTashbeehDataFromDatabase(int tasbeehId) {
        DBManager db = new DBManager(this);
        db.open();
        TasbeehModel tasbeehModel = db.getTasbeehUsingId(tasbeehId);
        db.close();
        if (tasbeehModel != null) {
            counter = tasbeehModel.getCount();
            totalCount.setText(Integer.toString(tasbeehModel.getTotal()));
            totalTasbeehCountValue = tasbeehModel.getTotal();
            totalUptoCount.setText(Integer.toString(tasbeehModel.getTotalCounterUpto()));
            numberTasbeehMenu.setText(Integer.toString(tasbeehModel.getTotalCounterUpto()));
            countValue.setText(Integer.toString(counter));

        }

        //Save initial value
        if(counter > 33) {
            thirtyThreeCounter = counter;
            thirtyThreeCounter = mod(thirtyThreeCounter,33);
        }
        else
        {
            thirtyThreeCounter = counter;
        }
        nintyNineCounter=counter;
    }

    public void saveTasbeehDBValue(int tasbeehId) {
        DBManager db = new DBManager(this);

        db.open();
        TasbeehModel tasbeehModel = db.getTasbeehUsingId(tasbeehId);
        if (tasbeehModel != null) {
            tasbeehModel.setTotalCounterUpto(Integer.parseInt(numberTasbeehMenu.getText().toString().trim()));
            tasbeehModel.setTotal(Integer.valueOf(totalCount.getText().toString()));
            tasbeehModel.setCount(Integer.valueOf(countValue.getText().toString()));
        }
        db.updateTasbeehUsingId(tasbeehModel);
        db.close();
    }


    public void initializeIndexList() {

        DBManager dbObj = new DBManager(this);
        dbObj.open();

        //   mTasbeehList = dbObj.getTasbeehList();
        mTasbeehList = dbObj.getTasbeehList1st();

        dbObj.close();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (isTasbeehContianer) {
                    isTasbeehContianer = false;
                    saveTasbeehPref();
                } else {
                    saveTasbeehDBValue(tasbeehId);//first save current value
                }
                if (position == 0) {
                    isTasbeehContianer = true;
                    getTasbeehPref();
                } else {
                    getTashbeehDataFromDatabase(position);//them nove to next or previous page
                    tasbeehId = position; //Change tasbeeh id)
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void showAnalytics(boolean isScreen, String eventNAme) {
        String screenName = "Dhikar detail";
        if (!isScreen) {
            CommunityGlobalClass.getInstance().sendAnalyticEvent(screenName, eventNAme);
        } else {
            CommunityGlobalClass.getInstance().sendAnalyticsScreen(screenName);
        }
    }
}
