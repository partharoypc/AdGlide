package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.annotation.SuppressLint;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;
import android.app.Activity;
import android.content.Context;

import com.partharoypc.adglide.util.AdGlideLog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.provider.AppOpenProvider;
import com.partharoypc.adglide.provider.AppOpenProviderFactory;
import com.partharoypc.adglide.util.AdGlideCallback;
import com.partharoypc.adglide.util.AdGlidePrefs;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("StaticFieldLeak")
public class AppOpenAd {
    private static final String TAG = "AdGlide";


    /**
     * Internal preference helper for persistent cooldowns.
     */
    private static AdGlidePrefs adGlidePrefs;


    /** Cooldown between App Open Ad impressions: default 30 minutes. */
    private static long cooldownMs = 30 * 60 * 1000L;

    /**
     * Sets the cooldown between App Open Ad impressions.
     * 
     * @param cooldownMinutes Cooldown in minutes.
     */
    public static void setCooldown(int cooldownMinutes) {
        cooldownMs = (long) cooldownMinutes * 60 * 1000L;
    }

    /** Returns {@code true} if enough time has passed since the last impression. */
    public static boolean isCooldownElapsed(Context context) {
        if (adGlidePrefs == null && context != null) {
            adGlidePrefs = new AdGlidePrefs(context);
        }
        long lastShownTimeMs = adGlidePrefs != null ? adGlidePrefs.getAppOpenLastShown() : 0;
        long effectiveCooldownMs = cooldownMs;
        com.partharoypc.adglide.AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
        if (config != null) {
            effectiveCooldownMs = (long) config.getAppOpenCooldownMinutes() * 60 * 1000L;
        }
        return lastShownTimeMs == 0 || (System.currentTimeMillis() - lastShownTimeMs) >= effectiveCooldownMs;
    }

    private java.lang.ref.WeakReference<Activity> activityRef;
    private com.partharoypc.adglide.util.AdLoader adLoader;

    // Provider management
    private static final java.util.Map<String, AppOpenProvider> providers = new java.util.concurrent.ConcurrentHashMap<>();

    public AppOpenAd() {
    }

    private AppOpenAd(Builder builder) {
        this.activityRef = builder.activityRef;
        this.adLoader = builder.adLoader;
    }

    private static AppOpenProvider getProvider(String network) {
        AppOpenProvider provider = providers.get(network);
        if (provider == null) {
            provider = AppOpenProviderFactory.getProvider(network);
            if (provider != null) {
                providers.put(network, provider);
            }
        }
        return provider;
    }

    public void onStartLifecycleObserver() {
        try {
            Activity activity = activityRef != null ? activityRef.get() : null;
            if (activity != null && com.partharoypc.adglide.AdGlide.isAppOpenEnabled()) {
                if (activity.getIntent().hasExtra("unique_id")) {
                    return;
                }
                showAdIfAvailable(activity, null);
            }
        } catch (Exception e) {
            AdGlideLog.e(TAG, "Error in onStartLifecycleObserver: " + e.getMessage());
        }
    }

    public void onStartActivityLifecycleCallbacks(Activity activity) {
        try {
            activityRef = new java.lang.ref.WeakReference<>(activity);
            if (adLoader == null) {
                adLoader = new com.partharoypc.adglide.util.AdLoader(activity,
                        com.partharoypc.adglide.util.AdFormat.APP_OPEN);
            }
        } catch (Exception e) {
            AdGlideLog.e(TAG, "Error in onStartActivityLifecycleCallbacks: " + e.getMessage());
        }
    }

    public void showAdIfAvailable(@NonNull Activity activity, @Nullable AdGlideCallback callback) {
        showAdIfAvailable(activity, false, callback);
    }

