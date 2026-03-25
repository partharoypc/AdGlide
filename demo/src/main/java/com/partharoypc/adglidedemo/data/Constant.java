package com.partharoypc.adglidedemo.data;

public class Constant {

    public static final boolean AD_STATUS = true;
    public static boolean BANNER_STATUS = true;
    public static boolean INTERSTITIAL_STATUS = true;
    public static boolean NATIVE_STATUS = true;
    public static boolean REWARDED_STATUS = true;
    public static boolean REWARDED_INTERSTITIAL_STATUS = true;

    public static String AD_NETWORK = "admob";
    public static String BACKUP_AD_NETWORK = "none";

    // SDK Settings
    public static boolean TEST_MODE = false;
    public static boolean DEBUG_MODE = true;
    public static boolean ENABLE_DEBUG_HUD = true;
    public static int AD_RESPONSE_TIMEOUT_MS = 3500;
    public static int APP_OPEN_COOLDOWN_MINUTES = 30;

    // AdMob Test IDs
    public static final String ADMOB_BANNER_ID = "ca-app-pub-3940256099942544/6300978111";
    public static final String ADMOB_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712";
    public static final String ADMOB_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917";
    public static final String ADMOB_REWARDED_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/5354046379";
    public static final String ADMOB_NATIVE_ID = "ca-app-pub-3940256099942544/2247696110";
    public static final String ADMOB_APP_OPEN_AD_ID = "ca-app-pub-3940256099942544/9257395921";

    // Meta Audience Network
    public static final String META_BANNER_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
    public static final String META_INTERSTITIAL_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
    public static final String META_REWARDED_ID = "VID_HD_9_16_39S_APP_INSTALL#YOUR_PLACEMENT_ID";
    public static final String META_NATIVE_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";

    // StartApp
    public static final String STARTAPP_APP_ID = "205489527";
    // Note: StartApp does not use separate ad unit IDs — the App ID covers all formats

    // Unity Ads
    public static final String UNITY_GAME_ID = "4089993";
    public static final String UNITY_BANNER_ID = "banner";
    public static final String UNITY_INTERSTITIAL_ID = "video";
    public static final String UNITY_REWARDED_ID = "rewardedVideo";

    // AppLovin MAX
    public static final String APPLOVIN_BANNER_ID = "da17eff31ae69f15";
    public static final String APPLOVIN_INTERSTITIAL_ID = "98f6a586ed642919";
    public static final String APPLOVIN_MAX_REWARDED_ID = "98f6a586ed642919";
    public static final String APPLOVIN_NATIVE_MANUAL_ID = "87343269587e8998";
    public static final String APPLOVIN_APP_OPEN_AP_ID = "de9f381d132b859a";

    // AppLovin Discovery Zone IDs
    public static final String APPLOVIN_BANNER_ZONE_ID = "afb7122672e86340";
    public static final String APPLOVIN_BANNER_MREC_ZONE_ID = "81287b697d935c32";
    public static final String APPLOVIN_INTERSTITIAL_ZONE_ID = "b6eba8b976279ea5";
    public static final String APPLOVIN_DISC_REWARDED_ZONE_ID = "b6eba8b976279ea5";

    // IronSource
    public static final String IRONSOURCE_APP_KEY = "85460dcd";
    public static final String IRONSOURCE_BANNER_ID = "DefaultBanner";
    public static final String IRONSOURCE_INTERSTITIAL_ID = "DefaultInterstitial";
    public static final String IRONSOURCE_REWARDED_ID = "DefaultRewardedVideo";
    public static final String IRONSOURCE_NATIVE_ID = "DefaultNative";

    // Wortise
    public static final String WORTISE_APP_ID = "57659a6d-e448-47fb-9759-dd9ca0e1a6c4";
    public static final String WORTISE_BANNER_ID = "test-banner";
    public static final String WORTISE_INTERSTITIAL_ID = "test-interstitial";
    public static final String WORTISE_REWARDED_ID = "test-rewarded";
    public static final String WORTISE_REWARDED_INTERSTITIAL_ID = "test-rewarded";
    public static final String WORTISE_NATIVE_ID = "test-native";
    public static final String WORTISE_APP_OPEN_AD_ID = "test-app-open";

    // Native Ad Styles
    public static String NATIVE_STYLE = "medium";
    public static final String STYLE_SMALL = "small";
    public static final String STYLE_MEDIUM = "medium";
    public static final String STYLE_BANNER = "banner";
    public static final String STYLE_VIDEO = "video";

    // App Open Ad Config
    public static boolean isAppOpen = false;
    public static boolean FORCE_TO_SHOW_APP_OPEN_AD_ON_START = true;
    public static boolean OPEN_ADS_ON_START = true;
    public static boolean OPEN_ADS_ON_RESUME = true;
    public static String META_APP_OPEN_ID = "YOUR_PLACEMENT_ID";
    public static String APPLOVIN_REWARDED_INT_ID = "98f6a586ed642919";

    // Frequency & Interval Config
    public static int INTERSTITIAL_AD_INTERVAL = 1;
    public static int REWARDED_AD_INTERVAL = 1;
    public static final int NATIVE_AD_INDEX = 2;
    public static final int NATIVE_AD_INTERVAL = 8;

    // House Ad Config
    public static boolean HOUSE_AD_ENABLE = true;
    public static final String HOUSE_AD_BANNER_IMAGE = "https://images.sampleads.com/banner.png";
    public static final String HOUSE_AD_BANNER_URL = "https://play.google.com/store/apps/details?id=com.partharoypc.sample";
    public static final String HOUSE_AD_INTERSTITIAL_IMAGE = "https://images.sampleads.com/inter.png";
    public static final String HOUSE_AD_INTERSTITIAL_URL = "https://play.google.com/store/apps/details?id=com.partharoypc.sample";

    // House Native Ads
    public static final String HOUSE_AD_NATIVE_TITLE = "AdGlide: Premium Monetization";
    public static final String HOUSE_AD_NATIVE_DESC = "Experience the future of ad mediation with zero latency and premium fill rates.";
    public static final String HOUSE_AD_NATIVE_CTA = "Learn More";
    public static final String HOUSE_AD_NATIVE_IMAGE = "https://images.sampleads.com/native_large.png";
    public static final String HOUSE_AD_NATIVE_ICON = "https://images.sampleads.com/icon.png";

}
