package com.partharoypc.adglide.provider.ironsource;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import com.partharoypc.adglide.provider.BannerProvider;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener;

public class IronSourceBannerProvider implements BannerProvider {
    private IronSourceBannerLayout ironSourceBannerLayout;

    @Override
    public void loadBanner(Activity activity, String adUnitId, BannerConfig config, BannerListener listener) {
        ISBannerSize size = config.isMrec() ? ISBannerSize.RECTANGLE : ISBannerSize.BANNER;
        ironSourceBannerLayout = IronSource.createBanner(activity, size);
        ironSourceBannerLayout.setLevelPlayBannerListener(new LevelPlayBannerListener() {
            @Override
            public void onAdLoaded(AdInfo adInfo) {
                listener.onAdLoaded(ironSourceBannerLayout);
            }

            @Override
            public void onAdLoadFailed(IronSourceError error) {
                listener.onAdFailedToLoad(error.getErrorMessage());
            }

            @Override
            public void onAdClicked(AdInfo adInfo) {
            }

            @Override
            public void onAdScreenPresented(AdInfo adInfo) {
            }

            @Override
            public void onAdScreenDismissed(AdInfo adInfo) {
            }

            @Override
            public void onAdLeftApplication(AdInfo adInfo) {
            }
        });

        IronSource.loadBanner(ironSourceBannerLayout, adUnitId);
    }

    @Override
    public void destroy() {
        if (ironSourceBannerLayout != null) {
            IronSource.destroyBanner(ironSourceBannerLayout);
            ironSourceBannerLayout = null;
        }
    }
}
