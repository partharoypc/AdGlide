package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.partharoypc.adglide.R;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.startapp.sdk.ads.nativead.NativeAdDetails;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import com.partharoypc.adglide.util.Constant;
import com.partharoypc.adglide.util.NativeTemplateStyle;
import com.partharoypc.adglide.util.TemplateView;
import com.partharoypc.adglide.util.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and displaying native ads within a Fragment layout.
 * Uses a Builder pattern for configuration.
 */
public class NativeAdFragment {

    public static class Builder {

        private static final String TAG = "AdGlide";
        private final Activity activity;
        private View view;

        private LinearLayout nativeAdViewContainer;

        private MediaView mediaView;
        private com.google.android.gms.ads.nativead.NativeAd adMobNativeAd;
        private TemplateView adMobNativeAdView;
        private LinearLayout adMobNativeBackground;

        private com.facebook.ads.NativeAd metaNativeAd;
        private NativeAdLayout metaNativeAdLayout;

        private View startAppNativeAdView;
        private ImageView startAppNativeImage;
        private ImageView startAppNativeIcon;
        private TextView startAppNativeTitle;
        private TextView startAppNativeDescription;
        private Button startAppNativeButton;
        private LinearLayout startAppNativeBackground;

        private FrameLayout appLovinNativeAd;
        private LinearLayout appLovinDiscoveryMrecAd;
        private FrameLayout wortiseNativeAd;
        private MaxNativeAdLoader nativeAdLoader;
        private MaxAd maxNativeAd;
        private StartAppNativeAd startAppNativeAd;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private String adMobNativeId = "";
        private String metaNativeId = "";
        private String appLovinNativeId = "";
        private String appLovinDiscMrecZoneId = "";
        private String wortiseNativeId = "";
        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;

        private String nativeAdStyle = "";
        private int nativeBackgroundLight = R.color.adglide_color_native_background_light;
        private int nativeBackgroundDark = R.color.adglide_color_native_background_dark;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        @androidx.annotation.NonNull
        public Builder build() {
            return this;
        }

