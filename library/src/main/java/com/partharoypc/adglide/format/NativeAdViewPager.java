package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_ADMOB;

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
        private com.google.android.gms.ads.nativead.NativeAd adMobNativeAdObj;
        private TemplateView admobNativeAd;
        private LinearLayout admobNativeBackground;

        private com.facebook.ads.NativeAd fanNativeAd;
        private NativeAdLayout fanNativeAdLayout;

        private View startappNativeAd;
        private ImageView startappNativeImage;
        private ImageView startappNativeIcon;
        private TextView startappNativeTitle;
        private TextView startappNativeDescription;
        private Button startappNativeButton;
        private LinearLayout startappNativeBackground;

        private FrameLayout applovinNativeAd;

        private ProgressBar progressBarAd;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private String adMobNativeId = "";
        private String fanNativeId = "";
        private String appLovinNativeId = "";
        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;

        private int nativeBackgroundLight = R.color.color_native_background_light;
        private int nativeBackgroundDark = R.color.color_native_background_dark;

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
        public Builder setFanNativeId(@androidx.annotation.NonNull String fanNativeId) {
            this.fanNativeId = fanNativeId;
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

                    admobNativeAd = view.findViewById(R.id.admob_native_ad_container);
                    mediaView = view.findViewById(R.id.media_view);
                    admobNativeBackground = view.findViewById(R.id.background);

                    fanNativeAdLayout = view.findViewById(R.id.fan_native_ad_container);

                    startappNativeAd = view.findViewById(R.id.startapp_native_ad_container);
                    startappNativeImage = view.findViewById(R.id.startapp_native_image);
                    startappNativeIcon = activity.findViewById(R.id.startapp_native_icon);
                    startappNativeTitle = view.findViewById(R.id.startapp_native_title);
                    startappNativeDescription = view.findViewById(R.id.startapp_native_description);
                    startappNativeButton = view.findViewById(R.id.startapp_native_button);
                    startappNativeButton.setOnClickListener(v1 -> startappNativeAd.performClick());
                    startappNativeBackground = view.findViewById(R.id.startapp_native_background);
                    applovinNativeAd = view.findViewById(R.id.applovin_native_ad_container);
                    progressBarAd = view.findViewById(R.id.progress_bar_ad);
                    progressBarAd.setVisibility(View.VISIBLE);

                    switch (adNetwork) {
                        case ADMOB:
                        case FAN_BIDDING_ADMOB:
                            if (admobNativeAd.getVisibility() != View.VISIBLE) {
                                AdLoader adLoader = new AdLoader.Builder(activity, adMobNativeId)
                                        .forNativeAd(NativeAd -> {
                                            if (darkTheme) {
                                                ColorDrawable colorDrawable = new ColorDrawable(
                                                        ContextCompat.getColor(activity, nativeBackgroundDark));
                                                NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                                        .withMainBackgroundColor(colorDrawable).build();
                                                admobNativeAd.setStyles(styles);
                                                admobNativeBackground.setBackgroundResource(nativeBackgroundDark);
                                            } else {
                                                ColorDrawable colorDrawable = new ColorDrawable(
                                                        ContextCompat.getColor(activity, nativeBackgroundLight));
                                                NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                                        .withMainBackgroundColor(colorDrawable).build();
                                                admobNativeAd.setStyles(styles);
                                                admobNativeBackground.setBackgroundResource(nativeBackgroundLight);
                                            }
                                            if (adMobNativeAdObj != null) {
                                                adMobNativeAdObj.destroy();
                                            }
                                            adMobNativeAdObj = NativeAd;
                                            mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                            admobNativeAd.setNativeAd(NativeAd);
                                            admobNativeAd.setVisibility(View.VISIBLE);
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

                        case META:
                            if (fanNativeAdLayout.getVisibility() != View.VISIBLE) {
                                fanNativeAd = new com.facebook.ads.NativeAd(activity, fanNativeId);
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
                                        fanNativeAdLayout.setVisibility(View.VISIBLE);
                                        progressBarAd.setVisibility(View.GONE);
                                        if (fanNativeAd != ad) {
                                            return;
                                        }
                                        // Inflate Native Ad into Container
                                        // inflateAd(nativeAd);
                                        fanNativeAd.unregisterView();
                                        // Add the Ad view into the ad container.
                                        LayoutInflater inflater = LayoutInflater.from(activity);
                                        // Inflate the Ad view. The layout referenced should be the one you created in
                                        // the last step.
                                        LinearLayout nativeAdView = (LinearLayout) inflater
                                                .inflate(R.layout.gnt_fan_large_template_view, fanNativeAdLayout,
                                                        false);
                                        fanNativeAdLayout.addView(nativeAdView);

                                        // Add the AdOptionsView
                                        LinearLayout adChoicesContainer = nativeAdView
                                                .findViewById(R.id.ad_choices_container);
                                        AdOptionsView adOptionsView = new AdOptionsView(activity, fanNativeAd,
                                                fanNativeAdLayout);
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
                                        LinearLayout fanNativeBackground = nativeAdView.findViewById(R.id.ad_unit);

                                        if (darkTheme) {
                                            nativeAdTitle.setTextColor(ContextCompat.getColor(activity,
                                                    R.color.applovin_dark_primary_text_color));
                                            nativeAdSocialContext.setTextColor(ContextCompat.getColor(activity,
                                                    R.color.applovin_dark_primary_text_color));
                                            sponsoredLabel.setTextColor(ContextCompat.getColor(activity,
                                                    R.color.applovin_dark_secondary_text_color));
                                            nativeAdBody.setTextColor(ContextCompat.getColor(activity,
                                                    R.color.applovin_dark_secondary_text_color));
                                            fanNativeBackground.setBackgroundResource(nativeBackgroundDark);
                                        } else {
                                            fanNativeBackground.setBackgroundResource(nativeBackgroundLight);
                                        }

                                        // Set the Text.
                                        nativeAdTitle.setText(fanNativeAd.getAdvertiserName());
                                        nativeAdBody.setText(fanNativeAd.getAdBodyText());
                                        nativeAdSocialContext.setText(fanNativeAd.getAdSocialContext());
                                        nativeAdCallToAction.setVisibility(
                                                fanNativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                                        nativeAdCallToAction.setText(fanNativeAd.getAdCallToAction());
                                        sponsoredLabel.setText(fanNativeAd.getSponsoredTranslation());

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
                                        fanNativeAd.registerViewForInteraction(nativeAdView, nativeAdIcon,
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

                                com.facebook.ads.NativeAd.NativeLoadAdConfig loadAdConfig = fanNativeAd
                                        .buildLoadAdConfig()
                                        .withAdListener(nativeAdListener).build();
                                fanNativeAd.loadAd(loadAdConfig);
                            } else {
                                Log.d(TAG, "FAN Native Ad has been loaded");
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

                    admobNativeAd = view.findViewById(R.id.admob_native_ad_container);
                    mediaView = view.findViewById(R.id.media_view);
                    admobNativeBackground = view.findViewById(R.id.background);

                    fanNativeAdLayout = view.findViewById(R.id.fan_native_ad_container);

                    startappNativeAd = view.findViewById(R.id.startapp_native_ad_container);
                    startappNativeImage = view.findViewById(R.id.startapp_native_image);
                    startappNativeIcon = activity.findViewById(R.id.startapp_native_icon);
                    startappNativeTitle = view.findViewById(R.id.startapp_native_title);
                    startappNativeDescription = view.findViewById(R.id.startapp_native_description);
                    startappNativeButton = view.findViewById(R.id.startapp_native_button);
                    startappNativeButton.setOnClickListener(v1 -> startappNativeAd.performClick());
                    startappNativeBackground = view.findViewById(R.id.startapp_native_background);
                    applovinNativeAd = view.findViewById(R.id.applovin_native_ad_container);
                    progressBarAd = view.findViewById(R.id.progress_bar_ad);
                    progressBarAd.setVisibility(View.VISIBLE);

                    switch (backupAdNetwork) {
                        case ADMOB:
                        case FAN_BIDDING_ADMOB:
                            if (admobNativeAd.getVisibility() != View.VISIBLE) {
                                AdLoader adLoader = new AdLoader.Builder(activity, adMobNativeId)
                                        .forNativeAd(NativeAd -> {
                                            if (darkTheme) {
                                                ColorDrawable colorDrawable = new ColorDrawable(
                                                        ContextCompat.getColor(activity, nativeBackgroundDark));
                                                NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                                        .withMainBackgroundColor(colorDrawable).build();
                                                admobNativeAd.setStyles(styles);
                                                admobNativeBackground.setBackgroundResource(nativeBackgroundDark);
                                            } else {
                                                ColorDrawable colorDrawable = new ColorDrawable(
                                                        ContextCompat.getColor(activity, nativeBackgroundLight));
                                                NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                                        .withMainBackgroundColor(colorDrawable).build();
                                                admobNativeAd.setStyles(styles);
                                                admobNativeBackground.setBackgroundResource(nativeBackgroundLight);
                                            }
                                            if (adMobNativeAdObj != null) {
                                                adMobNativeAdObj.destroy();
                                            }
                                            adMobNativeAdObj = NativeAd;
                                            mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                            admobNativeAd.setNativeAd(NativeAd);
                                            admobNativeAd.setVisibility(View.VISIBLE);
                                            progressBarAd.setVisibility(View.GONE);
                                        })
                                        .withAdListener(new AdListener() {
                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                                admobNativeAd.setVisibility(View.GONE);
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
                            if (fanNativeAdLayout.getVisibility() != View.VISIBLE) {
                                fanNativeAd = new com.facebook.ads.NativeAd(activity, fanNativeId);
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
                                        fanNativeAdLayout.setVisibility(View.VISIBLE);
                                        progressBarAd.setVisibility(View.GONE);
                                        if (fanNativeAd != ad) {
                                            return;
                                        }
                                        // Inflate Native Ad into Container
                                        // inflateAd(nativeAd);
                                        fanNativeAd.unregisterView();
                                        // Add the Ad view into the ad container.
                                        LayoutInflater inflater = LayoutInflater.from(activity);
                                        // Inflate the Ad view. The layout referenced should be the one you created in
                                        // the last step.
                                        LinearLayout nativeAdView = (LinearLayout) inflater
                                                .inflate(R.layout.gnt_fan_large_template_view, fanNativeAdLayout,
                                                        false);
                                        fanNativeAdLayout.addView(nativeAdView);

                                        // Add the AdOptionsView
                                        LinearLayout adChoicesContainer = nativeAdView
                                                .findViewById(R.id.ad_choices_container);
                                        AdOptionsView adOptionsView = new AdOptionsView(activity, fanNativeAd,
                                                fanNativeAdLayout);
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
                                        LinearLayout fanNativeBackground = nativeAdView.findViewById(R.id.ad_unit);

                                        if (darkTheme) {
                                            nativeAdTitle.setTextColor(ContextCompat.getColor(activity,
                                                    R.color.applovin_dark_primary_text_color));
                                            nativeAdSocialContext.setTextColor(ContextCompat.getColor(activity,
                                                    R.color.applovin_dark_primary_text_color));
                                            sponsoredLabel.setTextColor(ContextCompat.getColor(activity,
                                                    R.color.applovin_dark_secondary_text_color));
                                            nativeAdBody.setTextColor(ContextCompat.getColor(activity,
                                                    R.color.applovin_dark_secondary_text_color));
                                            fanNativeBackground.setBackgroundResource(nativeBackgroundDark);
                                        } else {
                                            fanNativeBackground.setBackgroundResource(nativeBackgroundLight);
                                        }

                                        // Set the Text.
                                        nativeAdTitle.setText(fanNativeAd.getAdvertiserName());
                                        nativeAdBody.setText(fanNativeAd.getAdBodyText());
                                        nativeAdSocialContext.setText(fanNativeAd.getAdSocialContext());
                                        nativeAdCallToAction.setVisibility(
                                                fanNativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                                        nativeAdCallToAction.setText(fanNativeAd.getAdCallToAction());
                                        sponsoredLabel.setText(fanNativeAd.getSponsoredTranslation());

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
                                        fanNativeAd.registerViewForInteraction(nativeAdView, nativeAdIcon,
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

                                com.facebook.ads.NativeAd.NativeLoadAdConfig loadAdConfig = fanNativeAd
                                        .buildLoadAdConfig()
                                        .withAdListener(nativeAdListener).build();
                                fanNativeAd.loadAd(loadAdConfig);
                            } else {
                                Log.d(TAG, "FAN Native Ad has been loaded");
                                progressBarAd.setVisibility(View.GONE);
                            }
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
            if (adMobNativeAdObj != null) {
                adMobNativeAdObj.destroy();
                adMobNativeAdObj = null;
            }
            if (admobNativeAd != null) {
                admobNativeAd.destroyNativeAd();
            }
            if (fanNativeAd != null) {
                fanNativeAd.destroy();
                fanNativeAd = null;
            }
        }

    }

}

