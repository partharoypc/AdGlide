package com.partharoypc.adglide.util;

import android.app.Activity;
import android.os.Build;
import com.partharoypc.adglide.util.AdGlideLog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
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
            android.net.Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                if (capabilities != null) {
                    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                           capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                           capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            }
        }
        AdGlideLog.i(TAG, "No internet connection available.");
        return false;
    }

    /**
     * Calculates the adaptive banner size for the current device width.
     *
     * @param activity the active activity
     * @return the adaptive banner width in DP
     */
    @SuppressWarnings("deprecation")
    public static int getAdaptiveBannerSize(@NonNull Activity activity) {
        float widthPixels;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            android.view.WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            widthPixels = windowMetrics.getBounds().width();
        } else {
            android.util.DisplayMetrics outMetrics = new android.util.DisplayMetrics();
            android.view.Display display = activity.getWindowManager().getDefaultDisplay();
            display.getMetrics(outMetrics);
            widthPixels = outMetrics.widthPixels;
        }

        float density = activity.getResources().getDisplayMetrics().density;
        return (int) (widthPixels / density);
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
