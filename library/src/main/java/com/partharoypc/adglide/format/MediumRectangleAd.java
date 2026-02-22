package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE;
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
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.ads.banner.BannerListener;

/**
 * Handles loading and displaying medium rectangle (300x250) ads from multiple
 * ad networks.
 */
public class MediumRectangleAd {

    public static class Builder {

        private static final String TAG = "AdGlide";
        private final Activity activity;
        private View view;
        private ViewGroup container;
        private int paddingLeft, paddingTop, paddingRight, paddingBottom;

        private AdView adMobBannerAd;
        private com.facebook.ads.AdView metaBannerAd;
        private com.applovin.mediation.ads.MaxAdView appLovinMaxBannerAd;
        private com.ironsource.mediationsdk.IronSourceBannerLayout ironSourceBannerLayout;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private String adMobBannerId = "";
        private String metaBannerId = "";
        private String unityBannerId = "";
        private String appLovinBannerId = "";
        private String ironSourceBannerId = "";
        private String wortiseBannerId = "";
        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        @NonNull
        public Builder build() {
            loadBannerAd();
            return this;
        }

        @NonNull
        public Builder setAdStatus(@NonNull String adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        @NonNull
        public Builder setAdNetwork(@NonNull String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        @NonNull
        public Builder setBackupAdNetwork(@Nullable String backupAdNetwork) {
            this.backupAdNetwork = (backupAdNetwork != null) ? backupAdNetwork : "";
            if (waterfallManager == null) {
                waterfallManager = new WaterfallManager();
            }
            waterfallManager.addNetwork(backupAdNetwork);
            return this;
        }

        @NonNull
        public Builder setBackupAdNetworks(@Nullable String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks != null && backupAdNetworks.length > 0) {
                this.backupAdNetwork = (backupAdNetworks[0] != null) ? backupAdNetworks[0] : "";
            }
            return this;
        }

        @NonNull
        public Builder setAdMobBannerId(@NonNull String adMobBannerId) {
            this.adMobBannerId = adMobBannerId;
            return this;
        }

        @NonNull
        public Builder setMetaBannerId(@NonNull String metaBannerId) {
            this.metaBannerId = metaBannerId;
            return this;
        }

        @NonNull
        public Builder setContainer(@NonNull ViewGroup container) {
            this.container = container;
            return this;
        }

        @NonNull
        public Builder setView(View view) {
            if (view instanceof ViewGroup) {
                this.container = (ViewGroup) view;
            }
            this.view = view;
            return this;
        }

        @NonNull
        public Builder setUnityBannerId(@NonNull String unityBannerId) {
            this.unityBannerId = unityBannerId;
            return this;
        }

        @NonNull
        public Builder setAppLovinBannerId(@NonNull String appLovinBannerId) {
            this.appLovinBannerId = appLovinBannerId;
            return this;
        }

        @NonNull
        public Builder setIronSourceBannerId(@NonNull String ironSourceBannerId) {
            this.ironSourceBannerId = ironSourceBannerId;
            return this;
        }

        @NonNull
        public Builder setWortiseBannerId(@NonNull String wortiseBannerId) {
            this.wortiseBannerId = wortiseBannerId;
            return this;
        }

        @NonNull
        public Builder setPlacementStatus(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        @NonNull
        public Builder setDarkTheme(boolean darkTheme) {
            this.darkTheme = darkTheme;
            return this;
        }

        @NonNull
        public Builder setLegacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        @NonNull
        public Builder setPadding(int left, int top, int right, int bottom) {
            this.paddingLeft = left;
            this.paddingTop = top;
            this.paddingRight = right;
            this.paddingBottom = bottom;
            return this;
        }

        public void loadBannerAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    if (view == null && container == null) {
                        Log.e(TAG, "MREC Container view is null. Cannot load ad.");
                        return;
                    }

                    final ViewGroup adContainer = (container != null) ? container : (ViewGroup) view;
                    adContainer.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            adMobBannerAd = new AdView(activity);
                            adMobBannerAd.setAdUnitId(adMobBannerId);
                            adMobBannerAd.setAdSize(Tools.getAdSizeMREC());
                            adContainer.removeAllViews();
                            adContainer.addView(adMobBannerAd);
                            adMobBannerAd.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                            adMobBannerAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdLoaded() {
                                    Tools.fadeIn(adContainer);
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                    adContainer.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }
                            });
                            break;

