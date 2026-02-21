package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.util.Tools;

/**
 * Handles loading and displaying medium rectangle (300x250) ads from multiple
 * ad networks.
 * Supports AdMob, and Facebook Audience Network.
 */
public class MediumRectangleAd {

    public static class Builder {

        private static final String TAG = "AdGlide";
        private final Activity activity;
        private AdView adView;
        private com.facebook.ads.AdView metaAdView;
        private FrameLayout ironSourceBannerView;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private String adMobBannerId = "";
        private String metaBannerId = "";
        private String unityBannerId = "";
        private String appLovinBannerId = "";
        private String appLovinBannerZoneId = "";
        private String ironSourceBannerId = "";
        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;

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

        public void loadBannerAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            FrameLayout adContainerView = activity.findViewById(R.id.ad_mob_banner_view_container);
                            adContainerView.post(() -> {
                                try {
                                    adView = new AdView(activity);
                                    adView.setAdUnitId(adMobBannerId);
                                    adContainerView.removeAllViews();
                                    adContainerView.addView(adView);
                                    adView.setAdSize(Tools.getAdSizeMREC());
                                    adView.loadAd(Tools.getAdRequest(activity, legacyGDPR));
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
                                } catch (Exception e) {
                                    Log.e(TAG, "Error inside adContainerView.post: " + e.getMessage());
                                    loadBackupBannerAd();
                                }
                            });
                            Log.d(TAG, adNetwork + " Banner Ad unit Id : " + adMobBannerId);
                            break;

                        case META:
                            metaAdView = new com.facebook.ads.AdView(activity, metaBannerId,
                                    AdSize.RECTANGLE_HEIGHT_250);
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

                        default:
                            break;
                    }
                    Log.d(TAG, "Banner Ad is enabled");
                } else {
                    Log.d(TAG, "Banner Ad is disabled");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadBannerAd: " + e.getMessage());
                loadBackupBannerAd();
            }
        }

        public void loadBackupBannerAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    switch (backupAdNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            FrameLayout adContainerView = activity.findViewById(R.id.ad_mob_banner_view_container);
                            adContainerView.post(() -> {
                                try {
                                    adView = new AdView(activity);
                                    adView.setAdUnitId(adMobBannerId);
                                    adContainerView.removeAllViews();
                                    adContainerView.addView(adView);
                                    adView.setAdSize(Tools.getAdSizeMREC());
                                    adView.loadAd(Tools.getAdRequest(activity, legacyGDPR));
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
                                } catch (Exception e) {
                                    Log.e(TAG, "Error inside adContainerView.post: " + e.getMessage());
                                }
                            });
                            Log.d(TAG, adNetwork + " Banner Ad unit Id : " + adMobBannerId);
                            break;

                        case META:
                            metaAdView = new com.facebook.ads.AdView(activity, metaBannerId,
                                    AdSize.RECTANGLE_HEIGHT_250);
                            RelativeLayout metaAdViewContainer = activity.findViewById(R.id.meta_banner_view_container);
                            metaAdViewContainer.addView(metaAdView);
                            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                                @Override
                                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                                    metaAdViewContainer.setVisibility(View.GONE);
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

                        default:
                            break;
                    }
                    Log.d(TAG, "Banner Ad is enabled");
                } else {
                    Log.d(TAG, "Banner Ad is disabled");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadBackupBannerAd: " + e.getMessage());
            }
        }

        /**
         * Destroys all loaded medium rectangle ad views and removes them from their
         * containers.
         * Call this method in your Activity's {@code onDestroy()} to prevent memory
         * leaks.
         */
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
        }

    }

}
