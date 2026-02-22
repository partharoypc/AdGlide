package com.partharoypc.adglide;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.format.AdNetwork;
import com.partharoypc.adglide.format.BannerAd;
import com.partharoypc.adglide.format.InterstitialAd;
import com.partharoypc.adglide.format.NativeAd;
import com.partharoypc.adglide.format.RewardedAd;
import com.partharoypc.adglide.util.AdPreloader;
import com.partharoypc.adglide.util.OnInterstitialAdDismissedListener;
import com.partharoypc.adglide.util.OnInterstitialAdShowedListener;
import com.partharoypc.adglide.util.OnRewardedAdCompleteListener;
import com.partharoypc.adglide.util.OnRewardedAdDismissedListener;

import java.util.List;

/**
 * Main entry point for the AdGlide ad library.
 * <p>
 * Provides a simple, developer-friendly API to initialize, load, and show ads
 * with minimal boilerplate. Supports 8 ad networks with automatic waterfall
 * fallback.
 * </p>
 *
 * <h3>Quick Start (3 lines):</h3>
 * 
 * <pre>{@code
 * // 1. Configure (once in Application or splash Activity)
 * AdGlide.init(activity)
 *         .setAdNetwork("admob")
 *         .setBackupAdNetworks("meta", "unity")
 *         .setAdMobInterstitialId("ca-app-pub-xxx/yyy")
 *         .setAdMobBannerId("ca-app-pub-xxx/zzz")
 *         .setAdMobRewardedId("ca-app-pub-xxx/rrr")
 *         .build();
 *
 * // 2. Show interstitial (anywhere)
 * AdGlide.showInterstitial(activity);
 *
 * // 3. Show rewarded (anywhere)
 * AdGlide.showRewarded(activity, () -> grantReward());
 * }</pre>
 *
 * <h3>Supported Ad Networks:</h3>
 * <ul>
 * <li>AdMob (Google) — {@code "admob"}</li>
 * <li>Meta (Facebook) — {@code "meta"}</li>
 * <li>Unity Ads — {@code "unity"}</li>
 * <li>AppLovin MAX — {@code "app_lovin_max"}</li>
 * 
 * <li>IronSource — {@code "ironsource"}</li>
 * <li>StartApp — {@code "startapp"}</li>
 * <li>Wortise — {@code "wortise"}</li>
 * </ul>
 *
 * <h3>Supported Ad Formats:</h3>
 * <ul>
 * <li>Banner — {@link #loadBanner(Activity, ViewGroup)}</li>
 * <li>Interstitial — {@link #showInterstitial(Activity)}</li>
 * <li>Rewarded —
 * {@link #showRewarded(Activity, OnRewardedAdCompleteListener)}</li>
 * <li>Native — via individual {@link NativeAd.Builder}</li>
 * <li>Rewarded Interstitial —
 * {@link #showRewardedInterstitial(Activity, OnRewardedAdCompleteListener)}</li>
 * <li>Native — {@link #loadNativeAd(Activity, ViewGroup)}</li>
 * <li>App Open — {@link #loadAppOpenAd(Activity)}</li>
 * </ul>
 */
public final class AdGlide {

    private static final String TAG = "AdGlide";
    private static boolean initialized = false;

    private AdGlide() {
        // Utility class
    }

    // ==================== INITIALIZATION ====================

    /**
     * Starts the AdGlide initialization builder.
     * <p>
     * Chain configuration methods, then call {@link InitBuilder#build()} to
     * initialize all ad SDKs and preload ads.
     * </p>
     *
     * @param activity The Activity context.
     * @return An InitBuilder for fluent configuration.
     */
    @NonNull
    public static InitBuilder init(@NonNull Activity activity) {
        return new InitBuilder(activity);
    }

    /**
     * Returns true if AdGlide has been initialized.
     */
    public static boolean isInitialized() {
        return initialized;
    }

    // ==================== INTERSTITIAL ====================

    /**
     * Shows a preloaded interstitial ad. If not preloaded, loads and shows
     * automatically.
     *
     * @param activity The Activity context.
     */
    public static void showInterstitial(@NonNull Activity activity) {
        showInterstitial(activity, null, null);
    }

    /**
     * Shows a preloaded interstitial ad with dismiss callback.
     *
     * @param activity  The Activity context.
     * @param onDismiss Callback when the ad is dismissed (nullable).
     */
    public static void showInterstitial(@NonNull Activity activity,
            @Nullable OnInterstitialAdDismissedListener onDismiss) {
        showInterstitial(activity, null, onDismiss);
    }

