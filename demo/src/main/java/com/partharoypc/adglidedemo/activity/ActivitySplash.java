package com.partharoypc.adglidedemo.activity;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.partharoypc.adglide.format.AdNetwork;
import com.partharoypc.adglide.format.AppOpenAd;
import com.partharoypc.adglidedemo.BuildConfig;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.application.MyApplication;
import com.partharoypc.adglidedemo.callback.CallbackConfig;
import com.partharoypc.adglidedemo.data.Constant;
import com.partharoypc.adglidedemo.database.SharedPref;
import com.partharoypc.adglidedemo.rest.RestAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class ActivitySplash extends AppCompatActivity {

    private static final String TAG = "ActivitySplash";
    public static int DELAY_PROGRESS = 1500;
    AdNetwork.Initialize adNetwork;
    AppOpenAd.Builder appOpenAdBuilder;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPref = new SharedPref(this);
        initAds();

        if (Constant.AD_STATUS.equals(AD_STATUS_ON) && Constant.OPEN_ADS_ON_START) {
            if (!Constant.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    switch (Constant.AD_NETWORK) {
                        case ADMOB:
                            if (!Constant.ADMOB_APP_OPEN_AD_ID.equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this,
                                        this::loadOpenAds);
                            } else {
                                loadOpenAds();
                            }
                            break;
                        case APPLOVIN:
                        case APPLOVIN_MAX:
                            if (!Constant.APPLOVIN_APP_OPEN_AP_ID.equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this,
                                        this::loadOpenAds);
                            } else {
                                loadOpenAds();
                            }
                            break;
                        case WORTISE:
                            if (!Constant.WORTISE_APP_OPEN_AD_ID.equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this,
                                        this::loadOpenAds);
                            } else {
                                loadOpenAds();
                            }
                            break;
                        default:
                            loadOpenAds();
                            break;
                    }
                }, DELAY_PROGRESS);
            } else {
                loadOpenAds();
            }
        } else {
            loadOpenAds();
        }

    }

    private void initAds() {
        adNetwork = new AdNetwork.Initialize(this)
                .setAdStatus(Constant.AD_STATUS)
                .setAdNetwork(Constant.AD_NETWORK)
                .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                .setAdMobAppId(null)
                .setStartappAppId(Constant.STARTAPP_APP_ID)
                .setUnityGameId(Constant.UNITY_GAME_ID)
                .setAppLovinSdkKey(getResources().getString(R.string.app_lovin_sdk_key))
                .setironSourceAppKey(Constant.IRONSOURCE_APP_KEY)
                .setWortiseAppId(Constant.WORTISE_APP_ID)
                .setDebug(BuildConfig.DEBUG)
                .build();
    }

    private void loadOpenAds() {
        if (Constant.FORCE_TO_SHOW_APP_OPEN_AD_ON_START && Constant.OPEN_ADS_ON_START) {
            appOpenAdBuilder = new AppOpenAd.Builder(this)
                    .setAdStatus(Constant.AD_STATUS)
                    .setAdNetwork(Constant.AD_NETWORK)
                    .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                    .setAdMobAppOpenId(Constant.ADMOB_APP_OPEN_AD_ID)
                    .setAppLovinAppOpenId(Constant.APPLOVIN_APP_OPEN_AP_ID)
                    .setWortiseAppOpenId(Constant.WORTISE_APP_OPEN_AD_ID)
                    .build().load(this::startMainActivity);
        } else {
            startMainActivity();
        }
    }

    public void startMainActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }, DELAY_PROGRESS);
    }

}
