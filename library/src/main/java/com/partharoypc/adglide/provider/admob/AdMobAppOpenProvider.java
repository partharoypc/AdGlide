package com.partharoypc.adglide.provider.admob;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.partharoypc.adglide.provider.AppOpenProvider;
import java.util.Date;

public class AdMobAppOpenProvider implements AppOpenProvider {
    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    private boolean isShowingAd = false;
    private long loadTime = 0;

    @Override
    public void loadAppOpenAd(Context context, String adUnitId, AppOpenListener listener) {
        if (isAdAvailable()) {
            listener.onAdLoaded();
            return;
        }
        if (isLoadingAd) {
            return;
        }

        if (!com.partharoypc.adglide.util.AdMobRateLimiter.isRequestAllowed(adUnitId)) {
            listener.onAdFailedToLoad("AdMob rate limit hit");
            return;
        }

        isLoadingAd = true;
        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(context, adUnitId, request, new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd ad) {
                com.partharoypc.adglide.util.AdMobRateLimiter.resetCooldown(adUnitId);
                appOpenAd = ad;
                isLoadingAd = false;
                loadTime = (new Date()).getTime();
                listener.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                if (loadAdError.getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                    com.partharoypc.adglide.util.AdMobRateLimiter.recordFailure(adUnitId);
                }
                isLoadingAd = false;
                listener.onAdFailedToLoad(loadAdError.getMessage());
            }
        });
    }

    @Override
    public void showAppOpenAd(Activity activity, AppOpenListener listener) {
        if (appOpenAd == null) {
            listener.onAdShowFailed("AdMob AppOpen ad not ready");
            return;
        }

        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                appOpenAd = null;
                isShowingAd = false;
                listener.onAdDismissed();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                appOpenAd = null;
                isShowingAd = false;
                listener.onAdShowFailed(adError.getMessage());
            }

            @Override
            public void onAdShowedFullScreenContent() {
                isShowingAd = true;
                listener.onAdShowed();
            }
        });

        appOpenAd.show(activity);
    }

    @Override
    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    @Override
    public boolean isShowingAd() {
        return isShowingAd;
    }

    @Override
    public void destroy() {
        appOpenAd = null;
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }
}
