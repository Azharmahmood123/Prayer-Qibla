package com.quranreading.qibladirection;

import android.app.Activity;

public class SettingSoundsActivityOld extends Activity/* implements OnCompletionListener, OnErrorListener */{


		public static final String EXTRA_ADHAN_INDEX = "adhan_sound";
//
//	MediaPlayer mp = new MediaPlayer();
//	int value = 0;
//	int indexSoundOption = 0;
//
//	private boolean chkPlay1 = false, chkPlay2 = false, chkPlay3 = false;
//	private ImageView btnPlay1, btnPlay2, btnPlay3;
//	private ImageView[] btnsAdhanSound = new ImageView[5];
//
//	private static final int btn_Default_tone = 0;
//	private static final int btn_Silent = 1;
//	private static final int btn_Adhan_1 = 2;
//	private static final int btn_Adhan_2 = 3;
//	private static final int btn_Adhan_3 = 4;
//	private static final int btn_play_1 = 5;
//	private static final int btn_play_2 = 6;
//	private static final int btn_play3 = 7;
//
//	private AlarmSharedPref alarmObj;
//
//	View viewSoundSettings;
//	private boolean inProcess = false;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated methodIndex stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.transperant_layout);
//		alarmObj = new AlarmSharedPref(this);
//		initializeViews();
//		initializeSettings();
//		showSoundSettingsDailog();
//	}
//
//	private void initializeSettings() {
//
//		indexSoundOption = alarmObj.getAlarmOptionIndex();
//		adjustSoundViews();
//	}
//
//	private void initializeViews() {
//		viewSoundSettings = getLayoutInflater().inflate(R.layout.activity_settings_sound, null);
//
//		btnsAdhanSound[btn_Default_tone] = (ImageView) viewSoundSettings.findViewById(R.id.img_default_tone);
//		btnsAdhanSound[btn_Silent] = (ImageView) viewSoundSettings.findViewById(R.id.img_silent);
//		btnsAdhanSound[btn_Adhan_1] = (ImageView) viewSoundSettings.findViewById(R.id.img_adhan_1_opt);
//		btnsAdhanSound[btn_Adhan_2] = (ImageView) viewSoundSettings.findViewById(R.id.img_adhan_2_opt);
//		btnsAdhanSound[btn_Adhan_3] = (ImageView) viewSoundSettings.findViewById(R.id.img_adhan_3_opt);
//		btnPlay1 = (ImageView) viewSoundSettings.findViewById(R.id.img_adhan_1_play);
//		btnPlay2 = (ImageView) viewSoundSettings.findViewById(R.id.img_adhan_2_play);
//		btnPlay3 = (ImageView) viewSoundSettings.findViewById(R.id.img_adhan_3_play);
//	}
//
//	private void adjustSoundViews() {
//		for (int i = 0; i < btnsAdhanSound.length; i++)
//		{
//			btnsAdhanSound[i].setImageResource(R.drawable.radio);
//		}
//		btnsAdhanSound[indexSoundOption].setImageResource(R.drawable.radio_selected);
//	}
//
//	public void onAlarmOptionsClick(View view) {
//		int index = Integer.parseInt(view.getTag().toString());
//		if(!((GlobalClass) getApplication()).isPurchase)
//		{
//			if(index < 3)
//			{
//				indexSoundOption = index;
//				adjustSoundViews();
//			}
//			else
//			{
//				if(!inProcess)
//				{
//					inProcess = true;
//					startActivity(new Intent(SettingSoundsActivityOld.this, UpgradeActivity.class));
//				}
//			}
//		}
//		else
//		{
//			indexSoundOption = index;
//			adjustSoundViews();
//		}
//	}
//
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated methodIndex stub
//		super.onResume();
//		inProcess = false;
//	}
//
//	public void onPlayClick(View view) {
//		Integer btnClick = Integer.parseInt(view.getTag().toString());
//
//		switch (btnClick)
//		{
//		case btn_play_1:
//		{
//			if(chkPlay2)
//			{
//				chkPlay2 = false;
//				btnPlay2.setImageResource(R.drawable.btn_play);
//			}
//
//			if(chkPlay3)
//			{
//				chkPlay3 = false;
//				btnPlay3.setImageResource(R.drawable.btn_play);
//			}
//
//			if(!chkPlay1)
//			{
//				chkPlay1 = true;
//				btnPlay1.setImageResource(R.drawable.btn_stop);
//
//				value = 0;
//				resetAudios();
//			}
//			else
//			{
//				value = -1;
//				resetAudios();
//
//				chkPlay1 = false;
//				btnPlay1.setImageResource(R.drawable.btn_play);
//			}
//		}
//			break;
//		case btn_play_2:
//		{
//			if(chkPlay1)
//			{
//				chkPlay1 = false;
//				btnPlay1.setImageResource(R.drawable.btn_play);
//			}
//
//			if(chkPlay3)
//			{
//				chkPlay3 = false;
//				btnPlay3.setImageResource(R.drawable.btn_play);
//			}
//
//			if(!chkPlay2)
//			{
//				value = 1;
//				resetAudios();
//
//				chkPlay2 = true;
//				btnPlay2.setImageResource(R.drawable.btn_stop);
//			}
//			else
//			{
//				value = -1;
//				resetAudios();
//
//				chkPlay2 = false;
//				btnPlay2.setImageResource(R.drawable.btn_play);
//			}
//		}
//			break;
//		case btn_play3:
//		{
//			if(chkPlay1)
//			{
//				chkPlay1 = false;
//				btnPlay1.setImageResource(R.drawable.btn_play);
//			}
//
//			if(chkPlay2)
//			{
//				chkPlay2 = false;
//				btnPlay2.setImageResource(R.drawable.btn_play);
//			}
//
//			if(!chkPlay3)
//			{
//				value = 2;
//				resetAudios();
//
//				chkPlay3 = true;
//				btnPlay3.setImageResource(R.drawable.btn_stop);
//			}
//			else
//			{
//				value = -1;
//				resetAudios();
//
//				chkPlay3 = false;
//				btnPlay3.setImageResource(R.drawable.btn_play);
//			}
//		}
//			break;
//
//		default:
//		{
//			break;
//		}
//
//		}
//
//	}
//
//	public void initializeAudios(int pos) {
//
//		String uriAudio = "azan_" + (pos);
//
//		int resrcAudio = getResources().getIdentifier(uriAudio, "raw", getPackageName());
//		if(mp != null)
//		{
//			mp.release();
//		}
//
//		if(resrcAudio > 0)
//		{
//			mp = MediaPlayer.create(this, resrcAudio);
//			mp.setOnCompletionListener(this);
//			mp.setOnErrorListener(this);
//		}
//	}
//
//	private void resetAudios() {
//		try
//		{
//			if(mp != null)
//			{
//				if(mp.isPlaying())
//				{
//					mp.pause();
//					mp.seekTo(0);
//				}
//			}
//
//			if(value != -1)
//			{
//				initializeAudios(value + 1);
//				mp.start();
//			}
//			else
//			{
//				chkPlay1 = false;
//				btnPlay1.setImageResource(R.drawable.btn_play);
//				chkPlay2 = false;
//				btnPlay2.setImageResource(R.drawable.btn_play);
//				chkPlay3 = false;
//				btnPlay3.setImageResource(R.drawable.btn_play);
//
//			}
//		}
//		catch (IllegalStateException e)
//		{
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void onCompletion(MediaPlayer mp) {
//		// TODO Auto-generated methodIndex stub
//		if(chkPlay1)
//		{
//			chkPlay1 = false;
//			btnPlay1.setImageResource(R.drawable.btn_play);
//		}
//
//		if(chkPlay2)
//		{
//			chkPlay2 = false;
//			btnPlay2.setImageResource(R.drawable.btn_play);
//		}
//
//		if(chkPlay3)
//		{
//			chkPlay3 = false;
//			btnPlay3.setImageResource(R.drawable.btn_play);
//		}
//	}
//
//	@Override
//	public boolean onError(MediaPlayer mp1, int what, int extra) {
//		// TODO Auto-generated methodIndex stub
//		boolean result = false;
//
//		try
//		{
//			if(mp != null)
//			{
//				mp.stop_r();
//				mp.reset();
//			}
//
//			String uriAudio = "azan_" + (value + 1);
//
//			int resrcAudio = getResources().getIdentifier(uriAudio, "raw", getPackageName());
//
//			if(resrcAudio > 0)
//			{
//				mp = MediaPlayer.create(this, resrcAudio);
//				mp.setOnCompletionListener(this);
//				mp.setOnErrorListener(this);
//
//				mp.start();
//				result = true;
//			}
//			else
//			{
//				result = false;
//			}
//		}
//		catch (Exception e)
//		{
//			result = false;
//		}
//
//		return result;
//	}
//
//	@Override
//	protected void onPause() {
//		// TODO Auto-generated methodIndex stub
//		super.onPause();
//		value = -1;
//		resetAudios();
//	}
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated methodIndex stub
//		super.onDestroy();
//		if(mp != null)
//		{
//			mp.release();
//		}
//	}
//
//	private void showSoundSettingsDailog() {
//		AlertDialog alertDialog = null;
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		// .setTitle(R.string.tone_settings);
//		builder.setOnCancelListener(new OnCancelListener() {
//
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				finish();
//			}
//		});
//
//		builder.setPositiveButton(R.string.okay, new OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//
//				sendAnalyticEvent(indexSoundOption);
//				alarmObj.setAlarmOptionIndex(indexSoundOption);
//				Intent resultIntent = new Intent();
//				resultIntent.putExtra(EXTRA_ADHAN_INDEX, indexSoundOption);
//				setResult(RESULT_OK, resultIntent);
//				finish();
//			}
//		});
//		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				finish();
//			}
//		});
//
//		alertDialog = builder.create();
//		alertDialog.setView(viewSoundSettings);
//		alertDialog.show();
//	}
//
//	private void sendAnalyticEvent(int indexSoundOption) {
//		String[] arrEvents = { "Adhan Default", "Adhan Silent", "Adhan 1", "Adhan 2", "Adhan 3" };
//		AnalyticSingaltonClass.getInstance(this).sendEventAnalytics("Settings-Qibla", arrEvents[indexSoundOption]);
//	}
}
