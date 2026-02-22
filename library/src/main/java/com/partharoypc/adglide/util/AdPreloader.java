package com.partharoypc.adglide.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.partharoypc.adglide.AdConfig;
import com.partharoypc.adglide.format.InterstitialAd;
import com.partharoypc.adglide.format.RewardedAd;

import java.lang.ref.WeakReference;

/**
 * Background ad preloader that ensures ads are ready to show instantly.
 * <p>
 * Call {@link #preload(Activity)} after SDK initialization to begin preloading
 * interstitial and rewarded ads. When the user triggers an ad show,
 * the ad is already loaded — zero wait time.
 * </p>
 *
 * <pre>{@code
 * // After AdGlide.init()
 * AdPreloader.getInstance().preload(activity);
 * }</pre>
 */
public final class AdPreloader {

    private static final String TAG = "AdGlide";
    private static volatile AdPreloader instance;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private WeakReference<Activity> activityRef;

    private InterstitialAd.Builder interstitialBuilder;
    private RewardedAd.Builder rewardedBuilder;

    private boolean interstitialPreloaded = false;
    private boolean rewardedPreloaded = false;

    private AdPreloader() {
    }

    /**
     * Returns the singleton instance.
     */
    @NonNull
    public static AdPreloader getInstance() {
        if (instance == null) {
            synchronized (AdPreloader.class) {
                if (instance == null) {
                    instance = new AdPreloader();
                }
            }
        }
        return instance;
    }

    /**
     * Begins preloading interstitial and rewarded ads using the global AdConfig.
     * <p>
     * This should be called once after ad SDK initialization. Subsequent calls
     * will only reload if ads haven't been preloaded yet.
     * </p>
     *
     * @param activity The Activity context (held via WeakReference to avoid leaks).
     */
    public void preload(@NonNull Activity activity) {
        this.activityRef = new WeakReference<>(activity);
        AdConfig config = AdConfig.getInstance();

        // Preload on the main thread (SDK requirement)
        mainHandler.post(() -> {
            preloadInterstitial(config);
            preloadRewarded(config);
        });
    }

    private void preloadInterstitial(@NonNull AdConfig config) {
        Activity activity = activityRef != null ? activityRef.get() : null;
        if (activity == null || activity.isFinishing())
            return;

        String network = config.getAdNetwork();
        if (network.isEmpty())
            return;

        try {
            interstitialBuilder = new InterstitialAd.Builder(activity)
                    .setAdStatus(config.getAdStatus())
                    .setAdNetwork(network)
                    .setAdMobInterstitialId(config.getAdMobInterstitialId())
                    .setMetaInterstitialId(config.getMetaInterstitialId())
                    .setUnityInterstitialId(config.getUnityInterstitialId())
                    .setAppLovinInterstitialId(config.getAppLovinInterstitialId())
                    .setIronSourceInterstitialId(config.getIronSourceInterstitialId())
                    .setWortiseInterstitialId(config.getWortiseInterstitialId())
                    .setPlacementStatus(config.getPlacementStatus())
                    .setInterval(config.getInterstitialInterval())
                    .setLegacyGDPR(config.isLegacyGDPR());

            // Set backup networks
            for (String backup : config.getBackupAdNetworks()) {
                interstitialBuilder.setBackupAdNetwork(backup);
            }

            interstitialBuilder.build();
            interstitialPreloaded = true;
            Log.d(TAG, "[Preloader] Interstitial preloaded for network: " + network);
        } catch (Exception e) {
            Log.e(TAG, "[Preloader] Failed to preload interstitial: " + e.getMessage());
        }
    }

    private void preloadRewarded(@NonNull AdConfig config) {
        Activity activity = activityRef != null ? activityRef.get() : null;
        if (activity == null || activity.isFinishing())
            return;

        String network = config.getAdNetwork();
        if (network.isEmpty())
            return;

        try {
            rewardedBuilder = new RewardedAd.Builder(activity)
                    .setAdStatus(config.getAdStatus())
                    .setAdNetwork(network)
                    .setAdMobRewardedId(config.getAdMobRewardedId())
                    .setMetaRewardedId(config.getMetaRewardedId())
                    .setUnityRewardedId(config.getUnityRewardedId())
                    .setApplovinMaxRewardedId(config.getAppLovinRewardedId())
                    .setIronSourceRewardedId(config.getIronSourceRewardedId())
                    .setWortiseRewardedId(config.getWortiseRewardedId())
                    .setPlacementStatus(config.getPlacementStatus())
                    .setLegacyGDPR(config.isLegacyGDPR());

            // Set backup networks
            for (String backup : config.getBackupAdNetworks()) {
                rewardedBuilder.setBackupAdNetwork(backup);
            }

            rewardedBuilder.build(
                    () -> Log.d(TAG, "[Preloader] Rewarded ad complete"),
                    () -> Log.d(TAG, "[Preloader] Rewarded ad dismissed"));
            rewardedPreloaded = true;
            Log.d(TAG, "[Preloader] Rewarded preloaded for network: " + network);
        } catch (Exception e) {
            Log.e(TAG, "[Preloader] Failed to preload rewarded: " + e.getMessage());
        }
    }

    /**
     * Returns the preloaded interstitial builder, or null if not yet preloaded.
     */
    public InterstitialAd.Builder getInterstitialBuilder() {
        return interstitialBuilder;
    }

    /**
     * Returns the preloaded rewarded builder, or null if not yet preloaded.
     */
    public RewardedAd.Builder getRewardedBuilder() {
        return rewardedBuilder;
    }

    /**
     * Returns true if interstitial ads have been preloaded.
     */
    public boolean isInterstitialPreloaded() {
        return interstitialPreloaded;
    }

    /**
     * Returns true if rewarded ads have been preloaded.
     */
    public boolean isRewardedPreloaded() {
        return rewardedPreloaded;
    }

    /**
     * Clears preloaded state. Call when the Activity is being destroyed.
     */
    public void destroy() {
        interstitialBuilder = null;
        rewardedBuilder = null;
        interstitialPreloaded = false;
        rewardedPreloaded = false;
        activityRef = null;
        mainHandler.removeCallbacksAndMessages(null);
    }
}
