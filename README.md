![AdGlide Banner](assets/banner.png)

# AdGlide SDK 🚀
### *The Premium Mediation Wrapper for High-Performance Android Apps*

[![Version](https://img.shields.io/badge/Version-1.0.0-blue.svg)](https://github.com/partharoypc/AdGlide)
[![SDK Support](https://img.shields.io/badge/Android-21%2B-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**AdGlide** is an industrial-grade mediation powerhouse designed for professional Android developers. It eliminates the friction of multi-network integration, providing a **"Just Copy-Paste"** architecture that handles initialization, bidding, waterfalls, and pre-fetching out of the box.

---

## 🏗️ Core Infrastructure & Logic

AdGlide supports three distinct integration patterns to maximize your yield:

1.  **Direct Use**: Target a specific network exclusively.
2.  **Bidding Mediation**: Utilize real-time header bidding for supported networks (AdMob, AppLovin MAX, IronSource).
3.  **Sequential Waterfall**: A fail-safe manager that cycles through backup networks instantly if the primary fails to fill.

### 📊 Comprehensive Capability Matrix

| Ad Format | AdMob | Meta | Unity | AppLovin | IronSource | StartApp | Wortise |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Direct Use** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Bidding Support**| ✅ | ✅ | ❌ | ✅ | ✅ | ❌ | ❌ |
| **Sequential Waterfall** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Banner** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Interstitial** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Native** | ✅ | ✅ | ❌ | ✅ | ❌ | ✅ | ✅ |
| **Rewarded** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **App Open** | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ | ✅ |

---

## ⚡ 60-Second Quick Start

### 1. Configure Repositories (`settings.gradle`)
```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
        maven { url 'https://artifacts.applovin.com/android' }
        maven { url 'https://artifact.bytedance.com/repository/pangle' }
        maven { url 'https://maven.wortise.com/artifactory/public' }
        maven { url 'https://android-sdk.is.com/' }
    }
}
```

### 2. Add dependencies (`build.gradle`)
```gradle
dependencies {
    implementation 'com.github.partharoypc:adglide:1.0.0'
    
    // 🚀 SELECT YOUR NETWORKS:
    implementation 'com.google.android.gms:play-services-ads:23.6.0'
    implementation 'com.google.android.ump:user-messaging-platform:3.1.0'
    implementation 'com.facebook.android:audience-network-sdk:6.18.0'
    implementation 'com.applovin:applovin-sdk:13.0.1'
    implementation 'com.unity3d.ads:unity-ads:4.12.5'
    implementation 'com.ironsource.sdk:mediationsdk:8.4.0'
    implementation 'com.startapp:inapp-sdk:5.1.0'
    implementation 'com.wortise:android-sdk:1.7.0'
}
```

### 3. Global Initialization
```java
new AdNetwork.Initialize(this)
    .setAdStatus("1") 
    .setAdNetwork("admob") 
    .setBackupAdNetworks("meta", "applovin") 
    .setAdMobAppId("ca-app-pub-3940256099942544~3347511713")
    .setDebug(true)
    .build();
```

---

## 📱 App Open Ads Mastery
AdGlide provides a highly optimized `AppOpenAd` manager that supports both manual triggers and auto-lifecycle monitoring.

### Manual Implementation
```java
new AppOpenAd.Builder(this)
    .setAdStatus("1")
    .setAdNetwork("admob")
    .setAdMobAppOpenId("YOUR_ID")
    .build(new OnShowAdCompleteListener() {
        @Override
        public void onShowAdComplete() {
            // Proceed to next screen
        }
    });
```

### Auto-Lifecycle Monitoring
Register AdGlide to automatically show ads on app restarts and resumes:
```java
// In your Application class or Splash
appOpenAd.setPlacementStatus(true)
    .setOnStartLifecycleObserver() // Monitor app foregrounding
    .setOnStartActivityLifecycleCallbacks(activity); // Monitor activity states
```

---

## 📺 Universal Ad Formats (Deep Dive)

### 1. Banner & Medium Rectangle
Supports **Adaptive Sizing** and **Collapsible Banners**.

```xml
<include layout="@layout/adglide_view_banner_ad" />
```

```java
new BannerAd.Builder(this)
    .setAdMobBannerId("YOUR_ID")
    .setIsCollapsibleBanner(true) // AdMob High-CTR format
    .setDarkTheme(true) // Auto-styling for native-style banners
    .build();
```

### 2. Interstitial Ads (With Frequency Capping)
Protect User Experience with built-in interval controls.

```java
new InterstitialAd.Builder(this)
    .setAdMobInterstitialId("YOUR_ID")
    .setInterval(3) // Shows on every 3rd call to .show()
    .build()
    .show();
```

### 3. Native Ads (Fluid Templates)
AdGlide features a "Unified Native" system that maps complex layouts into simple templates.

**Supported Styles:** `news`, `medium`, `small`, `radio`, `stream`.

```java
new NativeAd.Builder(this)
    .setAdMobNativeId("YOUR_ID")
    .setNativeAdStyle("news") 
    .setNativeAdBackgroundColor("#FFFFFF", "#212121") // Light & Dark support
    .setPadding(10, 10, 10, 10)
    .setMargin(5, 5, 5, 5)
    .build();
```

### 4. Rewarded & Rewarded Interstitial
Handle user rewards with full lifecycle callbacks.

```java
new RewardedAd.Builder(this)
    .setAdMobRewardedId("YOUR_ID")
    .build(new OnRewardedAdCompleteListener() {
        @Override
        public void onRewardedAdComplete() {
            // Grant item/coins here
        }
    }, new OnRewardedAdDismissedListener() {
        @Override
        public void onRewardedAdDismissed() {
            // Handle ad close
        }
    });
```

---

## 🌐 Remote Configuration (Server JSON)
Toggle ads or change networks remotely using the `RemoteConfigHelper`. Use the following JSON schema on your server:

```json
{
  "ad_status": "1",
  "ad_network": "admob",
  "backup_ads": "meta,applovin,unity",
  "admob_banner_id": "YOUR_ID",
  "admob_interstitial_id": "YOUR_ID"
}
```

**Implementation:**
```java
new RemoteConfigHelper().fetchConfig("https://your-api.com/config.json", new RemoteConfigHelper.ConfigListener() {
    @Override
    public void onConfigFetched(JSONObject config) {
        // Parse and pass to AdNetwork.Initialize
    }
    @Override
    public void onConfigFailed(String error) {
        // Use local defaults
    }
});
```

---

## 🚀 Pro Performance Features

### ⚡ AdRepository (Pre-Fetching)
Eliminate "loading..." spinners by background caching ads.

```java
// Pre-load in Splash
AdRepository.getInstance().preloadInterstitial(context, "admob", "YOUR_ID");

// The builder will automatically use the cached ad
new InterstitialAd.Builder(this).setAdMobInterstitialId("YOUR_ID").build();
```

### 🛡️ Triple-Base64 Security
Bypass simple string analysis and APK scrapers by obfuscating your Ad IDs.

```java
String safeId = Tools.decode("TWpZNE5UYzVOekk1TkRRME5nPT0=");
```

---

## 📘 Comprehensive API Reference

### Builder Methods Reference

| Builder | Method | Description |
| :--- | :--- | :--- |
| **Banner** | `.setIsCollapsibleBanner(bool)` | Native AdMob Collapsible Banner. |
| **Banner** | `.setDarkTheme(bool)` | Enables dark UI for native banners. |
| **Interstitial**| `.setInterval(int)` | Controls frequency (e.g., 3 = 1 ad every 3 actions). |
| **Native** | `.setNativeAdStyle(String)` | `news`, `medium`, `small`, `radio`, `stream`. |
| **Native** | `.setBackgroundResource(int)` | Custom drawable background. |
| **App Open** | `.setOnStartLifecycleObserver()`| Monitor app-wide start events. |

### Callback Listener Matrix
Listen to every event in the ad lifecycle:

1.  **`OnShowAdCompleteListener`**: `onShowAdComplete()`
2.  **`OnRewardedAdCompleteListener`**: `onUserEarnedReward()`
3.  **`OnRewardedAdDismissedListener`**: `onAdDismissed()`
4.  **`OnRewardedAdErrorListener`**: `onAdError()`
5.  **`OnInterstitialAdDismissedListener`**: `onDismissed()`
6.  **`OnInterstitialAdShowedListener`**: `onShowed()`
7.  **`ConfigListener`**: `onConfigFetched(JSONObject)`, `onConfigFailed(String)`

---

## 🛡️ Production Hardening (R8/ProGuard)
Essential for ensuring the SDK correctly obfuscates and doesn't strip runtime components.

```proguard
-keep public class com.partharoypc.adglide.format.** { public *; }
-keep public class com.partharoypc.adglide.util.** { public *; }
-keep interface com.partharoypc.adglide.util.On*Listener { *; }
```

---

## 🎨 Advanced UI Components
- **RecyclerView Integration**: `NativeAdViewHolder.loadNativeAd()` - zero-lag ad injection.
- **Fragment Management**: `NativeAdFragment.Builder` - lifecycle-safe fragment ads.
- **Onboarding/Slides**: `NativeAdViewPager.Builder` - fluid ads for carousels.

---

## 🤝 Support & Community
Developed with ❤️ by **[Partha Roy](https://github.com/partharoypc)**.

For bugs, feature requests, or custom mediation integrations, please open an issue or contact the developer directly.

---
*AdGlide is MIT Licensed. © 2024 Partha Roy.*

