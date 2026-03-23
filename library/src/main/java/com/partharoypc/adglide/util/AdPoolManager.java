package com.partharoypc.adglide.util;

import android.app.Activity;
import android.util.Log;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.format.AppOpenAd;
import com.partharoypc.adglide.format.InterstitialAd;
import com.partharoypc.adglide.format.RewardedAd;
import com.partharoypc.adglide.format.RewardedInterstitialAd;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Manages pools of pre-loaded Zero-Wait Ads for instant delivery.
 */
public class AdPoolManager {
    private static final String TAG = "AdGlide.Pool";
    private static final int MAX_POOL_SIZE = 2;

    private static final Queue<InterstitialAd.Builder> interstitialPrimaryPool = new LinkedList<>();
    private static final Queue<InterstitialAd.Builder> interstitialBackupPool = new LinkedList<>();

    private static final Queue<RewardedAd.Builder> rewardedPrimaryPool = new LinkedList<>();
    private static final Queue<RewardedAd.Builder> rewardedBackupPool = new LinkedList<>();

    private static final Queue<RewardedInterstitialAd.Builder> rewardedInterstitialPrimaryPool = new LinkedList<>();
    private static final Queue<RewardedInterstitialAd.Builder> rewardedInterstitialBackupPool = new LinkedList<>();

    private static final Queue<AppOpenAd.Builder> appOpenPrimaryPool = new LinkedList<>();
    private static final Queue<AppOpenAd.Builder> appOpenBackupPool = new LinkedList<>();

    // Native Pools
    private static final Queue<com.partharoypc.adglide.format.NativeAd.Builder> nativeSmallPrimaryPool = new LinkedList<>();
    private static final Queue<com.partharoypc.adglide.format.NativeAd.Builder> nativeSmallBackupPool = new LinkedList<>();

    private static final Queue<com.partharoypc.adglide.format.NativeAd.Builder> nativeMediumPrimaryPool = new LinkedList<>();
    private static final Queue<com.partharoypc.adglide.format.NativeAd.Builder> nativeMediumBackupPool = new LinkedList<>();

    private static boolean isLoadingInterstitial = false;
    private static boolean isLoadingRewarded = false;
    private static boolean isLoadingRewardedInterstitial = false;
    private static boolean isLoadingAppOpen = false;
    private static boolean isLoadingNativeSmall = false;
    private static boolean isLoadingNativeMedium = false;

    // --- INTERSTITIAL ---
    public static void fillInterstitialPool(Activity activity) {
        if (!AdGlide.isInterstitialEnabled()) return;
        int currentTotal = interstitialPrimaryPool.size() + interstitialBackupPool.size();
        if (currentTotal >= MAX_POOL_SIZE || isLoadingInterstitial) return;
        
        isLoadingInterstitial = true;
        Log.d(TAG, "Filling Interstitial Pool. Current total: " + currentTotal);
        
        InterstitialAd.Builder builder = new InterstitialAd.Builder(activity);
        builder.load(new AdGlideCallback() {
            @Override
            public void onAdLoaded(String network) {
                String primary = AdGlide.getConfig() != null ? AdGlide.getConfig().getPrimaryNetwork() : "";
                if (network.equals(primary)) {
                    interstitialPrimaryPool.offer(builder);
                } else {
                    interstitialBackupPool.offer(builder);
                }
                isLoadingInterstitial = false;
                fillInterstitialPool(activity);
            }
            @Override
            public void onAdFailedToLoad(String error) {
                isLoadingInterstitial = false;
            }
        });
    }

    public static InterstitialAd.Builder getInterstitial() {
        if (!interstitialPrimaryPool.isEmpty()) return interstitialPrimaryPool.poll();
        return interstitialBackupPool.poll();
    }

    public static boolean hasInterstitial() {
        if (!interstitialPrimaryPool.isEmpty() && interstitialPrimaryPool.peek().isAdLoaded()) return true;
        return !interstitialBackupPool.isEmpty() && interstitialBackupPool.peek().isAdLoaded();
    }

