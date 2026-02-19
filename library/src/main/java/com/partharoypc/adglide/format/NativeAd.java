package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_DISCOVERY;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.FACEBOOK;
import static com.partharoypc.adglide.util.Constant.FAN;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.app.Activity;
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
import com.partharoypc.adglide.util.TemplateView;
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
 * Uses a Builder pattern for configuration.
 */
public class NativeAd {

    public static class Builder {

        private static final String TAG = "AdNetwork";
        private final Activity activity;
        private LinearLayout nativeAdViewContainer;

        private MediaView mediaView;
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
        private MaxNativeAdLoader nativeAdLoader;
        private MaxAd maxNativeAd;

        private LinearLayout appLovinDiscoveryMrecAd;
        private FrameLayout wortiseNativeAd;

        private StartAppNativeAd startAppNativeAdObject;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private String adMobNativeId = "";
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
            this.waterfallManager = new WaterfallManager(backupAdNetwork);
            return this;
        }

        public Builder setBackupAdNetworks(String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks.length > 0) {
                this.backupAdNetwork = backupAdNetworks[0];
            }
            return this;
        }

        public Builder setAdMobNativeId(String adMobNativeId) {
            this.adMobNativeId = adMobNativeId;
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

        public Builder setAppLovinDiscMrecZoneId(String appLovinDiscMrecZoneId) {
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
            try {
                if (waterfallManager != null) {
                    waterfallManager.reset();
                }

                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {

                    nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);
                    admobNativeAd = activity.findViewById(R.id.admob_native_ad_container);
                    mediaView = activity.findViewById(R.id.media_view);
                    admobNativeBackground = activity.findViewById(R.id.background);
                    fanNativeAdLayout = activity.findViewById(R.id.fan_native_ad_container);
                    startappNativeAd = activity.findViewById(R.id.startapp_native_ad_container);
                    startappNativeImage = activity.findViewById(R.id.startapp_native_image);
                    startappNativeIcon = activity.findViewById(R.id.startapp_native_icon);
                    startappNativeTitle = activity.findViewById(R.id.startapp_native_title);
                    startappNativeDescription = activity.findViewById(R.id.startapp_native_description);
                    startappNativeButton = activity.findViewById(R.id.startapp_native_button);
                    if (startappNativeButton != null && startappNativeAd != null) {
                        startappNativeButton.setOnClickListener(v -> startappNativeAd.performClick());
                    }
                    startappNativeBackground = activity.findViewById(R.id.startapp_native_background);
                    applovinNativeAd = activity.findViewById(R.id.applovin_native_ad_container);
                    appLovinDiscoveryMrecAd = activity.findViewById(R.id.applovin_discovery_mrec_ad_container);
                    wortiseNativeAd = activity.findViewById(R.id.wortise_native_ad_container);

                    switch (adNetwork) {
                        case ADMOB:
                        case FAN_BIDDING_ADMOB: {
                            if (admobNativeAd.getVisibility() != View.VISIBLE) {
                                AdLoader adLoader = new AdLoader.Builder(activity, adMobNativeId)
                                        .forNativeAd(nativeAd -> {
                                            NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater()
                                                    .inflate(R.layout.gnt_admob_native_template_view, null);
                                            populateNativeAdView(nativeAd, nativeAdView);
                                            admobNativeAd.removeAllViews();
                                            admobNativeAd.addView(nativeAdView);
                                            admobNativeAd.setVisibility(View.VISIBLE);
                                            nativeAdViewContainer.setVisibility(View.VISIBLE);
                                        })
                                        .withAdListener(new com.google.android.gms.ads.AdListener() {
                                            @Override
                                            public void onAdFailedToLoad(
                                                    @NonNull com.google.android.gms.ads.LoadAdError adError) {
                                                loadBackupNativeAd();
                                            }
                                        })
                                        .build();
                                adLoader.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                            }
                            break;
                        }

                        case FAN:
                        case FACEBOOK: {
                            fanNativeAd = new com.facebook.ads.NativeAd(activity, fanNativeId);
                            NativeAdListener nativeAdListener = new NativeAdListener() {
                                @Override
                                public void onMediaDownloaded(Ad ad) {
                                }

                                @Override
                                public void onError(Ad ad, AdError adError) {
                                    fanNativeAdLayout.setVisibility(View.GONE);
                                    loadBackupNativeAd();
                                }

                                @Override
                                public void onAdLoaded(Ad ad) {
                                    fanNativeAdLayout.setVisibility(View.VISIBLE);
                                    nativeAdViewContainer.setVisibility(View.VISIBLE);
                                    if (fanNativeAd != ad)
                                        return;
                                    fanNativeAd.unregisterView();

                                    LayoutInflater inflater = LayoutInflater.from(activity);
                                    LinearLayout nativeAdView;

                                    switch (nativeAdStyle) {
                                        case Constant.STYLE_NEWS:
                                        case Constant.STYLE_MEDIUM:
                                            nativeAdView = (LinearLayout) inflater.inflate(
                                                    R.layout.gnt_fan_news_template_view, fanNativeAdLayout, false);
                                            break;
                                        case Constant.STYLE_VIDEO_SMALL:
                                            nativeAdView = (LinearLayout) inflater.inflate(
                                                    R.layout.gnt_fan_video_small_template_view, fanNativeAdLayout,
                                                    false);
                                            break;
                                        case Constant.STYLE_VIDEO_LARGE:
                                            nativeAdView = (LinearLayout) inflater.inflate(
                                                    R.layout.gnt_fan_video_large_template_view, fanNativeAdLayout,
                                                    false);
                                            break;
                                        case Constant.STYLE_RADIO:
                                        case Constant.STYLE_SMALL:
                                        default:
                                            nativeAdView = (LinearLayout) inflater.inflate(
                                                    R.layout.gnt_fan_radio_template_view, fanNativeAdLayout, false);
                                            break;
                                    }

                                    fanNativeAdLayout.removeAllViews();
                                    fanNativeAdLayout.addView(nativeAdView);

                                    LinearLayout adChoicesContainer = nativeAdView
                                            .findViewById(R.id.ad_choices_container);
                                    AdOptionsView adOptionsView = new AdOptionsView(activity, fanNativeAd,
                                            fanNativeAdLayout);
                                    adChoicesContainer.removeAllViews();
                                    adChoicesContainer.addView(adOptionsView, 0);

                                    TextView nativeAdTitle = nativeAdView.findViewById(R.id.native_ad_title);
                                    com.facebook.ads.MediaView nativeAdMedia = nativeAdView
                                            .findViewById(R.id.native_ad_media);
                                    com.facebook.ads.MediaView nativeAdIcon = nativeAdView
                                            .findViewById(R.id.native_ad_icon);
                                    TextView nativeAdSocialContext = nativeAdView
                                            .findViewById(R.id.native_ad_social_context);
                                    TextView nativeAdBody = nativeAdView.findViewById(R.id.native_ad_body);
                                    TextView sponsoredLabel = nativeAdView.findViewById(R.id.native_ad_sponsored_label);
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
                                        fanNativeBackground.setBackgroundResource(R.color.color_native_background_dark);
                                    }

                                    nativeAdTitle.setText(fanNativeAd.getAdvertiserName());
                                    nativeAdBody.setText(fanNativeAd.getAdBodyText());
                                    nativeAdSocialContext.setText(fanNativeAd.getAdSocialContext());
                                    nativeAdCallToAction.setVisibility(
                                            fanNativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                                    nativeAdCallToAction.setText(fanNativeAd.getAdCallToAction());
                                    sponsoredLabel.setText(fanNativeAd.getSponsoredTranslation());

                                    List<View> clickableViews = new ArrayList<>();
                                    clickableViews.add(nativeAdTitle);
                                    clickableViews.add(sponsoredLabel);
                                    clickableViews.add(nativeAdIcon);
                                    clickableViews.add(nativeAdMedia);
                                    clickableViews.add(nativeAdBody);
                                    clickableViews.add(nativeAdSocialContext);
                                    clickableViews.add(nativeAdCallToAction);

                                    fanNativeAd.registerViewForInteraction(nativeAdView, nativeAdIcon, nativeAdMedia,
                                            clickableViews);
                                }

                                @Override
                                public void onAdClicked(Ad ad) {
                                }

                                @Override
                                public void onLoggingImpression(Ad ad) {
                                }
                            };

                            fanNativeAd
                                    .loadAd(fanNativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case FAN_BIDDING_APPLOVIN_MAX: {
                            if (applovinNativeAd.getVisibility() != View.VISIBLE) {
                                nativeAdLoader = new MaxNativeAdLoader(appLovinNativeId, activity);
                                nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                                    @Override
                                    public void onNativeAdLoaded(MaxNativeAdView nativeAdView, MaxAd ad) {
                                        if (maxNativeAd != null)
                                            nativeAdLoader.destroy(maxNativeAd);
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
                                });
                                nativeAdLoader.loadAd();
                            }
                            break;
                        }

                        case STARTAPP: {
                            startAppNativeAdObject = new StartAppNativeAd(activity);
                            startAppNativeAdObject.loadAd(new NativeAdPreferences().setAdsNumber(1),
                                    new AdEventListener() {
                                        @Override
                                        public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad ad) {
                                            ArrayList<NativeAdDetails> ads = startAppNativeAdObject.getNativeAds();
                                            if (ads.size() > 0) {
                                                NativeAdDetails nativeAdDetails = ads.get(0);
                                                startappNativeImage.setImageBitmap(nativeAdDetails.getImageBitmap());
                                                startappNativeIcon
                                                        .setImageBitmap(nativeAdDetails.getSecondaryImageBitmap());
                                                startappNativeTitle.setText(nativeAdDetails.getTitle());
                                                startappNativeDescription.setText(nativeAdDetails.getDescription());
                                                startappNativeButton
                                                        .setText(nativeAdDetails.isApp() ? "Install" : "Open");
                                                nativeAdDetails.fillViewForInteraction(startappNativeAd);
                                                startappNativeAd.setVisibility(View.VISIBLE);
                                                nativeAdViewContainer.setVisibility(View.VISIBLE);
                                                if (darkTheme) {
                                                    startappNativeBackground.setBackgroundResource(
                                                            R.color.color_native_background_dark);
                                                    startappNativeTitle.setTextColor(ContextCompat.getColor(activity,
                                                            R.color.applovin_dark_primary_text_color));
                                                    startappNativeDescription.setTextColor(ContextCompat.getColor(
                                                            activity, R.color.applovin_dark_secondary_text_color));
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                            loadBackupNativeAd();
                                        }
                                    });
                            break;
                        }

                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading Native Ad: " + e.getMessage());
            }
        }

        public void loadBackupNativeAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    if (waterfallManager == null) {
                        if (!backupAdNetwork.isEmpty()) {
                            waterfallManager = new WaterfallManager(backupAdNetwork);
                        } else {
                            return;
                        }
                    }
                    String networkToLoad = waterfallManager.getNext();
                    if (networkToLoad == null)
                        return;
                    backupAdNetwork = networkToLoad;

                    switch (backupAdNetwork) {
                        case ADMOB:
                        case FAN_BIDDING_ADMOB: {
                            AdLoader adLoader = new AdLoader.Builder(activity, adMobNativeId)
                                    .forNativeAd(nativeAd -> {
                                        NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater()
                                                .inflate(R.layout.gnt_admob_native_template_view, null);
                                        populateNativeAdView(nativeAd, nativeAdView);
                                        admobNativeAd.removeAllViews();
                                        admobNativeAd.addView(nativeAdView);
                                        admobNativeAd.setVisibility(View.VISIBLE);
                                        nativeAdViewContainer.setVisibility(View.VISIBLE);
                                    })
                                    .withAdListener(new com.google.android.gms.ads.AdListener() {
                                        @Override
                                        public void onAdFailedToLoad(
                                                @NonNull com.google.android.gms.ads.LoadAdError adError) {
                                            loadBackupNativeAd();
                                        }
                                    })
                                    .build();
                            adLoader.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX: {
                            nativeAdLoader = new MaxNativeAdLoader(appLovinNativeId, activity);
                            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                                @Override
                                public void onNativeAdLoaded(MaxNativeAdView nativeAdView, MaxAd ad) {
                                    if (maxNativeAd != null)
                                        nativeAdLoader.destroy(maxNativeAd);
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
                            });
                            nativeAdLoader.loadAd();
                            break;
                        }

                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading Backup Native Ad: " + e.getMessage());
            }
        }

        public void setNativeAdPadding(int left, int top, int right, int bottom) {
            nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);
            if (nativeAdViewContainer != null) {
                nativeAdViewContainer.setPadding(left, top, right, bottom);
                if (darkTheme) {
                    nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundDark));
                } else {
                    nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundLight));
                }
            }
        }

        public void setNativeAdMargin(int left, int top, int right, int bottom) {
            nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);
            if (nativeAdViewContainer != null) {
                setMargins(nativeAdViewContainer, left, top, right, bottom);
            }
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
            if (nativeAdViewContainer != null) {
                nativeAdViewContainer.setBackgroundResource(drawableBackground);
            }
        }

        @SuppressWarnings("ConstantConditions")
        public void populateNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd,
                NativeAdView nativeAdView) {
            if (darkTheme) {
                if (nativeAdViewContainer != null)
                    nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundDark));
                nativeAdView.findViewById(R.id.background).setBackgroundResource(nativeBackgroundDark);
            } else {
                if (nativeAdViewContainer != null)
                    nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundLight));
                nativeAdView.findViewById(R.id.background).setBackgroundResource(nativeBackgroundLight);
            }

            nativeAdView.setMediaView(nativeAdView.findViewById(R.id.media_view));
            nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.primary));
            nativeAdView.setBodyView(nativeAdView.findViewById(R.id.body));
            nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.cta));
            nativeAdView.setIconView(nativeAdView.findViewById(R.id.icon));

            ((TextView) nativeAdView.getHeadlineView()).setText(nativeAd.getHeadline());
            if (nativeAdView.getMediaView() != null) {
                nativeAdView.getMediaView().setMediaContent(nativeAd.getMediaContent());
            }

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

        public void destroyNativeAd() {
            if (fanNativeAd != null) {
                fanNativeAd.destroy();
                fanNativeAd = null;
            }
            if (nativeAdLoader != null && maxNativeAd != null) {
                nativeAdLoader.destroy(maxNativeAd);
                maxNativeAd = null;
            }
            if (startAppNativeAdObject != null) {
                startAppNativeAdObject = null;
            }
        }
    }
}
