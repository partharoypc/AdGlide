package com.partharoypc.adglide.provider.unity;

import android.app.Activity;
import android.util.Log;

import com.partharoypc.adglide.provider.RewardedProvider;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;

public class UnityRewardedProvider implements RewardedProvider {
    private String placementId;
    private boolean isAvailable = false;
    private static final String TAG = "AdGlide.Unity";

    @Override
    public void loadRewardedAd(Activity activity, String adUnitId, RewardedConfig config, RewardedListener listener) {
        this.placementId = adUnitId;
        UnityAds.load(adUnitId, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                Log.d(TAG, "Rewarded Ad loaded: " + placementId);
                isAvailable = true;
                listener.onAdLoaded();
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                Log.e(TAG, "Rewarded Ad failed to load: [" + error + "] " + message);
                isAvailable = false;
                listener.onAdFailedToLoad(message);
            }
        });
    }

    @Override
    public void showRewardedAd(Activity activity, RewardedListener listener) {
        if (isAvailable) {
            UnityAds.show(activity, placementId, new IUnityAdsShowListener() {
                @Override
                public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                    if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                        listener.onAdCompleted();
                    }
                    listener.onAdDismissed();
                }

                @Override
                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error,
                        String message) {
                    Log.e(TAG, "Rewarded Ad failed to show: [" + error + "] " + message);
                    isAvailable = false;
                }

                @Override
                public void onUnityAdsShowStart(String placementId) {
                }

                @Override
                public void onUnityAdsShowClick(String placementId) {
                }
            });
        }
    }

    @Override
    public boolean isAdAvailable() {
        return isAvailable;
    }

    @Override
    public void destroy() {
        isAvailable = false;
    }
}
