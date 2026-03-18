package com.partharoypc.adglide.provider.meta;

import android.content.Context;
import com.facebook.ads.AdSettings;
import com.partharoypc.adglide.provider.NetworkInitializer;
import com.partharoypc.adglide.helper.AudienceNetworkInitializeHelper;

public class MetaInitializer implements NetworkInitializer {
    @Override
    public void initialize(Context context, InitializerConfig config) {
        // Support CCPA - Set Data Processing Options
        // By default, we set to empty array to allow Meta to use its default behavior
        // based on the user's location, which is usually what's needed.
        AdSettings.setDataProcessingOptions(new String[] {});

        AudienceNetworkInitializeHelper.initializeAd(context, config.isDebug() || config.isTestMode());
    }
}