                        case META:
                            metaBannerAd = new com.facebook.ads.AdView(activity, metaBannerId,
                                    AdSize.RECTANGLE_HEIGHT_250);
                            adContainer.removeAllViews();
                            adContainer.addView(metaBannerAd);
                            metaBannerAd.loadAd(metaBannerAd.buildLoadAdConfig()
                                    .withAdListener(new com.facebook.ads.AdListener() {
                                        @Override
                                        public void onError(Ad ad, com.facebook.ads.AdError adError) {
                                            adContainer.setVisibility(View.GONE);
                                            loadBackupBannerAd();
                                        }

                                        @Override
                                        public void onAdLoaded(Ad ad) {
                                            Tools.fadeIn(adContainer);
                                        }

                                        @Override
                                        public void onAdClicked(Ad ad) {
                                        }

                                        @Override
                                        public void onLoggingImpression(Ad ad) {
                                        }
                                    }).build());
                            break;

                        case STARTAPP:
                            Banner startAppBanner = new Banner(activity, new BannerListener() {
                                @Override
                                public void onReceiveAd(View view) {
                                    Tools.fadeIn(adContainer);
                                }

                                @Override
                                public void onFailedToReceiveAd(View view) {
                                    adContainer.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onImpression(View view) {
                                }

                                @Override
                                public void onClick(View view) {
                                }
                            });
                            adContainer.removeAllViews();
                            adContainer.addView(startAppBanner);
                            startAppBanner.loadAd();
                            break;

                        case UNITY:
                            com.unity3d.services.banners.BannerView unityBannerView = new com.unity3d.services.banners.BannerView(
                                    activity, unityBannerId,
                                    new com.unity3d.services.banners.UnityBannerSize(300, 250));
                            unityBannerView.setListener(new com.unity3d.services.banners.BannerView.IListener() {
                                @Override
                                public void onBannerLoaded(com.unity3d.services.banners.BannerView bannerAdView) {
                                    Tools.fadeIn(adContainer);
                                }

                                @Override
                                public void onBannerFailedToLoad(com.unity3d.services.banners.BannerView bannerAdView,
                                        com.unity3d.services.banners.BannerErrorInfo errorInfo) {
                                    adContainer.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onBannerClick(com.unity3d.services.banners.BannerView bannerAdView) {
                                }

                                @Override
                                public void onBannerLeftApplication(
                                        com.unity3d.services.banners.BannerView bannerAdView) {
                                }

                                @Override
                                public void onBannerShown(com.unity3d.services.banners.BannerView bannerAdView) {
                                }
                            });
                            adContainer.removeAllViews();
                            adContainer.addView(unityBannerView);
                            unityBannerView.load();
                            break;

                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX:
                            appLovinMaxBannerAd = new MaxAdView(appLovinBannerId,
                                    com.applovin.mediation.MaxAdFormat.MREC, activity);
                            appLovinMaxBannerAd.setListener(new MaxAdViewAdListener() {
                                @Override
                                public void onAdExpanded(MaxAd ad) {
                                }

                                @Override
                                public void onAdCollapsed(MaxAd ad) {
                                }

                                @Override
                                public void onAdLoaded(MaxAd ad) {
                                    Tools.fadeIn(adContainer);
                                }

                                @Override
                                public void onAdLoadFailed(String adUnitId, MaxError error) {
                                    adContainer.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                }

                                @Override
                                public void onAdClicked(MaxAd ad) {
                                }

                                @Override
                                public void onAdHidden(MaxAd ad) {
                                }

                                @Override
                                public void onAdDisplayed(MaxAd ad) {
                                }
                            });
                            int width = ViewGroup.LayoutParams.MATCH_PARENT;
                            int heightPx = AppLovinSdkUtils.dpToPx(activity, 250);
                            appLovinMaxBannerAd.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                            adContainer.removeAllViews();
                            adContainer.addView(appLovinMaxBannerAd);
                            appLovinMaxBannerAd.loadAd();
                            break;

                        case IRONSOURCE:
                        case META_BIDDING_IRONSOURCE:
                            ironSourceBannerLayout = IronSource.createBanner(activity, ISBannerSize.RECTANGLE);
                            ironSourceBannerLayout.setLevelPlayBannerListener(new LevelPlayBannerListener() {
                                @Override
                                public void onAdLoaded(AdInfo adInfo) {
                                    Tools.fadeIn(adContainer);
                                }

                                @Override
                                public void onAdLoadFailed(IronSourceError error) {
                                    adContainer.setVisibility(View.GONE);
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
                            adContainer.removeAllViews();
                            adContainer.addView(ironSourceBannerLayout);
                            IronSource.loadBanner(ironSourceBannerLayout, ironSourceBannerId);
                            break;

                        case WORTISE:
                            com.wortise.ads.banner.BannerAd wortiseMrec = new com.wortise.ads.banner.BannerAd(activity);
                            wortiseMrec.setAdSize(com.wortise.ads.AdSize.HEIGHT_250);
                            wortiseMrec.setAdUnitId(wortiseBannerId);
                            wortiseMrec.setListener(new com.wortise.ads.banner.BannerAd.Listener() {
                                @Override
                                public void onBannerLoaded(@NonNull com.wortise.ads.banner.BannerAd ad) {
                                    Tools.fadeIn(adContainer);
                                }

                                @Override
                                public void onBannerFailedToLoad(@NonNull com.wortise.ads.banner.BannerAd ad,
                                        @NonNull com.wortise.ads.AdError error) {
                                    adContainer.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onBannerClicked(@NonNull com.wortise.ads.banner.BannerAd ad) {
                                }

                                @Override
                                public void onBannerImpression(@NonNull com.wortise.ads.banner.BannerAd ad) {
                                }

                                @Override
                                public void onBannerRevenuePaid(@NonNull com.wortise.ads.banner.BannerAd ad,
                                        @NonNull com.wortise.ads.RevenueData revenueData) {
                                }
                            });
                            adContainer.removeAllViews();
                            adContainer.addView(wortiseMrec);
                            wortiseMrec.loadAd();
                            break;

                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadBannerAd: " + e.getMessage());
                loadBackupBannerAd();
            }
        }

        public void loadBackupBannerAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    if (view == null && container == null)
                        return;

                    final ViewGroup adContainer = (container != null) ? container : (ViewGroup) view;
                    String network = waterfallManager != null ? waterfallManager.getNext()
                            : (backupAdNetwork.isEmpty() ? "" : backupAdNetwork);

                    if (network == null || network.isEmpty() || network.equals("none")) {
                        adContainer.setVisibility(View.GONE);
                        return;
                    }

                    switch (network) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            adMobBannerAd = new AdView(activity);
                            adMobBannerAd.setAdUnitId(adMobBannerId);
                            adMobBannerAd.setAdSize(Tools.getAdSizeMREC());
                            adContainer.removeAllViews();
                            adContainer.addView(adMobBannerAd);
                            adMobBannerAd.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                            adMobBannerAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdLoaded() {
                                    Tools.fadeIn(adContainer);
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                    adContainer.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }
                            });
                            break;

                        case META:
                            metaBannerAd = new com.facebook.ads.AdView(activity, metaBannerId,
                                    AdSize.RECTANGLE_HEIGHT_250);
                            adContainer.removeAllViews();
                            adContainer.addView(metaBannerAd);
                            metaBannerAd.loadAd(metaBannerAd.buildLoadAdConfig()
                                    .withAdListener(new com.facebook.ads.AdListener() {
                                        @Override
                                        public void onError(Ad ad, com.facebook.ads.AdError adError) {
                                            adContainer.setVisibility(View.GONE);
                                            loadBackupBannerAd();
                                        }

                                        @Override
                                        public void onAdLoaded(Ad ad) {
                                            Tools.fadeIn(adContainer);
                                        }

                                        @Override
                                        public void onAdClicked(Ad ad) {
                                        }

                                        @Override
                                        public void onLoggingImpression(Ad ad) {
                                        }
                                    }).build());
                            break;

                        case STARTAPP:
                            Banner startAppBanner = new Banner(activity, new BannerListener() {
                                @Override
                                public void onReceiveAd(View view) {
                                    Tools.fadeIn(adContainer);
                                }

                                @Override
                                public void onFailedToReceiveAd(View view) {
                                    adContainer.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onImpression(View view) {
                                }

                                @Override
                                public void onClick(View view) {
                                }
                            });
                            adContainer.removeAllViews();
                            adContainer.addView(startAppBanner);
                            startAppBanner.loadAd();
                            break;

                        case UNITY:
                            com.unity3d.services.banners.BannerView unityBannerView = new com.unity3d.services.banners.BannerView(
                                    activity, unityBannerId,
                                    new com.unity3d.services.banners.UnityBannerSize(300, 250));
                            unityBannerView.setListener(new com.unity3d.services.banners.BannerView.IListener() {
                                @Override
                                public void onBannerLoaded(com.unity3d.services.banners.BannerView bannerAdView) {
                                    Tools.fadeIn(adContainer);
                                }

                                @Override
                                public void onBannerFailedToLoad(com.unity3d.services.banners.BannerView bannerAdView,
                                        com.unity3d.services.banners.BannerErrorInfo errorInfo) {
                                    adContainer.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onBannerClick(com.unity3d.services.banners.BannerView bannerAdView) {
                                }

                                @Override
                                public void onBannerLeftApplication(
                                        com.unity3d.services.banners.BannerView bannerAdView) {
                                }

                                @Override
                                public void onBannerShown(com.unity3d.services.banners.BannerView bannerAdView) {
                                }
                            });
                            adContainer.removeAllViews();
                            adContainer.addView(unityBannerView);
                            unityBannerView.load();
                            break;

                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX:
                            appLovinMaxBannerAd = new MaxAdView(appLovinBannerId,
                                    com.applovin.mediation.MaxAdFormat.MREC, activity);
                            appLovinMaxBannerAd.setListener(new MaxAdViewAdListener() {
                                @Override
                                public void onAdExpanded(MaxAd ad) {
                                }

                                @Override
                                public void onAdCollapsed(MaxAd ad) {
                                }

                                @Override
                                public void onAdLoaded(MaxAd ad) {
                                    Tools.fadeIn(adContainer);
                                }

                                @Override
                                public void onAdLoadFailed(String adUnitId, MaxError error) {
                                    adContainer.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                }

                                @Override
                                public void onAdClicked(MaxAd ad) {
                                }

                                @Override
                                public void onAdHidden(MaxAd ad) {
                                }

                                @Override
                                public void onAdDisplayed(MaxAd ad) {
                                }
                            });
                            int width = ViewGroup.LayoutParams.MATCH_PARENT;
                            int heightPx = AppLovinSdkUtils.dpToPx(activity, 250);
                            appLovinMaxBannerAd.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                            adContainer.removeAllViews();
                            adContainer.addView(appLovinMaxBannerAd);
                            appLovinMaxBannerAd.loadAd();
                            break;

                        case IRONSOURCE:
                        case META_BIDDING_IRONSOURCE:
                            ironSourceBannerLayout = IronSource.createBanner(activity, ISBannerSize.RECTANGLE);
                            ironSourceBannerLayout.setLevelPlayBannerListener(new LevelPlayBannerListener() {
                                @Override
                                public void onAdLoaded(AdInfo adInfo) {
                                    Tools.fadeIn(adContainer);
                                }

                                @Override
                                public void onAdLoadFailed(IronSourceError error) {
                                    adContainer.setVisibility(View.GONE);
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
                            adContainer.removeAllViews();
                            adContainer.addView(ironSourceBannerLayout);
                            IronSource.loadBanner(ironSourceBannerLayout, ironSourceBannerId);
                            break;

                        case WORTISE:
                            com.wortise.ads.banner.BannerAd wortiseMrec = new com.wortise.ads.banner.BannerAd(activity);
                            wortiseMrec.setAdSize(com.wortise.ads.AdSize.HEIGHT_250);
                            wortiseMrec.setAdUnitId(wortiseBannerId);
                            wortiseMrec.setListener(new com.wortise.ads.banner.BannerAd.Listener() {
                                @Override
                                public void onBannerLoaded(@NonNull com.wortise.ads.banner.BannerAd ad) {
                                    Tools.fadeIn(adContainer);
                                }

                                @Override
                                public void onBannerFailedToLoad(@NonNull com.wortise.ads.banner.BannerAd ad,
                                        @NonNull com.wortise.ads.AdError error) {
                                    adContainer.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onBannerClicked(@NonNull com.wortise.ads.banner.BannerAd ad) {
                                }

                                @Override
                                public void onBannerImpression(@NonNull com.wortise.ads.banner.BannerAd ad) {
                                }

                                @Override
                                public void onBannerRevenuePaid(@NonNull com.wortise.ads.banner.BannerAd ad,
                                        @NonNull com.wortise.ads.RevenueData revenueData) {
                                }
                            });
                            adContainer.removeAllViews();
                            adContainer.addView(wortiseMrec);
                            wortiseMrec.loadAd();
                            break;

                        default:
                            adContainer.setVisibility(View.GONE);
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadBackupBannerAd: " + e.getMessage());
            }
        }

        /**
         * Cleans up MREC resources and detaches from container. Call from onDestroy().
         */
        public void destroyAd() {
            if (adMobBannerAd != null) {
                adMobBannerAd.setAdListener(null);
                adMobBannerAd.destroy();
                adMobBannerAd = null;
            }

            if (metaBannerAd != null) {
                metaBannerAd.destroy();
                metaBannerAd = null;
            }

            if (appLovinMaxBannerAd != null) {
                appLovinMaxBannerAd.setListener(null);
                appLovinMaxBannerAd.destroy();
                appLovinMaxBannerAd = null;
            }

            if (ironSourceBannerLayout != null) {
                IronSource.destroyBanner(ironSourceBannerLayout);
                ironSourceBannerLayout = null;
            }

            if ((view != null && view instanceof ViewGroup) || container != null) {
                ViewGroup adContainer = (container != null) ? container : (ViewGroup) view;
                adContainer.removeAllViews();
            }
        }

    }

}
