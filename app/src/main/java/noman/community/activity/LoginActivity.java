package noman.community.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.quranreading.qibladirection.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import noman.CommunityGlobalClass;
import noman.community.model.GraphApiResponse;
import noman.community.model.SignInRequest;

/**
 * Created by xuhaib on 4/11/2016.
 */
public class LoginActivity extends FacebookActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private static LoginActivity mGmailActivity;
    private static GoogleApiClient mGoogleApiClient;
    private static GoogleSignInOptions googleSignInOptions;
    Intent intent;
    LoginButton btnFacebook;
    SignInButton btnGmail;
    Activity mActivity;
    boolean isInternetAvaliable;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  CommunityGlobalClass.getInstance().getHashKey(this.getPackageName());
        //Initiate my Activity and also extends from the activity
        FacebookActivity.initiateSDK();
        mActivity = this;

        intent = new Intent();
        setContentView(R.layout.activity_signin_signup);
        btnFacebook = (LoginButton) findViewById(R.id.btn_facebook);
        btnGmail = (SignInButton) findViewById(R.id.btn_gmail);
        btnFacebook.setBackgroundResource(R.drawable.facebook);

        RelativeLayout img_back = (RelativeLayout) findViewById(R.id.layout_drawer_menu_ic);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        TextView titleheader = (TextView) findViewById(R.id.txt_header_title);
        titleheader.setText(getResources().getString(R.string.activity_login));


        FacebookActivity.initiateSDK().getLoginDetails(btnFacebook);
        isInternetAvaliable = CommunityGlobalClass.getInstance().isInternetOn();
        //Get Country Name while app start
        if (isInternetAvaliable) {
            CommunityGlobalClass.getInstance().getCountryName();
        }
        //if already login in app then logout the user
       /* if (isFacebookLoggedIn()) {
            LoginManager.getInstance().logOut();
            //   FacebookActivity.initiateSDK().getUserInformation();
        }*/


        mGmailActivity = this;
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(mGmailActivity).enableAutoManage(mGmailActivity, mGmailActivity).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
        setGooglePlusButtonText(btnGmail, "Log in with Gmail");

        btnGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signInGmail();
            }
        });


    }


    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(15);
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setText(buttonText);
                return;
            }
        }
    }

    public boolean isFacebookLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    //Hiding Keyboard
    public void setKeyboardVisibility(View v, Context mContext) {
        if (v != null && mContext != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    public void onConnectionFailed(
            @NonNull
                    ConnectionResult connectionResult) {

    }

    private void signInGmail() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult googleSignInResult) {

        if (googleSignInResult.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = googleSignInResult.getSignInAccount();
            GraphApiResponse mGraphApiResponse = new GraphApiResponse();
            mGraphApiResponse.setId(acct.getId());
            mGraphApiResponse.setEmail(acct.getEmail());
            mGraphApiResponse.setFirstName(acct.getDisplayName());
          /*  String imageLink = "https://www.googleapis.com/plus/v1/people/" + acct.getId() + "?fields=image&key=" + getResources().getString(R.string.google_app_id);
            mGraphApiResponse.setImage(imageLink);*/
            // loadImageUrl(imageLink);
            CommunityGlobalClass.getInstance().mGraphApiResponse = mGraphApiResponse;
            SignInRequest mSignInRequest = new SignInRequest();
            mSignInRequest.setUser_login_id(mGraphApiResponse.getId());
            mSignInRequest.setEmail(mGraphApiResponse.getEmail());
            mSignInRequest.setLocation(CommunityGlobalClass.CountryName);
            mSignInRequest.setMode("1"); //For Gmail
            mSignInRequest.setName(mGraphApiResponse.getFirstName());

            //Call webservice
            callToLoadPrayer(mSignInRequest);

        }
    }

    public void loadImageUrl(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Response response)
                    throws IOException {
                response.body().byteStream(); // Read the data from the stream
                // DebugInfo.loggerException(response.body());
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}