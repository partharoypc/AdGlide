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
            case ADMOB, META_BIDDING_ADMOB -> {
                className = "com.partharoypc.adglide.provider.admob.AdMobBannerProvider";
                checkClass = "com.google.android.gms.ads.AdView";
            }
            case META -> {
                className = "com.partharoypc.adglide.provider.meta.MetaBannerProvider";
                checkClass = "com.facebook.ads.AdView";
            }
            case UNITY -> {
                className = "com.partharoypc.adglide.provider.unity.UnityBannerProvider";
                checkClass = "com.unity3d.services.banners.BannerView";
            }
            case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> {
                className = "com.partharoypc.adglide.provider.applovin.AppLovinBannerProvider";
                checkClass = "com.applovin.mediation.ads.MaxAdView";
            }
            case IRONSOURCE, META_BIDDING_IRONSOURCE -> {
                className = "com.partharoypc.adglide.provider.ironsource.IronSourceBannerProvider";
                checkClass = "com.ironsource.mediationsdk.IronSourceBannerLayout";
            }
            case STARTAPP -> {
                className = "com.partharoypc.adglide.provider.startapp.StartAppBannerProvider";
                checkClass = "com.startapp.sdk.ads.banner.Banner";
            }
            case WORTISE -> {
                className = "com.partharoypc.adglide.provider.wortise.WortiseBannerProvider";
                checkClass = "com.wortise.ads.banner.BannerAd";
            }
            case HOUSE_AD -> {
                className = "com.partharoypc.adglide.provider.housead.HouseAdBannerProvider";
                // House Ads use standard Android Views, so we check a base class
                checkClass = "android.widget.ImageView";
            }
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
