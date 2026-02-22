package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.util.Constant;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;
import com.startapp.sdk.ads.nativead.NativeAdDetails;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and displaying native ads from multiple ad networks.
 * Supports programmatic container support and smooth fade-in transitions.
 */
public class NativeAd {

    public static class Builder {

        private static final String TAG = "AdGlide";
        private final Activity activity;
        private ViewGroup container;
        private LinearLayout nativeAdViewContainer;

        private com.google.android.gms.ads.nativead.NativeAd adMobNativeAd;
        private com.facebook.ads.NativeAd metaNativeAd;
        private MaxNativeAdLoader nativeAdLoader;
        private MaxAd maxNativeAd;
        private StartAppNativeAd startAppNativeAd;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private String adMobNativeId = "";
        private String metaNativeId = "";
        private String appLovinNativeId = "";
        private String wortiseNativeId = "";
        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;
        private String nativeAdStyle = "";
        private int paddingLeft, paddingTop, paddingRight, paddingBottom;

        private int nativeBackgroundLight = R.color.adglide_color_native_background_light;
        private int nativeBackgroundDark = R.color.adglide_color_native_background_dark;

        public Builder(@NonNull Activity activity) {
            this.activity = activity;
        }

        @NonNull
        public Builder setContainer(@NonNull ViewGroup container) {
            this.container = container;
            return this;
        }

        @NonNull
        public Builder build() {
            loadNativeAd();
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
            if (this.waterfallManager == null) {
                this.waterfallManager = new WaterfallManager();
            }
            this.waterfallManager.addNetwork(backupAdNetwork);
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
        public Builder setAdMobNativeId(@NonNull String adMobNativeId) {
            this.adMobNativeId = adMobNativeId;
            return this;
        }

        @NonNull
        public Builder setMetaNativeId(@NonNull String metaNativeId) {
            this.metaNativeId = metaNativeId;
            return this;
        }

        @NonNull
        public Builder setAppLovinNativeId(@NonNull String appLovinNativeId) {
            this.appLovinNativeId = appLovinNativeId;
            return this;
        }

        @NonNull
        public Builder setWortiseNativeId(@NonNull String wortiseNativeId) {
            this.wortiseNativeId = wortiseNativeId;
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
        public Builder setNativeAdStyle(@NonNull String nativeAdStyle) {
            this.nativeAdStyle = nativeAdStyle;
            return this;
        }

        @NonNull
        public Builder setNativeAdBackgroundColor(int light, int dark) {
            this.nativeBackgroundLight = light;
            this.nativeBackgroundDark = dark;
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

        // --- Core Loading ---

        public void loadNativeAd() {
            loadNativeAdInternal(false);
        }

        private void loadNativeAdInternal(boolean isBackup) {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    if (container == null) {
                        Log.e(TAG, "Native container is null.");
                        return;
                    }

                    String network = isBackup ? backupAdNetwork : adNetwork;

                    // 1. Prepare Container
                    container.removeAllViews();
                    container.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                    int layoutRes = getLayoutRes();
                    View layout = activity.getLayoutInflater().inflate(layoutRes, container, true);
                    initializeViews(layout);

                    // 2. Load Network
                    switch (network) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            loadAdMobNative();
                            break;
                        case META:
                            loadMetaNative();
                            break;
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX:
                            loadAppLovinMaxNative();
                            break;
                        case STARTAPP:
                            loadStartAppNative();
                            break;
                        default:
                            loadBackupNativeAd();
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading native: " + e.getMessage());
                loadBackupNativeAd();
            }
        }

        private int getLayoutRes() {
            switch (nativeAdStyle) {
                case Constant.STYLE_SMALL:
                    return R.layout.adglide_view_native_ad_small;
                case Constant.STYLE_NEWS:
                    return R.layout.adglide_view_native_ad_news;
                case Constant.STYLE_RADIO:
                    return R.layout.adglide_view_native_ad_radio;
                case Constant.STYLE_VIDEO_SMALL:
                    return R.layout.adglide_view_native_ad_video_small;
                case Constant.STYLE_VIDEO_LARGE:
                    return R.layout.adglide_view_native_ad_video_large;
                case Constant.STYLE_MEDIUM:
                default:
                    return R.layout.adglide_view_native_ad_medium;
            }
        }

        private void initializeViews(View root) {
            nativeAdViewContainer = root.findViewById(R.id.native_ad_view_container);

            ViewStub adMobStub = root.findViewById(R.id.ad_mob_native_ad_stub);
            if (adMobStub != null)
                adMobStub.inflate();

            ViewStub metaStub = root.findViewById(R.id.meta_native_ad_stub);
            if (metaStub != null)
                metaStub.inflate();
        }

        private void loadAdMobNative() {
            final FrameLayout adMobNativeAdContainer = container.findViewById(R.id.ad_mob_native_ad_container);
            if (adMobNativeAdContainer == null)
                return;

            AdLoader adLoader = new AdLoader.Builder(activity, adMobNativeId)
                    .forNativeAd(nativeAd -> {
                        if (adMobNativeAd != null)
                            adMobNativeAd.destroy();
                        adMobNativeAd = nativeAd;

                        // Handle <merge> tag inflation
                        activity.getLayoutInflater().inflate(
                                R.layout.adglide_ad_mob_medium_template_view, adMobNativeAdContainer, true);
                        NativeAdView nativeAdView = adMobNativeAdContainer.findViewById(R.id.native_ad_view);

                        if (nativeAdView != null) {
                            populateAdMobView(nativeAd, nativeAdView);
                            Tools.fadeIn(nativeAdViewContainer);
                        }
                    })
                    .withAdListener(new com.google.android.gms.ads.AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull com.google.android.gms.ads.LoadAdError adError) {
                            loadBackupNativeAd();
                        }
                    })
                    .build();
            adLoader.loadAd(Tools.getAdRequest(activity, legacyGDPR));
        }