    public void showAdIfAvailable(@NonNull Activity activity, boolean ignoreCooldown, @Nullable AdGlideCallback callback) {
        try {
            if (!com.partharoypc.adglide.AdGlide.isAppOpenEnabled()) {
                if (callback != null)
                    callback.onAdDismissed();
                return;
            }

            // Strict "Only Once" show policy
            if (AdGlide.isAdShowing()) {
                AdGlideLog.d(TAG, "App Open Ad skipped — Another ad is already showing.");
                if (callback != null) callback.onAdDismissed();
                return;
            }

            // Cooldown check
            if (!ignoreCooldown && !isCooldownElapsed(activity)) {
                AdGlideLog.d(TAG, "App Open Ad skipped — cooldown not elapsed yet.");
                if (callback != null)
                    callback.onAdDismissed();
                return;
            }

            if (adLoader == null) {
                adLoader = new com.partharoypc.adglide.util.AdLoader(activity,
                        com.partharoypc.adglide.util.AdFormat.APP_OPEN);
            }

            adLoader.startLoading((network, resultCallback) -> {
                AppOpenProvider provider = getProvider(network);
                if (provider != null) {
                    AdGlide.setAdShowing(true);
                    provider.showAppOpenAd(activity, new AppOpenProvider.AppOpenListener() {
                        @Override
                        public void onAdLoaded() {
                            if (callback != null) callback.onAdLoaded();
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                            AdGlide.setAdShowing(false);
                            if (callback != null) callback.onAdFailedToLoad(error);
                            resultCallback.onFailure(error);
                        }

                        @Override
                        public void onAdDismissed() {
                            AdGlide.setAdShowing(false);
                            AdGlide.notifyAdDismissed("APP_OPEN", network);
                            if (callback != null) callback.onAdDismissed();
                        }

                        @Override
                        public void onAdShowFailed(String error) {
                            AdGlide.setAdShowing(false);
                            if (callback != null) callback.onAdDismissed();
                            resultCallback.onFailure(error);
                        }

                        @Override
                        public void onAdShowed() {
                            long now = System.currentTimeMillis();
                            if (adGlidePrefs == null) adGlidePrefs = new AdGlidePrefs(activity);
                            adGlidePrefs.setAppOpenLastShown(now);
                            AdGlide.notifyAdShowed("APP_OPEN", network);
                            if (callback != null)
                                callback.onAdShowed();
                        }

                        @Override
                        public void onAdClicked() {
                            AdGlide.notifyAdClicked("APP_OPEN", network);
                        }

                    });
                } else {
                    resultCallback.onFailure("Provider null");
                }
            }, callback);
        } catch (Exception e) {
            AdGlideLog.e(TAG, "Error in showAdIfAvailable: " + e.getMessage());
            if (callback != null) {
                callback.onAdDismissed();
            }
        }
    }

    // ── Builder ──────────────────────────────────────────────────────────

    public static class Builder extends BaseAdBuilder<Builder> {
        private AppOpenProvider currentProvider;
        private boolean ignoreCooldown = false;

        public Builder(@NonNull android.content.Context context) {
            super(context, com.partharoypc.adglide.util.AdFormat.APP_OPEN);
        }

        @NonNull
        public Builder cooldown(int minutes) {
            AppOpenAd.setCooldown(minutes);
            return this;
        }

        @Override
        protected void doShow(Activity displayActivity, AdGlideCallback callback) {
            showAppOpenAd(displayActivity, ignoreCooldown, callback);
        }

        public Builder loadAndShow(Activity displayActivity, boolean ignoreCooldown, AdGlideCallback callback) {
            this.showOnLoad = true;
            this.ignoreCooldown = ignoreCooldown;
            this.callback = callback;
            doLoad(callback);
            return this;
        }

        @Override
        protected void doLoad(AdGlideCallback callback) {
            this.callback = callback;
            if (adLoader == null)
                return;
            adLoader.startLoading((networkToLoad, resultCallback) -> {
                loadAdFromNetwork(networkToLoad, resultCallback, callback);
            }, callback);
        }

        private static String getAdUnitIdForNetwork(String network) {
            com.partharoypc.adglide.AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            return config != null ? config.resolveAdUnitId(com.partharoypc.adglide.util.AdFormat.APP_OPEN, network) : "0";
        }

