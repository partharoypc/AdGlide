package com.partharoypc.adglide;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.format.AdNetwork;
import com.partharoypc.adglide.format.BannerAd;
import com.partharoypc.adglide.format.InterstitialAd;
import com.partharoypc.adglide.format.RewardedAd;
import com.partharoypc.adglide.format.AppOpenAd;
import com.partharoypc.adglide.format.NativeAd;
import com.partharoypc.adglide.format.RewardedInterstitialAd;
import com.partharoypc.adglide.util.AdFormat;
import com.partharoypc.adglide.util.AdGlideCallback;
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglide.util.ConsentManager;
import com.partharoypc.adglide.util.PerformanceLogger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class AdGlide {
    private static final String TAG = "AdGlide";

    private static AdGlideConfig config;
    private static Application currentApplication;
    private static ConsentManager consentManager;
    private static GlobalAdListener globalAdListener;

    // Cached Builders
    private static java.lang.ref.WeakReference<BannerAd.Builder> cachedBannerBuilder;

    private static final AtomicInteger interstitialClickCounter = new AtomicInteger(0);
    private static final AtomicInteger rewardedClickCounter = new AtomicInteger(0);
    private static final AtomicInteger rewardedInterstitialClickCounter = new AtomicInteger(0);

    // Time-Gap Protection
    private static final AtomicLong lastFullAdShowTime = new AtomicLong(0);
    private static final long GLOBAL_TIME_GAP_MS = 60000; // 60 seconds

    private static final AtomicBoolean isInitialized = new AtomicBoolean(false);
    private static final AtomicBoolean isAdShowing = new AtomicBoolean(false);
    private static Application.ActivityLifecycleCallbacks lifecycleCallbacks;
    private static final AtomicBoolean isAppOpenRegistered = new AtomicBoolean(false);
    private static final AtomicReference<java.lang.ref.WeakReference<Activity>> currentActivityRef = new AtomicReference<>(new java.lang.ref.WeakReference<>(null));

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
     * @param application The exact Application instance. Must not be null.
     * @param glideConfig The global configuration for AdGlide. Must not be null.
     */
    public static void initialize(@NonNull Application application, @NonNull AdGlideConfig glideConfig) {
        if (isInitialized.getAndSet(true)) {
            AdGlideLog.w(TAG, "AdGlide is already initialized. Updating config instead.");
            updateConfig(glideConfig);
            return;
        }
        
        if (!glideConfig.isValid()) {
            AdGlideLog.e(TAG, "Initialization failed: Invalid Configuration. Check logs for details.");
            return;
        }

        // Load stored House Ad metadata for offline resilience if current config allows
        if (glideConfig.isHouseAdEnabled()) {
            com.partharoypc.adglide.util.AdGlidePrefs prefs = new com.partharoypc.adglide.util.AdGlidePrefs(application);
            // Merge stored metadata if current values are empty
            if (glideConfig.getHouseAdBannerImage().isEmpty()) {
                glideConfig = prefs.applyStoredHouseAd(glideConfig.toBuilder()).build();
            }
        }

        config = glideConfig;
        currentApplication = application;

        // Initialize SDKs based on the global configuration
        new AdNetwork.Initialize(application)
                .config(glideConfig)
                .build();

        if (config.getAdStatus()) {
            if (config.isAppOpenEnabled() && !isAppOpenRegistered.getAndSet(true)) {
                registerActivityLifecycle(application);
                registerProcessLifecycle();
            }
        }
        PerformanceLogger.log("Core", "AdGlide initialized (v" + (config.isDebug() ? "DEBUG" : "RELEASE") + ")");
        
        // Pre-cache House Ad assets for offline support
        preloadHouseAds(application);
    }

    /**
     * Retrieves the global Application context.
     * 
     * @return The active Application context.
     */
    @Nullable
    public static android.content.Context getContext() {
        return currentApplication;
    }

    /**
     * Completely shuts down the SDK and unregisters all callbacks.
     */
    public static void destroy() {
        if (!isInitialized.getAndSet(false) || currentApplication == null) return;
        
        if (lifecycleCallbacks != null) {
            currentApplication.unregisterActivityLifecycleCallbacks(lifecycleCallbacks);
            lifecycleCallbacks = null;
        }
        
        isAppOpenRegistered.set(false);
        config = null;
        currentApplication = null;
        consentManager = null;
        globalAdListener = null;
        cachedBannerBuilder = null;
        
        com.partharoypc.adglide.util.AdPoolManager.clearPools();
        AdGlideLog.d(TAG, "AdGlide SDK destroyed.");
    }

    /**
     * Updates the global configuration dynamically at runtime.
     * 
     * @param newConfig The new configuration to apply.
     */
    public static void updateConfig(@NonNull AdGlideConfig newConfig) {
        if (!isInitialized.get() || currentApplication == null) {
            AdGlideLog.e(TAG, "Cannot update config: AdGlide is not initialized.");
            return;
        }
        config = newConfig;

        new AdNetwork.Initialize(currentApplication)
                .config(config)
                .build();

        // Ensure AppOpen is registered/unregistered if status changed
        if (config.getAdStatus() && config.isAppOpenEnabled()) {
            if (!isAppOpenRegistered.get()) {
                registerActivityLifecycle(currentApplication);
                registerProcessLifecycle();
                isAppOpenRegistered.set(true);
            }
        }

        // Re-cache House Ad assets if config changed
        preloadHouseAds(currentApplication);
    }

    private static void preloadHouseAds(android.content.Context context) {
        if (config == null || !config.isHouseAdEnabled() || context == null) return;

        // Save metadata for offline resilience
        new com.partharoypc.adglide.util.AdGlidePrefs(context).saveHouseAdMetadata(config);

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

    public static boolean isAdShowing() {
        return isAdShowing.get();
    }

    public static void setAdShowing(boolean showing) {
        isAdShowing.set(showing);
        AdGlideLog.d(TAG, "Global Ad Showing State: " + showing);
    }

    public static boolean isInitialized() {
        return isInitialized.get();
    }

    /**
     * Checks if a format show is "imminent" (within 1 interaction of the interval).
     * This is used by the AdPoolManager to optimize the match rate and reduce waste.
     */
    public static boolean isImminent(com.partharoypc.adglide.util.AdFormat format) {
        if (config == null) return false;
        
        switch (format) {
            case INTERSTITIAL:
                return (config.getInterstitialInterval() - interstitialClickCounter.get()) <= 1;
            case REWARDED:
                return (config.getRewardedInterval() - rewardedClickCounter.get()) <= 1;
            case REWARDED_INTERSTITIAL:
                return (config.getRewardedInterval() - rewardedInterstitialClickCounter.get()) <= 1;
            case APP_OPEN:
            case NATIVE:
            case BANNER:
                return true; // These formats are always "imminent" or handled differently
            default:
                return false;
        }
    }

    /**
     * Interface for global ad event tracking across the entire application.
     */
    public interface GlobalAdListener {
        void onAdRequested(String format, String network);

        void onAdLoaded(String format, String network);

        void onAdFailedToLoad(String format, String network, String error);

        void onAdShowed(String format, String network);

        void onAdClicked(String format, String network);

        void onAdDismissed(String format, String network);

        void onAdCompleted(String format, String network);

        void onLateMatchSaved(String format, String network);

        void onHealerSkip(String format, String network);
    }

    /**
     * Sets a global listener to track all ad events in the app.
     */
    public static void setGlobalAdListener(GlobalAdListener listener) {
        globalAdListener = listener;
    }

    public static void notifyAdRequested(String format, String network) {
        if (globalAdListener != null)
            globalAdListener.onAdRequested(format, network);
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
        setAdShowing(true);
        lastFullAdShowTime.set(System.currentTimeMillis());
        if (globalAdListener != null)
            globalAdListener.onAdShowed(format, network);
    }

    public static void notifyAdClicked(String format, String network) {
        if (globalAdListener != null)
            globalAdListener.onAdClicked(format, network);
    }

    public static void notifyAdDismissed(String format, String network) {
        setAdShowing(false);
        if (globalAdListener != null)
            globalAdListener.onAdDismissed(format, network);
    }

    public static void notifyAdCompleted(String format, String network) {
        if (globalAdListener != null)
            globalAdListener.onAdCompleted(format, network);
    }

    public static void notifyLateMatchSaved(String format, String network) {
        if (globalAdListener != null)
            globalAdListener.onLateMatchSaved(format, network);
    }

    public static void notifyHealerSkip(String format, String network) {
        if (globalAdListener != null)
            globalAdListener.onHealerSkip(format, network);
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

    /**
     * Shows an interstitial ad with an optional callback.
     * 
     * @param activity The host activity for the ad. Must not be null.
     * @param callback The listener for ad events. May be null.
     */
    public static void showInterstitial(@NonNull Activity activity, @Nullable AdGlideCallback callback) {
        if (!isPreCheckOk(TAG, "Interstitial", callback)) return;

        if (!isInterstitialEnabled()) {
            AdGlideLog.d(TAG, "Interstitial Ad is disabled via configuration.");
            if (callback != null) callback.onAdDismissed();
            return;
        }

        if (interstitialClickCounter.incrementAndGet() < config.getInterstitialInterval()) {
            PerformanceLogger.log("INTERSTITIAL", "Ad skipped — Interval not met ("
                    + (interstitialClickCounter.get()) + "/" + config.getInterstitialInterval() + ")");
            if (callback != null) callback.onAdDismissed();
            return;
        }

        PerformanceLogger.log("INTERSTITIAL", "Triggering Interstitial Ad");

        // Strict "Only Once" show policy
        isAdShowing.set(true);

        // The unified Builder now manages both 'Loaded' (pooled) and 'LoadAndShow' logic
        InternalCallback internalCallback = new InternalCallback(com.partharoypc.adglide.util.AdFormat.INTERSTITIAL, activity, callback);
        
        InterstitialAd.Builder pooledAd = com.partharoypc.adglide.util.AdPoolManager.getInterstitial();
        if (pooledAd != null) {
            pooledAd.show(activity, internalCallback);
        } else {
            new InterstitialAd.Builder(activity).loadAndShow(activity, internalCallback);
        }
    }

    private static boolean isPreCheckOk(@NonNull String tag, @NonNull String format, @Nullable AdGlideCallback callback) {
        if (!isInitialized.get() || config == null) {
            AdGlideLog.e(tag, "AdGlide is not initialized. Call AdGlide.initialize() first before requesting: " + format);
            if (callback != null) callback.onAdDismissed();
            return false;
        }
        if (isAdShowing.get()) {
            AdGlideLog.d(tag, "A full-screen ad is already showing. Ignoring request for [" + format + "] to ensure 'only once' policy.");
            if (callback != null) callback.onAdDismissed();
            return false;
        }

        // TIME-GAP PROTECTION (UX Improvement)
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFullAdShowTime.get() < GLOBAL_TIME_GAP_MS) {
            long remaining = (GLOBAL_TIME_GAP_MS - (currentTime - lastFullAdShowTime.get())) / 1000;
            AdGlideLog.d(tag, "Time-Gap protection active. Skipping [" + format + "] (Backoff: " + remaining + "s)");
            if (callback != null) callback.onAdDismissed();
            return false;
        }

        return true;
    }

    private static class InternalCallback implements AdGlideCallback {
        private final AdFormat format;
        private final Activity activity;
        private final AdGlideCallback externalCallback;

        InternalCallback(AdFormat format, Activity activity, AdGlideCallback externalCallback) {
            this.format = format;
            this.activity = activity;
            this.externalCallback = externalCallback;
        }

        @Override
        public void onAdShowed() {
            lastFullAdShowTime.set(System.currentTimeMillis());
            autoPreload();
            if (externalCallback != null) externalCallback.onAdShowed();
        }

        @Override
        public void onAdDismissed() {
            isAdShowing.set(false);
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
            isAdShowing.set(false); // Reset if show attempt fails after loading
            if (externalCallback != null) externalCallback.onAdFailedToLoad(error);
        }

        private void autoPreload() {
            // Always preload the next ad after one is used, as per "Preload after Show" logic
            preload(activity, format);
        }

        private void resetCounter() {
            switch (format) {
                case INTERSTITIAL: interstitialClickCounter.set(0); break;
                case REWARDED: rewardedClickCounter.set(0); break;
                case REWARDED_INTERSTITIAL: rewardedInterstitialClickCounter.set(0); break;
            }
        }
    }

    /**
     * Shows a pre-cached rewarded ad, or loads one on the fly if not cached.
     * 
     * @param activity The host activity. Must not be null.
     * @param callback Ad event listener. May be null.
     */
    public static void showRewarded(@NonNull Activity activity, @Nullable AdGlideCallback callback) {
        if (!isPreCheckOk(TAG, "Rewarded", callback)) return;

        if (!isRewardedEnabled()) {
            AdGlideLog.d(TAG, "Rewarded Ad is disabled via configuration.");
            if (callback != null) callback.onAdDismissed();
            return;
        }

        if (rewardedClickCounter.incrementAndGet() < config.getRewardedInterval()) {
            AdGlideLog.d(TAG, "Rewarded Ad interval not met (" + rewardedClickCounter.get() + "/" + config.getRewardedInterval() + ")");
            if (callback != null) callback.onAdDismissed();
            return;
        }

        // Strict "Only Once" show policy
        isAdShowing.set(true);

        InternalCallback internalCallback = new InternalCallback(com.partharoypc.adglide.util.AdFormat.REWARDED, activity, callback);

        RewardedAd.Builder pooledAd = com.partharoypc.adglide.util.AdPoolManager.getRewarded();
        if (pooledAd != null) {
            pooledAd.show(activity, internalCallback);
        } else {
            new RewardedAd.Builder(activity).loadAndShow(activity, internalCallback);
        }
    }

    /**
     * Shows a pre-cached rewarded interstitial ad, or loads one on the fly if not
     * cached.
     * 
     * @param activity The host activity. Must not be null.
     * @param callback Ad event listener. May be null.
     */
    public static void showRewardedInterstitial(@NonNull Activity activity, @Nullable AdGlideCallback callback) {
        if (!isPreCheckOk(TAG, "RewardedInterstitial", callback)) return;

        if (!isRewardedInterstitialEnabled()) {
            AdGlideLog.d(TAG, "Rewarded Interstitial Ad is disabled via configuration.");
            if (callback != null) callback.onAdDismissed();
            return;
        }

        if (rewardedInterstitialClickCounter.incrementAndGet() < config.getRewardedInterval()) {
            AdGlideLog.d(TAG, "Rewarded Interstitial Ad interval not met (" + rewardedInterstitialClickCounter.get() + "/" + config.getRewardedInterval() + ")");
            if (callback != null) callback.onAdDismissed();
            return;
        }

        // Strict "Only Once" show policy
        isAdShowing.set(true);

        InternalCallback internalCallback = new InternalCallback(com.partharoypc.adglide.util.AdFormat.REWARDED_INTERSTITIAL, activity, callback);

        com.partharoypc.adglide.format.RewardedInterstitialAd.Builder pooledAd = com.partharoypc.adglide.util.AdPoolManager.getRewardedInterstitial();
        if (pooledAd != null) {
            pooledAd.show(activity, internalCallback);
        } else {
            new com.partharoypc.adglide.format.RewardedInterstitialAd.Builder(activity)
                .loadAndShow(activity, internalCallback);
        }
    }

    /**
     * Shows a banner ad in the activity's default banner container.
     * 
     * @param activity The host activity. Must not be null.
     */
    public static void showBanner(@NonNull Activity activity) {
        showBanner(activity, null);
    }

    /**
     * Shows a banner ad in the specified container.
     * 
     * @param activity The host activity. Must not be null.
     * @param container The ViewGroup to host the banner. May be null (provider may use default).
     */
    public static void showBanner(@NonNull Activity activity, @Nullable ViewGroup container) {
        if (!isPreCheckOk(TAG, "Banner", null)) return;
        if (!isBannerEnabled()) {
             AdGlideLog.d(TAG, "Banner Ad is disabled via configuration.");
             return;
        }
        
        BannerAd.Builder existing = cachedBannerBuilder != null ? cachedBannerBuilder.get() : null;
        if (existing != null) {
            existing.destroyAndDetachBanner();
        }
        
        BannerAd.Builder builder = new BannerAd.Builder(activity);
        if (container != null) builder.container(container);
        
        cachedBannerBuilder = new java.lang.ref.WeakReference<>(builder);
        builder.load();
    }

    /**
     * Shows a native ad in the activity's default container with a specific style.
     * 
     * @param activity The host activity. Must not be null.
     * @param nativeStyle The style identifier for the native ad. Must not be null.
     */
    public static void showNative(@NonNull Activity activity, @NonNull String nativeStyle) {
        showNative(activity, null, nativeStyle);
    }

    /**
     * Shows a native ad in the specified container with a specific style.
     * 
     * @param activity The host activity. Must not be null.
     * @param container The ViewGroup to host the native ad. May be null.
     * @param nativeStyle The style identifier. Must not be null.
     */
    public static void showNative(@NonNull Activity activity, @Nullable ViewGroup container, @NonNull String nativeStyle) {
        if (!isPreCheckOk(TAG, "Native", null)) return;
        if (!isNativeEnabled()) {
            AdGlideLog.d(TAG, "Native Ad is disabled via configuration.");
            return;
        }

        if (com.partharoypc.adglide.util.AdPoolManager.hasNative(nativeStyle)) {
            com.partharoypc.adglide.format.NativeAd.Builder pooledAd = com.partharoypc.adglide.util.AdPoolManager.getNative(nativeStyle);
            if (pooledAd != null) {
                pooledAd.attachToContainer(container, new AdGlideCallback() {
                    @Override
                    public void onAdShowed() {
                        preload(activity, com.partharoypc.adglide.util.AdFormat.NATIVE, nativeStyle);
                    }
                });
            } else {
                loadNativeOnFly(activity, container, nativeStyle);
            }
        } else {
            loadNativeOnFly(activity, container, nativeStyle);
        }
    }

    private static void loadNativeOnFly(Activity activity, ViewGroup container, String nativeStyle) {
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


    /**
     * @deprecated Use {@link #preload(Activity, com.partharoypc.adglide.util.AdFormat)} with {@link com.partharoypc.adglide.util.AdFormat#APP_OPEN}
     */
    @Deprecated
    public static void preloadAppOpen(Activity activity) {
        preload(activity, com.partharoypc.adglide.util.AdFormat.APP_OPEN);
    }

    private static long lastBackgroundTime = 0;

    private static void registerProcessLifecycle() {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            ProcessLifecycleOwner.get().getLifecycle().addObserver(new DefaultLifecycleObserver() {
                @Override
                public void onStart(@NonNull LifecycleOwner owner) {
                    AdGlideLog.d(TAG, "App moved to foreground. Checking for App Open ads...");
                    
                    if (isAdsEnabled() && config.isAutoLoadEnabled()) {
                        // Background preloading to ensure next ad is ready
                        com.partharoypc.adglide.util.AdPoolManager.fillInterstitialPool(null);
                        com.partharoypc.adglide.util.AdPoolManager.fillRewardedPool(null);
                    }

                    // SMART BACKOFF: Only show if the app was in the background for >30 seconds
                    long backgroundTime = System.currentTimeMillis() - lastBackgroundTime;
                    if (lastBackgroundTime > 0 && backgroundTime < 30000) {
                        AdGlideLog.d(TAG, "SKIPPING App Open Ad: User returned within " + (backgroundTime / 1000) + "s. (Backoff Threshold: 30s)");
                        return;
                    }

                    if (isAdsEnabled() && config.isAppOpenEnabled()) {
                        java.lang.ref.WeakReference<Activity> ref = currentActivityRef.get();
                        Activity activity = ref != null ? ref.get() : null;
                        if (activity != null && !activity.isFinishing() && (android.os.Build.VERSION.SDK_INT < 17 || !activity.isDestroyed())) {
                            showAppOpenAd(activity, false, null);
                        } else {
                            AdGlideLog.w(TAG, "Cannot show App Open ad: No active Activity context.");
                        }
                    }
                }

                @Override
                public void onStop(@NonNull LifecycleOwner owner) {
                    lastBackgroundTime = System.currentTimeMillis();
                    AdGlideLog.d(TAG, "App moved to background. Recording lastBackgroundTime.");
                }
            });
        });
    }

    private static void registerActivityLifecycle(Application application) {
        if (lifecycleCallbacks != null) return;
        
        lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}
            @Override public void onActivityStarted(@NonNull Activity activity) {
                currentActivityRef.set(new java.lang.ref.WeakReference<>(activity));
            }
            @Override public void onActivityResumed(@NonNull Activity activity) {
                currentActivityRef.set(new java.lang.ref.WeakReference<>(activity));
            }
            @Override public void onActivityPaused(@NonNull Activity activity) {}
            @Override public void onActivityStopped(@NonNull Activity activity) {}
            @Override public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}
            @Override public void onActivityDestroyed(@NonNull Activity activity) {
                java.lang.ref.WeakReference<Activity> ref = currentActivityRef.get();
                if (ref != null && ref.get() == activity) {
                    ref.clear();
                }
            }
        };
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }

    /**
     * Shows an app open ad with default settings.
     * 
     * @param activity The host activity. Must not be null.
     */
    public static void showAppOpenAd(@NonNull Activity activity) {
        showAppOpenAd(activity, false, null);
    }

    /**
     * Shows an app open ad with a callback.
     * 
     * @param activity The host activity. Must not be null.
     * @param callback Ad event listener. May be null.
     */
    public static void showAppOpenAd(@NonNull Activity activity, @Nullable AdGlideCallback callback) {
        showAppOpenAd(activity, true, callback); // Default to ignore cooldown when manually requested
    }

    /**
     * Shows an app open ad with cooldown control and a callback.
     * 
     * @param activity The host activity. Must not be null.
     * @param ignoreCooldown Whether to ignore the configured cooldown interval.
     * @param callback Ad event listener. May be null.
     */
    public static void showAppOpenAd(@NonNull Activity activity, boolean ignoreCooldown, @Nullable AdGlideCallback callback) {
        if (!isPreCheckOk(TAG, "AppOpen", callback)) return;
        if (!isAppOpenEnabled()) {
            AdGlideLog.d(TAG, "App Open Ad is disabled via configuration.");
            if (callback != null) callback.onAdDismissed();
            return;
        }

        AppOpenAd.Builder pooledAd = com.partharoypc.adglide.util.AdPoolManager.getAppOpen();
        if (pooledAd != null) {
            pooledAd.showAppOpenAd(activity, ignoreCooldown, new AdGlideCallback() {
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
            });
        } else {
            loadAndShowAppOpenOnFly(activity, ignoreCooldown, callback);
        }
    }

    private static void loadAndShowAppOpenOnFly(Activity activity, boolean ignoreCooldown, AdGlideCallback callback) {
        PerformanceLogger.log("APP_OPEN", "Loading App Open Ad on the fly");
        new AppOpenAd.Builder(activity).loadAndShow(activity, ignoreCooldown, new AdGlideCallback() {
            @Override
            public void onAdShowed() {
                com.partharoypc.adglide.util.AdPoolManager.fillAppOpenPool(activity);
                if (callback != null) callback.onAdShowed();
            }

            @Override
            public void onAdDismissed() {
                com.partharoypc.adglide.util.AdPoolManager.fillAppOpenPool(activity);
                if (callback != null) callback.onAdDismissed();
            }

            @Override
            public void onAdFailedToLoad(String error) {
                if (callback != null) callback.onAdFailedToLoad(error);
            }
        });
    }

    /**
     * Resets the entire SDK state, including all cached pools and NetworkHealer history.
     * Dangerous: Use only for debugging or when user specifically requests ad state reset.
     */
    public static void reset(@NonNull Context context) {
        if (isInitialized.get()) {
            com.partharoypc.adglide.util.AdPoolManager.clearPools();
            com.partharoypc.adglide.util.NetworkHealer.getInstance(context).reset();
            AdGlideLog.i(TAG, "Full SDK State Reset Triggered.");
        }
    }
}
