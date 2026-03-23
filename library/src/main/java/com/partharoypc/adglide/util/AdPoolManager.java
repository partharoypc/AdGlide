package com.partharoypc.adglide.util;

import android.app.Activity;
import android.util.Log;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.format.AppOpenAd;
import com.partharoypc.adglide.format.InterstitialAd;
import com.partharoypc.adglide.format.RewardedAd;
import com.partharoypc.adglide.format.RewardedInterstitialAd;
import com.partharoypc.adglide.format.NativeAd;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages pools of pre-loaded Zero-Wait Ads for instant delivery.
 * Refactored to use a more robust, generic architecture.
 */
public class AdPoolManager {
    private static final String TAG = "AdGlide.Pool";
    private static final int MAX_POOL_SIZE = 2;

    private static final Map<AdFormat, PoolSet<?>> pools = new EnumMap<>(AdFormat.class);
    private static final Map<String, PoolSet<NativeAd.Builder>> nativePools = new ConcurrentHashMap<>();
    private static final Map<AdFormat, Boolean> loadingState = new EnumMap<>(AdFormat.class);

    static {
        pools.put(AdFormat.INTERSTITIAL, new PoolSet<InterstitialAd.Builder>());
        pools.put(AdFormat.REWARDED, new PoolSet<RewardedAd.Builder>());
        pools.put(AdFormat.REWARDED_INTERSTITIAL, new PoolSet<RewardedInterstitialAd.Builder>());
        pools.put(AdFormat.APP_OPEN, new PoolSet<AppOpenAd.Builder>());
        
        for (AdFormat format : AdFormat.values()) {
            loadingState.put(format, false);
        }
    }

    private static class PoolSet<T> {
        final Queue<T> primary = new LinkedList<>();
        final Queue<T> backup = new LinkedList<>();

        void offer(T item, boolean isPrimary) {
            if (isPrimary) primary.offer(item);
            else backup.offer(item);
        }

        T poll() {
            if (!primary.isEmpty()) return primary.poll();
            return backup.poll();
        }

        boolean hasAvailable(AdCheck<T> check) {
            if (!primary.isEmpty() && check.isAvailable(primary.peek())) return true;
            return !backup.isEmpty() && check.isAvailable(backup.peek());
        }

        int size() {
            return primary.size() + backup.size();
        }
    }

    private interface AdCheck<T> {
        boolean isAvailable(T ad);
    }

    // --- INTERSTITIAL ---
    public static void fillInterstitialPool(Activity activity) {
        fillGenericPool(activity, AdFormat.INTERSTITIAL, () -> new InterstitialAd.Builder(activity), 
            (builder, callback) -> builder.load(callback));
    }

    public static InterstitialAd.Builder getInterstitial() {
        return (InterstitialAd.Builder) pools.get(AdFormat.INTERSTITIAL).poll();
    }

    public static boolean hasInterstitial() {
        return pools.get(AdFormat.INTERSTITIAL).hasAvailable(ad -> ad.isAdLoaded());
    }

    // --- REWARDED ---
    public static void fillRewardedPool(Activity activity) {
        fillGenericPool(activity, AdFormat.REWARDED, () -> new RewardedAd.Builder(activity), 
            (builder, callback) -> builder.load(callback));
    }

    public static RewardedAd.Builder getRewarded() {
        return (RewardedAd.Builder) pools.get(AdFormat.REWARDED).poll();
    }

    public static boolean hasRewarded() {
        return pools.get(AdFormat.REWARDED).hasAvailable(ad -> ad.isAdAvailable());
    }

    // --- REWARDED INTERSTITIAL ---
    public static void fillRewardedInterstitialPool(Activity activity) {
        fillGenericPool(activity, AdFormat.REWARDED_INTERSTITIAL, () -> new RewardedInterstitialAd.Builder(activity), 
            (builder, callback) -> builder.loadRewardedInterstitialAd(callback));
    }

