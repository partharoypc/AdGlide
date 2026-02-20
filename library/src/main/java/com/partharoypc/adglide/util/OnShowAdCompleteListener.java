package com.partharoypc.adglide.util;

/**
 * Callback invoked when an app open ad has completed showing.
 */
@FunctionalInterface
public interface OnShowAdCompleteListener {
    /** Called when the ad has finished showing or failed to show. */
    void onShowAdComplete();
}

