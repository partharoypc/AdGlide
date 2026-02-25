package com.partharoypc.adglide.provider;

import android.util.Log;
import com.partharoypc.adglide.util.ReflectionUtils;
import static com.partharoypc.adglide.util.Constant.*;

public class BannerProviderFactory {
    private static final String TAG = "AdGlide.BannerFactory";

    public static BannerProvider getProvider(String network) {
        String className = null;
        String checkClass = null;

        switch (network) {
            case ADMOB:
            case META_BIDDING_ADMOB:
                className = "com.partharoypc.adglide.provider.admob.AdMobBannerProvider";
                checkClass = "com.google.android.gms.ads.AdView";
                break;
            case META:
                className = "com.partharoypc.adglide.provider.meta.MetaBannerProvider";
                checkClass = "com.facebook.ads.AdView";
                break;
            case UNITY:
                className = "com.partharoypc.adglide.provider.unity.UnityBannerProvider";
                checkClass = "com.unity3d.services.banners.BannerView";
                break;
            case APPLOVIN:
            case APPLOVIN_MAX:
            case META_BIDDING_APPLOVIN_MAX:
                className = "com.partharoypc.adglide.provider.applovin.AppLovinBannerProvider";
                checkClass = "com.applovin.mediation.ads.MaxAdView";
                break;
            case IRONSOURCE:
            case META_BIDDING_IRONSOURCE:
                className = "com.partharoypc.adglide.provider.ironsource.IronSourceBannerProvider";
                checkClass = "com.ironsource.mediationsdk.IronSourceBannerLayout";
                break;
            case STARTAPP:
                className = "com.partharoypc.adglide.provider.startapp.StartAppBannerProvider";
                checkClass = "com.startapp.sdk.ads.banner.Banner";
                break;
            case WORTISE:
                className = "com.partharoypc.adglide.provider.wortise.WortiseBannerProvider";
                checkClass = "com.wortise.ads.banner.BannerAd";
                break;
        }

        if (className != null && (checkClass == null || ReflectionUtils.isClassAvailable(checkClass))) {
            return ReflectionUtils.createInstance(className);
        }

        if (className != null) {
            Log.w(TAG, "SDK for Banner network [" + network + "] is not added to the project. Skipping.");
        }

        return null;
    }
}
