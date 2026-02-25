package com.partharoypc.adglide.provider;

import android.content.Context;

public interface NetworkInitializer {
    void initialize(Context context, InitializerConfig config);

    interface InitializerConfig {
        String getAppId();

        boolean isDebug();

        boolean isTestMode();
    }
}
