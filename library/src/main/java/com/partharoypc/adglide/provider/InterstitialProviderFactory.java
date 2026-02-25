package com.partharoypc.adglide.provider;

import android.util.Log;
import com.partharoypc.adglide.util.ReflectionUtils;
import static com.partharoypc.adglide.util.Constant.*;

public class InterstitialProviderFactory {
    private static final String TAG = "AdGlide.InterstitialFactory";

    public static InterstitialProvider getProvider(String network) {
        String className = null;
        String checkClass = null;

        switch (network) {
            case ADMOB:
            case META_BIDDING_ADMOB:
                className = "com.partharoypc.adglide.provider.admob.AdMobInterstitialProvider";
                checkClass = "com.google.android.gms.ads.interstitial.InterstitialAd";
                break;
            case META:
                className = "com.partharoypc.adglide.provider.meta.MetaInterstitialProvider";
                checkClass = "com.facebook.ads.InterstitialAd";
                break;
            case UNITY:
                className = "com.partharoypc.adglide.provider.unity.UnityInterstitialProvider";
                checkClass = "com.unity3d.ads.UnityAds";
                break;
            case APPLOVIN:
            case APPLOVIN_MAX:
            case META_BIDDING_APPLOVIN_MAX:
                className = "com.partharoypc.adglide.provider.applovin.AppLovinInterstitialProvider";
                checkClass = "com.applovin.mediation.ads.MaxInterstitialAd";
                break;
            case IRONSOURCE:
            case META_BIDDING_IRONSOURCE:
                className = "com.partharoypc.adglide.provider.ironsource.IronSourceInterstitialProvider";
                checkClass = "com.ironsource.mediationsdk.IronSource";
                break;
            case STARTAPP:
                className = "com.partharoypc.adglide.provider.startapp.StartAppInterstitialProvider";
                checkClass = "com.startapp.sdk.adsbase.StartAppAd";
                break;
            case WORTISE:
                className = "com.partharoypc.adglide.provider.wortise.WortiseInterstitialProvider";
                checkClass = "com.wortise.ads.interstitial.InterstitialAd";
                break;
        }

        if (className != null && (checkClass == null || ReflectionUtils.isClassAvailable(checkClass))) {
            return ReflectionUtils.createInstance(className);
        }

        if (className != null) {
            Log.w(TAG, "SDK for Interstitial network [" + network + "] is not added to the project. Skipping.");
        }

        return null;
    }
}
