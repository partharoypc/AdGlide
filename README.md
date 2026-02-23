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
    .testMode(false) // Set to true to receive test ads
    .debug(true) // Enables verbose logging
    .network(AdGlideNetwork.ADMOB) // Primary Network via Type-safe Enum
    .backups(AdGlideNetwork.META, AdGlideNetwork.APPLOVIN, AdGlideNetwork.STARTAPP) // Multi-network Waterfall Fallback
    // Supply SDK IDs for your chosen networks:
    .adMobId("ca-app-pub-3940256099942544~3347511713")
    .startAppId("YOUR_STARTAPP_ID")
    .appLovinId("YOUR_APPLOVIN_SDK_KEY")
    .ironSourceId("YOUR_IRONSOURCE_APP_KEY")
    .unityId("YOUR_UNITY_GAME_ID")
    .wortiseId("YOUR_WORTISE_APP_ID")
    .build();
```

---

## 📺 Universal Ad Formats & Guidelines

AdGlide implements a flexible "Builder" pattern across all ad formats, giving you identical methods for fallback and configuration logic. 

**Core Global Settings Available on ALL Builders:**
*   `.status(boolean)`: Turn ad strictly ON or OFF.
*   `.placement(int)`: Highly useful for toggling specific ad placements (e.g., 0 for OFF, 1 for ON). Acts as a secondary status switch configurable via Remote Config.
*   `.network(AdGlideNetwork)`: Set Primary Network.
*   `.backup(AdGlideNetwork)`: Set a single fallback network.
*   `.backups(AdGlideNetwork...)`: Set multiple sequential fallback networks.
*   `.legacyGDPR(boolean)`: Enable legacy GDPR prompt for EU users (AdMob).

---

### 1. App Open Ads Mastery
Optimized `AppOpenAd` manager supporting AdMob, AppLovin MAX, and Wortise. Includes Manual triggers and Auto-Lifecycle monitoring.

**Manual Implementation:**
```java
new AppOpenAd.Builder(this)
    .status(true)
    .placement(1)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.APPLOVIN_MAX, AdGlideNetwork.WORTISE)
    .adMobId("YOUR_ADMOB_AD_UNIT_ID")
    .appLovinId("YOUR_APPLOVIN_AD_UNIT_ID")
    .wortiseId("YOUR_WORTISE_AD_UNIT_ID")
    .build()
    .load(new OnShowAdCompleteListener() {
        @Override
        public void onShowAdComplete() {
            // Ad dismissed or failed to load, proceed to next screen
        }
    }); // Automatically tries to load and show
```

**Auto-Lifecycle Monitoring:**
Register AdGlide to automatically show ads on app restarts and resumes (in Application class or Splash):
```java
AppOpenAd appOpenAd = new AppOpenAd()
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .adMobId("YOUR_ADMOB_AD_UNIT_ID");

appOpenAd.setLifecycleObserver() // Monitor app foregrounding
         .setActivityLifecycleCallbacks(activity); // Monitor activity states
```

---

### 2. Banner & Medium Rectangle Ads
Supports **Adaptive Sizing** and **Collapsible Banners**. Provide a specific Ad container layout.

```xml
<FrameLayout
    android:id="@+id/ad_mob_banner_view_container"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

```java
new BannerAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.META, AdGlideNetwork.STARTAPP)
    .adMobId("YOUR_ADMOB_AD_UNIT_ID")
    .metaId("YOUR_META_PLACEMENT_ID")
    .collapsible(true) // Highlights AdMob High-CTR format
    .darkTheme(true)   // Auto-styling for native-style fallback banners
    .build()
    .load();
```

**Medium Rectangle (300x250) Implementation:**
Using the same layout containers, simply swap `BannerAd` with `MediumRectangleAd`:

```java
new MediumRectangleAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.META)
    .adMobId("YOUR_ADMOB_MREC_ID")
    .metaId("YOUR_META_MREC_ID")
    .build()
    .load();
```

---

### 3. Interstitial Ads (With Frequency Capping)
Protect User Experience with built-in interval frequency controls safely. AdGlide handles ad loads and ensures pre-fetching logic works efficiently.

```java
new InterstitialAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.META, AdGlideNetwork.APPLOVIN, AdGlideNetwork.STARTAPP)
    .adMobId("YOUR_ADMOB_AD_UNIT_ID")
    .metaId("YOUR_META_PLACEMENT_ID")
    .appLovinId("YOUR_APPLOVIN_AD_UNIT_ID")
    .interval(3) // Ad will only trigger on every 3rd call to .show()
    .build()
    .load(new OnInterstitialAdDismissedListener() {
        @Override
        public void onInterstitialAdDismissed() {
            // Ad was shown and currently hidden. Proceed logically.
        }
    }) 
    .show(this); // Pass explicitly targeted activity or utilize internal context
```

