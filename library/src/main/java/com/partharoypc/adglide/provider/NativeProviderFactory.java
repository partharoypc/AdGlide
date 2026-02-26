package com.partharoypc.adglide.provider;

import android.util.Log;
import com.partharoypc.adglide.util.ReflectionUtils;
import static com.partharoypc.adglide.util.Constant.*;
import static com.partharoypc.adglide.util.Constant.UNITY;

public class NativeProviderFactory {
    private static final String TAG = "AdGlide.NativeFactory";

    public static NativeProvider getProvider(String network) {
        String className = null;
        String checkClass = null;

        switch (network) {
            case ADMOB, META_BIDDING_ADMOB -> {
                className = "com.partharoypc.adglide.provider.admob.AdMobNativeProvider";
                checkClass = "com.google.android.gms.ads.nativead.NativeAd";
            }
            case META -> {
                className = "com.partharoypc.adglide.provider.meta.MetaNativeProvider";
                checkClass = "com.facebook.ads.NativeAd";
            }
            case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> {
                className = "com.partharoypc.adglide.provider.applovin.AppLovinNativeProvider";
                checkClass = "com.applovin.mediation.nativeAds.MaxNativeAdLoader";
            }
            case STARTAPP -> {
                className = "com.partharoypc.adglide.provider.startapp.StartAppNativeProvider";
                checkClass = "com.startapp.sdk.ads.nativead.StartAppNativeAd";
            }
            case WORTISE -> {
                className = "com.partharoypc.adglide.provider.wortise.WortiseNativeProvider";
                checkClass = "com.wortise.ads.WortiseSdk";
            }
            case IRONSOURCE, META_BIDDING_IRONSOURCE -> {
                className = "com.partharoypc.adglide.provider.ironsource.IronSourceNativeProvider";
                checkClass = "com.ironsource.mediationsdk.IronSource";
            }
            case UNITY -> {
                Log.w(TAG, "Unity Ads does not support Native Ad format. Trying backup.");
                return null;
            }
        }

        if (className != null && (checkClass == null || ReflectionUtils.isClassAvailable(checkClass))) {
            return ReflectionUtils.createInstance(className);
        }

        if (className != null) {
            Log.w(TAG, "SDK for Native network [" + network + "] is not added to the project. Skipping.");
        }

        return null;
    }
}