    /**
     * Shows a preloaded interstitial ad with show and dismiss callbacks.
     *
     * @param activity  The Activity context.
     * @param onShow    Callback when the ad is shown (nullable).
     * @param onDismiss Callback when the ad is dismissed (nullable).
     */
    public static void showInterstitial(@NonNull Activity activity,
            @Nullable OnInterstitialAdShowedListener onShow,
            @Nullable OnInterstitialAdDismissedListener onDismiss) {
        AdPreloader preloader = AdPreloader.getInstance();

        if (preloader.isInterstitialPreloaded() && preloader.getInterstitialBuilder() != null) {
            if (onShow != null && onDismiss != null) {
                preloader.getInterstitialBuilder().show(onShow, onDismiss);
            } else if (onDismiss != null) {
                preloader.getInterstitialBuilder().show();
            } else {
                preloader.getInterstitialBuilder().show();
            }
        } else {
            // Lazy load + show
            AdConfig config = AdConfig.getInstance();
            InterstitialAd.Builder builder = createInterstitialBuilder(activity, config);
            if (onDismiss != null) {
                builder.build(onDismiss);
            } else {
                builder.build();
            }
            Log.d(TAG, "Interstitial not preloaded, loading on demand");
        }
    }

    // ==================== REWARDED ====================

    /**
     * Shows a rewarded ad with a reward callback.
     *
     * @param activity         The Activity context.
     * @param onRewardComplete Called when the user earns the reward.
     */
    public static void showRewarded(@NonNull Activity activity,
            @NonNull OnRewardedAdCompleteListener onRewardComplete) {
        showRewarded(activity, onRewardComplete, null);
    }

    /**
     * Shows a rewarded ad with reward and dismiss callbacks.
     *
     * @param activity         The Activity context.
     * @param onRewardComplete Called when the user earns the reward.
     * @param onDismiss        Called when the ad is dismissed (nullable).
     */
    public static void showRewarded(@NonNull Activity activity,
            @NonNull OnRewardedAdCompleteListener onRewardComplete,
            @Nullable OnRewardedAdDismissedListener onDismiss) {
        AdPreloader preloader = AdPreloader.getInstance();

        if (preloader.isRewardedPreloaded() && preloader.getRewardedBuilder() != null) {
            preloader.getRewardedBuilder().show(onRewardComplete, onDismiss,
                    () -> Log.e(TAG, "Rewarded ad error"));
        } else {
            // Lazy load + show
            AdConfig config = AdConfig.getInstance();
            RewardedAd.Builder builder = createRewardedBuilder(activity, config);
            builder.build(onRewardComplete, onDismiss);
            Log.d(TAG, "Rewarded not preloaded, loading on demand");
        }
    }

    // ==================== REWARDED INTERSTITIAL ====================

    /**
     * Shows a rewarded interstitial ad with a reward callback.
     *
     * @param activity         The Activity context.
     * @param onRewardComplete Called when the user earns the reward.
     */
    public static void showRewardedInterstitial(@NonNull Activity activity,
            @NonNull OnRewardedAdCompleteListener onRewardComplete) {
        showRewardedInterstitial(activity, onRewardComplete, null);
    }

    /**
     * Shows a rewarded interstitial ad with reward and dismiss callbacks.
     *
     * @param activity         The Activity context.
     * @param onRewardComplete Called when the user earns the reward.
     * @param onDismiss        Called when the ad is dismissed (nullable).
     */
    public static void showRewardedInterstitial(@NonNull Activity activity,
            @NonNull OnRewardedAdCompleteListener onRewardComplete,
            @Nullable OnRewardedAdDismissedListener onDismiss) {

        AdConfig config = AdConfig.getInstance();
        com.partharoypc.adglide.format.RewardedInterstitialAd.Builder builder = createRewardedInterstitialBuilder(
                activity, config);

        // RewardedInterstitial isn't preloaded in this version natively.
        // It lazy loads on demand.
        builder.build(onRewardComplete, onDismiss);
        Log.d(TAG, "RewardedInterstitial loading on demand");
    }

    // ==================== BANNER ====================

