package com.partharoypc.adglide.format;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAppOpenAd;
import com.partharoypc.adglide.util.OnShowAdCompleteListener;

import java.util.Date;

/**
 * Manages AppLovin App Open ads with loading, caching, and display
 * functionality.
 * Ads are cached for up to 4 hours before requiring a reload.
 */
public class AppOpenAdAppLovin implements MaxAdListener {

    private static final String LOG_TAG = "AppOpenAd";
    private MaxAppOpenAd appOpenAd;
    private boolean isLoadingAd = false;
    private boolean isShowingAd = false;
    private long loadTime = 0;
    private OnShowAdCompleteListener onShowAdCompleteListener;

    public AppOpenAdAppLovin() {
    }

    public void loadAd(Context context, String maxAppOpenAdUnitId) {
        if (isLoadingAd || isAdAvailable()) {
            return;
        }
        isLoadingAd = true;
        appOpenAd = new MaxAppOpenAd(maxAppOpenAdUnitId, context);
        appOpenAd.setListener(this);
        appOpenAd.loadAd();
    }

    public boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    public boolean isAdAvailable() {
        return appOpenAd != null && appOpenAd.isReady() && wasLoadTimeLessThanNHoursAgo(4);
    }

    public void showAdIfAvailable(@NonNull final Activity activity, String appOpenAdUnitId) {
        showAdIfAvailable(activity, appOpenAdUnitId, () -> {
        });
    }

    public void showAdIfAvailable(@NonNull final Activity activity, String appOpenAdUnitId,
            @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        this.onShowAdCompleteListener = onShowAdCompleteListener;

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
        appOpenAd.showAd();
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        Log.d(LOG_TAG, "AppLovin App Open ad loaded.");
        loadTime = (new Date()).getTime();
        isLoadingAd = false;
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        Log.d(LOG_TAG, "AppLovin App Open ad failed to load: " + error.getMessage());
        isLoadingAd = false;
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
        Log.d(LOG_TAG, "AppLovin App Open ad displayed.");
        isShowingAd = true;
    }

    @Override
    public void onAdNotDisplayed(MaxAd ad, MaxError error) {
        Log.d(LOG_TAG, "AppLovin App Open ad failed to display: " + error.getMessage());
        isShowingAd = false;
        if (onShowAdCompleteListener != null) {
            onShowAdCompleteListener.onShowAdComplete();
        }
    }

    @Override
    public void onAdHidden(MaxAd ad) {
        Log.d(LOG_TAG, "AppLovin App Open ad hidden.");
        isShowingAd = false;
        if (onShowAdCompleteListener != null) {
            onShowAdCompleteListener.onShowAdComplete();
        }
        // Prefetch the next ad
        appOpenAd.loadAd();
    }

    @Override
    public void onAdClicked(MaxAd ad) {
    }
}
