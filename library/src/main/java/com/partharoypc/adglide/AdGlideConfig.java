package com.partharoypc.adglide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdGlideConfig {

    // Global Settings
    private final boolean adStatus;
    private final String primaryNetwork;
    private final String backupNetwork;
    private final List<String> backupNetworks;
    private final boolean testMode;
    private final boolean debug;
    private final boolean legacyGDPR;
    private final boolean autoLoadInterstitial;
    private final boolean autoLoadRewarded;
    private final boolean enableAppOpenAd;
    private final boolean enableHouseAd;
    private final int interstitialInterval;
    private final int rewardedInterval;
    private final List<String> openAdExcludedActivities;
    private final boolean enableGDPR;
    private final boolean debugGDPR;
    private final boolean enableDebugHUD;

    // Granular Ad Type Status
    private final boolean bannerStatus;
    private final boolean interstitialStatus;
    private final boolean nativeStatus;
    private final boolean rewardedStatus;
    private final boolean appOpenStatus;

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
    private final String appLovinDiscRewardedZoneId;
    private final String ironSourceRewardedId;
    private final String wortiseRewardedId;

    // Rewarded Interstitial
    private final String adMobRewardedIntId;
    private final String appLovinRewardedIntId;
    private final String wortiseRewardedIntId;

    // App Open
    private final String adMobAppOpenId;
    private final String metaAppOpenId;
    private final String appLovinAppOpenId;
    private final String wortiseAppOpenId;

    // Native
    private final String adMobNativeId;
    private final String metaNativeId;
    private final String appLovinNativeId;
    private final String appLovinDiscNativeZoneId;
    private final String ironSourceNativeId;
    private final String wortiseNativeId;

    // House Ad
    private final String houseAdBannerImage;
    private final String houseAdBannerClickUrl;
    private final String houseAdInterstitialImage;
    private final String houseAdInterstitialClickUrl;

    private final com.partharoypc.adglide.util.OnPaidEventListener onPaidEventListener;

    private AdGlideConfig(Builder builder) {
        this.adStatus = builder.adStatus;
        this.primaryNetwork = builder.primaryNetwork;
        this.backupNetwork = builder.backupNetwork;
        this.backupNetworks = builder.backupNetworks;
        this.testMode = builder.testMode;
        this.debug = builder.debug;
        this.legacyGDPR = builder.legacyGDPR;
        this.autoLoadInterstitial = builder.autoLoadInterstitial;
        this.autoLoadRewarded = builder.autoLoadRewarded;
        this.enableAppOpenAd = builder.enableAppOpenAd;
        this.enableHouseAd = builder.enableHouseAd;
        this.interstitialInterval = builder.interstitialInterval;
        this.rewardedInterval = builder.rewardedInterval;
        this.openAdExcludedActivities = builder.openAdExcludedActivities;
        this.enableGDPR = builder.enableGDPR;
        this.debugGDPR = builder.debugGDPR;
        this.enableDebugHUD = builder.enableDebugHUD;

        this.bannerStatus = builder.bannerStatus;
        this.interstitialStatus = builder.interstitialStatus;
        this.nativeStatus = builder.nativeStatus;
        this.rewardedStatus = builder.rewardedStatus;
        this.appOpenStatus = builder.appOpenStatus;

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
        this.appLovinDiscRewardedZoneId = builder.appLovinDiscRewardedZoneId;
        this.ironSourceRewardedId = builder.ironSourceRewardedId;
        this.wortiseRewardedId = builder.wortiseRewardedId;

        this.adMobRewardedIntId = builder.adMobRewardedIntId;
        this.appLovinRewardedIntId = builder.appLovinRewardedIntId;
        this.wortiseRewardedIntId = builder.wortiseRewardedIntId;

        this.adMobAppOpenId = builder.adMobAppOpenId;
        this.metaAppOpenId = builder.metaAppOpenId;
        this.appLovinAppOpenId = builder.appLovinAppOpenId;
        this.wortiseAppOpenId = builder.wortiseAppOpenId;

        this.adMobNativeId = builder.adMobNativeId;
        this.metaNativeId = builder.metaNativeId;
        this.appLovinNativeId = builder.appLovinNativeId;
        this.appLovinDiscNativeZoneId = builder.appLovinDiscNativeZoneId;
        this.ironSourceNativeId = builder.ironSourceNativeId;
        this.wortiseNativeId = builder.wortiseNativeId;

        this.houseAdBannerImage = builder.houseAdBannerImage;
        this.houseAdBannerClickUrl = builder.houseAdBannerClickUrl;
        this.houseAdInterstitialImage = builder.houseAdInterstitialImage;
        this.houseAdInterstitialClickUrl = builder.houseAdInterstitialClickUrl;

        this.onPaidEventListener = builder.onPaidEventListener;
    }

    // Getters for Global Settings
    public boolean getAdStatus() {
        return adStatus;
    }

    public String getPrimaryNetwork() {
        return primaryNetwork;
    }

    public String getBackupNetwork() {
        return backupNetwork;
    }

    public List<String> getBackupNetworks() {
        return backupNetworks;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isLegacyGDPR() {
        return legacyGDPR;
    }

    public boolean isAutoLoadInterstitial() {
        return autoLoadInterstitial;
    }

    public boolean isAutoLoadRewarded() {
        return autoLoadRewarded;
    }

    public boolean isAppOpenAdEnabled() {
        return enableAppOpenAd;
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

    public com.partharoypc.adglide.util.OnPaidEventListener getOnPaidEventListener() {
        return onPaidEventListener;
    }

    public List<String> getOpenAdExcludedActivities() {
        return openAdExcludedActivities;
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

    public boolean isAppOpenEnabled() {
        return appOpenStatus;
    }

    // Getters for App IDs
    public String getAdMobAppId() {
        return adMobAppId;
    }

    public String getStartAppId() {
        return startappAppId;
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
    public String getAdMobBannerId() {
        return adMobBannerId;
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
    public String getAdMobInterstitialId() {
        return adMobInterstitialId;
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

    public String getAppLovinDiscRewardedZoneId() {
        return appLovinDiscRewardedZoneId;
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

    public String getAppLovinDiscNativeZoneId() {
        return appLovinDiscNativeZoneId;
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

    public static class Builder {
        private boolean adStatus = false;
        private String primaryNetwork = "";
        private String backupNetwork = "";
        private final List<String> backupNetworks = new ArrayList<>();
        private boolean testMode = false;
        private boolean debug = true;
        private boolean legacyGDPR = false;
        private boolean autoLoadInterstitial = false;
        private boolean autoLoadRewarded = false;
        private boolean enableAppOpenAd = false;
        private boolean enableHouseAd = false;
        private int interstitialInterval = 1;
        private int rewardedInterval = 1;
        private final List<String> openAdExcludedActivities = new ArrayList<>();
        private boolean enableGDPR = false;
        private boolean debugGDPR = false;
        private boolean enableDebugHUD = false;

        private boolean bannerStatus = false;
        private boolean interstitialStatus = false;
        private boolean nativeStatus = false;
        private boolean rewardedStatus = false;
        private boolean appOpenStatus = false;

        private String adMobAppId = "";
        private String startappAppId = "0";
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
        private String appLovinDiscRewardedZoneId = "";
        private String ironSourceRewardedId = "";
        private String wortiseRewardedId = "";

        private String adMobRewardedIntId = "";
        private String appLovinRewardedIntId = "";
        private String wortiseRewardedIntId = "";

        private String adMobAppOpenId = "";
        private String metaAppOpenId = "";
        private String appLovinAppOpenId = "";
        private String wortiseAppOpenId = "";

        private String adMobNativeId = "";
        private String metaNativeId = "";
        private String appLovinNativeId = "";
        private String appLovinDiscNativeZoneId = "";
        private String ironSourceNativeId = "";
        private String wortiseNativeId = "";

        private String houseAdBannerImage = "";
        private String houseAdBannerClickUrl = "";
        private String houseAdInterstitialImage = "";
        private String houseAdInterstitialClickUrl = "";

        private com.partharoypc.adglide.util.OnPaidEventListener onPaidEventListener = null;

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

        public Builder onPaidEventListener(com.partharoypc.adglide.util.OnPaidEventListener listener) {
            this.onPaidEventListener = listener;
            return this;
        }

        public Builder backupNetwork(AdGlideNetwork network) {
            this.backupNetwork = network.getValue();
            if (!this.backupNetworks.contains(this.backupNetwork))
                this.backupNetworks.add(this.backupNetwork);
            return this;
        }

        public Builder backupNetwork(String network) {
            this.backupNetwork = network;
            if (!this.backupNetworks.contains(this.backupNetwork))
                this.backupNetworks.add(this.backupNetwork);
            return this;
        }

        public Builder backupNetworks(AdGlideNetwork... networks) {
            this.backupNetworks.clear();
            for (AdGlideNetwork n : networks) {
                if (n != null)
                    this.backupNetworks.add(n.getValue());
            }
            if (!this.backupNetworks.isEmpty())
                this.backupNetwork = this.backupNetworks.get(0);
            return this;
        }

        public Builder backupNetworks(String... networks) {
            this.backupNetworks.clear();
            this.backupNetworks.addAll(Arrays.asList(networks));
            if (!this.backupNetworks.isEmpty())
                this.backupNetwork = this.backupNetworks.get(0);
            return this;
        }

        public Builder testMode(boolean testMode) {
            this.testMode = testMode;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder legacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public Builder autoLoadInterstitial(boolean autoLoadInterstitial) {
            this.autoLoadInterstitial = autoLoadInterstitial;
            return this;
        }

        public Builder autoLoadRewarded(boolean autoLoadRewarded) {
            this.autoLoadRewarded = autoLoadRewarded;
            return this;
        }

        public Builder enableAppOpenAd(boolean enableAppOpenAd) {
            this.enableAppOpenAd = enableAppOpenAd;
            return this;
        }

        public Builder houseAdEnabled(boolean enableHouseAd) {
            this.enableHouseAd = enableHouseAd;
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

        public Builder bannerStatus(boolean enable) {
            this.bannerStatus = enable;
            return this;
        }

        public Builder interstitialStatus(boolean enable) {
            this.interstitialStatus = enable;
            return this;
        }

        public Builder nativeStatus(boolean enable) {
            this.nativeStatus = enable;
            return this;
        }

        public Builder rewardedStatus(boolean enable) {
            this.rewardedStatus = enable;
            return this;
        }

        public Builder appOpenStatus(boolean enable) {
            this.appOpenStatus = enable;
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

        public Builder appLovinDiscRewardedZoneId(String id) {
            this.appLovinDiscRewardedZoneId = id;
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
            this.appLovinDiscNativeZoneId = id;
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

        public AdGlideConfig build() {
            return new AdGlideConfig(this);
        }
    }
}
