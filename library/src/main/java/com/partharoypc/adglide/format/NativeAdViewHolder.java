package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;

import android.app.Activity;
import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

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
import com.partharoypc.adglide.util.Constant;
import com.partharoypc.adglide.util.NativeTemplateStyle;
import com.partharoypc.adglide.util.TemplateView;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;

import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import com.startapp.sdk.ads.nativead.NativeAdPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView ViewHolder for displaying native ads within a list.
 * Binds ad views for multiple ad networks.
 */
public class NativeAdViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "AdGlide";
    private LinearLayout nativeAdViewContainer;

    // AdMob
    private MediaView mediaView;
    private com.google.android.gms.ads.nativead.NativeAd adMobNativeAd;
    private TemplateView adMobNativeAdView;
    private LinearLayout adMobNativeBackground;

    // Meta
    private com.facebook.ads.NativeAd metaNativeAd;
    private NativeAdLayout metaNativeAdLayout;

    // StartApp
    private View startAppNativeAdView;
    private ImageView startAppNativeImage;
    private ImageView startAppNativeIcon;
    private TextView startAppNativeTitle;
    private TextView startAppNativeDescription;
    private Button startAppNativeButton;
    private LinearLayout startAppNativeBackground;

    // AppLovin
    private FrameLayout appLovinNativeAd;
    private LinearLayout appLovinDiscoveryMrecAd;

    // Wortise
    private FrameLayout wortiseNativeAd;

    // Ad Loaders
    private MaxNativeAdLoader nativeAdLoader;
    private MaxAd maxNativeAd;
    private WaterfallManager waterfallManager;

    public NativeAdViewHolder(View view) {
        super(view);

        nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);

        // AdMob
        // Inflate AdMob ViewStub if present
        android.view.ViewStub adMobStub = view.findViewById(R.id.ad_mob_native_ad_stub);
        if (adMobStub != null) {
            adMobStub.inflate();
        }
        adMobNativeAdView = view.findViewById(R.id.ad_mob_native_ad_container);
        mediaView = view.findViewById(R.id.media_view);
        adMobNativeBackground = view.findViewById(R.id.background);

        // Ad Manager

        // Meta
        // Inflate Meta ViewStub if present
        android.view.ViewStub metaStub = view.findViewById(R.id.meta_native_ad_stub);
        if (metaStub != null) {
            metaStub.inflate();
        }
        metaNativeAdLayout = view.findViewById(R.id.meta_native_ad_container);

        // StartApp
        startAppNativeAdView = view.findViewById(R.id.start_app_native_ad_container);
        startAppNativeImage = view.findViewById(R.id.start_app_native_image);
        startAppNativeIcon = view.findViewById(R.id.start_app_native_icon);
        startAppNativeTitle = view.findViewById(R.id.start_app_native_title);
        startAppNativeDescription = view.findViewById(R.id.start_app_native_description);
        startAppNativeButton = view.findViewById(R.id.start_app_native_button);
        startAppNativeButton.setOnClickListener(v1 -> itemView.performClick());
        startAppNativeBackground = view.findViewById(R.id.start_app_native_background);

        // AppLovin
        appLovinNativeAd = view.findViewById(R.id.app_lovin_native_ad_container);
        appLovinDiscoveryMrecAd = view.findViewById(R.id.app_lovin_discovery_mrec_ad_container);

        wortiseNativeAd = view.findViewById(R.id.wortise_native_ad_container);

    }

    public void loadNativeAd(Context context, boolean adStatus, int placementStatus, String adNetwork,
            String backupAdNetwork, String adMobNativeId, String metaNativeId,
            String appLovinNativeId, String appLovinDiscMrecZoneId, String wortiseNativeId,
            boolean darkTheme, boolean legacyGDPR, String nativeAdStyle, int nativeBackgroundLight,
            int nativeBackgroundDark) {
        this.waterfallManager = new WaterfallManager(backupAdNetwork);
        loadNativeAdMain(context, adStatus, placementStatus, adNetwork, backupAdNetwork, adMobNativeId, metaNativeId,
                appLovinNativeId, appLovinDiscMrecZoneId, wortiseNativeId, darkTheme, legacyGDPR, nativeAdStyle,
                nativeBackgroundLight, nativeBackgroundDark, false);
    }

    public void loadBackupNativeAd(Context context, boolean adStatus, int placementStatus, String backupAdNetwork,
            String adMobNativeId, String metaNativeId, String appLovinNativeId,
            String appLovinDiscMrecZoneId, String wortiseNativeId, boolean darkTheme,
            boolean legacyGDPR, String nativeAdStyle, int nativeBackgroundLight, int nativeBackgroundDark) {
        loadNativeAdMain(context, adStatus, placementStatus, "", backupAdNetwork, adMobNativeId, metaNativeId,
                appLovinNativeId, appLovinDiscMrecZoneId, wortiseNativeId, darkTheme, legacyGDPR, nativeAdStyle,
                nativeBackgroundLight, nativeBackgroundDark, true);
    }

    public void setNativeAdPadding(int left, int top, int right, int bottom) {
        nativeAdViewContainer.setPadding(left, top, right, bottom);
    }

    public void setNativeAdMargin(int left, int top, int right, int bottom) {
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
        nativeAdViewContainer.setBackgroundResource(drawableBackground);
    }

    public void setNativeAdBackgroundColor(Context context, boolean darkTheme, int nativeBackgroundLight,
            int nativeBackgroundDark) {
        if (darkTheme) {
            nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(context, nativeBackgroundDark));
        } else {
            nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(context, nativeBackgroundLight));
        }
    }

    private void setAdMobStyle(Context context, boolean darkTheme, int backgroundLight, int backgroundDark) {
        int colorRes = darkTheme ? backgroundDark : backgroundLight;
        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context, colorRes));
        NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                .withMainBackgroundColor(colorDrawable).build();
        adMobNativeAdView.setStyles(styles);
        adMobNativeBackground.setBackgroundResource(android.R.color.transparent);
    }

    private void animateIn(View view) {
        if (view != null) {
            view.setAlpha(0f);
            view.setVisibility(View.VISIBLE);
            view.animate().alpha(1f).setDuration(400).start();
        }
    }

    private void handleAdMobLoad(Context context, String adMobNativeId, boolean darkTheme, int backgroundLight,
            int backgroundDark, boolean legacyGDPR, Runnable fallback) {
        if (adMobNativeAdView != null && adMobNativeId != null && !adMobNativeId.isEmpty()) {
            if (!com.partharoypc.adglide.util.AdMobRateLimiter.isRequestAllowed(adMobNativeId)) {
                adMobNativeAdView.setVisibility(View.GONE);
                if (fallback != null)
                    fallback.run();
                return;
            }
            adMobNativeAdView.setVisibility(View.VISIBLE);
            nativeAdViewContainer.setVisibility(View.VISIBLE);
            AdLoader adLoader = new AdLoader.Builder(context, adMobNativeId)
                    .forNativeAd(nativeAd -> {
                        com.partharoypc.adglide.util.AdMobRateLimiter.resetCooldown(adMobNativeId);
                        if (adMobNativeAd != null) {
                            adMobNativeAd.destroy();
                        }
                        adMobNativeAd = nativeAd;
                        setAdMobStyle(context, darkTheme, backgroundLight, backgroundDark);
                        mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                        adMobNativeAdView.setNativeAd(nativeAd);
                        animateIn(adMobNativeAdView);
                        animateIn(nativeAdViewContainer);
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                            if (adError.getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                                com.partharoypc.adglide.util.AdMobRateLimiter.recordFailure(adMobNativeId);
                            }
                            if (fallback != null) {
                                fallback.run();
                            } else {
                                adMobNativeAdView.setVisibility(View.GONE);
                                nativeAdViewContainer.setVisibility(View.GONE);
                            }
                        }
                    })
                    .build();
            adLoader.loadAd(Tools.getAdRequest((Activity) context, legacyGDPR));
        } else if (fallback != null) {
            fallback.run();
        }
    }

    private void handleFacebookLoad(Context context, String metaNativeId, String nativeAdStyle, boolean darkTheme,
            int backgroundLight, int backgroundDark, Runnable fallback) {
        if (metaNativeAdLayout.getVisibility() != View.VISIBLE) {
            metaNativeAd = new com.facebook.ads.NativeAd(context, metaNativeId);
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(com.facebook.ads.Ad ad) {
                }

                @Override
                public void onError(com.facebook.ads.Ad ad, AdError adError) {
                    if (fallback != null) {
                        fallback.run();
                    }
                }

                @Override
                public void onAdLoaded(com.facebook.ads.Ad ad) {
                    if (metaNativeAd != ad) {
                        return;
                    }
                    animateIn(metaNativeAdLayout);
                    animateIn(nativeAdViewContainer);
                    metaNativeAd.unregisterView();

                    LayoutInflater inflater = LayoutInflater.from(context);
                    LinearLayout nativeAdView;
                    int layoutRes;
                    switch (nativeAdStyle) {
                        case Constant.STYLE_NEWS:
                        case Constant.STYLE_MEDIUM:
                            layoutRes = R.layout.adglide_meta_news_template_view;
                            break;
                        case Constant.STYLE_VIDEO_SMALL:
                            layoutRes = R.layout.adglide_meta_video_small_template_view;
                            break;
                        case Constant.STYLE_VIDEO_LARGE:
                            layoutRes = R.layout.adglide_meta_video_large_template_view;
                            break;
                        case Constant.STYLE_RADIO:
                        case Constant.STYLE_SMALL:
                            layoutRes = R.layout.adglide_meta_radio_template_view;
                            break;
                        default:
                            layoutRes = R.layout.adglide_meta_medium_template_view;
                            break;
                    }
                    nativeAdView = (LinearLayout) inflater.inflate(layoutRes, metaNativeAdLayout, false);
                    metaNativeAdLayout.addView(nativeAdView);

                    LinearLayout adChoicesContainer = nativeAdView.findViewById(R.id.ad_choices_container);
                    AdOptionsView adOptionsView = new AdOptionsView(context, metaNativeAd, metaNativeAdLayout);
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
                    nativeAdCallToAction.setVisibility(metaNativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
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
                    metaNativeAd.registerViewForInteraction(nativeAdView, nativeAdIcon, nativeAdMedia, clickableViews);
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
        } else {
            Log.d(TAG, "Meta Native Ad has been loaded");
        }
    }

    private void handleAppLovinLoad(Context context, String appLovinNativeId, Runnable fallback) {
        if (appLovinNativeAd.getVisibility() != View.VISIBLE) {
            nativeAdLoader = new MaxNativeAdLoader(appLovinNativeId, context);
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(MaxNativeAdView nativeAdView, MaxAd ad) {
                    if (maxNativeAd != null) {
                        nativeAdLoader.destroy(maxNativeAd);
                    }
                    maxNativeAd = ad;
                    appLovinNativeAd.removeAllViews();
                    appLovinNativeAd.addView(nativeAdView);
                    animateIn(appLovinNativeAd);
                    animateIn(nativeAdViewContainer);
                }

                @Override
                public void onNativeAdLoadFailed(String adUnitId, MaxError error) {
                    if (fallback != null) {
                        fallback.run();
                    }
                }
            });
            nativeAdLoader.loadAd();
        } else {
            Log.d(TAG, "AppLovin Native Ad has been loaded");
        }
    }

    private void handleStartAppLoad(Context context, boolean darkTheme, Runnable fallback) {
        try {
            com.startapp.sdk.ads.nativead.StartAppNativeAd startAppNativeAd = new com.startapp.sdk.ads.nativead.StartAppNativeAd(
                    context);
            NativeAdPreferences nativePrefs = new NativeAdPreferences()
                    .setAdsNumber(1)
                    .setAutoBitmapDownload(true)
                    .setSecondaryImageSize(1);

            AdEventListener adEventListener = new AdEventListener() {
                @Override
                public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad ad) {
                    ArrayList<com.startapp.sdk.ads.nativead.NativeAdDetails> ads = startAppNativeAd.getNativeAds();
                    if (ads != null && !ads.isEmpty()) {
                        com.startapp.sdk.ads.nativead.NativeAdDetails nativeAdDetails = ads.get(0);
                        populateStartAppNativeAdView(context, nativeAdDetails, darkTheme);
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

    private void populateStartAppNativeAdView(Context context,
            com.startapp.sdk.ads.nativead.NativeAdDetails nativeAdDetails, boolean darkTheme) {
        int bgColor = darkTheme ? R.color.adglide_color_native_background_dark
                : R.color.adglide_color_native_background_light;
        nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(context, bgColor));
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

    public void destroyAd() {
        if (adMobNativeAd != null) {
            adMobNativeAd.destroy();
            adMobNativeAd = null;
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

    private void handleWortiseLoad(Context context, String wortiseNativeId, boolean darkTheme, int backgroundDark,
            int backgroundLight, Runnable fallback) {
        // Wortise Native Ads implementation is temporarily disabled due to SDK version
        // conflicts
        if (fallback != null)
            fallback.run();
    }

    private void loadNativeAdMain(Context context, boolean adStatus, int placementStatus, String adNetwork,
            String backupAdNetwork, String adMobNativeId, String metaNativeId,
            String appLovinNativeId, String appLovinDiscMrecZoneId, String wortiseNativeId,
            boolean darkTheme, boolean legacyGDPR, String nativeAdStyle, int backgroundLight,
            int backgroundDark, boolean isBackup) {

        try {
            if (adStatus && placementStatus != 0) {
                String network;
                if (!isBackup) {
                    if (waterfallManager != null)
                        waterfallManager.reset();
                    network = adNetwork;
                } else {
                    network = waterfallManager != null ? waterfallManager.getNext() : backupAdNetwork;
                }

                if (network == null || network.isEmpty() || network.equals("none")) {
                    nativeAdViewContainer.setVisibility(View.GONE);
                    return;
                }

                Runnable fallbackAction = () -> {
                    if (waterfallManager != null && waterfallManager.hasNext()) {
                        loadBackupNativeAd(context, adStatus, placementStatus, backupAdNetwork,
                                adMobNativeId, metaNativeId, appLovinNativeId,
                                appLovinDiscMrecZoneId, wortiseNativeId, darkTheme, legacyGDPR,
                                nativeAdStyle, backgroundLight, backgroundDark);
                    } else {
                        nativeAdViewContainer.setVisibility(View.GONE);
                    }
                };

                switch (network) {
                    case ADMOB:
                    case META_BIDDING_ADMOB:
                        handleAdMobLoad(context, adMobNativeId, darkTheme, backgroundLight, backgroundDark, legacyGDPR,
                                fallbackAction);
                        break;
                    case META:
                        handleFacebookLoad(context, metaNativeId, nativeAdStyle, darkTheme, backgroundLight,
                                backgroundDark, fallbackAction);
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case META_BIDDING_APPLOVIN_MAX:
                        handleAppLovinLoad(context, appLovinNativeId, fallbackAction);
                        break;
                    case STARTAPP:
                        handleStartAppLoad(context, darkTheme, fallbackAction);
                        break;
                    case WORTISE:
                        handleWortiseLoad(context, wortiseNativeId, darkTheme, backgroundDark, backgroundLight,
                                fallbackAction);
                        break;
                    default:
                        fallbackAction.run();
                        break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in loadNativeAdMain: " + e.getMessage());
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void populateNativeAdView(Context context, com.google.android.gms.ads.nativead.NativeAd nativeAd,
            NativeAdView nativeAdView, boolean darkTheme, int nativeBackgroundDark, int nativeBackgroundLight) {

        if (darkTheme) {
            nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(context, nativeBackgroundDark));
            nativeAdView.findViewById(R.id.background).setBackgroundResource(nativeBackgroundDark);
        } else {
            nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(context, nativeBackgroundLight));
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
}
