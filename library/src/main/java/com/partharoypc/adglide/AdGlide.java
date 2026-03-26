package com.partharoypc.adglide;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.ViewGroup;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.format.AdNetwork;
import com.partharoypc.adglide.format.AppOpenAd;
import com.partharoypc.adglide.format.InterstitialAd;
import com.partharoypc.adglide.format.RewardedAd;
import com.partharoypc.adglide.format.BannerAd;
import com.partharoypc.adglide.format.NativeAd;
import com.partharoypc.adglide.util.AdGlideCallback;
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglide.util.ConsentManager;
import com.partharoypc.adglide.util.PerformanceLogger;

public class AdGlide {
    private static final String TAG = "AdGlide";

    private static AdGlideConfig config;
    private static Application currentApplication;
    private static ConsentManager consentManager;
    private static GlobalAdListener globalAdListener;

    // Cached Builders
    private static java.lang.ref.WeakReference<BannerAd.Builder> cachedBannerBuilder;

    private static int interstitialClickCounter = 1;
    private static int rewardedClickCounter = 1;
    private static int rewardedInterstitialClickCounter = 1;
    private static boolean isInitialized = false;
    private static Application.ActivityLifecycleCallbacks lifecycleCallbacks;
    private static boolean isAppOpenRegistered = false;

    /**
     * Retrieves the global AdGlide configuration.
     * 
     * @return The active AdGlideConfig.
     */
    @Nullable
    public static AdGlideConfig getConfig() {
        return config;
    }

    /**
     * Starts the SDK initialization process with a global configuration.
     * 
     * @param application The exact Application instance.
     * @param glideConfig The global configuration for AdGlide.
     */
    public static void initialize(@Nullable Application application, @Nullable AdGlideConfig glideConfig) {
        if (isInitialized) {
            AdGlideLog.w(TAG, "AdGlide is already initialized. Updating config instead.");
            updateConfig(glideConfig);
            return;
        }
        if (application == null || glideConfig == null) {
            AdGlideLog.e(TAG, "Cannot initialize AdGlide with null application or config.");
            return;
        }
        
        if (!glideConfig.isValid()) {
            AdGlideLog.e(TAG, "Initialization failed: Invalid Configuration. Check logs for details.");
            return;
        }

        config = glideConfig;
        currentApplication = application;
        isInitialized = true;

        // Initialize SDKs based on the global configuration
        new AdNetwork.Initialize(application)
                .config(glideConfig)
                .build();

        if (config.getAdStatus()) {
            if (config.isAppOpenEnabled() && !isAppOpenRegistered) {
                registerAppOpenLifecycle(application);
                isAppOpenRegistered = true;
            }
        }
        PerformanceLogger.log("Core", "AdGlide initialized (v" + BuildConfig.VERSION_NAME + " - Premium)");
        
        // Pre-cache House Ad assets for offline support
        preloadHouseAds(application);
    }

    /**
     * Completely shuts down the SDK and unregisters all callbacks.
     */
    public static void destroy() {
        if (!isInitialized || currentApplication == null) return;
        
        if (lifecycleCallbacks != null) {
            currentApplication.unregisterActivityLifecycleCallbacks(lifecycleCallbacks);
            lifecycleCallbacks = null;
        }
        
        isAppOpenRegistered = false;
        isInitialized = false;
        config = null;
        currentApplication = null;
        consentManager = null;
        globalAdListener = null;
        cachedBannerBuilder = null;
        
        com.partharoypc.adglide.util.AdPoolManager.clear();
        AdGlideLog.d(TAG, "AdGlide SDK destroyed.");
    }

    /**
     * Updates the global configuration dynamically at runtime.
     * Re-initializes any necessary networks.
     */
    public static void updateConfig(AdGlideConfig newConfig) {
        if (!isInitialized || currentApplication == null)
            return;
        config = newConfig;

        new AdNetwork.Initialize(currentApplication)
                .config(config)
                .build();

        // Ensure AppOpen is registered/unregistered if status changed
        if (config.getAdStatus() && config.isAppOpenEnabled()) {
            if (!isAppOpenRegistered) {
                registerAppOpenLifecycle(currentApplication);
                isAppOpenRegistered = true;
            }
        }

        // Re-cache House Ad assets if config changed
        preloadHouseAds(currentApplication);
    }