    // --- REWARDED ---
    public static void fillRewardedPool(Activity activity) {
        if (!AdGlide.isRewardedEnabled()) return;
        int currentTotal = rewardedPrimaryPool.size() + rewardedBackupPool.size();
        if (currentTotal >= MAX_POOL_SIZE || isLoadingRewarded) return;
        
        isLoadingRewarded = true;
        Log.d(TAG, "Filling Rewarded Pool. Current total: " + currentTotal);
        
        RewardedAd.Builder builder = new RewardedAd.Builder(activity);
        builder.load(new AdGlideCallback() {
            @Override
            public void onAdLoaded(String network) {
                String primary = AdGlide.getConfig() != null ? AdGlide.getConfig().getPrimaryNetwork() : "";
                if (network.equals(primary)) {
                    rewardedPrimaryPool.offer(builder);
                } else {
                    rewardedBackupPool.offer(builder);
                }
                isLoadingRewarded = false;
                fillRewardedPool(activity);
            }
            @Override
            public void onAdFailedToLoad(String error) {
                isLoadingRewarded = false;
            }
        });
    }

    public static RewardedAd.Builder getRewarded() {
        if (!rewardedPrimaryPool.isEmpty()) return rewardedPrimaryPool.poll();
        return rewardedBackupPool.poll();
    }

    public static boolean hasRewarded() {
        if (!rewardedPrimaryPool.isEmpty() && rewardedPrimaryPool.peek().isAdAvailable()) return true;
        return !rewardedBackupPool.isEmpty() && rewardedBackupPool.peek().isAdAvailable();
    }

    // --- REWARDED INTERSTITIAL ---
    public static void fillRewardedInterstitialPool(Activity activity) {
        if (!AdGlide.isRewardedInterstitialEnabled()) return;
        int currentTotal = rewardedInterstitialPrimaryPool.size() + rewardedInterstitialBackupPool.size();
        if (currentTotal >= MAX_POOL_SIZE || isLoadingRewardedInterstitial) return;
        
        isLoadingRewardedInterstitial = true;
        Log.d(TAG, "Filling Rewarded Interstitial Pool. Current total: " + currentTotal);
        
        RewardedInterstitialAd.Builder builder = new RewardedInterstitialAd.Builder(activity);
        builder.loadRewardedInterstitialAd(new AdGlideCallback() {
            @Override
            public void onAdLoaded(String network) {
                String primary = AdGlide.getConfig() != null ? AdGlide.getConfig().getPrimaryNetwork() : "";
                if (network.equals(primary)) {
                    rewardedInterstitialPrimaryPool.offer(builder);
                } else {
                    rewardedInterstitialBackupPool.offer(builder);
                }
                isLoadingRewardedInterstitial = false;
                fillRewardedInterstitialPool(activity);
            }
            @Override
            public void onAdFailedToLoad(String error) {
                isLoadingRewardedInterstitial = false;
            }
        });
    }

    public static RewardedInterstitialAd.Builder getRewardedInterstitial() {
        if (!rewardedInterstitialPrimaryPool.isEmpty()) return rewardedInterstitialPrimaryPool.poll();
        return rewardedInterstitialBackupPool.poll();
    }

    public static boolean hasRewardedInterstitial() {
        if (!rewardedInterstitialPrimaryPool.isEmpty() && rewardedInterstitialPrimaryPool.peek().isAdAvailable()) return true;
        return !rewardedInterstitialBackupPool.isEmpty() && rewardedInterstitialBackupPool.peek().isAdAvailable();
    }

    // --- APP OPEN ---
    public static void fillAppOpenPool(Activity activity) {
        if (!AdGlide.isAppOpenEnabled()) return;
        int currentTotal = appOpenPrimaryPool.size() + appOpenBackupPool.size();
        if (currentTotal >= MAX_POOL_SIZE || isLoadingAppOpen) return;
        
        isLoadingAppOpen = true;
        Log.d(TAG, "Filling App Open Pool. Current total: " + currentTotal);
        
        AppOpenAd.Builder builder = new AppOpenAd.Builder(activity);
        builder.load(new AdGlideCallback() {
            @Override
            public void onAdLoaded(String network) {
                String primary = AdGlide.getConfig() != null ? AdGlide.getConfig().getPrimaryNetwork() : "";
                if (network.equals(primary)) {
                    appOpenPrimaryPool.offer(builder);
                } else {
                    appOpenBackupPool.offer(builder);
                }
                isLoadingAppOpen = false;
                fillAppOpenPool(activity);
            }
            @Override
            public void onAdFailedToLoad(String error) {
                isLoadingAppOpen = false;
            }
        });
    }

