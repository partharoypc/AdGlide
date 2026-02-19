package com.partharoypc.adglide.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Helper class to fetch and parse remote configuration JSON.
 */
public class RemoteConfigHelper {

    private static final String TAG = "RemoteConfigHelper";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface ConfigListener {
        void onConfigFetched(JSONObject config);

        void onConfigFailed(String error);
    }

    /**
     * Fetches JSON configuration from the specified URL.
     *
     * @param configUrl The URL to fetch the JSON from.
     * @param listener  The listener to receive the result.
     */
    public void fetchConfig(@NonNull String configUrl, @NonNull ConfigListener listener) {
        executor.execute(() -> {
            HttpURLConnection urlConnection = null;
            try {
                URL url = java.net.URI.create(configUrl).toURL();
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    JSONObject jsonConfig = new JSONObject(response.toString());
                    mainHandler.post(() -> listener.onConfigFetched(jsonConfig));
                } else {
                    mainHandler.post(() -> listener.onConfigFailed("HTTP Error: " + responseCode));
                }

            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching config", e);
                mainHandler.post(() -> listener.onConfigFailed(e.getMessage()));
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        });
    }
}
