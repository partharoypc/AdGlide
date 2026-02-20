package com.partharoypc.adglide.format;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.partharoypc.adglide.util.OnShowAdCompleteListener;

import java.util.Date;

/**
 * Manages AdMob App Open ads with loading, caching, and display functionality.
 * Ads are cached for up to 4 hours before requiring a reload.
 */
public class AdMobAppOpenAd {

    private static final String TAG = "AdGlide";
    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    private boolean isShowingAd = false;
    private long loadTime = 0;

    private long lastAdShowTime = 0;
    private static final long MIN_TIME_BETWEEN_ADS_MS = 1000 * 60 * 60 * 4;

    /**
     * Returns whether an app open ad is currently being shown.
     *
     * @return {@code true} if an ad is currently showing
     */
    public boolean isShowingAd() {
        return isShowingAd;
    }

    public AdMobAppOpenAd() {
    }

    public void loadAd(Context context, String adMobAppOpenAdUnitId) {
        try {
            if (isLoadingAd || isAdAvailable()) {
                return;
            }

            isLoadingAd = true;
            AdRequest request = new AdRequest.Builder().build();
            AppOpenAd.load(context, adMobAppOpenAdUnitId, request, new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull AppOpenAd ad) {
                    appOpenAd = ad;
                    isLoadingAd = false;
                    loadTime = (new Date()).getTime();

                    Log.d(TAG, "onAdLoaded.");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    isLoadingAd = false;
                    Log.d(TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in loadAd: " + e.getMessage());
            isLoadingAd = false;
        }
    }

    public boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    public void showAdIfAvailable(@NonNull final Activity activity, String appOpenAdUnitId) {
        showAdIfAvailable(activity, appOpenAdUnitId, () -> {
        });
    }

    public void showAdIfAvailable(@NonNull final Activity activity, String appOpenAdUnitId,
            @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        try {
            if (isShowingAd) {
                Log.d(TAG, "The app open ad is already showing.");
                return;
            }

            if (!isAdAvailable()) {
                Log.d(TAG, "The app open ad is not ready yet.");
                onShowAdCompleteListener.onShowAdComplete();
                loadAd(activity, appOpenAdUnitId);
                return;
            }

            long currentTime = new Date().getTime();
            long timeSinceLastShow = currentTime - lastAdShowTime;
            if (lastAdShowTime > 0 && timeSinceLastShow < MIN_TIME_BETWEEN_ADS_MS) {
                Log.d(TAG,
                        "The app open ad was shown " + (timeSinceLastShow / 1000 / 60) + " minutes ago. Skipping.");
                onShowAdCompleteListener.onShowAdComplete();
                return;
            }

            Log.d(TAG, "Will show ad.");

            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    appOpenAd = null;
                    isShowingAd = false;

                    Log.d(TAG, "onAdDismissedFullScreenContent.");

                    onShowAdCompleteListener.onShowAdComplete();
                    loadAd(activity, appOpenAdUnitId);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    appOpenAd = null;
                    isShowingAd = false;
                    Log.d(TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                    onShowAdCompleteListener.onShowAdComplete();
                    loadAd(activity, appOpenAdUnitId);
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    lastAdShowTime = new Date().getTime();
                    Log.d(TAG, "onAdShowedFullScreenContent.");
                }
            });

            isShowingAd = true;
            appOpenAd.show(activity);
        } catch (Exception e) {
            Log.e(TAG, "Error in showAdIfAvailable: " + e.getMessage());
            onShowAdCompleteListener.onShowAdComplete();
            loadAd(activity, appOpenAdUnitId);
        }
    }
}




