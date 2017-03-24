package noman;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.MainActivityNew;
import com.quranreading.qibladirection.R;
import com.quranreading.qibladirection.SplashActivity;
import com.quranreading.sharedPreference.LocationPref;
import com.squareup.okhttp.OkHttpClient;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import noman.Ads.PreLoadIntersitial;
import noman.community.RestApi.RestApi;
import noman.community.activity.ComunityActivity;
import noman.community.fragment.MineFragment;
import noman.community.fragment.PrayerFragment;
import noman.community.model.CountryModel;
import noman.community.model.GraphApiResponse;
import noman.community.model.Prayer;
import noman.community.model.SignInRequest;
import noman.community.urlmanager.UrlManager;
import noman.community.utility.LoggingInterceptor;
import noman.hijri.acitivity.CalenderActivity;
import noman.hijri.acitivity.EventActivity;
import noman.hijri.fragment.CalenderFragment;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class CommunityGlobalClass extends GlobalClass {
    public GraphApiResponse mGraphApiResponse;
    public static SweetAlertDialog pd = null;
    public int dateAdjustment = -2;
    public int selected = -1;
    public int yearSelected = 1435;
    public int selectedEvent = -1;
    public int yearEvent = 1435;
    public int dialogDate = -1;
    public static CommunityGlobalClass me;
    public static String CountryName = "";
    public static CountryModel mCountryModel;
    public static List<Prayer> mPrayerModel = new ArrayList<>();
    public static List<Prayer> minePrayerModel = new ArrayList<>();
    public static SignInRequest mSignInRequests;
    public static PrayerFragment mPrayerFragment;
    public static MineFragment mMineFragment;
    public static CalenderFragment mCalenderFragment;
    public static ComunityActivity mCommunityActivity;
    public static EventActivity mEventActivity;
    public static CalenderActivity mCalenderTabsActivity;
    public static SplashActivity mMainActivityNew;
    public int selectDateOutSide = -1;
    //This is use from the notification
    public boolean isTodayEvent = false;
    public int todayEventPostion = 0;
    public static PreLoadIntersitial mInterstitialAd;
    public static MainActivityNew mainActivityNew;

    public static int prayerCounter=0;

    public static int moduleId = 1;



    public static CommunityGlobalClass getInstance() {
        return me;
    }

    public Toast mToast;
    public static Handler handler;
    public static Runnable runnable;
    public static long timeDelay = 0;
    public static boolean isAdAlreadyShow = false;
    public static boolean isQuranModuleOpen = false;
    @Override

    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
        JodaTimeAndroid.init(this);
        me = this;
        //Load Default Ad here
        CommunityGlobalClass.getInstance().mInterstitialAd = new PreLoadIntersitial(this);


    }

    public void sendAnalyticsScreen(String name) {

        AnalyticSingaltonClass.getInstance(this).sendScreenAnalytics(name);
    }

    public void sendAnalyticEvent(String ScreenName, String eventAction) {
        AnalyticSingaltonClass.getInstance(this).sendEventAnalytics(ScreenName, eventAction);
    }

    //For heder add
    public static RestApi getRestApi() {

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.interceptors().add(new LoggingInterceptor());

        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(UrlManager.SERVER_URL).client(httpClient).build();
        return retrofit.create(RestApi.class);
    }

    public static RestApi getRestApiForUrl(String url) {

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.interceptors().add(new LoggingInterceptor());

        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(url).client(httpClient).build();
        return retrofit.create(RestApi.class);
    }


    /**
     * Get Hash Key of Project Package
     */
    public void getHashKey(String packageName) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("KeyHash:", hash);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void showLoading(Context context) {

        pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(context.getResources().getColor(R.color.colorPrimary));
        pd.setTitleText(getResources().getString(R.string.txt_loading));
        pd.setCancelable(false);
        pd.show();
    }

    public void cancelDialog() {
        if (pd != null) {
            pd.dismiss();
            pd.cancel();
            pd = null;

        }
    }

    public void showServerFailureDialog(Context context) {
        final SweetAlertDialog taskCompDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        taskCompDialog
                .setTitleText("Error")
                .setContentText("Cannot connect to server");
        taskCompDialog.setCancelable(false);
        taskCompDialog.show();

    }

    public String getCountryName() {


        Call<CountryModel> call = CommunityGlobalClass.getRestApiForUrl(UrlManager.IP_URL).getCountryName();
        call.enqueue(new retrofit.Callback<CountryModel>() {

            @Override
            public void onResponse(retrofit.Response<CountryModel> response, Retrofit retrofit) {
                mCountryModel = response.body();
                if (mCountryModel != null)
                    CountryName = mCountryModel.getCountryName();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Failure-Country", t.toString());
            }
        });


        return CountryName;
    }

    public boolean isInternetOn() {
        ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
    }

    public String convertDates(Context mContext, String startTime) {
        long oldMillis = 0;
        Log.e("DB", "" + startTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm:ss");
        try {
            Date date1 = dateFormat.parse(startTime);
            oldMillis = date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getFormattedDate(mContext, oldMillis);
    }

    public String getFormattedDate(Context context, long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "Today " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm", smsTime).toString();
        }
    }


    public String getCurrentDate() {
        //2016-11-23 10:20:00 AM
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
        String formattedDate = dateFormat.format(new Date()).toString();
        return formattedDate;
    }

    public void showToast(String msg) {
        //2016-11-23 10:20:00 AM
        mToast.makeText(mMainActivityNew, msg, mToast.LENGTH_SHORT).show();
    }

    public void ifHuaweiAlert(Context mcontext) {
        final SharedPreferences settings = getSharedPreferences("ProtectedApps", MODE_PRIVATE);
        final String saveIfSkip = "skipProtectedAppsMessage";
        boolean skipMessage = settings.getBoolean(saveIfSkip, false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            Intent intent = new Intent();
            intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
            if (isCallable(intent)) {
                final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(mcontext);
                dontShowAgain.setText("Do not show again");
                dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        editor.putBoolean(saveIfSkip, isChecked);
                        editor.apply();
                    }
                });

                new AlertDialog.Builder(mcontext)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Huawei Protected Apps")
                        .setMessage(String.format("%s requires to be enabled in 'Protected Apps' to function properly.%n", getString(R.string.app_name)))
                        .setView(dontShowAgain)
                        .setPositiveButton("Protected Apps", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                huaweiProtectedApps();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            } else {
                editor.putBoolean(saveIfSkip, true);
                editor.apply();
            }
        }
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void huaweiProtectedApps() {
        try {
            String cmd = "am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial();
            }
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ignored) {
        }
    }

    private String getUserSerial() {
        //noinspection ResourceType
        Object userManager = getSystemService("user");
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return "";
    }

    /// Globale
    public void showShortToast(String message, int milliesTime, int gravity) {
        Context mContext = CommunityGlobalClass.getInstance();
        if (mContext.getString(R.string.device).equals("large")) {
            final Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
            toast.setGravity(gravity, 0, 0);
            toast.show();
        } else {
            final Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
            toast.setGravity(gravity, 0, 0);
            toast.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, milliesTime);
        }
    }

    public void runTimerForAdsStratedgy() {

        if (CommunityGlobalClass.isAdAlreadyShow == false) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (CommunityGlobalClass.isQuranModuleOpen == false ) {
                        if (!((GlobalClass) mainActivityNew.getApplicationContext()).isPurchase) {
                            mainActivityNew.sendBroadcast(new Intent(MainActivityNew.ACTION_INTERSTITIAL_ADS_SHOW));
                            CommunityGlobalClass.isAdAlreadyShow=true;
                            Log.e("Ads","Display Intersitial");
                        }
                    }
                    handler.removeCallbacks(runnable);
                }
            };
        }
    }
    /**
     * validate your email address format. Ex-akhi@mani.com
     */
    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public boolean isValidPhoneNumber(String phoneNumber) {

        if(phoneNumber.length() < 5 || phoneNumber.length() >15)
        {
            return  false;
        }
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    public static String getMonthName(int month){
        switch(month){
            case 1:
                return "Jan";

            case 2:
                return "Feb";

            case 3:
                return "Mar";

            case 4:
                return "Apr";

            case 5:
                return "May";

            case 6:
                return "Jun";

            case 7:
                return "Jul";

            case 8:
                return "Aug";

            case 9:
                return "Sep";

            case 10:
                return "Oct";

            case 11:
                return "Nov";

            case 12:
                return "Dec";
        }

        return "";
    }

}
