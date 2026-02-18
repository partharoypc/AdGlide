package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.FACEBOOK;
import static com.partharoypc.adglide.util.Constant.FAN;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_AD_MANAGER;
import static com.partharoypc.adglide.util.Constant.GOOGLE_AD_MANAGER;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.NONE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.UNITY;
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
import com.google.android.gms.ads.nativead.NativeAdView;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.util.AdManagerTemplateView;
import com.partharoypc.adglide.util.Constant;
import com.partharoypc.adglide.util.NativeTemplateStyle;
import com.partharoypc.adglide.util.TemplateView;
import com.partharoypc.adglide.util.Tools;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder;
import com.applovin.sdk.AppLovinSdkUtils;
import com.startapp.sdk.ads.nativead.NativeAdDetails;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.wortise.ads.nativead.GoogleNativeAd;

import java.util.ArrayList;
import java.util.List;

public class NativeAd {

    public static class Builder {

        private static final String TAG = "AdNetwork";
        private final Activity activity;
        private LinearLayout nativeAdViewContainer;

        private MediaView mediaView;
        private TemplateView admobNativeAd;
        private LinearLayout admobNativeBackground;

        private MediaView adManagerMediaView;
        private AdManagerTemplateView adManagerNativeAd;
        private LinearLayout adManagerNativeBackground;

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
        private MaxNativeAdLoader nativeAdLoader;
        private MaxAd maxNativeAd;

        private LinearLayout appLovinDiscoveryMrecAd;
        private FrameLayout wortiseNativeAd;
        private GoogleNativeAd googleNativeAd;

        private StartAppNativeAd startAppNativeAdObject;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private String adMobNativeId = "";
        private String adManagerNativeId = "";
        private String fanNativeId = "";
        private String appLovinNativeId = "";
        private String appLovinDiscMrecZoneId = "";
        private String wortiseNativeId = "";
        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;

        private String nativeAdStyle = "";
        private int nativeBackgroundLight = R.color.color_native_background_light;
        private int nativeBackgroundDark = R.color.color_native_background_dark;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder build() {
            loadNativeAd();
            return this;
        }

        public Builder setPadding(int left, int top, int right, int bottom) {
            setNativeAdPadding(left, top, right, bottom);
            return this;
        }

        public Builder setMargin(int left, int top, int right, int bottom) {
            setNativeAdMargin(left, top, right, bottom);
            return this;
        }

