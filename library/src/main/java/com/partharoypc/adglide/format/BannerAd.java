package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
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

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
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

/**
 * Handles loading and displaying banner ads from 8 ad networks with waterfall
 * fallback.
 * <p>
 * Supports adaptive banners (AdMob), collapsible banners, and programmatic
 * container integration.
 * </p>
 */
public class BannerAd {

    public static class Builder {

        private static final String TAG = "AdGlide";
        private final Activity activity;
        private ViewGroup container;
        private int paddingLeft, paddingTop, paddingRight, paddingBottom;

        private AdView adMobAdView;
        private com.facebook.ads.AdView metaAdView;
        private BannerView unityBannerAd;
        private MaxAdView appLovinMaxBannerAd;
        private IronSourceBannerLayout ironSourceBannerLayout;
        private Banner startAppBannerAd;
        private com.wortise.ads.banner.BannerAd wortiseBannerAd;

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
        private boolean collapsibleBanner = false;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        /**
         * Sets the ViewGroup container where the banner will be inflated and displayed.
         * Recommended for professional integration.
         */
        @NonNull
        public Builder setContainer(@NonNull ViewGroup container) {
            this.container = container;
            return this;
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
        public Builder setBackupAdNetworks(String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks.length > 0) {
                this.backupAdNetwork = backupAdNetworks[0];
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
        public Builder setIsCollapsibleBanner(boolean collapsibleBanner) {
            this.collapsibleBanner = collapsibleBanner;
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

        // --- Core Loading Logic ---

        public void loadBannerAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    if (container == null) {
                        Log.e(TAG, "Banner container is null. Cannot load ad.");
                        return;
                    }

                    // Pre-inflate the ad network specific containers if using unified layout
                    // Or just use the container directly if it's meant for simple placement.
                    // To maintain professional structure, we inflate our internal banner layout
                    // first.
                    container.removeAllViews();
                    container.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                    activity.getLayoutInflater().inflate(R.layout.adglide_view_banner_ad, container, true);

                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            loadAdMobBanner();
                            break;
                        case META:
                            loadMetaBanner();
                            break;
                        case UNITY:
                            loadUnityBanner();
                            break;
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX:
                            loadAppLovinMaxBanner();
                            break;
                        case IRONSOURCE:
                        case META_BIDDING_IRONSOURCE:
                            loadIronSourceBanner();
                            break;
                        case STARTAPP:
                            loadStartAppBanner();
                            break;
                        case WORTISE:
                            loadWortiseBanner();
                            break;
                        default:
                            loadBackupBannerAd();
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading banner: " + e.getMessage());
                loadBackupBannerAd();
            }
        }

        private void loadAdMobBanner() {
            FrameLayout adContainer = container.findViewById(R.id.ad_mob_banner_view_container);
            if (adContainer == null)
                return;

            adMobAdView = new AdView(activity);
            adMobAdView.setAdUnitId(adMobBannerId);
            adMobAdView.setAdSize(Tools.getAdSize(activity));
            adContainer.removeAllViews();
            adContainer.addView(adMobAdView);

            adMobAdView.loadAd(Tools.getAdRequest(collapsibleBanner));
            adMobAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    Tools.fadeIn(adContainer);
                    Log.d(TAG, "AdMob Banner loaded");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                    adContainer.setVisibility(View.GONE);
                    loadBackupBannerAd();
                }
            });
        }

        private void loadMetaBanner() {
            RelativeLayout adContainer = container.findViewById(R.id.meta_banner_view_container);
            if (adContainer == null)
                return;

            metaAdView = new com.facebook.ads.AdView(activity, metaBannerId, AdSize.BANNER_HEIGHT_50);
            adContainer.removeAllViews();
            adContainer.addView(metaAdView);

            metaAdView.loadAd(metaAdView.buildLoadAdConfig()
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
        }

        private void loadUnityBanner() {
            RelativeLayout adContainer = container.findViewById(R.id.unity_banner_view_container);
            if (adContainer == null)
                return;

            unityBannerAd = new BannerView(activity, unityBannerId, new UnityBannerSize(320, 50));
            unityBannerAd.setListener(new BannerView.IListener() {
                @Override
                public void onBannerLoaded(BannerView bannerAdView) {
                    Tools.fadeIn(adContainer);
                }

                @Override
                public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
                    adContainer.setVisibility(View.GONE);
                    loadBackupBannerAd();
                }

                @Override
                public void onBannerClick(BannerView bannerAdView) {
                }

                @Override
                public void onBannerLeftApplication(BannerView bannerAdView) {
                }

                @Override
                public void onBannerShown(BannerView bannerAdView) {
                }
            });
            adContainer.removeAllViews();
            adContainer.addView(unityBannerAd);
            unityBannerAd.load();
        }

        private void loadAppLovinMaxBanner() {
            RelativeLayout adContainer = container.findViewById(R.id.app_lovin_banner_view_container);
            if (adContainer == null)
                return;

            appLovinMaxBannerAd = new MaxAdView(appLovinBannerId, activity);
            appLovinMaxBannerAd.setListener(new MaxAdViewAdListener() {
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
                public void onAdDisplayed(MaxAd ad) {
                }

                @Override
                public void onAdHidden(MaxAd ad) {
                }

                @Override
                public void onAdClicked(MaxAd ad) {
                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                }

                @Override
                public void onAdExpanded(MaxAd ad) {
                }

                @Override
                public void onAdCollapsed(MaxAd ad) {
                }
            });

            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int heightPx = AppLovinSdkUtils.dpToPx(activity, 50);
            appLovinMaxBannerAd.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
            adContainer.removeAllViews();
            adContainer.addView(appLovinMaxBannerAd);
            appLovinMaxBannerAd.loadAd();
        }

        private void loadIronSourceBanner() {
            FrameLayout adContainer = container.findViewById(R.id.iron_source_banner_view_container);
            if (adContainer == null)
                return;

            ironSourceBannerLayout = IronSource.createBanner(activity, ISBannerSize.BANNER);
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
        }

        private void loadStartAppBanner() {
            RelativeLayout adContainer = container.findViewById(R.id.start_app_banner_view_container);
            if (adContainer == null)
                return;

            startAppBannerAd = new Banner(activity, new BannerListener() {
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
            adContainer.addView(startAppBannerAd);
            startAppBannerAd.loadAd();
        }

        private void loadWortiseBanner() {
            FrameLayout adContainer = container.findViewById(R.id.wortise_banner_view_container);
            if (adContainer == null)
                return;

            wortiseBannerAd = new com.wortise.ads.banner.BannerAd(activity);
            wortiseBannerAd.setAdSize(com.wortise.ads.AdSize.HEIGHT_50);
            wortiseBannerAd.setAdUnitId(wortiseBannerId);
            wortiseBannerAd.setListener(new com.wortise.ads.banner.BannerAd.Listener() {
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
                        @NonNull com.wortise.ads.RevenueData rev) {
                }
            });
            adContainer.removeAllViews();
            adContainer.addView(wortiseBannerAd);
            wortiseBannerAd.loadAd();
        }

        // --- Waterfall Logic ---

        public void loadBackupBannerAd() {
            if (waterfallManager == null)
                return;

            String nextNet = waterfallManager.getNext();
            if (nextNet == null || nextNet.isEmpty() || nextNet.equals("none"))
                return;

            Log.d(TAG, "Banner failed, trying backup: " + nextNet);
            adNetwork = nextNet;
            loadBannerAd(); // Recursively try next
        }

        /**
         * Cleans up banner resources and detaches from container. Call from
         * onDestroy().
         */
        public void destroyAd() {
            if (adMobAdView != null) {
                adMobAdView.setAdListener(null);
                adMobAdView.destroy();
                adMobAdView = null;
            }
            if (metaAdView != null) {
                metaAdView.destroy();
                metaAdView = null;
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
            if (unityBannerAd != null) {
                unityBannerAd.destroy();
                unityBannerAd = null;
            }
            if (wortiseBannerAd != null) {
                wortiseBannerAd.destroy();
                wortiseBannerAd = null;
            }

            if (container != null) {
                container.removeAllViews();
            }
        }
    }
}
