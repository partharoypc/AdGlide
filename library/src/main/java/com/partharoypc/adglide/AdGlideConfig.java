package com.partharoypc.adglide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.partharoypc.adglide.util.AdGlideLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdGlideConfig {

    // Global Settings
    private final boolean adStatus;
    private final String primaryNetwork;
    private final List<String> backupNetworks;
    private final boolean testMode;
    private final boolean debug;
    private final boolean autoLoad;
    private final boolean enableHouseAd;
    private final int interstitialInterval;
    private final int rewardedInterval;
    private final List<String> openAdExcludedActivities;
    private final boolean enableGDPR;
    private final boolean debugGDPR;
    private final boolean enableDebugHUD;
    private final int adResponseTimeoutMs;
    private boolean isValid = true;

    // Granular Ad Type Status
    private final boolean bannerStatus;
    private final boolean interstitialStatus;
    private final boolean nativeStatus;
    private final boolean rewardedStatus;
    private final boolean appOpenStatus;
    private final boolean rewardedInterstitialStatus;
    private final int appOpenCooldownMinutes;

    // App IDs
    private final String adMobAppId;
    private final String startappAppId;
    private final String unityGameId;
    private final String appLovinSdkKey;
    private final String ironSourceAppKey;
    private final String wortiseAppId;

    // Ad Unit IDs
    // Banner
    private final String adMobBannerId;
    private final String metaBannerId;
    private final String unityBannerId;
    private final String appLovinBannerId;
    private final String ironSourceBannerId;
    private final String wortiseBannerId;

    // Interstitial
    private final String adMobInterstitialId;
    private final String metaInterstitialId;
    private final String unityInterstitialId;
    private final String appLovinInterstitialId;
    private final String ironSourceInterstitialId;
    private final String wortiseInterstitialId;

    // Rewarded
    private final String adMobRewardedId;
    private final String metaRewardedId;
    private final String unityRewardedId;
    private final String appLovinRewardedId;
    private final String ironSourceRewardedId;
    private final String wortiseRewardedId;

    // Rewarded Interstitial
    private final String adMobRewardedIntId;
    private final String appLovinRewardedIntId;
    private final String unityRewardedIntId;
    private final String ironSourceRewardedIntId;
    private final String wortiseRewardedIntId;

    // App Open
    private final String adMobAppOpenId;
    private final String metaAppOpenId;
    private final String appLovinAppOpenId;
    private final String startappAppOpenId;
    private final String ironSourceAppOpenId;
    private final String wortiseAppOpenId;

    // Native
    private final String adMobNativeId;
    private final String metaNativeId;
    private final String appLovinNativeId;
    private final String ironSourceNativeId;
    private final String wortiseNativeId;

    // House Ad
    private final String houseAdBannerImage;
    private final String houseAdBannerClickUrl;
    private final String houseAdInterstitialImage;
    private final String houseAdInterstitialClickUrl;
    private final String houseAdNativeTitle;
    private final String houseAdNativeDescription;
    private final String houseAdNativeImage;
    private final String houseAdNativeIcon;
    private final String houseAdNativeCTA;
    private final String houseAdNativeClickUrl;

    private AdGlideConfig(Builder builder) {
        this.adStatus = builder.adStatus;
        this.primaryNetwork = builder.primaryNetwork;
        this.backupNetworks = builder.backupNetworks;
        this.testMode = builder.testMode;
        this.debug = builder.debug;
        this.autoLoad = builder.autoLoad;
        this.enableHouseAd = builder.enableHouseAd;
        this.interstitialInterval = builder.interstitialInterval;
        this.rewardedInterval = builder.rewardedInterval;
        this.openAdExcludedActivities = builder.openAdExcludedActivities;
        this.enableGDPR = builder.enableGDPR;
        this.debugGDPR = builder.debugGDPR;
        this.enableDebugHUD = builder.enableDebugHUD;
        this.adResponseTimeoutMs = builder.adResponseTimeoutMs;

        this.bannerStatus = builder.bannerStatus;
        this.interstitialStatus = builder.interstitialStatus;
        this.nativeStatus = builder.nativeStatus;
        this.rewardedStatus = builder.rewardedStatus;
        this.appOpenStatus = builder.appOpenStatus;
        this.rewardedInterstitialStatus = builder.rewardedInterstitialStatus;
        this.appOpenCooldownMinutes = builder.appOpenCooldownMinutes;

        this.adMobAppId = builder.adMobAppId;
        this.startappAppId = builder.startappAppId;
        this.unityGameId = builder.unityGameId;
        this.appLovinSdkKey = builder.appLovinSdkKey;
        this.ironSourceAppKey = builder.ironSourceAppKey;
        this.wortiseAppId = builder.wortiseAppId;

        this.adMobBannerId = builder.adMobBannerId;
        this.metaBannerId = builder.metaBannerId;
        this.unityBannerId = builder.unityBannerId;
        this.appLovinBannerId = builder.appLovinBannerId;
        this.ironSourceBannerId = builder.ironSourceBannerId;
        this.wortiseBannerId = builder.wortiseBannerId;

        this.adMobInterstitialId = builder.adMobInterstitialId;
        this.metaInterstitialId = builder.metaInterstitialId;
        this.unityInterstitialId = builder.unityInterstitialId;
        this.appLovinInterstitialId = builder.appLovinInterstitialId;
        this.ironSourceInterstitialId = builder.ironSourceInterstitialId;
        this.wortiseInterstitialId = builder.wortiseInterstitialId;

        this.adMobRewardedId = builder.adMobRewardedId;
        this.metaRewardedId = builder.metaRewardedId;
        this.unityRewardedId = builder.unityRewardedId;
        this.appLovinRewardedId = builder.appLovinRewardedId;
        this.ironSourceRewardedId = builder.ironSourceRewardedId;
        this.wortiseRewardedId = builder.wortiseRewardedId;

        this.adMobRewardedIntId = builder.adMobRewardedIntId;
        this.appLovinRewardedIntId = builder.appLovinRewardedIntId;
        this.unityRewardedIntId = builder.unityRewardedIntId;
        this.ironSourceRewardedIntId = builder.ironSourceRewardedIntId;
        this.wortiseRewardedIntId = builder.wortiseRewardedIntId;

        this.adMobAppOpenId = builder.adMobAppOpenId;
        this.metaAppOpenId = builder.metaAppOpenId;
        this.appLovinAppOpenId = builder.appLovinAppOpenId;
        this.startappAppOpenId = builder.startappAppOpenId;
        this.ironSourceAppOpenId = builder.ironSourceAppOpenId;
        this.wortiseAppOpenId = builder.wortiseAppOpenId;

        this.adMobNativeId = builder.adMobNativeId;
        this.metaNativeId = builder.metaNativeId;
        this.appLovinNativeId = builder.appLovinNativeId;
        this.ironSourceNativeId = builder.ironSourceNativeId;
        this.wortiseNativeId = builder.wortiseNativeId;

        this.houseAdBannerImage = builder.houseAdBannerImage;
        this.houseAdBannerClickUrl = builder.houseAdBannerClickUrl;
        this.houseAdInterstitialImage = builder.houseAdInterstitialImage;
        this.houseAdInterstitialClickUrl = builder.houseAdInterstitialClickUrl;
        this.houseAdNativeTitle = builder.houseAdNativeTitle;
        this.houseAdNativeDescription = builder.houseAdNativeDescription;
        this.houseAdNativeImage = builder.houseAdNativeImage;
        this.houseAdNativeIcon = builder.houseAdNativeIcon;
        this.houseAdNativeCTA = builder.houseAdNativeCTA;
        this.houseAdNativeClickUrl = builder.houseAdNativeClickUrl;
    }

    public boolean isValid() {
        return isValid;
    }

    protected void setValid(boolean valid) {
        isValid = valid;
    }

    public Builder toBuilder() {
        Builder builder = new Builder()
            .enableAds(this.adStatus)
            .primaryNetwork(this.primaryNetwork)
            .testMode(this.testMode)
            .debug(this.debug)
            .autoLoad(this.autoLoad)
            .houseAdEnabled(this.enableHouseAd)
            .interstitialInterval(this.interstitialInterval)
            .rewardedInterval(this.rewardedInterval)
            .appOpenCooldown(this.appOpenCooldownMinutes)
            .bannerEnabled(this.bannerStatus)
            .interstitialEnabled(this.interstitialStatus)
            .nativeEnabled(this.nativeStatus)
            .rewardedEnabled(this.rewardedStatus)
            .appOpenEnabled(this.appOpenStatus)
            .rewardedInterstitialEnabled(this.rewardedInterstitialStatus)
            .enableGDPR(this.enableGDPR)
            .debugGDPR(this.debugGDPR)
            .enableDebugHUD(this.enableDebugHUD)
            .adResponseTimeout(this.adResponseTimeoutMs)
            .adMobAppId(this.adMobAppId)
            .startAppId(this.startappAppId)
            .unityGameId(this.unityGameId)
            .appLovinSdkKey(this.appLovinSdkKey)
            .ironSourceAppKey(this.ironSourceAppKey)
            .wortiseAppId(this.wortiseAppId)
            .adMobBannerId(this.adMobBannerId)
            .metaBannerId(this.metaBannerId)
            .unityBannerId(this.unityBannerId)
            .appLovinBannerId(this.appLovinBannerId)
            .ironSourceBannerId(this.ironSourceBannerId)
            .wortiseBannerId(this.wortiseBannerId)
            .adMobInterstitialId(this.adMobInterstitialId)
            .metaInterstitialId(this.metaInterstitialId)
            .unityInterstitialId(this.unityInterstitialId)
            .appLovinInterstitialId(this.appLovinInterstitialId)
            .ironSourceInterstitialId(this.ironSourceInterstitialId)
            .wortiseInterstitialId(this.wortiseInterstitialId)
            .adMobRewardedId(this.adMobRewardedId)
            .metaRewardedId(this.metaRewardedId)
            .unityRewardedId(this.unityRewardedId)
            .appLovinRewardedId(this.appLovinRewardedId)
            .ironSourceRewardedId(this.ironSourceRewardedId)
            .wortiseRewardedId(this.wortiseRewardedId)
            .adMobRewardedIntId(this.adMobRewardedIntId)
            .appLovinRewardedIntId(this.appLovinRewardedIntId)
            .unityRewardedIntId(this.unityRewardedIntId)
            .ironSourceRewardedIntId(this.ironSourceRewardedIntId)
            .wortiseRewardedIntId(this.wortiseRewardedIntId)
            .adMobAppOpenId(this.adMobAppOpenId)
            .metaAppOpenId(this.metaAppOpenId)
            .appLovinAppOpenId(this.appLovinAppOpenId)
            .startAppAppOpenId(this.startappAppOpenId)
            .ironSourceAppOpenId(this.ironSourceAppOpenId)
            .wortiseAppOpenId(this.wortiseAppOpenId)
            .adMobNativeId(this.adMobNativeId)
            .metaNativeId(this.metaNativeId)
            .appLovinNativeId(this.appLovinNativeId)
            .ironSourceNativeId(this.ironSourceNativeId)
            .wortiseNativeId(this.wortiseNativeId)
            .houseAdBannerImage(this.houseAdBannerImage)
            .houseAdBannerClickUrl(this.houseAdBannerClickUrl)
            .houseAdInterstitialImage(this.houseAdInterstitialImage)
            .houseAdInterstitialClickUrl(this.houseAdInterstitialClickUrl)
            .houseAdNativeTitle(this.houseAdNativeTitle)
            .houseAdNativeDescription(this.houseAdNativeDescription)
            .houseAdNativeImage(this.houseAdNativeImage)
            .houseAdNativeIcon(this.houseAdNativeIcon)
            .houseAdNativeCTA(this.houseAdNativeCTA)
            .houseAdNativeClickUrl(this.houseAdNativeClickUrl);

        if (this.backupNetworks != null) {
            builder.backupNetworks(this.backupNetworks.toArray(new String[0]));
        }
        
        return builder;
    }

    // Getters for Global Settings
    public boolean getAdStatus() {
        return adStatus;
    }

    /**
     * @return The primary ad network as a string. Never null.
     */
    @NonNull
    public String getPrimaryNetwork() {
        return primaryNetwork != null ? primaryNetwork : "";
    }

    /**
     * @return The list of backup ad networks. Never null.
     */
    @NonNull
    public List<String> getBackupNetworks() {
        return backupNetworks != null ? backupNetworks : new java.util.ArrayList<>();
    }

    public boolean isTestMode() {
        return testMode;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isAutoLoadEnabled() {
        return autoLoad;
    }

    public boolean isAppOpenEnabled() {
        return appOpenStatus;
    }

    public boolean isHouseAdEnabled() {
        return enableHouseAd;
    }

    public int getInterstitialInterval() {
        return interstitialInterval;
    }

    public int getRewardedInterval() {
        return rewardedInterval;
    }

    public List<String> getOpenAdExcludedActivities() {
        return openAdExcludedActivities;
    }

    public int getAppOpenCooldownMinutes() {
        return appOpenCooldownMinutes;
    }

    public boolean isEnableGDPR() {
        return enableGDPR;
    }

    public boolean isDebugGDPR() {
        return debugGDPR;
    }

    public boolean isEnableDebugHUD() {
        return enableDebugHUD;
    }
    
    public int getAdResponseTimeoutMs() {
        return adResponseTimeoutMs;
    }


    public boolean isBannerEnabled() {
        return bannerStatus;
    }

    public boolean isInterstitialEnabled() {
        return interstitialStatus;
    }

    public boolean isNativeEnabled() {
        return nativeStatus;
    }

    public boolean isRewardedEnabled() {
        return rewardedStatus;
    }


    public boolean isRewardedInterstitialEnabled() {
        return rewardedInterstitialStatus;
    }

    // Getters for App IDs
    /**
     * @return The AdMob App ID. Never null.
     */
    @NonNull
    public String getAdMobAppId() {
        return adMobAppId != null ? adMobAppId : "";
    }

    /**
     * @return The Startapp App ID. Never null.
     */
    @NonNull
    public String getStartAppId() {
        return startappAppId != null ? startappAppId : "";
    }

    public String getUnityGameId() {
        return unityGameId;
    }

    public String getAppLovinSdkKey() {
        return appLovinSdkKey;
    }

    public String getIronSourceAppKey() {
        return ironSourceAppKey;
    }

    public String getWortiseAppId() {
        return wortiseAppId;
    }

    // Getters for Banner IDs
    /**
     * @return The AdMob Banner unit ID. Never null.
     */
    @NonNull
    public String getAdMobBannerId() {
        return adMobBannerId != null ? adMobBannerId : "";
    }

    public String getMetaBannerId() {
        return metaBannerId;
    }

    public String getUnityBannerId() {
        return unityBannerId;
    }

    public String getAppLovinBannerId() {
        return appLovinBannerId;
    }

    public String getIronSourceBannerId() {
        return ironSourceBannerId;
    }

    public String getWortiseBannerId() {
        return wortiseBannerId;
    }

    // Getters for Interstitial IDs
    /**
     * @return The AdMob Interstitial unit ID. Never null.
     */
    @NonNull
    public String getAdMobInterstitialId() {
        return adMobInterstitialId != null ? adMobInterstitialId : "";
    }

    public String getMetaInterstitialId() {
        return metaInterstitialId;
    }

    public String getUnityInterstitialId() {
        return unityInterstitialId;
    }

    public String getAppLovinInterstitialId() {
        return appLovinInterstitialId;
    }

    public String getIronSourceInterstitialId() {
        return ironSourceInterstitialId;
    }

    public String getWortiseInterstitialId() {
        return wortiseInterstitialId;
    }

    // Getters for Rewarded IDs
    public String getAdMobRewardedId() {
        return adMobRewardedId;
    }

    public String getMetaRewardedId() {
        return metaRewardedId;
    }

    public String getUnityRewardedId() {
        return unityRewardedId;
    }

    public String getAppLovinRewardedId() {
        return appLovinRewardedId;
    }

    public String getIronSourceRewardedId() {
        return ironSourceRewardedId;
    }

    public String getWortiseRewardedId() {
        return wortiseRewardedId;
    }

    // Getters for Rewarded Interstitial IDs
    public String getAdMobRewardedIntId() {
        return adMobRewardedIntId;
    }

    public String getAppLovinRewardedIntId() {
        return appLovinRewardedIntId;
    }

    public String getUnityRewardedIntId() {
        return unityRewardedIntId;
    }

    public String getIronSourceRewardedIntId() {
        return ironSourceRewardedIntId;
    }

    public String getWortiseRewardedIntId() {
        return wortiseRewardedIntId;
    }

    // Getters for App Open IDs
    public String getAdMobAppOpenId() {
        return adMobAppOpenId;
    }

    public String getMetaAppOpenId() {
        return metaAppOpenId;
    }

    public String getAppLovinAppOpenId() {
        return appLovinAppOpenId;
    }

    public String getStartAppAppOpenId() {
        return startappAppOpenId;
    }

    public String getIronSourceAppOpenId() {
        return ironSourceAppOpenId;
    }

    public String getWortiseAppOpenId() {
        return wortiseAppOpenId;
    }

    // Getters for Native IDs
    public String getAdMobNativeId() {
        return adMobNativeId;
    }

    public String getMetaNativeId() {
        return metaNativeId;
    }

    public String getAppLovinNativeId() {
        return appLovinNativeId;
    }

    public String getIronSourceNativeId() {
        return ironSourceNativeId;
    }

    public String getWortiseNativeId() {
        return wortiseNativeId;
    }

    // Getters for House Ad
    public String getHouseAdBannerImage() {
        return houseAdBannerImage;
    }

    public String getHouseAdBannerClickUrl() {
        return houseAdBannerClickUrl;
    }

    public String getHouseAdInterstitialImage() {
        return houseAdInterstitialImage;
    }

    public String getHouseAdInterstitialClickUrl() {
        return houseAdInterstitialClickUrl;
    }

    public String getHouseAdNativeTitle() {
        return houseAdNativeTitle;
    }

    public String getHouseAdNativeDescription() {
        return houseAdNativeDescription;
    }

    public String getHouseAdNativeImage() {
        return houseAdNativeImage;
    }

    public String getHouseAdNativeIcon() {
        return houseAdNativeIcon;
    }

    public String getHouseAdNativeCTA() {
        return houseAdNativeCTA;
    }

    public String getHouseAdNativeClickUrl() {
        return houseAdNativeClickUrl;
    }

    public String resolveAdUnitId(com.partharoypc.adglide.util.AdFormat format, String network) {
        if (network == null) return "0";
        return switch (format) {
            case INTERSTITIAL -> switch (network) {
                case com.partharoypc.adglide.util.Constant.ADMOB, com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB -> getAdMobInterstitialId();
                case com.partharoypc.adglide.util.Constant.META -> getMetaInterstitialId();
                case com.partharoypc.adglide.util.Constant.UNITY -> getUnityInterstitialId();
                case com.partharoypc.adglide.util.Constant.APPLOVIN, com.partharoypc.adglide.util.Constant.APPLOVIN_MAX, com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX -> getAppLovinInterstitialId();
                case com.partharoypc.adglide.util.Constant.IRONSOURCE, com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE -> getIronSourceInterstitialId();
                case com.partharoypc.adglide.util.Constant.STARTAPP -> !getStartAppId().isEmpty() ? getStartAppId() : "startapp_id";
                case com.partharoypc.adglide.util.Constant.WORTISE -> getWortiseInterstitialId();
                case com.partharoypc.adglide.util.Constant.HOUSE_AD -> "house_ad";
                default -> "0";
            };
            case BANNER -> switch (network) {
                case com.partharoypc.adglide.util.Constant.ADMOB, com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB -> getAdMobBannerId();
                case com.partharoypc.adglide.util.Constant.META -> getMetaBannerId();
                case com.partharoypc.adglide.util.Constant.UNITY -> getUnityBannerId();
                case com.partharoypc.adglide.util.Constant.APPLOVIN, com.partharoypc.adglide.util.Constant.APPLOVIN_MAX, com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX -> getAppLovinBannerId();
                case com.partharoypc.adglide.util.Constant.IRONSOURCE, com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE -> getIronSourceBannerId();
                case com.partharoypc.adglide.util.Constant.STARTAPP -> !getStartAppId().isEmpty() ? getStartAppId() : "startapp_id";
                case com.partharoypc.adglide.util.Constant.WORTISE -> getWortiseBannerId();
                case com.partharoypc.adglide.util.Constant.HOUSE_AD -> "house_ad";
                default -> "0";
            };
            case REWARDED -> switch (network) {
                case com.partharoypc.adglide.util.Constant.ADMOB, com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB -> getAdMobRewardedId();
                case com.partharoypc.adglide.util.Constant.META -> getMetaRewardedId();
                case com.partharoypc.adglide.util.Constant.UNITY -> getUnityRewardedId();
                case com.partharoypc.adglide.util.Constant.APPLOVIN, com.partharoypc.adglide.util.Constant.APPLOVIN_MAX, com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX -> getAppLovinRewardedId();
                case com.partharoypc.adglide.util.Constant.IRONSOURCE, com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE -> getIronSourceRewardedId();
                case com.partharoypc.adglide.util.Constant.STARTAPP -> !getStartAppId().isEmpty() ? getStartAppId() : "startapp_id";
                case com.partharoypc.adglide.util.Constant.WORTISE -> getWortiseRewardedId();
                case com.partharoypc.adglide.util.Constant.HOUSE_AD -> "house_ad";
                default -> "0";
            };
            case REWARDED_INTERSTITIAL -> switch (network) {
                case com.partharoypc.adglide.util.Constant.ADMOB, com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB -> getAdMobRewardedIntId();
                case com.partharoypc.adglide.util.Constant.APPLOVIN, com.partharoypc.adglide.util.Constant.APPLOVIN_MAX, com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX -> getAppLovinRewardedIntId();
                case com.partharoypc.adglide.util.Constant.UNITY -> getUnityRewardedIntId();
                case com.partharoypc.adglide.util.Constant.IRONSOURCE, com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE -> getIronSourceRewardedIntId();
                case com.partharoypc.adglide.util.Constant.WORTISE -> getWortiseRewardedIntId();
                default -> "0";
            };
            case NATIVE -> switch (network) {
                case com.partharoypc.adglide.util.Constant.ADMOB, com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB -> getAdMobNativeId();
                case com.partharoypc.adglide.util.Constant.META -> getMetaNativeId();
                case com.partharoypc.adglide.util.Constant.APPLOVIN, com.partharoypc.adglide.util.Constant.APPLOVIN_MAX, com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX -> getAppLovinNativeId();
                case com.partharoypc.adglide.util.Constant.WORTISE -> getWortiseNativeId();
                case com.partharoypc.adglide.util.Constant.STARTAPP -> !getStartAppId().isEmpty() ? getStartAppId() : "startapp_id";
                case com.partharoypc.adglide.util.Constant.IRONSOURCE, com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE -> getIronSourceNativeId();
                case com.partharoypc.adglide.util.Constant.HOUSE_AD -> "house_ad";
                default -> "0";
            };
            case APP_OPEN -> switch (network) {
                case com.partharoypc.adglide.util.Constant.ADMOB, com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB -> getAdMobAppOpenId();
                case com.partharoypc.adglide.util.Constant.META -> getMetaAppOpenId();
                case com.partharoypc.adglide.util.Constant.APPLOVIN, com.partharoypc.adglide.util.Constant.APPLOVIN_MAX, com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX -> getAppLovinAppOpenId();
                case com.partharoypc.adglide.util.Constant.STARTAPP -> !getStartAppId().isEmpty() ? getStartAppId() : "startapp_id";
                case com.partharoypc.adglide.util.Constant.IRONSOURCE, com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE -> getIronSourceAppOpenId();
                case com.partharoypc.adglide.util.Constant.WORTISE -> getWortiseAppOpenId();
                default -> "0";
            };
            default -> "0";
        };
    }

    public static class Builder {
        private boolean adStatus = false;
        private String primaryNetwork = "";
        private final List<String> backupNetworks = new ArrayList<>();
        private boolean testMode = false;
        private boolean debug = true;
        private boolean autoLoad = true;
        private boolean bannerStatus = false;
        private boolean interstitialStatus = false;
        private boolean nativeStatus = false;
        private boolean rewardedStatus = false;
        private boolean appOpenStatus = false;
        private boolean rewardedInterstitialStatus = false;
        private boolean enableHouseAd = false;
        private int interstitialInterval = 0;
        private int rewardedInterval = 0;
        private final List<String> openAdExcludedActivities = new ArrayList<>();
        private boolean enableGDPR = false;
        private boolean debugGDPR = false;
        private boolean enableDebugHUD = false;
        private int adResponseTimeoutMs = 8000; // Increased to 8 seconds for better match rate with AdMob/Meta
        private int appOpenCooldownMinutes = 30;

        private String adMobAppId = "";
        private String startappAppId = "";
        private String unityGameId = "";
        private String appLovinSdkKey = "";
        private String ironSourceAppKey = "";
        private String wortiseAppId = "";

        private String adMobBannerId = "";
        private String metaBannerId = "";
        private String unityBannerId = "";
        private String appLovinBannerId = "";
        private String ironSourceBannerId = "";
        private String wortiseBannerId = "";

        private String adMobInterstitialId = "";
        private String metaInterstitialId = "";
        private String unityInterstitialId = "";
        private String appLovinInterstitialId = "";
        private String ironSourceInterstitialId = "";
        private String wortiseInterstitialId = "";

        private String adMobRewardedId = "";
        private String metaRewardedId = "";
        private String unityRewardedId = "";
        private String appLovinRewardedId = "";
        private String ironSourceRewardedId = "";
        private String wortiseRewardedId = "";

        private String adMobRewardedIntId = "";
        private String appLovinRewardedIntId = "";
        private String unityRewardedIntId = "";
        private String ironSourceRewardedIntId = "";
        private String wortiseRewardedIntId = "";

        private String adMobAppOpenId = "";
        private String metaAppOpenId = "";
        private String appLovinAppOpenId = "";
        private String startappAppOpenId = "";
        private String ironSourceAppOpenId = "";
        private String wortiseAppOpenId = "";

        private String adMobNativeId = "";
        private String metaNativeId = "";
        private String appLovinNativeId = "";
        private String ironSourceNativeId = "";
        private String wortiseNativeId = "";

        private String houseAdBannerImage = "";
        private String houseAdBannerClickUrl = "";
        private String houseAdInterstitialImage = "";
        private String houseAdInterstitialClickUrl = "";
        private String houseAdNativeTitle = "";
        private String houseAdNativeDescription = "";
        private String houseAdNativeImage = "";
        private String houseAdNativeIcon = "";
        private String houseAdNativeCTA = "";
        private String houseAdNativeClickUrl = "";

        public Builder() {
        }

        public Builder enableAds(boolean enable) {
            this.adStatus = enable;
            return this;
        }

        public Builder primaryNetwork(AdGlideNetwork network) {
            this.primaryNetwork = network.getValue();
            return this;
        }

        public Builder primaryNetwork(String network) {
            this.primaryNetwork = network;
            return this;
        }


        public Builder backupNetworks(AdGlideNetwork... networks) {
            this.backupNetworks.clear();
            for (AdGlideNetwork n : networks) {
                if (n != null)
                    this.backupNetworks.add(n.getValue());
            }
            if (!this.backupNetworks.isEmpty()) {
                // Done
            }
            return this;
        }

        public Builder backupNetworks(String... networks) {
            this.backupNetworks.clear();
            this.backupNetworks.addAll(Arrays.asList(networks));
            if (!this.backupNetworks.isEmpty()) {
                // Done
            }
            return this;
        }

        public Builder testMode(boolean testMode) {
            this.testMode = testMode;
            if (testMode) {
                this.debug = true;
                this.debugGDPR = true;
                this.enableDebugHUD = true;
            } else {
                this.debug = false;
                this.debugGDPR = false;
                this.enableDebugHUD = false;
            }
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder autoLoad(boolean enable) {
            this.autoLoad = enable;
            return this;
        }

        public Builder appOpenEnabled(boolean enable) {
            this.appOpenStatus = enable;
            return this;
        }

        public Builder houseAdEnabled(boolean enable) {
            this.enableHouseAd = enable;
            return this;
        }

        public Builder interstitialInterval(int interval) {
            this.interstitialInterval = interval;
            return this;
        }

        public Builder rewardedInterval(int interval) {
            this.rewardedInterval = interval;
            return this;
        }

        public Builder appOpenCooldown(int minutes) {
            this.appOpenCooldownMinutes = minutes;
            return this;
        }

        public Builder bannerEnabled(boolean enable) {
            this.bannerStatus = enable;
            return this;
        }

        public Builder interstitialEnabled(boolean enable) {
            this.interstitialStatus = enable;
            return this;
        }

        public Builder nativeEnabled(boolean enable) {
            this.nativeStatus = enable;
            return this;
        }

        public Builder rewardedEnabled(boolean enable) {
            this.rewardedStatus = enable;
            return this;
        }

        public Builder rewardedInterstitialEnabled(boolean enable) {
            this.rewardedInterstitialStatus = enable;
            return this;
        }

        public Builder excludeOpenAdFrom(Class<?>... activities) {
            for (Class<?> activity : activities) {
                this.openAdExcludedActivities.add(activity.getName());
            }
            return this;
        }

        public Builder enableGDPR(boolean enable) {
            this.enableGDPR = enable;
            return this;
        }

        public Builder debugGDPR(boolean debug) {
            this.debugGDPR = debug;
            return this;
        }

        public Builder enableDebugHUD(boolean enable) {
            this.enableDebugHUD = enable;
            return this;
        }

        public Builder adResponseTimeout(int timeoutMs) {
            this.adResponseTimeoutMs = timeoutMs;
            return this;
        }


        public Builder adMobAppId(String id) {
            this.adMobAppId = id;
            return this;
        }

        public Builder startAppId(String id) {
            this.startappAppId = id;
            return this;
        }

        public Builder unityGameId(String id) {
            this.unityGameId = id;
            return this;
        }

        public Builder appLovinSdkKey(String key) {
            this.appLovinSdkKey = key;
            return this;
        }

        public Builder ironSourceAppKey(String key) {
            this.ironSourceAppKey = key;
            return this;
        }

        public Builder wortiseAppId(String id) {
            this.wortiseAppId = id;
            return this;
        }

        public Builder adMobBannerId(String id) {
            this.adMobBannerId = id;
            return this;
        }

        public Builder metaBannerId(String id) {
            this.metaBannerId = id;
            return this;
        }

        public Builder unityBannerId(String id) {
            this.unityBannerId = id;
            return this;
        }

        public Builder appLovinBannerId(String id) {
            this.appLovinBannerId = id;
            return this;
        }

        public Builder ironSourceBannerId(String id) {
            this.ironSourceBannerId = id;
            return this;
        }

        public Builder wortiseBannerId(String id) {
            this.wortiseBannerId = id;
            return this;
        }

        public Builder adMobInterstitialId(String id) {
            this.adMobInterstitialId = id;
            return this;
        }

        public Builder metaInterstitialId(String id) {
            this.metaInterstitialId = id;
            return this;
        }

        public Builder unityInterstitialId(String id) {
            this.unityInterstitialId = id;
            return this;
        }

        public Builder appLovinInterstitialId(String id) {
            this.appLovinInterstitialId = id;
            return this;
        }

        public Builder ironSourceInterstitialId(String id) {
            this.ironSourceInterstitialId = id;
            return this;
        }

        public Builder wortiseInterstitialId(String id) {
            this.wortiseInterstitialId = id;
            return this;
        }

        public Builder adMobRewardedId(String id) {
            this.adMobRewardedId = id;
            return this;
        }

        public Builder metaRewardedId(String id) {
            this.metaRewardedId = id;
            return this;
        }

        public Builder unityRewardedId(String id) {
            this.unityRewardedId = id;
            return this;
        }

        public Builder appLovinRewardedId(String id) {
            this.appLovinRewardedId = id;
            return this;
        }

        public Builder ironSourceRewardedId(String id) {
            this.ironSourceRewardedId = id;
            return this;
        }

        public Builder wortiseRewardedId(String id) {
            this.wortiseRewardedId = id;
            return this;
        }

        public Builder adMobRewardedIntId(String id) {
            this.adMobRewardedIntId = id;
            return this;
        }

        public Builder appLovinRewardedIntId(String id) {
            this.appLovinRewardedIntId = id;
            return this;
        }

        public Builder unityRewardedIntId(String id) {
            this.unityRewardedIntId = id;
            return this;
        }

        public Builder ironSourceRewardedIntId(String id) {
            this.ironSourceRewardedIntId = id;
            return this;
        }

        public Builder wortiseRewardedIntId(String id) {
            this.wortiseRewardedIntId = id;
            return this;
        }

        public Builder adMobAppOpenId(String id) {
            this.adMobAppOpenId = id;
            return this;
        }

        public Builder metaAppOpenId(String id) {
            this.metaAppOpenId = id;
            return this;
        }

        public Builder appLovinAppOpenId(String id) {
            this.appLovinAppOpenId = id;
            return this;
        }

        public Builder startAppAppOpenId(String id) {
            this.startappAppOpenId = id;
            return this;
        }

        public Builder ironSourceAppOpenId(String id) {
            this.ironSourceAppOpenId = id;
            return this;
        }

        public Builder houseAdNativeTitle(String title) {
            this.houseAdNativeTitle = title;
            return this;
        }

        public Builder houseAdNativeDescription(String description) {
            this.houseAdNativeDescription = description;
            return this;
        }

        public Builder houseAdNativeImage(String imageUrl) {
            this.houseAdNativeImage = imageUrl;
            return this;
        }

        public Builder houseAdNativeIcon(String iconUrl) {
            this.houseAdNativeIcon = iconUrl;
            return this;
        }

        public Builder houseAdNativeCTA(String ctaText) {
            this.houseAdNativeCTA = ctaText;
            return this;
        }

        public Builder houseAdNativeClickUrl(String clickUrl) {
            this.houseAdNativeClickUrl = clickUrl;
            return this;
        }

        public Builder wortiseAppOpenId(String id) {
            this.wortiseAppOpenId = id;
            return this;
        }

        public Builder adMobNativeId(String id) {
            this.adMobNativeId = id;
            return this;
        }

        public Builder metaNativeId(String id) {
            this.metaNativeId = id;
            return this;
        }

        public Builder appLovinNativeId(String id) {
            this.appLovinNativeId = id;
            return this;
        }

        public Builder appLovinDiscNativeZoneId(String id) {
            return this;
        }

        public Builder ironSourceNativeId(String id) {
            this.ironSourceNativeId = id;
            return this;
        }

        public Builder wortiseNativeId(String id) {
            this.wortiseNativeId = id;
            return this;
        }

        public Builder houseAdBannerImage(String imageUrl) {
            this.houseAdBannerImage = imageUrl;
            this.enableHouseAd = true;
            return this;
        }

        public Builder houseAdBannerClickUrl(String clickUrl) {
            this.houseAdBannerClickUrl = clickUrl;
            return this;
        }

        public Builder houseAdInterstitialImage(String imageUrl) {
            this.houseAdInterstitialImage = imageUrl;
            this.enableHouseAd = true;
            return this;
        }

        public Builder houseAdInterstitialClickUrl(String clickUrl) {
            this.houseAdInterstitialClickUrl = clickUrl;
            return this;
        }

        /**
         * Finalizes the configuration and returns an AdGlideConfig instance.
         * 
         * @return A validated AdGlideConfig instance.
         */
        public AdGlideConfig build() {
            AdGlideConfig config = new AdGlideConfig(this);
            validate(config);
            return config;
        }

        private void validate(AdGlideConfig config) {
            if (!config.getAdStatus()) return;
            
            String primary = config.getPrimaryNetwork();
            if (primary == null || primary.isEmpty()) {
                AdGlideLog.w("Config", "Primary network is not set. Ads may not load correctly.");
            }
            
            checkNetwork(config, "admob", config.getAdMobAppId(), "App ID");
            checkNetwork(config, "startapp", config.getStartAppId(), "App ID");
            checkNetwork(config, "unity", config.getUnityGameId(), "Game ID");
            checkNetwork(config, "applovin", config.getAppLovinSdkKey(), "SDK Key");
            checkNetwork(config, "ironsource", config.getIronSourceAppKey(), "App Key");
            checkNetwork(config, "wortise", config.getWortiseAppId(), "App ID");
            
            if (config.isValid()) {
                AdGlideLog.i("Config", "SDK Configuration validated successfully.");
            } else {
                AdGlideLog.e("Config", "SDK Configuration validation failed. Check your setup for missing keys/IDs.");
            }
        }

        private void checkNetwork(AdGlideConfig config, String name, String value, String type) {
            String primary = config.getPrimaryNetwork();
            List<String> backups = config.getBackupNetworks();
            
            boolean isUsed = (primary != null && primary.toLowerCase(java.util.Locale.ROOT).contains(name));
            if (!isUsed && backups != null) {
                for (String n : backups) {
                    if (n.toLowerCase(java.util.Locale.ROOT).contains(name)) {
                        isUsed = true;
                        break;
                    }
                }
            }

            if (isUsed && (value == null || value.trim().isEmpty())) {
                AdGlideLog.e("Config", name.toUpperCase(java.util.Locale.ROOT) + " is enabled but " + type + " is missing!");
                config.setValid(false);
            }
        }
    }
}
