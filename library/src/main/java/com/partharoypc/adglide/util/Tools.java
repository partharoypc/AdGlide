package com.partharoypc.adglide.util;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;

/**
 * Utility class providing helper methods for ad request creation,
 * adaptive banner sizing, and network checks.
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
