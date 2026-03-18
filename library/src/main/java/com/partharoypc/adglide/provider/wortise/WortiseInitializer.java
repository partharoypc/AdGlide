package com.partharoypc.adglide.provider.wortise;

import android.content.Context;
import com.partharoypc.adglide.provider.NetworkInitializer;
import com.wortise.ads.WortiseSdk;
import com.wortise.ads.AdSettings;

public class WortiseInitializer implements NetworkInitializer {
    @Override
    public void initialize(Context context, InitializerConfig config) {
        WortiseSdk.initialize(context, config.getAppId());
        AdSettings.setTestEnabled(config.isDebug() || config.isTestMode());
    }
}
