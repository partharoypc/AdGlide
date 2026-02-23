package com.partharoypc.adglide.format;

import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.AdGlideNativeStyle;
import com.partharoypc.adglide.util.WaterfallManager;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.app.Activity;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.startapp.sdk.ads.nativead.NativeAdDetails;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
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
import com.partharoypc.adglide.util.Constant;

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
        private MaxNativeAdLoader nativeAdLoader;
        private MaxAd maxNativeAd;
        private StartAppNativeAd startAppNativeAd;
        private String nativeAdStyle = "";

        private ProgressBar progressBarAd;

        private boolean adStatus = true;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
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
            return this;
        }

        @androidx.annotation.NonNull
        public Builder load() {
            loadNativeAd();
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

        @androidx.annotation.NonNull
        public Builder style(@androidx.annotation.NonNull String nativeAdStyle) {
            this.nativeAdStyle = nativeAdStyle;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder style(AdGlideNativeStyle nativeAdStyle) {
            return style(nativeAdStyle.getValue());
        }

        public void loadNativeAd() {
            loadNativeAdMain(false);
        }

        private void loadNativeAdMain(boolean isBackup) {
            try {
                if (adStatus && placementStatus != 0) {
                    if (!Tools.isNetworkAvailable(activity)) {
                        Log.e(TAG, "Internet connection not available. Skipping Native Ad load (ViewPager).");
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

        private void initializeViews() {
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
            if (startAppNativeButton != null && startAppNativeAdView != null) {
                startAppNativeButton.setOnClickListener(v -> startAppNativeAdView.performClick());
            }
            startAppNativeBackground = view.findViewById(R.id.start_app_native_background);
            appLovinNativeAd = view.findViewById(R.id.app_lovin_native_ad_container);
            progressBarAd = view.findViewById(R.id.progress_bar_ad);
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
                        .forNativeAd(nativeAd -> {
                            com.partharoypc.adglide.util.AdMobRateLimiter.resetCooldown(adMobNativeId);
                            if (darkTheme) {
                                ColorDrawable colorDrawable = new ColorDrawable(
                                        ContextCompat.getColor(activity, nativeBackgroundDark));
                                NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                        .withMainBackgroundColor(colorDrawable).build();
                                adMobNativeAdView.setStyles(styles);
                                if (adMobNativeBackground != null)
                                    adMobNativeBackground.setBackgroundResource(nativeBackgroundDark);
                            } else {
                                ColorDrawable colorDrawable = new ColorDrawable(
                                        ContextCompat.getColor(activity, nativeBackgroundLight));
                                NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                        .withMainBackgroundColor(colorDrawable).build();
                                adMobNativeAdView.setStyles(styles);
                                if (adMobNativeBackground != null)
                                    adMobNativeBackground.setBackgroundResource(nativeBackgroundLight);
                            }
                            if (adMobNativeAd != null)
                                adMobNativeAd.destroy();
                            adMobNativeAd = nativeAd;
                            if (mediaView != null)
                                mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                            adMobNativeAdView.setNativeAd(nativeAd);
                            adMobNativeAdView.setVisibility(View.VISIBLE);
                            if (progressBarAd != null)
                                progressBarAd.setVisibility(View.GONE);
                        })
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                if (adError.getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                                    com.partharoypc.adglide.util.AdMobRateLimiter.recordFailure(adMobNativeId);
                                }
                                if (adMobNativeAdView != null)
                                    adMobNativeAdView.setVisibility(View.GONE);
                                if (fallback != null)
                                    fallback.run();
                            }
                        }).build();
                adLoader.loadAd(Tools.getAdRequest(activity, legacyGDPR));
            } else if (fallback != null)
                fallback.run();
        }

        private void handleFacebookLoad(Runnable fallback) {
            if (metaNativeAdLayout != null) {
                try {
                    metaNativeAd = new com.facebook.ads.NativeAd(activity, metaNativeId);
                    NativeAdListener nativeAdListener = new NativeAdListener() {
                        @Override
                        public void onMediaDownloaded(com.facebook.ads.Ad ad) {
                        }

                        @Override
                        public void onAdClicked(com.facebook.ads.Ad ad) {
                        }

                        @Override
                        public void onLoggingImpression(com.facebook.ads.Ad ad) {
                        }

                        @Override
                        public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
                            if (metaNativeAdLayout != null)
                                metaNativeAdLayout.setVisibility(View.GONE);
                            if (fallback != null)
                                fallback.run();
                        }

                        @Override
                        public void onAdLoaded(com.facebook.ads.Ad ad) {
                            if (metaNativeAd == null || metaNativeAd != ad)
                                return;
                            metaNativeAdLayout.setVisibility(View.VISIBLE);
                            if (progressBarAd != null)
                                progressBarAd.setVisibility(View.GONE);
                            metaNativeAd.unregisterView();
                            LayoutInflater inflater = LayoutInflater.from(activity);
                            LinearLayout nativeAdView;
                            switch (nativeAdStyle) {
                                case Constant.STYLE_NEWS:
                                case Constant.STYLE_MEDIUM:
                                    nativeAdView = (LinearLayout) inflater.inflate(
                                            R.layout.adglide_meta_news_template_view, metaNativeAdLayout, false);
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
                            metaNativeAdLayout.removeAllViews();
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
                            if (darkTheme) {
                                int color = ContextCompat.getColor(activity,
                                        R.color.adglide_app_lovin_dark_primary_text_color);
                                nativeAdTitle.setTextColor(color);
                                nativeAdSocialContext.setTextColor(color);
                                int secondaryColor = ContextCompat.getColor(activity,
                                        R.color.adglide_app_lovin_dark_secondary_text_color);
                                sponsoredLabel.setTextColor(secondaryColor);
                                nativeAdBody.setTextColor(secondaryColor);
                                metaNativeBackground.setBackgroundResource(nativeBackgroundDark);
                            } else {
                                metaNativeBackground.setBackgroundResource(nativeBackgroundLight);
                            }
                            nativeAdTitle.setText(metaNativeAd.getAdvertiserName());
                            nativeAdBody.setText(metaNativeAd.getAdBodyText());
                            nativeAdSocialContext.setText(metaNativeAd.getAdSocialContext());
                            nativeAdCallToAction
                                    .setVisibility(metaNativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
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
                    };
                    metaNativeAd.loadAd(metaNativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
                } catch (Exception e) {
                    if (fallback != null)
                        fallback.run();
                }
            } else if (fallback != null)
                fallback.run();
        }

        private void handleAppLovinLoad(Runnable fallback) {
            if (appLovinNativeAd != null) {
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
                        if (progressBarAd != null)
                            progressBarAd.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNativeAdLoadFailed(String adUnitId, MaxError error) {
                        if (appLovinNativeAd != null)
                            appLovinNativeAd.setVisibility(View.GONE);
                        if (fallback != null)
                            fallback.run();
                    }
                });
                nativeAdLoader.loadAd();
            } else if (fallback != null)
                fallback.run();
        }

        private void handleStartAppLoad(Runnable fallback) {
            if (startAppNativeAdView != null) {
                startAppNativeAd = new StartAppNativeAd(activity);
                startAppNativeAd.loadAd(
                        new NativeAdPreferences().setAdsNumber(1).setAutoBitmapDownload(true).setPrimaryImageSize(2),
                        new AdEventListener() {
                            @Override
                            public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad ad) {
                                ArrayList<NativeAdDetails> ads = startAppNativeAd.getNativeAds();
                                if (ads.size() > 0) {
                                    populateStartAppNativeAdView(ads.get(0));
                                    startAppNativeAdView.setVisibility(View.VISIBLE);
                                    if (progressBarAd != null)
                                        progressBarAd.setVisibility(View.GONE);
                                } else if (fallback != null)
                                    fallback.run();
                            }

                            @Override
                            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                if (startAppNativeAdView != null)
                                    startAppNativeAdView.setVisibility(View.GONE);
                                if (fallback != null)
                                    fallback.run();
                            }
                        });
            } else if (fallback != null)
                fallback.run();
        }

        private void populateStartAppNativeAdView(NativeAdDetails nativeAdDetails) {
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
            if (startAppNativeBackground != null) {
                if (darkTheme)
                    startAppNativeBackground.setBackgroundResource(nativeBackgroundDark);
                else
                    startAppNativeBackground.setBackgroundResource(nativeBackgroundLight);
            }
            if (startAppNativeAdView != null) {
                nativeAdDetails.registerViewForInteraction(startAppNativeAdView);
            }
        }

        private void handleWortiseLoad(Runnable fallback) {
            if (fallback != null)
                fallback.run();
        }

        public void loadBackupNativeAd() {
            loadNativeAdMain(true);
        }

    }

}
