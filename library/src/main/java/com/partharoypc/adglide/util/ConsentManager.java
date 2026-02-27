package com.partharoypc.adglide.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles Google User Messaging Platform (UMP) SDK for GDPR/CCPA compliance.
 */
public class ConsentManager {
    private static final String TAG = "AdGlide.Consent";
    private final ConsentInformation consentInformation;
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);

    public interface OnConsentLoadingCompleteListener {
        void onConsentLoadingComplete();
    }

    public ConsentManager(Context context) {
        this.consentInformation = UserMessagingPlatform.getConsentInformation(context);
    }

    /**
     * Request consent information from Google and show the form if required.
     */
    public void requestConsent(@NonNull Activity activity, boolean isDebug,
            OnConsentLoadingCompleteListener onConsentLoadingCompleteListener) {

        ConsentRequestParameters.Builder paramsBuilder = new ConsentRequestParameters.Builder();

        if (isDebug) {
            ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(activity)
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .build();
            paramsBuilder.setConsentDebugSettings(debugSettings);
        }

        ConsentRequestParameters params = paramsBuilder.build();

        consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                        activity,
                        loadAndShowError -> {
                            if (loadAndShowError != null) {
                                Log.w(TAG, String.format("%s: %s",
                                        loadAndShowError.getErrorCode(),
                                        loadAndShowError.getMessage()));
                            }

                            // Consent has been gathered or isn't required.
                            if (consentInformation.canRequestAds()) {
                                onConsentLoadingCompleteListener.onConsentLoadingComplete();
                            }
                        }),
                requestError -> {
                    if (requestError != null) {
                        Log.w(TAG, String.format("%s: %s",
                                requestError.getErrorCode(),
                                requestError.getMessage()));
                    }
                    // Even on error, we might be able to request ads if consent was previously
                    // given
                    if (consentInformation.canRequestAds()) {
                        onConsentLoadingCompleteListener.onConsentLoadingComplete();
                    } else {
                        // Fallback to calling complete anyway to not block the app
                        onConsentLoadingCompleteListener.onConsentLoadingComplete();
                    }
                });
    }

    public boolean canRequestAds() {
        return consentInformation.canRequestAds();
    }

    public boolean isPrivacyOptionsRequired() {
        return consentInformation
                .getPrivacyOptionsRequirementStatus() == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED;
    }

    public void showPrivacyOptionsForm(Activity activity,
            ConsentForm.OnConsentFormDismissedListener onConsentFormDismissedListener) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener);
    }
}