    private static void preloadHouseAds(android.content.Context context) {
        if (config == null || !config.isHouseAdEnabled() || context == null) return;

        AdGlideLog.d(TAG, "Proactively pre-fetching House Ad assets for offline support...");
        
        java.util.Set<String> assetUrls = new java.util.HashSet<>();
        if (config.getHouseAdBannerImage() != null && !config.getHouseAdBannerImage().isEmpty()) {
            assetUrls.add(config.getHouseAdBannerImage());
        }
        if (config.getHouseAdInterstitialImage() != null && !config.getHouseAdInterstitialImage().isEmpty()) {
            assetUrls.add(config.getHouseAdInterstitialImage());
        }
        if (config.getHouseAdNativeImage() != null && !config.getHouseAdNativeImage().isEmpty()) {
            assetUrls.add(config.getHouseAdNativeImage());
        }
        if (config.getHouseAdNativeIcon() != null && !config.getHouseAdNativeIcon().isEmpty()) {
            assetUrls.add(config.getHouseAdNativeIcon());
        }

        for (String url : assetUrls) {
            com.partharoypc.adglide.util.ImageDownloader.downloadImage(context, url, new com.partharoypc.adglide.util.ImageDownloader.ImageLoaderCallback() {
                @Override
                public void onImageLoaded(android.graphics.Bitmap bitmap) {
                    // Cached successfully
                }

                @Override
                public void onError(Exception e) {
                    // Silently fail pre-fetch (will retry during ad load if needed)
                }
            });
        }
    }

    /**
     * Optional manual consent request. If enableGDPR(true) is set,
     * this should be called in the Splash Activity or first Activity.
     */
    public interface OnConsentListener {
        void onConsentComplete();
    }

    public static void requestConsent(Activity activity, OnConsentListener listener) {
        if (config == null || !config.isEnableGDPR()) {
            if (listener != null)
                listener.onConsentComplete();
            return;
        }

        if (consentManager == null) {
            consentManager = new com.partharoypc.adglide.util.ConsentManager(activity.getApplicationContext());
        }

        consentManager.requestConsent(activity, config.isDebugGDPR(), () -> {
            if (listener != null)
                listener.onConsentComplete();
        });
    }

    public static boolean canRequestAds() {
        if (consentManager != null)
            return consentManager.canRequestAds();
        return true; // Default to true if ConsentManager not yet initialized
    }

    public static boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Interface for global ad event tracking across the entire application.
     */
    public interface GlobalAdListener {
        void onAdLoaded(String format, String network);

        void onAdFailedToLoad(String format, String network, String error);

        void onAdShowed(String format, String network);

        void onAdClicked(String format, String network);

        void onAdDismissed(String format, String network);

        void onAdCompleted(String format, String network);
    }

    /**
     * Sets a global listener to track all ad events in the app.
     */
    public static void setGlobalAdListener(GlobalAdListener listener) {
        globalAdListener = listener;
    }

    public static void notifyAdLoaded(String format, String network) {
        if (globalAdListener != null)
            globalAdListener.onAdLoaded(format, network);
    }

    public static void notifyAdFailedToLoad(String format, String network, String error) {
        if (globalAdListener != null)
            globalAdListener.onAdFailedToLoad(format, network, error);
    }

    public static void notifyAdShowed(String format, String network) {
        if (globalAdListener != null)
            globalAdListener.onAdShowed(format, network);
    }

    public static void notifyAdClicked(String format, String network) {
        if (globalAdListener != null)
            globalAdListener.onAdClicked(format, network);
    }

    public static void notifyAdDismissed(String format, String network) {
        if (globalAdListener != null)
            globalAdListener.onAdDismissed(format, network);
    }

    public static void notifyAdCompleted(String format, String network) {
        if (globalAdListener != null)
            globalAdListener.onAdCompleted(format, network);
    }

    // --- Developer Friendly State Helpers ---

    /**
     * Checks if the overall AdGlide SDK is enabled and correctly configured.
     */
    public static boolean isAdsEnabled() {
        return config != null && config.getAdStatus();
    }

    public static boolean isBannerEnabled() {
        return isAdsEnabled() && config.isBannerEnabled();
    }

    public static boolean isInterstitialEnabled() {
        return isAdsEnabled() && config.isInterstitialEnabled();
    }

    public static boolean isRewardedEnabled() {
        return isAdsEnabled() && config.isRewardedEnabled();
    }

    public static boolean isNativeEnabled() {
        return isAdsEnabled() && config.isNativeEnabled();
    }

    public static boolean isAppOpenEnabled() {
        return isAdsEnabled() && config.isAppOpenEnabled();
    }

    public static boolean isRewardedInterstitialEnabled() {
        return isAdsEnabled() && config.isRewardedInterstitialEnabled();
    }

    // --- Centralized Preloading Logic ---