        private void loadMetaNative() {
            NativeAdLayout metaNativeAdLayout = container.findViewById(R.id.meta_native_ad_container);
            if (metaNativeAdLayout == null)
                return;

            metaNativeAd = new com.facebook.ads.NativeAd(activity, metaNativeId);
            metaNativeAd.loadAd(metaNativeAd.buildLoadAdConfig()
                    .withAdListener(new NativeAdListener() {
                        @Override
                        public void onAdLoaded(Ad ad) {
                            if (metaNativeAd != ad)
                                return;
                            metaNativeAd.unregisterView();

                            int innerLayout;
                            switch (nativeAdStyle) {
                                case Constant.STYLE_NEWS:
                                    innerLayout = R.layout.adglide_meta_news_template_view;
                                    break;
                                case Constant.STYLE_VIDEO_SMALL:
                                    innerLayout = R.layout.adglide_meta_video_small_template_view;
                                    break;
                                case Constant.STYLE_VIDEO_LARGE:
                                    innerLayout = R.layout.adglide_meta_video_large_template_view;
                                    break;
                                default:
                                    innerLayout = R.layout.adglide_meta_radio_template_view;
                                    break;
                            }

                            View nativeAdView = activity.getLayoutInflater().inflate(innerLayout, metaNativeAdLayout,
                                    false);
                            populateMetaView(nativeAdView, metaNativeAdLayout);

                            metaNativeAdLayout.removeAllViews();
                            metaNativeAdLayout.addView(nativeAdView);
                            Tools.fadeIn(nativeAdViewContainer);
                        }

                        @Override
                        public void onError(Ad ad, AdError adError) {
                            loadBackupNativeAd();
                        }

                        @Override
                        public void onAdClicked(Ad ad) {
                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {
                        }

                        @Override
                        public void onMediaDownloaded(Ad ad) {
                        }
                    }).build());
        }

        private void loadAppLovinMaxNative() {
            FrameLayout appLovinNativeAdContainer = container.findViewById(R.id.app_lovin_native_ad_container);
            if (appLovinNativeAdContainer == null)
                return;

            nativeAdLoader = new MaxNativeAdLoader(appLovinNativeId, activity);
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(MaxNativeAdView nativeAdView, MaxAd ad) {
                    if (maxNativeAd != null)
                        nativeAdLoader.destroy(maxNativeAd);
                    maxNativeAd = ad;
                    appLovinNativeAdContainer.removeAllViews();
                    appLovinNativeAdContainer.addView(nativeAdView);
                    Tools.fadeIn(nativeAdViewContainer);
                }

                @Override
                public void onNativeAdLoadFailed(String adUnitId, MaxError error) {
                    loadBackupNativeAd();
                }
            });
            nativeAdLoader.loadAd();
        }

