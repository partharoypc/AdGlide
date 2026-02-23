package com.partharoypc.adglide;

/**
 * Enum representing the available native ad styles in the AdGlide SDK.
 */
public enum AdGlideNativeStyle {
    SMALL("small"),
    MEDIUM("medium"),
    RADIO("radio"),
    NEWS("news"),
    VIDEO_SMALL("video_small"),
    VIDEO_LARGE("video_large"),
    STREAM("stream");

    private final String value;

    AdGlideNativeStyle(String value) {
        this.value = value;
    }

    /**
     * @return the string key associated with the style.
     */
    public String getValue() {
        return value;
    }

    /**
     * Converts a string value to the corresponding AdGlideNativeStyle enum.
     * 
     * @param value The string value to convert.
     * @return The matching enum, or MEDIUM if no match is found.
     */
    public static AdGlideNativeStyle fromString(String value) {
        for (AdGlideNativeStyle style : AdGlideNativeStyle.values()) {
            if (style.value.equalsIgnoreCase(value)) {
                return style;
            }
        }
        return MEDIUM;
    }
}
