package com.quranreading.qibladirection;


import com.quranreading.sharedPreference.AlarmSharedPref;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class AlarmNotificationActivity extends Activity implements SeekBar.OnSeekBarChangeListener, OnCompletionListener
{
	TextView tv, tvHead;
	int seekProgress;
	ImageView imgRedDot;
	SeekBar seekControl;
	AudioManager audioManager;
	static int currentVol;
	MediaPlayer mp = new MediaPlayer();
	String[] message = {"Fajar Namaz Time", "Zuhur Namaz Time", "Asar Namaz Time", "Maghrib Namaz Time", "Isha Namaz Time", "Sunrise"};
	
	AlarmSharedPref alarmObj;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		finish();
		/*getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);  
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON); 
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int height = dm.heightPixels;
		int width = dm.widthPixels;

		if ((width == 720 && height == 1280)) 
		{ 
			// Samsung S3
			setContentView(R.layout.notification_activity_s3);
		} 
		else 
		{
			setContentView(R.layout.notification_activity);
		}
		
		Locale.setDefault(Locale.US);
		
		alarmObj = new AlarmSharedPref(this);
		
		int tone = alarmObj.getTone();
		Boolean silent = alarmObj.getSilentMode();
		
		tvHead =(TextView)findViewById(R.id.txt_header);
		tv =(TextView)findViewById(R.id.txt_msg);
		
		int id = getIntent().getIntExtra("ID", 1);
		
		tv.setText(message[id-1]);
		
		if(id == 6)
		{
			tvHead.setText("Alarm");
		}
		
		audioManager = (AudioManager) getSystemService(AlarmNotification.AUDIO_SERVICE);
		currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		String uriAudio =  "alarm_" + tone;
		
		int resrcAudio = getResources().getIdentifier(uriAudio, "raw", getPackageName());
			
		if(resrcAudio > 0)
		{
			mp = MediaPlayer.create(this, resrcAudio);
			mp.setOnCompletionListener(this);
		}
		
		mp = MediaPlayer.create(this, R.raw.alarm_1);
		mp.setOnCompletionListener(this);
		
		if(mp != null && !silent)
		{
			mp.start();
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, AudioManager.FLAG_PLAY_SOUND);
		}
		
		seekControl = (SeekBar) findViewById(R.id.seek_bar);
		imgRedDot = (ImageView) findViewById(R.id.img_reddot);
		imgRedDot.setVisibility(View.GONE);
		
		seekControl.setOnSeekBarChangeListener(this);
		
		seekProgress = seekControl.getProgress();*/
	}
	
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) 
	{
		/*if(progress <= seekProgress)
		{
			seekControl.setProgress(seekProgress);
		}
		
		if(progress >= 95)
		{
			mp.stop_r();
			finish();
		}*/
    }

    public void onStartTrackingTouch(SeekBar seekBar) 
    {
    	/*imgRedDot.setVisibility(View.VISIBLE);*/
    }

    public void onStopTrackingTouch(SeekBar seekBar) 
    {
    	/*int progress = seekBar.getProgress();
    	if(progress <= seekProgress)
		{
			seekControl.setProgress(seekProgress);
			imgRedDot.setVisibility(View.GONE);
		}
		
		if(progress < 95)
		{
			seekControl.setProgress(seekProgress);
			imgRedDot.setVisibility(View.GONE);
		}*/
		
		/*if(progress >= 98)
		{
			finish();
		}*/	
    }

	@Override
	public void onCompletion(MediaPlayer mp) 
	{
		// TODO Auto-generated methodIndex stub
		//mp.start();
	}
	
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	    if (keyCode == KeyEvent.KEYCODE_BACK ) 
	    {
	    	 return false;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() 
	{
		// TODO Auto-generated methodIndex stub
		super.onPause();
		mp.release();
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVol, 0);
	}*/
}
