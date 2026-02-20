package com.partharoypc.adglide.util;

/**
 * Callback invoked when a rewarded ad has been successfully loaded and is ready
 * to show.
 */
@FunctionalInterface
public interface OnRewardedAdLoadedListener {
    /** Called when the rewarded ad is loaded and available. */
    void onRewardedAdLoaded();
}

