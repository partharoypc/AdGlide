package com.partharoypc.adglide.provider;

import android.util.Log;
import com.partharoypc.adglide.util.ReflectionUtils;
import static com.partharoypc.adglide.util.Constant.*;

public class RewardedProviderFactory {
    private static final String TAG = "AdGlide.RewardedFactory";

    public static RewardedProvider getProvider(String network) {
        String className = null;
        String checkClass = null;

        switch (network) {
            case ADMOB, META_BIDDING_ADMOB -> {
                className = "com.partharoypc.adglide.provider.admob.AdMobRewardedProvider";
                checkClass = "com.google.android.gms.ads.rewarded.RewardedAd";
            }
            case META -> {
                className = "com.partharoypc.adglide.provider.meta.MetaRewardedProvider";
                checkClass = "com.facebook.ads.RewardedVideoAd";
            }
            case APPLOVIN, APPLOVIN_MAX -> {
                className = "com.partharoypc.adglide.provider.applovin.AppLovinRewardedProvider";
                checkClass = "com.applovin.mediation.ads.MaxRewardedAd";
            }
            case UNITY -> {
                className = "com.partharoypc.adglide.provider.unity.UnityRewardedProvider";
                checkClass = "com.unity3d.ads.UnityAds";
            }
            case STARTAPP -> {
                className = "com.partharoypc.adglide.provider.startapp.StartAppRewardedProvider";
                checkClass = "com.startapp.sdk.adsbase.StartAppAd";
            }
            case IRONSOURCE -> {
                className = "com.partharoypc.adglide.provider.ironsource.IronSourceRewardedProvider";
                checkClass = "com.ironsource.mediationsdk.IronSource";
            }
            case WORTISE -> {
                className = "com.partharoypc.adglide.provider.wortise.WortiseRewardedProvider";
                checkClass = "com.wortise.ads.rewarded.RewardedAd";
            }
        }

        if (className != null && (checkClass == null || ReflectionUtils.isClassAvailable(checkClass))) {
            return ReflectionUtils.createInstance(className);
        }

        if (className != null) {
            Log.w(TAG, "SDK for Rewarded network [" + network + "] is not added to the project. Skipping.");
        }

        return null;
    }
}
