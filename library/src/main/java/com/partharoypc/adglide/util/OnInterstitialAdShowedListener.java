package com.partharoypc.adglide.util;

/**
 * Callback invoked when an interstitial ad is successfully shown to the user.
 */
@FunctionalInterface
public interface OnInterstitialAdShowedListener {
    /** Called when the interstitial ad is displayed on screen. */
    void onInterstitialAdShowed();
}
