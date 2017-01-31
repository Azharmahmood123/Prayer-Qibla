package com.quranreading.qibladirection;

import java.util.Locale;

import com.quranreading.sharedPreference.LanguagePref;
import com.quranreading.sharedPreference.QiblaDirectionPref;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import quran.activities.ServiceClass;

public class GlobalClass extends Application {

	public boolean deviceS3 = false;
	public boolean showAdRemoveDialog = true;
	// public boolean showPrayerSettingDialog = false;
	public boolean chkAsianCountry = false;
	public int providerEnabled = 0;
	public String searchCity = "";
	public boolean isPurchase = false;
	public QiblaDirectionPref purchasePref = null;
	public LanguagePref languagePref = null;
	public boolean isSettingsAlertShown = false;

	public Typeface faceRobotoL, faceRobotoB, faceRobotoR;

	@Override
	public void onCreate() {
		// TODO Auto-generated methodIndex stub
		super.onCreate();
		setTypeFace();

		purchasePref = new QiblaDirectionPref(this);
		isPurchase = purchasePref.getPurchased();
		languagePref = new LanguagePref(this);

		setLocale(languagePref.getLanguage());
	}

	// private void setDefaultLocale() {
	// String[] countryCodes = { "en", "tr", "es", "ru", "fr", "de", "ms", "in", "it", "th" };
	//
	// ArrayList<String> countryCodesList = new ArrayList<String>();
	// for (int index = 0; index < countryCodes.length; index++)
	// {
	// countryCodesList.add(countryCodes[index]);
	// }
	//
	// String defaultLanguage = Locale.getDefault().getLanguage();
	// TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	// String countryCode = tm.getSimCountryIso();
	//
	// if(countryCode != null)
	// {
	// if(countryCode.isEmpty())
	// {
	// }
	// else
	// {
	// defaultLanguage = countryCode;
	// }
	// }
	//
	// if(countryCodesList.contains(defaultLanguage))
	// {
	// setLocale(countryCodesList.indexOf(defaultLanguage));
	// }
	// else
	// {
	// setLocale(languagePref.getLanguage());
	// }
	// }

	public void setLocale(int languageIndex) {
		String[] countryCodes = { "en", "tr", "es", "ru", "fr", "de", "ms", "in", "it", "th" };
		Locale myLocale = new Locale(countryCodes[languageIndex]);
		Resources res = getApplicationContext().getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
		languagePref.setLanguage(languageIndex);
	}

	//////////////////////////////////////////////////
	///////////////// Quran Data////////////////
	//////////////////////////////////////////////////

	public static final String ARABIC_FONT = "PDMS_Saleem_QuranFont-signed.ttf";
	public static final String ARABIC_FONT1 = "PDMS_Saleem_ACQuranFont_shipped.ttf";
	public static final String ARABIC_FONT2 = "trado.ttf";
	public static final String ARABIC_FONT3 = "XBZarIndoPak.ttf";
	// public static final String ARABIC_FONT3 = "me_quran.ttf";
	public static final String ARABIC_FONT4 = "noorehira.ttf";

	public boolean isTransliteration = false;
	public int ayahPos = 0;
	public boolean isTranslation = false;
	public boolean showTransliteration = false;
	public boolean isAutoPlay = true;

	public int font_size_arabic, font_size_eng, ayahPadding = 40;
	public final int fontSize_A_small[] = { 28, 30, 33, 36, 38, 42 };
	public final int fontSize_A_small_1[] = { 34, 36, 38, 40, 42, 44 };
	public final int fontSize_E_small[] = { 16, 18, 21, 24, 26, 30 };

	public final int fontSize_A_med[] = { 40, 45, 50, 56, 64, 70 };
	public final int fontSize_A_med_1[] = { 58, 61, 64, 67, 70, 73 };
	public final int fontSize_E_med[] = { 28, 30, 32, 36, 40, 46 };

	public final int fontSize_A_large[] = { 50, 54, 58, 62, 66, 72 };
	public final int fontSize_A_large_1[] = { 60, 64, 68, 72, 76, 80 };
	public final int fontSize_E_large[] = { 30, 32, 36, 40, 46, 52 };

	public Typeface faceArabic, faceArabic1, faceArabic2, faceArabic3, faceArabic4;

	public final int time_Reciter_1[] = { 0, 4030, 8000, 11500, 14800, 18900, 23900, 34400, 41150, 52500, 65200, 76200, 90300, 104300, 115000, 129000, 144400, 151900, 157000, 173550, 186000, 197000, 204000, 211500, 226400, 232300, 238000, 244800, 251000, 271000, 279200, 292400, 302000, 309200,
			323500, 334500, 343400, 359000, 368500, 379000, 387400, 405500, 413500, 419000, 427100, 432700, 441500, 451500, 476500, 483500, 492000, 499300, 508200, 520000, 530000, 539000, 545500, 552800, 558800, 563700, 569000, 581000, 586500, 594700, 602000, 609000, 622000, 632500, 644000, 652000,
			662800, 673800, 684400, 691500, 697500, 704500, 711000, 720000, 730200, 738100, 748300, 759100, 772500, 782000, 800000 };

	public final int time_Reciter_2[] = { 0, 6400, 10600, 15500, 21400, 27400, 34000, 47800, 57000, 72300, 87700, 100800, 120300, 137500, 149200, 171400, 191200, 201700, 209500, 230000, 246000, 258500, 269000, 279500, 299000, 309000, 319000, 328800, 336800, 357000, 368500, 384000, 397000, 406000,
			420000, 434500, 445000, 461500, 473500, 484000, 494000, 514000, 524300, 532500, 543000, 552000, 563500, 576000, 606500, 616500, 629000, 641000, 653000, 670000, 683500, 698200, 710000, 721000, 731000, 739000, 746000, 766000, 775000, 787000, 796000, 804000, 821500, 835800, 850000, 860500,
			875300, 886000, 903000, 913000, 921000, 932000, 942000, 954000, 969000, 982000, 996000, 1010000, 1028500, 1045500, 1066500 };

	private void setTypeFace() {

		String Font_Roboto_Light = "Roboto_Light.ttf";
		String Font_Roboto_Bold = "Roboto_Bold.ttf";
		String Font_Roboto_Regular = "Roboto_Regular.ttf";

		faceRobotoL = Typeface.createFromAsset(getAssets(), Font_Roboto_Light);
		faceRobotoB = Typeface.createFromAsset(getAssets(), Font_Roboto_Bold);
		faceRobotoR = Typeface.createFromAsset(getAssets(), Font_Roboto_Regular);
		faceArabic = Typeface.createFromAsset(getAssets(), ARABIC_FONT3);
		faceArabic1 = Typeface.createFromAsset(getAssets(), ARABIC_FONT1);
		faceArabic2 = Typeface.createFromAsset(getAssets(), ARABIC_FONT2);
		faceArabic3 = Typeface.createFromAsset(getAssets(), ARABIC_FONT3);
		faceArabic4 = Typeface.createFromAsset(getAssets(), ARABIC_FONT4);
	}

	public boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
		{
			if(ServiceClass.class.getName().equals(service.service.getClassName()))
			{
				return true;
			}
		}

		return false;
	}
}
