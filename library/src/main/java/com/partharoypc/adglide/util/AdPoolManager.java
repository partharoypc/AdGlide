package com.partharoypc.adglide.util;

import android.app.Activity;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.format.AppOpenAd;
import com.partharoypc.adglide.format.InterstitialAd;
import com.partharoypc.adglide.format.RewardedAd;
import com.partharoypc.adglide.format.RewardedInterstitialAd;
import com.partharoypc.adglide.format.NativeAd;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.lang.ref.WeakReference;

/**
 * Manages pools of pre-loaded Zero-Wait Ads for instant delivery.
 * Refactored to use a more robust, generic architecture.
 */
public class AdPoolManager {
    private static final String TAG = "AdGlide.Pool";
    private static final int MAX_POOL_SIZE = 2;

    private static final Map<AdFormat, PoolSet<?>> pools = new EnumMap<>(AdFormat.class);
    private static final Map<String, PoolSet<NativeAd.Builder>> nativePools = new ConcurrentHashMap<>();
    private static final Map<AdFormat, AtomicBoolean> loadingState = new EnumMap<>(AdFormat.class);

    static {
        pools.put(AdFormat.INTERSTITIAL, new PoolSet<InterstitialAd.Builder>());
        pools.put(AdFormat.REWARDED, new PoolSet<RewardedAd.Builder>());
        pools.put(AdFormat.REWARDED_INTERSTITIAL, new PoolSet<RewardedInterstitialAd.Builder>());
        pools.put(AdFormat.APP_OPEN, new PoolSet<AppOpenAd.Builder>());
        
        for (AdFormat format : AdFormat.values()) {
            loadingState.put(format, new AtomicBoolean(false));
        }
    }

    private static class PoolSet<T> {
        final Queue<T> primary = new ConcurrentLinkedQueue<>();
        final Queue<T> backup = new ConcurrentLinkedQueue<>();

        void offer(T item, boolean isPrimary) {
            if (isPrimary) primary.offer(item);
            else backup.offer(item);
        }

        T poll() {
            T item = primary.poll();
            if (item != null) return item;
            return backup.poll();
        }

        boolean hasAvailable(AdCheck<T> check) {
            T pHead = primary.peek();
            if (pHead != null && check.isAvailable(pHead)) return true;
            T bHead = backup.peek();
            return bHead != null && check.isAvailable(bHead);
        }

        int size() {
            return primary.size() + backup.size();
        }
    }

    private interface AdCheck<T> {
        boolean isAvailable(T ad);
    }

    /**
     * Entry point to fill any ad pool.
     */
    public static void fillPool(Activity activity, AdFormat format, String nativeStyle) {
        switch (format) {
            case INTERSTITIAL:
                fillGenericPool(activity, AdFormat.INTERSTITIAL, () -> new InterstitialAd.Builder(activity),
                        (builder, callback) -> builder.load(callback));
                break;
            case REWARDED:
                fillGenericPool(activity, AdFormat.REWARDED, () -> new RewardedAd.Builder(activity),
                        (builder, callback) -> builder.load(callback));
                break;
            case REWARDED_INTERSTITIAL:
                fillGenericPool(activity, AdFormat.REWARDED_INTERSTITIAL, () -> new RewardedInterstitialAd.Builder(activity),
                        (builder, callback) -> builder.loadRewardedInterstitialAd(callback));
                break;
            case APP_OPEN:
                fillGenericPool(activity, AdFormat.APP_OPEN, () -> new AppOpenAd.Builder(activity),
                        (builder, callback) -> builder.load(callback));
                break;
            case NATIVE:
                fillNativePool(activity, nativeStyle);
                break;
            default:
                break;
        }
    }

    public static void fillInterstitialPool(Activity activity) {
        fillPool(activity, AdFormat.INTERSTITIAL, null);
    }

    public static void fillRewardedPool(Activity activity) {
        fillPool(activity, AdFormat.REWARDED, null);
    }

    public static void fillRewardedInterstitialPool(Activity activity) {
        fillPool(activity, AdFormat.REWARDED_INTERSTITIAL, null);
    }

    public static void fillAppOpenPool(Activity activity) {
        fillPool(activity, AdFormat.APP_OPEN, null);
    }

    public static InterstitialAd.Builder getInterstitial() {
        PoolSet<InterstitialAd.Builder> pool = getPool(AdFormat.INTERSTITIAL);
        return pool != null ? pool.poll() : null;
    }

    public static boolean hasInterstitial() {
        PoolSet<InterstitialAd.Builder> pool = getPool(AdFormat.INTERSTITIAL);
        return pool != null && pool.hasAvailable(ad -> ad.isAdLoaded());
    }

    public static RewardedAd.Builder getRewarded() {
        PoolSet<RewardedAd.Builder> pool = getPool(AdFormat.REWARDED);
        return pool != null ? pool.poll() : null;
    }

    public static boolean hasRewarded() {
        PoolSet<RewardedAd.Builder> pool = getPool(AdFormat.REWARDED);
        return pool != null && pool.hasAvailable(ad -> ad.isAdAvailable());
    }

