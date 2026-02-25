package com.partharoypc.adglide.provider.meta;

import android.content.Context;
import com.partharoypc.adglide.provider.NetworkInitializer;
import com.partharoypc.adglide.helper.AudienceNetworkInitializeHelper;

public class MetaInitializer implements NetworkInitializer {
    @Override
    public void initialize(Context context, InitializerConfig config) {
        AudienceNetworkInitializeHelper.initializeAd(context, config.isDebug() || config.isTestMode());
    }
}
