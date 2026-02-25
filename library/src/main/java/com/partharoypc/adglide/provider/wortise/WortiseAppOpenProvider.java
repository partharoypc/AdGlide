package com.partharoypc.adglide.provider.wortise;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import com.partharoypc.adglide.provider.AppOpenProvider;
import com.wortise.ads.AdError;
import com.wortise.ads.appopen.AppOpenAd;
import java.util.Date;

public class WortiseAppOpenProvider implements AppOpenProvider, AppOpenAd.Listener {
    private AppOpenAd appOpenAd;
    private boolean isLoadingAd = false;
    private boolean isShowingAd = false;
    private long loadTime = 0;
    private AppOpenListener listener;

    @Override
    public void loadAppOpenAd(Context context, String adUnitId, AppOpenListener listener) {
        if (isAdAvailable()) {
            listener.onAdLoaded();
            return;
        }
        if (isLoadingAd) {
            return;
        }
        this.listener = listener;
        isLoadingAd = true;
        appOpenAd = new AppOpenAd(context, adUnitId);
        appOpenAd.setListener(this);
        appOpenAd.loadAd();
    }

    @Override
    public void showAppOpenAd(Activity activity, AppOpenListener listener) {
        this.listener = listener;
        if (isAdAvailable()) {
            appOpenAd.showAd();
        } else {
            listener.onAdShowFailed("Wortise AppOpen ad not ready");
        }
    }

    @Override
    public boolean isAdAvailable() {
        return appOpenAd != null && appOpenAd.isAvailable() && wasLoadTimeLessThanNHoursAgo(4);
    }

    @Override
    public boolean isShowingAd() {
        return isShowingAd;
    }

    @Override
    public void destroy() {
        if (appOpenAd != null) {
            appOpenAd = null;
        }
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    @Override
    public void onAppOpenLoaded(@NonNull AppOpenAd ad) {
        loadTime = (new Date()).getTime();
        isLoadingAd = false;
        if (listener != null)
            listener.onAdLoaded();
    }

    @Override
    public void onAppOpenFailedToLoad(@NonNull AppOpenAd ad, @NonNull AdError error) {
        isLoadingAd = false;
        if (listener != null)
            listener.onAdFailedToLoad(error.getMessage());
    }

    @Override
    public void onAppOpenClicked(@NonNull AppOpenAd ad) {
    }

    @Override
    public void onAppOpenDismissed(@NonNull AppOpenAd ad) {
        isShowingAd = false;
        if (listener != null)
            listener.onAdDismissed();
        // Clear listener BEFORE prefetch so the stale show-listener
        // is not called when the next ad loads automatically.
        listener = null;
        ad.loadAd();
    }

    @Override
    public void onAppOpenFailedToShow(@NonNull AppOpenAd ad, @NonNull AdError error) {
        isShowingAd = false;
        if (listener != null)
            listener.onAdShowFailed(error.getMessage());
    }

    @Override
    public void onAppOpenImpression(@NonNull AppOpenAd ad) {
    }

    @Override
    public void onAppOpenShown(@NonNull AppOpenAd ad) {
        isShowingAd = true;
        if (listener != null)
            listener.onAdShowed();
    }

    @Override
    public void onAppOpenRevenuePaid(@NonNull AppOpenAd ad, @NonNull com.wortise.ads.RevenueData revenueData) {
    }
}