    /**
     * Loads and displays a banner ad in the specified container.
     *
     * @param activity        The Activity context.
     * @param bannerContainer The ViewGroup to display the banner in.
     * @return The BannerAd.Builder for further customization.
     */
    @NonNull
    public static BannerAd.Builder loadBanner(@NonNull Activity activity,
            @NonNull ViewGroup bannerContainer) {
        AdConfig config = AdConfig.getInstance();
        List<String> backups = config.getBackupAdNetworks();

        BannerAd.Builder builder = new BannerAd.Builder(activity)
                .setAdStatus(config.getAdStatus())
                .setAdNetwork(config.getAdNetwork())
                .setAdMobBannerId(config.getAdMobBannerId())
                .setMetaBannerId(config.getMetaBannerId())
                .setUnityBannerId(config.getUnityBannerId())
                .setAppLovinBannerId(config.getAppLovinBannerId())
                .setIronSourceBannerId(config.getIronSourceBannerId())
                .setWortiseBannerId(config.getWortiseBannerId())
                .setPlacementStatus(config.getPlacementStatus())
                .setDarkTheme(config.isDarkTheme())
                .setLegacyGDPR(config.isLegacyGDPR())
                .setIsCollapsibleBanner(config.isCollapsibleBanner());

        if (!backups.isEmpty()) {
            builder.setBackupAdNetworks(backups.toArray(new String[0]));
        }

        builder.setContainer(bannerContainer);
        builder.build();
        return builder;
    }

    // ==================== MEDIUM RECTANGLE (MREC) ====================

    /**
     * Loads and displays a medium rectangle (MREC) ad.
     *
     * @param activity      The Activity context.
     * @param mrecContainer The ViewGroup to display the MREC in.
     * @return The MediumRectangleAd.Builder for further customization.
     */
    @NonNull
    public static com.partharoypc.adglide.format.MediumRectangleAd.Builder loadMediumRectangle(
            @NonNull Activity activity,
            @NonNull ViewGroup mrecContainer) {
        AdConfig config = AdConfig.getInstance();
        List<String> backups = config.getBackupAdNetworks();

        com.partharoypc.adglide.format.MediumRectangleAd.Builder builder = new com.partharoypc.adglide.format.MediumRectangleAd.Builder(
                activity)
                .setAdStatus(config.getAdStatus())
                .setAdNetwork(config.getAdNetwork())
                .setAdMobBannerId(config.getAdMobBannerId())
                .setMetaBannerId(config.getMetaBannerId())
                .setUnityBannerId(config.getUnityBannerId())
                .setAppLovinBannerId(config.getAppLovinBannerId())
                .setIronSourceBannerId(config.getIronSourceBannerId())
                .setWortiseBannerId(config.getWortiseBannerId())
                .setPlacementStatus(config.getPlacementStatus())
                .setDarkTheme(config.isDarkTheme())
                .setLegacyGDPR(config.isLegacyGDPR());

        if (!backups.isEmpty()) {
            builder.setBackupAdNetworks(backups.toArray(new String[0]));
        }

        builder.setContainer(mrecContainer);
        builder.build();
        return builder;
    }

    // ==================== NATIVE ====================

    /**
     * Loads and displays a Native ad.
     *
     * @param activity        The Activity context.
     * @param nativeContainer The ViewGroup to display the native ad in.
     * @return The NativeAd.Builder for further customization.
     */
    @NonNull
    public static NativeAd.Builder loadNativeAd(@NonNull Activity activity,
            @NonNull ViewGroup nativeContainer) {
        AdConfig config = AdConfig.getInstance();
        List<String> backups = config.getBackupAdNetworks();

        NativeAd.Builder builder = new NativeAd.Builder(activity)
                .setAdStatus(config.getAdStatus())
                .setAdNetwork(config.getAdNetwork())
                .setAdMobNativeId(config.getAdMobNativeId())
                .setMetaNativeId(config.getMetaNativeId())
                .setWortiseNativeId(config.getWortiseNativeId())
                .setPlacementStatus(config.getPlacementStatus())
                .setDarkTheme(config.isDarkTheme())
                .setNativeAdStyle(config.getNativeAdStyle())
                .setLegacyGDPR(config.isLegacyGDPR());

        if (!backups.isEmpty()) {
            builder.setBackupAdNetwork(backups.get(0));
        }

        builder.setContainer(nativeContainer);
        builder.build();
        return builder;
    }

    // ==================== APP OPEN ====================

