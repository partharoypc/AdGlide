package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.app.Activity;
import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.AdGlideNativeStyle;
import com.partharoypc.adglide.util.WaterfallManager;
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

    private static void animateIn(View view) {
        if (view != null) {
            view.setAlpha(0f);
            view.setVisibility(View.VISIBLE);
            view.animate().alpha(1f).setDuration(400).start();
        }
    }

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

        private boolean adStatus = true;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
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
        public Builder padding(int left, int top, int right, int bottom) {
            setNativeAdPadding(left, top, right, bottom);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder margin(int left, int top, int right, int bottom) {
            setNativeAdMargin(left, top, right, bottom);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder backgroundResource(int drawableBackground) {
            setNativeAdBackgroundResource(drawableBackground);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder view(View view) {
            this.view = view;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder status(boolean adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder network(@androidx.annotation.NonNull String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder network(AdGlideNetwork network) {
            return network(network.getValue());
        }

        @androidx.annotation.Nullable
        public Builder backup(@androidx.annotation.Nullable String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            this.waterfallManager = new WaterfallManager(backupAdNetwork);
            return this;
        }

        @androidx.annotation.Nullable
        public Builder backup(AdGlideNetwork backupAdNetwork) {
            return backup(backupAdNetwork.getValue());
        }

        @androidx.annotation.Nullable
        public Builder backups(String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks.length > 0) {
                this.backupAdNetwork = backupAdNetworks[0];
            }
            return this;
        }

        @androidx.annotation.Nullable
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return backups(AdGlideNetwork.toStringArray(backupAdNetworks));
        }

        @androidx.annotation.NonNull
        public Builder adMobId(@androidx.annotation.NonNull String adMobNativeId) {
            this.adMobNativeId = adMobNativeId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder zoneId(@androidx.annotation.NonNull String appLovinDiscMrecZoneId) {
            this.appLovinDiscMrecZoneId = appLovinDiscMrecZoneId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder metaId(@androidx.annotation.NonNull String metaNativeId) {
            this.metaNativeId = metaNativeId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder appLovinId(@androidx.annotation.NonNull String appLovinNativeId) {
            this.appLovinNativeId = appLovinNativeId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder wortiseId(@androidx.annotation.NonNull String wortiseNativeId) {
            this.wortiseNativeId = wortiseNativeId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder placement(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder darkTheme(boolean darkTheme) {
            this.darkTheme = darkTheme;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder legacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder style(@androidx.annotation.NonNull String nativeAdStyle) {
            this.nativeAdStyle = nativeAdStyle;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder style(AdGlideNativeStyle nativeAdStyle) {
            return style(nativeAdStyle.getValue());
        }

        @androidx.annotation.NonNull
        public Builder backgroundColor(int colorLight, int colorDark) {
            this.nativeBackgroundLight = colorLight;
            this.nativeBackgroundDark = colorDark;
            return this;
        }

        public void loadNativeAd() {
            loadNativeAdMain(false);
        }

        private void loadNativeAdMain(boolean isBackup) {
            try {
                if (adStatus && placementStatus != 0) {
                    if (!Tools.isNetworkAvailable(activity)) {
                        Log.e(TAG, "Internet connection not available. Skipping Native Ad load.");
                        return;
                    }
                    if (isBackup) {
                        if (waterfallManager == null) {
                            if (!backupAdNetwork.isEmpty()) {
                                waterfallManager = new WaterfallManager(backupAdNetwork);
                            } else {
                                return;
                            }
                        }
                        String networkToLoad = waterfallManager.getNext();
                        if (networkToLoad == null) {
                            Log.d(TAG, "All backup native ads failed to load");
                            return;
                        }
                        backupAdNetwork = networkToLoad;
                    } else if (waterfallManager != null) {
                        waterfallManager.reset();
                    }

                    String network = isBackup ? backupAdNetwork : adNetwork;
                    initializeViews();

                    Runnable fallback = this::loadBackupNativeAd;

                    switch (network) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            handleAdMobLoad(fallback);
                            break;
                        case META:
                            handleFacebookLoad(fallback);
                            break;
                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX:
                            handleAppLovinLoad(fallback);
                            break;
                        case STARTAPP:
                            handleStartAppLoad(fallback);
                            break;
                        case WORTISE:
                            handleWortiseLoad(fallback);
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadNativeAdMain: " + e.getMessage());
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
                            animateIn(appLovinNativeAd);
                            animateIn(nativeAdViewContainer);
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
                            animateIn(startAppNativeAdView);
                            animateIn(nativeAdViewContainer);
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

        private void handleAdMobLoad(Runnable fallback) {
            if (adMobNativeAdView != null && adMobNativeId != null && !adMobNativeId.isEmpty()) {
                if (!com.partharoypc.adglide.util.AdMobRateLimiter.isRequestAllowed(adMobNativeId)) {
                    adMobNativeAdView.setVisibility(View.GONE);
                    if (fallback != null)
                        fallback.run();
                    return;
                }
                AdLoader adLoader = new AdLoader.Builder(activity, adMobNativeId)
                        .forNativeAd(NativeAd -> {
                            com.partharoypc.adglide.util.AdMobRateLimiter.resetCooldown(adMobNativeId);
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
                            animateIn(adMobNativeAdView);
                            animateIn(nativeAdViewContainer);
                        })
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                if (adError.getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                                    com.partharoypc.adglide.util.AdMobRateLimiter.recordFailure(adMobNativeId);
                                }
                                adMobNativeAdView.setVisibility(View.GONE);
                                if (fallback != null)
                                    fallback.run();
                            }
                        })
                        .build();
                adLoader.loadAd(Tools.getAdRequest(activity, legacyGDPR));
            } else {
                Log.d(TAG, "AdMob Native Ad ID is empty or view is missing");
                if (fallback != null)
                    fallback.run();
            }
        }

        private void handleFacebookLoad(Runnable fallback) {
            try {
                metaNativeAd = new com.facebook.ads.NativeAd(activity, metaNativeId);
                NativeAdListener nativeAdListener = new NativeAdListener() {
                    @Override
                    public void onMediaDownloaded(com.facebook.ads.Ad ad) {
                    }

                    @Override
                    public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
                        metaNativeAdLayout.setVisibility(View.GONE);
                        if (fallback != null)
                            fallback.run();
                    }

                    @Override
                    public void onAdLoaded(com.facebook.ads.Ad ad) {
                        animateIn(metaNativeAdLayout);
                        animateIn(nativeAdViewContainer);
                        if (metaNativeAd != ad) {
                            return;
                        }
                        metaNativeAd.unregisterView();
                        LayoutInflater inflater = LayoutInflater.from(activity);
                        LinearLayout nativeAdView;

                        switch (nativeAdStyle) {
                            case Constant.STYLE_NEWS:
                            case Constant.STYLE_MEDIUM:
                                nativeAdView = (LinearLayout) inflater
                                        .inflate(R.layout.adglide_meta_news_template_view, metaNativeAdLayout, false);
                                break;
                            case Constant.STYLE_VIDEO_SMALL:
                                nativeAdView = (LinearLayout) inflater.inflate(
                                        R.layout.adglide_meta_video_small_template_view, metaNativeAdLayout, false);
                                break;
                            case Constant.STYLE_VIDEO_LARGE:
                                nativeAdView = (LinearLayout) inflater.inflate(
                                        R.layout.adglide_meta_video_large_template_view, metaNativeAdLayout, false);
                                break;
                            case Constant.STYLE_RADIO:
                            case Constant.STYLE_SMALL:
                                nativeAdView = (LinearLayout) inflater.inflate(
                                        R.layout.adglide_meta_radio_template_view, metaNativeAdLayout, false);
                                break;
                            default:
                                nativeAdView = (LinearLayout) inflater.inflate(
                                        R.layout.adglide_meta_medium_template_view, metaNativeAdLayout, false);
                                break;
                        }
                        metaNativeAdLayout.addView(nativeAdView);

                        LinearLayout adChoicesContainer = nativeAdView.findViewById(R.id.ad_choices_container);
                        AdOptionsView adOptionsView = new AdOptionsView(activity, metaNativeAd, metaNativeAdLayout);
                        adChoicesContainer.removeAllViews();
                        adChoicesContainer.addView(adOptionsView, 0);

                        TextView nativeAdTitle = nativeAdView.findViewById(R.id.native_ad_title);
                        com.facebook.ads.MediaView nativeAdMedia = nativeAdView.findViewById(R.id.native_ad_media);
                        com.facebook.ads.MediaView nativeAdIcon = nativeAdView.findViewById(R.id.native_ad_icon);
                        TextView nativeAdSocialContext = nativeAdView.findViewById(R.id.native_ad_social_context);
                        TextView nativeAdBody = nativeAdView.findViewById(R.id.native_ad_body);
                        TextView sponsoredLabel = nativeAdView.findViewById(R.id.native_ad_sponsored_label);
                        Button nativeAdCallToAction = nativeAdView.findViewById(R.id.native_ad_call_to_action);
                        LinearLayout metaNativeBackground = nativeAdView.findViewById(R.id.ad_unit);

                        metaNativeBackground.setBackgroundResource(android.R.color.transparent);

                        nativeAdTitle.setText(metaNativeAd.getAdvertiserName());
                        nativeAdBody.setText(metaNativeAd.getAdBodyText());
                        nativeAdSocialContext.setText(metaNativeAd.getAdSocialContext());
                        nativeAdCallToAction.setVisibility(
                                metaNativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                        nativeAdCallToAction.setText(metaNativeAd.getAdCallToAction());
                        sponsoredLabel.setText(metaNativeAd.getSponsoredTranslation());

                        List<View> clickableViews = new ArrayList<>();
                        clickableViews.add(nativeAdTitle);
                        clickableViews.add(sponsoredLabel);
                        clickableViews.add(nativeAdIcon);
                        clickableViews.add(nativeAdMedia);
                        clickableViews.add(nativeAdBody);
                        clickableViews.add(nativeAdSocialContext);
                        clickableViews.add(nativeAdCallToAction);

                        metaNativeAd.registerViewForInteraction(nativeAdView, nativeAdIcon, nativeAdMedia,
                                clickableViews);
                    }

                    @Override
                    public void onAdClicked(com.facebook.ads.Ad ad) {
                    }

                    @Override
                    public void onLoggingImpression(com.facebook.ads.Ad ad) {
                    }
                };

                com.facebook.ads.NativeAd.NativeLoadAdConfig loadAdConfig = metaNativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener).build();
                metaNativeAd.loadAd(loadAdConfig);
            } catch (NoClassDefFoundError | Exception e) {
                Log.e(TAG, "Failed to load Meta native ad. Error: " + e.getMessage());
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

        private void initializeViews() {
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
            loadNativeAdMain(true);
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