        @androidx.annotation.NonNull
        public Builder load() {
            loadNativeAd();
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setPadding(int left, int top, int right, int bottom) {
            setNativeAdPadding(left, top, right, bottom);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setMargin(int left, int top, int right, int bottom) {
            setNativeAdMargin(left, top, right, bottom);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setBackgroundResource(int drawableBackground) {
            setNativeAdBackgroundResource(drawableBackground);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setView(View view) {
            this.view = view;
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
        public Builder setAdMobNativeId(@androidx.annotation.NonNull String adMobNativeId) {
            this.adMobNativeId = adMobNativeId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setAppLovinDiscoveryMrecZoneId(@androidx.annotation.NonNull String appLovinDiscMrecZoneId) {
            this.appLovinDiscMrecZoneId = appLovinDiscMrecZoneId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setMetaNativeId(@androidx.annotation.NonNull String metaNativeId) {
            this.metaNativeId = metaNativeId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setAppLovinNativeId(@androidx.annotation.NonNull String appLovinNativeId) {
            this.appLovinNativeId = appLovinNativeId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setWortiseNativeId(@androidx.annotation.NonNull String wortiseNativeId) {
            this.wortiseNativeId = wortiseNativeId;
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
        public Builder setNativeAdStyle(@androidx.annotation.NonNull String nativeAdStyle) {
            this.nativeAdStyle = nativeAdStyle;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setNativeAdBackgroundColor(int colorLight, int colorDark) {
            this.nativeBackgroundLight = colorLight;
            this.nativeBackgroundDark = colorDark;
            return this;
        }

        public void loadNativeAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {

                    nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);

                    // Inflate AdMob ViewStub if present
                    android.view.ViewStub adMobStub = view.findViewById(R.id.ad_mob_native_ad_stub);
                    if (adMobStub != null) {
                        adMobStub.inflate();
                    }
                    adMobNativeAdView = view.findViewById(R.id.ad_mob_native_ad_container);
                    mediaView = view.findViewById(R.id.media_view);
                    adMobNativeBackground = view.findViewById(R.id.background);

                    // Inflate Meta ViewStub if present
                    android.view.ViewStub metaStub = view.findViewById(R.id.meta_native_ad_stub);
                    if (metaStub != null) {
                        metaStub.inflate();
                    }
                    metaNativeAdLayout = view.findViewById(R.id.meta_native_ad_container);

                    startAppNativeAdView = view.findViewById(R.id.start_app_native_ad_container);
                    startAppNativeImage = view.findViewById(R.id.start_app_native_image);
                    startAppNativeIcon = view.findViewById(R.id.start_app_native_icon);
                    startAppNativeTitle = view.findViewById(R.id.start_app_native_title);
                    startAppNativeDescription = view.findViewById(R.id.start_app_native_description);
                    startAppNativeButton = view.findViewById(R.id.start_app_native_button);
                    startAppNativeButton.setOnClickListener(v -> startAppNativeAdView.performClick());
                    startAppNativeBackground = view.findViewById(R.id.start_app_native_background);

                    appLovinNativeAd = view.findViewById(R.id.app_lovin_native_ad_container);
                    appLovinDiscoveryMrecAd = view.findViewById(R.id.app_lovin_discovery_mrec_ad_container);
                    wortiseNativeAd = view.findViewById(R.id.wortise_native_ad_container);

                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            if (adMobNativeAdView.getVisibility() != View.VISIBLE) {
                                adMobNativeAdView.setVisibility(View.VISIBLE);
                                nativeAdViewContainer.setVisibility(View.VISIBLE);
                                AdLoader adLoader = new AdLoader.Builder(activity, adMobNativeId)
                                        .forNativeAd(NativeAd -> {
                                            if (darkTheme) {
                                                ColorDrawable colorDrawable = new ColorDrawable(
                                                        ContextCompat.getColor(activity, nativeBackgroundDark));
                                                NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                                        .withMainBackgroundColor(colorDrawable).build();
                                                adMobNativeAdView.setStyles(styles);
                                                adMobNativeBackground.setBackgroundResource(nativeBackgroundDark);
                                            } else {
                                                ColorDrawable colorDrawable = new ColorDrawable(
                                                        ContextCompat.getColor(activity, nativeBackgroundLight));
                                                NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                                        .withMainBackgroundColor(colorDrawable).build();
                                                adMobNativeAdView.setStyles(styles);
                                                adMobNativeBackground.setBackgroundResource(nativeBackgroundLight);
                                            }
                                            if (adMobNativeAd != null) {
                                                adMobNativeAd.destroy();
                                            }
                                            adMobNativeAd = NativeAd;
                                            mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                            adMobNativeAdView.setNativeAd(NativeAd);
                                        })
                                        .withAdListener(new AdListener() {
                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                                loadBackupNativeAd();
                                            }
                                        })
                                        .build();
                                adLoader.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                            } else {
                                Log.d(TAG, "AdMob Native Ad has been loaded");
                            }
                            break;

                        case META:
                            try {
                                metaNativeAd = new com.facebook.ads.NativeAd(activity, metaNativeId);
                                NativeAdListener nativeAdListener = new NativeAdListener() {
                                    @Override
                                    public void onMediaDownloaded(com.facebook.ads.Ad ad) {

                                    }

                                    @Override
                                    public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
                                        loadBackupNativeAd();
                                    }

                                    @Override
                                    public void onAdLoaded(com.facebook.ads.Ad ad) {
                                        // Race condition, load() called again before last ad was displayed
                                        metaNativeAdLayout.setVisibility(View.VISIBLE);
                                        nativeAdViewContainer.setVisibility(View.VISIBLE);
                                        if (metaNativeAd != ad) {
                                            return;
                                        }
                                        // Inflate Native Ad into Container
                                        // inflateAd(nativeAd);
                                        metaNativeAd.unregisterView();
                                        // Add the Ad view into the ad container.
                                        LayoutInflater inflater = LayoutInflater.from(activity);
                                        // Inflate the Ad view. The layout referenced should be the one you created in
                                        // the last step.
                                        LinearLayout nativeAdView;

                                        switch (nativeAdStyle) {
                                            case Constant.STYLE_NEWS:
                                            case Constant.STYLE_MEDIUM:
                                                nativeAdView = (LinearLayout) inflater
                                                        .inflate(R.layout.adglide_meta_news_template_view,
                                                                metaNativeAdLayout,
                                                                false);
                                                break;
                                            case Constant.STYLE_VIDEO_SMALL:
                                                nativeAdView = (LinearLayout) inflater.inflate(
                                                        R.layout.adglide_meta_video_small_template_view,
                                                        metaNativeAdLayout,
                                                        false);
                                                break;
                                            case Constant.STYLE_VIDEO_LARGE:
                                                nativeAdView = (LinearLayout) inflater.inflate(
                                                        R.layout.adglide_meta_video_large_template_view,
                                                        metaNativeAdLayout,
                                                        false);
                                                break;
                                            case Constant.STYLE_RADIO:
                                            case Constant.STYLE_SMALL:
                                                nativeAdView = (LinearLayout) inflater.inflate(
                                                        R.layout.adglide_meta_radio_template_view, metaNativeAdLayout,
                                                        false);
                                                break;
                                            default:
                                                nativeAdView = (LinearLayout) inflater.inflate(
                                                        R.layout.adglide_meta_medium_template_view, metaNativeAdLayout,
                                                        false);
                                                break;
                                        }
                                        metaNativeAdLayout.addView(nativeAdView);

                                        // Add the AdOptionsView
                                        LinearLayout adChoicesContainer = nativeAdView
                                                .findViewById(R.id.ad_choices_container);
                                        AdOptionsView adOptionsView = new AdOptionsView(activity, metaNativeAd,
                                                metaNativeAdLayout);
                                        adChoicesContainer.removeAllViews();
                                        adChoicesContainer.addView(adOptionsView, 0);

                                        // Create native UI using the ad metadata.
                                        TextView nativeAdTitle = nativeAdView.findViewById(R.id.native_ad_title);
                                        com.facebook.ads.MediaView nativeAdMedia = nativeAdView
                                                .findViewById(R.id.native_ad_media);
                                        com.facebook.ads.MediaView nativeAdIcon = nativeAdView
                                                .findViewById(R.id.native_ad_icon);
                                        TextView nativeAdSocialContext = nativeAdView
                                                .findViewById(R.id.native_ad_social_context);
                                        TextView nativeAdBody = nativeAdView.findViewById(R.id.native_ad_body);
                                        TextView sponsoredLabel = nativeAdView
                                                .findViewById(R.id.native_ad_sponsored_label);
                                        Button nativeAdCallToAction = nativeAdView
                                                .findViewById(R.id.native_ad_call_to_action);
                                        LinearLayout metaNativeBackground = nativeAdView.findViewById(R.id.ad_unit);

                                        if (darkTheme) {
                                            nativeAdTitle.setTextColor(
                                                    ContextCompat.getColor(activity,
                                                            R.color.adglide_app_lovin_dark_primary_text_color));
                                            nativeAdSocialContext.setTextColor(
                                                    ContextCompat.getColor(activity,
                                                            R.color.adglide_app_lovin_dark_primary_text_color));
                                            sponsoredLabel.setTextColor(ContextCompat.getColor(activity,
                                                    R.color.adglide_app_lovin_dark_secondary_text_color));
                                            nativeAdBody.setTextColor(ContextCompat.getColor(activity,
                                                    R.color.adglide_app_lovin_dark_secondary_text_color));
                                            metaNativeBackground.setBackgroundResource(nativeBackgroundDark);
                                        } else {
                                            metaNativeBackground.setBackgroundResource(nativeBackgroundLight);
                                        }

                                        // Set the Text.
                                        nativeAdTitle.setText(metaNativeAd.getAdvertiserName());
                                        nativeAdBody.setText(metaNativeAd.getAdBodyText());
                                        nativeAdSocialContext.setText(metaNativeAd.getAdSocialContext());
                                        nativeAdCallToAction
                                                .setVisibility(
                                                        metaNativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                                        nativeAdCallToAction.setText(metaNativeAd.getAdCallToAction());
                                        sponsoredLabel.setText(metaNativeAd.getSponsoredTranslation());

                                        // Create a list of clickable views
                                        List<View> clickableViews = new ArrayList<>();
                                        clickableViews.add(nativeAdTitle);
                                        clickableViews.add(sponsoredLabel);
                                        clickableViews.add(nativeAdIcon);
                                        clickableViews.add(nativeAdMedia);
                                        clickableViews.add(nativeAdBody);
                                        clickableViews.add(nativeAdSocialContext);
                                        clickableViews.add(nativeAdCallToAction);

                                        // Register the Title and CTA button to listen for clicks.
                                        metaNativeAd.registerViewForInteraction(nativeAdView, nativeAdIcon,
                                                nativeAdMedia,
                                                clickableViews);

                                    }

                                    @Override
                                    public void onAdClicked(com.facebook.ads.Ad ad) {

                                    }

                                    @Override
                                    public void onLoggingImpression(com.facebook.ads.Ad ad) {

                                    }
                                };

                                com.facebook.ads.NativeAd.NativeLoadAdConfig loadAdConfig = metaNativeAd
                                        .buildLoadAdConfig()
                                        .withAdListener(nativeAdListener).build();
                                metaNativeAd.loadAd(loadAdConfig);
                            } catch (NoClassDefFoundError | Exception e) {
                                Log.e(TAG, "Failed to load Meta native ad. Error: " + e.getMessage());
                                loadBackupNativeAd();
                            }
                            break;

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX:
                            handleAppLovinLoad(null);
                            break;

                        case STARTAPP:
                            handleStartAppLoad(null);
                            break;

                        case WORTISE:
                            handleWortiseLoad(null);
                            break;

                        default:
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadNativeAd: " + e.getMessage());
            }
        }

        private void handleAppLovinLoad(Runnable fallback) {
            try {
                if (appLovinNativeAd.getVisibility() != View.VISIBLE) {
                    nativeAdLoader = new MaxNativeAdLoader(appLovinNativeId, activity);
                    nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                        @Override
                        public void onNativeAdLoaded(MaxNativeAdView nativeAdView, MaxAd ad) {
                            if (maxNativeAd != null)
                                nativeAdLoader.destroy(maxNativeAd);
                            maxNativeAd = ad;
                            appLovinNativeAd.removeAllViews();
                            appLovinNativeAd.addView(nativeAdView);
                            appLovinNativeAd.setVisibility(View.VISIBLE);
                            nativeAdViewContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onNativeAdLoadFailed(String adUnitId, MaxError error) {
                            if (fallback != null)
                                fallback.run();
                        }
                    });
                    nativeAdLoader.loadAd();
                }
            } catch (NoClassDefFoundError | Exception e) {
                Log.e(TAG, "Failed to load AppLovin native ad. Error: " + e.getMessage());
                if (fallback != null)
                    fallback.run();
            }
        }

        private void handleStartAppLoad(Runnable fallback) {
            try {
                startAppNativeAd = new StartAppNativeAd(activity);
                NativeAdPreferences nativePrefs = new NativeAdPreferences()
                        .setAdsNumber(1)
                        .setAutoBitmapDownload(true)
                        .setPrimaryImageSize(2);

                AdEventListener adEventListener = new AdEventListener() {
                    @Override
                    public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad ad) {
                        ArrayList<NativeAdDetails> ads = startAppNativeAd.getNativeAds();
                        if (ads != null && !ads.isEmpty()) {
                            NativeAdDetails nativeAdDetails = ads.get(0);
                            populateStartAppNativeAdView(nativeAdDetails);
                            startAppNativeAdView.setVisibility(View.VISIBLE);
                            nativeAdViewContainer.setVisibility(View.VISIBLE);
                        } else {
                            if (fallback != null)
                                fallback.run();
                        }
                    }

                    @Override
                    public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                        if (fallback != null)
                            fallback.run();
                    }
                };
                startAppNativeAd.loadAd(nativePrefs, adEventListener);
            } catch (NoClassDefFoundError | Exception e) {
                Log.e(TAG, "Failed to load StartApp native ad. Error: " + e.getMessage());
                if (fallback != null)
                    fallback.run();
            }
        }

        private void handleWortiseLoad(Runnable fallback) {
            // Wortise Native Ads implementation is temporarily disabled due to SDK version
            // conflicts
            if (fallback != null)
                fallback.run();
        }

        private void populateNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd,
                com.google.android.gms.ads.nativead.NativeAdView nativeAdView) {
            int bgColor = darkTheme ? nativeBackgroundDark : nativeBackgroundLight;
            if (nativeAdViewContainer != null)
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, bgColor));

            View background = nativeAdView.findViewById(R.id.background);
            if (background != null)
                background.setBackgroundResource(bgColor);

            nativeAdView.setMediaView(nativeAdView.findViewById(R.id.media_view));
            nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.primary));
            nativeAdView.setBodyView(nativeAdView.findViewById(R.id.body));
            nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.cta));
            nativeAdView.setIconView(nativeAdView.findViewById(R.id.icon));