        public Builder setBackgroundResource(int drawableBackground) {
            setNativeAdBackgroundResource(drawableBackground);
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

        public Builder setAdMobNativeId(String adMobNativeId) {
            this.adMobNativeId = adMobNativeId;
            return this;
        }

        public Builder setAdManagerNativeId(String adManagerNativeId) {
            this.adManagerNativeId = adManagerNativeId;
            return this;
        }

        public Builder setFanNativeId(String fanNativeId) {
            this.fanNativeId = fanNativeId;
            return this;
        }

        public Builder setAppLovinNativeId(String appLovinNativeId) {
            this.appLovinNativeId = appLovinNativeId;
            return this;
        }

        public Builder setAppLovinDiscoveryMrecZoneId(String appLovinDiscMrecZoneId) {
            this.appLovinDiscMrecZoneId = appLovinDiscMrecZoneId;
            return this;
        }

        public Builder setWortiseNativeId(String wortiseNativeId) {
            this.wortiseNativeId = wortiseNativeId;
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

        public Builder setNativeAdStyle(String nativeAdStyle) {
            this.nativeAdStyle = nativeAdStyle;
            return this;
        }

        public Builder setNativeAdBackgroundColor(int colorLight, int colorDark) {
            this.nativeBackgroundLight = colorLight;
            this.nativeBackgroundDark = colorDark;
            return this;
        }

        public void loadNativeAd() {

            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {

                nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);

                admobNativeAd = activity.findViewById(R.id.admob_native_ad_container);
                mediaView = activity.findViewById(R.id.media_view);
                admobNativeBackground = activity.findViewById(R.id.background);

                adManagerNativeAd = activity.findViewById(R.id.google_ad_manager_native_ad_container);
                adManagerMediaView = activity.findViewById(R.id.ad_manager_media_view);
                adManagerNativeBackground = activity.findViewById(R.id.ad_manager_background);

                fanNativeAdLayout = activity.findViewById(R.id.fan_native_ad_container);

                startappNativeAd = activity.findViewById(R.id.startapp_native_ad_container);
                startappNativeImage = activity.findViewById(R.id.startapp_native_image);
                startappNativeIcon = activity.findViewById(R.id.startapp_native_icon);
                startappNativeTitle = activity.findViewById(R.id.startapp_native_title);
                startappNativeDescription = activity.findViewById(R.id.startapp_native_description);
                startappNativeButton = activity.findViewById(R.id.startapp_native_button);
                startappNativeButton.setOnClickListener(v -> startappNativeAd.performClick());
                startappNativeBackground = activity.findViewById(R.id.startapp_native_background);

                applovinNativeAd = activity.findViewById(R.id.applovin_native_ad_container);
                appLovinDiscoveryMrecAd = activity.findViewById(R.id.applovin_discovery_mrec_ad_container);

                wortiseNativeAd = activity.findViewById(R.id.wortise_native_ad_container);

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
                                        mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                        admobNativeAd.setNativeAd(NativeAd);
                                        admobNativeAd.setVisibility(View.VISIBLE);
                                        nativeAdViewContainer.setVisibility(View.VISIBLE);
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

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        if (adManagerNativeAd.getVisibility() != View.VISIBLE) {
                            AdLoader adLoader = new AdLoader.Builder(activity, adManagerNativeId)
                                    .forNativeAd(NativeAd -> {
                                        if (darkTheme) {
                                            ColorDrawable colorDrawable = new ColorDrawable(
                                                    ContextCompat.getColor(activity, nativeBackgroundDark));
                                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                                    .withMainBackgroundColor(colorDrawable).build();
                                            adManagerNativeAd.setStyles(styles);
                                            adManagerNativeBackground.setBackgroundResource(nativeBackgroundDark);
                                        } else {
                                            ColorDrawable colorDrawable = new ColorDrawable(
                                                    ContextCompat.getColor(activity, nativeBackgroundLight));
                                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                                    .withMainBackgroundColor(colorDrawable).build();
                                            adManagerNativeAd.setStyles(styles);
                                            adManagerNativeBackground.setBackgroundResource(nativeBackgroundLight);
                                        }
                                        adManagerMediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                        adManagerNativeAd.setNativeAd(NativeAd);
                                        adManagerNativeAd.setVisibility(View.VISIBLE);
                                        nativeAdViewContainer.setVisibility(View.VISIBLE);
                                    })
                                    .withAdListener(new AdListener() {
                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                            loadBackupNativeAd();
                                        }
                                    })
                                    .build();
                            adLoader.loadAd(Tools.getGoogleAdManagerRequest());
                        } else {
                            Log.d(TAG, "Ad Manager Native Ad has been loaded");
                        }
                        break;

                    case FAN:
                    case FACEBOOK:
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
                                nativeAdViewContainer.setVisibility(View.VISIBLE);
                                if (fanNativeAd != ad) {
                                    return;
                                }
                                fanNativeAd.unregisterView();
                                // Add the Ad view into the ad container.
                                LayoutInflater inflater = LayoutInflater.from(activity);
                                // Inflate the Ad view. The layout referenced should be the one you created in
                                // the last step.
                                LinearLayout nativeAdView;

                                switch (nativeAdStyle) {
                                    case Constant.STYLE_NEWS:
                                    case Constant.STYLE_MEDIUM:
                                        nativeAdView = (LinearLayout) inflater
                                                .inflate(R.layout.gnt_fan_news_template_view, fanNativeAdLayout, false);
                                        break;
                                    case Constant.STYLE_VIDEO_SMALL:
                                        nativeAdView = (LinearLayout) inflater.inflate(
                                                R.layout.gnt_fan_video_small_template_view, fanNativeAdLayout, false);
                                        break;
                                    case Constant.STYLE_VIDEO_LARGE:
                                        nativeAdView = (LinearLayout) inflater.inflate(
                                                R.layout.gnt_fan_video_large_template_view, fanNativeAdLayout, false);
                                        break;
                                    case Constant.STYLE_RADIO:
                                    case Constant.STYLE_SMALL:
                                        nativeAdView = (LinearLayout) inflater.inflate(
                                                R.layout.gnt_fan_radio_template_view, fanNativeAdLayout, false);
                                        break;
                                    default:
                                        nativeAdView = (LinearLayout) inflater.inflate(
                                                R.layout.gnt_fan_medium_template_view, fanNativeAdLayout, false);
                                        break;
                                }
                                fanNativeAdLayout.addView(nativeAdView);

                                // Add the AdOptionsView
                                LinearLayout adChoicesContainer = nativeAdView.findViewById(R.id.ad_choices_container);
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
                                TextView sponsoredLabel = nativeAdView.findViewById(R.id.native_ad_sponsored_label);
                                Button nativeAdCallToAction = nativeAdView.findViewById(R.id.native_ad_call_to_action);
                                LinearLayout fanNativeBackground = nativeAdView.findViewById(R.id.ad_unit);

                                if (darkTheme) {
                                    nativeAdTitle.setTextColor(
                                            ContextCompat.getColor(activity, R.color.applovin_dark_primary_text_color));
                                    nativeAdSocialContext.setTextColor(
                                            ContextCompat.getColor(activity, R.color.applovin_dark_primary_text_color));
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
                                nativeAdCallToAction
                                        .setVisibility(fanNativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
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
                                fanNativeAd.registerViewForInteraction(nativeAdView, nativeAdIcon, nativeAdMedia,
                                        clickableViews);

                            }

                            @Override
                            public void onAdClicked(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(com.facebook.ads.Ad ad) {

                            }
                        };

                        com.facebook.ads.NativeAd.NativeLoadAdConfig loadAdConfig = fanNativeAd.buildLoadAdConfig()
                                .withAdListener(nativeAdListener).build();
                        fanNativeAd.loadAd(loadAdConfig);
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        if (applovinNativeAd.getVisibility() != View.VISIBLE) {
                            nativeAdLoader = new MaxNativeAdLoader(appLovinNativeId, activity);
                            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                                @Override
                                public void onNativeAdLoaded(MaxNativeAdView nativeAdView, MaxAd ad) {
                                    if (maxNativeAd != null) {
                                        nativeAdLoader.destroy(maxNativeAd);
                                    }
                                    maxNativeAd = ad;
                                    applovinNativeAd.removeAllViews();
                                    applovinNativeAd.addView(nativeAdView);
                                    applovinNativeAd.setVisibility(View.VISIBLE);
                                    nativeAdViewContainer.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onNativeAdLoadFailed(String adUnitId, MaxError error) {
                                    loadBackupNativeAd();
                                }

                                @Override
                                public void onNativeAdClicked(MaxAd ad) {
                                }

                                @Override
                                public void onNativeAdExpired(MaxAd ad) {
                                    nativeAdLoader.loadAd();
                                }
                            });

                            int layoutId = R.layout.gnt_applovin_medium_template_view;
                            switch (nativeAdStyle) {
                                case Constant.STYLE_NEWS:
                                    layoutId = R.layout.gnt_applovin_news_template_view;
                                    break;
                                case Constant.STYLE_VIDEO_SMALL:
                                    layoutId = R.layout.gnt_applovin_video_small_template_view;
                                    break;
                                case Constant.STYLE_VIDEO_LARGE:
                                    layoutId = R.layout.gnt_applovin_video_large_template_view;
                                    break;
                                case Constant.STYLE_RADIO:
                                case Constant.STYLE_SMALL:
                                    layoutId = R.layout.gnt_applovin_radio_template_view;
                                    break;
                            }

                            MaxNativeAdViewBinder binder = new MaxNativeAdViewBinder.Builder(layoutId)
                                    .setTitleTextViewId(R.id.native_ad_title)
                                    .setBodyTextViewId(R.id.native_ad_body)
                                    .setAdvertiserTextViewId(R.id.native_ad_sponsored_label)
                                    .setIconImageViewId(R.id.native_ad_icon)
                                    .setMediaContentViewGroupId(R.id.native_ad_media)
                                    .setOptionsContentViewGroupId(R.id.ad_choices_container)
                                    .setCallToActionButtonId(R.id.native_ad_call_to_action)
                                    .build();
                            nativeAdLoader.setNativeAdViewBinder(binder);
                            nativeAdLoader.loadAd();
                        } else {
                            Log.d(TAG, "AppLovin Native Ad has been loaded");
                        }
                        break;

                    case STARTAPP:
                        startAppNativeAdObject = new StartAppNativeAd(activity);
                        StartAppNativeAd.NativeAdPreferences nativePrefs = new StartAppNativeAd.NativeAdPreferences()
                                .setAdsNumber(1)
                                .setAutoBitmapDownload(true)
                                .setPrimaryImageSize(2);
                        startAppNativeAdObject.loadAd(nativePrefs, new AdEventListener() {
                            @Override
                            public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                ArrayList<NativeAdDetails> ads = startAppNativeAdObject.getNativeAds();
                                if (ads.size() > 0) {
                                    NativeAdDetails nativeAd = ads.get(0);
                                    startappNativeTitle.setText(nativeAd.getTitle());
                                    startappNativeDescription.setText(nativeAd.getDescription());
                                    if (nativeAd.getSecondaryImageBitmap() != null) {
                                        startappNativeIcon.setImageBitmap(nativeAd.getSecondaryImageBitmap());
                                    } else if (nativeAd.getImageBitmap() != null) {
                                        startappNativeIcon.setImageBitmap(nativeAd.getImageBitmap());
                                    }
                                    if (nativeAd.getImageBitmap() != null) {
                                        startappNativeImage.setImageBitmap(nativeAd.getImageBitmap());
                                    }
                                    startappNativeButton.setText(nativeAd.isApp() ? "Install" : "Open");
                                    nativeAd.registerViewForInteraction(startappNativeAd);

                                    startappNativeAd.setVisibility(View.VISIBLE);
                                    nativeAdViewContainer.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                loadBackupNativeAd();
                            }
                        });
                        break;

                    case WORTISE:
                        if (wortiseNativeAd.getVisibility() != View.VISIBLE) {
                            googleNativeAd = new GoogleNativeAd(activity, wortiseNativeId,
                                    new GoogleNativeAd.Listener() {
                                        @Override
                                        public void onNativeClicked(@NonNull GoogleNativeAd googleNativeAd) {
                                        }

                                        @Override
                                        public void onNativeImpression(@NonNull GoogleNativeAd googleNativeAd) {
                                        }

                                        @Override
                                        public void onNativeLoaded(@NonNull GoogleNativeAd googleNativeAd) {
                                            wortiseNativeAd.removeAllViews();
                                            int layoutId = R.layout.gnt_wortise_medium_template_view;
                                            switch (nativeAdStyle) {
                                                case Constant.STYLE_NEWS:
                                                    layoutId = R.layout.gnt_wortise_news_template_view;
                                                    break;
                                                case Constant.STYLE_VIDEO_SMALL:
                                                    layoutId = R.layout.gnt_wortise_video_small_template_view;
                                                    break;
                                                case Constant.STYLE_VIDEO_LARGE:
                                                    layoutId = R.layout.gnt_wortise_video_large_template_view;
                                                    break;
                                                case Constant.STYLE_RADIO:
                                                case Constant.STYLE_SMALL:
                                                    layoutId = R.layout.gnt_wortise_radio_template_view;
                                                    break;
                                            }
                                            com.google.android.gms.ads.nativead.NativeAdView nativeAdView = (com.google.android.gms.ads.nativead.NativeAdView) LayoutInflater
                                                    .from(activity).inflate(layoutId, null);

                                            ((TextView) nativeAdView.getHeadlineView())
                                                    .setText(googleNativeAd.getHeadline());
                                            ((TextView) nativeAdView.getBodyView()).setText(googleNativeAd.getBody());
                                            ((Button) nativeAdView.getCallToActionView())
                                                    .setText(googleNativeAd.getCallToAction());
                                            if (googleNativeAd.getIcon() != null) {
                                                ((ImageView) nativeAdView.getIconView())
                                                        .setImageDrawable(googleNativeAd.getIcon().getDrawable());
                                            } else {
                                                nativeAdView.getIconView().setVisibility(View.GONE);
                                            }

                                            wortiseNativeAd.addView(nativeAdView);
                                            wortiseNativeAd.setVisibility(View.VISIBLE);
                                            nativeAdViewContainer.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onNativeFailed(@NonNull GoogleNativeAd googleNativeAd,
                                                @NonNull com.wortise.ads.AdError adError) {
                                            loadBackupNativeAd();
                                        }
                                    });
                            googleNativeAd.load();
                        } else {
                            Log.d(TAG, "Wortise Native Ad has been loaded");
                        }
                        break;

                    default:
                        break;
                }

            }

        }

