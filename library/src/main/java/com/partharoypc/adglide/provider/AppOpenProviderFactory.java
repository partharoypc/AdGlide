package com.partharoypc.adglide.provider;

import com.partharoypc.adglide.util.ReflectionUtils;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.WORTISE;

public class AppOpenProviderFactory {
    private static final String ADMOB_PROV = "com.partharoypc.adglide.provider.admob.AdMobAppOpenProvider";
    private static final String APPLOVIN_PROV = "com.partharoypc.adglide.provider.applovin.AppLovinAppOpenProvider";
    private static final String WORTISE_PROV = "com.partharoypc.adglide.provider.wortise.WortiseAppOpenProvider";

    public static AppOpenProvider getProvider(String network) {
        String className = null;
        String checkClass = null;

        switch (network) {
            case ADMOB:
            case META_BIDDING_ADMOB:
                className = ADMOB_PROV;
                checkClass = "com.google.android.gms.ads.appopen.AppOpenAd";
                break;
            case APPLOVIN:
            case APPLOVIN_MAX:
            case META_BIDDING_APPLOVIN_MAX:
                className = APPLOVIN_PROV;
                checkClass = "com.applovin.mediation.ads.MaxAppOpenAd";
                break;
            case com.partharoypc.adglide.util.Constant.META:
                className = "com.partharoypc.adglide.provider.meta.MetaAppOpenProvider";
                checkClass = "com.facebook.ads.InterstitialAd";
                break;
            case WORTISE:
                className = WORTISE_PROV;
                checkClass = "com.wortise.ads.appopen.AppOpenAd";
                break;
        }

        if (className != null && (checkClass == null || ReflectionUtils.isClassAvailable(checkClass))) {
            return (AppOpenProvider) ReflectionUtils.createInstance(className);
        }
        return null;
    }
}