    /**
     * Preloads an ad of any format. For native ads, the default style is used.
     * Use {@link #preload(Activity, com.partharoypc.adglide.util.AdFormat, String)} for custom native styles.
     */
    public static void preload(Activity activity, com.partharoypc.adglide.util.AdFormat format) {
        preload(activity, format, null);
    }

    /**
     * Preloads an ad of any format into the pool for instant delivery.
     */
    public static void preload(Activity activity, com.partharoypc.adglide.util.AdFormat format, @Nullable String nativeStyle) {
        if (config == null || !config.getAdStatus()) return;

        switch (format) {
            case INTERSTITIAL:
                com.partharoypc.adglide.util.AdPoolManager.fillInterstitialPool(activity);
                break;
            case REWARDED:
                com.partharoypc.adglide.util.AdPoolManager.fillRewardedPool(activity);
                break;
            case REWARDED_INTERSTITIAL:
                com.partharoypc.adglide.util.AdPoolManager.fillRewardedInterstitialPool(activity);
                break;
            case NATIVE:
                if (nativeStyle != null) {
                    com.partharoypc.adglide.util.AdPoolManager.fillNativePool(activity, nativeStyle);
                }
                break;
            case APP_OPEN:
                com.partharoypc.adglide.util.AdPoolManager.fillAppOpenPool(activity);
                break;
            case BANNER:
                break;
        }
    }

    public static void showInterstitial(Activity activity, AdGlideCallback callback) {
        if (!isPreCheckOk(TAG, "Interstitial", callback)) return;

        if (!isInterstitialEnabled()) {
            AdGlideLog.d(TAG, "Interstitial Ad is disabled.");
            if (callback != null) callback.onAdDismissed();
            return;
        }

        if (interstitialClickCounter < config.getInterstitialInterval()) {
            PerformanceLogger.log("INTERSTITIAL", "Ad skipped — Interval not met ("
                    + interstitialClickCounter + "/" + config.getInterstitialInterval() + ")");
            interstitialClickCounter++;
            if (callback != null) callback.onAdDismissed();
            return;
        }

        PerformanceLogger.log("INTERSTITIAL", "Showing Interstitial Ad");

        if (com.partharoypc.adglide.util.AdPoolManager.hasInterstitial()) {
            InterstitialAd.Builder pooledAd = com.partharoypc.adglide.util.AdPoolManager.getInterstitial();
            pooledAd.show(activity, new InternalCallback(com.partharoypc.adglide.util.AdFormat.INTERSTITIAL, activity, callback));
        } else {
            new InterstitialAd.Builder(activity).loadAndShow(activity, new InternalCallback(com.partharoypc.adglide.util.AdFormat.INTERSTITIAL, activity, callback));
        }
    }

    private static boolean isPreCheckOk(String tag, String format, AdGlideCallback callback) {
        if (!isInitialized || config == null) {
            AdGlideLog.e(tag, "AdGlide is not initialized. Call AdGlide.initialize() first.");
            if (callback != null) callback.onAdDismissed();
            return false;
        }
        return true;
    }

    private static class InternalCallback implements AdGlideCallback {
        private final com.partharoypc.adglide.util.AdFormat format;
        private final Activity activity;
        private final AdGlideCallback externalCallback;

        InternalCallback(com.partharoypc.adglide.util.AdFormat format, Activity activity, AdGlideCallback externalCallback) {
            this.format = format;
            this.activity = activity;
            this.externalCallback = externalCallback;
        }

        @Override
        public void onAdShowed() {
            autoPreload();
            if (externalCallback != null) externalCallback.onAdShowed();
        }

        @Override
        public void onAdDismissed() {
            resetCounter();
            autoPreload();
            if (externalCallback != null) externalCallback.onAdDismissed();
        }

        @Override
        public void onAdCompleted() {
            if (externalCallback != null) externalCallback.onAdCompleted();
        }

        @Override
        public void onAdFailedToLoad(String error) {
            if (externalCallback != null) externalCallback.onAdFailedToLoad(error);
        }

        private void autoPreload() {
            if (config != null && config.isAutoLoadEnabled()) {
                preload(activity, format);
            }
        }

        private void resetCounter() {
            switch (format) {
                case INTERSTITIAL: interstitialClickCounter = 1; break;
                case REWARDED: rewardedClickCounter = 1; break;
                case REWARDED_INTERSTITIAL: rewardedInterstitialClickCounter = 1; break;
            }
        }
    }

