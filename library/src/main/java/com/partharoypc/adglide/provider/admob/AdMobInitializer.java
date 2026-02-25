package com.partharoypc.adglide.provider.admob;

import android.content.Context;
import android.util.Log;
import com.partharoypc.adglide.provider.NetworkInitializer;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.partharoypc.adglide.helper.AudienceNetworkInitializeHelper;
import java.util.Map;

public class AdMobInitializer implements NetworkInitializer {
    private static final String TAG = "AdGlide.AdMob";

    @Override
    public void initialize(Context context, InitializerConfig config) {
        MobileAds.initialize(context, initializationStatus -> {
            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
            for (String adapterClass : statusMap.keySet()) {
                AdapterStatus adapterStatus = statusMap.get(adapterClass);
                if (adapterStatus != null) {
                    Log.d(TAG, String.format("Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, adapterStatus.getDescription(), adapterStatus.getLatency()));
                }
            }
        });
    }
}
