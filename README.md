<p align="center">
  <img src="assets/banner.png" alt="AdGlide Banner">
</p>

# AdGlide SDK 🚀
### *The Premium Mediation Wrapper for High-Performance Android Apps*

[![Version](https://img.shields.io/badge/Version-1.1.0-blue.svg)](https://github.com/partharoypc/AdGlide)
[![SDK Support](https://img.shields.io/badge/Android-21%2B-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**AdGlide** is an industrial-grade mediation powerhouse designed for professional Android developers. It eliminates the friction of multi-network integration, providing a **"Just Copy-Paste"** architecture that handles initialization, bidding, waterfalls, and pre-fetching out of the box.

---

## 🏗️ Core Infrastructure & Logic

AdGlide supports three distinct integration patterns to maximize your yield:

1.  **Direct Use**: Target a specific network exclusively.
2.  **Bidding Mediation**: Utilize real-time header bidding for supported networks (AdMob, AppLovin MAX, IronSource).
3.  **Sequential Waterfall**: A fail-safe `WaterfallManager` that cycles through unlimited backup arrays instantly if the primary fails to fill.
4.  **Intelligent Rate Limiting**: Built-in `AdMobRateLimiter` ensures failing AdMob units don't loop endlessly or trigger Google penalties on "No Fill" errors.

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
    implementation 'com.github.partharoypc:adglide:1.1.0'
    
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
AdGlide.init(this)
    .status(true) 
    .testMode(false)
    .network(AdGlideNetwork.ADMOB) // ❤️ Type-safe Enums 
    .backups(AdGlideNetwork.META, AdGlideNetwork.APPLOVIN, AdGlideNetwork.STARTAPP) // 🚀 Multi-network Waterfall
    .adMobId("ca-app-pub-3940256099942544~3347511713")
    .debug(true)
    .build();
```

---

## 📱 App Open Ads Mastery
AdGlide provides a highly optimized `AppOpenAd` manager that supports both manual triggers and auto-lifecycle monitoring.

### Manual Implementation
```java
new AppOpenAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.META, AdGlideNetwork.STARTAPP)
    .adMobId("YOUR_ID")
    .build()
    .load(new OnShowAdCompleteListener() {
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
appOpenAd.setLifecycleObserver() // Monitor app foregrounding
    .setActivityLifecycleCallbacks(activity); // Monitor activity states
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
    .adMobId("YOUR_ID")
    .collapsible(true) // AdMob High-CTR format
    .darkTheme(true) // Auto-styling for native-style banners
    .build()
    .load();
```

### 2. Interstitial Ads (With Frequency Capping)
Protect User Experience with built-in interval controls.

```java
new InterstitialAd.Builder(this)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.META, AdGlideNetwork.APPLOVIN, AdGlideNetwork.STARTAPP)
    .adMobId("YOUR_ID")
    .metaId("YOUR_ID")
    .interval(3) // Shows on every 3rd call to .show()
    .build()
    .load() // Load the ad
    .show(); // Show it (subject to interval check)
```

### 3. Native Ads (Fluid Templates)
AdGlide features a "Unified Native" system that maps complex layouts into simple templates.

**Supported Styles:** Use `AdGlideNativeStyle` enum (`STYLE_NEWS`, `STYLE_MEDIUM`, `STYLE_SMALL`, etc.)

```java
new NativeAd.Builder(this)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.META, AdGlideNetwork.STARTAPP)
    .adMobId("YOUR_ID")
    .style(AdGlideNativeStyle.STYLE_NEWS) // Type-safe Native Styles
    .backgroundColor("#FFFFFF", "#212121") // Light & Dark support
    .padding(10, 10, 10, 10)
    .margin(5, 5, 5, 5)
    .build()
    .load();
```

### 4. Rewarded & Rewarded Interstitial
Handle user rewards with full lifecycle callbacks.

```java
new RewardedAd.Builder(this)
    .adMobId("YOUR_ID")
    .build()
    .load()
    .show(new OnRewardedAdCompleteListener() {
        @Override
        public void onRewardedAdComplete() {
            // Grant item/coins here
        }
    }, new OnRewardedAdDismissedListener() {
        @Override
        public void onRewardedAdDismissed() {
            // Handle ad close
        }
    }, new OnRewardedAdErrorListener() {
        @Override
        public void onRewardedAdError() {
            // Handle load/show errors
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
new InterstitialAd.Builder(this).adMobId("YOUR_ID").build().load();
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
| **Common** | `.build()` | Finalizes configuration and returns the core instance. |
| **Common** | `.load()` | Initiates the electrical request to Fetch/Preload ads. |
| **Common** | `.status(boolean)` | Master toggle to enable/disable ads dynamically. |
| **Common** | `.network(AdGlideNetwork)` | Sets the primary ad network via type-safe enum. |
| **Common** | `.backups(AdGlideNetwork...)` | Variadic backups for robust WaterfallManager integration. |
| **Common** | `.[network]Id(String)` | Set IDs: `adMobId`, `metaId`, `appLovinId`, `startAppId`, `unityId`, `ironSourceId`, `wortiseId`. |
| **Common** | `.legacyGDPR(boolean)` | Toggles legacy GDPR consent flow for EU users. |
| **Banner** | `.collapsible(boolean)` | Native AdMob Collapsible Banner. |
| **Banner** | `.darkTheme(boolean)` | Enables dark UI for native banners. |
| **Interstitial**| `.interval(int)` | Controls frequency (e.g., 3 = 1 ad every 3 actions). |
| **Native** | `.style(AdGlideNativeStyle)` | Encapsulates native template structures. |
| **Native** | `.background(int)` | Custom drawable background resource ID. |
| **Native** | `.backgroundColor(String, String)`| Hex colors for Light & Dark mode native backgrounds. |
| **Native** | `.padding(l, t, r, b)` | Sets layout padding for Native ad containers. |
| **Native** | `.margin(l, t, r, b)` | Sets layout margins for Native ad containers. |
| **App Open** | `.setLifecycleObserver()`| Monitor app-wide start events. |

### Callback Listener Matrix
Listen to every event in the ad lifecycle:

1.  **`OnShowAdCompleteListener`**: `onShowAdComplete()`
2.  **`OnRewardedAdCompleteListener`**: `onUserEarnedReward()`
3.  **`OnRewardedAdDismissedListener`**: `onAdDismissed()`
4.  **`OnRewardedAdErrorListener`**: `onAdError()`
5.  **`OnInterstitialAdDismissedListener`**: `onDismissed()`
6.  **`OnInterstitialAdShowedListener`**: `onShowed()`

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
*AdGlide is MIT Licensed. © 2026 Partha Roy.*

