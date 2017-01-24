package com.quranreading.alarms;

import java.util.ArrayList;
import java.util.Calendar;

import org.xmlpull.v1.XmlPullParser;

import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.qibladirection.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import quran.activities.SurahActivity;
import quran.adapter.XMLParser;
import quran.sharedpreference.SurahsSharedPref;

public class AlarmReceiverAyah extends BroadcastReceiver {

	public static final int NOTIFY_AYAH_ALARM_ID = 10;
	public static final int HOUR = 18;//24 hours format is used here as Ayah Notification is set by user through Time Picker in Settings Screen
	public static final int MINT = 0;
	// public static final String AM = "am";
	// public static final String PM = "pm";

	private static final int LANGUAGE_Off = 0;
	private static final int LANGUAGE_English_Saheeh = 1;
	private static final int LANGUAGE_English_Pickthal = 2;
	private static final int LANGUAGE_English_Shakir = 3;
	private static final int LANGUAGE_English_Maududi = 4;
	private static final int LANGUAGE_English_Daryabadi = 5;
	private static final int LANGUAGE_English_YusufAli = 6;
	private static final int LANGUAGE_Urdu = 7;
	private static final int LANGUAGE_Spanish = 8;
	private static final int LANGUAGE_French = 9;
	private static final int LANGUAGE_Chinese = 10;
	private static final int LANGUAGE_Persian = 11;
	private static final int LANGUAGE_Italian = 12;
	private static final int LANGUAGE_Dutch = 13;
	private static final int LANGUAGE_Indonesian = 14;
	private static final int LANGUAGE_Melayu = 15;
	private static final int LANGUAGE_Hindi = 16;
	private static final int LANGUAGE_Bangla = 17;
	private static final int LANGUAGE_TURKISH = 18;

	int surahPos;
	int ayaNo;
	Context context;
	SurahsSharedPref settngPref;
	ArrayList<String> surahSizeList = new ArrayList<String>();
	private XmlPullParser xpp;
	private String bismillahText;
	private CharSequence message;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		settngPref = new SurahsSharedPref(context);

		AnalyticSingaltonClass.getInstance(context).sendEventAnalytics("Notification", "AyahOfTheDayTriggered");

		randInt();
		randAyaNo();

