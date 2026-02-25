package com.partharoypc.adglide.provider.wortise;

import android.content.Context;
import com.partharoypc.adglide.provider.NetworkInitializer;
import com.wortise.ads.WortiseSdk;

public class WortiseInitializer implements NetworkInitializer {
    @Override
    public void initialize(Context context, InitializerConfig config) {
        WortiseSdk.initialize(context, config.getAppId());
        // WortiseSdk.setTestMode(config.isDebug() || config.isTestMode());
    }
}