    public static RewardedInterstitialAd.Builder getRewardedInterstitial() {
        PoolSet<RewardedInterstitialAd.Builder> pool = getPool(AdFormat.REWARDED_INTERSTITIAL);
        return pool != null ? pool.poll() : null;
    }

    public static boolean hasRewardedInterstitial() {
        PoolSet<RewardedInterstitialAd.Builder> pool = getPool(AdFormat.REWARDED_INTERSTITIAL);
        return pool != null && pool.hasAvailable(ad -> ad.isAdAvailable());
    }

    public static AppOpenAd.Builder getAppOpen() {
        PoolSet<AppOpenAd.Builder> pool = getPool(AdFormat.APP_OPEN);
        return pool != null ? pool.poll() : null;
    }

    public static boolean hasAppOpen() {
        PoolSet<AppOpenAd.Builder> pool = getPool(AdFormat.APP_OPEN);
        return pool != null && pool.hasAvailable(ad -> ad.isAdAvailable());
    }

    public static NativeAd.Builder getNative(String style) {
        PoolSet<NativeAd.Builder> poolSet = nativePools.get(style.toLowerCase(Locale.ROOT));
        return poolSet != null ? poolSet.poll() : null;
    }

    public static boolean hasNative(String style) {
        PoolSet<NativeAd.Builder> poolSet = nativePools.get(style.toLowerCase(Locale.ROOT));
        return poolSet != null && poolSet.hasAvailable(ad -> ad.isAdLoaded());
    }

    @SuppressWarnings("unchecked")
    private static <T> PoolSet<T> getPool(AdFormat format) {
        return (PoolSet<T>) pools.get(format);
    }

    // --- GENERIC FILLERS ---
    private interface AdBuilderProvider<T> { T create(); }
    private interface AdLoadAction<T> { void load(T ad, AdGlideCallback callback); }

    @SuppressWarnings("unchecked")
    private static <T> void fillGenericPool(Activity activity, AdFormat format, AdBuilderProvider<T> provider, AdLoadAction<T> loader) {
        if (!isFormatEnabled(format) || activity == null || activity.isFinishing()) return;

        PoolSet<T> poolSet = (PoolSet<T>) pools.get(format);
        int limit = (format == AdFormat.REWARDED || format == AdFormat.REWARDED_INTERSTITIAL || format == AdFormat.APP_OPEN) ? 1 : MAX_POOL_SIZE;

        AtomicBoolean loading = loadingState.get(format);
        if (poolSet.size() >= limit || (loading != null && loading.get())) return;

        if (loading != null && !loading.compareAndSet(false, true)) return;

        final WeakReference<Activity> activityRef = new WeakReference<>(activity);
        T builder = provider.create();

        loader.load(builder, new AdGlideCallback() {
            @Override
            public void onAdLoaded(String network) {
                if (loading != null) loading.set(false);
                String primary = AdGlide.getConfig() != null ? AdGlide.getConfig().getPrimaryNetwork() : "";
                poolSet.offer(builder, network.equals(primary));

                // Decouple refill call to prevent StackOverflow on immediate callbacks
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    Activity currentActivity = activityRef.get();
                    if (currentActivity != null && !currentActivity.isFinishing()) {
                        fillGenericPool(currentActivity, format, provider, loader);
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(String error) {
                if (loading != null) loading.set(false);
            }
        });
    }

    public static void fillNativePool(Activity activity, String style) {
        if (!AdGlide.isNativeEnabled() || activity == null || activity.isFinishing() || style == null) return;
        
        PoolSet<NativeAd.Builder> poolSet = nativePools.get(style.toLowerCase(Locale.ROOT));
        if (poolSet == null) {
            synchronized (nativePools) {
                poolSet = nativePools.get(style.toLowerCase(Locale.ROOT));
                if (poolSet == null) {
                    poolSet = new PoolSet<>();
                    nativePools.put(style.toLowerCase(Locale.ROOT), poolSet);
                }
            }
        }
        final PoolSet<NativeAd.Builder> finalPoolSet = poolSet;

        if (poolSet.size() >= MAX_POOL_SIZE) return;

        final WeakReference<Activity> activityRef = new WeakReference<>(activity);
        NativeAd.Builder builder = new NativeAd.Builder(activity).style(style);
        builder.load(new AdGlideCallback() {
            @Override
            public void onAdLoaded(String network) {
                String primary = AdGlide.getConfig() != null ? AdGlide.getConfig().getPrimaryNetwork() : "";
                finalPoolSet.offer(builder, network.equals(primary));

                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    Activity currentActivity = activityRef.get();
                    if (currentActivity != null && !currentActivity.isFinishing()) {
                        fillNativePool(currentActivity, style);
                    }
                });
            }
            @Override
            public void onAdFailedToLoad(String error) {
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

    public static void clear() {
        for (PoolSet<?> pool : pools.values()) {
            pool.primary.clear();
            pool.backup.clear();
        }
        nativePools.clear();
        for (AtomicBoolean loading : loadingState.values()) {
            loading.set(false);
        }
        AdGlideLog.d(TAG, "Ad pools cleared.");
    }
}
