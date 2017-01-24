package noman.community.prefrences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import noman.community.model.SignInRequest;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by vtbsol on 1/13/2016.
 */
public class SavePreference {

    static final String prefName = "username";
    static final String prefPassword = "password";
    static final String remember_login_user = "isLogin";
    static final String remember_hijri_value = "hijri_value";
    static final String remember_menu_hijri_correction = "isHijriCorrection";
    static final String remember_menu_notificaiton = "isHijriCorrection";


    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setRememberLogin(Context ctx, Boolean remember) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(remember_login_user, remember);
        editor.commit();

    }

    public static Boolean getRememberLogin(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(remember_login_user, false);

    }

    public static void setMenuOption(Context ctx, int remember) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(remember_login_user, remember);
        editor.commit();

    }

    public static int getMenuOption(Context ctx) {
        return getSharedPreferences(ctx).getInt(remember_login_user, 0);

    }

    public static void setPrefUsername(Context ctx, String Name) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(prefName, Name);
        editor.commit();

    }

    public static String getPrefUsername(Context ctx) {
        return getSharedPreferences(ctx).getString(prefName, null);
    }

    public static void setPrefPassword(Context ctx, String pswd) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(prefPassword, pswd);
        editor.commit();
    }

    public static String getPrefPassword(Context ctx) {
        return getSharedPreferences(ctx).getString(prefPassword, null);
    }

    public SignInRequest getDataFromSharedPreferences() {
        String PREFS_TAG = "SharedPrefs";
        String PRODUCT_TAG = "UserInfo";
        Gson gson = new Gson();
        SignInRequest userFromShared = null;
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(PRODUCT_TAG, "");
        userFromShared = gson.fromJson(jsonPreferences, SignInRequest.class);
        return userFromShared;
    }

    public void setDataFromSharedPreferences(SignInRequest mSignInRequest) {
        Gson gson = new Gson();
        String jsonCurProduct = gson.toJson(mSignInRequest);
        String PREFS_TAG = "SharedPrefs";
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String PRODUCT_TAG = "UserInfo";
        editor.putString(PRODUCT_TAG, jsonCurProduct);
        editor.commit();
    }


    public static void setHijriCorrectionSetting(Context ctx, int remember) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(remember_menu_hijri_correction, remember);
        editor.commit();

    }

    public static int getHijriCorrectionSetting(Context ctx) {
        return getSharedPreferences(ctx).getInt(remember_menu_hijri_correction, 2);

    }

    public static void setNotificationSetting(Context ctx, int remember) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(remember_menu_notificaiton, remember);
        editor.commit();

    }

    public static int getNotificationSetting(Context ctx) {
        return getSharedPreferences(ctx).getInt(remember_menu_notificaiton, 0);

    }

    public static void setHijriValue(Context ctx, int remember) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(remember_hijri_value, remember);
        editor.commit();

    }

    public static int getHijriValue(Context ctx) {
        return getSharedPreferences(ctx).getInt(remember_hijri_value, 0);

    }
}
