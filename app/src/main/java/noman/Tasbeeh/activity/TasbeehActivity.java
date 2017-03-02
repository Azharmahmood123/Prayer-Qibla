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
import noman.Tasbeeh.SharedPref;
import noman.Tasbeeh.adapter.CustomViewPagerAdapter;
import noman.Tasbeeh.model.TasbeehModel;


/**
 * Created by Ninesol on 12/9/2016.
 */

public class TasbeehActivity extends AdIntegration implements View.OnClickListener {

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

    private boolean convertCheck = false;
    private boolean firstHalf = false;
    private boolean secondHalf = false;
    private boolean soundMode = true;
    private boolean vibrateMode = false;

    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayerForBackward;

    private Vibrator vibrator;

    private int zeroCheck;
    private int totalTasbeehCountValue = 0;
    private int counter = 0;
    private int currentCounterValue = 0;


    boolean isTasbeehContianer = false;
    ViewPager viewPager;
    int tasbeehId = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
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
        sharedPref = new SharedPref(this);


        isTasbeehContianer = getIntent().getExtras().getBoolean("isTasbeeh", false);
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

        mediaPlayer = MediaPlayer.create(TasbeehActivity.this, R.raw.tasbeeh_test);
        mediaPlayerForBackward = MediaPlayer.create(TasbeehActivity.this, R.raw.tasbeeh_backward);

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
            viewPager.setCurrentItem(tasbeehId - 1);
        }


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
                totalTasbeehCountValue = totalTasbeehCountValue + 1;
                totalCount.setText(Integer.toString(totalTasbeehCountValue));
                currentCounterValue = Integer.parseInt(countValue.getText().toString());
                if (totalUptoCount.getText().toString().equals("33")) {
                    if (currentCounterValue == 33) {
                        countValue.setText("0");
                        counter = 0;
                        counter = counter + 1;
                        currentCounterValue = currentCounterValue + 1;
                        countValue.setText(Integer.toString(counter));
                    } else {
                        counter = counter + 1;
                        currentCounterValue = currentCounterValue + 1;
                        countValue.setText(Integer.toString(counter));
                    }
                } else if (totalUptoCount.getText().toString().equals("99")) {
                    if (currentCounterValue == 99) {
                        countValue.setText("0");
                        counter = 0;
                        counter = counter + 1;
                        currentCounterValue = currentCounterValue + 1;
                        countValue.setText(Integer.toString(counter));
                    } else {
                        counter = counter + 1;
                        currentCounterValue = currentCounterValue + 1;
                        countValue.setText(Integer.toString(counter));
                    }
                }
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
                    } else if (!soundMode && !vibrateMode) {

                    }
                    currentCounterValue = Integer.parseInt(countValue.getText().toString());
                    totalTasbeehCountValue = totalTasbeehCountValue + 1;
                    totalCount.setText(Integer.toString(totalTasbeehCountValue));
                    if (currentCounterValue == 33) {
                        countValue.setText("0");
                        counter = 0;
                        counter = counter + 1;
                        currentCounterValue = currentCounterValue + 1;
                        countValue.setText(Integer.toString(counter));
                    } else {
                        counter = counter + 1;
                        currentCounterValue = currentCounterValue + 1;
                        countValue.setText(Integer.toString(counter));
                    }
                } else if (deltaX < 0) {
                    tasbeeh.setBackground(getResources().getDrawable(R.drawable.tasbeeh_decrement_animation));
                    frameAnimation = (AnimationDrawable) tasbeeh.getBackground();
                    frameAnimation.start();
                    if (soundMode) {
                        mediaPlayerForBackward.start();
                    } else if (!soundMode && vibrateMode) {
                        vibrator.vibrate(100);
                    } else if (!soundMode && !vibrateMode) {

                    }
                    zeroCheck = Integer.parseInt(countValue.getText().toString());
                    if (zeroCheck == 0) {
                        if (totalTasbeehCountValue > 0) {
                            if (Integer.valueOf(totalUptoCount.getText().toString()) == 33) {
                                counter = 34;
                            } else {
                                counter = 100;
                            }
                            counter = counter - 1;
                            currentCounterValue = currentCounterValue - 1;
                            countValue.setText(Integer.toString(counter));
                        }
                    } else {
                        counter = counter - 1;
                        countValue.setText(Integer.toString(counter));
                        totalTasbeehCountValue = totalTasbeehCountValue - 1;
                        totalCount.setText(Integer.toString(totalTasbeehCountValue));
                    }
                }
                return true;
            }
        });

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
                AlertDialog.Builder confirmActionDialog = new AlertDialog.Builder(this);
                confirmActionDialog.setTitle("Confirm Action");
                confirmActionDialog.setMessage("Are you sure to reset the current/total counter? This cannot be undone.");
                confirmActionDialog.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        counter = 0;
                        countValue.setText("0");
                        totalTasbeehCountValue = 0;
                        totalCount.setText("0");
                    }
                });

                confirmActionDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = confirmActionDialog.create();
                alertDialog.show();
                break;
            case R.id.countdown_mode_btn:
                if (numberTasbeehMenu.getText().toString().equals("33")) {
                    numberTasbeehMenu.setText("99");
                    //sharedPref.setCountMode(99);
                    totalUptoCount.setText("99");
                    if (convertCheck) {
                        if (firstHalf) {
                            counter = counter + 33;
                            currentCounterValue = currentCounterValue + 33;
                            countValue.setText(Integer.toString(counter));
                        } else if (secondHalf) {
                            counter = counter + 66;
                            currentCounterValue = currentCounterValue + 66;
                            countValue.setText(Integer.toString(counter));
                        }
                    }
                } else if (numberTasbeehMenu.getText().toString().equals("99")) {
                    numberTasbeehMenu.setText("33");
                    //  sharedPref.setCountMode(33);
                    totalUptoCount.setText("33");
                    currentCounterValue = Integer.valueOf(countValue.getText().toString());
                    if (currentCounterValue <= 33) {

                    } else if (currentCounterValue > 33 && currentCounterValue <= 66) {
                        convertCheck = true;
                        firstHalf = true;
                        secondHalf = false;
                        counter = counter % 33;
                        currentCounterValue = currentCounterValue % 33;
                        countValue.setText(Integer.toString(counter));
                    } else if (currentCounterValue > 66) {
                        convertCheck = true;
                        firstHalf = false;
                        secondHalf = true;
                        counter = counter % 33;
                        currentCounterValue = currentCounterValue % 33;
                        countValue.setText(Integer.toString(counter));
                    }
                }
                break;
        }
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
        sharedPref.saveTasbeehCountValue(counter);
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
    }

    public void saveTasbeehDBValue(int tasbeehId) {
        DBManager db = new DBManager(this);
        db.open();
        TasbeehModel tasbeehModel = db.getTasbeehUsingId(tasbeehId);
        if (tasbeehModel != null) {
            tasbeehModel.setTotalCounterUpto(Integer.parseInt(numberTasbeehMenu.getText().toString().trim()));
            tasbeehModel.setTotal(Integer.valueOf(totalCount.getText().toString()));
            tasbeehModel.setCount(counter);
        }
        db.updateTasbeehUsingId(tasbeehModel, tasbeehId);
        db.close();
    }


    public void initializeIndexList() {

        DBManager dbObj = new DBManager(this);
        dbObj.open();
        mTasbeehList = dbObj.getTasbeehList();
        dbObj.close();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                saveTasbeehDBValue(tasbeehId);//first save current value
                getTashbeehDataFromDatabase(position + 1);//them nove to next or previous page
                tasbeehId = position + 1; //Change tasbeeh id
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


}
