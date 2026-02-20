package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_ADMOB;

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
    private com.google.android.gms.ads.nativead.NativeAd adMobNativeAdObj;
    private TemplateView admobNativeAd;
    private LinearLayout admobNativeBackground;

    // FAN
    private com.facebook.ads.NativeAd fanNativeAd;
    private NativeAdLayout fanNativeAdLayout;

    // StartApp
    private View startappNativeAd;
    private ImageView startappNativeImage;
    private ImageView startappNativeIcon;
    private TextView startappNativeTitle;
    private TextView startappNativeDescription;
    private Button startappNativeButton;
    private LinearLayout startappNativeBackground;

    // AppLovin
    private FrameLayout applovinNativeAd;
    private LinearLayout appLovinDiscoveryMrecAd;

    // Wortise
    private FrameLayout wortiseNativeAd;

    // Ad Loaders
    private MaxNativeAdLoader nativeAdLoader;
    private MaxAd maxNativeAd;
    // removed
    // removed

    public NativeAdViewHolder(View view) {
        super(view);

        nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);

        // AdMob
        admobNativeAd = view.findViewById(R.id.admob_native_ad_container);
        mediaView = view.findViewById(R.id.media_view);
        admobNativeBackground = view.findViewById(R.id.background);

        // Ad Manager

        // FAN
        fanNativeAdLayout = view.findViewById(R.id.fan_native_ad_container);

        // StartApp
        startappNativeAd = view.findViewById(R.id.startapp_native_ad_container);
        startappNativeImage = view.findViewById(R.id.startapp_native_image);
        startappNativeIcon = view.findViewById(R.id.startapp_native_icon);
        startappNativeTitle = view.findViewById(R.id.startapp_native_title);
        startappNativeDescription = view.findViewById(R.id.startapp_native_description);
        startappNativeButton = view.findViewById(R.id.startapp_native_button);
        startappNativeButton.setOnClickListener(v1 -> itemView.performClick());
        startappNativeBackground = view.findViewById(R.id.startapp_native_background);

        // AppLovin
        applovinNativeAd = view.findViewById(R.id.applovin_native_ad_container);
        appLovinDiscoveryMrecAd = view.findViewById(R.id.applovin_discovery_mrec_ad_container);

        wortiseNativeAd = view.findViewById(R.id.wortise_native_ad_container);

    }

    public void loadNativeAd(Context context, String adStatus, int placementStatus, String adNetwork,
            String backupAdNetwork, String adMobNativeId, String fanNativeId,
            String appLovinNativeId, String appLovinDiscMrecZoneId, String wortiseNativeId,
            boolean darkTheme, boolean legacyGDPR, String nativeAdStyle, int nativeBackgroundLight,
            int nativeBackgroundDark) {
        loadNativeAdMain(context, adStatus, placementStatus, adNetwork, backupAdNetwork, adMobNativeId, fanNativeId,
                appLovinNativeId, appLovinDiscMrecZoneId, wortiseNativeId, darkTheme, legacyGDPR, nativeAdStyle,
                nativeBackgroundLight, nativeBackgroundDark, false);
    }

    public void loadBackupNativeAd(Context context, String adStatus, int placementStatus, String backupAdNetwork,
            String adMobNativeId, String fanNativeId, String appLovinNativeId,
            String appLovinDiscMrecZoneId, String wortiseNativeId, boolean darkTheme,
            boolean legacyGDPR, String nativeAdStyle, int nativeBackgroundLight, int nativeBackgroundDark) {
        loadNativeAdMain(context, adStatus, placementStatus, "", backupAdNetwork, adMobNativeId, fanNativeId,
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
        admobNativeAd.setStyles(styles);
        admobNativeBackground.setBackgroundResource(colorRes);
    }

    private void handleAdMobLoad(Context context, String adMobNativeId, boolean darkTheme, int backgroundLight,
            int backgroundDark, boolean legacyGDPR, Runnable fallback) {
        if (admobNativeAd.getVisibility() != View.VISIBLE) {
            AdLoader adLoader = new AdLoader.Builder(context, adMobNativeId)
                    .forNativeAd(nativeAd -> {
                        if (adMobNativeAdObj != null) {
                            adMobNativeAdObj.destroy();
                        }
                        adMobNativeAdObj = nativeAd;
                        setAdMobStyle(context, darkTheme, backgroundLight, backgroundDark);
                        mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                        admobNativeAd.setNativeAd(nativeAd);
                        admobNativeAd.setVisibility(View.VISIBLE);
                        nativeAdViewContainer.setVisibility(View.VISIBLE);
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                            if (fallback != null) {
                                fallback.run();
                            } else {
                                admobNativeAd.setVisibility(View.GONE);
                                nativeAdViewContainer.setVisibility(View.GONE);
                            }
                        }
                    })
                    .build();
            adLoader.loadAd(Tools.getAdRequest((Activity) context, legacyGDPR));
        } else {
            Log.d(TAG, "AdMob native ads has been loaded");
        }
    }

    private void handleFacebookLoad(Context context, String fanNativeId, String nativeAdStyle, boolean darkTheme,
            int backgroundLight, int backgroundDark, Runnable fallback) {
        if (fanNativeAdLayout.getVisibility() != View.VISIBLE) {
            fanNativeAd = new com.facebook.ads.NativeAd(context, fanNativeId);
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
                    if (fanNativeAd != ad) {
                        return;
                    }
                    fanNativeAdLayout.setVisibility(View.VISIBLE);
                    nativeAdViewContainer.setVisibility(View.VISIBLE);
                    fanNativeAd.unregisterView();

                    LayoutInflater inflater = LayoutInflater.from(context);
                    LinearLayout nativeAdView;
                    int layoutRes;
                    switch (nativeAdStyle) {
                        case Constant.STYLE_NEWS:
                        case Constant.STYLE_MEDIUM:
                            layoutRes = R.layout.gnt_fan_news_template_view;
                            break;
                        case Constant.STYLE_VIDEO_SMALL:
                            layoutRes = R.layout.gnt_fan_video_small_template_view;
                            break;
                        case Constant.STYLE_VIDEO_LARGE:
                            layoutRes = R.layout.gnt_fan_video_large_template_view;
                            break;
                        case Constant.STYLE_RADIO:
                        case Constant.STYLE_SMALL:
                            layoutRes = R.layout.gnt_fan_radio_template_view;
                            break;
                        default:
                            layoutRes = R.layout.gnt_fan_medium_template_view;
                            break;
                    }
                    nativeAdView = (LinearLayout) inflater.inflate(layoutRes, fanNativeAdLayout, false);
                    fanNativeAdLayout.addView(nativeAdView);

                    LinearLayout adChoicesContainer = nativeAdView.findViewById(R.id.ad_choices_container);
                    AdOptionsView adOptionsView = new AdOptionsView(context, fanNativeAd, fanNativeAdLayout);
                    adChoicesContainer.removeAllViews();
                    adChoicesContainer.addView(adOptionsView, 0);

                    TextView nativeAdTitle = nativeAdView.findViewById(R.id.native_ad_title);
                    com.facebook.ads.MediaView nativeAdMedia = nativeAdView.findViewById(R.id.native_ad_media);
                    com.facebook.ads.MediaView nativeAdIcon = nativeAdView.findViewById(R.id.native_ad_icon);
                    TextView nativeAdSocialContext = nativeAdView.findViewById(R.id.native_ad_social_context);
                    TextView nativeAdBody = nativeAdView.findViewById(R.id.native_ad_body);
                    TextView sponsoredLabel = nativeAdView.findViewById(R.id.native_ad_sponsored_label);
                    Button nativeAdCallToAction = nativeAdView.findViewById(R.id.native_ad_call_to_action);
                    LinearLayout fanNativeBackground = nativeAdView.findViewById(R.id.ad_unit);

                    if (darkTheme) {
                        int textColor = ContextCompat.getColor(context, R.color.applovin_dark_primary_text_color);
                        int secondaryColor = ContextCompat.getColor(context,
                                R.color.applovin_dark_secondary_text_color);
                        nativeAdTitle.setTextColor(textColor);
                        nativeAdSocialContext.setTextColor(textColor);
                        sponsoredLabel.setTextColor(secondaryColor);
                        nativeAdBody.setTextColor(secondaryColor);
                        fanNativeBackground.setBackgroundResource(backgroundDark);
                    } else {
                        fanNativeBackground.setBackgroundResource(backgroundLight);
                    }

                    nativeAdTitle.setText(fanNativeAd.getAdvertiserName());
                    nativeAdBody.setText(fanNativeAd.getAdBodyText());
                    nativeAdSocialContext.setText(fanNativeAd.getAdSocialContext());
                    nativeAdCallToAction.setVisibility(fanNativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
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
                    fanNativeAd.registerViewForInteraction(nativeAdView, nativeAdIcon, nativeAdMedia, clickableViews);
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
        } else {
            Log.d(TAG, "FAN Native Ad has been loaded");
        }
    }

    private void handleAppLovinLoad(Context context, String appLovinNativeId, Runnable fallback) {
        if (applovinNativeAd.getVisibility() != View.VISIBLE) {
            nativeAdLoader = new MaxNativeAdLoader(appLovinNativeId, context);
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
        if (fallback != null)
            fallback.run();
    }

    public void destroyAd() {
        if (adMobNativeAdObj != null) {
            adMobNativeAdObj.destroy();
            adMobNativeAdObj = null;
        }
        if (fanNativeAd != null) {
            fanNativeAd.destroy();
            fanNativeAd = null;
        }
        if (nativeAdLoader != null && maxNativeAd != null) {
            nativeAdLoader.destroy(maxNativeAd);
            maxNativeAd = null;
        }

    }

    private void handleWortiseLoad(Context context, String wortiseNativeId, boolean darkTheme, int backgroundDark,
            int backgroundLight, Runnable fallback) {
        if (fallback != null)
            fallback.run();
    }

    private void loadNativeAdMain(Context context, String adStatus, int placementStatus, String adNetwork,
            String backupAdNetwork, String adMobNativeId, String fanNativeId,
            String appLovinNativeId, String appLovinDiscMrecZoneId, String wortiseNativeId,
            boolean darkTheme, boolean legacyGDPR, String nativeAdStyle, int backgroundLight,
            int backgroundDark, boolean isBackup) {

        try {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                String network = isBackup ? backupAdNetwork : adNetwork;
                Runnable fallbackAction = isBackup ? () -> {
                    // Final fallback: hide everything
                    nativeAdViewContainer.setVisibility(View.GONE);
                } : () -> {
                    loadBackupNativeAd(context, adStatus, placementStatus, backupAdNetwork,
                            adMobNativeId, fanNativeId, appLovinNativeId,
                            appLovinDiscMrecZoneId, wortiseNativeId, darkTheme, legacyGDPR,
                            nativeAdStyle, backgroundLight, backgroundDark);
                };

                switch (network) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        handleAdMobLoad(context, adMobNativeId, darkTheme, backgroundLight, backgroundDark, legacyGDPR,
                                fallbackAction);
                        break;
                    case META:
                        handleFacebookLoad(context, fanNativeId, nativeAdStyle, darkTheme, backgroundLight,
                                backgroundDark, fallbackAction);
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
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

