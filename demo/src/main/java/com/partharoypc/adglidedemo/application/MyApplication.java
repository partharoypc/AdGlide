package com.partharoypc.adglidedemo.application;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // AppOpenAd lifecycle management is now handled entirely within AdGlide
        // automatically
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
