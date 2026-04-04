package com.partharoypc.adglide.provider.startapp;

import android.content.Context;
import com.partharoypc.adglide.provider.NetworkInitializer;
import com.startapp.sdk.adsbase.StartAppSDK;

public class StartAppInitializer implements NetworkInitializer {
    @Override
    public void initialize(Context context, InitializerConfig config) {
        // Use the modern init signature (appId only)
        StartAppSDK.init(context, config.getAppId());
        StartAppSDK.setTestAdsEnabled(config.isDebug() || config.isTestMode());
        
        // Note: Splash is now disabled via AndroidManifest.xml meta-data 
        // to avoid using the deprecated StartAppAd.disableSplash() API.
    }
}
