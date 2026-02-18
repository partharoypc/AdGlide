package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_DISCOVERY;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.FACEBOOK;
import static com.partharoypc.adglide.util.Constant.FAN;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_AD_MANAGER;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.GOOGLE_AD_MANAGER;
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
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.util.Tools;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

public class BannerAd {

    public static class Builder {

        private static final String TAG = "AdNetwork";
        private final Activity activity;
        private AdView adView;
        private AdManagerAdView adManagerAdView;
        private com.facebook.ads.AdView fanAdView;
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
        private String adMobBannerId = "";
        private String googleAdManagerBannerId = "";
        private String fanBannerId = "";
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

        public Builder build() {
            loadBannerAd();
            return this;
        }

        public Builder setAdStatus(String adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        public Builder setAdNetwork(String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        public Builder setBackupAdNetwork(String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            return this;
        }

        public Builder setAdMobBannerId(String adMobBannerId) {
            this.adMobBannerId = adMobBannerId;
            return this;
        }

        public Builder setGoogleAdManagerBannerId(String googleAdManagerBannerId) {
            this.googleAdManagerBannerId = googleAdManagerBannerId;
            return this;
        }

        public Builder setFanBannerId(String fanBannerId) {
            this.fanBannerId = fanBannerId;
            return this;
        }

        public Builder setUnityBannerId(String unityBannerId) {
            this.unityBannerId = unityBannerId;
            return this;
        }

        public Builder setAppLovinBannerId(String appLovinBannerId) {
            this.appLovinBannerId = appLovinBannerId;
            return this;
        }

        public Builder setAppLovinBannerZoneId(String appLovinBannerZoneId) {
            this.appLovinBannerZoneId = appLovinBannerZoneId;
            return this;
        }

        public Builder setIronSourceBannerId(String ironSourceBannerId) {
            this.ironSourceBannerId = ironSourceBannerId;
            return this;
        }

        public Builder setWortiseBannerId(String wortiseBannerId) {
            this.wortiseBannerId = wortiseBannerId;
            return this;
        }

        public Builder setPlacementStatus(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        public Builder setDarkTheme(boolean darkTheme) {
            this.darkTheme = darkTheme;
            return this;
        }

        public Builder setLegacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public Builder setIsCollapsibleBanner(boolean collapsibleBanner) {
            this.collapsibleBanner = collapsibleBanner;
            return this;
        }

        public void loadBannerAd() {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (adNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        FrameLayout adContainerView = activity.findViewById(R.id.admob_banner_view_container);
                        adContainerView.post(() -> {
                            adView = new AdView(activity);
                            adView.setAdUnitId(adMobBannerId);
                            adContainerView.removeAllViews();
                            adContainerView.addView(adView);
                            adView.setAdSize(Tools.getAdSize(activity));
                            adView.loadAd(Tools.getAdRequest(collapsibleBanner));
                            adView.setAdListener(new AdListener() {
                                @Override
                                public void onAdLoaded() {
                                    // Code to be executed when an ad finishes loading.
                                    adContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                    // Code to be executed when an ad request fails.
                                    adContainerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onAdOpened() {
                                    // Code to be executed when an ad opens an overlay that
                                    // covers the screen.
                                }

                                @Override
                                public void onAdClicked() {
                                    // Code to be executed when the user clicks on an ad.
                                }

                                @Override
                                public void onAdClosed() {
                                    // Code to be executed when the user is about to return
                                    // to the app after tapping on an ad.
                                }
                            });
                        });
                        Log.d(TAG, adNetwork + " Banner Ad unit Id : " + adMobBannerId);
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        FrameLayout googleAdContainerView = activity.findViewById(R.id.google_ad_banner_view_container);
                        googleAdContainerView.post(() -> {
                            adManagerAdView = new AdManagerAdView(activity);
                            adManagerAdView.setAdUnitId(googleAdManagerBannerId);
                            googleAdContainerView.removeAllViews();
                            googleAdContainerView.addView(adManagerAdView);
                            adManagerAdView.setAdSize(Tools.getAdSize(activity));
                            adManagerAdView.loadAd(Tools.getGoogleAdManagerRequest());
                            adManagerAdView.setAdListener(new AdListener() {
                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    super.onAdFailedToLoad(loadAdError);
                                    googleAdContainerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }

                                @Override
                                public void onAdLoaded() {
                                    super.onAdLoaded();
                                    googleAdContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdOpened() {
                                    super.onAdOpened();
                                }
                            });
                        });
                        break;

                    case FAN:
                    case FACEBOOK:
                        fanAdView = new com.facebook.ads.AdView(activity, fanBannerId, AdSize.BANNER_HEIGHT_50);
                        RelativeLayout fanAdViewContainer = activity.findViewById(R.id.fan_banner_view_container);
                        fanAdViewContainer.addView(fanAdView);
                        com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                            @Override
                            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                                fanAdViewContainer.setVisibility(View.GONE);
                                loadBackupBannerAd();
                                Log.d(TAG, "Error load FAN : " + adError.getErrorMessage());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                fanAdViewContainer.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdClicked(Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {

                            }
                        };
                        com.facebook.ads.AdView.AdViewLoadConfig loadAdConfig = fanAdView.buildLoadAdConfig()
                                .withAdListener(adListener).build();
                        fanAdView.loadAd(loadAdConfig);
                        break;

                    case UNITY:
                        RelativeLayout unityAdContainerView = activity.findViewById(R.id.unity_banner_view_container);
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
                        });
                        unityAdContainerView.removeAllViews();
                        unityAdContainerView.addView(unityBannerAd);
                        unityBannerAd.load();
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        RelativeLayout appLovinMaxAdContainerView = activity
                                .findViewById(R.id.applovin_max_banner_view_container);
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

                    case APPLOVIN_DISCOVERY:
                        RelativeLayout appLovinDiscoveryAdContainerView = activity
                                .findViewById(R.id.applovin_discovery_banner_view_container);
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

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        ironSourceBannerView = activity.findViewById(R.id.ironsource_banner_view_container);
                        ironSourceBannerLayout = IronSource.createBanner(activity, ISBannerSize.BANNER);
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        ironSourceBannerView.addView(ironSourceBannerLayout, 0, layoutParams);
                        ironSourceBannerLayout.setBannerListener(new BannerListener() {
                            @Override
                            public void onBannerAdLoaded() {
                                ironSourceBannerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onBannerAdLoadFailed(IronSourceError error) {
                                ironSourceBannerView.setVisibility(View.GONE);
                                loadBackupBannerAd();
                            }

                            @Override
                            public void onBannerAdClicked() {
                            }

                            @Override
                            public void onBannerAdScreenPresented() {
                            }

                            @Override
                            public void onBannerAdScreenDismissed() {
                            }

                            @Override
                            public void onBannerAdLeftApplication() {
                            }
                        });
                        IronSource.loadBanner(ironSourceBannerLayout, ironSourceBannerId);
                        break;

                    case STARTAPP:
                        RelativeLayout startAppAdContainerView = activity
                                .findViewById(R.id.startapp_banner_view_container);
                        startAppBannerAd = new Banner(activity, new AdEventListener() {
                            @Override
                            public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                startAppAdContainerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                startAppAdContainerView.setVisibility(View.GONE);
                                loadBackupBannerAd();
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

                    case WORTISE:
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
                        });
                        wortiseBannerView.removeAllViews();
                        wortiseBannerView.addView(wortiseBannerAd);
                        wortiseBannerAd.loadAd();
                        break;

                    default:
                        break;
                }
                Log.d(TAG, "Banner Ad is enabled");
            } else {
                Log.d(TAG, "Banner Ad is disabled");
            }
        }

        public void loadBackupBannerAd() {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (backupAdNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        FrameLayout adContainerView = activity.findViewById(R.id.admob_banner_view_container);
                        adContainerView.post(() -> {
                            adView = new AdView(activity);
                            adView.setAdUnitId(adMobBannerId);
                            adContainerView.removeAllViews();
                            adContainerView.addView(adView);
                            adView.setAdSize(Tools.getAdSize(activity));
                            // adView.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                            adView.loadAd(Tools.getAdRequest(collapsibleBanner));
                            adView.setAdListener(new AdListener() {
                                @Override
                                public void onAdLoaded() {
                                    // Code to be executed when an ad finishes loading.
                                    adContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                    // Code to be executed when an ad request fails.
                                    adContainerView.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAdOpened() {
                                    // Code to be executed when an ad opens an overlay that
                                    // covers the screen.
                                }

                                @Override
                                public void onAdClicked() {
                                    // Code to be executed when the user clicks on an ad.
                                }

                                @Override
                                public void onAdClosed() {
                                    // Code to be executed when the user is about to return
                                    // to the app after tapping on an ad.
                                }
                            });
                        });
                        Log.d(TAG, adNetwork + " Banner Ad unit Id : " + adMobBannerId);
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        FrameLayout googleAdContainerView = activity.findViewById(R.id.google_ad_banner_view_container);
                        googleAdContainerView.post(() -> {
                            adManagerAdView = new AdManagerAdView(activity);
                            adManagerAdView.setAdUnitId(googleAdManagerBannerId);
                            googleAdContainerView.removeAllViews();
                            googleAdContainerView.addView(adManagerAdView);
                            adManagerAdView.setAdSize(Tools.getAdSize(activity));
                            adManagerAdView.loadAd(Tools.getGoogleAdManagerRequest());
                            adManagerAdView.setAdListener(new AdListener() {
                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    super.onAdFailedToLoad(loadAdError);
                                    googleAdContainerView.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }

                                @Override
                                public void onAdLoaded() {
                                    super.onAdLoaded();
                                    googleAdContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdOpened() {
                                    super.onAdOpened();
                                }
                            });
                        });
                        break;

                    case FAN:
                    case FACEBOOK:
                        fanAdView = new com.facebook.ads.AdView(activity, fanBannerId, AdSize.BANNER_HEIGHT_50);
                        RelativeLayout fanAdViewContainer = activity.findViewById(R.id.fan_banner_view_container);
                        fanAdViewContainer.addView(fanAdView);
                        com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                            @Override
                            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                                fanAdViewContainer.setVisibility(View.GONE);
                                Log.d(TAG, "Error load FAN : " + adError.getErrorMessage());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                fanAdViewContainer.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdClicked(Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {

                            }
                        };
                        com.facebook.ads.AdView.AdViewLoadConfig loadAdConfig = fanAdView.buildLoadAdConfig()
                                .withAdListener(adListener).build();
                        fanAdView.loadAd(loadAdConfig);
                        break;

                    case UNITY:
                        RelativeLayout unityAdContainerView = activity.findViewById(R.id.unity_banner_view_container);
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
                                Log.d(TAG, "Unity Banner Ad failed to load: " + errorInfo.errorMessage);
                            }

                            @Override
                            public void onBannerLeftApplication(BannerView bannerAdView) {

                            }
                        });
                        unityAdContainerView.removeAllViews();
                        unityAdContainerView.addView(unityBannerAd);
                        unityBannerAd.load();
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        RelativeLayout appLovinMaxAdContainerView = activity
                                .findViewById(R.id.applovin_max_banner_view_container);
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

                    case APPLOVIN_DISCOVERY:
                        RelativeLayout appLovinDiscoveryAdContainerView = activity
                                .findViewById(R.id.applovin_discovery_banner_view_container);
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
                            }
                        });
                        appLovinDiscoveryAdContainerView.removeAllViews();
                        appLovinDiscoveryAdContainerView.addView(appLovinDiscoveryBannerAd);
                        appLovinDiscoveryBannerAd.loadNextAd();
                        break;

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        ironSourceBannerView = activity.findViewById(R.id.ironsource_banner_view_container);
                        ironSourceBannerLayout = IronSource.createBanner(activity, ISBannerSize.BANNER);
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        ironSourceBannerView.addView(ironSourceBannerLayout, 0, layoutParams);
                        ironSourceBannerLayout.setBannerListener(new BannerListener() {
                            @Override
                            public void onBannerAdLoaded() {
                                ironSourceBannerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onBannerAdLoadFailed(IronSourceError error) {
                                ironSourceBannerView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onBannerAdClicked() {
                            }

                            @Override
                            public void onBannerAdScreenPresented() {
                            }

                            @Override
                            public void onBannerAdScreenDismissed() {
                            }

                            @Override
                            public void onBannerAdLeftApplication() {
                            }
                        });
                        IronSource.loadBanner(ironSourceBannerLayout, ironSourceBannerId);
                        break;

                    case STARTAPP:
                        RelativeLayout startAppAdContainerView = activity
                                .findViewById(R.id.startapp_banner_view_container);
                        startAppBannerAd = new Banner(activity, new AdEventListener() {
                            @Override
                            public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                startAppAdContainerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                startAppAdContainerView.setVisibility(View.GONE);
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

                    case WORTISE:
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
                            }

                            @Override
                            public void onBannerLoaded(@NonNull com.wortise.ads.banner.BannerAd bannerAd) {
                                wortiseBannerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onBannerImpression(@NonNull com.wortise.ads.banner.BannerAd bannerAd) {

                            }
                        });
                        wortiseBannerView.removeAllViews();
                        wortiseBannerView.addView(wortiseBannerAd);
                        wortiseBannerAd.loadAd();
                        break;

                    default:
                        break;
                }
                Log.d(TAG, "Banner Ad is enabled");
            } else {
                Log.d(TAG, "Banner Ad is disabled");
            }
        }

        /**
         * Destroys all loaded banner ad views and removes them from their containers.
         * Call this method in your Activity's {@code onDestroy()} to prevent memory
         * leaks.
         */
        public void destroyAndDetachBanner() {
            if (adView != null) {
                adView.setAdListener(null);
                adView.destroy();
                adView = null;
            }
            if (adManagerAdView != null) {
                adManagerAdView.setAdListener(null);
                adManagerAdView.destroy();
                adManagerAdView = null;
            }
            if (fanAdView != null) {
                fanAdView.destroy();
                fanAdView = null;
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
