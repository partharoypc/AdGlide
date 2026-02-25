package com.partharoypc.adglide.provider.unity;

import android.app.Activity;
import android.util.Log;

import com.partharoypc.adglide.provider.InterstitialProvider;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

public class UnityInterstitialProvider implements InterstitialProvider {
    private boolean isLoaded = false;
    private String placementId;
    private static final String TAG = "AdGlide.Unity";

    @Override
    public void loadInterstitial(Activity activity, String adUnitId, InterstitialConfig config,
            InterstitialListener listener) {
        this.placementId = adUnitId;
        UnityAds.load(adUnitId, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                Log.d(TAG, "Interstitial Ad loaded: " + placementId);
                isLoaded = true;
                listener.onAdLoaded();
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                Log.e(TAG, "Interstitial Ad failed to load: [" + error + "] " + message);
                isLoaded = false;
                listener.onAdFailedToLoad(message);
            }
        });
    }

    @Override
    public void showInterstitial(Activity activity, InterstitialListener listener) {
        if (isLoaded) {
            UnityAds.show(activity, placementId, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                @Override
                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error,
                        String message) {
                    Log.e(TAG, "Interstitial Ad failed to show: [" + error + "] " + message);
                    isLoaded = false;
                    listener.onAdShowFailed(message);
                }

                @Override
                public void onUnityAdsShowStart(String placementId) {
                    listener.onAdShowed();
                }

                public void onUnityAdsShowClick(String placementId) {
                }

                @Override
                public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                    isLoaded = false;
                    listener.onAdDismissed();
                }
            });
        } else {
            listener.onAdShowFailed("Unity Interstitial not loaded");
        }
    }

    @Override
    public boolean isAdLoaded() {
        return isLoaded;
    }

    @Override
    public void destroy() {
        // Unity doesn't have a destroy for individual ads
    }
}
