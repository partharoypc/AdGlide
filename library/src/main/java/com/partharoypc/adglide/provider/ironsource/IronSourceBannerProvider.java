package com.partharoypc.adglide.provider.ironsource;

import android.app.Activity;
import com.partharoypc.adglide.provider.BannerProvider;
import com.unity3d.mediation.LevelPlayAdSize;
import com.unity3d.mediation.banner.LevelPlayBannerAdView;
import com.unity3d.mediation.banner.LevelPlayBannerAdViewListener;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.LevelPlayAdError;
import androidx.annotation.NonNull;

public class IronSourceBannerProvider implements BannerProvider {
    private LevelPlayBannerAdView bannerAdView;

    @Override
    public void loadBanner(Activity activity, String adUnitId, BannerConfig config, BannerListener listener) {
        if (!com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).isNetworkHealed("ironsource")) {
            listener.onAdFailedToLoad("IronSource is currently healing from recent failures.");
            return;
        }
        // Removed redundant notifyLoadStarted call

        LevelPlayAdSize size = config.isMrec() ? LevelPlayAdSize.MEDIUM_RECTANGLE : LevelPlayAdSize.BANNER;
        LevelPlayBannerAdView.Config adConfig = new LevelPlayBannerAdView.Config.Builder()
                .setAdSize(size)
                .build();
        bannerAdView = new LevelPlayBannerAdView(activity, adUnitId, adConfig);
        bannerAdView.setBannerListener(new LevelPlayBannerAdViewListener() {
            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo adInfo) {
                com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).recordSuccess("ironsource", adUnitId);
                listener.onAdLoaded(bannerAdView);
            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError error) {
                com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).recordFailure("ironsource", adUnitId);
                listener.onAdFailedToLoad(error.getErrorMessage());
            }

            @Override
            public void onAdDisplayed(@NonNull LevelPlayAdInfo adInfo) {
            }

            @Override
            public void onAdDisplayFailed(@NonNull LevelPlayAdInfo adInfo, @NonNull LevelPlayAdError error) {
            }

            @Override
            public void onAdClicked(@NonNull LevelPlayAdInfo adInfo) {
            }

            @Override
            public void onAdExpanded(@NonNull LevelPlayAdInfo adInfo) {
            }

            @Override
            public void onAdCollapsed(@NonNull LevelPlayAdInfo adInfo) {
            }

            @Override
            public void onAdLeftApplication(@NonNull LevelPlayAdInfo adInfo) {
            }
        });

        bannerAdView.loadAd();
    }

    @Override
    public void destroy() {
        if (bannerAdView != null) {
            bannerAdView.destroy();
            bannerAdView = null;
        }
    }
}
