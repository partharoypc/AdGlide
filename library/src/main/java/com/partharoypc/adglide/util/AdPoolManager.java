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

public class AdPoolManager {
    private static final String TAG = "AdGlide.Pool";
    private static final int MAX_POOL_SIZE = 1;

    private static final Map<AdFormat, PoolSet<?>> pools = new EnumMap<>(AdFormat.class);
    private static final Map<String, PoolSet<NativeAd.Builder>> nativePools = new ConcurrentHashMap<>();
    private static final Map<AdFormat, AtomicBoolean> loadingState = new EnumMap<>(AdFormat.class);
    private static final Map<String, AtomicBoolean> nativeLoadingState = new ConcurrentHashMap<>();

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
            if (isPrimary)
                primary.offer(item);
            else
                backup.offer(item);
        }

        T poll() {
            T item = primary.poll();
            if (item != null)
                return item;
            return backup.poll();
        }

        boolean hasAvailable(AdCheck<T> check) {
            T pHead = primary.peek();
            if (pHead != null && check.isAvailable(pHead))
                return true;
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
                fillGenericPool(activity, AdFormat.REWARDED_INTERSTITIAL,
                        () -> new RewardedInterstitialAd.Builder(activity),
                        (builder, callback) -> builder.load(callback));
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
        if (pool == null) return null;
        
        InterstitialAd.Builder builder;
        while ((builder = pool.poll()) != null) {
            if (builder.getActivity() != null && !builder.getActivity().isFinishing()) {
                return builder;
            }
            AdGlideLog.d(TAG, "Discarding pooled Interstitial ad: Original Activity context was destroyed.");
        }
        return null;
    }

    public static boolean hasInterstitial() {
        PoolSet<InterstitialAd.Builder> pool = getPool(AdFormat.INTERSTITIAL);
        return pool != null && pool.hasAvailable(ad -> ad.isAdLoaded());
    }

    public static RewardedAd.Builder getRewarded() {
        PoolSet<RewardedAd.Builder> pool = getPool(AdFormat.REWARDED);
        if (pool == null) return null;
        
        RewardedAd.Builder builder;
        while ((builder = pool.poll()) != null) {
            if (builder.getActivity() != null && !builder.getActivity().isFinishing()) {
                return builder;
            }
            AdGlideLog.d(TAG, "Discarding pooled Rewarded ad: Original Activity context was destroyed.");
        }
        return null;
    }

    public static boolean hasRewarded() {
        PoolSet<RewardedAd.Builder> pool = getPool(AdFormat.REWARDED);
        return pool != null && pool.hasAvailable(ad -> ad.isAdAvailable());
    }

    public static RewardedInterstitialAd.Builder getRewardedInterstitial() {
        PoolSet<RewardedInterstitialAd.Builder> pool = getPool(AdFormat.REWARDED_INTERSTITIAL);
        if (pool == null) return null;
        
        RewardedInterstitialAd.Builder builder;
        while ((builder = pool.poll()) != null) {
            if (builder.getActivity() != null && !builder.getActivity().isFinishing()) {
                return builder;
            }
            AdGlideLog.d(TAG, "Discarding pooled Rewarded Interstitial ad: Original Activity context was destroyed.");
        }
        return null;
    }

    public static boolean hasRewardedInterstitial() {
        PoolSet<RewardedInterstitialAd.Builder> pool = getPool(AdFormat.REWARDED_INTERSTITIAL);
        return pool != null && pool.hasAvailable(ad -> ad.isAdAvailable());
    }

    public static AppOpenAd.Builder getAppOpen() {
        PoolSet<AppOpenAd.Builder> pool = getPool(AdFormat.APP_OPEN);
        if (pool == null) return null;
        
        AppOpenAd.Builder builder;
        while ((builder = pool.poll()) != null) {
            if (builder.getActivity() != null && !builder.getActivity().isFinishing()) {
                return builder;
            }
            AdGlideLog.d(TAG, "Discarding pooled App Open ad: Original Activity context was destroyed.");
        }
        return null;
    }

    public static boolean hasAppOpen() {
        PoolSet<AppOpenAd.Builder> pool = getPool(AdFormat.APP_OPEN);
        return pool != null && pool.hasAvailable(ad -> ad.isAdAvailable());
    }

    public static NativeAd.Builder getNative(String style) {
        PoolSet<NativeAd.Builder> poolSet = nativePools.get(style.toLowerCase(Locale.ROOT));
        if (poolSet == null) return null;
        
        NativeAd.Builder builder;
        while ((builder = poolSet.poll()) != null) {
            if (builder.getActivity() != null && !builder.getActivity().isFinishing()) {
                return builder;
            }
            AdGlideLog.d(TAG, "Discarding pooled Native ad [" + style + "]: Original Activity context was destroyed.");
        }
        return null;
    }

    public static boolean hasNative(String style) {
        PoolSet<NativeAd.Builder> poolSet = nativePools.get(style.toLowerCase(Locale.ROOT));
        return poolSet != null && poolSet.hasAvailable(ad -> ad.isAdLoaded());
    }

    /**
     * Cache an ad that loaded after the initial request timed out (Late Fill).
     * This helps achieve a 95%+ Show Rate by ensuring every match eventually becomes an impression.
     */
    public static <T> void cacheLateFill(AdFormat format, String network, T builder) {
        if (builder == null) return;
        
        AdGlide.notifyLateMatchSaved(format.toString(), network);
        
        PoolSet<T> poolSet = getPool(format);
        if (poolSet != null) {
            String primary = AdGlide.getConfig() != null ? AdGlide.getConfig().getPrimaryNetwork() : "";
            boolean isPrimary = (network != null && network.equals(primary));
            
            // Only cache if we have space, to avoid memory leaks
            int limit = (format == AdFormat.REWARDED || format == AdFormat.REWARDED_INTERSTITIAL || format == AdFormat.APP_OPEN) ? 1 : MAX_POOL_SIZE;
            if (poolSet.size() < limit + 1) { // Allow +1 for late fills to be ready for the next request
                poolSet.offer(builder, isPrimary);
                AdGlideLog.d(TAG, "Cached late fill for " + format + " from [" + network + "] into " + (isPrimary ? "Primary" : "Backup") + " pool.");
            } else {
                AdGlideLog.d(TAG, "Late fill for " + format + " discarded (Pool already sufficiently full).");
            }
        }
    }

    public static void cacheLateFillNative(String network, NativeAd.Builder builder) {
        if (builder == null) return;
        
        AdGlide.notifyLateMatchSaved(AdFormat.NATIVE.toString(), network);
        
        String style = builder.getNativeStyle();
        PoolSet<NativeAd.Builder> poolSet = nativePools.get(style.toLowerCase(Locale.ROOT));
        if (poolSet != null && poolSet.size() < MAX_POOL_SIZE + 1) {
             poolSet.offer(builder, true); // Treat late fills as primary candidates
             AdGlideLog.d(TAG, "Cached late fill for NativeStyle [" + style + "] after timeout.");
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> PoolSet<T> getPool(AdFormat format) {
        return (PoolSet<T>) pools.get(format);
    }

    // --- GENERIC FILLERS ---
    private interface AdBuilderProvider<T> {
        T create();
    }

    private interface AdLoadAction<T> {
        void load(T ad, AdGlideCallback callback);
    }

    @SuppressWarnings("unchecked")
    private static <T> void fillGenericPool(Activity activity, AdFormat format, AdBuilderProvider<T> provider,
            AdLoadAction<T> loader) {
        if (!isFormatEnabled(format) || activity == null || activity.isFinishing())
            return;

        // PROTECT SHOW RATE: Don't request new ads while another one is showing
        if (AdGlide.isAdShowing()) {
            return;
        }

        // REDUCE WASTE: Don't request unless the next show is "imminent" (within 1 click)
        if (!AdGlide.isImminent(format)) {
            return;
        }

        PoolSet<T> poolSet = (PoolSet<T>) pools.get(format);
        int limit = (format == AdFormat.REWARDED || format == AdFormat.REWARDED_INTERSTITIAL
                || format == AdFormat.APP_OPEN) ? 1 : MAX_POOL_SIZE;

        AtomicBoolean loading = loadingState.get(format);
        if (poolSet.size() >= limit || (loading != null && loading.get()))
            return;

        if (loading != null && !loading.compareAndSet(false, true))
            return;

        final WeakReference<Activity> activityRef = new WeakReference<>(activity);
        T builder = provider.create();

        loader.load(builder, new AdGlideCallback() {
            @Override
            public void onAdLoaded(String network) {
                if (loading != null)
                    loading.set(false);
                String primary = AdGlide.getConfig() != null ? AdGlide.getConfig().getPrimaryNetwork() : "";
                poolSet.offer(builder, network.equals(primary));
            }

            @Override
            public void onAdFailedToLoad(String error) {
                if (loading != null)
                    loading.set(false);
            }
        });
    }

    public static void fillNativePool(Activity activity, String style) {
        if (!AdGlide.isNativeEnabled() || activity == null || activity.isFinishing() || style == null)
            return;

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

        if (poolSet.size() >= MAX_POOL_SIZE)
            return;

        AtomicBoolean loading = nativeLoadingState.get(style.toLowerCase(Locale.ROOT));
        if (loading == null) {
            loading = new AtomicBoolean(false);
            nativeLoadingState.put(style.toLowerCase(Locale.ROOT), loading);
        }

        if (!loading.compareAndSet(false, true))
            return;
            
        final AtomicBoolean finalLoading = loading;
        final WeakReference<Activity> activityRef = new WeakReference<>(activity);
        NativeAd.Builder builder = new NativeAd.Builder(activity).style(style);
        builder.load(new AdGlideCallback() {
            @Override
            public void onAdLoaded(String network) {
                finalLoading.set(false);
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
                finalLoading.set(false);
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

    public static void clearPools() {
        for (PoolSet<?> pool : pools.values()) {
            pool.primary.clear();
            pool.backup.clear();
        }
        nativePools.clear();
        for (AtomicBoolean loading : loadingState.values()) {
            loading.set(false);
        }
        for (AtomicBoolean loading : nativeLoadingState.values()) {
            loading.set(false);
        }
        AdGlideLog.d(TAG, "Ad pools cleared.");
    }
}
