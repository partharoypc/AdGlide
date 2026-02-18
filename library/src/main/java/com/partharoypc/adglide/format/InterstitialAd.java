package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_DISCOVERY;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.FACEBOOK;
import static com.partharoypc.adglide.util.Constant.FAN;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_AD_MANAGER;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.GOOGLE_AD_MANAGER;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.NONE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.UNITY;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinSdkUtils;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.partharoypc.adglide.util.OnInterstitialAdDismissedListener;
import com.partharoypc.adglide.util.OnInterstitialAdShowedListener;
import com.partharoypc.adglide.util.Tools;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.wortise.ads.interstitial.InterstitialAd;

public class InterstitialAd {

    public static class Builder {

        private static final String TAG = "AdNetwork";
        private final Activity activity;
        private com.google.android.gms.ads.interstitial.InterstitialAd adMobInterstitialAd;
        private AdManagerInterstitialAd adManagerInterstitialAd;
        private com.facebook.ads.InterstitialAd fanInterstitialAd;
        private MaxInterstitialAd appLovinMaxInterstitialAd;
        private com.applovin.adview.AppLovinInterstitialAd appLovinDiscoveryInterstitialAd;
        private StartAppAd startAppInterstitialAd;
        private com.wortise.ads.interstitial.InterstitialAd wortiseInterstitialAd;
        private int retryAttempt;
        private int counter = 1;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private String adMobInterstitialId = "";
        private String googleAdManagerInterstitialId = "";
        private String fanInterstitialId = "";
        private String unityInterstitialId = "";
        private String appLovinInterstitialId = "";
        private String appLovinInterstitialZoneId = "";
        private String ironSourceInterstitialId = "";
        private String wortiseInterstitialId = "";
        private int placementStatus = 1;
        private int interval = 3;
        private boolean legacyGDPR = false;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder build() {
            loadInterstitialAd();
            return this;
        }

        public Builder build(OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            loadInterstitialAd(onInterstitialAdDismissedListener);
            return this;
        }

        public void show() {
            showInterstitialAd();
        }

        public void show(OnInterstitialAdShowedListener onInterstitialAdShowedListener,
                OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            showInterstitialAd(onInterstitialAdShowedListener, onInterstitialAdDismissedListener);
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

        public Builder setAdMobInterstitialId(String adMobInterstitialId) {
            this.adMobInterstitialId = adMobInterstitialId;
            return this;
        }

        public Builder setGoogleAdManagerInterstitialId(String googleAdManagerInterstitialId) {
            this.googleAdManagerInterstitialId = googleAdManagerInterstitialId;
            return this;
        }

        public Builder setFanInterstitialId(String fanInterstitialId) {
            this.fanInterstitialId = fanInterstitialId;
            return this;
        }

        public Builder setUnityInterstitialId(String unityInterstitialId) {
            this.unityInterstitialId = unityInterstitialId;
            return this;
        }

        public Builder setAppLovinInterstitialId(String appLovinInterstitialId) {
            this.appLovinInterstitialId = appLovinInterstitialId;
            return this;
        }

        public Builder setAppLovinInterstitialZoneId(String appLovinInterstitialZoneId) {
            this.appLovinInterstitialZoneId = appLovinInterstitialZoneId;
            return this;
        }

        public Builder setIronSourceInterstitialId(String ironSourceInterstitialId) {
            this.ironSourceInterstitialId = ironSourceInterstitialId;
            return this;
        }

        public Builder setWortiseInterstitialId(String wortiseInterstitialId) {
            this.wortiseInterstitialId = wortiseInterstitialId;
            return this;
        }

        public Builder setPlacementStatus(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        public Builder setInterval(int interval) {
            this.interval = interval;
            return this;
        }

        public Builder setLegacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public void loadInterstitialAd() {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (adNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        com.google.android.gms.ads.interstitial.InterstitialAd.load(activity, adMobInterstitialId,
                                Tools.getAdRequest(activity, legacyGDPR), new InterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(
                                            @NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                                        adMobInterstitialAd = interstitialAd;
                                        adMobInterstitialAd
                                                .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        loadInterstitialAd();
                                                    }

                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(
                                                            @NonNull com.google.android.gms.ads.AdError adError) {
                                                        Log.d(TAG, "The ad failed to show.");
                                                    }

                                                    @Override
                                                    public void onAdShowedFullScreenContent() {
                                                        adMobInterstitialAd = null;
                                                        Log.d(TAG, "The ad was shown.");
                                                    }
                                                });
                                        Log.i(TAG, "onAdLoaded");
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        Log.i(TAG, loadAdError.getMessage());
                                        adMobInterstitialAd = null;
                                        loadBackupInterstitialAd();
                                        Log.d(TAG, "Failed load AdMob Interstitial Ad");
                                    }
                                });
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        AdManagerInterstitialAd.load(activity, googleAdManagerInterstitialId,
                                Tools.getGoogleAdManagerRequest(), new AdManagerInterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                                        super.onAdLoaded(interstitialAd);
                                        adManagerInterstitialAd = interstitialAd;
                                        adManagerInterstitialAd
                                                .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdClicked() {
                                                        super.onAdClicked();
                                                    }

                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        super.onAdDismissedFullScreenContent();
                                                        loadInterstitialAd();
                                                    }

                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(
                                                            @NonNull AdError adError) {
                                                        super.onAdFailedToShowFullScreenContent(adError);
                                                    }

                                                    @Override
                                                    public void onAdImpression() {
                                                        super.onAdImpression();
                                                    }

