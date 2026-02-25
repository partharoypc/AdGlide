package com.partharoypc.adglide.provider;

import android.util.Log;
import com.partharoypc.adglide.util.ReflectionUtils;
import static com.partharoypc.adglide.util.Constant.*;

public class NetworkInitializerFactory {
    private static final String TAG = "AdGlide.Factory";

    public static NetworkInitializer getInitializer(String network) {
        String className = null;
        String checkClass = null;

        switch (network) {
            case ADMOB:
            case META_BIDDING_ADMOB:
                className = "com.partharoypc.adglide.provider.admob.AdMobInitializer";
                checkClass = "com.google.android.gms.ads.MobileAds";
                break;
            case META:
                className = "com.partharoypc.adglide.provider.meta.MetaInitializer";
                checkClass = "com.facebook.ads.AudienceNetworkAds";
                break;
            case UNITY:
                className = "com.partharoypc.adglide.provider.unity.UnityInitializer";
                checkClass = "com.unity3d.ads.UnityAds";
                break;
            case APPLOVIN:
            case APPLOVIN_MAX:
            case META_BIDDING_APPLOVIN_MAX:
                className = "com.partharoypc.adglide.provider.applovin.AppLovinInitializer";
                checkClass = "com.applovin.sdk.AppLovinSdk";
                break;
            case IRONSOURCE:
            case META_BIDDING_IRONSOURCE:
                className = "com.partharoypc.adglide.provider.ironsource.IronSourceInitializer";
                checkClass = "com.ironsource.mediationsdk.IronSource";
                break;
            case STARTAPP:
                className = "com.partharoypc.adglide.provider.startapp.StartAppInitializer";
                checkClass = "com.startapp.sdk.adsbase.StartAppSDK";
                break;
            case WORTISE:
                className = "com.partharoypc.adglide.provider.wortise.WortiseInitializer";
                checkClass = "com.wortise.ads.WortiseSdk";
                break;
        }

        if (className != null && (checkClass == null || ReflectionUtils.isClassAvailable(checkClass))) {
            return ReflectionUtils.createInstance(className);
        }

        if (className != null) {
            Log.w(TAG, "SDK for network [" + network + "] is not added to the project. Skipping.");
        }

        return null;
    }
}
