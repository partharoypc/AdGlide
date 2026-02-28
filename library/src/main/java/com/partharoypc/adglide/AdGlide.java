package com.partharoypc.adglide;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.format.AdNetwork;
import com.partharoypc.adglide.format.AppOpenAd;
import com.partharoypc.adglide.format.InterstitialAd;
import com.partharoypc.adglide.format.RewardedAd;
import com.partharoypc.adglide.format.BannerAd;
import com.partharoypc.adglide.format.NativeAd;
import com.partharoypc.adglide.util.OnInterstitialAdDismissedListener;
import com.partharoypc.adglide.util.OnShowAdCompleteListener;
import com.partharoypc.adglide.util.OnRewardedAdCompleteListener;
import com.partharoypc.adglide.util.OnRewardedAdDismissedListener;

public class AdGlide {
    private static final String TAG = "AdGlide";

    private static AdGlideConfig config;
    private static Application currentApplication;
    private static com.partharoypc.adglide.util.ConsentManager consentManager;

    // Cached Buidlers
    private static InterstitialAd.Builder cachedInterstitial;
    private static RewardedAd.Builder cachedRewarded;
    private static com.partharoypc.adglide.format.RewardedInterstitialAd.Builder cachedRewardedInterstitial;
    private static AppOpenAd.Builder cachedAppOpen;

    private static int interstitialClickCounter = 1;
    private static int rewardedClickCounter = 1;
    private static int rewardedInterstitialClickCounter = 1;
    private static boolean isInitialized = false;
    private static boolean isAppOpenRegistered = false;

    private static java.lang.ref.WeakReference<Activity> currentActivityRef;

    /**
     * Retrieves the global AdGlide configuration.
     * 
     * @return The active AdGlideConfig.
     */
    public static AdGlideConfig getConfig() {
        return config;
    }

    /**
     * Starts the SDK initialization process with a global configuration.
     * 
     * @param application The exact Application instance.
     * @param glideConfig The global configuration for AdGlide.
     */
    public static void initialize(Application application, AdGlideConfig glideConfig) {
        if (isInitialized) {
            Log.w(TAG, "AdGlide is already initialized. Updating config instead.");
            updateConfig(glideConfig);
            return;
        }
        config = glideConfig;
        currentApplication = application;
        isInitialized = true;

        // Initialize SDKs based on the global configuration
        new AdNetwork.Initialize(application)
                .status(glideConfig.getAdStatus())
                .network(glideConfig.getPrimaryNetwork())
                .backups(glideConfig.getBackupNetworks().toArray(new String[0]))
                .testMode(glideConfig.isTestMode())
                .debug(glideConfig.isDebug())
                .adMobId(glideConfig.getAdMobAppId())
                .appLovinId(glideConfig.getAppLovinSdkKey())
                .ironSourceId(glideConfig.getIronSourceAppKey())
                .startAppId(glideConfig.getStartAppId())
                .unityId(glideConfig.getUnityGameId())
                .wortiseId(glideConfig.getWortiseAppId())
                .build();

        if (config.getAdStatus()) {
            if (config.isAppOpenAdEnabled() && !isAppOpenRegistered) {
                registerAppOpenLifecycle(application);
                isAppOpenRegistered = true;
            }
        }
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
                .status(config.getAdStatus())
                .network(config.getPrimaryNetwork())
                .backups(config.getBackupNetworks().toArray(new String[0]))
                .testMode(config.isTestMode())
                .debug(config.isDebug())
                .adMobId(config.getAdMobAppId())
                .appLovinId(config.getAppLovinSdkKey())
                .ironSourceId(config.getIronSourceAppKey())
                .startAppId(config.getStartAppId())
                .unityId(config.getUnityGameId())
                .wortiseId(config.getWortiseAppId())
                .build();
    }

