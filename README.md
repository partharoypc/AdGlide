<p align="center">
  <img src="assets/banner.png" alt="AdGlide SDK Banner" width="800">
</p>

# AdGlide SDK 🚀

### *The Premium Mediation Wrapper for High-Performance Android Apps*

[![Version](https://img.shields.io/badge/Version-1.9.0-blue.svg)](https://github.com/partharoypc/AdGlide)
[![Android](https://img.shields.io/badge/Android-23%2B-green.svg)](https://developer.android.com)
[![Compile SDK](https://img.shields.io/badge/Compile_SDK-36-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/projects/jdk/17/)
[![License](https://img.shields.io/badge/License-Apache_2.0-yellow.svg)](LICENSE)

**AdGlide** is an industrial-grade ad mediation SDK for professional Android developers. It eliminates the friction of multi-network integration with a **"Just Copy-Paste"** architecture that handles initialization, waterfall orchestration, rate limiting, pre-fetching, and crash protection out of the box across **8 ad networks**.

---

## ✨ What's New in v1.9.0 (Super Perfect Edition)

- **📱 "Super Perfect" App Open Ads** — New rotation-proof lifecycle management. Ads only trigger on true app-resume, never during screen rotations.
- **💎 100% Lint-Free Quality** — Industrial-grade codebase with zero warnings (Locale/I18n fixed) for maximum stability and performance.
- **🛡️ Refined Zero-Config ProGuard** — Embedded `consumer-rules.pro` now features even tighter, more secure scopes for all 8+ mediation networks.
- **⚡ "Zero-Wait" Pre-loading** — Optimized pooling engine with immediate background refills for instant ad delivery.
- **🔄 Dynamic Versioning** — SDK logs and performance metrics now automatically sync with your Gradle versioning.
- **🏠 Expanded House Ads** — Native House Ads now support full click-tracking and optimized video templates.

---

## 🏗️ 1. Core Architecture

AdGlide supports four integration patterns:

1. **Direct Use** — Target a single ad network exclusively.
2. **Bidding Mediation** — Real-time header bidding (Meta ↔ AdMob, Meta ↔ AppLovin, Meta ↔ IronSource).
3. **Sequential Waterfall** — `WaterfallManager` rotates through backup networks on fill failure for near-100% fill rates.
4. **Intelligent Rate Limiting** — Built-in exponential backoff on `AdMob Error 3`, protecting your account health automatically.

### 📊 Network × Format Support Matrix

| Ad Format | AdMob | Meta | AppLovin | Wortise | IronSource | Unity | StartApp | HouseAd |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Banner** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Interstitial** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Rewarded** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Rewarded Interstitial** | ✅ | ❌ | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ |
| **Native** | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ | ✅ |
| **App Open** | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ | ✅ |
| **Bidding** | ✅ | ✅ | ✅ | ❌ | ✅ | ❌ | ❌ | ❌ |
| **Waterfall** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

## ⚡ 2. Step-by-Step Setup Guide

Integrate AdGlide in under 5 minutes.

### Step 1: Configure Repositories

Add the following to your **`settings.gradle`** inside the `dependencyResolutionManagement` block.

> [!TIP]
> Only include repositories for the networks you actually use.

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // 🌟 Required for AdGlide
        maven { url 'https://jitpack.io' }

        // Ad Network Repositories (add only what you need)
        maven { url 'https://artifacts.applovin.com/android' }         // AppLovin
        maven { url 'https://maven.wortise.com/artifactory/public' }   // Wortise
        maven { url 'https://android-sdk.is.com/' }                    // IronSource
        maven { url 'https://artifact.bytedance.com/repository/pangle' } // Pangle/Wortise
        maven { url 'https://cboost.jfrog.io/artifactory/chartboost-ads/' } // Chartboost
    }
}
```

---

### Step 2: Add Dependencies

Open your **app-level `build.gradle`** and add the AdGlide core plus only the network SDKs you need.

```gradle
dependencies {
    // 🚀 AdGlide Core (Required)
    implementation 'com.github.partharoypc:adglide:1.9.0'

    // ─── Choose Your Networks ───────────────────────────────────────
    implementation 'com.google.android.gms:play-services-ads:25.0.0'       // AdMob ✅
    // implementation 'com.facebook.android:audience-network-sdk:6.21.0'   // Meta
    // implementation 'com.applovin:applovin-sdk:13.6.1'                   // AppLovin
    // implementation 'com.startapp:inapp-sdk:5.3.0'                       // StartApp
    // implementation 'com.wortise:android-sdk:1.7.2'                      // Wortise
    // implementation 'com.unity3d.ads-mediation:mediation-sdk:9.3.0'      // IronSource (LevelPlay)
    // implementation 'com.unity3d.ads:unity-ads:4.17.0'                   // Unity Ads
}
```

---

### Step 3: Configure AndroidManifest.xml

> [!CAUTION]
> Missing the AdMob App ID will crash the app on launch immediately.

```xml
<application ...>
    <!-- AdMob App ID (Required if using AdMob) -->
    <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX"/>

    <!-- AppLovin SDK Key (Required if using AppLovin) -->
    <meta-data
        android:name="applovin.sdk.key"
        android:value="YOUR_APPLOVIN_SDK_KEY"/>
</application>
```

---

### Step 4: Initialize in Your Application Class

Initialize AdGlide once in your `Application.onCreate()`. Use `AdGlideNetwork` enums for a more modern, type-safe configuration.

```java
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;
import com.partharoypc.adglide.AdGlideNetwork;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AdGlideConfig config = new AdGlideConfig.Builder()
            // ─── Global Settings ─────────────────────────────────────────
            .enableAds(true)                         // Master on/off switch
            .testMode(true)                          // 🛡️ NEW: Centralized one-flag diagnostics
            // .debug(true)                          // (Optional) TestMode already enables this
            // .enableDebugHUD(true)                 // (Optional) TestMode already enables this

            // ─── Waterfall Strategy (Type-Safe Enum) ─────────────────────
            .primaryNetwork(AdGlideNetwork.ADMOB)
            .backupNetworks(AdGlideNetwork.META, AdGlideNetwork.UNITY, AdGlideNetwork.IRONSOURCE)

            // ─── Format Toggles ──────────────────────────────────────────
            .bannerEnabled(true)
            .interstitialEnabled(true)
            .rewardedEnabled(true)
            .nativeEnabled(true)
            .appOpenEnabled(true)
            .rewardedInterstitialEnabled(false)

            // ─── Smart Loading ───────────────────────────────────────────
            .autoLoadInterstitial(true)              // Prefetch after each show
            .autoLoadRewarded(true)                  // Prefetch after each show
            .appOpenCooldown(15)                     // ⏱️ NEW: Wait 15 mins between app-resume ads
            .interstitialInterval(2)                 // Show every N clicks (0 = always)
            .rewardedInterval(1)                     // Show every N clicks (0 = always)

            // ─── GDPR / Privacy ─────────────────────────────────────────
            .enableGDPR(true)
            .debugGDPR(false)

            // ─── App Open Exclusions ─────────────────────────────────────
            .excludeOpenAdFrom(SplashActivity.class, PaymentActivity.class)

            // ─── AdMob ───────────────────────────────────────────────────
            .adMobBannerId("ca-app-pub-XXXXXXXX~XXXXXXXX")
            .adMobInterstitialId("ca-app-pub-XXXXXXXX~XXXXXXXX")
            .adMobRewardedId("ca-app-pub-XXXXXXXX~XXXXXXXX")
            .adMobRewardedIntId("ca-app-pub-XXXXXXXX~XXXXXXXX")
            .adMobNativeId("ca-app-pub-XXXXXXXX~XXXXXXXX")
            .adMobAppOpenId("ca-app-pub-XXXXXXXX~XXXXXXXX")

            // ─── Bidding & Mediation (New in v2.7.0) ─────────────────────
            .unityRewardedIntId("unity_rewarded_int")
            .ironSourceRewardedIntId("iron_rewarded_int")
            .ironSourceAppOpenId("iron_app_open")
            .startAppAppOpenId("startapp_app_open")

            // ─── House Ads (Offline Failover) ───────────────────────────
            .houseAdEnabled(true)
            .houseAdBannerImage("https://yourcdn.com/promo_banner.jpg")
            .houseAdBannerClickUrl("https://play.google.com/store/apps/details?id=your.app")
            .houseAdInterstitialImage("https://yourcdn.com/promo_full.jpg")
            .houseAdInterstitialClickUrl("https://play.google.com/store/apps/details?id=your.app")
            
            // 🆕 Native House Ad 
            .houseAdNativeTitle("Check Our Other App!")
            .houseAdNativeDescription("Experience the best productivity today.")
            .houseAdNativeCTA("Download Now")
            .houseAdNativeImage("https://yourcdn.com/promo_native_large.jpg")
            .houseAdNativeIcon("https://yourcdn.com/promo_icon.png")
            .houseAdNativeClickUrl("https://play.google.com/store/apps/details?id=your.app")

            .build();

        AdGlide.initialize(this, config);
    }
}
```

---

### Step 5: Register Application Class

```xml
<application
    android:name=".MyApplication"
    ...>
</application>
```

> [!IMPORTANT]
> **Do NOT miss** `android:name=".MyApplication"` — without it, AdGlide will not initialize and no ads will show.

---

## 🛠️ 3. Ad Implementation (Copy-Paste API)

### 📱 3.1 App Open Ads

App Open ads are fully automatic. When `appOpenEnabled(true)` is set, the SDK attaches to the Activity lifecycle and shows the ad automatically on each app resume — no additional code required.

#### Excluding Specific Screens

```java
// In your AdGlideConfig Builder:
.excludeOpenAdFrom(SplashActivity.class, PaymentActivity.class)
```

#### GDPR Consent on Splash Screen

```java
// Call this BEFORE showing any ads, typically in SplashActivity
AdGlide.requestConsent(this, () -> {
    // Consent complete — safe to show ads now
    proceedToMainActivity();
});
```

---

### 🖼️ 3.2 Banner Ads

```java
// Quick 1-liner (uses config's primary network)
AdGlide.showBanner(activity, binding.bannerContainer);

// Builder API for advanced control
new BannerAd.Builder(activity)
    .container(binding.bannerContainer)
    .collapsible(true) // ↗️ Up to 5× eCPM boost (AdMob only)
    .autoRefresh(30)   // 🔄 NEW: Auto-refresh with flicker-free background load
    .load();
```

---

### 🎬 3.3 Interstitial Ads

The recommended pattern: never block navigation. The callback fires immediately if no ad is ready.

```java
// Facade (recommended) — respects interval + preload cache
AdGlide.showInterstitial(activity, () -> {
    // Called after dismiss OR if no ad is ready
    startActivity(new Intent(this, NextActivity.class));
});

// Manual preload trigger (when autoLoadInterstitial is false)
AdGlide.preloadInterstitial(activity);

// Builder API
new InterstitialAd.Builder(activity)
    .load(new AdGlideCallback() {
        @Override public void onAdLoaded() { /* ready */ }
        @Override public void onAdFailedToLoad(String error) { /* handle */ }
    });
```

---

### 🎁 3.4 Rewarded Ads

```java
// Facade (recommended)
AdGlide.showRewarded(activity, new AdGlideCallback() {
    @Override
    public void onAdCompleted() {
        grantCoins(50); // ✅ User watched the full ad
    }

    @Override
    public void onAdDismissed() {
        resumeGame(); // Called whether completed or skipped
    }
});

// Manual preload
AdGlide.preloadRewarded(activity);
```

---

### 🎁 3.5 Rewarded Interstitial Ads

```java
// Facade
AdGlide.showRewardedInterstitial(activity, new AdGlideCallback() {
    @Override
    public void onAdCompleted() { grantBonus(); }

    @Override
    public void onAdDismissed() { resumeFlow(); }
});

// Manual preload
AdGlide.preloadRewardedInterstitial(activity);
```

---

### 🎨 3.6 Native Ads

Our "Super Perfect" Native Ad styles are fully Material Design compliant and seamlessly integrate across **all** supported networks.

```java
// Quick 1-liner
AdGlide.showNative(activity, binding.nativeContainer, "medium");

// Builder API with full style control and professional error handling
if (AdGlide.isNativeEnabled()) {
    binding.nativeContainer.setVisibility(View.VISIBLE);
    
    new NativeAd.Builder(activity)
        .container(binding.nativeContainer)
        .style(AdGlideNativeStyle.MEDIUM) // AdGlideNativeStyle.SMALL | MEDIUM | BANNER | VIDEO
        .load(new AdGlideCallback() {
            @Override
            public void onAdLoaded() {
                // Ad was successfully loaded and rendered
            }

            @Override
            public void onAdFailedToLoad(String error) {
                // Hide the container to prevent empty white space if no ad fills
                binding.nativeContainer.setVisibility(View.GONE);
            }
        });
} else {
    binding.nativeContainer.setVisibility(View.GONE);
}
```

#### Native Ad Styles Explained

- **`AdGlideNativeStyle.SMALL`** (`"small"`): Compact 1-line radio style. Fits perfectly in recycler views.
- **`AdGlideNativeStyle.MEDIUM`** (`"medium"`): Standard box with prominent CTA.
- **`AdGlideNativeStyle.BANNER`** (`"banner"`): Traditional news feed inline styling.
- **`AdGlideNativeStyle.VIDEO`** (`"video"`): Large scale immersive video or image container.

---

### 📡 3.7 Sequential Ad Delivery (Anti-Overlap)

AdGlide contains a built-in **Sequential Request Queue**. If your users click multiple ad-triggering buttons rapidly, the SDK ensures ads are shown **one-by-one** sequentially rather than overlapping or crashing.

### 📊 3.8 Global Diagnostic API

Monitor your SDK performance in real-time by registering a global listener.

```java
AdGlide.setListener((format, network, status, duration) -> {
    Log.d("AdGlideStats", format + " loaded from " + network + " in " + duration + "ms");
});
```

---

## 🚀 4. Pro Features

### 🏠 4.1 House Ads (Zero-Fill Fallback)

House Ads keep your inventory monetized even when all ad networks fail to fill. They show static promo images for your own apps or services.

```java
.houseAdEnabled(true)
.houseAdBannerImage("https://yourcdn.com/promo_banner.jpg")
.houseAdBannerClickUrl("https://play.google.com/store/apps/details?id=your.app")
.houseAdInterstitialImage("https://yourcdn.com/promo_full.jpg")
.houseAdInterstitialClickUrl("https://play.google.com/store/apps/details?id=your.app")

// 🆕 Native House Ad configuration
.houseAdNativeTitle("Try AdGlide Pro")
.houseAdNativeDescription("Unlock all advanced mediation features.")
.houseAdNativeCTA("Upgrade Now")
.houseAdNativeImage("https://yourcdn.com/promo_native.jpg")
.houseAdNativeIcon("https://yourcdn.com/promo_icon.png")
```

---

### 🛠️ 4.2 Live Debug HUD

The Debug HUD shows you exactly which network filled, failed, or was rate-limited on your device in real time.

```java
// Trigger via a secret gesture or developer menu
AdGlide.showDebugHUD(activity);
```

> [!TIP]
> Enable in config with `.enableDebugHUD(true)`. The HUD shows a **Waterfall Status** panel, a full **Performance Log**, and a **Clear Logs** button.

---

### 🔄 4.3 Runtime Config Updates

You can hot-swap the AdGlide configuration at runtime — for example, to disable ads after an in-app purchase:

```java
// Disable all ads instantly (e.g., after premium purchase)
AdGlide.updateConfig(new AdGlideConfig.Builder()
    .enableAds(false)
    .build());
```

---

## 🔒 5. Production & Security

### 🛡️ Zero-Config ProGuard / R8

**AdGlide is fully zero-config for code shrinking and obfuscation.**
Our embedded `consumer-rules.pro` automatically propagates the most optimal, tightly-scoped `-keep` and `-dontwarn` protocols directly into your release build.

You **do not** need to write any manual ProGuard rules for AdGlide or its supported networks when integrating via standard Gradle.

<details>
<summary><b>View Rules (Only required for local .aar manual integration)</b></summary>

```proguard
# AdGlide Core & Interfaces
-keep public class com.partharoypc.adglide.AdGlide { *; }
-keep public class com.partharoypc.adglide.AdGlideConfig { public *; }
-keep public class com.partharoypc.adglide.AdGlideConfig$Builder { public *; }
-keep class com.partharoypc.adglide.BuildConfig { *; }
-keepattributes Signature,*Annotation*,Exceptions,InnerClasses,EnclosingMethod

# Primary Network Integrations
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.mediation.** { *; }
-keep class com.facebook.ads.** { *; }
-keep class com.applovin.** { *; }
-keep class com.unity3d.ads.** { *; }
-keep class com.unity3d.services.** { *; }
-keep class com.ironsource.** { *; }
-keep class com.startapp.** { *; }
-keep class com.wortise.** { *; }
-keep class com.bytedance.** { *; }
-keep class com.google.android.ump.** { *; }

# Suppress warnings for omitted compileOnly networks
-dontwarn com.google.android.gms.ads.**
-dontwarn com.google.ads.mediation.**
-dontwarn com.facebook.ads.**
-dontwarn com.facebook.infer.annotation.**
-dontwarn com.applovin.**
-dontwarn com.unity3d.ads.**
-dontwarn com.ironsource.**
-dontwarn com.startapp.**
-dontwarn com.wortise.**
-dontwarn com.bytedance.**
-dontwarn com.google.android.ump.**
-dontwarn pl.droidsonroids.gif.**

# General Library Suppressions
-dontwarn androidx.annotation.**
-dontwarn com.google.errorprone.annotations.**
```

</details>

---

## 📈 6. Best Practices

| Practice | Recommendation |
| :--- | :--- |
| **App Open Ads** | Always enable — highest eCPM format. Exclude only truly blocking screens. |
| **Auto-Loading** | Use `.autoLoadInterstitial(true)` for zero-wait ad-ready state. |
| **Waterfall** | Set at least 1 backup network for higher fill rates. |
| **Intervals** | Start with `interstitialInterval(2)` — show every 2nd click to balance UX & revenue. |
| **House Ads** | Always configure a fallback — never waste an impression. |
| **Test Mode** | Always develop with `.testMode(true)` — **never** use real ads during testing. |
| **Collapsible Banners** | Use on home/dashboard screens for up to 5× standard CPM. |

---

## 📋 7. Quick Reference — `AdGlideConfig.Builder` Methods

| Method | Type | Default | Description |
| :--- | :--- | :---: | :--- |
| `enableAds(bool)` | `boolean` | `false` | Master ad on/off toggle |
| `testMode(bool)` | `boolean` | `false` | Use test ads during development |
| `debug(bool)` | `boolean` | `true` | Enable verbose logcat output |
| `primaryNetwork(network)` | `AdGlideNetwork` | — | Primary network (Enum preferred) |
| `backupNetworks(n...)` | `AdGlideNetwork...`| — | Fallback networks (Enum preferred) |
| `bannerEnabled(bool)` | `boolean` | `false` | Toggle banner ads |
| `interstitialEnabled(bool)` | `boolean` | `false` | Toggle interstitial ads |
| `rewardedEnabled(bool)` | `boolean` | `false` | Toggle rewarded ads |
| `nativeEnabled(bool)` | `boolean` | `false` | Toggle native ads |
| `appOpenEnabled(bool)` | `boolean` | `false` | Toggle app open ads |
| `rewardedInterstitialEnabled(bool)` | `boolean` | `false` | Toggle rewarded interstitial |
| `autoLoadInterstitial(bool)` | `boolean` | `false` | Auto-prefetch after show |
| `autoLoadRewarded(bool)` | `boolean` | `false` | Auto-prefetch after show |
| `interstitialInterval(int)` | `int` | `0` | Show every N calls (0 = always) |
| `rewardedInterval(int)` | `int` | `0` | Show every N calls (0 = always) |
| `houseAdEnabled(bool)` | `boolean` | `false` | Enable house ads fallback |
| `excludeOpenAdFrom(Class...)` | `Class<?>...` | — | Blacklist activities from App Open |
| `enableGDPR(bool)` | `boolean` | `false` | Show UMP consent form |
| `debugGDPR(bool)` | `boolean` | `false` | Force GDPR dialog in debug |
| `enableDebugHUD(bool)` | `boolean` | `false` | Enable integrated Debug HUD |
| `adResponseTimeout(ms)` | `int` | `3500` | Max wait for network response |

### Configuration Constants (`com.partharoypc.adglide.util.Constant`)

Use these constants when defining your networks in the `AdGlideConfig.Builder` if you prefer string keys:

| Value | Network |
| :--- | :--- |
| `Constant.ADMOB` | Google AdMob |
| `Constant.META` | Meta Audience Network |
| `Constant.APPLOVIN_MAX` | AppLovin MAX |
| `Constant.WORTISE` | Wortise |
| `Constant.IRONSOURCE` | IronSource / LevelPlay |
| `Constant.UNITY` | Unity Ads |
| `Constant.STARTAPP` | StartApp |
| `Constant.HOUSE_AD` | House Ad (internal) |
| `Constant.META_BIDDING_ADMOB` | Meta bidding via AdMob |
| `Constant.META_BIDDING_APPLOVIN_MAX` | Meta bidding via AppLovin |
| `Constant.META_BIDDING_IRONSOURCE` | Meta bidding via IronSource |

---

---

<div align="center">

## 👨‍💻 Developed By

**Partha Roy**  
*Senior Android Engineer & Architecture Specialist*

[![GitHub](https://img.shields.io/badge/GitHub-partharoypc-181717?style=for-the-badge&logo=github)](https://github.com/partharoypc)
[![License](https://img.shields.io/badge/License-Apache_2.0-yellow.svg?style=for-the-badge)](LICENSE)

*Built for Scale • Optimized for Speed • Perfected for Developers*

**© 2026 [AdGlide SDK](https://github.com/partharoypc/AdGlide) — All rights reserved.**

</div>
```
