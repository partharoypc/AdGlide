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
import com.partharoypc.adglide.util.AdGlideCallback;

public class AdGlide {
    private static final String TAG = "AdGlide";

    private static AdGlideConfig config;
    private static Application currentApplication;
    private static com.partharoypc.adglide.util.ConsentManager consentManager;
    private static AdGlideListener globalListener;

    public interface AdGlideListener {
        void onAdStatusChanged(String format, String network, String status);
        void onPerformanceMetrics(String format, long loadTimeMs);
    }
    
    public static void setListener(AdGlideListener listener) {
        globalListener = listener;
    }

    public static AdGlideListener getListener() {
        return globalListener;
    }

    private static boolean isShowingFullScreenAd = false;
    private static final java.util.Queue<Runnable> pendingAdRequests = new java.util.LinkedList<>();

    private static synchronized void processShowRequest(Runnable request) {
        if (config != null && !config.isSequentialQueueEnabled()) {
            isShowingFullScreenAd = true;
            request.run();
            return;
        }

        if (isShowingFullScreenAd) {
            Log.d(TAG, "Ad already showing. Adding request to sequential queue.");
            pendingAdRequests.offer(request);
        } else {
            isShowingFullScreenAd = true;
            request.run();
        }
    }

    private static synchronized void onAdShowFinished() {
        isShowingFullScreenAd = false;
        if (!pendingAdRequests.isEmpty()) {
            Log.d(TAG, "Popping next ad from sequential queue.");
            processShowRequest(pendingAdRequests.poll());
        }
    }

    // Cached Buidlers
    private static java.lang.ref.WeakReference<BannerAd.Builder> cachedBannerBuilder;

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
                .config(glideConfig)
                .build();