                                                    @Override
                                                    public void onAdShowedFullScreenContent() {
                                                        super.onAdShowedFullScreenContent();
                                                        adManagerInterstitialAd = null;
                                                        Log.d(TAG, "The ad was shown.");
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        super.onAdFailedToLoad(loadAdError);
                                        adManagerInterstitialAd = null;
                                        loadBackupInterstitialAd();
                                        Log.d(TAG, "Failed load Ad Manager Interstitial Ad");
                                    }
                                });
                        break;

                    case FAN:
                    case FACEBOOK:
                        fanInterstitialAd = new com.facebook.ads.InterstitialAd(activity, fanInterstitialId);
                        com.facebook.ads.InterstitialAdListener adListener = new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
                                fanInterstitialAd.loadAd();
                            }

                            @Override
                            public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
                                loadBackupInterstitialAd();
                            }

                            @Override
                            public void onAdLoaded(com.facebook.ads.Ad ad) {
                                Log.d(TAG, "FAN Interstitial is loaded");
                            }

                            @Override
                            public void onAdClicked(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(com.facebook.ads.Ad ad) {

                            }
                        };

                        com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = fanInterstitialAd
                                .buildLoadAdConfig().withAdListener(adListener).build();
                        fanInterstitialAd.loadAd(loadAdConfig);
                        break;

                    case UNITY:
                        UnityAds.load(unityInterstitialId, new IUnityAdsLoadListener() {
                            @Override
                            public void onUnityAdsAdLoaded(String placementId) {
                                Log.d(TAG, "Unity Interstitial Ad loaded");
                            }

                            @Override
                            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error,
                                    String message) {
                                loadBackupInterstitialAd();
                                Log.d(TAG, "Unity Interstitial Ad failed to load: " + error + " - " + message);
                            }
                        });
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        appLovinMaxInterstitialAd = new MaxInterstitialAd(appLovinInterstitialId, activity);
                        appLovinMaxInterstitialAd.setListener(new MaxAdListener() {
                            @Override
                            public void onAdLoaded(MaxAd ad) {
                                Log.d(TAG, "AppLovin Interstitial Ad loaded");
                            }

                            @Override
                            public void onAdDisplayed(MaxAd ad) {
                            }

                            @Override
                            public void onAdHidden(MaxAd ad) {
                                loadInterstitialAd();
                            }

                            @Override
                            public void onAdClicked(MaxAd ad) {
                            }

                            @Override
                            public void onAdLoadFailed(String adUnitId, MaxError error) {
                                loadBackupInterstitialAd();
                                Log.d(TAG, "AppLovin Interstitial Ad failed to load: " + error.getMessage());
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                            }
                        });
                        appLovinMaxInterstitialAd.loadAd();
                        break;

                    case APPLOVIN_DISCOVERY:
                        appLovinDiscoveryInterstitialAd = com.applovin.adview.AppLovinInterstitialAd
                                .create(AppLovinSdkUtils.getZone(activity.getApplicationContext(),
                                        appLovinInterstitialZoneId, activity), activity);
                        appLovinDiscoveryInterstitialAd.setAdLoadListener(new AppLovinAdLoadListener() {
                            @Override
                            public void adReceived(AppLovinAd ad) {
                                Log.d(TAG, "AppLovin Discovery Interstitial Ad loaded");
                            }

                            @Override
                            public void failedToReceiveAd(int errorCode) {
                                loadBackupInterstitialAd();
                                Log.d(TAG, "AppLovin Discovery Interstitial Ad failed to load");
                            }
                        });
                        appLovinDiscoveryInterstitialAd.loadNextAd();
                        break;

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        IronSource.setLevelPlayInterstitialListener(new InterstitialListener() {
                            @Override
                            public void onInterstitialAdReady() {
                                Log.d(TAG, "IronSource Interstitial Ad loaded");
                            }

                            @Override
                            public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
                                loadBackupInterstitialAd();
                                Log.d(TAG, "IronSource Interstitial Ad failed to load: "
                                        + ironSourceError.getErrorMessage());
                            }

                            @Override
                            public void onInterstitialAdOpened() {
                            }

                            @Override
                            public void onInterstitialAdClosed() {
                                loadInterstitialAd();
                            }

                            @Override
                            public void onInterstitialAdShowSucceeded() {
                            }

                            @Override
                            public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
                            }

                            @Override
                            public void onInterstitialAdClicked() {
                            }
                        });
                        IronSource.loadInterstitial();
                        break;

                    case STARTAPP:
                        startAppInterstitialAd = new StartAppAd(activity);
                        startAppInterstitialAd.loadAd(new AdEventListener() {
                            @Override
                            public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                Log.d(TAG, "StartApp Interstitial Ad loaded");
                            }

                            @Override
                            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                loadBackupInterstitialAd();
                                Log.d(TAG, "StartApp Interstitial Ad failed to load");
                            }
                        });
                        break;

                    case WORTISE:
                        wortiseInterstitialAd = new com.wortise.ads.interstitial.InterstitialAd(activity,
                                wortiseInterstitialId);
                        wortiseInterstitialAd.setListener(new com.wortise.ads.interstitial.InterstitialAd.Listener() {
                            @Override
                            public void onInterstitialClicked(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                            }

                            @Override
                            public void onInterstitialDismissed(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                loadInterstitialAd();
                            }

                            @Override
                            public void onInterstitialFailedToLoad(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                    @NonNull com.wortise.ads.AdError adError) {
                                loadBackupInterstitialAd();
                                Log.d(TAG, "Wortise Interstitial Ad failed to load");
                            }

                            @Override
                            public void onInterstitialFailedToShow(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                    @NonNull com.wortise.ads.AdError adError) {
                            }

                            @Override
                            public void onInterstitialImpression(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                            }

                            @Override
                            public void onInterstitialLoaded(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                Log.d(TAG, "Wortise Interstitial Ad loaded");
                            }

                            @Override
                            public void onInterstitialShown(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                            }
                        });
                        wortiseInterstitialAd.loadAd();
                        break;

                    default:
                        break;
                }
            }
        }

        public void loadBackupInterstitialAd() {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (backupAdNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        com.google.android.gms.ads.interstitial.InterstitialAd.load(activity, adMobInterstitialId,
                                Tools.getAdRequest(activity, legacyGDPR), new InterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(
                                            @NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                                        adMobInterstitialAd = interstitialAd;
                                        adMobInterstitialAd
                                                .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        loadInterstitialAd();
                                                    }

                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(
                                                            @NonNull com.google.android.gms.ads.AdError adError) {
                                                        Log.d(TAG, "The ad failed to show.");
                                                    }

                                                    @Override
                                                    public void onAdShowedFullScreenContent() {
                                                        adMobInterstitialAd = null;
                                                        Log.d(TAG, "The ad was shown.");
                                                    }
                                                });
                                        Log.i(TAG, "onAdLoaded");
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        Log.i(TAG, loadAdError.getMessage());
                                        adMobInterstitialAd = null;
                                        Log.d(TAG, "Failed load AdMob Interstitial Ad");
                                    }
                                });
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        AdManagerInterstitialAd.load(activity, googleAdManagerInterstitialId,
                                Tools.getGoogleAdManagerRequest(), new AdManagerInterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                                        super.onAdLoaded(interstitialAd);
                                        adManagerInterstitialAd = interstitialAd;
                                        adManagerInterstitialAd
                                                .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdClicked() {
                                                        super.onAdClicked();
                                                    }

                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        super.onAdDismissedFullScreenContent();
                                                        loadInterstitialAd();
                                                    }

                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(
                                                            @NonNull AdError adError) {
                                                        super.onAdFailedToShowFullScreenContent(adError);
                                                    }

                                                    @Override
                                                    public void onAdImpression() {
                                                        super.onAdImpression();
                                                    }

                                                    @Override
                                                    public void onAdShowedFullScreenContent() {
                                                        super.onAdShowedFullScreenContent();
                                                        adManagerInterstitialAd = null;
                                                        Log.d(TAG, "The ad was shown.");
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        super.onAdFailedToLoad(loadAdError);
                                        adManagerInterstitialAd = null;
                                        Log.d(TAG, "Failed load Ad Manager Interstitial Ad");
                                    }
                                });
                        break;

                    case FAN:
                    case FACEBOOK:
                        fanInterstitialAd = new com.facebook.ads.InterstitialAd(activity, fanInterstitialId);
                        com.facebook.ads.InterstitialAdListener adListener = new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
                                fanInterstitialAd.loadAd();
                            }

                            @Override
                            public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {

                            }

                            @Override
                            public void onAdLoaded(com.facebook.ads.Ad ad) {
                                Log.d(TAG, "FAN Interstitial is loaded");
                            }

                            @Override
                            public void onAdClicked(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(com.facebook.ads.Ad ad) {

                            }
                        };

                        com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = fanInterstitialAd
                                .buildLoadAdConfig().withAdListener(adListener).build();
                        fanInterstitialAd.loadAd(loadAdConfig);
                        break;

                    case UNITY:
                        UnityAds.load(unityInterstitialId, new IUnityAdsLoadListener() {
                            @Override
                            public void onUnityAdsAdLoaded(String placementId) {
                                Log.d(TAG, "Unity Interstitial Ad loaded");
                            }

                            @Override
                            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error,
                                    String message) {
                                Log.d(TAG, "Unity Interstitial Ad failed to load: " + error + " - " + message);
                            }
                        });
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        appLovinMaxInterstitialAd = new MaxInterstitialAd(appLovinInterstitialId, activity);
                        appLovinMaxInterstitialAd.setListener(new MaxAdListener() {
                            @Override
                            public void onAdLoaded(MaxAd ad) {
                                Log.d(TAG, "AppLovin Interstitial Ad loaded");
                            }

                            @Override
                            public void onAdDisplayed(MaxAd ad) {
                            }

                            @Override
                            public void onAdHidden(MaxAd ad) {
                                loadInterstitialAd();
                            }

                            @Override
                            public void onAdClicked(MaxAd ad) {
                            }

                            @Override
                            public void onAdLoadFailed(String adUnitId, MaxError error) {
                                Log.d(TAG, "AppLovin Interstitial Ad failed to load: " + error.getMessage());
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                            }
                        });
                        appLovinMaxInterstitialAd.loadAd();
                        break;

                    case APPLOVIN_DISCOVERY:
                        appLovinDiscoveryInterstitialAd = com.applovin.adview.AppLovinInterstitialAd
                                .create(AppLovinSdkUtils.getZone(activity.getApplicationContext(),
                                        appLovinInterstitialZoneId, activity), activity);
                        appLovinDiscoveryInterstitialAd.setAdLoadListener(new AppLovinAdLoadListener() {
                            @Override
                            public void adReceived(AppLovinAd ad) {
                                Log.d(TAG, "AppLovin Discovery Interstitial Ad loaded");
                            }

                            @Override
                            public void failedToReceiveAd(int errorCode) {
                                Log.d(TAG, "AppLovin Discovery Interstitial Ad failed to load");
                            }
                        });
                        appLovinDiscoveryInterstitialAd.loadNextAd();
                        break;

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        IronSource.setLevelPlayInterstitialListener(new InterstitialListener() {
                            @Override
                            public void onInterstitialAdReady() {
                                Log.d(TAG, "IronSource Interstitial Ad loaded");
                            }

                            @Override
                            public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
                                Log.d(TAG, "IronSource Interstitial Ad failed to load: "
                                        + ironSourceError.getErrorMessage());
                            }

                            @Override
                            public void onInterstitialAdOpened() {
                            }

                            @Override
                            public void onInterstitialAdClosed() {
                                loadInterstitialAd();
                            }

                            @Override
                            public void onInterstitialAdShowSucceeded() {
                            }

                            @Override
                            public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
                            }

                            @Override
                            public void onInterstitialAdClicked() {
                            }
                        });
                        IronSource.loadInterstitial();
                        break;

                    case STARTAPP:
                        startAppInterstitialAd = new StartAppAd(activity);
                        startAppInterstitialAd.loadAd(new AdEventListener() {
                            @Override
                            public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                Log.d(TAG, "StartApp Interstitial Ad loaded");
                            }

                            @Override
                            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                Log.d(TAG, "StartApp Interstitial Ad failed to load");
                            }
                        });
                        break;

                    case WORTISE:
                        wortiseInterstitialAd = new com.wortise.ads.interstitial.InterstitialAd(activity,
                                wortiseInterstitialId);
                        wortiseInterstitialAd.setListener(new com.wortise.ads.interstitial.InterstitialAd.Listener() {
                            @Override
                            public void onInterstitialClicked(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                            }

                            @Override
                            public void onInterstitialDismissed(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                loadInterstitialAd();
                            }

                            @Override
                            public void onInterstitialFailedToLoad(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                    @NonNull com.wortise.ads.AdError adError) {
                                Log.d(TAG, "Wortise Interstitial Ad failed to load");
                            }

                            @Override
                            public void onInterstitialFailedToShow(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                    @NonNull com.wortise.ads.AdError adError) {
                            }

                            @Override
                            public void onInterstitialImpression(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                            }

                            @Override
                            public void onInterstitialLoaded(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                Log.d(TAG, "Wortise Interstitial Ad loaded");
                            }

                            @Override
                            public void onInterstitialShown(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                            }
                        });
                        wortiseInterstitialAd.loadAd();
                        break;

                    default:
                        break;
                }
            }
        }

        public void showInterstitialAd() {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                if (counter == interval) {
                    switch (adNetwork) {
                        case ADMOB:
                        case FAN_BIDDING_ADMOB:
                            if (adMobInterstitialAd != null) {
                                adMobInterstitialAd.show(activity);
                                Log.d(TAG, "admob interstitial not null");
                            } else {
                                showBackupInterstitialAd();
                                Log.d(TAG, "admob interstitial null");
                            }
                            break;

                        case GOOGLE_AD_MANAGER:
                        case FAN_BIDDING_AD_MANAGER:
                            if (adManagerInterstitialAd != null) {
                                adManagerInterstitialAd.show(activity);
                                Log.d(TAG, "ad manager interstitial not null");
                            } else {
                                showBackupInterstitialAd();
                                Log.d(TAG, "ad manager interstitial null");
                            }
                            break;

                        case FAN:
                        case FACEBOOK:
                            if (fanInterstitialAd != null && fanInterstitialAd.isAdLoaded()) {
                                fanInterstitialAd.show();
                                Log.d(TAG, "fan interstitial not null");
                            } else {
                                showBackupInterstitialAd();
                                Log.d(TAG, "fan interstitial null");
                            }
                            break;

                        case UNITY:
                            if (UnityAds.isInitialized()) {
                                UnityAds.show(activity, unityInterstitialId, new IUnityAdsShowListener() {
                                    @Override
                                    public void onUnityAdsShowFailure(String placementId,
                                            UnityAds.UnityAdsShowError error, String message) {
                                        showBackupInterstitialAd();
                                        Log.d(TAG, "Unity Interstitial Failed to show: " + message);
                                    }

                                    @Override
                                    public void onUnityAdsShowStart(String placementId) {
                                    }

                                    @Override
                                    public void onUnityAdsShowClick(String placementId) {
                                    }

                                    @Override
                                    public void onUnityAdsShowComplete(String placementId,
                                            UnityAds.UnityAdsShowCompletionState state) {
                                    }
                                });
                            } else {
                                showBackupInterstitialAd();
                            }
                            break;

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case FAN_BIDDING_APPLOVIN_MAX:
                            if (appLovinMaxInterstitialAd != null && appLovinMaxInterstitialAd.isReady()) {
                                appLovinMaxInterstitialAd.showAd();
                            } else {
                                showBackupInterstitialAd();
                            }
                            break;

                        case APPLOVIN_DISCOVERY:
                            if (appLovinDiscoveryInterstitialAd != null
                                    && appLovinDiscoveryInterstitialAd.isAdReadyToDisplay()) {
                                appLovinDiscoveryInterstitialAd.show();
                            } else {
                                showBackupInterstitialAd();
                            }
                            break;

                        case IRONSOURCE:
                        case FAN_BIDDING_IRONSOURCE:
                            if (IronSource.isInterstitialReady()) {
                                IronSource.showInterstitial();
                            } else {
                                showBackupInterstitialAd();
                            }
                            break;

                        case STARTAPP:
                            if (startAppInterstitialAd != null && startAppInterstitialAd.isReady()) {
                                startAppInterstitialAd.showAd();
                            } else {
                                showBackupInterstitialAd();
                            }
                            break;

                        case WORTISE:
                            if (wortiseInterstitialAd != null && wortiseInterstitialAd.isAvailable()) {
                                wortiseInterstitialAd.showAd();
                            } else {
                                showBackupInterstitialAd();
                            }
                            break;

                        default:
                            break;
                    }
                    counter = 1;
                } else {
                    counter++;
                }
                Log.d(TAG, "Current counter : " + counter);
            }
        }

        public void showBackupInterstitialAd() {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                Log.d(TAG, "Show Backup Interstitial Ad [" + backupAdNetwork.toUpperCase() + "]");
                switch (backupAdNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        if (adMobInterstitialAd != null) {
                            adMobInterstitialAd.show(activity);
                        }
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        if (adManagerInterstitialAd != null) {
                            adManagerInterstitialAd.show(activity);
                        }
                        break;

                    case FAN:
                    case FACEBOOK:
                        if (fanInterstitialAd != null && fanInterstitialAd.isAdLoaded()) {
                            fanInterstitialAd.show();
                        }
                        break;

                    case UNITY:
                        if (UnityAds.isInitialized()) {
                            UnityAds.show(activity, unityInterstitialId);
                        }
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        if (appLovinMaxInterstitialAd != null && appLovinMaxInterstitialAd.isReady()) {
                            appLovinMaxInterstitialAd.showAd();
                        }
                        break;

                    case APPLOVIN_DISCOVERY:
                        if (appLovinDiscoveryInterstitialAd != null
                                && appLovinDiscoveryInterstitialAd.isAdReadyToDisplay()) {
                            appLovinDiscoveryInterstitialAd.show();
                        }
                        break;

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        if (IronSource.isInterstitialReady()) {
                            IronSource.showInterstitial();
                        }
                        break;

                    case STARTAPP:
                        if (startAppInterstitialAd != null && startAppInterstitialAd.isReady()) {
                            startAppInterstitialAd.showAd();
                        }
                        break;

                    case WORTISE:
                        if (wortiseInterstitialAd != null && wortiseInterstitialAd.isAvailable()) {
                            wortiseInterstitialAd.showAd();
                        }
                        break;

                    default:
                        break;
                }
            }
        }

        public void loadInterstitialAd(OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (adNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        com.google.android.gms.ads.interstitial.InterstitialAd.load(activity, adMobInterstitialId,
                                Tools.getAdRequest(activity, legacyGDPR), new InterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(
                                            @NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                                        adMobInterstitialAd = interstitialAd;
                                        adMobInterstitialAd
                                                .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        loadInterstitialAd(onInterstitialAdDismissedListener);
                                                        onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                                                    }

                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(
                                                            @NonNull com.google.android.gms.ads.AdError adError) {
                                                        Log.d(TAG, "The ad failed to show.");
                                                    }

                                                    @Override
                                                    public void onAdShowedFullScreenContent() {
                                                        adMobInterstitialAd = null;
                                                        Log.d(TAG, "The ad was shown.");
                                                    }
                                                });
                                        Log.i(TAG, "onAdLoaded");
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        Log.i(TAG, loadAdError.getMessage());
                                        adMobInterstitialAd = null;
                                        loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                        Log.d(TAG, "Failed load AdMob Interstitial Ad");
                                    }
                                });
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        AdManagerInterstitialAd.load(activity, googleAdManagerInterstitialId,
                                Tools.getGoogleAdManagerRequest(), new AdManagerInterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                                        super.onAdLoaded(interstitialAd);
                                        adManagerInterstitialAd = interstitialAd;
                                        adManagerInterstitialAd
                                                .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdClicked() {
                                                        super.onAdClicked();
                                                    }

                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        super.onAdDismissedFullScreenContent();
                                                        loadInterstitialAd(onInterstitialAdDismissedListener);
                                                        onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                                                    }

                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(
                                                            @NonNull AdError adError) {
                                                        super.onAdFailedToShowFullScreenContent(adError);
                                                    }

                                                    @Override
                                                    public void onAdImpression() {
                                                        super.onAdImpression();
                                                    }

                                                    @Override
                                                    public void onAdShowedFullScreenContent() {
                                                        super.onAdShowedFullScreenContent();
                                                        adManagerInterstitialAd = null;
                                                        Log.d(TAG, "The ad was shown.");
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        super.onAdFailedToLoad(loadAdError);
                                        adManagerInterstitialAd = null;
                                        loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                        Log.d(TAG, "Failed load Ad Manager Interstitial Ad");
                                    }
                                });
                        break;

                    case FAN:
                    case FACEBOOK:
                        fanInterstitialAd = new com.facebook.ads.InterstitialAd(activity, fanInterstitialId);
                        com.facebook.ads.InterstitialAdListener adListener = new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
                                fanInterstitialAd.loadAd();
                                onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                            }

                            @Override
                            public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
                                loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                            }

                            @Override
                            public void onAdLoaded(com.facebook.ads.Ad ad) {
                                Log.d(TAG, "FAN Interstitial is loaded");
                            }

                            @Override
                            public void onAdClicked(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(com.facebook.ads.Ad ad) {

                            }
                        };

                        com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = fanInterstitialAd
                                .buildLoadAdConfig().withAdListener(adListener).build();
                        fanInterstitialAd.loadAd(loadAdConfig);
                        break;

                    case UNITY:
                        UnityAds.load(unityInterstitialId, new IUnityAdsLoadListener() {
                            @Override
                            public void onUnityAdsAdLoaded(String placementId) {
                                Log.d(TAG, "Unity Interstitial Ad loaded");
                            }

                            @Override
                            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error,
                                    String message) {
                                loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                Log.d(TAG, "Unity Interstitial Ad failed to load: " + error + " - " + message);
                            }
                        });
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        appLovinMaxInterstitialAd = new MaxInterstitialAd(appLovinInterstitialId, activity);
                        appLovinMaxInterstitialAd.setListener(new MaxAdListener() {
                            @Override
                            public void onAdLoaded(MaxAd ad) {
                                Log.d(TAG, "AppLovin Interstitial Ad loaded");
                            }

                            @Override
                            public void onAdDisplayed(MaxAd ad) {
                            }

                            @Override
                            public void onAdHidden(MaxAd ad) {
                                loadInterstitialAd(onInterstitialAdDismissedListener);
                                onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                            }

                            @Override
                            public void onAdClicked(MaxAd ad) {
                            }

                            @Override
                            public void onAdLoadFailed(String adUnitId, MaxError error) {
                                loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                Log.d(TAG, "AppLovin Interstitial Ad failed to load: " + error.getMessage());
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                            }
                        });
                        appLovinMaxInterstitialAd.loadAd();
                        break;

                    case APPLOVIN_DISCOVERY:
                        appLovinDiscoveryInterstitialAd = com.applovin.adview.AppLovinInterstitialAd
                                .create(AppLovinSdkUtils.getZone(activity.getApplicationContext(),
                                        appLovinInterstitialZoneId, activity), activity);
                        appLovinDiscoveryInterstitialAd.setAdLoadListener(new AppLovinAdLoadListener() {
                            @Override
                            public void adReceived(AppLovinAd ad) {
                                Log.d(TAG, "AppLovin Discovery Interstitial Ad loaded");
                            }

                            @Override
                            public void failedToReceiveAd(int errorCode) {
                                loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                Log.d(TAG, "AppLovin Discovery Interstitial Ad failed to load");
                            }
                        });
                        appLovinDiscoveryInterstitialAd.loadNextAd();
                        break;

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        IronSource.setLevelPlayInterstitialListener(new InterstitialListener() {
                            @Override
                            public void onInterstitialAdReady() {
                                Log.d(TAG, "IronSource Interstitial Ad loaded");
                            }

                            @Override
                            public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
                                loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                Log.d(TAG, "IronSource Interstitial Ad failed to load: "
                                        + ironSourceError.getErrorMessage());
                            }

                            @Override
                            public void onInterstitialAdOpened() {
                            }

                            @Override
                            public void onInterstitialAdClosed() {
                                loadInterstitialAd(onInterstitialAdDismissedListener);
                                onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                            }

                            @Override
                            public void onInterstitialAdShowSucceeded() {
                            }

                            @Override
                            public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
                            }

                            @Override
                            public void onInterstitialAdClicked() {
                            }
                        });
                        IronSource.loadInterstitial();
                        break;

                    case STARTAPP:
                        startAppInterstitialAd = new StartAppAd(activity);
                        startAppInterstitialAd.loadAd(new AdEventListener() {
                            @Override
                            public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                Log.d(TAG, "StartApp Interstitial Ad loaded");
                            }

                            @Override
                            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                Log.d(TAG, "StartApp Interstitial Ad failed to load");
                            }
                        });
                        break;

                    case WORTISE:
                        wortiseInterstitialAd = new com.wortise.ads.interstitial.InterstitialAd(activity,
                                wortiseInterstitialId);
                        wortiseInterstitialAd.setListener(new com.wortise.ads.interstitial.InterstitialAd.Listener() {
                            @Override
                            public void onInterstitialClicked(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                            }

                            @Override
                            public void onInterstitialDismissed(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                loadInterstitialAd(onInterstitialAdDismissedListener);
                                onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                            }

                            @Override
                            public void onInterstitialFailedToLoad(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                    @NonNull com.wortise.ads.AdError adError) {
                                loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                Log.d(TAG, "Wortise Interstitial Ad failed to load");
                            }

                            @Override
                            public void onInterstitialFailedToShow(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                    @NonNull com.wortise.ads.AdError adError) {
                            }

                            @Override
                            public void onInterstitialImpression(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                            }

                            @Override
                            public void onInterstitialLoaded(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                Log.d(TAG, "Wortise Interstitial Ad loaded");
                            }

                            @Override
                            public void onInterstitialShown(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                            }
                        });
                        wortiseInterstitialAd.loadAd();
                        break;

                    default:
                        break;
                }
            }
        }

        public void loadBackupInterstitialAd(OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (backupAdNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        com.google.android.gms.ads.interstitial.InterstitialAd.load(activity, adMobInterstitialId,
                                Tools.getAdRequest(activity, legacyGDPR), new InterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(
                                            @NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                                        adMobInterstitialAd = interstitialAd;
                                        adMobInterstitialAd
                                                .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        loadInterstitialAd(onInterstitialAdDismissedListener);
                                                        onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                                                    }

                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(
                                                            @NonNull com.google.android.gms.ads.AdError adError) {
                                                        Log.d(TAG, "The ad failed to show.");
                                                    }

                                                    @Override
                                                    public void onAdShowedFullScreenContent() {
                                                        adMobInterstitialAd = null;
                                                        Log.d(TAG, "The ad was shown.");
                                                    }
                                                });
                                        Log.i(TAG, "onAdLoaded");
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        Log.i(TAG, loadAdError.getMessage());
                                        adMobInterstitialAd = null;
                                        Log.d(TAG, "Failed load AdMob Interstitial Ad");
                                    }
                                });
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        AdManagerInterstitialAd.load(activity, googleAdManagerInterstitialId,
                                Tools.getGoogleAdManagerRequest(), new AdManagerInterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                                        super.onAdLoaded(interstitialAd);
                                        adManagerInterstitialAd = interstitialAd;
                                        adManagerInterstitialAd
                                                .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdClicked() {
                                                        super.onAdClicked();
                                                    }

                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        super.onAdDismissedFullScreenContent();
                                                        loadInterstitialAd(onInterstitialAdDismissedListener);
                                                        onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                                                    }

                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(
                                                            @NonNull AdError adError) {
                                                        super.onAdFailedToShowFullScreenContent(adError);
                                                    }

                                                    @Override
                                                    public void onAdImpression() {
                                                        super.onAdImpression();
                                                    }

                                                    @Override
                                                    public void onAdShowedFullScreenContent() {
                                                        super.onAdShowedFullScreenContent();
                                                        adManagerInterstitialAd = null;
                                                        Log.d(TAG, "The ad was shown.");
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        super.onAdFailedToLoad(loadAdError);
                                        adManagerInterstitialAd = null;
                                        Log.d(TAG, "Failed load Ad Manager Interstitial Ad");
                                    }
                                });
                        break;

                    case FAN:
                    case FACEBOOK:
                        fanInterstitialAd = new com.facebook.ads.InterstitialAd(activity, fanInterstitialId);
                        com.facebook.ads.InterstitialAdListener adListener = new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
                                fanInterstitialAd.loadAd();
                                onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                            }

                            @Override
                            public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {

                            }

                            @Override
                            public void onAdLoaded(com.facebook.ads.Ad ad) {
                                Log.d(TAG, "FAN Interstitial is loaded");
                            }

                            @Override
                            public void onAdClicked(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(com.facebook.ads.Ad ad) {

                            }
                        };

                        com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = fanInterstitialAd
                                .buildLoadAdConfig().withAdListener(adListener).build();
                        fanInterstitialAd.loadAd(loadAdConfig);
                        break;

                    case UNITY:
                        UnityAds.load(unityInterstitialId, new IUnityAdsLoadListener() {
                            @Override
                            public void onUnityAdsAdLoaded(String placementId) {
                                Log.d(TAG, "Unity Interstitial Ad loaded");
                            }

                            @Override
                            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error,
                                    String message) {
                                Log.d(TAG, "Unity Interstitial Ad failed to load: " + error + " - " + message);
                            }
                        });
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        appLovinMaxInterstitialAd = new MaxInterstitialAd(appLovinInterstitialId, activity);
                        appLovinMaxInterstitialAd.setListener(new MaxAdListener() {
                            @Override
                            public void onAdLoaded(MaxAd ad) {
                                Log.d(TAG, "AppLovin Interstitial Ad loaded");
                            }

                            @Override
                            public void onAdDisplayed(MaxAd ad) {
                            }

                            @Override
                            public void onAdHidden(MaxAd ad) {
                                loadInterstitialAd(onInterstitialAdDismissedListener);
                                onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                            }

                            @Override
                            public void onAdClicked(MaxAd ad) {
                            }

                            @Override
                            public void onAdLoadFailed(String adUnitId, MaxError error) {
                                Log.d(TAG, "AppLovin Interstitial Ad failed to load: " + error.getMessage());
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                            }
                        });
                        appLovinMaxInterstitialAd.loadAd();
                        break;

                    case APPLOVIN_DISCOVERY:
                        appLovinDiscoveryInterstitialAd = com.applovin.adview.AppLovinInterstitialAd
                                .create(AppLovinSdkUtils.getZone(activity.getApplicationContext(),
                                        appLovinInterstitialZoneId, activity), activity);
                        appLovinDiscoveryInterstitialAd.setAdLoadListener(new AppLovinAdLoadListener() {
                            @Override
                            public void adReceived(AppLovinAd ad) {
                                Log.d(TAG, "AppLovin Discovery Interstitial Ad loaded");
                            }

                            @Override
                            public void failedToReceiveAd(int errorCode) {
                                Log.d(TAG, "AppLovin Discovery Interstitial Ad failed to load");
                            }
                        });
                        appLovinDiscoveryInterstitialAd.loadNextAd();
                        break;

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        IronSource.setLevelPlayInterstitialListener(new InterstitialListener() {
                            @Override
                            public void onInterstitialAdReady() {
                                Log.d(TAG, "IronSource Interstitial Ad loaded");
                            }

                            @Override
                            public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
                                Log.d(TAG, "IronSource Interstitial Ad failed to load: "
                                        + ironSourceError.getErrorMessage());
                            }

                            @Override
                            public void onInterstitialAdOpened() {
                            }

                            @Override
                            public void onInterstitialAdClosed() {
                                loadInterstitialAd(onInterstitialAdDismissedListener);
                                onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                            }

                            @Override
                            public void onInterstitialAdShowSucceeded() {
                            }

                            @Override
                            public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
                            }

                            @Override
                            public void onInterstitialAdClicked() {
                            }
                        });
                        IronSource.loadInterstitial();
                        break;

                    case STARTAPP:
                        startAppInterstitialAd = new StartAppAd(activity);
                        startAppInterstitialAd.loadAd(new AdEventListener() {
                            @Override
                            public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                Log.d(TAG, "StartApp Interstitial Ad loaded");
                            }

                            @Override
                            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                Log.d(TAG, "StartApp Interstitial Ad failed to load");
                            }
                        });
                        break;

                    case WORTISE:
                        wortiseInterstitialAd = new com.wortise.ads.interstitial.InterstitialAd(activity,
                                wortiseInterstitialId);
                        wortiseInterstitialAd.setListener(new com.wortise.ads.interstitial.InterstitialAd.Listener() {
                            @Override
                            public void onInterstitialClicked(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                            }

                            @Override
                            public void onInterstitialDismissed(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                loadInterstitialAd(onInterstitialAdDismissedListener);
                                onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                            }

                            @Override
                            public void onInterstitialFailedToLoad(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                    @NonNull com.wortise.ads.AdError adError) {
                                Log.d(TAG, "Wortise Interstitial Ad failed to load");
                            }

                            @Override
                            public void onInterstitialFailedToShow(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                    @NonNull com.wortise.ads.AdError adError) {
                            }

                            @Override
                            public void onInterstitialImpression(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                            }

                            @Override
                            public void onInterstitialLoaded(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                Log.d(TAG, "Wortise Interstitial Ad loaded");
                            }

                            @Override
                            public void onInterstitialShown(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                            }
                        });
                        wortiseInterstitialAd.loadAd();
                        break;

                    default:
                        break;
                }
            }
        }

        public void showInterstitialAd(OnInterstitialAdShowedListener onInterstitialAdShowedListener,
                OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                if (counter == interval) {
                    switch (adNetwork) {
                        case ADMOB:
                        case FAN_BIDDING_ADMOB:
                            if (adMobInterstitialAd != null) {
                                adMobInterstitialAd.show(activity);
                                onInterstitialAdShowedListener.onInterstitialAdShowed();
                                Log.d(TAG, "admob interstitial not null");
                            } else {
                                showBackupInterstitialAd(onInterstitialAdShowedListener,
                                        onInterstitialAdDismissedListener);
                                Log.d(TAG, "admob interstitial null");
                            }
                            break;

                        case GOOGLE_AD_MANAGER:
                        case FAN_BIDDING_AD_MANAGER:
                            if (adManagerInterstitialAd != null) {
                                adManagerInterstitialAd.show(activity);
                                onInterstitialAdShowedListener.onInterstitialAdShowed();
                                Log.d(TAG, "ad manager interstitial not null");
                            } else {
                                showBackupInterstitialAd(onInterstitialAdShowedListener,
                                        onInterstitialAdDismissedListener);
                                Log.d(TAG, "ad manager interstitial null");
                            }
                            break;

                        case FAN:
                        case FACEBOOK:
                            if (fanInterstitialAd != null && fanInterstitialAd.isAdLoaded()) {
                                fanInterstitialAd.show();
                                onInterstitialAdShowedListener.onInterstitialAdShowed();
                                Log.d(TAG, "fan interstitial not null");
                            } else {
                                showBackupInterstitialAd(onInterstitialAdShowedListener,
                                        onInterstitialAdDismissedListener);
                                Log.d(TAG, "fan interstitial null");
                            }
                            break;

                        case UNITY:
                            if (UnityAds.isInitialized()) {
                                UnityAds.show(activity, unityInterstitialId, new IUnityAdsShowListener() {
                                    @Override
                                    public void onUnityAdsShowFailure(String placementId,
                                            UnityAds.UnityAdsShowError error, String message) {
                                        showBackupInterstitialAd(onInterstitialAdShowedListener,
                                                onInterstitialAdDismissedListener);
                                    }

                                    @Override
                                    public void onUnityAdsShowStart(String placementId) {
                                        onInterstitialAdShowedListener.onInterstitialAdShowed();
                                    }

                                    @Override
                                    public void onUnityAdsShowClick(String placementId) {
                                    }

                                    @Override
                                    public void onUnityAdsShowComplete(String placementId,
                                            UnityAds.UnityAdsShowCompletionState state) {
                                    }
                                });
                            } else {
                                showBackupInterstitialAd(onInterstitialAdShowedListener,
                                        onInterstitialAdDismissedListener);
                            }
                            break;

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case FAN_BIDDING_APPLOVIN_MAX:
                            if (appLovinMaxInterstitialAd != null && appLovinMaxInterstitialAd.isReady()) {
                                appLovinMaxInterstitialAd.showAd();
                                onInterstitialAdShowedListener.onInterstitialAdShowed();
                            } else {
                                showBackupInterstitialAd(onInterstitialAdShowedListener,
                                        onInterstitialAdDismissedListener);
                            }
                            break;

                        case APPLOVIN_DISCOVERY:
                            if (appLovinDiscoveryInterstitialAd != null
                                    && appLovinDiscoveryInterstitialAd.isAdReadyToDisplay()) {
                                appLovinDiscoveryInterstitialAd.show();
                                onInterstitialAdShowedListener.onInterstitialAdShowed();
                            } else {
                                showBackupInterstitialAd(onInterstitialAdShowedListener,
                                        onInterstitialAdDismissedListener);
                            }
                            break;

                        case IRONSOURCE:
                        case FAN_BIDDING_IRONSOURCE:
                            if (IronSource.isInterstitialReady()) {
                                IronSource.showInterstitial();
                                onInterstitialAdShowedListener.onInterstitialAdShowed();
                            } else {
                                showBackupInterstitialAd(onInterstitialAdShowedListener,
                                        onInterstitialAdDismissedListener);
                            }
                            break;

                        case STARTAPP:
                            if (startAppInterstitialAd != null && startAppInterstitialAd.isReady()) {
                                startAppInterstitialAd.showAd();
                                onInterstitialAdShowedListener.onInterstitialAdShowed();
                            } else {
                                showBackupInterstitialAd(onInterstitialAdShowedListener,
                                        onInterstitialAdDismissedListener);
                            }
                            break;

                        case WORTISE:
                            if (wortiseInterstitialAd != null && wortiseInterstitialAd.isAvailable()) {
                                wortiseInterstitialAd.showAd();
                                onInterstitialAdShowedListener.onInterstitialAdShowed();
                            } else {
                                showBackupInterstitialAd(onInterstitialAdShowedListener,
                                        onInterstitialAdDismissedListener);
                            }
                            break;

                        default:
                            break;
                    }
                    counter = 1;
                } else {
                    onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                    counter++;
                }
                Log.d(TAG, "Current counter : " + counter);
            } else {
                onInterstitialAdDismissedListener.onInterstitialAdDismissed();
            }
        }

        public void showBackupInterstitialAd(OnInterstitialAdShowedListener onInterstitialAdShowedListener,
                OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                Log.d(TAG, "Show Backup Interstitial Ad [" + backupAdNetwork.toUpperCase() + "]");
                switch (backupAdNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        if (adMobInterstitialAd != null) {
                            adMobInterstitialAd.show(activity);
                            onInterstitialAdShowedListener.onInterstitialAdShowed();
                        }
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        if (adManagerInterstitialAd != null) {
                            adManagerInterstitialAd.show(activity);
                            onInterstitialAdShowedListener.onInterstitialAdShowed();
                        }
                        break;

                    case FAN:
                    case FACEBOOK:
                        if (fanInterstitialAd != null && fanInterstitialAd.isAdLoaded()) {
                            fanInterstitialAd.show();
                            onInterstitialAdShowedListener.onInterstitialAdShowed();
                        }
                        break;

                    case UNITY:
                        if (UnityAds.isInitialized()) {
                            UnityAds.show(activity, unityInterstitialId, new IUnityAdsShowListener() {
                                @Override
                                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error,
                                        String message) {
                                }

                                @Override
                                public void onUnityAdsShowStart(String placementId) {
                                    onInterstitialAdShowedListener.onInterstitialAdShowed();
                                }

                                @Override
                                public void onUnityAdsShowClick(String placementId) {
                                }

                                @Override
                                public void onUnityAdsShowComplete(String placementId,
                                        UnityAds.UnityAdsShowCompletionState state) {
                                }
                            });
                        }
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        if (appLovinMaxInterstitialAd != null && appLovinMaxInterstitialAd.isReady()) {
                            appLovinMaxInterstitialAd.showAd();
                            onInterstitialAdShowedListener.onInterstitialAdShowed();
                        }
                        break;

                    case APPLOVIN_DISCOVERY:
                        if (appLovinDiscoveryInterstitialAd != null
                                && appLovinDiscoveryInterstitialAd.isAdReadyToDisplay()) {
                            appLovinDiscoveryInterstitialAd.show();
                            onInterstitialAdShowedListener.onInterstitialAdShowed();
                        }
                        break;

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        if (IronSource.isInterstitialReady()) {
                            IronSource.showInterstitial();
                            onInterstitialAdShowedListener.onInterstitialAdShowed();
                        }
                        break;

                    case STARTAPP:
                        if (startAppInterstitialAd != null && startAppInterstitialAd.isReady()) {
                            startAppInterstitialAd.showAd();
                            onInterstitialAdShowedListener.onInterstitialAdShowed();
                        }
                        break;

                    case WORTISE:
                        if (wortiseInterstitialAd != null && wortiseInterstitialAd.isAvailable()) {
                            wortiseInterstitialAd.showAd();
                            onInterstitialAdShowedListener.onInterstitialAdShowed();
                        }
                        break;

                    default:
                        break;
                }
            } else {
                onInterstitialAdDismissedListener.onInterstitialAdDismissed();
            }
        }

        /**
         * Destroys and releases all interstitial ad resources to prevent memory leaks.
         * Should be called when the hosting Activity is destroyed.
         */
        public void destroyInterstitialAd() {
            if (adMobInterstitialAd != null) {
                adMobInterstitialAd = null;
            }
            if (adManagerInterstitialAd != null) {
                adManagerInterstitialAd = null;
            }
            if (fanInterstitialAd != null) {
                fanInterstitialAd.destroy();
                fanInterstitialAd = null;
            }
            if (appLovinMaxInterstitialAd != null) {
                appLovinMaxInterstitialAd.destroy();
                appLovinMaxInterstitialAd = null;
            }
            if (appLovinDiscoveryInterstitialAd != null) {
                appLovinDiscoveryInterstitialAd = null;
            }
            if (wortiseInterstitialAd != null) {
                wortiseInterstitialAd.destroy();
                wortiseInterstitialAd = null;
            }
            if (startAppInterstitialAd != null) {
                startAppInterstitialAd = null;
            }
        }

    }

}
