package com.partharoypc.adglide.provider.ironsource;

import android.app.Activity;
import android.content.Context;
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglide.provider.NetworkInitializer;
import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.LevelPlayInitListener;
import com.unity3d.mediation.LevelPlayInitRequest;
import com.unity3d.mediation.LevelPlayConfiguration;
import com.unity3d.mediation.LevelPlayInitError;
import androidx.annotation.NonNull;

public class IronSourceInitializer implements NetworkInitializer {
    private static final String TAG = "AdGlide.IronSource";

    @Override
    public void initialize(Context context, InitializerConfig config) {
        LevelPlayInitRequest initRequest = new LevelPlayInitRequest.Builder(config.getAppId())
                .build();

        LevelPlay.init(context, initRequest, new LevelPlayInitListener() {
            @Override
            public void onInitFailed(@NonNull LevelPlayInitError error) {
                AdGlideLog.e(TAG, "IronSource LevelPlay init failed: " + error.getErrorMessage());
            }

            @Override
            public void onInitSuccess(@NonNull LevelPlayConfiguration configuration) {
                AdGlideLog.d(TAG, "IronSource LevelPlay init successful.");
            }
        });
    }
}
