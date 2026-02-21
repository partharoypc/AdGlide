package com.partharoypc.adglide.gdpr;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_DISCOVERY;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;

import static com.partharoypc.adglide.util.Constant.STARTAPP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles Google User Messaging Platform (UMP) consent flow for GDPR
 * compliance.
 * Supports debug mode with test device IDs and child-directed consent
 * parameters.
 */
public class GDPR {
    private static final String TAG = "AdGlide";

    private ConsentInformation consentInformation;
    private ConsentDebugSettings debugSettings;
    private ConsentRequestParameters params;
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private ConsentForm consentForm;
    private final Activity activity;

    public GDPR(Activity activity) {
        this.activity = activity;
    }

    public void updateGDPRConsentStatus() {
        ConsentRequestParameters params = new ConsentRequestParameters.Builder().build();
        consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        consentInformation.requestConsentInfoUpdate(activity, params, () -> {
            if (consentInformation.isConsentFormAvailable()) {
                loadForm(activity);
            }
        },
                formError -> {
                });
        Log.d(TAG, "AdMob GDPR is selected");
    }

    @SuppressLint("HardwareIds")
    public void updateGDPRConsentStatus(String adType, boolean isDebug, boolean childDirected) {
        switch (adType) {
            case ADMOB:
                if (isDebug) {
                    String androidId = Settings.Secure.getString(activity.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    String deviceId = md5(androidId).toUpperCase(java.util.Locale.ROOT);
                    debugSettings = new ConsentDebugSettings.Builder(activity)
                            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                            .addTestDeviceHashedId(deviceId)
                            .build();
                    params = new ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings)
                            .setTagForUnderAgeOfConsent(childDirected).build();
                } else {
                    params = new ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(childDirected).build();
                }
                consentInformation = UserMessagingPlatform.getConsentInformation(activity);
                consentInformation.requestConsentInfoUpdate(activity, params,
                        () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                                activity, loadAndShowError -> {
                                    if (consentInformation.canRequestAds()) {
                                        initializeMobileAdsSdk();
                                    }
                                }),
                        requestConsentError -> {
                        });
                if (consentInformation.canRequestAds()) {
                    initializeMobileAdsSdk();
                }
                break;
            case STARTAPP:
            case APPLOVIN_MAX:
            case APPLOVIN_DISCOVERY:
                break;
        }
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }
        MobileAds.initialize(activity);
    }

    public void loadForm(Activity activity) {
        UserMessagingPlatform.loadConsentForm(activity, consentForm -> {
            this.consentForm = consentForm;
            if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                consentForm.show(activity, formError -> {
                    loadForm(activity);
                });
            }
        },
                formError -> {
                });
    }

    public static String md5(final String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            // Logger.logStackTrace(TAG,e);
        }
        return "";
    }

    public void resetConsent() {
        consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        consentInformation.reset();
        updateGDPRConsentStatus();
    }

    public void showPrivacyOptionsForm() {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, formError -> {
            if (formError != null) {
                Log.d(TAG, "showPrivacyOptionsForm error: " + formError.getMessage());
            }
        });
    }

}
