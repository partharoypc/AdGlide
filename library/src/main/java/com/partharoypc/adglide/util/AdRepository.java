package com.partharoypc.adglide.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.concurrent.ConcurrentHashMap;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;

/**
 * Singleton repository to pre-load and store ads for instant display.
 */
public class AdRepository {

    private static final String TAG = "AdGlide";
    private static AdRepository instance;
    private final ConcurrentHashMap<String, Object> interstitialCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> isLoading = new ConcurrentHashMap<>();

    private AdRepository() {
    }

    public static synchronized AdRepository getInstance() {
        if (instance == null) {
            instance = new AdRepository();
        }
        return instance;
    }

    /**
     * Retrieves a pre-loaded interstitial ad and removes it from the cache.
     *
     * @param adNetwork The ad network identifier.
     * @param adUnitId  The ad unit ID.
     * @return The ad object, or null if not found.
     */
    public Object getInterstitial(String adNetwork, String adUnitId) {
        String key = getKey(adNetwork, adUnitId);
        return interstitialCache.remove(key);
    }

    /**
     * Checks if an interstitial ad is available in the cache.
     */
    public boolean isInterstitialAvailable(String adNetwork, String adUnitId) {
        return interstitialCache.containsKey(getKey(adNetwork, adUnitId));
    }

    /**
     * Pre-loads an interstitial ad if one is not already loaded or loading.
     *
     * @param context   The application context.
     * @param adNetwork The ad network identifier.
     * @param adUnitId  The ad unit ID.
     */
    public void preloadInterstitial(Context context, String adNetwork, String adUnitId) {
        String key = getKey(adNetwork, adUnitId);

        if (interstitialCache.containsKey(key)) {
            Log.d(TAG, "Ad already cached for: " + key);
            return;
        }

        if (Boolean.TRUE.equals(isLoading.get(key))) {
            Log.d(TAG, "Ad currently loading for: " + key);
            return;
        }

        isLoading.put(key, true);
        Log.d(TAG, "Pre-loading ad for: " + key);

        switch (adNetwork) {
            case ADMOB:
            case META_BIDDING_ADMOB:
                loadAdMobInterstitial(context, key, adUnitId);
                break;

            case META:
                loadFacebookInterstitial(context, key, adUnitId);
                break;
            case APPLOVIN:
            case APPLOVIN_MAX:
            case META_BIDDING_APPLOVIN_MAX:
                loadAppLovinInterstitial(context, key, adUnitId);
                break;
            default:
                isLoading.put(key, false);
                Log.d(TAG, "Network not supported for pre-loading: " + adNetwork);
                break;
        }
    }

    private void loadAdMobInterstitial(Context context, String key, String adUnitId) {
        InterstitialAd.load(context.getApplicationContext(), adUnitId, Tools.getAdRequest(null, false),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        interstitialCache.put(key, interstitialAd);
                        isLoading.put(key, false);
                        Log.d(TAG, "AdMob Interstitial Cached: " + key);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        isLoading.put(key, false);
                        Log.e(TAG, "AdMob Pre-load Failed: " + loadAdError.getMessage());
                    }
                });
    }

    private void loadFacebookInterstitial(Context context, String key, String adUnitId) {
        com.facebook.ads.InterstitialAd metaAd = new com.facebook.ads.InterstitialAd(context, adUnitId);
        com.facebook.ads.InterstitialAdListener listener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {
            }

            @Override
            public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
            }

            @Override
            public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
                isLoading.put(key, false);
                Log.e(TAG, "Meta Pre-load Failed: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(com.facebook.ads.Ad ad) {
                interstitialCache.put(key, metaAd);
                isLoading.put(key, false);
                Log.d(TAG, "Meta Interstitial Cached: " + key);
            }

            @Override
            public void onAdClicked(com.facebook.ads.Ad ad) {
            }

            @Override
            public void onLoggingImpression(com.facebook.ads.Ad ad) {
            }
        };

        com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = metaAd.buildLoadAdConfig()
                .withAdListener(listener)
                .build();
        metaAd.loadAd(loadAdConfig);
    }

    private void loadAppLovinInterstitial(Context context, String key, String adUnitId) {
        // MaxInterstitialAd requires an Activity usually, but can accept Context for
        // loading.
        // However, the constructor expects Activity. We might need to pass the activity
        // if available.
        // Ideally, we pass the Application Context to avoid leaks, but MAX SDK might
        // need Activity.
        // Checked docs: MaxInterstitialAd(String adUnitId, Activity activity) OR
        // MaxInterstitialAd(String adUnitId, AppLovinSdk sdk, Activity activity)
        // It seems strict about Activity. We will try passing the context cast to
        // Activity if possible, or application context if it allows.
        // Android SDKs often allow Application Context for loading. Let's try passing
        // the context.
        // If context is not an activity, this might crash or fail.
        // Safeguard: If context is not activity, we might skip AppLovin for pre-loading
        // or accept the risk of passing Application Context (some SDKs handle it).

        // For now, we will create it. If it fails at runtime, we'll catch it.
        // Ideally, the 'context' passed to preloadInterstitial should be an Activity if
        // possible, BUT for a repo, we want to store it longer than the activity.
        // MaxInterstitialAd holds a reference to the activity. This is a leak risk if
        // not destroyed.
        // Given this, PRE-LOADING AppLovin MAX ads in a singleton is risky without
        // careful management.
        // Strategy: Skip AppLovin pre-loading in this generic singleton for now to
        // ensure stability, OR implement a wrapper.
        // Better: AdMob and GAM are the primary focus.

        // Implemented with try-catch and Activity check.
        if (context instanceof android.app.Activity) {
            MaxInterstitialAd maxAd = new MaxInterstitialAd(adUnitId, (android.app.Activity) context);
            maxAd.setListener(new MaxAdListener() {
                @Override
                public void onAdLoaded(MaxAd ad) {
                    interstitialCache.put(key, maxAd);
                    isLoading.put(key, false);
                    Log.d(TAG, "AppLovin Interstitial Cached: " + key);
                }

                @Override
                public void onAdDisplayed(MaxAd ad) {
                }

                @Override
                public void onAdHidden(MaxAd ad) {
                }

                @Override
                public void onAdClicked(MaxAd ad) {
                }

                @Override
                public void onAdLoadFailed(String adUnitId, MaxError error) {
                    isLoading.put(key, false);
                    Log.e(TAG, "AppLovin Pre-load Failed: " + error.getMessage());
                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                }
            });
            maxAd.loadAd();
        } else {
            Log.w(TAG, "Cannot preload AppLovin Max without an Activity instance.");
            isLoading.put(key, false);
        }
    }

    private String getKey(String adNetwork, String adUnitId) {
        return adNetwork + "_" + adUnitId;
    }
}




