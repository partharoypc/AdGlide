package com.partharoypc.adglide;

import com.partharoypc.adglide.util.Constant;

/**
 * Supported ad networks in AdGlide.
 */
public enum AdGlideNetwork {
    ADMOB(Constant.ADMOB),
    META(Constant.META),
    UNITY(Constant.UNITY),
    APPLOVIN(Constant.APPLOVIN),
    APPLOVIN_MAX(Constant.APPLOVIN_MAX),
    IRONSOURCE(Constant.IRONSOURCE),
    STARTAPP(Constant.STARTAPP),
    WORTISE(Constant.WORTISE),
    NONE(Constant.NONE),

    // Bidding variants
    META_BIDDING_ADMOB(Constant.META_BIDDING_ADMOB),
    META_BIDDING_APPLOVIN_MAX(Constant.META_BIDDING_APPLOVIN_MAX),
    META_BIDDING_IRONSOURCE(Constant.META_BIDDING_IRONSOURCE);

    private final String value;

    AdGlideNetwork(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Finds the matching enum for a string value.
     * 
     * @param value The ad network string key.
     * @return The matching enum, or NONE if not found.
     */
    public static AdGlideNetwork fromString(String value) {
        for (AdGlideNetwork network : AdGlideNetwork.values()) {
            if (network.value.equalsIgnoreCase(value)) {
                return network;
            }
        }
        return NONE;
    }

    /**
     * Converts a vararg of AdGlideNetwork to a String array.
     * 
     * @param networks The network enums to convert.
     * @return An array of string keys.
     */
    public static String[] toStringArray(AdGlideNetwork... networks) {
        if (networks == null)
            return new String[0];
        String[] result = new String[networks.length];
        for (int i = 0; i < networks.length; i++) {
            result[i] = networks[i].getValue();
        }
        return result;
    }
}
