package com.partharoypc.adglide.util;

public interface AdGlideCallback {
    /** Called when an ad is successfully loaded. */
    default void onAdLoaded() {
    }

    /**
     * Called when an ad is successfully loaded, providing the name of the winning
     * network.
     */
    default void onAdLoaded(String network) {
        onAdLoaded();
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

    /**
     * Called when an ad fails to show at runtime.
     * 
     * @param error Descriptive error message.
     */
    default void onAdShowFailed(String error) {
        onAdDismissed(); // Fallback to ensure state recovery for simple integrations
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
