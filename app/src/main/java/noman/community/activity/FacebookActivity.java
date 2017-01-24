package noman.community.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.orhanobut.logger.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import noman.CommunityGlobalClass;
import noman.community.prefrences.SavePreference;
import noman.community.utility.DebugInfo;
import noman.community.model.GraphApiResponse;
import noman.community.model.SignInRequest;
import noman.community.model.SignUpResponse;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by xuhaib on 4/21/2016.
 */
public class FacebookActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    private static FacebookActivity mFacebookActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  facebookSDKInitialize();
        // setContentView(R.layout.activity_facebook);
        mFacebookActivity = this;

    }


    public static FacebookActivity initiateSDK() {

        mFacebookActivity.facebookSDKInitialize();
        // getLoginDetails(loginButton);
        return mFacebookActivity;
    }

    /*
     Initialize the facebook sdk.
     And then callback manager will handle the login responses.
    */
    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

 /*
  Register a callback function with LoginButton to respond to the login result.
 */

    public void getLoginDetails(LoginButton login_button) {

        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
                Log.e("facebook", "Succes full login");
                getUserInformation();
            }

            @Override
            public void onCancel() {
                // code for cancellation
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("data", data.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


    public void getUserInformation() {

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            }
        };

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                // Application code
                String id = null;
                try {
                    id = object.get("id").toString();
                    String imageLink = "http://graph.facebook.com/" + id + "/picture?type=large";
                    //add image  element in json object
                    object.put("image", imageLink);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Convert json object to string and pass to model class
                GraphApiResponse data = new Gson().fromJson(object.toString(), GraphApiResponse.class);
                //DebugInfo.loggerException(object.toString());

                CommunityGlobalClass.getInstance().mGraphApiResponse = data;

                SignInRequest mSignInRequest = new SignInRequest();
                mSignInRequest.setUser_login_id(data.getId());
                mSignInRequest.setEmail(data.getEmail());
                mSignInRequest.setLocation(CommunityGlobalClass.CountryName);
                mSignInRequest.setMode("0"); //For facebook
                mSignInRequest.setName(data.getFirstName());
                callToLoadPrayer(mSignInRequest);

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,first_name,last_name,location");//put parameters that is need
        request.setParameters(parameters);
        request.executeAsync();

    }

    public void callToLoadPrayer(final SignInRequest mSignUpRequest) {
        CommunityGlobalClass.getInstance().showLoading(mFacebookActivity);
        Call<SignUpResponse> call = CommunityGlobalClass.getRestApi().signInUser(mSignUpRequest);
        call.enqueue(new retrofit.Callback<SignUpResponse>() {

            @Override
            public void onResponse(Response<SignUpResponse> response, Retrofit retrofit) {
                CommunityGlobalClass.getInstance().cancelDialog();

                CommunityGlobalClass.mPrayerModel = response.body().getPrayers();
                mSignUpRequest.setUser_id(response.body().getResponse().getUser_id());
                //Save PreFerence in the list
                SavePreference savePreference = new SavePreference();
                savePreference.setDataFromSharedPreferences(mSignUpRequest);
                CommunityGlobalClass.mSignInRequests = mSignUpRequest;

                CommunityGlobalClass.mainActivityNew.initDrawer();
                FacebookActivity.super.onBackPressed();
                startActivity(new Intent(FacebookActivity.this, PostActivity.class));
                //Refresh the Mine Tab
                CommunityGlobalClass.mCommunityActivity.moveToMineTab();
                CommunityGlobalClass.mMineFragment.onLoadMineList();
            }

            @Override
            public void onFailure(Throwable t) {
                CommunityGlobalClass.getInstance().cancelDialog();
                if (BuildConfig.DEBUG) DebugInfo.loggerException("SignUp-Failure" + t.getMessage());
                CommunityGlobalClass.getInstance().showServerFailureDialog(FacebookActivity.this);
            }
        });


    }


}