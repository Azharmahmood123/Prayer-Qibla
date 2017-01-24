package com.quranreading.helper;

import android.os.Environment;

import java.io.File;

public class Constants {

	public static final String BroadcastActionDownload = "download";

	public static final String BroadcastActionComplete = "download_complete";

	public static final String VERSION_NUMBER = " 4.0";
	public static final String WEB_URL = "www.QuranReading.com";
	public static final int DRAWER_LIST_LENGTH = 12;
	public static final String extMp3 = ".mp3";

	public static final String QuranFile1 = "quran_translation_basit.zip";
	public static final String QuranFile2 = "quran_a.zip";

	public static final String DUAS_ZIP = "duas.zip";
	public static final String DUAS_ZIP_TEMP = "temp_duas.zip";

	public static final String NAMES_ZIP = "names.zip";
	public static final String NAMES_ZIP_TEMP = "temp_names.zip";

	public static final String rootFolderQuran = "QiblaConnect/Quran/";
	public static final File rootPathQuran = new File(Environment.getExternalStorageDirectory(), rootFolderQuran);
	public static final String rootFolderDuas = "QiblaConnect/Duas/";
	public static final File rootPathDuas = new File(Environment.getExternalStorageDirectory(), rootFolderDuas);
	public static final String rootFolderNames = "QiblaConnect/Names/";
	public static final File rootPathNames = new File(Environment.getExternalStorageDirectory(), rootFolderNames);

	public static final String URL_REFFERER = "&referrer=utm_source%3DQibla";
	public static final String BANNER_IMAGE_URL = "market://details?id=com.QuranReading.qurannow";

	public static final int ARABIC_TEXT_SIZE_INDEX = 1;
	public static final int CITIES_LENGTH_LIMIT = 65;

	public static final int TABS_COUNT = 5;

}
