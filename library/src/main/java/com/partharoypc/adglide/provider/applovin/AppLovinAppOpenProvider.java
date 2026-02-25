package com.partharoypc.adglide.provider.applovin;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAppOpenAd;
import com.partharoypc.adglide.provider.AppOpenProvider;
import java.util.Date;

public class AppLovinAppOpenProvider implements AppOpenProvider, MaxAdListener {
    private MaxAppOpenAd appOpenAd;
    private boolean isLoadingAd = false;
    private boolean isShowingAd = false;
    private long loadTime = 0;
    private AppOpenListener listener;

    @Override
    public void loadAppOpenAd(Context context, String adUnitId, AppOpenListener listener) {
        if (isLoadingAd || isAdAvailable()) {
            return;
        }
        this.listener = listener;
        isLoadingAd = true;
        appOpenAd = new MaxAppOpenAd(adUnitId, context);
        appOpenAd.setListener(this);
        appOpenAd.loadAd();
    }

    @Override
    public void showAppOpenAd(Activity activity, AppOpenListener listener) {
        this.listener = listener;
        if (isAdAvailable()) {
            appOpenAd.showAd();
        } else {
            listener.onAdShowFailed("AppLovin AppOpen ad not ready");
        }
    }

    @Override
    public boolean isAdAvailable() {
        return appOpenAd != null && appOpenAd.isReady() && wasLoadTimeLessThanNHoursAgo(4);
    }

    @Override
    public boolean isShowingAd() {
        return isShowingAd;
    }

    @Override
    public void destroy() {
        if (appOpenAd != null) {
            appOpenAd.destroy();
            appOpenAd = null;
        }
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        loadTime = (new Date()).getTime();
        isLoadingAd = false;
        if (listener != null)
            listener.onAdLoaded();
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        isLoadingAd = false;
        if (listener != null)
            listener.onAdFailedToLoad(error.getMessage());
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
        isShowingAd = true;
        if (listener != null)
            listener.onAdShowed();
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        isShowingAd = false;
        if (listener != null)
            listener.onAdShowFailed(error.getMessage());
    }

    @Override
    public void onAdHidden(MaxAd ad) {
        isShowingAd = false;
        if (listener != null)
            listener.onAdDismissed();
        // Prefetch next
        appOpenAd.loadAd();
    }

    @Override
    public void onAdClicked(MaxAd ad) {
    }
}