    /**
     * Loads an App Open Ad.
     *
     * @param activity The Activity context.
     * @return The AppOpenAd.Builder.
     */
    @NonNull
    public static com.partharoypc.adglide.format.AppOpenAd.Builder loadAppOpenAd(@NonNull Activity activity) {
        AdConfig config = AdConfig.getInstance();
        List<String> backups = config.getBackupAdNetworks();

        com.partharoypc.adglide.format.AppOpenAd.Builder builder = new com.partharoypc.adglide.format.AppOpenAd.Builder(
                activity)
                .setAdStatus(config.getAdStatus())
                .setAdNetwork(config.getAdNetwork())
                .setAdMobAppOpenId(config.getAdMobAppOpenId())
                .setAppLovinAppOpenId(config.getAppLovinAppOpenId())
                .setWortiseAppOpenId(config.getWortiseAppOpenId());

        if (!backups.isEmpty()) {
            builder.setBackupAdNetwork(backups.get(0));
        }

        builder.build();
        return builder;
    }

    // ==================== CLEANUP ====================

    /**
     * Cleans up all global AdGlide resources. Call from your main Activity's
     * onDestroy().
     */
    public static void destroyAd() {
        AdPreloader.getInstance().destroy();
        com.partharoypc.adglide.util.AdLoadStrategy.getInstance().reset();
        Log.d(TAG, "AdGlide global resources cleaned up");
    }

    // ==================== INTERNAL HELPERS ====================

    private static InterstitialAd.Builder createInterstitialBuilder(
            @NonNull Activity activity, @NonNull AdConfig config) {
        List<String> backups = config.getBackupAdNetworks();

        InterstitialAd.Builder builder = new InterstitialAd.Builder(activity)
                .setAdStatus(config.getAdStatus())
                .setAdNetwork(config.getAdNetwork())
                .setAdMobInterstitialId(config.getAdMobInterstitialId())
                .setMetaInterstitialId(config.getMetaInterstitialId())
                .setUnityInterstitialId(config.getUnityInterstitialId())
                .setAppLovinInterstitialId(config.getAppLovinInterstitialId())
                .setIronSourceInterstitialId(config.getIronSourceInterstitialId())
                .setWortiseInterstitialId(config.getWortiseInterstitialId())
                .setPlacementStatus(config.getPlacementStatus())
                .setInterval(config.getInterstitialInterval())
                .setLegacyGDPR(config.isLegacyGDPR());

        if (!backups.isEmpty()) {
            builder.setBackupAdNetworks(backups.toArray(new String[0]));
        }

        return builder;
    }

    private static RewardedAd.Builder createRewardedBuilder(
            @NonNull Activity activity, @NonNull AdConfig config) {
        List<String> backups = config.getBackupAdNetworks();

        RewardedAd.Builder builder = new RewardedAd.Builder(activity)
                .setAdStatus(config.getAdStatus())
                .setAdNetwork(config.getAdNetwork())
                .setAdMobRewardedId(config.getAdMobRewardedId())
                .setMetaRewardedId(config.getMetaRewardedId())
                .setUnityRewardedId(config.getUnityRewardedId())
                .setApplovinMaxRewardedId(config.getAppLovinRewardedId())
                .setIronSourceRewardedId(config.getIronSourceRewardedId())
                .setWortiseRewardedId(config.getWortiseRewardedId())
                .setPlacementStatus(config.getPlacementStatus())
                .setLegacyGDPR(config.isLegacyGDPR());

        if (!backups.isEmpty()) {
            builder.setBackupAdNetworks(backups.toArray(new String[0]));
        }

        return builder;
    }

    private static com.partharoypc.adglide.format.RewardedInterstitialAd.Builder createRewardedInterstitialBuilder(
            @NonNull Activity activity, @NonNull AdConfig config) {
        List<String> backups = config.getBackupAdNetworks();

        com.partharoypc.adglide.format.RewardedInterstitialAd.Builder builder = new com.partharoypc.adglide.format.RewardedInterstitialAd.Builder(
                activity)
                .setAdStatus(config.getAdStatus())
                .setAdNetwork(config.getAdNetwork())
                .setAdMobRewardedInterstitialId(config.getAdMobRewardedInterstitialId())
                .setMetaRewardedId(config.getMetaRewardedId())
                .setUnityRewardedId(config.getUnityRewardedId())
                .setApplovinMaxRewardedId(config.getAppLovinRewardedId())
                .setIronSourceRewardedId(config.getIronSourceRewardedId())
                .setWortiseRewardedId(config.getWortiseRewardedId())
                .setPlacementStatus(config.getPlacementStatus())
                .setLegacyGDPR(config.isLegacyGDPR());

        if (!backups.isEmpty()) {
            builder.setBackupAdNetworks(backups.toArray(new String[0]));
        }

        return builder;
    }