    public static RewardedInterstitialAd.Builder getRewardedInterstitial() {
        return (RewardedInterstitialAd.Builder) pools.get(AdFormat.REWARDED_INTERSTITIAL).poll();
    }

    public static boolean hasRewardedInterstitial() {
        return pools.get(AdFormat.REWARDED_INTERSTITIAL).hasAvailable(ad -> ad.isAdAvailable());
    }

    // --- APP OPEN ---
    public static void fillAppOpenPool(Activity activity) {
        fillGenericPool(activity, AdFormat.APP_OPEN, () -> new AppOpenAd.Builder(activity), 
            (builder, callback) -> builder.load(callback));
    }

    public static AppOpenAd.Builder getAppOpen() {
        return (AppOpenAd.Builder) pools.get(AdFormat.APP_OPEN).poll();
    }

    public static boolean hasAppOpen() {
        return pools.get(AdFormat.APP_OPEN).hasAvailable(ad -> ad.isAdAvailable());
    }

    // --- NATIVE ---
    public static void fillNativePool(Activity activity, String style) {
        if (!AdGlide.isNativeEnabled()) return;
        PoolSet<NativeAd.Builder> poolSet = nativePools.computeIfAbsent(style.toLowerCase(), k -> new PoolSet<>());
        
        if (poolSet.size() >= MAX_POOL_SIZE) return;
        
        NativeAd.Builder builder = new NativeAd.Builder(activity).style(style);
        builder.load(new AdGlideCallback() {
            @Override
            public void onAdLoaded(String network) {
                String primary = AdGlide.getConfig() != null ? AdGlide.getConfig().getPrimaryNetwork() : "";
                poolSet.offer(builder, network.equals(primary));
                fillNativePool(activity, style);
            }
            @Override
            public void onAdFailedToLoad(String error) {
            }
        });
    }

    public static NativeAd.Builder getNative(String style) {
        PoolSet<NativeAd.Builder> poolSet = nativePools.get(style.toLowerCase());
        return poolSet != null ? poolSet.poll() : null;
    }

    public static boolean hasNative(String style) {
        PoolSet<NativeAd.Builder> poolSet = nativePools.get(style.toLowerCase());
        return poolSet != null && poolSet.hasAvailable(ad -> ad.isAdLoaded());
    }

    // --- GENERIC FILLER ---
    private interface AdBuilderProvider<T> { T create(); }
    private interface AdLoadAction<T> { void load(T ad, AdGlideCallback callback); }

    @SuppressWarnings("unchecked")
    private static <T> void fillGenericPool(Activity activity, AdFormat format, AdBuilderProvider<T> provider, AdLoadAction<T> loader) {
        if (!isFormatEnabled(format)) return;
        
        PoolSet<T> poolSet = (PoolSet<T>) pools.get(format);
        if (poolSet.size() >= MAX_POOL_SIZE || loadingState.get(format)) return;

        loadingState.put(format, true);
        T builder = provider.create();
        
        loader.load(builder, new AdGlideCallback() {
            @Override
            public void onAdLoaded(String network) {
                String primary = AdGlide.getConfig() != null ? AdGlide.getConfig().getPrimaryNetwork() : "";
                poolSet.offer(builder, network.equals(primary));
                loadingState.put(format, false);
                fillGenericPool(activity, format, provider, loader);
            }

            @Override
            public void onAdFailedToLoad(String error) {
                loadingState.put(format, false);
            }
        });
    }

    private static boolean isFormatEnabled(AdFormat format) {
        return switch (format) {
            case INTERSTITIAL -> AdGlide.isInterstitialEnabled();
            case REWARDED -> AdGlide.isRewardedEnabled();
            case REWARDED_INTERSTITIAL -> AdGlide.isRewardedInterstitialEnabled();
            case APP_OPEN -> AdGlide.isAppOpenEnabled();
            default -> false;
        };
    }
}