        public void loadBackupNativeAd() {

            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {

                nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);

                admobNativeAd = activity.findViewById(R.id.admob_native_ad_container);
                mediaView = activity.findViewById(R.id.media_view);
                admobNativeBackground = activity.findViewById(R.id.background);

                adManagerNativeAd = activity.findViewById(R.id.google_ad_manager_native_ad_container);
                adManagerMediaView = activity.findViewById(R.id.ad_manager_media_view);
                adManagerNativeBackground = activity.findViewById(R.id.ad_manager_background);

                startappNativeAd = activity.findViewById(R.id.startapp_native_ad_container);
                startappNativeImage = activity.findViewById(R.id.startapp_native_image);
                startappNativeIcon = activity.findViewById(R.id.startapp_native_icon);
                startappNativeTitle = activity.findViewById(R.id.startapp_native_title);
                startappNativeDescription = activity.findViewById(R.id.startapp_native_description);
                startappNativeButton = activity.findViewById(R.id.startapp_native_button);
                startappNativeButton.setOnClickListener(v -> startappNativeAd.performClick());
                startappNativeBackground = activity.findViewById(R.id.startapp_native_background);

                applovinNativeAd = activity.findViewById(R.id.applovin_native_ad_container);
                appLovinDiscoveryMrecAd = activity.findViewById(R.id.applovin_discovery_mrec_ad_container);

                wortiseNativeAd = activity.findViewById(R.id.wortise_native_ad_container);

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
                                        mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                        admobNativeAd.setNativeAd(NativeAd);
                                        admobNativeAd.setVisibility(View.VISIBLE);
                                        nativeAdViewContainer.setVisibility(View.VISIBLE);
                                    })
                                    .withAdListener(new AdListener() {
                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                            admobNativeAd.setVisibility(View.GONE);
                                            nativeAdViewContainer.setVisibility(View.GONE);
                                        }
                                    })
                                    .build();
                            adLoader.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                        } else {
                            Log.d(TAG, "AdMob Native Ad has been loaded");
                        }
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        if (adManagerNativeAd.getVisibility() != View.VISIBLE) {
                            AdLoader adLoader = new AdLoader.Builder(activity, adManagerNativeId)
                                    .forNativeAd(NativeAd -> {
                                        if (darkTheme) {
                                            ColorDrawable colorDrawable = new ColorDrawable(
                                                    ContextCompat.getColor(activity, nativeBackgroundDark));
                                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                                    .withMainBackgroundColor(colorDrawable).build();
                                            adManagerNativeAd.setStyles(styles);
                                            adManagerNativeBackground.setBackgroundResource(nativeBackgroundDark);
                                        } else {
                                            ColorDrawable colorDrawable = new ColorDrawable(
                                                    ContextCompat.getColor(activity, nativeBackgroundLight));
                                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                                    .withMainBackgroundColor(colorDrawable).build();
                                            adManagerNativeAd.setStyles(styles);
                                            adManagerNativeBackground.setBackgroundResource(nativeBackgroundLight);
                                        }
                                        adManagerMediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                        adManagerNativeAd.setNativeAd(NativeAd);
                                        adManagerNativeAd.setVisibility(View.VISIBLE);
                                        nativeAdViewContainer.setVisibility(View.VISIBLE);
                                    })
                                    .withAdListener(new AdListener() {
                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                            adManagerNativeAd.setVisibility(View.GONE);
                                            nativeAdViewContainer.setVisibility(View.GONE);
                                        }
                                    })
                                    .build();
                            adLoader.loadAd(Tools.getGoogleAdManagerRequest());
                        } else {
                            Log.d(TAG, "Ad Manager Native Ad has been loaded");
                        }
                        break;

                    case FAN:
                    case FACEBOOK:
                        fanNativeAd = new com.facebook.ads.NativeAd(activity, fanNativeId);
                        NativeAdListener nativeAdListener = new NativeAdListener() {
                            @Override
                            public void onMediaDownloaded(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onError(com.facebook.ads.Ad ad, AdError adError) {
                                nativeAdViewContainer.setVisibility(View.GONE);
                                fanNativeAdLayout.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAdLoaded(com.facebook.ads.Ad ad) {
                                // Race condition, load() called again before last ad was displayed
                                fanNativeAdLayout.setVisibility(View.VISIBLE);
                                nativeAdViewContainer.setVisibility(View.VISIBLE);
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
                                LinearLayout nativeAdView;

                                switch (nativeAdStyle) {
                                    case Constant.STYLE_NEWS:
                                    case Constant.STYLE_MEDIUM:
                                        nativeAdView = (LinearLayout) inflater
                                                .inflate(R.layout.gnt_fan_news_template_view, fanNativeAdLayout, false);
                                        break;
                                    case Constant.STYLE_VIDEO_SMALL:
                                        nativeAdView = (LinearLayout) inflater.inflate(
                                                R.layout.gnt_fan_video_small_template_view, fanNativeAdLayout, false);
                                        break;
                                    case Constant.STYLE_VIDEO_LARGE:
                                        nativeAdView = (LinearLayout) inflater.inflate(
                                                R.layout.gnt_fan_video_large_template_view, fanNativeAdLayout, false);
                                        break;
                                    case Constant.STYLE_RADIO:
                                    case Constant.STYLE_SMALL:
                                        nativeAdView = (LinearLayout) inflater.inflate(
                                                R.layout.gnt_fan_radio_template_view, fanNativeAdLayout, false);
                                        break;
                                    default:
                                        nativeAdView = (LinearLayout) inflater.inflate(
                                                R.layout.gnt_fan_medium_template_view, fanNativeAdLayout, false);
                                        break;
                                }
                                fanNativeAdLayout.addView(nativeAdView);

                                // Add the AdOptionsView
                                LinearLayout adChoicesContainer = nativeAdView.findViewById(R.id.ad_choices_container);
                                AdOptionsView adOptionsView = new AdOptionsView(activity, fanNativeAd,
                                        fanNativeAdLayout);
                                adChoicesContainer.removeAllViews();
                                adChoicesContainer.addView(adOptionsView, 0);

                                // Create native UI using the ad metadata.
                                TextView nativeAdTitle = nativeAdView.findViewById(R.id.native_ad_title);
                                com.facebook.ads.MediaView nativeAdIcon = nativeAdView
                                        .findViewById(R.id.native_ad_icon);
                                com.facebook.ads.MediaView nativeAdMedia = nativeAdView
                                        .findViewById(R.id.native_ad_media);
                                TextView nativeAdSocialContext = nativeAdView
                                        .findViewById(R.id.native_ad_social_context);
                                TextView nativeAdBody = nativeAdView.findViewById(R.id.native_ad_body);
                                TextView sponsoredLabel = nativeAdView.findViewById(R.id.native_ad_sponsored_label);
                                Button nativeAdCallToAction = nativeAdView.findViewById(R.id.native_ad_call_to_action);
                                LinearLayout fanNativeBackground = nativeAdView.findViewById(R.id.ad_unit);

                                if (darkTheme) {
                                    nativeAdTitle.setTextColor(
                                            ContextCompat.getColor(activity, R.color.applovin_dark_primary_text_color));
                                    nativeAdSocialContext.setTextColor(
                                            ContextCompat.getColor(activity, R.color.applovin_dark_primary_text_color));
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
                                nativeAdCallToAction
                                        .setVisibility(fanNativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
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
                                fanNativeAd.registerViewForInteraction(nativeAdView, nativeAdIcon, nativeAdMedia,
                                        clickableViews);

                            }

                            @Override
                            public void onAdClicked(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(com.facebook.ads.Ad ad) {

                            }
                        };

                        com.facebook.ads.NativeAd.NativeLoadAdConfig loadAdConfig = fanNativeAd.buildLoadAdConfig()
                                .withAdListener(nativeAdListener).build();
                        fanNativeAd.loadAd(loadAdConfig);
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        if (applovinNativeAd.getVisibility() != View.VISIBLE) {
                            nativeAdLoader = new MaxNativeAdLoader(appLovinNativeId, activity);
                            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                                @Override
                                public void onNativeAdLoaded(MaxNativeAdView nativeAdView, MaxAd ad) {
                                    if (maxNativeAd != null) {
                                        nativeAdLoader.destroy(maxNativeAd);
                                    }
                                    maxNativeAd = ad;
                                    applovinNativeAd.removeAllViews();
                                    applovinNativeAd.addView(nativeAdView);
                                    applovinNativeAd.setVisibility(View.VISIBLE);
                                    nativeAdViewContainer.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onNativeAdLoadFailed(String adUnitId, MaxError error) {
                                    applovinNativeAd.setVisibility(View.GONE);
                                    nativeAdViewContainer.setVisibility(View.GONE);
                                }

                                @Override
                                public void onNativeAdClicked(MaxAd ad) {
                                }

                                @Override
                                public void onNativeAdExpired(MaxAd ad) {
                                    nativeAdLoader.loadAd();
                                }
                            });

                            int layoutId = R.layout.gnt_applovin_medium_template_view;
                            switch (nativeAdStyle) {
                                case Constant.STYLE_NEWS:
                                    layoutId = R.layout.gnt_applovin_news_template_view;
                                    break;
                                case Constant.STYLE_VIDEO_SMALL:
                                    layoutId = R.layout.gnt_applovin_video_small_template_view;
                                    break;
                                case Constant.STYLE_VIDEO_LARGE:
                                    layoutId = R.layout.gnt_applovin_video_large_template_view;
                                    break;
                                case Constant.STYLE_RADIO:
                                case Constant.STYLE_SMALL:
                                    layoutId = R.layout.gnt_applovin_radio_template_view;
                                    break;
                            }
                            MaxNativeAdViewBinder binder = new MaxNativeAdViewBinder.Builder(layoutId)
                                    .setTitleTextViewId(R.id.native_ad_title)
                                    .setBodyTextViewId(R.id.native_ad_body)
                                    .setAdvertiserTextViewId(R.id.native_ad_sponsored_label)
                                    .setIconImageViewId(R.id.native_ad_icon)
                                    .setMediaContentViewGroupId(R.id.native_ad_media)
                                    .setOptionsContentViewGroupId(R.id.ad_choices_container)
                                    .setCallToActionButtonId(R.id.native_ad_call_to_action)
                                    .build();
                            nativeAdLoader.setNativeAdViewBinder(binder);
                            nativeAdLoader.loadAd();
                        }
                        break;

                    case STARTAPP:
                        startAppNativeAdObject = new StartAppNativeAd(activity);
                        StartAppNativeAd.NativeAdPreferences nativePrefs = new StartAppNativeAd.NativeAdPreferences()
                                .setAdsNumber(1)
                                .setAutoBitmapDownload(true)
                                .setPrimaryImageSize(2);
                        startAppNativeAdObject.loadAd(nativePrefs, new AdEventListener() {
                            @Override
                            public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                ArrayList<NativeAdDetails> ads = startAppNativeAdObject.getNativeAds();
                                if (ads.size() > 0) {
                                    NativeAdDetails nativeAd = ads.get(0);
                                    startappNativeTitle.setText(nativeAd.getTitle());
                                    startappNativeDescription.setText(nativeAd.getDescription());
                                    if (nativeAd.getSecondaryImageBitmap() != null) {
                                        startappNativeIcon.setImageBitmap(nativeAd.getSecondaryImageBitmap());
                                    } else if (nativeAd.getImageBitmap() != null) {
                                        startappNativeIcon.setImageBitmap(nativeAd.getImageBitmap());
                                    }
                                    if (nativeAd.getImageBitmap() != null) {
                                        startappNativeImage.setImageBitmap(nativeAd.getImageBitmap());
                                    }
                                    startappNativeButton.setText(nativeAd.isApp() ? "Install" : "Open");
                                    nativeAd.registerViewForInteraction(startappNativeAd);
                                    startappNativeAd.setVisibility(View.VISIBLE);
                                    nativeAdViewContainer.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                startappNativeAd.setVisibility(View.GONE);
                                nativeAdViewContainer.setVisibility(View.GONE);
                            }
                        });
                        break;

                    case WORTISE:
                        if (wortiseNativeAd.getVisibility() != View.VISIBLE) {
                            googleNativeAd = new GoogleNativeAd(activity, wortiseNativeId,
                                    new GoogleNativeAd.Listener() {
                                        @Override
                                        public void onNativeClicked(@NonNull GoogleNativeAd googleNativeAd) {
                                        }

                                        @Override
                                        public void onNativeImpression(@NonNull GoogleNativeAd googleNativeAd) {
                                        }

                                        @Override
                                        public void onNativeLoaded(@NonNull GoogleNativeAd googleNativeAd) {
                                            wortiseNativeAd.removeAllViews();
                                            int layoutId = R.layout.gnt_wortise_medium_template_view;
                                            switch (nativeAdStyle) {
                                                case Constant.STYLE_NEWS:
                                                    layoutId = R.layout.gnt_wortise_news_template_view;
                                                    break;
                                                case Constant.STYLE_VIDEO_SMALL:
                                                    layoutId = R.layout.gnt_wortise_video_small_template_view;
                                                    break;
                                                case Constant.STYLE_VIDEO_LARGE:
                                                    layoutId = R.layout.gnt_wortise_video_large_template_view;
                                                    break;
                                                case Constant.STYLE_RADIO:
                                                case Constant.STYLE_SMALL:
                                                    layoutId = R.layout.gnt_wortise_radio_template_view;
                                                    break;
                                            }
                                            com.google.android.gms.ads.nativead.NativeAdView nativeAdView = (com.google.android.gms.ads.nativead.NativeAdView) LayoutInflater
                                                    .from(activity).inflate(layoutId, null);
                                            ((TextView) nativeAdView.getHeadlineView())
                                                    .setText(googleNativeAd.getHeadline());
                                            ((TextView) nativeAdView.getBodyView()).setText(googleNativeAd.getBody());
                                            ((Button) nativeAdView.getCallToActionView())
                                                    .setText(googleNativeAd.getCallToAction());
                                            if (googleNativeAd.getIcon() != null) {
                                                ((ImageView) nativeAdView.getIconView())
                                                        .setImageDrawable(googleNativeAd.getIcon().getDrawable());
                                            } else {
                                                nativeAdView.getIconView().setVisibility(View.GONE);
                                            }
                                            wortiseNativeAd.addView(nativeAdView);
                                            wortiseNativeAd.setVisibility(View.VISIBLE);
                                            nativeAdViewContainer.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onNativeFailed(@NonNull GoogleNativeAd googleNativeAd,
                                                @NonNull com.wortise.ads.AdError adError) {
                                            wortiseNativeAd.setVisibility(View.GONE);
                                            nativeAdViewContainer.setVisibility(View.GONE);
                                        }
                                    });
                            googleNativeAd.load();
                        }
                        break;

                    default:
                        nativeAdViewContainer.setVisibility(View.GONE);
                        break;
                }

            }

        }

        public void setNativeAdPadding(int left, int top, int right, int bottom) {
            nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);
            nativeAdViewContainer.setPadding(left, top, right, bottom);
            if (darkTheme) {
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundDark));
            } else {
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundLight));
            }
        }

        public void setNativeAdMargin(int left, int top, int right, int bottom) {
            nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);
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
            nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);
            nativeAdViewContainer.setBackgroundResource(drawableBackground);
        }

        @SuppressWarnings("ConstantConditions")
        public void populateNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd,
                NativeAdView nativeAdView) {

            if (darkTheme) {
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundDark));
                nativeAdView.findViewById(R.id.background).setBackgroundResource(nativeBackgroundDark);
            } else {
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundLight));
                nativeAdView.findViewById(R.id.background).setBackgroundResource(nativeBackgroundLight);
            }

            nativeAdView.setMediaView(nativeAdView.findViewById(R.id.media_view));
            nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.primary));
            nativeAdView.setBodyView(nativeAdView.findViewById(R.id.body));
            nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.cta));
            nativeAdView.setIconView(nativeAdView.findViewById(R.id.icon));

            ((TextView) nativeAdView.getHeadlineView()).setText(nativeAd.getHeadline());
            nativeAdView.getMediaView().setMediaContent(nativeAd.getMediaContent());

            if (nativeAd.getBody() == null) {
                nativeAdView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                nativeAdView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) nativeAdView.getBodyView()).setText(nativeAd.getBody());
            }

            if (nativeAd.getCallToAction() == null) {
                nativeAdView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                nativeAdView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) nativeAdView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }

            if (nativeAd.getIcon() == null) {
                nativeAdView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) nativeAdView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                nativeAdView.getIconView().setVisibility(View.VISIBLE);
            }

            nativeAdView.setNativeAd(nativeAd);
        }

        /**
         * Destroys and releases all native ad resources to prevent memory leaks.
         * Should be called when the hosting Activity is destroyed.
         */
        public void destroyNativeAd() {
            if (admobNativeAd != null) {
                admobNativeAd.destroyNativeAd();
            }
            if (adManagerNativeAd != null) {
                adManagerNativeAd.destroyNativeAd();
            }
            if (fanNativeAd != null) {
                fanNativeAd.destroy();
                fanNativeAd = null;
            }
            if (nativeAdLoader != null && maxNativeAd != null) {
                nativeAdLoader.destroy(maxNativeAd);
                maxNativeAd = null;
            }
            if (googleNativeAd != null) {
                googleNativeAd.destroy();
                googleNativeAd = null;
            }
            if (startAppNativeAdObject != null) {
                startAppNativeAdObject = null;
            }
        }

    }

}
