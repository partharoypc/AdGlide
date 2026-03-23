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

    private static final Queue<InterstitialAd.Builder> interstitialPool = new LinkedList<>();
    private static final Queue<RewardedAd.Builder> rewardedPool = new LinkedList<>();
    private static final Queue<RewardedInterstitialAd.Builder> rewardedInterstitialPool = new LinkedList<>();
    private static final Queue<AppOpenAd.Builder> appOpenPool = new LinkedList<>();

    // Native Pools
    private static final Queue<com.partharoypc.adglide.format.NativeAd.Builder> nativeSmallPool = new LinkedList<>();
    private static final Queue<com.partharoypc.adglide.format.NativeAd.Builder> nativeMediumPool = new LinkedList<>();

    private static boolean isLoadingInterstitial = false;
    private static boolean isLoadingRewarded = false;
    private static boolean isLoadingRewardedInterstitial = false;
    private static boolean isLoadingAppOpen = false;
    private static boolean isLoadingNativeSmall = false;
    private static boolean isLoadingNativeMedium = false;

    // --- INTERSTITIAL ---
    public static void fillInterstitialPool(Activity activity) {
        if (!AdGlide.isInterstitialEnabled()) return;
        if (interstitialPool.size() >= MAX_POOL_SIZE || isLoadingInterstitial) return;
        
        isLoadingInterstitial = true;
        Log.d(TAG, "Filling Interstitial Pool. Current size: " + interstitialPool.size());
        
        InterstitialAd.Builder builder = new InterstitialAd.Builder(activity);
        builder.load(new AdGlideCallback() {
            @Override
            public void onAdLoaded() {
                interstitialPool.offer(builder);
                isLoadingInterstitial = false;
                fillInterstitialPool(activity); // Recursively fill till max
            }
            @Override
            public void onAdFailedToLoad(String error) {
                isLoadingInterstitial = false;
            }
        });
    }

    public static InterstitialAd.Builder getInterstitial() {
        return interstitialPool.poll();
    }

    public static boolean hasInterstitial() {
        return !interstitialPool.isEmpty() && interstitialPool.peek().isAdLoaded() && interstitialPool.peek().getActivity() != null;
    }

    // --- REWARDED ---
    public static void fillRewardedPool(Activity activity) {
        if (!AdGlide.isRewardedEnabled()) return;
        if (rewardedPool.size() >= MAX_POOL_SIZE || isLoadingRewarded) return;
        
        isLoadingRewarded = true;
        Log.d(TAG, "Filling Rewarded Pool. Current size: " + rewardedPool.size());
        
        RewardedAd.Builder builder = new RewardedAd.Builder(activity);
        builder.load(new AdGlideCallback() {
            @Override
            public void onAdLoaded() {
                rewardedPool.offer(builder);
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
        return rewardedPool.poll();
    }

    public static boolean hasRewarded() {
        return !rewardedPool.isEmpty() && rewardedPool.peek().isAdAvailable() && rewardedPool.peek().getActivity() != null;
    }

    // --- REWARDED INTERSTITIAL ---
    public static void fillRewardedInterstitialPool(Activity activity) {
        if (!AdGlide.isRewardedInterstitialEnabled()) return;
        if (rewardedInterstitialPool.size() >= MAX_POOL_SIZE || isLoadingRewardedInterstitial) return;
        
        isLoadingRewardedInterstitial = true;
        Log.d(TAG, "Filling Rewarded Interstitial Pool. Current size: " + rewardedInterstitialPool.size());
        
        RewardedInterstitialAd.Builder builder = new RewardedInterstitialAd.Builder(activity);
        builder.loadRewardedInterstitialAd(new AdGlideCallback() {
            @Override
            public void onAdLoaded() {
                rewardedInterstitialPool.offer(builder);
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
        return rewardedInterstitialPool.poll();
    }

    public static boolean hasRewardedInterstitial() {
        return !rewardedInterstitialPool.isEmpty() && rewardedInterstitialPool.peek().isAdAvailable() && rewardedInterstitialPool.peek().getActivity() != null;
    }

    // --- APP OPEN ---
    public static void fillAppOpenPool(Activity activity) {
        if (!AdGlide.isAppOpenEnabled()) return;
        if (appOpenPool.size() >= MAX_POOL_SIZE || isLoadingAppOpen) return;
        
        isLoadingAppOpen = true;
        Log.d(TAG, "Filling App Open Pool. Current size: " + appOpenPool.size());
        
        AppOpenAd.Builder builder = new AppOpenAd.Builder(activity);
        builder.load(new AdGlideCallback() {
            @Override
            public void onAdLoaded() {
                appOpenPool.offer(builder);
                isLoadingAppOpen = false;
                fillAppOpenPool(activity);
            }
            @Override
            public void onAdFailedToLoad(String error) {
                isLoadingAppOpen = false;
            }
            @Override
            public void onAdDismissed() {
                isLoadingAppOpen = false;
            }
        });
    }

    public static AppOpenAd.Builder getAppOpen() {
        return appOpenPool.poll();
    }

    public static boolean hasAppOpen() {
        return !appOpenPool.isEmpty() && appOpenPool.peek().isAdAvailable() && appOpenPool.peek().getActivity() != null;
    }

    // --- NATIVE ---
    public static void fillNativePool(Activity activity, String style) {
        if (!AdGlide.isNativeEnabled()) return;
        boolean isSmall = "small".equalsIgnoreCase(style);
        Queue<com.partharoypc.adglide.format.NativeAd.Builder> pool = isSmall ? nativeSmallPool : nativeMediumPool;
        
        if (pool.size() >= MAX_POOL_SIZE) return;
        if (isSmall && isLoadingNativeSmall) return;
        if (!isSmall && isLoadingNativeMedium) return;

        if (isSmall) isLoadingNativeSmall = true; else isLoadingNativeMedium = true;
        Log.d(TAG, "Filling Native [" + style + "] Pool. Current size: " + pool.size());

        com.partharoypc.adglide.format.NativeAd.Builder builder = new com.partharoypc.adglide.format.NativeAd.Builder(activity)
                .style(style);
        
        builder.load(new AdGlideCallback() {
            @Override
            public void onAdLoaded() {
                pool.offer(builder);
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
        return "small".equalsIgnoreCase(style) ? nativeSmallPool.poll() : nativeMediumPool.poll();
    }

    public static boolean hasNative(String style) {
        Queue<com.partharoypc.adglide.format.NativeAd.Builder> pool = "small".equalsIgnoreCase(style) ? nativeSmallPool : nativeMediumPool;
        return !pool.isEmpty() && pool.peek().isAdLoaded() && pool.peek().getActivity() != null;
    }
}
