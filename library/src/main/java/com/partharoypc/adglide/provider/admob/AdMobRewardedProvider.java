package com.partharoypc.adglide.provider.admob;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.partharoypc.adglide.provider.RewardedProvider;

public class AdMobRewardedProvider implements RewardedProvider {
    private RewardedAd rewardedAd;
    private RewardedInterstitialAd rewardedInterstitialAd;

    @Override
    public void loadRewardedAd(Activity activity, String adUnitId, RewardedConfig config, RewardedListener listener) {
        if (!com.partharoypc.adglide.util.AdMobRateLimiter.isRequestAllowed(adUnitId)) {
            listener.onAdFailedToLoad("AdMob rate limit hit");
            return;
        }

        com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder().build();

        if (config.isInterstitial()) {
            RewardedInterstitialAd.load(activity, adUnitId, adRequest, new RewardedInterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                    com.partharoypc.adglide.util.AdMobRateLimiter.resetCooldown(adUnitId);
                    ad.setOnPaidEventListener(adValue -> {
                        com.partharoypc.adglide.util.OnPaidEventListener paidListener = com.partharoypc.adglide.AdGlide
                                .getConfig() != null
                                        ? com.partharoypc.adglide.AdGlide.getConfig().getOnPaidEventListener()
                                        : null;
                        if (paidListener != null) {
                            paidListener.onPaidEvent(adValue.getValueMicros(), adValue.getCurrencyCode(),
                                    String.valueOf(adValue.getPrecisionType()), "AdMob RewardedInterstitial", adUnitId);
                        }
                    });
                    rewardedInterstitialAd = ad;
                    setupInterstitialCallback(listener);
                    listener.onAdLoaded();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                    if (adError.getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                        com.partharoypc.adglide.util.AdMobRateLimiter.recordFailure(adUnitId);
                    }
                    listener.onAdFailedToLoad(adError.getMessage());
                }
            });
        } else {
            RewardedAd.load(activity, adUnitId, adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull RewardedAd ad) {
                    com.partharoypc.adglide.util.AdMobRateLimiter.resetCooldown(adUnitId);
                    ad.setOnPaidEventListener(adValue -> {
                        com.partharoypc.adglide.util.OnPaidEventListener paidListener = com.partharoypc.adglide.AdGlide
                                .getConfig() != null
                                        ? com.partharoypc.adglide.AdGlide.getConfig().getOnPaidEventListener()
                                        : null;
                        if (paidListener != null) {
                            paidListener.onPaidEvent(adValue.getValueMicros(), adValue.getCurrencyCode(),
                                    String.valueOf(adValue.getPrecisionType()), "AdMob Rewarded", adUnitId);
                        }
                    });
                    rewardedAd = ad;
                    setupRewardedCallback(listener);
                    listener.onAdLoaded();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                    if (adError.getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                        com.partharoypc.adglide.util.AdMobRateLimiter.recordFailure(adUnitId);
                    }
                    listener.onAdFailedToLoad(adError.getMessage());
                }
            });
        }
    }

    private void setupRewardedCallback(RewardedListener listener) {
        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                rewardedAd = null;
                listener.onAdDismissed();
            }
        });
    }

    private void setupInterstitialCallback(RewardedListener listener) {
        rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                rewardedInterstitialAd = null;
                listener.onAdDismissed();
            }
        });
    }

    @Override
    public void showRewardedAd(Activity activity, RewardedListener listener) {
        if (rewardedInterstitialAd != null) {
            rewardedInterstitialAd.show(activity, rewardItem -> listener.onAdCompleted());
        } else if (rewardedAd != null) {
            rewardedAd.show(activity, rewardItem -> listener.onAdCompleted());
        }
    }

    @Override
    public boolean isAdAvailable() {
        return rewardedAd != null || rewardedInterstitialAd != null;
    }

    @Override
    public void destroy() {
        rewardedAd = null;
        rewardedInterstitialAd = null;
    }
}