    /**
     * Updates the SDK configuration dynamically from a remote JSON URL.
     */
    public static void fetchRemoteConfig(String url,
            com.partharoypc.adglide.util.RemoteConfigManager.OnConfigFetchedListener listener) {
        com.partharoypc.adglide.util.RemoteConfigManager.fetch(url,
                new com.partharoypc.adglide.util.RemoteConfigManager.OnConfigFetchedListener() {
                    @Override
                    public void onSuccess(AdGlideConfig.Builder updatedBuilder) {
                        config = updatedBuilder.build();
                        if (listener != null)
                            listener.onSuccess(updatedBuilder);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (listener != null)
                            listener.onFailure(e);
                    }
                });
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

    /**
     * For backward compatibility if someone still calls initialize with just a
     * Context.
     */
    public static void initialize(Context context, AdGlideConfig glideConfig) {
        if (context instanceof Application) {
            initialize((Application) context, glideConfig);
        } else if (context instanceof Activity) {
            initialize(((Activity) context).getApplication(), glideConfig);
        } else {
            // Fallback (auto-caching features that require Application will not work fully)
            config = glideConfig;
            new AdNetwork.Initialize(context)
                    .status(glideConfig.getAdStatus())
                    .network(glideConfig.getPrimaryNetwork())
                    .backups(glideConfig.getBackupNetworks().toArray(new String[0]))
                    .testMode(glideConfig.isTestMode())
                    .debug(glideConfig.isDebug())
                    .adMobId(glideConfig.getAdMobAppId())
                    .appLovinId(glideConfig.getAppLovinSdkKey())
                    .ironSourceId(glideConfig.getIronSourceAppKey())
                    .startAppId(glideConfig.getStartAppId())
                    .unityId(glideConfig.getUnityGameId())
                    .wortiseId(glideConfig.getWortiseAppId())
                    .build();
        }
    }

    // --- Preloading Logic ---

    public static void preloadInterstitial(Activity activity) {
        if (config != null && config.getAdStatus()) {
            cachedInterstitial = new InterstitialAd.Builder(activity);
            cachedInterstitial.load();
        }
    }

    public static void preloadRewarded(Activity activity) {
        if (config != null && config.getAdStatus()) {
            cachedRewarded = new RewardedAd.Builder(activity);
            cachedRewarded.load();
        }
    }

    public static void preloadRewardedInterstitial(Activity activity) {
        if (config != null && config.getAdStatus()) {
            cachedRewardedInterstitial = new com.partharoypc.adglide.format.RewardedInterstitialAd.Builder(activity);
            // Reusing build internally to perform a standard cache load
            cachedRewardedInterstitial.loadRewardedInterstitialAd(null, null);
        }
    }

    // --- Facade Show Methods ---

    /**
     * Shows a pre-cached interstitial ad, or loads one on the fly if not cached.
     * Once the ad is dismissed, it automatically caches the next one (if
     * configured).
     */
    public static void showInterstitial(Activity activity, OnInterstitialAdDismissedListener listener) {
        if (config == null || !isInterstitialEnabled()) {
            if (listener != null)
                listener.onInterstitialAdDismissed();
            return;
        }

        if (interstitialClickCounter < config.getInterstitialInterval()) {
            com.partharoypc.adglide.util.PerformanceLogger.log("INTERSTITIAL", "Ad skipped — Interval not met ("
                    + interstitialClickCounter + "/" + config.getInterstitialInterval() + ")");
            interstitialClickCounter++;
            if (listener != null)
                listener.onInterstitialAdDismissed();
            return;
        }

        com.partharoypc.adglide.util.PerformanceLogger.log("INTERSTITIAL", "Showing Interstitial Ad");

        if (cachedInterstitial != null && cachedInterstitial.isAdLoaded()) {
            cachedInterstitial.show(activity, null, new OnInterstitialAdDismissedListener() {
                @Override
                public void onInterstitialAdDismissed() {
                    interstitialClickCounter = 1; // Reset after successfully showing
                    if (config.isAutoLoadInterstitial()) {
                        preloadInterstitial(activity);
                    }
                    if (listener != null)
                        listener.onInterstitialAdDismissed();
                }
            });
        } else {
            // Fallback: load and show on the fly
            new InterstitialAd.Builder(activity).loadAndShow(activity, null, new OnInterstitialAdDismissedListener() {
                @Override
                public void onInterstitialAdDismissed() {
                    interstitialClickCounter = 1; // Reset after fallback show/attempt
                    if (listener != null)
                        listener.onInterstitialAdDismissed();
                }
            });
        }
    }

    /**
     * Shows a pre-cached rewarded ad, or loads one on the fly if not cached.
     */
    public static void showRewarded(Activity activity, OnRewardedAdCompleteListener onComplete,
            OnRewardedAdDismissedListener onDismiss) {
        if (config == null || !isRewardedEnabled()) {
            if (onDismiss != null)
                onDismiss.onRewardedAdDismissed();
            return;
        }

        if (rewardedClickCounter < config.getRewardedInterval()) {
            Log.d(TAG, "Rewarded Ad interval not met. Current counter: " + rewardedClickCounter);
            rewardedClickCounter++;
            if (onDismiss != null)
                onDismiss.onRewardedAdDismissed();
            return;
        }

        if (cachedRewarded != null && cachedRewarded.isAdAvailable()) {
            cachedRewarded.showRewardedAd(activity, onComplete, new OnRewardedAdDismissedListener() {
                @Override
                public void onRewardedAdDismissed() {
                    rewardedClickCounter = 1; // Reset after successfully showing
                    if (config.isAutoLoadRewarded()) {
                        preloadRewarded(activity);
                    }
                    if (onDismiss != null)
                        onDismiss.onRewardedAdDismissed();
                }
            }, null);
        } else {
            // Fallback: load and show on the fly
            new RewardedAd.Builder(activity).loadAndShow(activity, onComplete, new OnRewardedAdDismissedListener() {
                @Override
                public void onRewardedAdDismissed() {
                    rewardedClickCounter = 1; // Reset after fallback show/attempt
                    if (onDismiss != null)
                        onDismiss.onRewardedAdDismissed();
                }
            }, null);
        }
    }

    /**
     * Shows a pre-cached rewarded interstitial ad, or loads one on the fly if not
     * cached.
     */
    public static void showRewardedInterstitial(Activity activity, OnRewardedAdCompleteListener onComplete,
            OnRewardedAdDismissedListener onDismiss, com.partharoypc.adglide.util.OnRewardedAdErrorListener onError) {
        if (config == null || !isRewardedInterstitialEnabled()) {
            if (onDismiss != null)
                onDismiss.onRewardedAdDismissed();
            return;
        }

        if (rewardedInterstitialClickCounter < config.getRewardedInterval()) {
            Log.d(TAG,
                    "Rewarded Interstitial Ad interval not met. Current counter: " + rewardedInterstitialClickCounter);
            rewardedInterstitialClickCounter++;
            if (onDismiss != null)
                onDismiss.onRewardedAdDismissed();
            return;
        }

        // Technically RewardedInterstitialAd has no direct isAdAvailable() externally
        // exposed, but we know it triggers show directly or load backups.
        // We will just try to trigger showRewardedInterstitialAd. If empty,
        // loadAndShow.
        if (cachedRewardedInterstitial != null) {
            // Attempt to show what is cached. If nothing is cached, it'll fail cleanly with
            // an error or dismiss. But we should ideally guarantee loadAndShow.
            // RewardedInterstitialAd doesn't have a public isAdAvailable.
            cachedRewardedInterstitial.showRewardedInterstitialAd(activity, onComplete,
                    new OnRewardedAdDismissedListener() {
                        @Override
                        public void onRewardedAdDismissed() {
                            rewardedInterstitialClickCounter = 1;
                            if (config.isAutoLoadRewarded()) {
                                preloadRewardedInterstitial(activity);
                            }
                            if (onDismiss != null)
                                onDismiss.onRewardedAdDismissed();
                        }
                    }, new com.partharoypc.adglide.util.OnRewardedAdErrorListener() {
                        @Override
                        public void onRewardedAdError() {
                            // Cache missed or failed. Fallback to loadAndShow
                            new com.partharoypc.adglide.format.RewardedInterstitialAd.Builder(activity)
                                    .loadAndShow(activity, onComplete, new OnRewardedAdDismissedListener() {
                                        @Override
                                        public void onRewardedAdDismissed() {
                                            rewardedInterstitialClickCounter = 1;
                                            if (onDismiss != null)
                                                onDismiss.onRewardedAdDismissed();
                                        }
                                    }, onError);
                        }
                    });
        } else {
            // Fallback immediately
            new com.partharoypc.adglide.format.RewardedInterstitialAd.Builder(activity).loadAndShow(activity,
                    onComplete, new OnRewardedAdDismissedListener() {
                        @Override
                        public void onRewardedAdDismissed() {
                            rewardedInterstitialClickCounter = 1;
                            if (onDismiss != null)
                                onDismiss.onRewardedAdDismissed();
                        }
                    }, onError);
        }
    }

    /**
     * Shows a banner ad in the activity's default banner container.
     */
    public static void showBanner(Activity activity) {
        if (config == null || !isBannerEnabled())
            return;
        new BannerAd.Builder(activity).load();
    }

    /**
     * Shows a banner ad in a custom container.
     */
    public static void showBanner(Activity activity, ViewGroup container) {
        if (config == null || !isBannerEnabled())
            return;
        new BannerAd.Builder(activity).container(container).load();
    }

    /**
     * Shows a native ad in the specified containter without needing a builder.
     */
    public static void showNative(Activity activity, String nativeStyle) {
        if (config == null || !isNativeEnabled())
            return;
        com.partharoypc.adglide.util.PerformanceLogger.log("NATIVE", "Showing Native Ad Style: " + nativeStyle);
        new NativeAd.Builder(activity).style(nativeStyle).load();
    }

    /**
     * Shows a native ad in a custom container with specified style.
     */
    public static void showNative(Activity activity, ViewGroup container, String nativeStyle) {
        if (config == null || !isNativeEnabled())
            return;
        com.partharoypc.adglide.util.PerformanceLogger.log("NATIVE", "Showing Native Ad Style: " + nativeStyle);
        new NativeAd.Builder(activity).container(container).style(nativeStyle).load();
    }

    /**
     * Shows the integrated SDK debugger HUD.
     */
    public static void showDebugHUD(Activity activity) {
        if (config != null && config.isEnableDebugHUD()) {
            activity.startActivity(
                    new android.content.Intent(activity, com.partharoypc.adglide.util.DebugActivity.class));
        }
    }

    // --- Global App Open Ad Management ---

    private static void registerAppOpenLifecycle(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                currentActivityRef = new java.lang.ref.WeakReference<>(activity);
                // Show App Open Ad automatically if it isn't Specifically Excluded
                if (config != null && config.getAdStatus() && config.isAppOpenAdEnabled()) {
                    // Check exclusion list
                    if (!config.getOpenAdExcludedActivities().contains(activity.getClass().getName())) {
                        showAppOpenAd(activity);
                    }
                }
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                currentActivityRef = new java.lang.ref.WeakReference<>(activity);
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
            }
        });
    }

    private static void showAppOpenAd(Activity activity) {
        if (cachedAppOpen == null) {
            cachedAppOpen = new AppOpenAd.Builder(activity);
            cachedAppOpen.load(new OnShowAdCompleteListener() {
                @Override
                public void onShowAdComplete() {
                    // Start of the session, ad was just loaded
                }
            });
        } else if (cachedAppOpen.isAdAvailable()) {
            cachedAppOpen.showAppOpenAd();
        } else {
            cachedAppOpen.load();
        }
    }
}
