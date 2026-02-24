package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.NONE;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.AdGlideNativeStyle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewStub;

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

        private static final String TAG = "AdGlide";
        private final Activity activity;
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
        private MaxNativeAdLoader nativeAdLoader;
        private MaxAd maxNativeAd;

        private LinearLayout appLovinDiscoveryMrecAd;
        private FrameLayout wortiseNativeAd;

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
        public Builder background(int drawableBackground) {
            setNativeAdBackgroundResource(drawableBackground);
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
        public Builder zoneId(@androidx.annotation.NonNull String appLovinDiscMrecZoneId) {
            this.appLovinDiscMrecZoneId = appLovinDiscMrecZoneId;
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

        public void loadBackupNativeAd() {
            loadNativeAdMain(true);
        }

        private void loadNativeAdMain(boolean isBackup) {
            try {
                if (adStatus && placementStatus != 0) {
                    if (isBackup) {
                        if (!Tools.isNetworkAvailable(activity)) {
                            Log.e(TAG, "Internet connection not available. Skipping Backup Native Ad load.");
                            return;
                        }
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
                    } else {
                        if (!Tools.isNetworkAvailable(activity)) {
                            Log.e(TAG, "Internet connection not available. Skipping Primary Native Ad load.");
                            return;
                        }
                        if (waterfallManager != null) {
                            waterfallManager.reset();
                        }
                    }

                    String network = isBackup ? backupAdNetwork : adNetwork;
                    initializeViews(network);

                    Runnable fallback = () -> loadBackupNativeAd();

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
                        case NONE:
                            if (!isBackup) {
                                fallback.run();
                            } else {
                                loadBackupNativeAd();
                            }
                            break;
                        default:
                            Log.w(TAG, "Unsupported or null network: " + network);
                            if (fallback != null)
                                fallback.run();
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadNativeAdMain: " + e.getMessage());
                if (!isBackup)
                    loadBackupNativeAd();
            }
        }

        private void initializeViews(String network) {
            nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);

            if (network.equals(ADMOB) || network.equals(META_BIDDING_ADMOB)) {
                // Inflate AdMob ViewStub if present
                ViewStub adMobStub = activity.findViewById(R.id.ad_mob_native_ad_stub);
                if (adMobStub != null) {
                    adMobStub.inflate();
                }
                adMobNativeAdView = activity.findViewById(R.id.ad_mob_native_ad_container);
                mediaView = activity.findViewById(R.id.media_view);
                adMobNativeBackground = activity.findViewById(R.id.background);
            }

            if (network.equals(META)) {
                // Inflate Meta ViewStub if present
                ViewStub metaStub = activity.findViewById(R.id.meta_native_ad_stub);
                if (metaStub != null) {
                    try {
                        // Check if FAN class is available before inflating layout that uses it
                        Class.forName("com.facebook.ads.NativeAdLayout");
                        metaStub.inflate();
                    } catch (ClassNotFoundException e) {
                        Log.e(TAG, "Meta Audience Network SDK not found. Integration issue?");
                    }
                }
                metaNativeAdLayout = activity.findViewById(R.id.meta_native_ad_container);
            }

            if (network.equals(STARTAPP)) {
                startAppNativeAdView = activity.findViewById(R.id.start_app_native_ad_container);
                startAppNativeImage = activity.findViewById(R.id.start_app_native_image);
                startAppNativeIcon = activity.findViewById(R.id.start_app_native_icon);
                startAppNativeTitle = activity.findViewById(R.id.start_app_native_title);
                startAppNativeDescription = activity.findViewById(R.id.start_app_native_description);
                startAppNativeButton = activity.findViewById(R.id.start_app_native_button);
                if (startAppNativeButton != null && startAppNativeAdView != null) {
                    startAppNativeButton.setOnClickListener(v -> startAppNativeAdView.performClick());
                }
                startAppNativeBackground = activity.findViewById(R.id.start_app_native_background);
            }

            if (network.equals(APPLOVIN) || network.equals(APPLOVIN_MAX) || network.equals(META_BIDDING_APPLOVIN_MAX)) {
                appLovinNativeAd = activity.findViewById(R.id.app_lovin_native_ad_container);
                appLovinDiscoveryMrecAd = activity.findViewById(R.id.app_lovin_discovery_mrec_ad_container);
            }

            if (network.equals(WORTISE)) {
                wortiseNativeAd = activity.findViewById(R.id.wortise_native_ad_container);
            }
        }

        private void handleAdMobLoad(Runnable fallback) {
            try {
                if (adMobNativeAdView == null) {
                    if (fallback != null)
                        fallback.run();
                    return;
                }
                if (adMobNativeAdView.getVisibility() != View.VISIBLE) {
                    if (!com.partharoypc.adglide.util.AdMobRateLimiter.isRequestAllowed(adMobNativeId)) {
                        if (fallback != null)
                            fallback.run();
                        return;
                    }
                    AdLoader adLoader = new AdLoader.Builder(activity, adMobNativeId)
                            .forNativeAd(nativeAd -> {
                                com.partharoypc.adglide.util.AdMobRateLimiter.resetCooldown(adMobNativeId);
                                if (adMobNativeAd != null) {
                                    adMobNativeAd.destroy();
                                }
                                adMobNativeAd = nativeAd;
                                NativeAdView nativeAdView = adMobNativeAdView.findViewById(R.id.native_ad_view);
                                if (nativeAdView == null) {
                                    activity.getLayoutInflater().inflate(
                                            com.partharoypc.adglide.R.layout.adglide_ad_mob_medium_template_view,
                                            adMobNativeAdView, true);
                                    nativeAdView = adMobNativeAdView.findViewById(R.id.native_ad_view);
                                }
                                populateNativeAdView(nativeAd, nativeAdView);
                                animateIn(adMobNativeAdView);
                                animateIn(nativeAdViewContainer);
                            })
                            .withAdListener(new com.google.android.gms.ads.AdListener() {
                                @Override
                                public void onAdFailedToLoad(@NonNull com.google.android.gms.ads.LoadAdError adError) {
                                    if (adError.getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                                        com.partharoypc.adglide.util.AdMobRateLimiter.recordFailure(adMobNativeId);
                                    }
                                    if (fallback != null)
                                        fallback.run();
                                }
                            })
                            .build();
                    adLoader.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                }
            } catch (NoClassDefFoundError | Exception e) {
                Log.e(TAG, "Failed to load AdMob native ad. Error: " + e.getMessage());
                if (fallback != null)
                    fallback.run();
            }
        }

        private void handleFacebookLoad(Runnable fallback) {
            try {
                if (metaNativeAdLayout == null) {
                    if (fallback != null)
                        fallback.run();
                    return;
                }
                metaNativeAd = new com.facebook.ads.NativeAd(activity, metaNativeId);
                NativeAdListener nativeAdListener = new NativeAdListener() {
                    @Override
                    public void onMediaDownloaded(Ad ad) {
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        metaNativeAdLayout.setVisibility(View.GONE);
                        if (fallback != null)
                            fallback.run();
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        animateIn(metaNativeAdLayout);
                        animateIn(nativeAdViewContainer);
                        if (metaNativeAd != ad)
                            return;
                        metaNativeAd.unregisterView();

                        LayoutInflater inflater = LayoutInflater.from(activity);
                        LinearLayout nativeAdView;
                        int layoutRes;
                        AdGlideNativeStyle style = AdGlideNativeStyle.fromString(nativeAdStyle);
                        switch (style) {
                            case NEWS:
                            case MEDIUM:
                                layoutRes = R.layout.adglide_meta_news_template_view;
                                break;
                            case VIDEO_SMALL:
                                layoutRes = R.layout.adglide_meta_video_small_template_view;
                                break;
                            case VIDEO_LARGE:
                                layoutRes = R.layout.adglide_meta_video_large_template_view;
                                break;
                            case RADIO:
                            case SMALL:
                            default:
                                layoutRes = R.layout.adglide_meta_radio_template_view;
                                break;
                        }
                        nativeAdView = (LinearLayout) inflater.inflate(layoutRes, metaNativeAdLayout, false);
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
                            metaNativeBackground.setBackgroundResource(android.R.color.transparent);
                        } else {
                            metaNativeBackground.setBackgroundResource(android.R.color.transparent);
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

                    @Override
                    public void onAdClicked(Ad ad) {
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {
                    }
                };
                metaNativeAd.loadAd(metaNativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
            } catch (NoClassDefFoundError | Exception e) {
                Log.e(TAG, "Failed to load Meta native ad. Error: " + e.getMessage());
                if (fallback != null)
                    fallback.run();
            }
        }

        private void handleAppLovinLoad(Runnable fallback) {
            try {
                if (appLovinNativeAd == null) {
                    if (fallback != null)
                        fallback.run();
                    return;
                }
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

        private void handleWortiseLoad(Runnable fallback) {
            // Wortise Native Ads implementation is temporarily disabled due to SDK version
            // conflicts
            if (fallback != null)
                fallback.run();
        }

        public void setNativeAdPadding(int left, int top, int right, int bottom) {
            nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);
            if (nativeAdViewContainer != null) {
                nativeAdViewContainer.setPadding(left, top, right, bottom);
                int colorRes = darkTheme ? nativeBackgroundDark : nativeBackgroundLight;
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, colorRes));
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

        private void animateIn(View view) {
            if (view != null) {
                view.setAlpha(0f);
                view.setVisibility(View.VISIBLE);
                view.animate().alpha(1f).setDuration(400).start();
            }
        }

        @SuppressWarnings("ConstantConditions")
        public void populateNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd,
                NativeAdView nativeAdView) {
            if (nativeAdViewContainer != null)
                nativeAdViewContainer.setBackgroundColor(Color.TRANSPARENT);

            View background = nativeAdView.findViewById(R.id.background);
            if (background != null)
                background.setBackgroundResource(android.R.color.transparent);

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
            if (nativeAdViewContainer != null)
                nativeAdViewContainer.setBackgroundColor(Color.TRANSPARENT);

            if (startAppNativeBackground != null)
                startAppNativeBackground.setBackgroundResource(android.R.color.transparent);

            if (startAppNativeTitle != null)
                startAppNativeTitle.setText(nativeAdDetails.getTitle());

            if (startAppNativeDescription != null)
                startAppNativeDescription.setText(nativeAdDetails.getDescription());

            if (startAppNativeImage != null)
                startAppNativeImage.setImageBitmap(nativeAdDetails.getImageBitmap());

            if (startAppNativeIcon != null)
                startAppNativeIcon.setImageBitmap(nativeAdDetails.getSecondaryImageBitmap());

            startAppNativeButton.setText(nativeAdDetails.isApp() ? "Install" : "Open");

            nativeAdDetails.registerViewForInteraction(startAppNativeAdView);
        }

        public void destroyNativeAd() {
            if (adMobNativeAd != null) {
                adMobNativeAd.destroy();
                adMobNativeAd = null;
            }
            if (metaNativeAd != null) {
                metaNativeAd.destroy();
                metaNativeAd = null;
            }
            if (nativeAdLoader != null && maxNativeAd != null) {
                nativeAdLoader.destroy(maxNativeAd);
                maxNativeAd = null;
            }
            if (startAppNativeAd != null) {
                startAppNativeAd = null;
            }
        }
    }
}