    /**
     * Shows a pre-cached rewarded ad, or loads one on the fly if not cached.
     */
    public static void showRewarded(Activity activity, AdGlideCallback callback) {
        if (!isPreCheckOk(TAG, "Rewarded", callback)) return;

        if (!isRewardedEnabled()) {
            AdGlideLog.d(TAG, "Rewarded Ad is disabled.");
            if (callback != null) callback.onAdDismissed();
            return;
        }

        if (rewardedClickCounter < config.getRewardedInterval()) {
            AdGlideLog.d(TAG, "Rewarded Ad interval not met. Current counter: " + rewardedClickCounter);
            rewardedClickCounter++;
            if (callback != null) callback.onAdDismissed();
            return;
        }

        if (com.partharoypc.adglide.util.AdPoolManager.hasRewarded()) {
            RewardedAd.Builder pooledAd = com.partharoypc.adglide.util.AdPoolManager.getRewarded();
            pooledAd.showRewardedAd(activity, new InternalCallback(com.partharoypc.adglide.util.AdFormat.REWARDED, activity, callback));
        } else {
            new RewardedAd.Builder(activity).loadAndShow(activity, new InternalCallback(com.partharoypc.adglide.util.AdFormat.REWARDED, activity, callback));
        }
    }

    /**
     * Shows a pre-cached rewarded interstitial ad, or loads one on the fly if not
     * cached.
     */
    public static void showRewardedInterstitial(Activity activity, AdGlideCallback callback) {
        if (!isPreCheckOk(TAG, "RewardedInterstitial", callback)) return;

        if (!isRewardedInterstitialEnabled()) {
            AdGlideLog.d(TAG, "Rewarded Interstitial Ad is disabled.");
            if (callback != null) callback.onAdDismissed();
            return;
        }

        if (rewardedInterstitialClickCounter < config.getRewardedInterval()) {
            AdGlideLog.d(TAG, "Rewarded Interstitial Ad interval not met. Current counter: " + rewardedInterstitialClickCounter);
            rewardedInterstitialClickCounter++;
            if (callback != null) callback.onAdDismissed();
            return;
        }

        if (com.partharoypc.adglide.util.AdPoolManager.hasRewardedInterstitial()) {
            com.partharoypc.adglide.format.RewardedInterstitialAd.Builder pooledAd = 
                com.partharoypc.adglide.util.AdPoolManager.getRewardedInterstitial();
            pooledAd.showRewardedInterstitialAd(activity, new InternalCallback(com.partharoypc.adglide.util.AdFormat.REWARDED_INTERSTITIAL, activity, callback));
        } else {
            new com.partharoypc.adglide.format.RewardedInterstitialAd.Builder(activity)
                .loadAndShow(activity, new InternalCallback(com.partharoypc.adglide.util.AdFormat.REWARDED_INTERSTITIAL, activity, callback));
        }
    }

    /**
     * Shows a banner ad in the activity's default banner container.
     */
    public static void showBanner(Activity activity) {
        showBanner(activity, null);
    }

    public static void showBanner(Activity activity, ViewGroup container) {
        if (config == null || !isBannerEnabled()) return;
        
        BannerAd.Builder existing = cachedBannerBuilder != null ? cachedBannerBuilder.get() : null;
        if (existing != null) {
            existing.destroyAndDetachBanner();
        }
        
        BannerAd.Builder builder = new BannerAd.Builder(activity);
        if (container != null) builder.container(container);
        
        cachedBannerBuilder = new java.lang.ref.WeakReference<>(builder);
        builder.load();
    }

    public static void showNative(Activity activity, String nativeStyle) {
        showNative(activity, null, nativeStyle);
    }

    public static void showNative(Activity activity, ViewGroup container, String nativeStyle) {
        if (config == null || !isNativeEnabled()) return;

        if (com.partharoypc.adglide.util.AdPoolManager.hasNative(nativeStyle)) {
            com.partharoypc.adglide.format.NativeAd.Builder pooledAd = com.partharoypc.adglide.util.AdPoolManager.getNative(nativeStyle);
            pooledAd.attachToContainer(container, new AdGlideCallback() {
                @Override
                public void onAdShowed() {
                    preload(activity, com.partharoypc.adglide.util.AdFormat.NATIVE, nativeStyle);
                }
            });
        } else {
            PerformanceLogger.log("NATIVE", "Showing Native Ad Style: " + nativeStyle);
            NativeAd.Builder builder = new NativeAd.Builder(activity).style(nativeStyle);
            if (container != null) builder.container(container);
            builder.load(new AdGlideCallback() {
                @Override
                public void onAdShowed() {
                    preload(activity, com.partharoypc.adglide.util.AdFormat.NATIVE, nativeStyle);
                }
            });
        }
    }


