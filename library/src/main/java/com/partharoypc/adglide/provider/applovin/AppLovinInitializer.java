package com.partharoypc.adglide.provider.applovin;

import android.content.Context;
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglide.provider.NetworkInitializer;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkInitializationConfiguration;
import com.applovin.sdk.AppLovinSdkSettings;

public class AppLovinInitializer implements NetworkInitializer {
    private static final String TAG = "AdGlide.AppLovin";
    private static AppLovinSdk sharedSdk;

    @Override
    public void initialize(Context context, InitializerConfig config) {
        String sdkKey = config.getAppId();
        AppLovinSdkInitializationConfiguration.Builder builder;

        if (sdkKey != null && !sdkKey.trim().isEmpty() && !sdkKey.equals("0")) {
            AdGlideLog.d(TAG, "Initializing AppLovin SDK with programmatic key.");
            builder = AppLovinSdkInitializationConfiguration.builder(sdkKey, context);
        } else {
            AdGlideLog.d(TAG, "Initializing AppLovin SDK with manifest key.");
            builder = AppLovinSdkInitializationConfiguration.builder(null, context);
        }

        AppLovinSdkInitializationConfiguration initConfig = builder
                .setMediationProvider(AppLovinMediationProvider.MAX)
                .build();

        sharedSdk = AppLovinSdk.getInstance(context);
        if (config.isDebug() || config.isTestMode()) {
            sharedSdk.getSettings().setVerboseLogging(true);
        }
        sharedSdk.initialize(initConfig, configuration -> {
            AdGlideLog.d(TAG, "AppLovin SDK initialized successfully.");
        });
    }

    public static AppLovinSdk getSdk(Context context) {
        if (sharedSdk == null) {
            sharedSdk = AppLovinSdk.getInstance(context);
        }
        return sharedSdk;
    }
}