		sendNotification();
	}

	private void randAyaNo() {
		setNotificationLanguage(settngPref.getTranslationIndex());
	}

	public void randInt() {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		int min = 1, max = 114;

		if(day == Calendar.FRIDAY)
		{
			surahPos = 18;
			Log.v("Day", "Friday");
		}
		else
		{
			surahPos = (int) (min + (Math.random() * max));
		}
	}

	private void sendNotification() {

		String name = getSurahName();

		Intent resultIntent = new Intent(context, AyahNotificationActivity.class);
		resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		resultIntent.putExtra(SurahActivity.KEY_EXTRA_SURAH_NO, surahPos);
		resultIntent.putExtra(SurahActivity.KEY_EXTRA_AYAH_NO, ayaNo);

		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher).setLargeIcon(bm).setAutoCancel(true).setContentTitle(name).setContentText(message);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// context ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(AyahNotificationActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(resultPendingIntent);

		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mBuilder.setSound(uri);
		mNotificationManager.notify(NOTIFY_AYAH_ALARM_ID, mBuilder.build());
	}

	// public void showNamazNotification() {
	//
	// String name = getSurahName();
	//
	// Intent intnt = new Intent(context, AyahNotificationActivity.class);
	// intnt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	// intnt.putExtra(SurahActivity.KEY_EXTRA_SURAH_NO, surahPos);
	// intnt.putExtra(SurahActivity.KEY_EXTRA_AYAH_NO, ayaNo);
	//
	// PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFY_AYAH_ALARM_ID, intnt, PendingIntent.FLAG_CANCEL_CURRENT);
	//
	// NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	// Notification notif = new Notification(R.drawable.ic_launcher, name, System.currentTimeMillis());
	//
	// CharSequence from = name;
	//
	// notif.setLatestEventInfo(context, from, message, pendingIntent);
	//
	// notif.flags |= Notification.FLAG_AUTO_CANCEL;
	// notif.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
	//
	// nm.notify(NOTIFY_AYAH_ALARM_ID, notif);
	// }

	public String getSurahName() {
		String name = "";
		String[] namesArray = {};

		int resrcNamesArray;

		resrcNamesArray = context.getResources().getIdentifier("surah_names", "array", context.getPackageName());

		if(resrcNamesArray > 0)
		{
			namesArray = context.getResources().getStringArray(resrcNamesArray);
			name = "Surah " + namesArray[surahPos - 1];
		}

		return name;
	}

	private void setNotificationLanguage(int translationLanguage) {

		switch (translationLanguage)
		{

		case LANGUAGE_Off:
		{
			xpp = context.getResources().getXml(R.xml.eng_translation_pickthal);
			bismillahText = context.getResources().getString(R.string.bismillahTextEngPickthal);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		case LANGUAGE_English_Saheeh:
		{
			xpp = context.getResources().getXml(R.xml.english_translation);
			bismillahText = context.getResources().getString(R.string.bismillahTextEngSaheeh);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		case LANGUAGE_English_Pickthal:
		{
			xpp = context.getResources().getXml(R.xml.eng_translation_pickthal);
			bismillahText = context.getResources().getString(R.string.bismillahTextEngPickthal);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		case LANGUAGE_English_Shakir:
		{
			xpp = context.getResources().getXml(R.xml.eng_translation_shakir);
			bismillahText = context.getResources().getString(R.string.bismillahTextEngShakir);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		case LANGUAGE_English_Maududi:
		{
			xpp = context.getResources().getXml(R.xml.eng_translation_maududi);
			bismillahText = context.getResources().getString(R.string.bismillahTextEngMadudi);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		case LANGUAGE_English_Daryabadi:
		{
			xpp = context.getResources().getXml(R.xml.eng_translation_daryabadi);
			bismillahText = context.getResources().getString(R.string.bismillahTextEngDarayabadi);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		case LANGUAGE_English_YusufAli:
		{
			xpp = context.getResources().getXml(R.xml.eng_translation_yusufali);
			bismillahText = context.getResources().getString(R.string.bismillahTextEngYusaf);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		case LANGUAGE_Urdu:
		{
			xpp = context.getResources().getXml(R.xml.urdu_translation_jhalandhry);
			bismillahText = context.getResources().getString(R.string.bismillahTextUrdu);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day_urdu) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		case LANGUAGE_Spanish:
		{
			xpp = context.getResources().getXml(R.xml.spanish_cortes_trans);
			bismillahText = context.getResources().getString(R.string.bismillahTextSpanishCortes);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day_spanish) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		case LANGUAGE_French:
		{
			xpp = context.getResources().getXml(R.xml.french_trans);
			bismillahText = context.getResources().getString(R.string.bismillahTextFrench);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day_french) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		case LANGUAGE_Chinese:
		{
			xpp = context.getResources().getXml(R.xml.chinese_trans);
			bismillahText = context.getResources().getString(R.string.bismillahTextChinese);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day_chinese) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		case LANGUAGE_Persian:
		{
			xpp = context.getResources().getXml(R.xml.persian_ghoomshei_trans);
			bismillahText = context.getResources().getString(R.string.bismillahTextPersianGhommshei);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day_persian) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		case LANGUAGE_Italian:
		{
			xpp = context.getResources().getXml(R.xml.italian_trans);
			bismillahText = context.getResources().getString(R.string.bismillahTextItalian);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day_italian) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		case LANGUAGE_Dutch:
		{
			xpp = context.getResources().getXml(R.xml.dutch_trans_keyzer);
			bismillahText = context.getResources().getString(R.string.bismillahTextDutchKeyzer);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day_dutch) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		case LANGUAGE_Indonesian:
		{
			xpp = context.getResources().getXml(R.xml.indonesian_bhasha_trans);
			bismillahText = context.getResources().getString(R.string.bismillahTextIndonesianBahasha);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day_indonesian) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		case LANGUAGE_Melayu:
		{
			xpp = context.getResources().getXml(R.xml.malay_basmeih);
			bismillahText = context.getResources().getString(R.string.bismillahTextIndonesianBahasha);

			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day_malay) + " - " + surahSizeList.get(ayaNo).toString();

		}
			break;

		case LANGUAGE_Hindi:
		{
			xpp = context.getResources().getXml(R.xml.hindi_suhel_farooq_khan_and_saifur_rahman_nadwi);
			bismillahText = context.getResources().getString(R.string.bismillahTextIndonesianBahasha);

			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day_hindi) + " - " + surahSizeList.get(ayaNo).toString();

		}
			break;

		case LANGUAGE_Bangla:
		{
			xpp = context.getResources().getXml(R.xml.bangali_zohurul_hoque);
			bismillahText = context.getResources().getString(R.string.bismillahTextIndonesianBahasha);

			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day_bengali) + " - " + surahSizeList.get(ayaNo).toString();

		}
			break;

		case LANGUAGE_TURKISH:
		{
			xpp = context.getResources().getXml(R.xml.turkish_diyanet_isleri);
			bismillahText = context.getResources().getString(R.string.bismillahTextTurkish);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day_turkish) + " - " + surahSizeList.get(ayaNo).toString();
		}
			break;

		default:
		{
			xpp = context.getResources().getXml(R.xml.eng_translation_pickthal);
			bismillahText = context.getResources().getString(R.string.bismillahTextEngPickthal);
			surahSizeList = XMLParser.getTranslatedAyaList(context, xpp, surahPos, bismillahText);
			int min = 1, max = surahSizeList.size() - 1;
			// Log.d("Random No", "Generate");
			ayaNo = (int) (min + (Math.random() * max));
			message = context.getResources().getString(R.string.aya_of_day) + " - " + surahSizeList.get(ayaNo).toString();
		}
		}

	}
}