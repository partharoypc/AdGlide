package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.annotation.SuppressLint;
import com.partharoypc.adglide.AdGlideConfig;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.provider.AppOpenProvider;
import com.partharoypc.adglide.provider.AppOpenProviderFactory;
import com.partharoypc.adglide.util.AdGlideCallback;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("StaticFieldLeak")
public class AppOpenAd {
    private static final String TAG = "AdGlide";


    /**
     * Timestamp (ms) of the last time an App Open Ad was shown. 0 = never shown.
     */
    private static long lastShownTimeMs = 0;

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
    public static boolean isCooldownElapsed() {
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
    private static final Map<String, AppOpenProvider> providers = new HashMap<>();

    public AppOpenAd() {
    }

    private AppOpenAd(Builder builder) {
        this.activityRef = builder.activityRef;
        this.adLoader = new com.partharoypc.adglide.util.AdLoader(activityRef.get(),
                com.partharoypc.adglide.util.AdFormat.APP_OPEN);
    }

    private static synchronized AppOpenProvider getProvider(String network) {
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
            Log.e(TAG, "Error in onStartLifecycleObserver: " + e.getMessage());
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
            Log.e(TAG, "Error in onStartActivityLifecycleCallbacks: " + e.getMessage());
        }
    }

    public void showAdIfAvailable(@NonNull Activity activity, @Nullable AdGlideCallback callback) {
        try {
            if (!com.partharoypc.adglide.AdGlide.isAppOpenEnabled()) {
                if (callback != null)
                    callback.onAdDismissed();
                return;
            }

            // Cooldown check
            if (!isCooldownElapsed()) {
                Log.d(TAG, "App Open Ad skipped — cooldown not elapsed yet.");
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
                    provider.showAppOpenAd(activity, new AppOpenProvider.AppOpenListener() {
                        @Override
                        public void onAdLoaded() {
                            if (callback != null) callback.onAdLoaded();
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                            if (callback != null) callback.onAdFailedToLoad(error);
                            resultCallback.onFailure(error);
                        }

                        @Override
                        public void onAdDismissed() {
                            if (callback != null) callback.onAdDismissed();
                        }

                        @Override
                        public void onAdShowFailed(String error) {
                            if (callback != null) callback.onAdDismissed();
                            resultCallback.onFailure(error);
                        }

                        @Override
                        public void onAdShowed() {
                            lastShownTimeMs = System.currentTimeMillis();
                            resultCallback.onSuccess();
                            if (callback != null) callback.onAdShowed();
                        }
                    });
                } else {
                    resultCallback.onFailure("Provider null");
                }
            }, callback);
        } catch (Exception e) {
            Log.e(TAG, "Error in showAdIfAvailable: " + e.getMessage());
            if (callback != null) {
                callback.onAdDismissed();
            }
        }
    }

    // ── Builder ──────────────────────────────────────────────────────────

    public static class Builder {
        private final com.partharoypc.adglide.util.AdLoader adLoader;
        private final java.lang.ref.WeakReference<Activity> activityRef;
        private AppOpenProvider currentProvider;

        public Builder(Activity activity) {
            this.activityRef = new java.lang.ref.WeakReference<>(activity);
            this.adLoader = new com.partharoypc.adglide.util.AdLoader(activity,
                    com.partharoypc.adglide.util.AdFormat.APP_OPEN);
        }

        @NonNull
        public Builder cooldown(int minutes) {
            AppOpenAd.setCooldown(minutes);
            return this;
        }

        @NonNull
        public Builder load() {
            loadAppOpenAd(null);
            return this;
        }

        @NonNull
        public Builder load(AdGlideCallback callback) {
            loadAppOpenAd(callback);
            return this;
        }

        public void loadAppOpenAd(AdGlideCallback callback) {
            if (adLoader == null)
                return;
            adLoader.startLoading((networkToLoad, resultCallback) -> {
                loadAdFromNetwork(networkToLoad, resultCallback, callback);
            }, callback);
        }

        private void loadAdFromNetwork(String network, com.partharoypc.adglide.util.AdLoader.LoadResultCallback resultCallback, AdGlideCallback callback) {
            try {
                String adUnitId = getAdUnitIdForNetwork(network);
                if (adUnitId == null || adUnitId.trim().isEmpty() || (adUnitId.equals("0") && !network.equals("startapp"))) {
                    Log.d(TAG, "Ad unit ID for " + network + " is invalid. Trying backup.");
                    resultCallback.onFailure("Invalid Ad Unit ID");
                    return;
                }

                Activity activity = activityRef.get();
                if (activity == null) {
                    Log.e(TAG, "Activity is null. Cannot load App Open from network.");
                    resultCallback.onFailure("Activity is null");
                    return;
                }
                AppOpenProvider provider = getProvider(network);
                if (provider != null) {
                    this.currentProvider = provider;
                    provider.loadAppOpenAd(activity, adUnitId, new AppOpenProvider.AppOpenListener() {
                        @Override
                        public void onAdLoaded() {
                            com.partharoypc.adglide.util.PerformanceLogger.log("AppOpen", "Loaded: " + network);
                            Log.d(TAG, "AppOpen ad loaded from [" + network.toUpperCase(java.util.Locale.ROOT) + "]. Showing now.");
                            resultCallback.onSuccess();
                            if (callback != null) callback.onAdLoaded();
                            
                            if (!isCooldownElapsed()) {
                                Log.d(TAG, "App Open Ad skipped — cooldown not elapsed yet.");
                                if (callback != null) callback.onAdDismissed();
                                return;
                            }
                            Activity currentActivity = activityRef.get();
                            if (currentActivity != null) {
                                provider.showAppOpenAd(currentActivity, this);
                            } else if (callback != null) {
                                callback.onAdDismissed();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                            com.partharoypc.adglide.util.PerformanceLogger.error("AppOpen", "Failed [" + network + "]: " + error);
                            Log.e(TAG, "AppOpen failed to load from [" + network.toUpperCase(java.util.Locale.ROOT) + "]: " + error);
                            if (callback != null) callback.onAdFailedToLoad(error);
                            resultCallback.onFailure(error);
                        }

                        @Override
                        public void onAdDismissed() {
                            if (callback != null) callback.onAdDismissed();
                        }

                        @Override
                        public void onAdShowFailed(String error) {
                            Log.e(TAG, "AppOpen failed to show from [" + network.toUpperCase(java.util.Locale.ROOT) + "]: " + error);
                            if (callback != null) callback.onAdDismissed();
                        }

                        @Override
                        public void onAdShowed() {
                            com.partharoypc.adglide.util.PerformanceLogger.log("AppOpen", "Showed: " + network);
                            Log.d(TAG, "AppOpen ad showed from [" + network.toUpperCase(java.util.Locale.ROOT) + "]");
                            lastShownTimeMs = System.currentTimeMillis();
                            if (callback != null) callback.onAdShowed();
                        }
                    });
                } else {
                    resultCallback.onFailure("Provider null");
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed loading AppOpen from " + network + ": " + e.getMessage());
                resultCallback.onFailure(e.getMessage());
            }
        }

        private static String getAdUnitIdForNetwork(String network) {
            AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            if (config == null)
                return "0";
            return switch (network) {
                case ADMOB, META_BIDDING_ADMOB -> config.getAdMobAppOpenId();
                case META -> config.getMetaAppOpenId();
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> config.getAppLovinAppOpenId();
                case WORTISE -> config.getWortiseAppOpenId();
                default -> "0";
            };
        }

        public void showAppOpenAd(AdGlideCallback callback) {
            try {
                if (!com.partharoypc.adglide.AdGlide.isAppOpenEnabled()) {
                    if (callback != null)
                        callback.onAdDismissed();
                    return;
                }

                if (!isCooldownElapsed()) {
                    Log.d(TAG, "App Open Ad skipped — cooldown not elapsed yet.");
                    if (callback != null)
                        callback.onAdDismissed();
                    return;
                }

                // Try to show from currently loaded network if available
                if (currentProvider != null && currentProvider.isAdAvailable()) {
                    Activity activity = activityRef != null ? activityRef.get() : null;
                    currentProvider.showAppOpenAd(activity, new AppOpenProvider.AppOpenListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                        }

                        @Override
                        public void onAdDismissed() {
                            if (callback != null)
                                callback.onAdDismissed();
                        }

                        @Override
                        public void onAdShowFailed(String error) {
                            if (callback != null)
                                callback.onAdDismissed();
                        }

                        @Override
                        public void onAdShowed() {
                            lastShownTimeMs = System.currentTimeMillis();
                            if (callback != null)
                                callback.onAdShowed();
                        }
                    });
                } else if (callback != null) {
                    callback.onAdDismissed();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showAppOpenAd: " + e.getMessage());
                if (callback != null)
                    callback.onAdDismissed();
            }
        }

        public AppOpenAd build() {
            return new AppOpenAd(this);
        }

        @Deprecated
        public boolean isAdAvailable() {
            return currentProvider != null && currentProvider.isAdAvailable();
        }
    }
}