            if (nativeAdView.getHeadlineView() != null)
                ((TextView) nativeAdView.getHeadlineView()).setText(nativeAd.getHeadline());

            if (nativeAdView.getMediaView() != null)
                nativeAdView.getMediaView().setMediaContent(nativeAd.getMediaContent());

            if (nativeAdView.getBodyView() != null) {
                if (nativeAd.getBody() == null) {
                    nativeAdView.getBodyView().setVisibility(View.INVISIBLE);
                } else {
                    nativeAdView.getBodyView().setVisibility(View.VISIBLE);
                    ((TextView) nativeAdView.getBodyView()).setText(nativeAd.getBody());
                }
            }

            if (nativeAdView.getCallToActionView() != null) {
                if (nativeAd.getCallToAction() == null) {
                    nativeAdView.getCallToActionView().setVisibility(View.INVISIBLE);
                } else {
                    nativeAdView.getCallToActionView().setVisibility(View.VISIBLE);
                    ((Button) nativeAdView.getCallToActionView()).setText(nativeAd.getCallToAction());
                }
            }

            if (nativeAdView.getIconView() != null) {
                if (nativeAd.getIcon() == null) {
                    nativeAdView.getIconView().setVisibility(View.GONE);
                } else {
                    ((ImageView) nativeAdView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                    nativeAdView.getIconView().setVisibility(View.VISIBLE);
                }
            }

            nativeAdView.setNativeAd(nativeAd);
        }

