package com.partharoypc.adglide.util;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;

/**
 * Centralized helper for ad revenue tracking across all networks.
 */
public class RevenueHelper {

    private RevenueHelper() {
        // Utility class
    }

    /**
     * Unified method to handle paid events from any network.
     *
     * @param valueMicros the value in micros
     * @param currency    the currency code (e.g., "USD")
     * @param precision   the precision type as a string
     * @param network     the network name (e.g., "AdMob", "AppLovin")
     * @param format      the ad format (e.g., "Banner", "Interstitial")
     * @param adUnitId    the ad unit ID
     */
    public static void logRevenue(double valueMicros, String currency, String precision, String network, String format,
            String adUnitId) {
        AdGlideConfig config = AdGlide.getConfig();
        OnPaidEventListener listener = (config != null) ? config.getOnPaidEventListener() : null;

        if (listener != null) {
            listener.onPaidEvent(
                    (long) valueMicros,
                    currency,
                    precision,
                    network + " " + format,
                    adUnitId);
        }
    }
}