        private void loadStartAppNative() {
            startAppNativeAd = new StartAppNativeAd(activity);
            NativeAdPreferences nativePrefs = new NativeAdPreferences()
                    .setAdsNumber(1)
                    .setAutoBitmapDownload(true)
                    .setPrimaryImageSize(2);

            startAppNativeAd.loadAd(nativePrefs, new AdEventListener() {
                @Override
                public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad ad) {
                    ArrayList<NativeAdDetails> ads = startAppNativeAd.getNativeAds();
                    if (ads != null && !ads.isEmpty()) {
                        View root = container.findViewById(R.id.start_app_native_ad_container);
                        if (root != null) {
                            populateStartAppView(ads.get(0), root);
                            Tools.fadeIn(nativeAdViewContainer);
                        }
                    } else {
                        loadBackupNativeAd();
                    }
                }

                @Override
                public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                    loadBackupNativeAd();
                }
            });
        }

        public void loadBackupNativeAd() {
            if (waterfallManager == null)
                return;
            String next = waterfallManager.getNext();
            if (next == null || next.isEmpty() || next.equals("none"))
                return;

            backupAdNetwork = next;
            loadNativeAdInternal(true);
        }

        // --- View Population ---

        private void populateAdMobView(com.google.android.gms.ads.nativead.NativeAd nativeAd,
                NativeAdView nativeAdView) {
            int bgColor = darkTheme ? nativeBackgroundDark : nativeBackgroundLight;
            nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, bgColor));

            nativeAdView.setMediaView(nativeAdView.findViewById(R.id.media_view));
            nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.primary));
            nativeAdView.setBodyView(nativeAdView.findViewById(R.id.body));
            nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.cta));
            nativeAdView.setIconView(nativeAdView.findViewById(R.id.icon));

            if (nativeAdView.getHeadlineView() != null)
                ((TextView) nativeAdView.getHeadlineView()).setText(nativeAd.getHeadline());

            if (nativeAdView.getBodyView() != null)
                ((TextView) nativeAdView.getBodyView()).setText(nativeAd.getBody());

            if (nativeAdView.getCallToActionView() != null)
                ((Button) nativeAdView.getCallToActionView()).setText(nativeAd.getCallToAction());

            if (nativeAdView.getIconView() != null && nativeAd.getIcon() != null)
                ((ImageView) nativeAdView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());

            nativeAdView.setNativeAd(nativeAd);
        }

        private void populateMetaView(View view, NativeAdLayout layout) {
            int bgColor = darkTheme ? nativeBackgroundDark : nativeBackgroundLight;
            nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, bgColor));

            LinearLayout adChoices = view.findViewById(R.id.ad_choices_container);
            AdOptionsView options = new AdOptionsView(activity, metaNativeAd, layout);
            adChoices.removeAllViews();
            adChoices.addView(options, 0);

            TextView title = view.findViewById(R.id.native_ad_title);
            TextView body = view.findViewById(R.id.native_ad_body);
            Button cta = view.findViewById(R.id.native_ad_call_to_action);
            com.facebook.ads.MediaView media = view.findViewById(R.id.native_ad_media);
            com.facebook.ads.MediaView icon = view.findViewById(R.id.native_ad_icon);

            title.setText(metaNativeAd.getAdvertiserName());
            body.setText(metaNativeAd.getAdBodyText());
            cta.setText(metaNativeAd.getAdCallToAction());

            List<View> clickable = new ArrayList<>();
            clickable.add(title);
            clickable.add(cta);
            clickable.add(media);
            metaNativeAd.registerViewForInteraction(view, icon, media, clickable);
        }

        private void populateStartAppView(NativeAdDetails details, View root) {
            int bgColor = darkTheme ? nativeBackgroundDark : nativeBackgroundLight;
            nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, bgColor));

            TextView title = root.findViewById(R.id.start_app_native_title);
            TextView body = root.findViewById(R.id.start_app_native_description);
            ImageView img = root.findViewById(R.id.start_app_native_image);
            ImageView icon = root.findViewById(R.id.start_app_native_icon);
            Button cta = root.findViewById(R.id.start_app_native_button);

            title.setText(details.getTitle());
            body.setText(details.getDescription());
            img.setImageBitmap(details.getImageBitmap());
            icon.setImageBitmap(details.getSecondaryImageBitmap());
            cta.setText(details.isApp() ? "Install" : "Open");

            details.registerViewForInteraction(root);
        }

        /**
         * Cleans up native resources and detaches from container. Call from
         * onDestroy().
         */
        public void destroyAd() {
            if (adMobNativeAd != null)
                adMobNativeAd.destroy();
            if (metaNativeAd != null)
                metaNativeAd.destroy();
            if (nativeAdLoader != null && maxNativeAd != null)
                nativeAdLoader.destroy(maxNativeAd);

            if (container != null) {
                container.removeAllViews();
            }
        }
    }
}
