package com.partharoypc.adglide.format;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.partharoypc.adglide.util.OnShowAdCompleteListener;
import com.partharoypc.adglide.util.Tools;
import com.wortise.ads.AdError;
import com.wortise.ads.appopen.AppOpenAd;

import java.util.Date;

/**
 * Manages Wortise App Open ads with loading, caching, and display
 * functionality.
 * Ads are cached for up to 4 hours before requiring a reload.
 */
public class WortiseAppOpenAd implements AppOpenAd.Listener {
    @Override
    public void onAppOpenRevenuePaid(@NonNull com.wortise.ads.appopen.AppOpenAd ad,
            @NonNull com.wortise.ads.RevenueData rd) {
    }

    private static final String TAG = "AdGlide";
    private AppOpenAd appOpenAd;
    private boolean isLoadingAd = false;
    private boolean isShowingAd = false;

    public boolean isShowingAd() {
        return isShowingAd;
    }

    private long loadTime = 0;
    private OnShowAdCompleteListener onShowAdCompleteListener;

    public WortiseAppOpenAd() {
    }

    public void loadAd(Context context, String wortiseAppOpenId) {
        if (isLoadingAd || isAdAvailable()) {
            return;
        }
        if (!Tools.isNetworkAvailable(context)) {
            Log.e(TAG, "Internet connection not available. Skipping Wortise App Open ad load.");
            return;
        }
        isLoadingAd = true;
        appOpenAd = new AppOpenAd(context, wortiseAppOpenId);
        appOpenAd.setListener(this);
        appOpenAd.loadAd();
    }

    public boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    public boolean isAdAvailable() {
        return appOpenAd != null && appOpenAd.isAvailable() && wasLoadTimeLessThanNHoursAgo(4);
    }

    public void showAdIfAvailable(@NonNull final Activity activity, String appOpenAdUnitId) {
        showAdIfAvailable(activity, appOpenAdUnitId, () -> {
        });
    }

    public void showAdIfAvailable(@NonNull final Activity activity, String wortiseAppOpenAdUnitId,
            @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        this.onShowAdCompleteListener = onShowAdCompleteListener;

        if (isShowingAd) {
            Log.d(TAG, "The app open ad is already showing.");
            return;
        }

        if (!isAdAvailable()) {
            Log.d(TAG, "The app open ad is not ready yet.");
            onShowAdCompleteListener.onShowAdComplete();
            loadAd(activity, wortiseAppOpenAdUnitId);
            return;
        }

        Log.d(TAG, "Will show ad.");
        appOpenAd.showAd();
    }

    @Override
    public void onAppOpenLoaded(@NonNull AppOpenAd ad) {
        Log.d(TAG, "Wortise App Open ad loaded.");
        loadTime = (new Date()).getTime();
        isLoadingAd = false;
    }

    @Override
    public void onAppOpenFailedToLoad(@NonNull AppOpenAd ad, @NonNull AdError error) {
        Log.d(TAG, "Wortise App Open ad failed to load: " + error.getMessage());
        isLoadingAd = false;
    }

    @Override
    public void onAppOpenClicked(@NonNull AppOpenAd ad) {
    }

    @Override
    public void onAppOpenDismissed(@NonNull AppOpenAd ad) {
        Log.d(TAG, "Wortise App Open ad dismissed.");
        isShowingAd = false;
        if (onShowAdCompleteListener != null) {
            onShowAdCompleteListener.onShowAdComplete();
        }
        // Prefetch
        ad.loadAd();
    }

    @Override
    public void onAppOpenFailedToShow(@NonNull AppOpenAd ad, @NonNull AdError error) {
        Log.d(TAG, "Wortise App Open ad failed to show: " + error.getMessage());
        isShowingAd = false;
        if (onShowAdCompleteListener != null) {
            onShowAdCompleteListener.onShowAdComplete();
        }
    }

    @Override
    public void onAppOpenImpression(@NonNull AppOpenAd ad) {
    }

    @Override
    public void onAppOpenShown(@NonNull AppOpenAd ad) {
        Log.d(TAG, "Wortise App Open ad shown.");
        isShowingAd = true;
    }
}
