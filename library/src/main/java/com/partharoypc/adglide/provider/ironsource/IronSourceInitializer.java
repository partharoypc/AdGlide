package com.partharoypc.adglide.provider.ironsource;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.partharoypc.adglide.provider.NetworkInitializer;
import com.ironsource.mediationsdk.IronSource;
import com.partharoypc.adglide.helper.AudienceNetworkInitializeHelper;

public class IronSourceInitializer implements NetworkInitializer {
    private static final String TAG = "AdGlide.IronSource";

    @Override
    public void initialize(Context context, InitializerConfig config) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            IronSource.init(activity, config.getAppId(), IronSource.AD_UNIT.REWARDED_VIDEO,
                    IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.BANNER);
        } else {
            Log.e(TAG, "IronSource requires an Activity Context to initialize. Skipping IronSource init.");
        }
    }
}