        private void loadAdFromNetwork(String network, com.partharoypc.adglide.util.AdLoader.LoadResultCallback resultCallback, AdGlideCallback callback) {
            try {
                String adUnitId = getAdUnitIdForNetwork(network);
                if (adUnitId == null || adUnitId.trim().isEmpty() || (adUnitId.equals("0") && !network.equals(com.partharoypc.adglide.util.Constant.STARTAPP))) {
                    AdGlideLog.d(TAG, "Ad unit ID for " + network + " is invalid. Skipping.");
                    resultCallback.onFailure("Invalid Ad Unit ID");
                    return;
                }

                Activity activity = (activityRef != null) ? activityRef.get() : null;
                if (activity == null) {
                    AdGlideLog.e(TAG, "Activity context is missing. Cannot load App Open from network. Falling back to Application context for loader, but match rate may be affected.");
                }
                AppOpenProvider provider = getProvider(network);
                if (provider != null) {
                    this.currentProvider = provider;
                    provider.loadAppOpenAd(activity, adUnitId, new AppOpenProvider.AppOpenListener() {
                        @Override
                        public void onAdLoaded() {
                            com.partharoypc.adglide.util.PerformanceLogger.log("AppOpen", "Loaded: " + network);
                            AdGlideLog.d(TAG, "AppOpen ad loaded from [" + network.toUpperCase(java.util.Locale.ROOT) + "]. Showing now.");
                            
                            if (adLoader != null && adLoader.isTimedOut()) {
                                AdGlideLog.d(TAG, "App Open LOADED after timeout. Caching as Late Fill.");
                                com.partharoypc.adglide.util.AdPoolManager.cacheLateFill(com.partharoypc.adglide.util.AdFormat.APP_OPEN, network, Builder.this);
                            }
                            
                            resultCallback.onSuccess();
                            if (callback != null) callback.onAdLoaded();
                            
                            if (showOnLoad) {
                                showOnLoad = false;
                                doShow(activity, callback);
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                            com.partharoypc.adglide.util.PerformanceLogger.error("AppOpen", "Failed [" + network + "]: " + error);
                            AdGlideLog.e(TAG, "AppOpen failed to load from [" + network.toUpperCase(java.util.Locale.ROOT) + "]: " + error);
                            if (callback != null) callback.onAdFailedToLoad(error);
                            resultCallback.onFailure(error);
                        }

                        @Override
                        public void onAdDismissed() {
                            AdGlide.notifyAdDismissed("APP_OPEN", network);
                            if (callback != null) callback.onAdDismissed();
                        }


                        @Override
                        public void onAdShowFailed(String error) {
                            AdGlideLog.e(TAG, "AppOpen failed to show from [" + network.toUpperCase(java.util.Locale.ROOT) + "]: " + error);
                            if (callback != null) callback.onAdDismissed();
                        }

                        @Override
                        public void onAdShowed() {
                            com.partharoypc.adglide.util.PerformanceLogger.log("AppOpen", "Showed: " + network);
                            AdGlideLog.d(TAG, "AppOpen ad showed from [" + network.toUpperCase(java.util.Locale.ROOT) + "]");
                            long now = System.currentTimeMillis();
                            if (adGlidePrefs == null && activityRef.get() != null) adGlidePrefs = new AdGlidePrefs(activityRef.get());
                            if (adGlidePrefs != null) adGlidePrefs.setAppOpenLastShown(now);
                            AdGlide.notifyAdShowed("APP_OPEN", network); // Synchronized notification
                            if (callback != null) callback.onAdShowed();
                        }

                        @Override
                        public void onAdClicked() {
                            AdGlide.notifyAdClicked("APP_OPEN", network);
                        }

                    });
                } else {
                    resultCallback.onFailure("Provider null");
                }
            } catch (Exception e) {
                AdGlideLog.e(TAG, "Failed loading AppOpen from " + network + ": " + e.getMessage());
                resultCallback.onFailure(e.getMessage());
            }
        }


        public void showAppOpenAd(Activity activity, boolean ignoreCooldown, AdGlideCallback callback) {
            try {
                if (!com.partharoypc.adglide.AdGlide.isAppOpenEnabled()) {
                    if (callback != null)
                        callback.onAdDismissed();
                    return;
                }

                Activity safeActivity = activity != null ? activity : (activityRef != null ? activityRef.get() : null);
                if (safeActivity == null || safeActivity.isFinishing() || (android.os.Build.VERSION.SDK_INT >= 17 && safeActivity.isDestroyed())) {
                    if (callback != null) callback.onAdDismissed();
                    return;
                }

                // Strict "Only Once" show policy
                if (AdGlide.isAdShowing()) {
                    AdGlideLog.d(TAG, "App Open Ad skipped — Another ad is already showing.");
                    if (callback != null) callback.onAdDismissed();
                    return;
                }

                if (!ignoreCooldown && !isCooldownElapsed(safeActivity)) {
                    AdGlideLog.d(TAG, "App Open Ad skipped — cooldown not elapsed yet.");
                    if (callback != null) callback.onAdDismissed();
                    return;
                }

                // Try to show from currently loaded network if available
                if (currentProvider != null && currentProvider.isAdAvailable()) {
                    AdGlide.setAdShowing(true);
                    currentProvider.showAppOpenAd(safeActivity, new AppOpenProvider.AppOpenListener() {

                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                        }

                        @Override
                        public void onAdDismissed() {
                            AdGlide.setAdShowing(false);
                            AdGlide.notifyAdDismissed("APP_OPEN", currentNetwork != null ? currentNetwork : "UNKNOWN");
                            if (callback != null)
                                callback.onAdDismissed();
                        }

                        @Override
                        public void onAdShowFailed(String error) {
                            AdGlide.setAdShowing(false);
                            if (callback != null)
                                callback.onAdDismissed();
                        }

                        @Override
                        public void onAdShowed() {
                            if (adGlidePrefs == null && activity != null) adGlidePrefs = new AdGlidePrefs(activity);
                            if (adGlidePrefs != null) adGlidePrefs.setAppOpenLastShown(System.currentTimeMillis());
                            AdGlide.notifyAdShowed("APP_OPEN", currentNetwork != null ? currentNetwork : "UNKNOWN");
                            if (callback != null)
                                callback.onAdShowed();
                        }
                    });
                } else if (callback != null) {
                    callback.onAdDismissed();
                }
            } catch (Exception e) {
                AdGlideLog.e(TAG, "Error in showAppOpenAd: " + e.getMessage());
                if (callback != null)
                    callback.onAdDismissed();
            }
        }


        public boolean isAdAvailable() {
            return currentProvider != null && currentProvider.isAdAvailable();
        }
    }
}