---

### 4. Native Ads (Fluid Templates)
AdGlide features a highly modular "Unified Native" system mapping complex ad layouts into fluid UI templates configurable by simple constants.

**Supported Styles:** Use `AdGlideNativeStyle` enum (`STYLE_NEWS`, `STYLE_MEDIUM`, `STYLE_SMALL`, `STYLE_VIDEO_SMALL`, `STYLE_VIDEO_LARGE` etc.)

```java
new NativeAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.META, AdGlideNetwork.STARTAPP)
    .adMobId("YOUR_ADMOB_AD_UNIT_ID")
    .metaId("YOUR_META_PLACEMENT_ID")
    .style(AdGlideNativeStyle.STYLE_NEWS) // Inject structural style formatting
    .backgroundColor(R.color.white, R.color.black) // Light & Dark resource IDs
    .darkTheme(true) // Enforces dark theme variants strictly
    .padding(10, 10, 10, 10)
    .margin(5, 5, 5, 5)
    .background(R.drawable.custom_ad_bg) // Custom drawable contouring
    .build()
    .load();
```

---

### 5. Rewarded & Rewarded Interstitial Ads
Manage valuable user rewards efficiently with broad-acting lifecycle hook callbacks out of the box.

```java
new RewardedAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .adMobId("YOUR_ADMOB_AD_UNIT_ID")
    .build()
    .load(new OnRewardedAdCompleteListener() {
        @Override
        public void onRewardedAdComplete() {
            // Success! Grant item/coins here
        }
    }, new OnRewardedAdDismissedListener() {
        @Override
        public void onRewardedAdDismissed() {
            // Handle when the ad user navigates back
        }
    });

// Separate call to trigger visibility when required
rewardedAdBuilder.show(
    new OnRewardedAdCompleteListener() { ... },
    new OnRewardedAdDismissedListener() { ... },
    new OnRewardedAdErrorListener() {
        @Override
        public void onRewardedAdError() {
            // Handle logical flow if ad breaks or prevents reward payload
        }
    }
);
```

---

## 🚀 Pro Performance Features

### ⚡ AdRepository (Pre-Fetching)
Eliminate "loading..." spinners by background caching ads asynchronously. Ensure the same identifier mapping across calls.

```java
// Pre-load in Splash Activity logically
AdRepository.getInstance().preloadInterstitial(context, AdGlideNetwork.ADMOB.getValue(), "YOUR_ADMOB_AD_UNIT_ID");

// Any subsequent Builders globally initialized using the same ID fetches directly from AdRepository
new InterstitialAd.Builder(this)
    .network(AdGlideNetwork.ADMOB)
    .adMobId("YOUR_ADMOB_AD_UNIT_ID")
    .build()
    .load();

// Prevent memory leaks when destroying your Activity/App
AdRepository.getInstance().clearCache();
```

### 🛡️ Triple-Base64 Security
Bypass simple string analysis and APK scrapers by intentionally obfuscating your Ad IDs locally inside strings dynamically decrypted.

```java
String safeId = Tools.decode("TWpZNE5UYzVOekk1TkRRME5nPT0=");
```

---

## 📘 Comprehensive Callback Listener Matrix
AdGlide isolates callbacks across formats clearly bridging logical interfaces per module:

1.  **`OnShowAdCompleteListener`**: Hooked heavily in `AppOpenAd`.
2.  **`OnRewardedAdCompleteListener`**: Yields exact payload access.
3.  **`OnRewardedAdDismissedListener`**: Tracks completion/closing behaviors safely.
4.  **`OnRewardedAdErrorListener`**: Safely encapsulates failures strictly out of view.
5.  **`OnInterstitialAdDismissedListener`**: Executed post-display interaction termination.
6.  **`OnInterstitialAdShowedListener`**: Fires on explicitly rendering pixel data successfully.
7.  **`OnRewardedAdLoadedListener`**: Monitors explicit caching logic cleanly.

---

## 🛡️ Production Hardening (R8/ProGuard)
Ensure the SDK and components don't face unmanaged R8 aggressive obfuscation. Add explicitly:

```proguard
-keep public class com.partharoypc.adglide.format.** { public *; }
-keep public class com.partharoypc.adglide.util.** { public *; }
-keep interface com.partharoypc.adglide.util.On*Listener { *; }
```

---

## 🤝 Support & Community
Developed with ❤️ by **[Partha Roy](https://github.com/partharoypc)**.

For bugs, feature requests, or custom mediation integrations, please open an issue or contact the developer directly.

---
*AdGlide is MIT Licensed. © 2026 Partha Roy.*
