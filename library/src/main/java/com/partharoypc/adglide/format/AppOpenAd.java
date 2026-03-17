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
        return lastShownTimeMs == 0 || (System.currentTimeMillis() - lastShownTimeMs) >= cooldownMs;
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

            adLoader.startLoading(new com.partharoypc.adglide.util.AdLoader.AdLoadCallback() {
                @Override
                public void onAdLoaded(String network) {
                    AppOpenProvider provider = getProvider(network);
                    if (provider != null) {
                        provider.showAppOpenAd(activity, new AppOpenProvider.AppOpenListener() {
                            @Override
                            public void onAdLoaded() {
                                if (callback != null)
                                    callback.onAdLoaded();
                            }

                            @Override
                            public void onAdFailedToLoad(String error) {
                                if (callback != null)
                                    callback.onAdFailedToLoad(error);
                                loadBackupAd(callback);
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
                                loadBackupAd(callback);
                            }

                            @Override
                            public void onAdShowed() {
                                lastShownTimeMs = System.currentTimeMillis();
                                if (callback != null)
                                    callback.onAdShowed();
                            }
                        });
                    } else {
                        loadBackupAd(callback);
                    }
                }

                @Override
                public void onAdFailed(String error) {
                    if (callback != null)
                        callback.onAdDismissed();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in showAdIfAvailable: " + e.getMessage());
            if (callback != null) {
                callback.onAdDismissed();
            }
        }
    }

    private void loadBackupAd(AdGlideCallback callback) {
        if (adLoader == null)
            return;
        adLoader.loadNext(new com.partharoypc.adglide.util.AdLoader.AdLoadCallback() {
            @Override
            public void onAdLoaded(String network) {
                AppOpenProvider provider = getProvider(network);
                Activity activity = activityRef != null ? activityRef.get() : null;
                if (provider != null && activity != null) {
                    provider.showAppOpenAd(activity, new AppOpenProvider.AppOpenListener() {
                        @Override
                        public void onAdLoaded() {
                            if (callback != null)
                                callback.onAdLoaded();
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                            if (callback != null)
                                callback.onAdFailedToLoad(error);
                            loadBackupAd(callback);
                        }

                        @Override
                        public void onAdDismissed() {
                            if (callback != null)
                                callback.onAdDismissed();
                        }

                        @Override
                        public void onAdShowFailed(String error) {
                            loadBackupAd(callback);
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
            }

            @Override
            public void onAdFailed(String error) {
                if (callback != null)
                    callback.onAdDismissed();
            }
        });
    }

    // ── Builder ──────────────────────────────────────────────────────────

    public static class Builder {
        private final com.partharoypc.adglide.util.AdLoader adLoader;
        private final java.lang.ref.WeakReference<Activity> activityRef;

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
            adLoader.startLoading(new com.partharoypc.adglide.util.AdLoader.AdLoadCallback() {
                @Override
                public void onAdLoaded(String network) {
                    loadAdFromNetwork(network, callback);
                }

                @Override
                public void onAdFailed(String error) {
                    Log.d(TAG, "App Open load failed: " + error);
                    if (callback != null)
                        callback.onAdDismissed();
                }
            });
        }

        private void loadAdFromNetwork(String network, AdGlideCallback callback) {
            try {
                String adUnitId = getAdUnitIdForNetwork(network);
                if (adUnitId == null || adUnitId.trim().isEmpty()
                        || (adUnitId.equals("0") && !network.equals("startapp"))) {
                    Log.d(TAG, "Ad unit ID for " + network + " is invalid. Trying backup.");
                    loadBackupAppOpenAd(callback);
                    return;
                }

                Activity activity = activityRef.get();
                if (activity == null) {
                    Log.e(TAG, "Activity is null. Cannot load App Open from network.");
                    return;
                }
                AppOpenProvider provider = getProvider(network);
                if (provider != null) {
                    provider.loadAppOpenAd(activity, adUnitId, new AppOpenProvider.AppOpenListener() {
                        @Override
                        public void onAdLoaded() {
                            com.partharoypc.adglide.util.PerformanceLogger.log("AppOpen", "Loaded: " + network);
                            Log.d(TAG, "AppOpen ad loaded from [" + network.toUpperCase(java.util.Locale.ROOT)
                                    + "]. Showing now.");
                            if (callback != null)
                                callback.onAdLoaded();
                            // Splash path auto-show logic
                            if (!isCooldownElapsed()) {
                                Log.d(TAG, "App Open Ad skipped — cooldown not elapsed yet.");
                                if (callback != null)
                                    callback.onAdDismissed();
                                return;
                            }
                            Activity activity = activityRef.get();
                            if (activity != null) {
                                provider.showAppOpenAd(activity, this);
                            } else if (callback != null) {
                                callback.onAdDismissed();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                            com.partharoypc.adglide.util.PerformanceLogger.error("AppOpen",
                                    "Failed [" + network + "]: " + error);
                            Log.e(TAG, "AppOpen failed to load from [" + network.toUpperCase(java.util.Locale.ROOT)
                                    + "]: " + error);
                            if (callback != null)
                                callback.onAdFailedToLoad(error);
                            loadBackupAppOpenAd(callback);
                        }

                        @Override
                        public void onAdDismissed() {
                            // No-op
                            if (callback != null)
                                callback.onAdDismissed();
                        }

                        @Override
                        public void onAdShowFailed(String error) {
                            Log.e(TAG, "AppOpen failed to show from [" + network.toUpperCase(java.util.Locale.ROOT)
                                    + "]: " + error);
                            if (callback != null)
                                callback.onAdDismissed();
                        }

                        @Override
                        public void onAdShowed() {
                            com.partharoypc.adglide.util.PerformanceLogger.log("AppOpen", "Showed: " + network);
                            Log.d(TAG, "AppOpen ad showed from [" + network.toUpperCase(java.util.Locale.ROOT) + "]");
                            lastShownTimeMs = System.currentTimeMillis();
                            if (callback != null)
                                callback.onAdShowed();
                        }
                    });
                } else {
                    loadBackupAppOpenAd(callback);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed loading AppOpen from " + network + ": " + e.getMessage());
                loadBackupAppOpenAd(callback);
            }
        }

        private void loadBackupAppOpenAd(AdGlideCallback callback) {
            if (adLoader == null)
                return;
            adLoader.loadNext(new com.partharoypc.adglide.util.AdLoader.AdLoadCallback() {
                @Override
                public void onAdLoaded(String network) {
                    loadAdFromNetwork(network, callback);
                }

                @Override
                public void onAdFailed(String error) {
                    Log.d(TAG, "App Open backup load failed: " + error);
                    if (callback != null)
                        callback.onAdDismissed();
                }
            });
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
                final AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
                if (config == null)
                    return;

                String network = config.getPrimaryNetwork();
                AppOpenProvider provider = getProvider(network);
                if (provider != null && provider.isAdAvailable()) {
                    Activity activity = activityRef != null ? activityRef.get() : null;
                    provider.showAppOpenAd(activity, new AppOpenProvider.AppOpenListener() {
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

        public boolean isAdAvailable() {
            return false; // Deprecated, check provider directly if needed
        }
    }
}
