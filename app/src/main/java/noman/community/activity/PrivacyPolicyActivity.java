package noman.community.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.quranreading.qibladirection.LanguageSelectionActivity;
import com.quranreading.qibladirection.MainActivityNew;
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
public class PrivacyPolicyActivity extends Activity {


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  CommunityGlobalClass.getInstance().getHashKey(this.getPackageName());
        //Initiate my Activity and also extends from the activity

        setContentView(R.layout.privacy_policy_activity);

        RelativeLayout img_back = (RelativeLayout) findViewById(R.id.layout_drawer_menu_ic);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        TextView titleheader = (TextView) findViewById(R.id.txt_header_title);
        titleheader.setText(getResources().getString(R.string.activity_privacy_policy));
        TextView tv = (TextView) findViewById(R.id.textDisclaimer);
        tv.setText(getText(R.string.text_disclaimer));
        tv.setMovementMethod(LinkMovementMethod.getInstance());

        if (tv.getLinksClickable() == true) {
            tv.setLinkTextColor(getResources().getColor(R.color.link_text_color));
        }
    }

}