    // ==================== INIT BUILDER ====================

    /**
     * Fluent builder for initializing AdGlide.
     * <p>
     * Configures the global {@link AdConfig} and initializes ad network SDKs.
     * Call {@link #build()} to finalize initialization and start preloading ads.
     * </p>
     */
    public static final class InitBuilder {

        private final Activity activity;
        private final AdConfig config;
        private boolean autoPreload = true;

        private InitBuilder(@NonNull Activity activity) {
            this.activity = activity;
            this.config = AdConfig.getInstance();
        }

        // --- General ---

        @NonNull
        public InitBuilder setAdStatus(@NonNull String adStatus) {
            config.setAdStatus(adStatus);
            return this;
        }

        @NonNull
        public InitBuilder setAdNetwork(@NonNull String adNetwork) {
            config.setAdNetwork(adNetwork);
            return this;
        }

        @NonNull
        public InitBuilder setBackupAdNetworks(@NonNull String... networks) {
            config.setBackupAdNetworks(networks);
            return this;
        }

        @NonNull
        public InitBuilder setPlacementStatus(int status) {
            config.setPlacementStatus(status);
            return this;
        }

        @NonNull
        public InitBuilder setLegacyGDPR(boolean legacyGDPR) {
            config.setLegacyGDPR(legacyGDPR);
            return this;
        }

        @NonNull
        public InitBuilder setDebug(boolean debug) {
            config.setDebug(debug);
            return this;
        }

        /**
         * Whether to automatically preload interstitial and rewarded ads after init.
         * Default is true.
         */
        @NonNull
        public InitBuilder setAutoPreload(boolean autoPreload) {
            this.autoPreload = autoPreload;
            return this;
        }

        // --- AdMob ---

        @NonNull
        public InitBuilder setAdMobAppId(@NonNull String id) {
            config.setAdMobAppId(id);
            return this;
        }

        @NonNull
        public InitBuilder setAdMobBannerId(@NonNull String id) {
            config.setAdMobBannerId(id);
            return this;
        }

        @NonNull
        public InitBuilder setAdMobInterstitialId(@NonNull String id) {
            config.setAdMobInterstitialId(id);
            return this;
        }

        @NonNull
        public InitBuilder setAdMobRewardedId(@NonNull String id) {
            config.setAdMobRewardedId(id);
            return this;
        }

        @NonNull
        public InitBuilder setAdMobRewardedInterstitialId(@NonNull String id) {
            config.setAdMobRewardedInterstitialId(id);
            return this;
        }

        @NonNull
        public InitBuilder setAdMobNativeId(@NonNull String id) {
            config.setAdMobNativeId(id);
            return this;
        }

        @NonNull
        public InitBuilder setAdMobAppOpenId(@NonNull String id) {
            config.setAdMobAppOpenId(id);
            return this;
        }

        // --- Meta ---

        @NonNull
        public InitBuilder setMetaBannerId(@NonNull String id) {
            config.setMetaBannerId(id);
            return this;
        }

        @NonNull
        public InitBuilder setMetaInterstitialId(@NonNull String id) {
            config.setMetaInterstitialId(id);
            return this;
        }

        @NonNull
        public InitBuilder setMetaRewardedId(@NonNull String id) {
            config.setMetaRewardedId(id);
            return this;
        }

        @NonNull
        public InitBuilder setMetaNativeId(@NonNull String id) {
            config.setMetaNativeId(id);
            return this;
        }

        // --- Unity ---

        @NonNull
        public InitBuilder setUnityGameId(@NonNull String id) {
            config.setUnityGameId(id);
            return this;
        }

        @NonNull
        public InitBuilder setUnityBannerId(@NonNull String id) {
            config.setUnityBannerId(id);
            return this;
        }

        @NonNull
        public InitBuilder setUnityInterstitialId(@NonNull String id) {
            config.setUnityInterstitialId(id);
            return this;
        }

        @NonNull
        public InitBuilder setUnityRewardedId(@NonNull String id) {
            config.setUnityRewardedId(id);
            return this;
        }

        // --- AppLovin ---

