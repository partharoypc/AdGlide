package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_DISCOVERY;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.NONE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.UNITY;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.applovin.adview.AppLovinAdView;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinSdkUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.ads.banner.BannerListener;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BannerAd {

    public static class Builder {

    private static final String TAG = "AdGlide";
        private final Activity activity;
        private AdView adView;
        private com.facebook.ads.AdView metaAdView;
        private BannerView unityBannerAd;
        private MaxAdView appLovinMaxBannerAd;
        private AppLovinAdView appLovinDiscoveryBannerAd;
        private IronSourceBannerLayout ironSourceBannerLayout;
        private Banner startAppBannerAd;
        private com.wortise.ads.banner.BannerAd wortiseBannerAd;

        private FrameLayout ironSourceBannerView;
        private FrameLayout wortiseBannerView;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private String adMobBannerId = "";
        private String metaBannerId = "";
        private String unityBannerId = "";
        private String appLovinBannerId = "";
        private String appLovinBannerZoneId = "";
        private String ironSourceBannerId = "";
        private String wortiseBannerId = "";
        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;
        private boolean collapsibleBanner = false;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        @androidx.annotation.NonNull
        public Builder build() {
            loadBannerAd();
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setAdStatus(@androidx.annotation.NonNull String adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setAdNetwork(@androidx.annotation.NonNull String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        @androidx.annotation.Nullable
        public Builder setBackupAdNetwork(@androidx.annotation.Nullable String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            if (waterfallManager == null) {
                waterfallManager = new WaterfallManager(backupAdNetwork);
            } else {
                waterfallManager.getNetworks().add(backupAdNetwork);
            }
            return this;
        }

        @androidx.annotation.Nullable
        public Builder addBackupAdNetwork(@androidx.annotation.Nullable String backupAdNetwork) {
            if (waterfallManager == null) {
                waterfallManager = new WaterfallManager(backupAdNetwork);
            } else {
                waterfallManager.getNetworks().add(backupAdNetwork);
            }
            return this;
        }

        @androidx.annotation.Nullable
        public Builder setBackupAdNetworks(String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks.length > 0) {
                this.backupAdNetwork = backupAdNetworks[0]; // Maintain legacy sync
            }
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setAdMobBannerId(@androidx.annotation.NonNull String adMobBannerId) {
            this.adMobBannerId = adMobBannerId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setMetaBannerId(@androidx.annotation.NonNull String metaBannerId) {
            this.metaBannerId = metaBannerId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setUnityBannerId(@androidx.annotation.NonNull String unityBannerId) {
            this.unityBannerId = unityBannerId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setAppLovinBannerId(@androidx.annotation.NonNull String appLovinBannerId) {
            this.appLovinBannerId = appLovinBannerId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setAppLovinBannerZoneId(@androidx.annotation.NonNull String appLovinBannerZoneId) {
            this.appLovinBannerZoneId = appLovinBannerZoneId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setironSourceBannerId(@androidx.annotation.NonNull String ironSourceBannerId) {
            this.ironSourceBannerId = ironSourceBannerId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setWortiseBannerId(@androidx.annotation.NonNull String wortiseBannerId) {
            this.wortiseBannerId = wortiseBannerId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setPlacementStatus(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setDarkTheme(boolean darkTheme) {
            this.darkTheme = darkTheme;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setLegacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setIsCollapsibleBanner(boolean collapsibleBanner) {
            this.collapsibleBanner = collapsibleBanner;
            return this;
        }

        public void loadBannerAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB: {
                            FrameLayout adContainerView = activity.findViewById(R.id.ad_mob_banner_view_container);
                            adContainerView.post(() -> {
                                try {
                                    adView = new AdView(activity);
                                    adView.setAdUnitId(adMobBannerId);
                                    adContainerView.removeAllViews();
                                    adContainerView.addView(adView);
                                    adView.setAdSize(Tools.getAdSize(activity));
                                    adView.loadAd(Tools.getAdRequest(collapsibleBanner));
                                    adView.setAdListener(new AdListener() {
                                        @Override
                                        public void onAdLoaded() {
                                            adContainerView.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                            adContainerView.setVisibility(View.GONE);
                                            loadBackupBannerAd();
                                        }

                                        @Override
                                        public void onAdOpened() {
                                        }

                                        @Override
                                        public void onAdClicked() {
                                        }

                                        @Override
                                        public void onAdClosed() {
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.e(TAG, "Error loading AdMob Banner: " + e.getMessage());
                                    loadBackupBannerAd();
                                }
                            });
                            Log.d(TAG, adNetwork + " Banner Ad unit Id : " + adMobBannerId);
                            break;
                        }

                        case META: {
                            metaAdView = new com.facebook.ads.AdView(activity, metaBannerId, AdSize.BANNER_HEIGHT_50);
                            RelativeLayout metaAdViewContainer = activity.findViewById(R.id.meta_banner_view_container);
                            metaAdViewContainer.addView(metaAdView);
                            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                                @Override
                                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                                    metaAdViewContainer.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                    Log.d(TAG, "Error load FAN : " + adError.getErrorMessage());
                                }

                                @Override
                                public void onAdLoaded(Ad ad) {
                                    metaAdViewContainer.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdClicked(Ad ad) {
                                }

                                @Override
                                public void onLoggingImpression(Ad ad) {
                                }
                            };

                            com.facebook.ads.AdView.AdViewLoadConfig loadAdConfig = metaAdView.buildLoadAdConfig()
                                    .withAdListener(adListener).build();
                            metaAdView.loadAd(loadAdConfig);
                            break;
                        }

                        case UNITY: {
                            RelativeLayout unityAdContainerView = activity
                                    .findViewById(R.id.unity_banner_view_container);
                            unityBannerAd = new BannerView(activity, unityBannerId, new UnityBannerSize(320, 50));
                            unityBannerAd.setListener(new BannerView.IListener() {
                                @Override
                                public void onBannerLoaded(BannerView bannerAdView) {
                                    unityAdContainerView.setVisibility(View.VISIBLE);
                                    Log.d(TAG, "Unity Banner Ad loaded");
                                }

                                @Override
                                public void onBannerClick(BannerView bannerAdView) {
                                }

                                @Override
                                public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
                                    unityAdContainerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                    Log.d(TAG, "Unity Banner Ad failed to load: " + errorInfo.errorMessage);
                                }

                                @Override
                                public void onBannerLeftApplication(BannerView bannerAdView) {
                                }

                                @Override
                                public void onBannerShown(BannerView bannerAdView) {
                                    // No-op
                                }
                            });
                            unityAdContainerView.removeAllViews();
                            unityAdContainerView.addView(unityBannerAd);
                            unityBannerAd.load();
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX: {
                            RelativeLayout appLovinMaxAdContainerView = new RelativeLayout(activity);
                            appLovinMaxBannerAd = new MaxAdView(appLovinBannerId, activity);
                            appLovinMaxBannerAd.setListener(new MaxAdViewAdListener() {
                                @Override
                                public void onAdExpanded(MaxAd ad) {
                                }

                                @Override
                                public void onAdCollapsed(MaxAd ad) {
                                }

                                @Override
                                public void onAdLoaded(MaxAd ad) {
                                    appLovinMaxAdContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdDisplayed(MaxAd ad) {
                                }

                                @Override
                                public void onAdHidden(MaxAd ad) {
                                }

                                @Override
                                public void onAdClicked(MaxAd ad) {
                                }

                                @Override
                                public void onAdLoadFailed(String adUnitId, MaxError error) {
                                    appLovinMaxAdContainerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                }
                            });
                            int width = ViewGroup.LayoutParams.MATCH_PARENT;
                            int heightPx = AppLovinSdkUtils.dpToPx(activity, 50);
                            appLovinMaxBannerAd.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                            appLovinMaxAdContainerView.removeAllViews();
                            appLovinMaxAdContainerView.addView(appLovinMaxBannerAd);
                            appLovinMaxBannerAd.loadAd();
                            break;
                        }

                        case APPLOVIN_DISCOVERY: {
                            RelativeLayout appLovinDiscoveryAdContainerView = activity
                                    .findViewById(R.id.app_lovin_discovery_banner_view_container);
                            appLovinDiscoveryBannerAd = new AppLovinAdView(AppLovinAdSize.BANNER, appLovinBannerZoneId,
                                    activity);
                            appLovinDiscoveryBannerAd.setAdLoadListener(new AppLovinAdLoadListener() {
                                @Override
                                public void adReceived(AppLovinAd ad) {
                                    appLovinDiscoveryAdContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void failedToReceiveAd(int errorCode) {
                                    appLovinDiscoveryAdContainerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }
                            });
                            appLovinDiscoveryAdContainerView.removeAllViews();
                            appLovinDiscoveryAdContainerView.addView(appLovinDiscoveryBannerAd);
                            appLovinDiscoveryBannerAd.loadNextAd();
                            break;
                        }

                        case IRONSOURCE:
                        case META_BIDDING_IRONSOURCE: {
                            ironSourceBannerView = activity.findViewById(R.id.iron_source_banner_view_container);
                            ironSourceBannerLayout = IronSource.createBanner(activity, ISBannerSize.BANNER);
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                            ironSourceBannerView.addView(ironSourceBannerLayout, 0, layoutParams);
                            ironSourceBannerLayout.setLevelPlayBannerListener(new LevelPlayBannerListener() {
                                @Override
                                public void onAdLoaded(AdInfo adInfo) {
                                    ironSourceBannerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdLoadFailed(IronSourceError error) {
                                    ironSourceBannerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onAdClicked(AdInfo adInfo) {
                                }

                                @Override
                                public void onAdScreenPresented(AdInfo adInfo) {
                                }

                                @Override
                                public void onAdScreenDismissed(AdInfo adInfo) {
                                }

                                @Override
                                public void onAdLeftApplication(AdInfo adInfo) {
                                }
                            });
                            IronSource.loadBanner(ironSourceBannerLayout, ironSourceBannerId);
                            break;
                        }

                        case STARTAPP: {
                            RelativeLayout startAppAdContainerView = activity
                                    .findViewById(R.id.start_app_banner_view_container);
                            startAppBannerAd = new Banner(activity, new BannerListener() {
                                @Override
                                public void onReceiveAd(View view) {
                                    startAppAdContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onFailedToReceiveAd(View view) {
                                    startAppAdContainerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onImpression(View view) {
                                }

                                @Override
                                public void onClick(View view) {
                                }
                            });
                            RelativeLayout.LayoutParams bannerParameters = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            startAppAdContainerView.removeAllViews();
                            startAppAdContainerView.addView(startAppBannerAd, bannerParameters);
                            startAppBannerAd.loadAd();
                            break;
                        }

                        case WORTISE: {
                            wortiseBannerView = activity.findViewById(R.id.wortise_banner_view_container);
                            wortiseBannerAd = new com.wortise.ads.banner.BannerAd(activity);
                            wortiseBannerAd.setAdSize(com.wortise.ads.AdSize.HEIGHT_50);
                            wortiseBannerAd.setAdUnitId(wortiseBannerId);
                            wortiseBannerAd.setListener(new com.wortise.ads.banner.BannerAd.Listener() {
                                @Override
                                public void onBannerClicked(@NonNull com.wortise.ads.banner.BannerAd bannerAd) {
                                }

                                @Override
                                public void onBannerFailedToLoad(@NonNull com.wortise.ads.banner.BannerAd bannerAd,
                                                                 @NonNull com.wortise.ads.AdError adError) {
                                    wortiseBannerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onBannerLoaded(@NonNull com.wortise.ads.banner.BannerAd bannerAd) {
                                    wortiseBannerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onBannerImpression(@NonNull com.wortise.ads.banner.BannerAd bannerAd) {
                                }

                                @Override
                                public void onBannerRevenuePaid(@NonNull com.wortise.ads.banner.BannerAd bannerAd,
                                                                @NonNull com.wortise.ads.RevenueData revenueData) {
                                }
                            });
                            wortiseBannerView.removeAllViews();
                            wortiseBannerView.addView(wortiseBannerAd);
                            wortiseBannerAd.loadAd();
                            break;
                        }

                        default:
                            break;
                    }
                    Log.d(TAG, "Banner Ad is enabled");
                } else {
                    Log.d(TAG, "Banner Ad is disabled");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading Banner Ad: " + e.getMessage());
            }
        }

        public void loadBackupBannerAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    if (waterfallManager == null) {
                        if (backupAdNetwork != null && !backupAdNetwork.isEmpty()) {
                            waterfallManager = new WaterfallManager(backupAdNetwork);
                        } else {
                            return;
                        }
                    }

                    String networkToLoad = waterfallManager.getNext();
                    if (networkToLoad == null) {
                        Log.d(TAG, "All backup banner ads failed to load");
                        return;
                    }

                    switch (networkToLoad) {
                        case ADMOB:
                        case META_BIDDING_ADMOB: {
                            FrameLayout adContainerView = activity.findViewById(R.id.ad_mob_banner_view_container);
                            adContainerView.post(() -> {
                                try {
                                    adView = new AdView(activity);
                                    adView.setAdUnitId(adMobBannerId);
                                    adContainerView.removeAllViews();
                                    adContainerView.addView(adView);
                                    adView.setAdSize(Tools.getAdSize(activity));
                                    adView.loadAd(Tools.getAdRequest(collapsibleBanner));
                                    adView.setAdListener(new AdListener() {
                                        @Override
                                        public void onAdLoaded() {
                                            adContainerView.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                            adContainerView.setVisibility(View.GONE);
                                            loadBackupBannerAd();
                                        }

                                        @Override
                                        public void onAdOpened() {
                                        }

                                        @Override
                                        public void onAdClicked() {
                                        }

                                        @Override
                                        public void onAdClosed() {
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.e(TAG, "Error loading AdMob Backup Banner: " + e.getMessage());
                                    loadBackupBannerAd();
                                }
                            });
                            Log.d(TAG, networkToLoad + " Banner Ad unit Id : " + adMobBannerId);
                            break;
                        }

                        case META: {
                            metaAdView = new com.facebook.ads.AdView(activity, metaBannerId, AdSize.BANNER_HEIGHT_50);
                            RelativeLayout metaAdViewContainer = activity.findViewById(R.id.meta_banner_view_container);
                            metaAdViewContainer.addView(metaAdView);
                            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                                @Override
                                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                                    metaAdViewContainer.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                    Log.d(TAG, "Error load FAN : " + adError.getErrorMessage());
                                }

                                @Override
                                public void onAdLoaded(Ad ad) {
                                    metaAdViewContainer.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdClicked(Ad ad) {
                                }

                                @Override
                                public void onLoggingImpression(Ad ad) {
                                }
                            };

                            com.facebook.ads.AdView.AdViewLoadConfig loadAdConfig = metaAdView.buildLoadAdConfig()
                                    .withAdListener(adListener).build();
                            metaAdView.loadAd(loadAdConfig);
                            break;
                        }

                        case UNITY: {
                            RelativeLayout unityAdContainerView = activity
                                    .findViewById(R.id.unity_banner_view_container);
                            unityBannerAd = new BannerView(activity, unityBannerId, new UnityBannerSize(320, 50));
                            unityBannerAd.setListener(new BannerView.IListener() {
                                @Override
                                public void onBannerLoaded(BannerView bannerAdView) {
                                    unityAdContainerView.setVisibility(View.VISIBLE);
                                    Log.d(TAG, "Unity Banner Ad loaded");
                                }

                                @Override
                                public void onBannerClick(BannerView bannerAdView) {
                                }

                                @Override
                                public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
                                    unityAdContainerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                    Log.d(TAG, "Unity Banner Ad failed to load: " + errorInfo.errorMessage);
                                }

                                @Override
                                public void onBannerLeftApplication(BannerView bannerAdView) {
                                }

                                @Override
                                public void onBannerShown(BannerView bannerAdView) {
                                }
                            });
                            unityAdContainerView.removeAllViews();
                            unityAdContainerView.addView(unityBannerAd);
                            unityBannerAd.load();
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX: {
                            RelativeLayout appLovinMaxAdContainerView = new RelativeLayout(activity);
                            appLovinMaxBannerAd = new MaxAdView(appLovinBannerId, activity);
                            appLovinMaxBannerAd.setListener(new MaxAdViewAdListener() {
                                @Override
                                public void onAdExpanded(MaxAd ad) {
                                }

                                @Override
                                public void onAdCollapsed(MaxAd ad) {
                                }

                                @Override
                                public void onAdLoaded(MaxAd ad) {
                                    appLovinMaxAdContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdDisplayed(MaxAd ad) {
                                }

                                @Override
                                public void onAdHidden(MaxAd ad) {
                                }

                                @Override
                                public void onAdClicked(MaxAd ad) {
                                }

                                @Override
                                public void onAdLoadFailed(String adUnitId, MaxError error) {
                                    appLovinMaxAdContainerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                }
                            });
                            int width = ViewGroup.LayoutParams.MATCH_PARENT;
                            int heightPx = AppLovinSdkUtils.dpToPx(activity, 50);
                            appLovinMaxBannerAd.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                            appLovinMaxAdContainerView.removeAllViews();
                            appLovinMaxAdContainerView.addView(appLovinMaxBannerAd);
                            appLovinMaxBannerAd.loadAd();
                            break;
                        }

                        case APPLOVIN_DISCOVERY: {
                            RelativeLayout appLovinDiscoveryAdContainerView = activity
                                    .findViewById(R.id.app_lovin_discovery_banner_view_container);
                            appLovinDiscoveryBannerAd = new AppLovinAdView(AppLovinAdSize.BANNER, appLovinBannerZoneId,
                                    activity);
                            appLovinDiscoveryBannerAd.setAdLoadListener(new AppLovinAdLoadListener() {
                                @Override
                                public void adReceived(AppLovinAd ad) {
                                    appLovinDiscoveryAdContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void failedToReceiveAd(int errorCode) {
                                    appLovinDiscoveryAdContainerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }
                            });
                            appLovinDiscoveryAdContainerView.removeAllViews();
                            appLovinDiscoveryAdContainerView.addView(appLovinDiscoveryBannerAd);
                            appLovinDiscoveryBannerAd.loadNextAd();
                            break;
                        }

                        case IRONSOURCE:
                        case META_BIDDING_IRONSOURCE: {
                            ironSourceBannerView = activity.findViewById(R.id.iron_source_banner_view_container);
                            ironSourceBannerLayout = IronSource.createBanner(activity, ISBannerSize.BANNER);
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                            ironSourceBannerView.addView(ironSourceBannerLayout, 0, layoutParams);
                            ironSourceBannerLayout.setLevelPlayBannerListener(new LevelPlayBannerListener() {
                                @Override
                                public void onAdLoaded(AdInfo adInfo) {
                                    ironSourceBannerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdLoadFailed(IronSourceError error) {
                                    ironSourceBannerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onAdClicked(AdInfo adInfo) {
                                }

                                @Override
                                public void onAdScreenPresented(AdInfo adInfo) {
                                }

                                @Override
                                public void onAdScreenDismissed(AdInfo adInfo) {
                                }

                                @Override
                                public void onAdLeftApplication(AdInfo adInfo) {
                                }
                            });
                            IronSource.loadBanner(ironSourceBannerLayout, ironSourceBannerId);
                            break;
                        }

                        case STARTAPP: {
                            RelativeLayout startAppAdContainerView = activity
                                    .findViewById(R.id.start_app_banner_view_container);
                            startAppBannerAd = new Banner(activity, new BannerListener() {
                                @Override
                                public void onReceiveAd(View view) {
                                    startAppAdContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onFailedToReceiveAd(View view) {
                                    startAppAdContainerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onImpression(View view) {
                                }

                                @Override
                                public void onClick(View view) {
                                }
                            });
                            RelativeLayout.LayoutParams bannerParameters = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            startAppAdContainerView.removeAllViews();
                            startAppAdContainerView.addView(startAppBannerAd, bannerParameters);
                            startAppBannerAd.loadAd();
                            break;
                        }

                        case WORTISE: {
                            wortiseBannerView = activity.findViewById(R.id.wortise_banner_view_container);
                            wortiseBannerAd = new com.wortise.ads.banner.BannerAd(activity);
                            wortiseBannerAd.setAdSize(com.wortise.ads.AdSize.HEIGHT_50);
                            wortiseBannerAd.setAdUnitId(wortiseBannerId);
                            wortiseBannerAd.setListener(new com.wortise.ads.banner.BannerAd.Listener() {
                                @Override
                                public void onBannerClicked(@NonNull com.wortise.ads.banner.BannerAd bannerAd) {
                                }

                                @Override
                                public void onBannerFailedToLoad(@NonNull com.wortise.ads.banner.BannerAd bannerAd,
                                                                 @NonNull com.wortise.ads.AdError adError) {
                                    wortiseBannerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onBannerLoaded(@NonNull com.wortise.ads.banner.BannerAd bannerAd) {
                                    wortiseBannerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onBannerImpression(@NonNull com.wortise.ads.banner.BannerAd bannerAd) {
                                }

                                @Override
                                public void onBannerRevenuePaid(@NonNull com.wortise.ads.banner.BannerAd bannerAd,
                                                                @NonNull com.wortise.ads.RevenueData revenueData) {
                                }
                            });
                            wortiseBannerView.removeAllViews();
                            wortiseBannerView.addView(wortiseBannerAd);
                            wortiseBannerAd.loadAd();
                            break;
                        }

                        default:
                            break;
                    }
                    Log.d(TAG, "[" + networkToLoad + "] is selected as Backup Ads");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading Backup Banner Ad: " + e.getMessage());
            }
        }

        public void destroyAndDetachBanner() {
            if (adView != null) {
                adView.setAdListener(null);
                adView.destroy();
                adView = null;
            }
            if (metaAdView != null) {
                metaAdView.destroy();
                metaAdView = null;
            }
            if (unityBannerAd != null) {
                unityBannerAd.destroy();
                unityBannerAd = null;
            }
            if (appLovinMaxBannerAd != null) {
                appLovinMaxBannerAd.destroy();
                appLovinMaxBannerAd = null;
            }
            if (appLovinDiscoveryBannerAd != null) {
                appLovinDiscoveryBannerAd.destroy();
                appLovinDiscoveryBannerAd = null;
            }
            if (ironSourceBannerLayout != null) {
                IronSource.destroyBanner(ironSourceBannerLayout);
                ironSourceBannerLayout = null;
            }

            if (wortiseBannerAd != null) {
                wortiseBannerAd.destroy();
                wortiseBannerAd = null;
            }
            if (startAppBannerAd != null) {
                startAppBannerAd = null;
            }
        }

    }

}