    /**
     * @deprecated Use {@link #preload(Activity, com.partharoypc.adglide.util.AdFormat)} with {@link com.partharoypc.adglide.util.AdFormat#APP_OPEN}
     */
    @Deprecated
    public static void preloadAppOpen(Activity activity) {
        preload(activity, com.partharoypc.adglide.util.AdFormat.APP_OPEN);
    }

    private static int foregroundActivityCount = 0;
    private static boolean isChangingConfig = false;

    private static void registerAppOpenLifecycle(Application application) {
        lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                foregroundActivityCount++;
                if (foregroundActivityCount == 1 && !isChangingConfig) {
                    if (isAdsEnabled()) {
                        if (config.isAutoLoadEnabled()) {
                            if (config.isInterstitialEnabled()) preload(activity, com.partharoypc.adglide.util.AdFormat.INTERSTITIAL);
                            if (config.isRewardedEnabled()) preload(activity, com.partharoypc.adglide.util.AdFormat.REWARDED);
                            if (config.isRewardedInterstitialEnabled()) preload(activity, com.partharoypc.adglide.util.AdFormat.REWARDED_INTERSTITIAL);
                            if (config.isAppOpenEnabled()) preload(activity, com.partharoypc.adglide.util.AdFormat.APP_OPEN);
                        }
                        
                        if (config.isAppOpenEnabled()) {
                            List<String> excluded = config.getOpenAdExcludedActivities();
                            if (excluded == null || !excluded.contains(activity.getClass().getName())) {
                                showAppOpenAd(activity, null);
                            }
                        }
                    }
                }
                isChangingConfig = false;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {}

            @Override
            public void onActivityPaused(@NonNull Activity activity) {}

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                isChangingConfig = activity.isChangingConfigurations();
                foregroundActivityCount--;
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {}
        };
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }

    public static void showAppOpenAd(Activity activity) {
        showAppOpenAd(activity, false, null);
    }

    public static void showAppOpenAd(Activity activity, AdGlideCallback callback) {
        showAppOpenAd(activity, true, callback); // Default to ignore cooldown when manually requested
    }

    public static void showAppOpenAd(Activity activity, boolean ignoreCooldown, AdGlideCallback callback) {
        if (com.partharoypc.adglide.util.AdPoolManager.hasAppOpen()) {
            AppOpenAd.Builder pooledAd = com.partharoypc.adglide.util.AdPoolManager.getAppOpen();
            pooledAd.showAppOpenAd(ignoreCooldown, new AdGlideCallback() {
                @Override
                public void onAdShowed() {
                    com.partharoypc.adglide.util.AdPoolManager.fillAppOpenPool(activity);
                    if (callback != null)
                        callback.onAdShowed();
                }

                @Override
                public void onAdDismissed() {
                    com.partharoypc.adglide.util.AdPoolManager.fillAppOpenPool(activity);
                    if (callback != null)
                        callback.onAdDismissed();
                }

                @Override
                public void onAdFailedToLoad(String error) {
                    loadAndShowAppOpenOnFly(activity, ignoreCooldown, callback);
                }

                @Override
                public void onAdLoaded() {
                    if (callback != null)
                        callback.onAdLoaded();
                }

                @Override
                public void onAdCompleted() {
                    if (callback != null)
                        callback.onAdCompleted();
                }
            });
        } else {
            loadAndShowAppOpenOnFly(activity, ignoreCooldown, callback);
        }
    }

    private static void loadAndShowAppOpenOnFly(Activity activity, boolean ignoreCooldown, AdGlideCallback callback) {
        new AppOpenAd.Builder(activity).loadAndShow(activity, ignoreCooldown, new AdGlideCallback() {
            @Override
            public void onAdShowed() {
                com.partharoypc.adglide.util.AdPoolManager.fillAppOpenPool(activity);
                if (callback != null)
                    callback.onAdShowed();
            }

            @Override
            public void onAdDismissed() {
                com.partharoypc.adglide.util.AdPoolManager.fillAppOpenPool(activity);
                if (callback != null)
                    callback.onAdDismissed();
            }

            @Override
            public void onAdFailedToLoad(String error) {
                com.partharoypc.adglide.util.AdPoolManager.fillAppOpenPool(activity);
                if (callback != null)
                    callback.onAdFailedToLoad(error);
            }

            @Override
            public void onAdLoaded() {
                if (callback != null)
                    callback.onAdLoaded();
            }

            @Override
            public void onAdCompleted() {
                if (callback != null)
                    callback.onAdCompleted();
            }
        });
    }

    public static void showDebugHUD(Activity activity) {
        if (config != null && config.isEnableDebugHUD()) {
            activity.startActivity(
                    new android.content.Intent(activity, com.partharoypc.adglide.util.DebugActivity.class));
        }
    }
}
