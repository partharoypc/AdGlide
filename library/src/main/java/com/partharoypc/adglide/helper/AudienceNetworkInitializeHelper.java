package com.partharoypc.adglide.helper;

import android.content.Context;
import com.partharoypc.adglide.util.AdGlideLog;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.partharoypc.adglide.BuildConfig;
public class AudienceNetworkInitializeHelper implements AudienceNetworkAds.InitListener {

    /**
     * It's recommended to call this method from Application.onCreate().
     * Otherwise you can call it from all Activity.onCreate()
     * methods for Activities that contain ads.
     *
     * @param context Application or Activity.
     */
    public static void initialize(Context context) {
        com.partharoypc.adglide.AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
        boolean isDebug = config != null && config.isDebug();
        initializeAd(context, isDebug);
    }

    public static void initializeAd(Context context, boolean debug) {
        if (debug) {
            AdSettings.turnOnSDKDebugger(context);
            AdSettings.setTestMode(true);
            AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE);
            AdGlideLog.d(AudienceNetworkAds.TAG, "Meta Audience Network initialized in Debug Mode");
        }

        if (!AudienceNetworkAds.isInitialized(context)) {
            AudienceNetworkAds
                    .buildInitSettings(context)
                    .withInitListener(new AudienceNetworkInitializeHelper())
                    .initialize();
        }
    }

    /**
     * Call this to add specific test devices. Use the hashed ID from the logs.
     * @param deviceId The hashed ID for the test device.
     */
    public static void addTestDevice(String deviceId) {
        AdSettings.addTestDevice(deviceId);
    }

    @Override
    public void onInitialized(AudienceNetworkAds.InitResult result) {
        AdGlideLog.d(AudienceNetworkAds.TAG, "Meta SDK Init Result: " + result.getMessage());
    }
}