        private void populateStartAppNativeAdView(NativeAdDetails nativeAdDetails) {
            int bgColor = darkTheme ? nativeBackgroundDark : nativeBackgroundLight;
            if (nativeAdViewContainer != null)
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, bgColor));

            if (startAppNativeBackground != null)
                startAppNativeBackground.setBackgroundResource(bgColor);

            if (startAppNativeTitle != null)
                startAppNativeTitle.setText(nativeAdDetails.getTitle());

            if (startAppNativeDescription != null)
                startAppNativeDescription.setText(nativeAdDetails.getDescription());

            if (startAppNativeImage != null)
                startAppNativeImage.setImageBitmap(nativeAdDetails.getImageBitmap());

            if (startAppNativeIcon != null)
                startAppNativeIcon.setImageBitmap(nativeAdDetails.getSecondaryImageBitmap());

            if (startAppNativeButton != null)
                startAppNativeButton.setText(nativeAdDetails.isApp() ? "Install" : "Open");

            nativeAdDetails.registerViewForInteraction(startAppNativeAdView);
        }

        public void loadBackupNativeAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {

                    nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);

                    // Inflate AdMob ViewStub if present
                    android.view.ViewStub adMobStub = view.findViewById(R.id.ad_mob_native_ad_stub);
                    if (adMobStub != null) {
                        adMobStub.inflate();
                    }
                    adMobNativeAdView = view.findViewById(R.id.ad_mob_native_ad_container);
                    mediaView = view.findViewById(R.id.media_view);
                    adMobNativeBackground = view.findViewById(R.id.background);

                    // Inflate Meta ViewStub if present
                    android.view.ViewStub metaStub = view.findViewById(R.id.meta_native_ad_stub);
                    if (metaStub != null) {
                        metaStub.inflate();
                    }
                    metaNativeAdLayout = view.findViewById(R.id.meta_native_ad_container);

                    startAppNativeAdView = view.findViewById(R.id.start_app_native_ad_container);
                    startAppNativeImage = view.findViewById(R.id.start_app_native_image);
                    startAppNativeIcon = view.findViewById(R.id.start_app_native_icon);
                    startAppNativeTitle = view.findViewById(R.id.start_app_native_title);
                    startAppNativeDescription = view.findViewById(R.id.start_app_native_description);
                    startAppNativeButton = view.findViewById(R.id.start_app_native_button);
                    startAppNativeButton.setOnClickListener(v -> startAppNativeAdView.performClick());
                    startAppNativeBackground = view.findViewById(R.id.start_app_native_background);

                    appLovinNativeAd = view.findViewById(R.id.app_lovin_native_ad_container);
                    appLovinDiscoveryMrecAd = view.findViewById(R.id.app_lovin_discovery_mrec_ad_container);

                    switch (backupAdNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            if (adMobNativeAdView.getVisibility() != View.VISIBLE) {
                                adMobNativeAdView.setVisibility(View.VISIBLE);
                                nativeAdViewContainer.setVisibility(View.VISIBLE);
                                AdLoader adLoader = new AdLoader.Builder(activity, adMobNativeId)
                                        .forNativeAd(NativeAd -> {
                                            if (darkTheme) {
                                                ColorDrawable colorDrawable = new ColorDrawable(
                                                        ContextCompat.getColor(activity, nativeBackgroundDark));
                                                NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                                        .withMainBackgroundColor(colorDrawable).build();
                                                adMobNativeAdView.setStyles(styles);
                                                adMobNativeBackground.setBackgroundResource(nativeBackgroundDark);
                                            } else {
                                                ColorDrawable colorDrawable = new ColorDrawable(
                                                        ContextCompat.getColor(activity, nativeBackgroundLight));
                                                NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                                        .withMainBackgroundColor(colorDrawable).build();
                                                adMobNativeAdView.setStyles(styles);
                                                adMobNativeBackground.setBackgroundResource(nativeBackgroundLight);
                                            }
                                            if (adMobNativeAd != null) {
                                                adMobNativeAd.destroy();
                                            }
                                            adMobNativeAd = NativeAd;
                                            mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                            adMobNativeAdView.setNativeAd(NativeAd);
                                        })
                                        .withAdListener(new AdListener() {
                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                                adMobNativeAdView.setVisibility(View.GONE);
                                                nativeAdViewContainer.setVisibility(View.GONE);
                                            }
                                        })
                                        .build();
                                adLoader.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                            } else {
                                Log.d(TAG, "AdMob Native Ad has been loaded");
                            }
                            break;

                        case META:
                            try {
                                metaNativeAd = new com.facebook.ads.NativeAd(activity, metaNativeId);
                                NativeAdListener nativeAdListener = new NativeAdListener() {
                                    @Override
                                    public void onMediaDownloaded(com.facebook.ads.Ad ad) {

                                    }

                                    @Override
                                    public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
                                        adMobNativeAdView.setVisibility(View.GONE);
                                        nativeAdViewContainer.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAdLoaded(com.facebook.ads.Ad ad) {
                                        // Race condition, load() called again before last ad was displayed
                                        metaNativeAdLayout.setVisibility(View.VISIBLE);
                                        nativeAdViewContainer.setVisibility(View.VISIBLE);
                                        if (metaNativeAd != ad) {
                                            return;
                                        }
                                        // Inflate Native Ad into Container
                                        // inflateAd(nativeAd);
                                        metaNativeAd.unregisterView();
                                        // Add the Ad view into the ad container.
                                        LayoutInflater inflater = LayoutInflater.from(activity);
                                        // Inflate the Ad view. The layout referenced should be the one you created in
                                        // the last step.
                                        LinearLayout nativeAdView;

                                        switch (nativeAdStyle) {
                                            case Constant.STYLE_NEWS:
                                            case Constant.STYLE_MEDIUM:
                                                nativeAdView = (LinearLayout) inflater
                                                        .inflate(R.layout.adglide_meta_news_template_view,
                                                                metaNativeAdLayout,
                                                                false);
                                                break;
                                            case Constant.STYLE_VIDEO_SMALL:
                                                nativeAdView = (LinearLayout) inflater.inflate(
                                                        R.layout.adglide_meta_video_small_template_view,
                                                        metaNativeAdLayout,
                                                        false);
                                                break;
                                            case Constant.STYLE_VIDEO_LARGE:
                                                nativeAdView = (LinearLayout) inflater.inflate(
                                                        R.layout.adglide_meta_video_large_template_view,
                                                        metaNativeAdLayout,
                                                        false);
                                                break;
                                            case Constant.STYLE_RADIO:
                                            case Constant.STYLE_SMALL:
                                                nativeAdView = (LinearLayout) inflater.inflate(
                                                        R.layout.adglide_meta_radio_template_view, metaNativeAdLayout,
                                                        false);
                                                break;
                                            default:
                                                nativeAdView = (LinearLayout) inflater.inflate(
                                                        R.layout.adglide_meta_medium_template_view, metaNativeAdLayout,
                                                        false);
                                                break;
                                        }
                                        metaNativeAdLayout.addView(nativeAdView);

                                        // Add the AdOptionsView
                                        LinearLayout adChoicesContainer = nativeAdView
                                                .findViewById(R.id.ad_choices_container);
                                        AdOptionsView adOptionsView = new AdOptionsView(activity, metaNativeAd,
                                                metaNativeAdLayout);
                                        adChoicesContainer.removeAllViews();
                                        adChoicesContainer.addView(adOptionsView, 0);

                                        // Create native UI using the ad metadata.
                                        TextView nativeAdTitle = nativeAdView.findViewById(R.id.native_ad_title);
                                        com.facebook.ads.MediaView nativeAdMedia = nativeAdView
                                                .findViewById(R.id.native_ad_media);
                                        com.facebook.ads.MediaView nativeAdIcon = nativeAdView
                                                .findViewById(R.id.native_ad_icon);
                                        TextView nativeAdSocialContext = nativeAdView
                                                .findViewById(R.id.native_ad_social_context);
                                        TextView nativeAdBody = nativeAdView.findViewById(R.id.native_ad_body);
                                        TextView sponsoredLabel = nativeAdView
                                                .findViewById(R.id.native_ad_sponsored_label);
                                        Button nativeAdCallToAction = nativeAdView
                                                .findViewById(R.id.native_ad_call_to_action);
                                        LinearLayout metaNativeBackground = nativeAdView.findViewById(R.id.ad_unit);

                                        if (darkTheme) {
                                            nativeAdTitle.setTextColor(
                                                    ContextCompat.getColor(activity,
                                                            R.color.adglide_app_lovin_dark_primary_text_color));
                                            nativeAdSocialContext.setTextColor(
                                                    ContextCompat.getColor(activity,
                                                            R.color.adglide_app_lovin_dark_primary_text_color));
                                            sponsoredLabel.setTextColor(ContextCompat.getColor(activity,
                                                    R.color.adglide_app_lovin_dark_secondary_text_color));
                                            nativeAdBody.setTextColor(ContextCompat.getColor(activity,
                                                    R.color.adglide_app_lovin_dark_secondary_text_color));
                                            metaNativeBackground.setBackgroundResource(nativeBackgroundDark);
                                        } else {
                                            metaNativeBackground.setBackgroundResource(nativeBackgroundLight);
                                        }

                                        // Set the Text.
                                        nativeAdTitle.setText(metaNativeAd.getAdvertiserName());
                                        nativeAdBody.setText(metaNativeAd.getAdBodyText());
                                        nativeAdSocialContext.setText(metaNativeAd.getAdSocialContext());
                                        nativeAdCallToAction
                                                .setVisibility(
                                                        metaNativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                                        nativeAdCallToAction.setText(metaNativeAd.getAdCallToAction());
                                        sponsoredLabel.setText(metaNativeAd.getSponsoredTranslation());

                                        // Create a list of clickable views
                                        List<View> clickableViews = new ArrayList<>();
                                        clickableViews.add(nativeAdTitle);
                                        clickableViews.add(sponsoredLabel);
                                        clickableViews.add(nativeAdIcon);
                                        clickableViews.add(nativeAdMedia);
                                        clickableViews.add(nativeAdBody);
                                        clickableViews.add(nativeAdSocialContext);
                                        clickableViews.add(nativeAdCallToAction);

                                        // Register the Title and CTA button to listen for clicks.
                                        metaNativeAd.registerViewForInteraction(nativeAdView, nativeAdIcon,
                                                nativeAdMedia,
                                                clickableViews);

                                    }

                                    @Override
                                    public void onAdClicked(com.facebook.ads.Ad ad) {

                                    }

                                    @Override
                                    public void onLoggingImpression(com.facebook.ads.Ad ad) {

                                    }
                                };
                                com.facebook.ads.NativeAd.NativeLoadAdConfig loadAdConfig = metaNativeAd
                                        .buildLoadAdConfig()
                                        .withAdListener(nativeAdListener).build();
                                metaNativeAd.loadAd(loadAdConfig);
                            } catch (NoClassDefFoundError | Exception e) {
                                Log.e(TAG, "Failed to load Meta native ad. Error: " + e.getMessage());
                                adMobNativeAdView.setVisibility(View.GONE);
                                nativeAdViewContainer.setVisibility(View.GONE);
                            }
                            break;

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX:
                            handleAppLovinLoad(() -> {
                                appLovinNativeAd.setVisibility(View.GONE);
                                nativeAdViewContainer.setVisibility(View.GONE);
                            });
                            break;

                        case STARTAPP:
                            handleStartAppLoad(() -> {
                                startAppNativeAdView.setVisibility(View.GONE);
                                nativeAdViewContainer.setVisibility(View.GONE);
                            });
                            break;

                        case WORTISE:
                            handleWortiseLoad(() -> {
                                wortiseNativeAd.setVisibility(View.GONE);
                                nativeAdViewContainer.setVisibility(View.GONE);
                            });
                            break;

                        default:
                            nativeAdViewContainer.setVisibility(View.GONE);
                            break;

                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadBackupNativeAd: " + e.getMessage());
            }
        }

        public void setNativeAdPadding(int left, int top, int right, int bottom) {
            nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);
            nativeAdViewContainer.setPadding(left, top, right, bottom);
            if (darkTheme) {
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundDark));
            } else {
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundLight));
            }
        }

        public void setNativeAdMargin(int left, int top, int right, int bottom) {
            nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);
            setMargins(nativeAdViewContainer, left, top, right, bottom);
        }

        public void setMargins(View view, int left, int top, int right, int bottom) {
            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                p.setMargins(left, top, right, bottom);
                view.requestLayout();
            }
        }

        public void setNativeAdBackgroundResource(int drawableBackground) {
            nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);
            nativeAdViewContainer.setBackgroundResource(drawableBackground);
        }

        /**
         * Destroys and releases all native ad resources to prevent memory leaks.
         * Should be called when the hosting Fragment is destroyed.
         */
        public void destroyNativeAd() {
            if (adMobNativeAd != null) {
                adMobNativeAd.destroy();
                adMobNativeAd = null;
            }
            if (adMobNativeAdView != null) {
                adMobNativeAdView.destroyNativeAd();
            }
            if (metaNativeAd != null) {
                metaNativeAd.destroy();
                metaNativeAd = null;
            }
            if (nativeAdLoader != null) {
                nativeAdLoader.destroy();
                nativeAdLoader = null;
            }
            if (maxNativeAd != null) {
                maxNativeAd = null;
            }
            if (startAppNativeAd != null) {
                startAppNativeAd = null;
            }
        }

    }

}
