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

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.partharoypc.adglide.gdpr.LegacyGDPR;

import java.nio.charset.StandardCharsets;

/**
 * Utility class providing helper methods for ad request creation,
 * adaptive banner sizing, and string decoding operations.
 */
public class Tools {

    private static final String TAG = "AdNetwork";

    /**
     * Calculates the adaptive banner ad size based on the device screen width.
     *
     * @param activity the current Activity used to determine screen dimensions
     * @return the adaptive banner {@link AdSize} for the current orientation
     */
    @NonNull
    @SuppressWarnings("deprecation")
    public static AdSize getAdSize(@NonNull Activity activity) {
        int adWidth;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            float widthPixels = windowMetrics.getBounds().width();
            float density = activity.getResources().getDisplayMetrics().density;
            adWidth = (int) (widthPixels / density);
        } else {
            DisplayMetrics outMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
            float widthPixels = outMetrics.widthPixels;
            float density = outMetrics.density;
            adWidth = (int) (widthPixels / density);
        }
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    /**
     * Returns the medium rectangle (300x250) ad size.
     *
     * @return the {@link AdSize#MEDIUM_RECTANGLE} constant
     */
    @NonNull
    public static AdSize getAdSizeMREC() {
        return AdSize.MEDIUM_RECTANGLE;
    }

    /**
     * Builds an {@link AdRequest} with optional legacy GDPR consent bundle.
     *
     * @param activity   the current Activity
     * @param legacyGDPR whether to attach legacy GDPR consent extras
     * @return a configured {@link AdRequest}
     */
    @NonNull
    public static AdRequest getAdRequest(@NonNull Activity activity, boolean legacyGDPR) {
        if (legacyGDPR) {
            return new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, LegacyGDPR.getBundleAd(activity))
                    .build();
        } else {
            return new AdRequest.Builder().build();
        }
    }

    /**
     * Builds an {@link AdRequest}, optionally configured for collapsible banners.
     *
     * @param isCollapsibleBanner whether to enable collapsible banner mode
     * @return a configured {@link AdRequest}
     */
    @NonNull
    public static AdRequest getAdRequest(boolean isCollapsibleBanner) {
        if (isCollapsibleBanner) {
            Log.d(TAG, "Loading collapsible banner");
            Bundle extras = new Bundle();
            extras.putString("collapsible", "bottom");
            return new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();
        } else {
            Log.d(TAG, "Loading default banner");
            return new AdRequest.Builder().build();
        }
    }

    /**
     * Builds an {@link AdManagerAdRequest} for Google Ad Manager.
     *
     * @return a configured {@link AdManagerAdRequest}
     */
    @NonNull
    @SuppressLint("VisibleForTests")
    public static AdManagerAdRequest getGoogleAdManagerRequest() {
        return new AdManagerAdRequest.Builder().build();
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

}
