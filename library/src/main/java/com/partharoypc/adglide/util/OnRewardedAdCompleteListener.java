package com.partharoypc.adglide.util;

/**
 * Callback invoked when the user has earned a reward from a rewarded ad.
 */
@FunctionalInterface
public interface OnRewardedAdCompleteListener {
    /**
     * Called when the rewarded ad interaction is complete and the reward is earned.
     */
    void onRewardedAdComplete();
}

