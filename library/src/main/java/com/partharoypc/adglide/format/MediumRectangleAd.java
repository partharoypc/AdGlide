package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;

import android.app.Activity;
import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.util.WaterfallManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

        private boolean adStatus = true;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
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

        @NonNull
        public Builder build() {
            return this;
        }

        @NonNull
        public Builder load() {
            loadMediumRectangleAd();
            return this;
        }

        @NonNull
        public Builder status(boolean adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        @NonNull
        public Builder network(@NonNull String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        @NonNull
        public Builder network(AdGlideNetwork network) {
            return network(network.getValue());
        }

        @Nullable
        public Builder backup(@Nullable String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            this.waterfallManager = new WaterfallManager(backupAdNetwork);
            return this;
        }

        @Nullable
        public Builder backup(AdGlideNetwork backupAdNetwork) {
            return backup(backupAdNetwork.getValue());
        }

        @Nullable
        public Builder backups(String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks.length > 0) {
                this.backupAdNetwork = backupAdNetworks[0];
            }
            return this;
        }

        @Nullable
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return backups(AdGlideNetwork.toStringArray(backupAdNetworks));
        }

        @NonNull
        public Builder adMobId(@NonNull String adMobBannerId) {
            this.adMobBannerId = adMobBannerId;
            return this;
        }

        @NonNull
        public Builder metaId(@NonNull String metaBannerId) {
            this.metaBannerId = metaBannerId;
            return this;
        }

        @NonNull
        public Builder unityId(@NonNull String unityBannerId) {
            this.unityBannerId = unityBannerId;
            return this;
        }

        @NonNull
        public Builder appLovinId(@NonNull String appLovinBannerId) {
            this.appLovinBannerId = appLovinBannerId;
            return this;
        }

        @NonNull
        public Builder zoneId(@NonNull String appLovinBannerZoneId) {
            this.appLovinBannerZoneId = appLovinBannerZoneId;
            return this;
        }

        @NonNull
        public Builder ironSourceId(@NonNull String ironSourceBannerId) {
            this.ironSourceBannerId = ironSourceBannerId;
            return this;
        }

        @NonNull
        public Builder placement(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        @NonNull
        public Builder darkTheme(boolean darkTheme) {
            this.darkTheme = darkTheme;
            return this;
        }

        @NonNull
        public Builder legacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public void loadMediumRectangleAd() {
            loadMediumRectangleAdMain(false);
        }

        private void loadMediumRectangleAdMain(boolean isBackup) {
            try {
                if (adStatus && placementStatus != 0) {
                    if (isBackup) {
                        if (!Tools.isNetworkAvailable(activity)) {
                            Log.e(TAG, "Internet connection not available. Skipping Backup Medium Rectangle Ad load.");
                            return;
                        }
                        if (waterfallManager == null) {
                            if (backupAdNetwork != null && !backupAdNetwork.isEmpty()) {
                                waterfallManager = new WaterfallManager(backupAdNetwork);
                            } else {
                                return;
                            }
                        }
                        String networkToLoad = waterfallManager.getNext();
                        if (networkToLoad == null) {
                            Log.d(TAG, "All backup medium rectangle ads failed to load");
                            return;
                        }
                        backupAdNetwork = networkToLoad;
                    } else {
                        if (!Tools.isNetworkAvailable(activity)) {
                            Log.e(TAG, "Internet connection not available. Skipping Primary Medium Rectangle Ad load.");
                            return;
                        }
                        if (waterfallManager != null) {
                            waterfallManager.reset();
                        }
                    }

                    String network = isBackup ? backupAdNetwork : adNetwork;
                    loadAdFromNetwork(network);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadMediumRectangleAdMain: " + e.getMessage());
            }
        }

        private void loadAdFromNetwork(String networkToLoad) {
            try {
                switch (networkToLoad) {
                    case ADMOB:
                    case META_BIDDING_ADMOB:
                        if (!com.partharoypc.adglide.util.AdMobRateLimiter.isRequestAllowed(adMobBannerId)) {
                            loadBackupMediumRectangleAd();
                            break;
                        }
                        FrameLayout adContainerView = activity.findViewById(R.id.ad_mob_banner_view_container);
                        if (adContainerView != null) {
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
                                            com.partharoypc.adglide.util.AdMobRateLimiter.resetCooldown(adMobBannerId);
                                            adContainerView.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                            if (adError
                                                    .getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                                                com.partharoypc.adglide.util.AdMobRateLimiter
                                                        .recordFailure(adMobBannerId);
                                            }
                                            adContainerView.setVisibility(View.GONE);
                                            loadBackupMediumRectangleAd();
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.e(TAG, "Error inside adContainerView.post: " + e.getMessage());
                                    loadBackupMediumRectangleAd();
                                }
                            });
                        } else {
                            loadBackupMediumRectangleAd();
                        }
                        break;

                    case META:
                        metaAdView = new com.facebook.ads.AdView(activity, metaBannerId, AdSize.RECTANGLE_HEIGHT_250);
                        RelativeLayout metaAdViewContainer = activity.findViewById(R.id.meta_banner_view_container);
                        if (metaAdViewContainer != null) {
                            metaAdViewContainer.removeAllViews();
                            metaAdViewContainer.addView(metaAdView);
                            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                                @Override
                                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                                    metaAdViewContainer.setVisibility(View.GONE);
                                    Log.d(TAG, "Error load FAN : " + adError.getErrorMessage());
                                    loadBackupMediumRectangleAd();
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
                        } else {
                            loadBackupMediumRectangleAd();
                        }
                        break;
                    default:
                        loadBackupMediumRectangleAd();
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadAdFromNetwork: " + e.getMessage());
                loadBackupMediumRectangleAd();
            }
        }

        public void loadBackupMediumRectangleAd() {
            loadMediumRectangleAdMain(true);
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
        }

    }

}
