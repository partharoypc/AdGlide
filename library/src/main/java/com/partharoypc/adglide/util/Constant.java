package com.partharoypc.adglide.util;

public class Constant {

    private Constant() {
        // Utility class — prevent instantiation
    }

    // ad option
    public static final String ADMOB = "admob";
    public static final String META = "meta";
    public static final String STARTAPP = "startapp";
    public static final String UNITY = "unity";
    public static final String APPLOVIN = "applovin";
    public static final String APPLOVIN_MAX = "app_lovin_max";
    public static final String APPLOVIN_DISCOVERY = "app_lovin_discovery";
    public static final String IRONSOURCE = "ironsource";
    public static final String WORTISE = "wortise";
    public static final String NONE = "none";

    // ad bidding
    public static final String META_BIDDING_ADMOB = "meta_bidding_admob";
    public static final String META_BIDDING_APPLOVIN_MAX = "meta_bidding_applovin_max";
    public static final String META_BIDDING_IRONSOURCE = "meta_bidding_ironsource";

    // startapp native ad image parameters
    public static final int STARTAPP_IMAGE_SMALL = 2; // for image size 150px X 150px

    // unity banner ad size
    public static final int UNITY_ADS_BANNER_WIDTH_MEDIUM = 320;
    public static final int UNITY_ADS_BANNER_HEIGHT_MEDIUM = 50;

}
