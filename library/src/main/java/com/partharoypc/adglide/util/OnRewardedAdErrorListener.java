package com.partharoypc.adglide.util;

/**
 * Callback invoked when a rewarded ad fails to load or show.
 */
@FunctionalInterface
public interface OnRewardedAdErrorListener {
    /** Called when a rewarded ad error occurs. */
    void onRewardedAdError();
}