        @NonNull
        public InitBuilder setAppLovinSdkKey(@NonNull String key) {
            config.setAppLovinSdkKey(key);
            return this;
        }

        @NonNull
        public InitBuilder setAppLovinBannerId(@NonNull String id) {
            config.setAppLovinBannerId(id);
            return this;
        }

        @NonNull
        public InitBuilder setAppLovinInterstitialId(@NonNull String id) {
            config.setAppLovinInterstitialId(id);
            return this;
        }

        @NonNull
        public InitBuilder setAppLovinRewardedId(@NonNull String id) {
            config.setAppLovinRewardedId(id);
            return this;
        }

        @NonNull
        public InitBuilder setAppLovinAppOpenId(@NonNull String id) {
            config.setAppLovinAppOpenId(id);
            return this;
        }

        // --- IronSource ---

        @NonNull
        public InitBuilder setIronSourceAppKey(@NonNull String key) {
            config.setIronSourceAppKey(key);
            return this;
        }

        @NonNull
        public InitBuilder setIronSourceBannerId(@NonNull String id) {
            config.setIronSourceBannerId(id);
            return this;
        }

        @NonNull
        public InitBuilder setIronSourceInterstitialId(@NonNull String id) {
            config.setIronSourceInterstitialId(id);
            return this;
        }

        @NonNull
        public InitBuilder setIronSourceRewardedId(@NonNull String id) {
            config.setIronSourceRewardedId(id);
            return this;
        }

        // --- StartApp ---

        @NonNull
        public InitBuilder setStartAppId(@NonNull String id) {
            config.setStartAppId(id);
            return this;
        }

        // --- Wortise ---

        @NonNull
        public InitBuilder setWortiseAppId(@NonNull String id) {
            config.setWortiseAppId(id);
            return this;
        }

        @NonNull
        public InitBuilder setWortiseBannerId(@NonNull String id) {
            config.setWortiseBannerId(id);
            return this;
        }

        @NonNull
        public InitBuilder setWortiseInterstitialId(@NonNull String id) {
            config.setWortiseInterstitialId(id);
            return this;
        }

        @NonNull
        public InitBuilder setWortiseRewardedId(@NonNull String id) {
            config.setWortiseRewardedId(id);
            return this;
        }

        @NonNull
        public InitBuilder setWortiseNativeId(@NonNull String id) {
            config.setWortiseNativeId(id);
            return this;
        }

        @NonNull
        public InitBuilder setWortiseAppOpenId(@NonNull String id) {
            config.setWortiseAppOpenId(id);
            return this;
        }

        // --- Format Settings ---

        @NonNull
        public InitBuilder setInterstitialInterval(int seconds) {
            config.setInterstitialInterval(seconds);
            return this;
        }

        @NonNull
        public InitBuilder setDarkTheme(boolean darkTheme) {
            config.setDarkTheme(darkTheme);
            return this;
        }

        @NonNull
        public InitBuilder setCollapsibleBanner(boolean collapsible) {
            config.setCollapsibleBanner(collapsible);
            return this;
        }

        @NonNull
        public InitBuilder setNativeAdStyle(@NonNull String style) {
            config.setNativeAdStyle(style);
            return this;
        }

        // --- Build ---

        /**
         * Finalizes initialization: initializes ad network SDKs and optionally
         * begins preloading interstitial and rewarded ads.
         */
        public void build() {
            try {
                // Initialize primary + backup SDKs
                AdNetwork.Initialize networkInit = new AdNetwork.Initialize(activity)
                        .setAdStatus(config.getAdStatus())
                        .setAdNetwork(config.getAdNetwork())
                        .setAdMobAppId(config.getAdMobAppId())
                        .setStartappAppId(config.getStartAppId())
                        .setUnityGameId(config.getUnityGameId())
                        .setAppLovinSdkKey(config.getAppLovinSdkKey())
                        .setIronSourceAppKey(config.getIronSourceAppKey())
                        .setWortiseAppId(config.getWortiseAppId())
                        .setDebug(config.isDebug());

                // Add backup networks
                for (String backup : config.getBackupAdNetworks()) {
                    networkInit.addBackupAdNetwork(backup);
                }

                networkInit.build();

                initialized = true;
                Log.d(TAG, "AdGlide initialized successfully with network: " + config.getAdNetwork());

                // Auto-preload if enabled
                if (autoPreload) {
                    AdPreloader.getInstance().preload(activity);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize AdGlide: " + e.getMessage());
            }
        }
    }
}
