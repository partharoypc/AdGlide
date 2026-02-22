package com.partharoypc.adglidedemo.application;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
// import static com.partharoypc.adglide.util.Constant.GOOGLE_AD_MANAGER;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.util.OnShowAdCompleteListener;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;

import com.partharoypc.adglide.util.OnShowAdCompleteListener;
import com.partharoypc.adglidedemo.data.Constant;

@SuppressWarnings("ConstantConditions")
public class MyApplication extends Application {

    Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!Constant.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
            AdGlide.loadAppOpenAd(this)
                    .setOnStartActivityLifecycleCallbacks(null) // null uses internal default
                    .setOnStartLifecycleObserver();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void showAdIfAvailable(@NonNull Activity activity,
            @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        if (Constant.OPEN_ADS_ON_START) {
            if (Constant.AD_STATUS.equals(AD_STATUS_ON)) {
                AdGlide.loadAppOpenAd(activity);
                Constant.isAppOpen = true;
            }
        }
    }

}