        if (config.getAdStatus()) {
            if (config.isAppOpenEnabled() && !isAppOpenRegistered) {
                registerAppOpenLifecycle(application);
                isAppOpenRegistered = true;
            }
        }
        com.partharoypc.adglide.util.PerformanceLogger.log("Core", "AdGlide initialized (v1.9.0 - Zero-Wait)");
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
                    .config(glideConfig)
                    .build();
        }
    }

    // --- Preloading Logic ---

    public static void preloadInterstitial(Activity activity) {
        if (config != null && config.getAdStatus()) {
            com.partharoypc.adglide.util.AdPoolManager.fillInterstitialPool(activity);
        }
    }

    public static void preloadRewarded(Activity activity) {
        if (config != null && config.getAdStatus()) {
            com.partharoypc.adglide.util.AdPoolManager.fillRewardedPool(activity);
        }
    }

    public static void preloadRewardedInterstitial(Activity activity) {
        if (config != null && config.getAdStatus()) {
            com.partharoypc.adglide.util.AdPoolManager.fillRewardedInterstitialPool(activity);
        }
    }

    public static void preloadNative(Activity activity, String style) {
        if (config != null && config.getAdStatus()) {
            com.partharoypc.adglide.util.AdPoolManager.fillNativePool(activity, style);
        }
    }

    public static void showInterstitial(Activity activity, AdGlideCallback callback) {
        if (!isInitialized || config == null) {
            Log.e(TAG, "AdGlide is not initialized. Call AdGlide.initialize() first.");
            if (callback != null)
                callback.onAdDismissed();
            return;
        }

        if (!isInterstitialEnabled()) {
            Log.d(TAG, "Interstitial Ad is disabled.");
            if (callback != null)
                callback.onAdDismissed();
            return;
        }

        if (interstitialClickCounter < config.getInterstitialInterval()) {
            com.partharoypc.adglide.util.PerformanceLogger.log("INTERSTITIAL", "Ad skipped — Interval not met ("
                    + interstitialClickCounter + "/" + config.getInterstitialInterval() + ")");
            interstitialClickCounter++;
            if (callback != null)
                callback.onAdDismissed();
            return;
        }

        processShowRequest(() -> {
            com.partharoypc.adglide.util.PerformanceLogger.log("INTERSTITIAL", "Showing Interstitial Ad");

            if (com.partharoypc.adglide.util.AdPoolManager.hasInterstitial()) {
                InterstitialAd.Builder pooledAd = com.partharoypc.adglide.util.AdPoolManager.getInterstitial();
                pooledAd.show(activity, new AdGlideCallback() {
                    @Override
                    public void onAdShowed() {
                        if (config.isAutoLoadInterstitial()) {
                            preloadInterstitial(activity);
                        }
                        if (callback != null) callback.onAdShowed();
                    }

                    @Override
                    public void onAdDismissed() {
                        interstitialClickCounter = 1; // Reset after successfully showing
                        if (config.isAutoLoadInterstitial()) {
                            preloadInterstitial(activity);
                        }
                        if (callback != null) callback.onAdDismissed();
                        onAdShowFinished();
                    }

                    @Override
                    public void onAdFailedToLoad(String error) {
                        if (callback != null) callback.onAdFailedToLoad(error);
                        onAdShowFinished();
                    }
                });
            } else {
                // Fallback: load and show on the fly
                new InterstitialAd.Builder(activity).loadAndShow(activity, new AdGlideCallback() {
                    @Override
                    public void onAdShowed() {
                        if (config.isAutoLoadInterstitial()) {
                            preloadInterstitial(activity);
                        }
                        if (callback != null) callback.onAdShowed();
                    }

                    @Override
                    public void onAdDismissed() {
                        interstitialClickCounter = 1; // Reset after fallback show/attempt
                        if (config.isAutoLoadInterstitial()) {
                            preloadInterstitial(activity);
                        }
                        if (callback != null) callback.onAdDismissed();
                        onAdShowFinished();
                    }

                    @Override
                    public void onAdFailedToLoad(String error) {
                        if (callback != null) callback.onAdFailedToLoad(error);
                        onAdShowFinished();
                    }
                });
            }
        });
    }

    /**
     * Shows a pre-cached rewarded ad, or loads one on the fly if not cached.
     */
    public static void showRewarded(Activity activity, AdGlideCallback callback) {
        if (!isInitialized || config == null) {
            Log.e(TAG, "AdGlide is not initialized. Call AdGlide.initialize() first.");
            if (callback != null)
                callback.onAdDismissed();
            return;
        }

        if (!isRewardedEnabled()) {
            Log.d(TAG, "Rewarded Ad is disabled.");
            if (callback != null)
                callback.onAdDismissed();
            return;
        }

        if (rewardedClickCounter < config.getRewardedInterval()) {
            Log.d(TAG, "Rewarded Ad interval not met. Current counter: " + rewardedClickCounter);
            rewardedClickCounter++;
            if (callback != null)
                callback.onAdDismissed();
            return;
        }

        processShowRequest(() -> {
            if (com.partharoypc.adglide.util.AdPoolManager.hasRewarded()) {
                RewardedAd.Builder pooledAd = com.partharoypc.adglide.util.AdPoolManager.getRewarded();
                pooledAd.showRewardedAd(activity, new AdGlideCallback() {
                    @Override
                    public void onAdShowed() {
                        if (config.isAutoLoadRewarded()) {
                            preloadRewarded(activity);
                        }
                        if (callback != null) callback.onAdShowed();
                    }

                    @Override
                    public void onAdDismissed() {
                        rewardedClickCounter = 1; // Reset after successfully showing
                        if (config.isAutoLoadRewarded()) {
                            preloadRewarded(activity);
                        }
                        if (callback != null) callback.onAdDismissed();
                        onAdShowFinished();
                    }

                    @Override
                    public void onAdCompleted() {
                        if (callback != null) callback.onAdCompleted();
                    }

                    @Override
                    public void onAdFailedToLoad(String error) {
                        if (callback != null) callback.onAdFailedToLoad(error);
                        onAdShowFinished();
                    }
                });
            } else {
                // Fallback: load and show on the fly
                new RewardedAd.Builder(activity).loadAndShow(activity, new AdGlideCallback() {
                    @Override
                    public void onAdShowed() {
                        if (config.isAutoLoadRewarded()) {
                            preloadRewarded(activity);
                        }
                        if (callback != null) callback.onAdShowed();
                    }

                    @Override
                    public void onAdDismissed() {
                        rewardedClickCounter = 1; // Reset after fallback show/attempt
                        if (config.isAutoLoadRewarded()) {
                            preloadRewarded(activity);
                        }
                        if (callback != null) callback.onAdDismissed();
                        onAdShowFinished();
                    }

                    @Override
                    public void onAdCompleted() {
                        if (callback != null) callback.onAdCompleted();
                    }

                    @Override
                    public void onAdFailedToLoad(String error) {
                        if (callback != null) callback.onAdFailedToLoad(error);
                        onAdShowFinished();
                    }
                });
            }
        });
    }

    /**
     * Shows a pre-cached rewarded interstitial ad, or loads one on the fly if not
     * cached.
     */
    public static void showRewardedInterstitial(Activity activity, AdGlideCallback callback) {
        if (!isInitialized || config == null) {
            Log.e(TAG, "AdGlide is not initialized. Call AdGlide.initialize() first.");
            if (callback != null)
                callback.onAdDismissed();
            return;
        }

        if (!isRewardedInterstitialEnabled()) {
            Log.d(TAG, "Rewarded Interstitial Ad is disabled.");
            if (callback != null)
                callback.onAdDismissed();
            return;
        }

        if (rewardedInterstitialClickCounter < config.getRewardedInterval()) {
            Log.d(TAG,
                    "Rewarded Interstitial Ad interval not met. Current counter: " + rewardedInterstitialClickCounter);
            rewardedInterstitialClickCounter++;
            if (callback != null)
                callback.onAdDismissed();
            return;
        }

        processShowRequest(() -> {
            if (com.partharoypc.adglide.util.AdPoolManager.hasRewardedInterstitial()) {
                com.partharoypc.adglide.format.RewardedInterstitialAd.Builder pooledAd = com.partharoypc.adglide.util.AdPoolManager.getRewardedInterstitial();
                pooledAd.showRewardedInterstitialAd(activity, new AdGlideCallback() {
                    @Override
                    public void onAdShowed() {
                        if (config.isAutoLoadRewarded()) {
                            preloadRewardedInterstitial(activity);
                        }
                        if (callback != null) callback.onAdShowed();
                    }

                    @Override
                    public void onAdDismissed() {
                        rewardedInterstitialClickCounter = 1;
                        if (config.isAutoLoadRewarded()) {
                            preloadRewardedInterstitial(activity);
                        }
                        if (callback != null) callback.onAdDismissed();
                        onAdShowFinished();
                    }

                    @Override
                    public void onAdCompleted() {
                        if (callback != null) callback.onAdCompleted();
                    }

                    @Override
                    public void onAdFailedToLoad(String error) {
                        // Cache missed or failed. Fallback to loadAndShow
                        new com.partharoypc.adglide.format.RewardedInterstitialAd.Builder(activity)
                                .loadAndShow(activity, new AdGlideCallback() {
                                    @Override
                                    public void onAdShowed() {
                                        if (config.isAutoLoadRewarded()) {
                                            preloadRewardedInterstitial(activity);
                                        }
                                        if (callback != null) callback.onAdShowed();
                                    }

                                    @Override
                                    public void onAdDismissed() {
                                        rewardedInterstitialClickCounter = 1;
                                        if (config.isAutoLoadRewarded()) {
                                            preloadRewardedInterstitial(activity);
                                        }
                                        if (callback != null) callback.onAdDismissed();
                                        onAdShowFinished();
                                    }

                                    @Override
                                    public void onAdCompleted() {
                                        if (callback != null) callback.onAdCompleted();
                                    }

                                    @Override
                                    public void onAdFailedToLoad(String error2) {
                                        if (callback != null) callback.onAdFailedToLoad(error2);
                                        onAdShowFinished();
                                    }
                                });
                    }
                });
            } else {
                // Fallback immediately
                new com.partharoypc.adglide.format.RewardedInterstitialAd.Builder(activity).loadAndShow(activity,
                        new AdGlideCallback() {
                            @Override
                            public void onAdShowed() {
                                if (config.isAutoLoadRewarded()) {
                                    preloadRewardedInterstitial(activity);
                                }
                                if (callback != null) callback.onAdShowed();
                            }

                            @Override
                            public void onAdDismissed() {
                                rewardedInterstitialClickCounter = 1;
                                if (config.isAutoLoadRewarded()) {
                                    preloadRewardedInterstitial(activity);
                                }
                                if (callback != null) callback.onAdDismissed();
                                onAdShowFinished();
                            }

                            @Override
                            public void onAdCompleted() {
                                if (callback != null) callback.onAdCompleted();
                            }

                            @Override
                            public void onAdFailedToLoad(String error) {
                                if (callback != null) callback.onAdFailedToLoad(error);
                                onAdShowFinished();
                            }
                        });
            }
        });
    }

    /**
     * Shows a banner ad in the activity's default banner container.
     */
    public static void showBanner(Activity activity) {
        if (config == null || !isBannerEnabled())
            return;
        if (cachedBannerBuilder != null && cachedBannerBuilder.get() != null) {
            cachedBannerBuilder.get().destroyAndDetachBanner();
        }
        BannerAd.Builder builder = new BannerAd.Builder(activity);
        cachedBannerBuilder = new java.lang.ref.WeakReference<>(builder);
        builder.load();
    }

    /**
     * Shows a banner ad in a custom container.
     */
    public static void showBanner(Activity activity, ViewGroup container) {
        if (config == null || !isBannerEnabled())
            return;
        if (cachedBannerBuilder != null && cachedBannerBuilder.get() != null) {
            cachedBannerBuilder.get().destroyAndDetachBanner();
        }
        BannerAd.Builder builder = new BannerAd.Builder(activity).container(container);
        cachedBannerBuilder = new java.lang.ref.WeakReference<>(builder);
        builder.load();
    }

    /**
     * Shows a native ad in the specified containter without needing a builder.
     */
    public static void showNative(Activity activity, String nativeStyle) {
        if (config == null || !isNativeEnabled())
            return;
        
        if (com.partharoypc.adglide.util.AdPoolManager.hasNative(nativeStyle)) {
            com.partharoypc.adglide.format.NativeAd.Builder pooledAd = com.partharoypc.adglide.util.AdPoolManager.getNative(nativeStyle);
            pooledAd.attachToContainer(null, new AdGlideCallback() {
                @Override
                public void onAdShowed() {
                    preloadNative(activity, nativeStyle);
                }
            });
        } else {
            com.partharoypc.adglide.util.PerformanceLogger.log("NATIVE", "Showing Native Ad Style: " + nativeStyle);
            new NativeAd.Builder(activity).style(nativeStyle).load(new AdGlideCallback() {
                @Override
                public void onAdShowed() {
                    preloadNative(activity, nativeStyle);
                }
            });
        }
    }

    /**
     * Shows a native ad in a custom container with specified style.
     */
    public static void showNative(Activity activity, ViewGroup container, String nativeStyle) {
        if (config == null || !isNativeEnabled())
            return;

        if (com.partharoypc.adglide.util.AdPoolManager.hasNative(nativeStyle)) {
            com.partharoypc.adglide.format.NativeAd.Builder pooledAd = com.partharoypc.adglide.util.AdPoolManager.getNative(nativeStyle);
            pooledAd.attachToContainer(container, new AdGlideCallback() {
                @Override
                public void onAdShowed() {
                    preloadNative(activity, nativeStyle);
                }
            });
        } else {
            com.partharoypc.adglide.util.PerformanceLogger.log("NATIVE", "Showing Native Ad Style: " + nativeStyle);
            new NativeAd.Builder(activity).container(container).style(nativeStyle).load(new AdGlideCallback() {
                @Override
                public void onAdShowed() {
                    preloadNative(activity, nativeStyle);
                }
            });
        }
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
                if (config != null && config.getAdStatus() && config.isAppOpenEnabled()) {
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
        processShowRequest(() -> {
            if (com.partharoypc.adglide.util.AdPoolManager.hasAppOpen()) {
                AppOpenAd.Builder pooledAd = com.partharoypc.adglide.util.AdPoolManager.getAppOpen();
                pooledAd.showAppOpenAd(new AdGlideCallback() {
                    @Override
                    public void onAdShowed() {
                        com.partharoypc.adglide.util.AdPoolManager.fillAppOpenPool(activity);
                    }

                    @Override
                    public void onAdDismissed() {
                        com.partharoypc.adglide.util.AdPoolManager.fillAppOpenPool(activity);
                        onAdShowFinished();
                    }

                    @Override
                    public void onAdFailedToLoad(String error) {
                        onAdShowFinished();
                    }
                });
            } else {
                // Initiate background load immediately
                com.partharoypc.adglide.util.AdPoolManager.fillAppOpenPool(activity);

                // Load and show on the fly for this explicit request
                new AppOpenAd.Builder(activity).load(new AdGlideCallback() {
                    @Override
                    public void onAdLoaded() {
                        new AppOpenAd.Builder(activity).showAppOpenAd(new AdGlideCallback() {
                            @Override
                            public void onAdShowed() {
                                com.partharoypc.adglide.util.AdPoolManager.fillAppOpenPool(activity);
                            }

                            @Override
                            public void onAdDismissed() {
                                com.partharoypc.adglide.util.AdPoolManager.fillAppOpenPool(activity);
                                onAdShowFinished();
                            }

                            @Override
                            public void onAdFailedToLoad(String error) {
                                onAdShowFinished();
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(String error) {
                        onAdShowFinished();
                    }
                });
            }
        });
    }
}