    public static AppOpenAd.Builder getAppOpen() {
        if (!appOpenPrimaryPool.isEmpty()) return appOpenPrimaryPool.poll();
        return appOpenBackupPool.poll();
    }

    public static boolean hasAppOpen() {
        if (!appOpenPrimaryPool.isEmpty() && appOpenPrimaryPool.peek().isAdAvailable()) return true;
        return !appOpenBackupPool.isEmpty() && appOpenBackupPool.peek().isAdAvailable();
    }

    // --- NATIVE ---
    public static void fillNativePool(Activity activity, String style) {
        if (!AdGlide.isNativeEnabled()) return;
        boolean isSmall = "small".equalsIgnoreCase(style);
        Queue<com.partharoypc.adglide.format.NativeAd.Builder> primaryPool = isSmall ? nativeSmallPrimaryPool : nativeMediumPrimaryPool;
        Queue<com.partharoypc.adglide.format.NativeAd.Builder> backupPool = isSmall ? nativeSmallBackupPool : nativeMediumBackupPool;
        
        int currentTotal = primaryPool.size() + backupPool.size();
        if (currentTotal >= MAX_POOL_SIZE) return;
        if (isSmall && isLoadingNativeSmall) return;
        if (!isSmall && isLoadingNativeMedium) return;

        if (isSmall) isLoadingNativeSmall = true; else isLoadingNativeMedium = true;
        Log.d(TAG, "Filling Native [" + style + "] Pool. Current total: " + currentTotal);

        com.partharoypc.adglide.format.NativeAd.Builder builder = new com.partharoypc.adglide.format.NativeAd.Builder(activity)
                .style(style);
        
        builder.load(new AdGlideCallback() {
            @Override
            public void onAdLoaded(String network) {
                String primary = AdGlide.getConfig() != null ? AdGlide.getConfig().getPrimaryNetwork() : "";
                if (network.equals(primary)) {
                    primaryPool.offer(builder);
                } else {
                    backupPool.offer(builder);
                }
                if (isSmall) isLoadingNativeSmall = false; else isLoadingNativeMedium = false;
                fillNativePool(activity, style);
            }
            @Override
            public void onAdFailedToLoad(String error) {
                if (isSmall) isLoadingNativeSmall = false; else isLoadingNativeMedium = false;
            }
        });
    }

    public static com.partharoypc.adglide.format.NativeAd.Builder getNative(String style) {
        boolean isSmall = "small".equalsIgnoreCase(style);
        Queue<com.partharoypc.adglide.format.NativeAd.Builder> primaryPool = isSmall ? nativeSmallPrimaryPool : nativeMediumPrimaryPool;
        Queue<com.partharoypc.adglide.format.NativeAd.Builder> backupPool = isSmall ? nativeSmallBackupPool : nativeMediumBackupPool;

        if (!primaryPool.isEmpty()) return primaryPool.poll();
        return backupPool.poll();
    }

    public static boolean hasNative(String style) {
        boolean isSmall = "small".equalsIgnoreCase(style);
        Queue<com.partharoypc.adglide.format.NativeAd.Builder> primaryPool = isSmall ? nativeSmallPrimaryPool : nativeMediumPrimaryPool;
        Queue<com.partharoypc.adglide.format.NativeAd.Builder> backupPool = isSmall ? nativeSmallBackupPool : nativeMediumBackupPool;

        if (!primaryPool.isEmpty() && primaryPool.peek().isAdLoaded()) return true;
        return !backupPool.isEmpty() && backupPool.peek().isAdLoaded();
    }
}
