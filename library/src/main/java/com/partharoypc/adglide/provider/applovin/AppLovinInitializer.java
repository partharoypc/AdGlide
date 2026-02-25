package com.partharoypc.adglide.provider.applovin;

import android.content.Context;
import android.util.Log;
import com.partharoypc.adglide.provider.NetworkInitializer;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkSettings;

public class AppLovinInitializer implements NetworkInitializer {
    private static final String TAG = "AdGlide.AppLovin";
    private static AppLovinSdk sharedSdk;

    @Override
    public void initialize(Context context, InitializerConfig config) {
        String sdkKey = config.getAppId();
        if (sdkKey != null && !sdkKey.trim().isEmpty() && !sdkKey.equals("0")) {
            Log.d(TAG, "Initializing AppLovin SDK with programmatic key.");
            sharedSdk = AppLovinSdk.getInstance(sdkKey, new AppLovinSdkSettings(context), context);
        } else {
            Log.d(TAG, "Initializing AppLovin SDK with manifest key.");
            sharedSdk = AppLovinSdk.getInstance(context);
        }

        sharedSdk.setMediationProvider(AppLovinMediationProvider.MAX);
        sharedSdk.initializeSdk(configuration -> {
            Log.d(TAG, "AppLovin SDK initialized successfully.");
        });
    }

    public static AppLovinSdk getSdk(Context context) {
        if (sharedSdk == null) {
            sharedSdk = AppLovinSdk.getInstance(context);
        }
        return sharedSdk;
    }
}
