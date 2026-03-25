package com.partharoypc.adglide.provider.unity;

import android.content.Context;
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglide.provider.NetworkInitializer;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;

public class UnityInitializer implements NetworkInitializer {
    private static final String TAG = "AdGlide.Unity";

    @Override
    public void initialize(Context context, InitializerConfig config) {
        UnityAds.initialize(context, config.getAppId(), config.isDebug() || config.isTestMode(),
                new IUnityAdsInitializationListener() {
                    @Override
                    public void onInitializationComplete() {
                        AdGlideLog.d(TAG, "Unity Ads Initialization Complete");
                    }

                    @Override
                    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error,
                            String message) {
                        AdGlideLog.d(TAG, "Unity Ads Initialization Failed: " + error + " - " + message);
                    }
                });
    }
}
