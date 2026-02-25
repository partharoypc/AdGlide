package com.partharoypc.adglidedemo.application;

import static com.partharoypc.adglide.util.Constant.ADMOB;

import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.WORTISE;

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

import com.partharoypc.adglide.format.AppOpenAd;
import com.partharoypc.adglide.util.OnShowAdCompleteListener;
import com.partharoypc.adglidedemo.data.Constant;

@SuppressWarnings("ConstantConditions")
public class MyApplication extends Application {

    private AppOpenAd.Builder appOpenAdBuilder;
    Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!Constant.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
            registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
            ProcessLifecycleOwner.get().getLifecycle().addObserver(lifecycleObserver);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    LifecycleObserver lifecycleObserver = new DefaultLifecycleObserver() {
        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onStart(owner);
            if (Constant.isAppOpen) {
                if (Constant.OPEN_ADS_ON_RESUME) {
                    if (Constant.AD_STATUS) {
                        if (currentActivity != null && !currentActivity.getIntent().hasExtra("unique_id")) {
                            if (appOpenAdBuilder != null) {
                                appOpenAdBuilder.showAppOpenAd();
                            } else {
                                loadAndShowAd(currentActivity);
                            }
                        }
                    }
                }
            }
        }
    };

    private void loadAndShowAd(Activity activity) {
        appOpenAdBuilder = new AppOpenAd.Builder(activity)
                .status(Constant.AD_STATUS)
                .network(Constant.AD_NETWORK)
                .backup(Constant.BACKUP_AD_NETWORK)
                .adMobId(Constant.ADMOB_APP_OPEN_AD_ID)
                .appLovinId(Constant.APPLOVIN_APP_OPEN_AP_ID)
                .wortiseId(Constant.WORTISE_APP_OPEN_AD_ID)
                .load();
    }

    ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            if (Constant.OPEN_ADS_ON_START) {
                if (Constant.AD_STATUS) {
                    currentActivity = activity;
                }
            }
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
        }
    };

    public void showAdIfAvailable(@NonNull Activity activity,
            @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        if (Constant.OPEN_ADS_ON_START) {
            if (Constant.AD_STATUS) {
                Constant.isAppOpen = true;
                new AppOpenAd.Builder(activity)
                        .status(Constant.AD_STATUS)
                        .network(Constant.AD_NETWORK)
                        .backup(Constant.BACKUP_AD_NETWORK)
                        .adMobId(Constant.ADMOB_APP_OPEN_AD_ID)
                        .appLovinId(Constant.APPLOVIN_APP_OPEN_AP_ID)
                        .wortiseId(Constant.WORTISE_APP_OPEN_AD_ID)
                        .load(onShowAdCompleteListener);
            } else {
                onShowAdCompleteListener.onShowAdComplete();
            }
        } else {
            onShowAdCompleteListener.onShowAdComplete();
        }
    }

}
