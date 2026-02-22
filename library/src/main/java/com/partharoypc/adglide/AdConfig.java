package com.partharoypc.adglide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class AdConfig {

    private static volatile AdConfig instance;

    // --- General ---
    private String adStatus = "1";
    private String adNetwork = "";
    private final List<String> backupAdNetworks = new ArrayList<>();
    private int placementStatus = 1;
    private boolean legacyGDPR = false;
    private boolean debug = true;

    // --- AdMob ---
    private String adMobAppId = "";
    private String adMobBannerId = "";
    private String adMobInterstitialId = "";
    private String adMobRewardedId = "";
    private String adMobRewardedInterstitialId = "";
    private String adMobNativeId = "";
    private String adMobAppOpenId = "";

    // --- Meta ---
    private String metaBannerId = "";
    private String metaInterstitialId = "";
    private String metaRewardedId = "";
    private String metaNativeId = "";

    // --- Unity ---
    private String unityGameId = "";
    private String unityBannerId = "";
    private String unityInterstitialId = "";
    private String unityRewardedId = "";

    // --- AppLovin ---
    private String appLovinSdkKey = "";
    private String appLovinBannerId = "";
    private String appLovinInterstitialId = "";
    private String appLovinRewardedId = "";
    private String appLovinAppOpenId = "";

    // --- IronSource ---
    private String ironSourceAppKey = "";
    private String ironSourceBannerId = "";
    private String ironSourceInterstitialId = "";
    private String ironSourceRewardedId = "";

    // --- StartApp ---
    private String startAppId = "0";

    // --- Wortise ---
    private String wortiseAppId = "";
    private String wortiseBannerId = "";
    private String wortiseInterstitialId = "";
    private String wortiseRewardedId = "";
    private String wortiseNativeId = "";
    private String wortiseAppOpenId = "";

    // --- Interstitial Settings ---
    private int interstitialInterval = 3;

    // --- Banner Settings ---
    private boolean darkTheme = false;
    private boolean collapsibleBanner = false;

    // --- Native Ad Settings ---
    private String nativeAdStyle = "medium";

    private AdConfig() {
    }

    /**
     * Returns the singleton AdConfig instance.
     *
     * @return The global AdConfig.
     */
    @NonNull
    public static AdConfig getInstance() {
        if (instance == null) {
            synchronized (AdConfig.class) {
                if (instance == null) {
                    instance = new AdConfig();
                }
            }
        }
        return instance;
    }

    // ========== General Setters ==========

    @NonNull
    public AdConfig setAdStatus(@NonNull String adStatus) {
        this.adStatus = adStatus;
        return this;
    }

    @NonNull
    public AdConfig setAdNetwork(@NonNull String adNetwork) {
        this.adNetwork = adNetwork;
        return this;
    }

    @NonNull
    public AdConfig setBackupAdNetworks(@NonNull String... networks) {
        this.backupAdNetworks.clear();
        this.backupAdNetworks.addAll(Arrays.asList(networks));
        return this;
    }

    @NonNull
    public AdConfig addBackupAdNetwork(@NonNull String network) {
        if (!this.backupAdNetworks.contains(network)) {
            this.backupAdNetworks.add(network);
        }
        return this;
    }

    @NonNull
    public AdConfig setPlacementStatus(int placementStatus) {
        this.placementStatus = placementStatus;
        return this;
    }

    @NonNull
    public AdConfig setLegacyGDPR(boolean legacyGDPR) {
        this.legacyGDPR = legacyGDPR;
        return this;
    }

    @NonNull
    public AdConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    // ========== AdMob Setters ==========

    @NonNull
    public AdConfig setAdMobAppId(@NonNull String id) {
        this.adMobAppId = id;
        return this;
    }

    @NonNull
    public AdConfig setAdMobBannerId(@NonNull String id) {
        this.adMobBannerId = id;
        return this;
    }

    @NonNull
    public AdConfig setAdMobInterstitialId(@NonNull String id) {
        this.adMobInterstitialId = id;
        return this;
    }

    @NonNull
    public AdConfig setAdMobRewardedId(@NonNull String id) {
        this.adMobRewardedId = id;
        return this;
    }

    @NonNull
    public AdConfig setAdMobRewardedInterstitialId(@NonNull String id) {
        this.adMobRewardedInterstitialId = id;
        return this;
    }

    @NonNull
    public AdConfig setAdMobNativeId(@NonNull String id) {
        this.adMobNativeId = id;
        return this;
    }

    @NonNull
    public AdConfig setAdMobAppOpenId(@NonNull String id) {
        this.adMobAppOpenId = id;
        return this;
    }

    // ========== Meta Setters ==========

    @NonNull
    public AdConfig setMetaBannerId(@NonNull String id) {
        this.metaBannerId = id;
        return this;
    }

    @NonNull
    public AdConfig setMetaInterstitialId(@NonNull String id) {
        this.metaInterstitialId = id;
        return this;
    }

    @NonNull
    public AdConfig setMetaRewardedId(@NonNull String id) {
        this.metaRewardedId = id;
        return this;
    }

    @NonNull
    public AdConfig setMetaNativeId(@NonNull String id) {
        this.metaNativeId = id;
        return this;
    }

    // ========== Unity Setters ==========

    @NonNull
    public AdConfig setUnityGameId(@NonNull String id) {
        this.unityGameId = id;
        return this;
    }

    @NonNull
    public AdConfig setUnityBannerId(@NonNull String id) {
        this.unityBannerId = id;
        return this;
    }

    @NonNull
    public AdConfig setUnityInterstitialId(@NonNull String id) {
        this.unityInterstitialId = id;
        return this;
    }

    @NonNull
    public AdConfig setUnityRewardedId(@NonNull String id) {
        this.unityRewardedId = id;
        return this;
    }

    // ========== AppLovin Setters ==========

    @NonNull
    public AdConfig setAppLovinSdkKey(@NonNull String key) {
        this.appLovinSdkKey = key;
        return this;
    }

    @NonNull
    public AdConfig setAppLovinBannerId(@NonNull String id) {
        this.appLovinBannerId = id;
        return this;
    }

    @NonNull
    public AdConfig setAppLovinInterstitialId(@NonNull String id) {
        this.appLovinInterstitialId = id;
        return this;
    }

    @NonNull
    public AdConfig setAppLovinRewardedId(@NonNull String id) {
        this.appLovinRewardedId = id;
        return this;
    }

    @NonNull
    public AdConfig setAppLovinAppOpenId(@NonNull String id) {
        this.appLovinAppOpenId = id;
        return this;
    }

    // ========== IronSource Setters ==========

    @NonNull
    public AdConfig setIronSourceAppKey(@NonNull String key) {
        this.ironSourceAppKey = key;
        return this;
    }

    @NonNull
    public AdConfig setIronSourceBannerId(@NonNull String id) {
        this.ironSourceBannerId = id;
        return this;
    }

    @NonNull
    public AdConfig setIronSourceInterstitialId(@NonNull String id) {
        this.ironSourceInterstitialId = id;
        return this;
    }

    @NonNull
    public AdConfig setIronSourceRewardedId(@NonNull String id) {
        this.ironSourceRewardedId = id;
        return this;
    }

    // ========== StartApp Setters ==========

    @NonNull
    public AdConfig setStartAppId(@NonNull String id) {
        this.startAppId = id;
        return this;
    }

    // ========== Wortise Setters ==========

    @NonNull
    public AdConfig setWortiseAppId(@NonNull String id) {
        this.wortiseAppId = id;
        return this;
    }

    @NonNull
    public AdConfig setWortiseBannerId(@NonNull String id) {
        this.wortiseBannerId = id;
        return this;
    }

    @NonNull
    public AdConfig setWortiseInterstitialId(@NonNull String id) {
        this.wortiseInterstitialId = id;
        return this;
    }

    @NonNull
    public AdConfig setWortiseRewardedId(@NonNull String id) {
        this.wortiseRewardedId = id;
        return this;
    }

    @NonNull
    public AdConfig setWortiseNativeId(@NonNull String id) {
        this.wortiseNativeId = id;
        return this;
    }

    @NonNull
    public AdConfig setWortiseAppOpenId(@NonNull String id) {
        this.wortiseAppOpenId = id;
        return this;
    }

    // ========== Format Settings ==========

    @NonNull
    public AdConfig setInterstitialInterval(int seconds) {
        this.interstitialInterval = seconds;
        return this;
    }

    @NonNull
    public AdConfig setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
        return this;
    }

    @NonNull
    public AdConfig setCollapsibleBanner(boolean collapsible) {
        this.collapsibleBanner = collapsible;
        return this;
    }

    @NonNull
    public AdConfig setNativeAdStyle(@NonNull String style) {
        this.nativeAdStyle = style;
        return this;
    }

    // ========== Getters ==========

    @NonNull
    public String getAdStatus() {
        return adStatus;
    }

    @NonNull
    public String getAdNetwork() {
        return adNetwork;
    }

    @NonNull
    public List<String> getBackupAdNetworks() {
        return Collections.unmodifiableList(backupAdNetworks);
    }

    @Nullable
    public String getFirstBackupAdNetwork() {
        return backupAdNetworks.isEmpty() ? null : backupAdNetworks.get(0);
    }

    public int getPlacementStatus() {
        return placementStatus;
    }

    public boolean isLegacyGDPR() {
        return legacyGDPR;
    }

    public boolean isDebug() {
        return debug;
    }

    @NonNull
    public String getAdMobAppId() {
        return adMobAppId;
    }

    @NonNull
    public String getAdMobBannerId() {
        return adMobBannerId;
    }

    @NonNull
    public String getAdMobInterstitialId() {
        return adMobInterstitialId;
    }

    @NonNull
    public String getAdMobRewardedId() {
        return adMobRewardedId;
    }

    @NonNull
    public String getAdMobRewardedInterstitialId() {
        return adMobRewardedInterstitialId;
    }

    @NonNull
    public String getAdMobNativeId() {
        return adMobNativeId;
    }

    @NonNull
    public String getAdMobAppOpenId() {
        return adMobAppOpenId;
    }

    @NonNull
    public String getMetaBannerId() {
        return metaBannerId;
    }

    @NonNull
    public String getMetaInterstitialId() {
        return metaInterstitialId;
    }

    @NonNull
    public String getMetaRewardedId() {
        return metaRewardedId;
    }

    @NonNull
    public String getMetaNativeId() {
        return metaNativeId;
    }

    @NonNull
    public String getUnityGameId() {
        return unityGameId;
    }

    @NonNull
    public String getUnityBannerId() {
        return unityBannerId;
    }

    @NonNull
    public String getUnityInterstitialId() {
        return unityInterstitialId;
    }

    @NonNull
    public String getUnityRewardedId() {
        return unityRewardedId;
    }

    @NonNull
    public String getAppLovinSdkKey() {
        return appLovinSdkKey;
    }

    @NonNull
    public String getAppLovinBannerId() {
        return appLovinBannerId;
    }

    @NonNull
    public String getAppLovinInterstitialId() {
        return appLovinInterstitialId;
    }

    @NonNull
    public String getAppLovinRewardedId() {
        return appLovinRewardedId;
    }

    @NonNull
    public String getAppLovinAppOpenId() {
        return appLovinAppOpenId;
    }

    @NonNull
    public String getIronSourceAppKey() {
        return ironSourceAppKey;
    }

    @NonNull
    public String getIronSourceBannerId() {
        return ironSourceBannerId;
    }

    @NonNull
    public String getIronSourceInterstitialId() {
        return ironSourceInterstitialId;
    }

    @NonNull
    public String getIronSourceRewardedId() {
        return ironSourceRewardedId;
    }

    @NonNull
    public String getStartAppId() {
        return startAppId;
    }

    @NonNull
    public String getWortiseAppId() {
        return wortiseAppId;
    }

    @NonNull
    public String getWortiseBannerId() {
        return wortiseBannerId;
    }

    @NonNull
    public String getWortiseInterstitialId() {
        return wortiseInterstitialId;
    }

    @NonNull
    public String getWortiseRewardedId() {
        return wortiseRewardedId;
    }

    @NonNull
    public String getWortiseNativeId() {
        return wortiseNativeId;
    }

    @NonNull
    public String getWortiseAppOpenId() {
        return wortiseAppOpenId;
    }

    public int getInterstitialInterval() {
        return interstitialInterval;
    }

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public boolean isCollapsibleBanner() {
        return collapsibleBanner;
    }

    @NonNull
    public String getNativeAdStyle() {
        return nativeAdStyle;
    }
}
