package com.partharoypc.adglide.util;

import android.content.Context;
import android.util.Log;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles fetching dynamic configurations from a remote JSON endpoint.
 */
public class RemoteConfigManager {
    private static final String TAG = "AdGlide.Remote";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static boolean isFetching = false;

    public interface OnConfigFetchedListener {
        void onSuccess(AdGlideConfig.Builder updatedBuilder);

        void onFailure(Exception e);
    }

    /**
     * Fetches JSON from the URL and applies it to the current config.
     */
    public static void fetch(String configUrl, OnConfigFetchedListener listener) {
        if (isFetching) {
            Log.d(TAG, "Fetch already in progress. Skipping duplicate request.");
            return;
        }
        isFetching = true;
        PerformanceLogger.log("CORE", "Fetching remote config from: " + configUrl);
        executor.execute(() -> {
            try {
                URL url = new java.net.URI(configUrl).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                AdGlideConfig.Builder builder = parseJson(json);
                PerformanceLogger.log("CORE", "Remote config fetched successfully");
                if (listener != null) {
                    listener.onSuccess(builder);
                }
            } catch (Exception e) {
                PerformanceLogger.error("CORE", "Remote config fetch failed: " + e.getMessage());
                if (listener != null) {
                    listener.onFailure(e);
                }
            } finally {
                isFetching = false;
            }
        });
    }

    private static AdGlideConfig.Builder parseJson(JSONObject json) throws Exception {
        AdGlideConfig.Builder builder = new AdGlideConfig.Builder();

        if (json.has("ad_status"))
            builder.enableAds(json.getBoolean("ad_status"));
        if (json.has("primary_network"))
            builder.primaryNetwork(json.getString("primary_network"));
        if (json.has("test_mode"))
            builder.testMode(json.getBoolean("test_mode"));

        // Granular Ad Type Status
        if (json.has("banner_status"))
            builder.bannerStatus(json.getBoolean("banner_status"));
        if (json.has("interstitial_status"))
            builder.interstitialStatus(json.getBoolean("interstitial_status"));
        if (json.has("native_status"))
            builder.nativeStatus(json.getBoolean("native_status"));
        if (json.has("rewarded_status"))
            builder.rewardedStatus(json.getBoolean("rewarded_status"));
        if (json.has("app_open_status"))
            builder.appOpenStatus(json.getBoolean("app_open_status"));

        // AdMob IDs
        if (json.has("admob_app_id"))
            builder.adMobAppId(json.getString("admob_app_id"));
        if (json.has("admob_banner"))
            builder.adMobBannerId(json.getString("admob_banner"));
        if (json.has("admob_interstitial"))
            builder.adMobInterstitialId(json.getString("admob_interstitial"));
        if (json.has("admob_rewarded"))
            builder.adMobRewardedId(json.getString("admob_rewarded"));
        if (json.has("admob_native"))
            builder.adMobNativeId(json.getString("admob_native"));
        if (json.has("admob_app_open"))
            builder.adMobAppOpenId(json.getString("admob_app_open"));

        // Intervals
        if (json.has("interstitial_interval"))
            builder.interstitialInterval(json.getInt("interstitial_interval"));
        if (json.has("rewarded_interval"))
            builder.rewardedInterval(json.getInt("rewarded_interval"));

        return builder;
    }
}
