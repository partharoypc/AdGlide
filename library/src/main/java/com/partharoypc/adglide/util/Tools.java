package com.partharoypc.adglide.util;

import static com.partharoypc.adglide.util.Constant.TOKEN;
import static com.partharoypc.adglide.util.Constant.VALUE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowMetrics;

import androidx.annotation.NonNull;

import com.partharoypc.adglide.gdpr.LegacyGDPR;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import java.nio.charset.StandardCharsets;

/**
 * Utility class providing helper methods for ad request creation,
 * adaptive banner sizing, and string decoding operations.
 */
public class Tools {

    private static final String TAG = "AdGlide";

    /**
     * Checks if the device is currently connected to the internet.
     *
     * @param context the application context
     * @return true if network is available, false otherwise
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null)
            return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager
                        .getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else
                        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            } else {
                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        return true;
                    }
                } catch (Exception e) {
                    Log.i(TAG, "Exception in isNetworkAvailable: " + e.getMessage());
                }
            }
        }
        Log.i(TAG, "No internet connection available.");
        return false;
    }

    /**
     * Decodes a triple-encoded Base64 string.
     *
     * @param code the triple-encoded string
     * @return the decoded plain text
     */
    @NonNull
    public static String decode(@NonNull String code) {
        return decodeBase64(decodeBase64(decodeBase64(code)));
    }

    /**
     * Decodes a single Base64-encoded string.
     *
     * @param code the Base64-encoded string
     * @return the decoded plain text
     */
    @NonNull
    public static String decodeBase64(@NonNull String code) {
        byte[] valueDecoded = Base64.decode(code.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }

    /**
     * Decodes a Base64-encoded string after replacing token placeholders.
     *
     * @param code the token-replaced Base64-encoded string
     * @return the decoded plain text
     */
    @NonNull
    public static String jsonDecode(@NonNull String code) {
        String data = code.replace(TOKEN, VALUE);
        byte[] valueDecoded = Base64.decode(data.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }

    /**
     * Checks if the current device is a tablet.
     *
     * @param context the application context
     * @return true if the device is a tablet, false otherwise
     */
    public static boolean isTablet(@NonNull Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK) >= android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
