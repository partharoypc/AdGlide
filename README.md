# AdGlide SDK - The Ultimate Developer Guide 🚀

[![Version](https://img.shields.io/badge/Version-1.0.0-blue.svg)](https://github.com/partharoypc/AdGlide)
[![SDK Support](https://img.shields.io/badge/Android-21%2B-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-orange.svg)](LICENSE)

AdGlide is an all-in-one, **Super Developer Friendly** Android library designed to master ad integration across multiple networks with minimal code. Unified API, Robust Waterfall, and Production-Ready.

---

## 📖 Table of Contents
- [✨ Features](#-features)
- [📦 Installation & Setup](#-installation--setup)
- [🛠 Comprehensive Manifest Guide](#-comprehensive-manifest-guide)
- [🚀 Global Initialization](#-global-initialization)
- [📺 Ad Format Implementations](#-ad-format-implementations)
    - [Banner Ads](#1-banner-ads)
    - [Interstitial Ads](#2-interstitial-ads)
    - [Native Ads](#3-native-ads)
    - [Rewarded Ads](#4-rewarded-ads)
    - [App Open Ads](#5-app-open-ads)
- [🌊 Advanced Waterfall Strategy](#-advanced-waterfall-strategy)
- [🛡 GDPR & Privacy Compliance](#-gdpr--privacy-compliance)
- [🛡 ProGuard / R8 Rules](#-proguard--r8-rules)
- [🧪 Testing & Ad Units](#-testing--ad-units)
- [⚠️ Network Specific Notes](#️-network-specific-notes)

---

## ✨ Features

- **Unified Mediation**: AdMob, Meta (FAN), Unity, AppLovin (MAX & Discovery), IronSource, StartApp, and Wortise.
- **Smart Waterfall**: Dynamic fallback to unlimited backup networks.
- **Display Intervals**: Control ad frequency for Interstitials.
- **Collapsible Banners**: Support for AdMob collapsible banner requests.
- **Native Ad Styles**: Multiple pre-built templates (`small`, `medium`, `radio`, `news`, `video`, etc.).
- **Theme Support**: Built-in Dark/Light theme switching for ad views.
- **Zero Configuration GDPR**: Automated Google UMP integration.

---

## 📦 Installation & Setup

### 1. Root Repositories
Add these to your `settings.gradle` or project-level `build.gradle`:

```gradle
repositories {
    google()
    mavenCentral()
    maven { url 'https://jitpack.io' }
    maven { url "https://artifact.bytedance.com/repository/pangle" }
    maven { url "https://android-sdk.is.com/" }
}
```

### 2. Dependency
```gradle
dependencies {
    implementation 'com.github.partharoypc:AdGlide:1.0.0'
}
```

---

## 🛠 Comprehensive Manifest Guide

Different networks require specific tags. Add these as needed to your `AndroidManifest.xml`:

### Core Requirements
```xml
<!-- AdMob App ID -->
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-xxxxxxxxxxxxxxxx~xxxxxxxxxx"/>

<!-- AppLovin SDK Key -->
<meta-data
    android:name="applovin.sdk.key"
    android:value="YOUR_APPLOVIN_SDK_KEY"/>
```

### Optional Performance Flags
```xml
<meta-data
    android:name="com.google.android.gms.ads.flag.OPTIMIZE_INITIALIZATION"
    android:value="true" />
<meta-data
    android:name="com.google.android.gms.ads.flag.OPTIMIZE_AD_LOADING"
    android:value="true" />
```

---

## 🚀 Global Initialization

Initialize all SDKs at app launch (e.g., in `MainActivity` or `SplashActivity`).

```java
new AdNetwork.Initialize(this)
    .setAdStatus("1")           // "1" = ON, "0" = OFF
    .setAdNetwork("admob")      // Options: admob, meta, unity, applovin, ironsource, wortise, startapp
    .setBackupAdNetwork("meta") // Legacy single backup
    .setBackupAdNetworks("meta", "applovin", "unity") // Modern multi-backup waterfall
    .setAdMobAppId(null)        // Usually handled by Manifest
    .setStartappAppId("ID")
    .setUnityGameId("ID")
    .setAppLovinSdkKey("KEY")
    .setironSourceAppKey("KEY")
    .setWortiseAppId("ID")
    .setDebug(BuildConfig.DEBUG) // Enables logging and test modes
    .build();
```

---

## 📺 Ad Format Implementations

### 1. Banner Ads
Requires a container and layout inflation.

**Java:**
```java
LinearLayout bannerContainer = findViewById(R.id.banner_container);
View bannerAdView = getLayoutInflater().inflate(com.partharoypc.adglide.R.layout.adglide_view_banner_ad, bannerContainer, false);
bannerContainer.addView(bannerAdView);

new BannerAd.Builder(this)
    .setAdStatus("1")
    .setAdNetwork("admob")
    .setBackupAdNetwork("meta")
    .setAdMobBannerId("ID")
    .setMetaBannerId("ID")
    .setUnityBannerId("ID")
    .setAppLovinBannerId("ID")
    .setironSourceBannerId("ID")
    .setWortiseBannerId("ID")
    .setDarkTheme(false)
    .setIsCollapsibleBanner(true) // AdMob Collapsible support
    .build();
```

### 2. Interstitial Ads
Includes frequency control via `setInterval`.

```java
InterstitialAd.Builder interstitial = new InterstitialAd.Builder(this)
    .setAdStatus("1")
    .setAdNetwork("admob")
    .setAdMobInterstitialId("ID")
    .setInterval(3) // Only shows every 3rd time showInterstitialAd() is called
    .build();

// Trigger display
interstitial.showInterstitialAd();
```

### 3. Native Ads
Supports extreme customization and pre-built styles.

**Styles:** `small`, `medium`, `radio`, `news`, `video_small`, `video_large`, `stream`.

```java
LinearLayout nativeContainer = findViewById(R.id.native_container);
// Inflate matching style layout
View nativeView = getLayoutInflater().inflate(com.partharoypc.adglide.R.layout.adglide_view_native_ad_medium, nativeContainer, false);
nativeContainer.addView(nativeView);

new NativeAd.Builder(this)
    .setAdStatus("1")
    .setAdNetwork("admob")
    .setNativeAdStyle("medium")
    .setAdMobNativeId("ID")
    .setPadding(10, 10, 10, 10)
    .setMargin(5, 5, 5, 5)
    .setDarkTheme(true)
    .build();
```

### 4. Rewarded Ads
Securely handle user rewards.

```java
new RewardedAd.Builder(this)
    .setAdStatus("1")
    .setAdNetwork("admob")
    .setAdMobRewardedId("ID")
    .build(new OnRewardedAdCompleteListener() {
        @Override
        public void onRewardedAdComplete() {
            // Reward user here
        }
    }, new OnRewardedAdDismissedListener() {
        @Override
        public void onRewardedAdDismissed() {
            // Ad closed
        }
    });
```

### 5. App Open Ads
Two ways to implement:

**A. Simple Show (Activity Level):**
```java
new AppOpenAd.Builder(this)
    .setAdStatus("1")
    .setAdNetwork("admob")
    .setAdMobAppOpenId("ID")
    .build();
```

**B. Full Lifecycle (Application Level):**
Register in your `Application` class using `ProcessLifecycleOwner` (see Demo app for snippet).

---

## 🌊 Advanced Waterfall Strategy

The library uses `WaterfallManager` internally. You can specify an ordered list of networks to try:

```java
.setBackupAdNetworks("admob", "meta", "applovin", "unity")
```
AdGlide will try each network in order until an ad is successfully loaded.

---

## 🛡 GDPR & Privacy Compliance

One-line integration for European user consent requirements.

```java
GDPR gdpr = new GDPR(this);

// Standard Load
gdpr.updateGDPRConsentStatus(); 

// Advanced: With Debug & Child-Directed flags
gdpr.updateGDPRConsentStatus("admob", BuildConfig.DEBUG, false);

// Reset consent (for testing)
gdpr.resetConsent();
```

---

## 🛡 ProGuard / R8 Rules

If you use shrinking/obfuscation, these rules are already included in the library (`consumer-rules.pro`), but you can verify them:

```pro
-keep public class com.partharoypc.adglide.format.** { public *; }
-keep interface com.partharoypc.adglide.util.On*Listener { *; }
-keep class com.google.android.gms.ads.** { *; }
-keep class com.facebook.ads.** { *; }
```

---

## 🧪 Testing & Ad Units

### AdMob Test IDs
| Format | Test Unit ID |
| :--- | :--- |
| Banner | `ca-app-pub-3940256099942544/6300978111` |
| Interstitial | `ca-app-pub-3940256099942544/1033173712` |
| Native | `ca-app-pub-3940256099942544/2247696110` |
| Rewarded | `ca-app-pub-3940256099942544/5224354917` |
| App Open | `ca-app-pub-3940256099942544/3419835294` |

---

## ⚠️ Network Specific Notes

### IronSource Lifecycle
Developers must manually call IronSource lifecycle methods in their `MainActivity`:
```java
@Override
protected void onResume() {
    super.onResume();
    IronSource.onResume(this);
}

@Override
protected void onPause() {
    super.onPause();
    IronSource.onPause(this);
}
```

### Meta (FAN) Initialization
AdGlide uses `AudienceNetworkInitializeHelper` to ensure thread-safe initialization of Meta Audience Network.

---

## 🤝 Credits & License

Engineered by [Partha Roy](https://github.com/partharoypc). Distributed under the MIT License.
