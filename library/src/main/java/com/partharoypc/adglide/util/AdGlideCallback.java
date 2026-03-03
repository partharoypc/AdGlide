package com.partharoypc.adglide.util;

/**
 * Unified callback for all ad events in the AdGlide SDK.
 * Provides default empty implementations to allow developers to override only
 * needed methods.
 */
public interface AdGlideCallback {
    /** Called when an ad is successfully loaded. */
    default void onAdLoaded() {
    }

    /**
     * Called when an ad fails to load.
     * 
     * @param error Descriptive error message.
     */
    default void onAdFailedToLoad(String error) {
    }

    /** Called when an ad is displayed to the user. */
    default void onAdShowed() {
    }

    /** Called when an ad is dismissed by the user. */
    default void onAdDismissed() {
    }

    /**
     * Called when a rewarded ad has been completed and the user should be rewarded.
     */
    default void onAdCompleted() {
    }

    /** Called when an ad is clicked. */
    default void onAdClicked() {
    }
}
