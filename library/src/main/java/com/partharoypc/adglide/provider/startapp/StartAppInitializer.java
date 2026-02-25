package com.partharoypc.adglide.provider.startapp;

import android.content.Context;
import com.partharoypc.adglide.provider.NetworkInitializer;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

public class StartAppInitializer implements NetworkInitializer {
    @Override
    public void initialize(Context context, InitializerConfig config) {
        StartAppSDK.init(context, config.getAppId(), true);
        StartAppSDK.setTestAdsEnabled(config.isDebug() || config.isTestMode());
        StartAppAd.disableSplash();
        StartAppSDK.enableReturnAds(false);
    }
}
