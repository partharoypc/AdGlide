package com.partharoypc.adglide.format;

import com.partharoypc.adglide.AdGlideNetwork;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;

import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.partharoypc.adglide.util.OnRewardedAdCompleteListener;
import com.partharoypc.adglide.util.OnRewardedAdDismissedListener;
import com.partharoypc.adglide.util.OnRewardedAdErrorListener;
import com.partharoypc.adglide.util.OnRewardedAdLoadedListener;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

/**
 * Handles loading and displaying rewarded interstitial ads.
 * Currently supports AdMob.
 */
public class RewardedInterstitialAd {

    public static class Builder {

        private static final String TAG = "AdGlide";
        private final Activity activity;
        private com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd adMobRewardedInterstitialAd;

        private boolean adStatus = true;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;

        private String adMobRewardedInterstitialId = "";

        private int placementStatus = 1;
        private boolean legacyGDPR = false;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        @androidx.annotation.NonNull
        public Builder build(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss) {
            loadRewardedInterstitialAd(onComplete, onDismiss);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder show(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            showRewardedInterstitialAd(null, onComplete, onDismiss, onError);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder show(@NonNull Activity displayActivity, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            showRewardedInterstitialAd(displayActivity, onComplete, onDismiss, onError);
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
            if (!backupAdNetwork.isEmpty()) {
                this.waterfallManager = new WaterfallManager(backupAdNetwork);
            }
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
        public Builder adMobId(@androidx.annotation.NonNull String adMobRewardedInterstitialId) {
            this.adMobRewardedInterstitialId = adMobRewardedInterstitialId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder placement(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder legacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public void loadRewardedInterstitialAd(OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss) {
            try {
                if (adStatus && placementStatus != 0) {
                    if (waterfallManager != null) {
                        waterfallManager.reset();
                    }
                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            if (!com.partharoypc.adglide.util.AdMobRateLimiter
                                    .isRequestAllowed(adMobRewardedInterstitialId)) {
                                loadRewardedBackupAd(onComplete, onDismiss);
                                return;
                            }
                            com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd.load(activity,
                                    adMobRewardedInterstitialId,
                                    Tools.getAdRequest(activity, legacyGDPR), new RewardedInterstitialAdLoadCallback() {
                                        @Override
                                        public void onAdLoaded(
                                                @NonNull com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd ad) {
                                            com.partharoypc.adglide.util.AdMobRateLimiter
                                                    .resetCooldown(adMobRewardedInterstitialId);
                                            adMobRewardedInterstitialAd = ad;
                                            adMobRewardedInterstitialAd
                                                    .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                        @Override
                                                        public void onAdDismissedFullScreenContent() {
                                                            super.onAdDismissedFullScreenContent();
                                                            adMobRewardedInterstitialAd = null;
                                                            loadRewardedInterstitialAd(onComplete, onDismiss);
                                                            onDismiss.onRewardedAdDismissed();
                                                        }

                                                        @Override
                                                        public void onAdFailedToShowFullScreenContent(
                                                                @NonNull AdError adError) {
                                                            super.onAdFailedToShowFullScreenContent(adError);
                                                            adMobRewardedInterstitialAd = null;
                                                        }
                                                    });
                                            Log.d(TAG, "[" + adNetwork + "] " + "rewarded interstitial ad loaded");
                                        }

                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                            if (loadAdError
                                                    .getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                                                com.partharoypc.adglide.util.AdMobRateLimiter
                                                        .recordFailure(adMobRewardedInterstitialId);
                                            }
                                            adMobRewardedInterstitialAd = null;
                                            loadRewardedBackupAd(onComplete, onDismiss);
                                            Log.d(TAG,
                                                    "[" + adNetwork + "] " + "failed to load rewarded interstitial ad: "
                                                            + loadAdError.getMessage());
                                        }
                                    });
                            break;

                        default:
                            loadRewardedBackupAd(onComplete, onDismiss);
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadRewardedInterstitialAd: " + e.getMessage());
                loadRewardedBackupAd(onComplete, onDismiss);
            }
        }

        public void loadRewardedBackupAd(OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss) {
            try {
                if (adStatus && placementStatus != 0) {
                    if (waterfallManager == null) {
                        if (!backupAdNetwork.isEmpty()) {
                            waterfallManager = new WaterfallManager(backupAdNetwork);
                        } else {
                            return;
                        }
                    }

                    String networkToLoad = waterfallManager.getNext();
                    if (networkToLoad == null) {
                        Log.d(TAG, "All backup rewarded interstitial ads failed to load");
                        return;
                    }
                    backupAdNetwork = networkToLoad;
                    Log.d(TAG, "Loading Backup Rewarded Interstitial Ad ["
                            + backupAdNetwork.toUpperCase(java.util.Locale.ROOT) + "]");

                    switch (backupAdNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd.load(activity,
                                    adMobRewardedInterstitialId,
                                    Tools.getAdRequest(activity, legacyGDPR), new RewardedInterstitialAdLoadCallback() {
                                        @Override
                                        public void onAdLoaded(
                                                @NonNull com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd ad) {
                                            adMobRewardedInterstitialAd = ad;
                                            adMobRewardedInterstitialAd
                                                    .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                        @Override
                                                        public void onAdDismissedFullScreenContent() {
                                                            super.onAdDismissedFullScreenContent();
                                                            adMobRewardedInterstitialAd = null;
                                                            loadRewardedInterstitialAd(onComplete, onDismiss);
                                                            onDismiss.onRewardedAdDismissed();
                                                        }

                                                        @Override
                                                        public void onAdFailedToShowFullScreenContent(
                                                                @NonNull AdError adError) {
                                                            super.onAdFailedToShowFullScreenContent(adError);
                                                            adMobRewardedInterstitialAd = null;
                                                        }
                                                    });
                                            Log.d(TAG, "[" + backupAdNetwork + "] [backup] "
                                                    + "rewarded interstitial ad loaded");
                                        }

                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                            loadRewardedBackupAd(onComplete, onDismiss);
                                        }
                                    });
                            break;

                        default:
                            loadRewardedBackupAd(onComplete, onDismiss);
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadRewardedBackupAd: " + e.getMessage());
            }
        }

        public void showRewardedInterstitialAd(OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdErrorListener onError) {
            showRewardedInterstitialAd(null, onComplete, onDismiss, onError);
        }

        public void showRewardedInterstitialAd(Activity displayActivity, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdErrorListener onError) {
            try {
                Activity targetActivity = displayActivity != null ? displayActivity : activity;
                if (adStatus && placementStatus != 0) {
                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            if (adMobRewardedInterstitialAd != null) {
                                adMobRewardedInterstitialAd.show(targetActivity, new OnUserEarnedRewardListener() {
                                    @Override
                                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                        onComplete.onRewardedAdComplete();
                                    }
                                });
                            } else {
                                showBackupRewardedInterstitialAd(targetActivity, onComplete, onDismiss, onError);
                            }
                            break;

                        default:
                            showBackupRewardedInterstitialAd(targetActivity, onComplete, onDismiss, onError);
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showRewardedInterstitialAd: " + e.getMessage());
                showBackupRewardedInterstitialAd(displayActivity, onComplete, onDismiss, onError);
            }
        }

        public void showBackupRewardedInterstitialAd(OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdErrorListener onError) {
            showBackupRewardedInterstitialAd(null, onComplete, onDismiss, onError);
        }

        public void showBackupRewardedInterstitialAd(Activity displayActivity, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdErrorListener onError) {
            try {
                Activity targetActivity = displayActivity != null ? displayActivity : activity;
                if (adStatus && placementStatus != 0) {
                    switch (backupAdNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            if (adMobRewardedInterstitialAd != null) {
                                adMobRewardedInterstitialAd.show(targetActivity, new OnUserEarnedRewardListener() {
                                    @Override
                                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                        onComplete.onRewardedAdComplete();
                                    }
                                });
                            } else {
                                onError.onRewardedAdError();
                            }
                            break;

                        default:
                            onError.onRewardedAdError();
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showBackupRewardedInterstitialAd: " + e.getMessage());
                onError.onRewardedAdError();
            }
        }

    }
}
