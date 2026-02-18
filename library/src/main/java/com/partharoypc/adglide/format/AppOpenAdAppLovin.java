package com.partharoypc.adglide.format;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.partharoypc.adglide.util.OnShowAdCompleteListener;

import java.util.Date;

/**
 * Manages AppLovin App Open ads with loading, caching, and display
 * functionality.
 * Ads are cached for up to 4 hours before requiring a reload.
 */
public class AppOpenAdAppLovin {

    private static final String LOG_TAG = "AppOpenAd";
    private boolean isLoadingAd = false;
    private boolean isShowingAd = false;
    private long loadTime = 0;

    /**
     * Returns whether an app open ad is currently being shown.
     *
     * @return {@code true} if an ad is currently showing
     */
    public boolean isShowingAd() {
        return isShowingAd;
    }

    public AppOpenAdAppLovin() {
    }

    public void loadAd(Context context, String maxAppOpenAdUnitId) {
        if (isLoadingAd || isAdAvailable()) {
            return;
        }
        isLoadingAd = true;
    }

    public boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    public boolean isAdAvailable() {
        return false;
    }

    public void showAdIfAvailable(@NonNull final Activity activity, String appOpenAdUnitId) {
        showAdIfAvailable(activity, appOpenAdUnitId, () -> {
        });
    }

    public void showAdIfAvailable(@NonNull final Activity activity, String appOpenAdUnitId,
            @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        if (isShowingAd) {
            Log.d(LOG_TAG, "The app open ad is already showing.");
            return;
        }

        if (!isAdAvailable()) {
            Log.d(LOG_TAG, "The app open ad is not ready yet.");
            onShowAdCompleteListener.onShowAdComplete();
            loadAd(activity, appOpenAdUnitId);
            return;
        }

        Log.d(LOG_TAG, "Will show ad.");

        isShowingAd = true;
    }

}
