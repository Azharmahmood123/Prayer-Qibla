package quran.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.quranreading.alarms.AlarmReceiverAyah;
import com.quranreading.qibladirection.R;

public class SurahsSharedPref {

    private Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;
    private SharedPreferences pref;
    private static final String PREF_NAME = "SettingsPref";

    private static final String LAST_READ = "lastRead";
    private static final String LAST_READ_SURAH = "lastReadSurah";

    private static final String RECITER = "reciter";

    // private static final String TRANSLATION_INDEX = "translation_index";
    // private static final String TRANSLITERATION = "transliteration";

    // Used to Lock translation and transliteration for premium version
    private static final String TRANSLATION_INDEX = "translation_index_premium";
    private static final String TRANSLITERATION = "transliteration_premium";

    private static final String REPEAT_SURAH = "repeat_surah";

    // used in Previous Version when It is ON by Default
    // private static final String AYAH_NOTIFICATION = "ayah_notification";

    // New Key used for By Default OFF
    private static final String AYAH_NOTIFICATION = "ayah_notification_new";

    private static final String AYAH_TIME_HOUR = "notification_hour";
    private static final String AYAH_TIME_MINT = "notification_mint";
    private static final String FONT_SIZE_ENG = "english_font_size";
    private static final String FONT_SIZE_ARABIC = "arabic_font_size";
    private static final String FONT_SIZE_SEEKBAR_POS = "seekbar_font_size_pos";
    private static final String LAST_TRANSLIRATION_STATE = "last_transliration_pos";

    private static final String LAST_TRANSALATION_POS = "last_translation_pos";
    private static final String READ_MODE_STATE = "read_mode_state";


    private static final String IS_FIRSTTIME_MENU_CLICK = "is_first_time_menu_click";

    private static final String IS_FIRSTTIME_QURAN_CLICK = "is_first_time_quran_click";
    private static final String IS_SECONDTIME_QURAN_CLICK = "is_second_time_quran_click";

    public SurahsSharedPref(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public boolean getIsFirstTimeQuranOpen() {
        return pref.getBoolean(IS_FIRSTTIME_QURAN_CLICK, true);
    }

    public void setIsFirstTimeQuranOpen(boolean size) {
        editor.putBoolean(IS_FIRSTTIME_QURAN_CLICK, size);
        editor.commit();
    }

    public boolean getIsSecondTimeQuranOpen() {
        return pref.getBoolean(IS_SECONDTIME_QURAN_CLICK, true);
    }

    public void setIsSecondTimeQuranOpen(boolean size) {
        editor.putBoolean(IS_SECONDTIME_QURAN_CLICK, size);
        editor.commit();
    }

    public boolean getIsFirstTimeMenuOpen() {
        return pref.getBoolean(IS_FIRSTTIME_MENU_CLICK, true);
    }

    public void setIsFirstTimeMenuOpen(boolean size) {
        editor.putBoolean(IS_FIRSTTIME_MENU_CLICK, size);
        editor.commit();
    }

    public boolean getLastTranslirationState() {
        return pref.getBoolean(LAST_TRANSLIRATION_STATE, true);
    }

    public void setLastTranslirationState(boolean size) {
        editor.putBoolean(LAST_TRANSLIRATION_STATE, size);
        editor.commit();
    }

    public boolean getReadModeState() {
        return pref.getBoolean(READ_MODE_STATE, false);
    }

    public void setReadModeState(boolean size) {
        editor.putBoolean(READ_MODE_STATE, size);
        editor.commit();
    }

    public int getLastSaveTransaltion() {
        return pref.getInt(LAST_TRANSALATION_POS, 0);
    }

    public void setLastSaveTransaltion(int size) {
        editor.putInt(LAST_TRANSALATION_POS, size);
        editor.commit();
    }

    // ****** Adding Font Sizes here ****************
    public int getEnglishFontSize() {
        return pref.getInt(FONT_SIZE_ENG, (int) _context.getResources().getDimension(R.dimen._8sdp));
    }

    public void setEnglishFontSize(int size) {
        editor.putInt(FONT_SIZE_ENG, size);
        editor.commit();
    }

    public int getArabicFontSize() {
        return pref.getInt(FONT_SIZE_ARABIC, (int) _context.getResources().getDimension(R.dimen._14sdp));
    }

    public void setArabicFontSize(int size) {
        editor.putInt(FONT_SIZE_ARABIC, size);
        editor.commit();
    }

    public int getSeekbarPosition() {
        return pref.getInt(FONT_SIZE_SEEKBAR_POS, 2);
    }

    public void setSeekbarPosition(int size) {
        editor.putInt(FONT_SIZE_SEEKBAR_POS, size);
        editor.commit();
    }

    // ****** END ****************
    public void setReciter(int reciter) {
        editor.putInt(RECITER, reciter);
        editor.commit();
    }

    public int getReciter() {
        return pref.getInt(RECITER, 1);
    }

    public void setLastRead(int index) {
        editor.putInt(LAST_READ, index);
        editor.commit();
    }

    public int getLastRead() {
        return pref.getInt(LAST_READ, -1);
    }

    public void setLastReadSurah(int index) {
        editor.putInt(LAST_READ_SURAH, index);
        editor.commit();
    }

    public int getLastReadSurah() {
        return pref.getInt(LAST_READ_SURAH, 0);
    }

    public boolean isTransliteration() {
        return pref.getBoolean(TRANSLITERATION, true);
    }

    public void setTransliteration(boolean isTransliteration) {
        editor.putBoolean(TRANSLITERATION, isTransliteration);
        editor.commit();
    }

	/*
     * public void setTranslation(boolean isTranslation) { editor.putBoolean(TRANSLATION, isTranslation); editor.commit(); }
	 * 
	 * public boolean isTranslation() { return pref.getBoolean(TRANSLATION, true); }
	 */

    public void setTranslationIndex(int translation) {
        editor.putInt(TRANSLATION_INDEX, translation);
        editor.commit();
    }

    public int getTranslationIndex() {
        return pref.getInt(TRANSLATION_INDEX, 1);
    }

    // For Save Surah Repeat Option
    public void setRepeatSurah(boolean isRepeat) {
        editor.putBoolean(REPEAT_SURAH, isRepeat);
        editor.commit();
    }

    public boolean isRepeatSurah() {
        return pref.getBoolean(REPEAT_SURAH, false);
    }

    // For Daily Ayah Notification Settings
    public void setAyahNotification(boolean isEnabled) {
        editor.putBoolean(AYAH_NOTIFICATION, isEnabled);
        editor.commit();
    }

    public boolean isAyahNotification() {
        return pref.getBoolean(AYAH_NOTIFICATION, false);
    }

    public void setAlarmHours(int hours) {
        editor.putInt(AYAH_TIME_HOUR, hours);
        editor.commit();
    }

    public int getAlarmHours() {
        return pref.getInt(AYAH_TIME_HOUR, AlarmReceiverAyah.HOUR);
    }

    public void setAlarmMints(int mints) {
        editor.putInt(AYAH_TIME_MINT, mints);
        editor.commit();
    }

    public int getAlarmMints() {
        return pref.getInt(AYAH_TIME_MINT, AlarmReceiverAyah.MINT);
    }

    public void clearStoredData() {
        editor.clear();
        editor.commit();
    }
}