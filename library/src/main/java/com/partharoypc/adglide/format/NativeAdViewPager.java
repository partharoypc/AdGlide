package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.WORTISE;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import com.partharoypc.adglide.util.NativeTemplateStyle;
import com.partharoypc.adglide.util.TemplateView;
import com.partharoypc.adglide.util.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and displaying native ads within a ViewPager layout.
 * Uses a Builder pattern for configuration.
 */
public class NativeAdViewPager {

    public static class Builder {

        private static final String TAG = "AdGlide";
        private final Activity activity;

        private View view;

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

        private ProgressBar progressBarAd;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private String adMobNativeId = "";
        private String metaNativeId = "";
        private String appLovinNativeId = "";
        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;

        private int nativeBackgroundLight = R.color.adglide_color_native_background_light;
        private int nativeBackgroundDark = R.color.adglide_color_native_background_dark;

        public Builder(Activity activity, View view) {
            this.activity = activity;
            this.view = view;
        }

        @androidx.annotation.NonNull
        public Builder build() {
            loadNativeAd();
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
        public Builder setNativeAdBackgroundColor(int colorLight, int colorDark) {
            this.nativeBackgroundLight = colorLight;
            this.nativeBackgroundDark = colorDark;
            return this;
        }

        public void loadNativeAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {

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
                    startAppNativeIcon = activity.findViewById(R.id.start_app_native_icon);
                    startAppNativeTitle = view.findViewById(R.id.start_app_native_title);
                    startAppNativeDescription = view.findViewById(R.id.start_app_native_description);
                    startAppNativeButton = view.findViewById(R.id.start_app_native_button);
                    startAppNativeButton.setOnClickListener(v1 -> startAppNativeAdView.performClick());
                    startAppNativeBackground = view.findViewById(R.id.start_app_native_background);
                    appLovinNativeAd = view.findViewById(R.id.app_lovin_native_ad_container);
                    progressBarAd = view.findViewById(R.id.progress_bar_ad);
                    progressBarAd.setVisibility(View.VISIBLE);

                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            if (adMobNativeAdView.getVisibility() != View.VISIBLE) {
                                adMobNativeAdView.setVisibility(View.VISIBLE);
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
                                            progressBarAd.setVisibility(View.GONE);
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
                                progressBarAd.setVisibility(View.GONE);
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

                        case META:
                            if (metaNativeAdLayout.getVisibility() != View.VISIBLE) {
                                metaNativeAd = new com.facebook.ads.NativeAd(activity, metaNativeId);
                                NativeAdListener nativeAdListener = new NativeAdListener() {
                                    @Override
                                    public void onMediaDownloaded(com.facebook.ads.Ad ad) {

                                    }

                                    @Override
                                    public void onError(com.facebook.ads.Ad ad, AdError adError) {
                                        loadBackupNativeAd();
                                    }

                                    @Override
                                    public void onAdLoaded(com.facebook.ads.Ad ad) {
                                        // Race condition, load() called again before last ad was displayed
                                        metaNativeAdLayout.setVisibility(View.VISIBLE);
                                        progressBarAd.setVisibility(View.GONE);
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
                                        LinearLayout nativeAdView = (LinearLayout) inflater
                                                .inflate(R.layout.adglide_meta_large_template_view, metaNativeAdLayout,
                                                        false);
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
                                            nativeAdTitle.setTextColor(ContextCompat.getColor(activity,
                                                    R.color.adglide_app_lovin_dark_primary_text_color));
                                            nativeAdSocialContext.setTextColor(ContextCompat.getColor(activity,
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
                                        nativeAdCallToAction.setVisibility(
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
                            } else {
                                Log.d(TAG, "Meta Native Ad has been loaded");
                                progressBarAd.setVisibility(View.GONE);
                            }
                            break;

                        default:
                            break;

                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadNativeAd: " + e.getMessage());
            }
        }

        public void loadBackupNativeAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {

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
                    startAppNativeIcon = activity.findViewById(R.id.start_app_native_icon);
                    startAppNativeTitle = view.findViewById(R.id.start_app_native_title);
                    startAppNativeDescription = view.findViewById(R.id.start_app_native_description);
                    startAppNativeButton = view.findViewById(R.id.start_app_native_button);
                    startAppNativeButton.setOnClickListener(v1 -> startAppNativeAdView.performClick());
                    startAppNativeBackground = view.findViewById(R.id.start_app_native_background);
                    appLovinNativeAd = view.findViewById(R.id.app_lovin_native_ad_container);
                    progressBarAd = view.findViewById(R.id.progress_bar_ad);
                    progressBarAd.setVisibility(View.VISIBLE);

                    switch (backupAdNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            if (adMobNativeAdView.getVisibility() != View.VISIBLE) {
                                adMobNativeAdView.setVisibility(View.VISIBLE);
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
                                            progressBarAd.setVisibility(View.GONE);
                                        })
                                        .withAdListener(new AdListener() {
                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                                adMobNativeAdView.setVisibility(View.GONE);
                                                progressBarAd.setVisibility(View.GONE);
                                            }
                                        })
                                        .build();
                                adLoader.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                            } else {
                                Log.d(TAG, "AdMob Native Ad has been loaded");
                                progressBarAd.setVisibility(View.GONE);
                            }
                            break;

                        case META:
                            if (metaNativeAdLayout.getVisibility() != View.VISIBLE) {
                                metaNativeAd = new com.facebook.ads.NativeAd(activity, metaNativeId);
                                NativeAdListener nativeAdListener = new NativeAdListener() {
                                    @Override
                                    public void onMediaDownloaded(com.facebook.ads.Ad ad) {

                                    }

                                    @Override
                                    public void onError(com.facebook.ads.Ad ad, AdError adError) {

                                    }

                                    @Override
                                    public void onAdLoaded(com.facebook.ads.Ad ad) {
                                        // Race condition, load() called again before last ad was displayed
                                        metaNativeAdLayout.setVisibility(View.VISIBLE);
                                        progressBarAd.setVisibility(View.GONE);
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
                                        LinearLayout nativeAdView = (LinearLayout) inflater
                                                .inflate(R.layout.adglide_meta_large_template_view, metaNativeAdLayout,
                                                        false);
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
                                            nativeAdTitle.setTextColor(ContextCompat.getColor(activity,
                                                    R.color.adglide_app_lovin_dark_primary_text_color));
                                            nativeAdSocialContext.setTextColor(ContextCompat.getColor(activity,
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
                                        nativeAdCallToAction.setVisibility(
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
                            } else {
                                Log.d(TAG, "Meta Native Ad has been loaded");
                                progressBarAd.setVisibility(View.GONE);
                            }
                            break;

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX:
                            handleAppLovinLoad(() -> {
                                appLovinNativeAd.setVisibility(View.GONE);
                                progressBarAd.setVisibility(View.GONE);
                            });
                            break;

                        case STARTAPP:
                            handleStartAppLoad(() -> {
                                startAppNativeAdView.setVisibility(View.GONE);
                                progressBarAd.setVisibility(View.GONE);
                            });
                            break;

                        case WORTISE:
                            handleWortiseLoad(() -> {
                                if (wortiseNativeAd != null)
                                    wortiseNativeAd.setVisibility(View.GONE);
                                progressBarAd.setVisibility(View.GONE);
                            });
                            break;

                        default:
                            break;

                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadBackupNativeAd: " + e.getMessage());
            }
        }

        /**
         * Destroys and releases all native ad resources to prevent memory leaks.
         * Should be called when the hosting ViewPager is destroyed.
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
            maxNativeAd = null;
        }

        private void handleAppLovinLoad(Runnable fallback) {
            try {
                if (appLovinNativeAd.getVisibility() != View.VISIBLE) {
                    nativeAdLoader = new com.applovin.mediation.nativeAds.MaxNativeAdLoader(appLovinNativeId, activity);
                    nativeAdLoader.setNativeAdListener(new com.applovin.mediation.nativeAds.MaxNativeAdListener() {
                        @Override
                        public void onNativeAdLoaded(com.applovin.mediation.nativeAds.MaxNativeAdView nativeAdView,
                                com.applovin.mediation.MaxAd ad) {
                            if (maxNativeAd != null)
                                nativeAdLoader.destroy(maxNativeAd);
                            maxNativeAd = ad;
                            appLovinNativeAd.removeAllViews();
                            appLovinNativeAd.addView(nativeAdView);
                            appLovinNativeAd.setVisibility(View.VISIBLE);
                            progressBarAd.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNativeAdLoadFailed(String adUnitId, com.applovin.mediation.MaxError error) {
                            if (fallback != null)
                                fallback.run();
                            else
                                loadBackupNativeAd();
                        }
                    });
                    nativeAdLoader.loadAd();
                } else {
                    progressBarAd.setVisibility(View.GONE);
                }
            } catch (NoClassDefFoundError | Exception e) {
                Log.e(TAG, "Failed to load AppLovin native ad. Error: " + e.getMessage());
                if (fallback != null)
                    fallback.run();
                else
                    loadBackupNativeAd();
            }
        }

        private void handleStartAppLoad(Runnable fallback) {
            try {
                com.startapp.sdk.ads.nativead.StartAppNativeAd startAppNativeAd = new com.startapp.sdk.ads.nativead.StartAppNativeAd(
                        activity);
                com.startapp.sdk.ads.nativead.NativeAdPreferences nativePrefs = new com.startapp.sdk.ads.nativead.NativeAdPreferences()
                        .setAdsNumber(1)
                        .setAutoBitmapDownload(true)
                        .setSecondaryImageSize(1);

                com.startapp.sdk.adsbase.adlisteners.AdEventListener adEventListener = new com.startapp.sdk.adsbase.adlisteners.AdEventListener() {
                    @Override
                    public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad ad) {
                        ArrayList<com.startapp.sdk.ads.nativead.NativeAdDetails> ads = startAppNativeAd.getNativeAds();
                        if (ads != null && !ads.isEmpty()) {
                            com.startapp.sdk.ads.nativead.NativeAdDetails nativeAdDetails = ads.get(0);
                            populateStartAppNativeAdView(nativeAdDetails);
                            startAppNativeAdView.setVisibility(View.VISIBLE);
                            progressBarAd.setVisibility(View.GONE);
                        } else {
                            if (fallback != null)
                                fallback.run();
                            else
                                loadBackupNativeAd();
                        }
                    }

                    @Override
                    public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                        if (fallback != null)
                            fallback.run();
                        else
                            loadBackupNativeAd();
                    }
                };
                startAppNativeAd.loadAd(nativePrefs, adEventListener);
            } catch (NoClassDefFoundError | Exception e) {
                Log.e(TAG, "Failed to load StartApp native ad. Error: " + e.getMessage());
                if (fallback != null)
                    fallback.run();
                else
                    loadBackupNativeAd();
            }
        }

        private void populateStartAppNativeAdView(com.startapp.sdk.ads.nativead.NativeAdDetails nativeAdDetails) {
            int bgColor = darkTheme ? nativeBackgroundDark : nativeBackgroundLight;
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

        private void handleWortiseLoad(Runnable fallback) {
            // Wortise Native Ads implementation is temporarily disabled due to SDK version
            // conflicts
            if (fallback != null)
                fallback.run();
            else
                loadBackupNativeAd();
        }

        private void populateWortiseNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd,
                com.google.android.gms.ads.nativead.NativeAdView nativeAdView) {
            int bgColor = darkTheme ? nativeBackgroundDark : nativeBackgroundLight;
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

        private com.applovin.mediation.nativeAds.MaxNativeAdLoader nativeAdLoader;
        private com.applovin.mediation.MaxAd maxNativeAd;
        private FrameLayout wortiseNativeAd;
        private String wortiseNativeId = "";

        @androidx.annotation.NonNull
        public Builder setWortiseNativeId(@androidx.annotation.NonNull String wortiseNativeId) {
            this.wortiseNativeId = wortiseNativeId;
            return this;
        }

    }

}
