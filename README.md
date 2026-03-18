# AdGlide SDK 🚀
### *The Premium Mediation Wrapper for High-Performance Android Apps*

[![Version](https://img.shields.io/badge/Version-1.6.0-blue.svg)](https://github.com/partharoypc/AdGlide)
[![Android](https://img.shields.io/badge/Android-23%2B-green.svg)](https://developer.android.com)
[![Compile SDK](https://img.shields.io/badge/Compile_SDK-36-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/projects/jdk/17/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**AdGlide** is an industrial-grade ad mediation SDK for professional Android developers. It eliminates the friction of multi-network integration with a **"Just Copy-Paste"** architecture that handles initialization, waterfall orchestration, rate limiting, pre-fetching, and crash protection out of the box across **8 ad networks**.

---

## ✨ What's New in v1.6.0

- **IronSource LevelPlay Migration** — Fully migrated to the new LevelPlay Ad Unit APIs (SDK 9.3.0). Higher performance and better mediation orchestration.
- **Fully Crash-Safe Ad Display** — Every `showAd()` call on every network is now wrapped in a `try-catch` guard. Third-party SDK crashes can never propagate to your app.
- **Consistent Callback Coverage** — `onAdShowed()` and `onAdShowFailed()` are now reliably fired on all 8 networks.
- **Plural `backupNetworks()`** — Easily supply an array of backup networks for robust waterfall fill.
- **`PerformanceLogger` Across All Networks** — Unified event tracking (load, show, fail) for all providers; visible in the live Debug HUD.
- **Debug HUD v2** — New Waterfall Status panel with real-time ✓/✗ events, plus a "Clear Logs" button.
- **`AdGlide.preloadRewardedInterstitial()`** — New manual preload API for Rewarded Interstitial format.
- **`AdGlide.updateConfig()`** — Hot-swap your configuration at runtime without re-initializing.
- **`AdGlide.requestConsent()`** — Explicit GDPR/UMP consent request flow for Splash screens.
- **Brand New Demo App** — The repository includes a stunning Material 3 demo app featuring a live Network Selector, Theme Toggle, and interactive integration examples for every format.

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
| **Rewarded** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ |
| **Rewarded Interstitial** | ✅ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| **Native** | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ | ❌ |
| **App Open** | ✅ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
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
    }
}
```

---

### Step 2: Add Dependencies

Open your **app-level `build.gradle`** and add the AdGlide core plus only the network SDKs you need.

```gradle
dependencies {
    // 🚀 AdGlide Core (Required)
    implementation 'com.github.partharoypc:adglide:1.7.0'

    // 🛡️ GDPR Consent (Required for EU compliance)
    implementation 'com.google.android.ump:user-messaging-platform:4.0.0'

    // ─── Choose Your Networks ───────────────────────────────────────
    implementation 'com.google.android.gms:play-services-ads:25.0.0'    // AdMob ✅
    // implementation 'com.facebook.android:audience-network-sdk:6.21.0' // Meta
    // implementation 'com.applovin:applovin-sdk:13.6.1'                  // AppLovin
    // implementation 'com.startapp:inapp-sdk:5.3.0'                      // StartApp
    // implementation 'com.wortise:android-sdk:1.7.2'                     // Wortise
    // implementation 'com.unity3d.ads-mediation:mediation-sdk:9.3.0'     // IronSource (LevelPlay)
    // implementation 'com.unity3d.ads:unity-ads:4.17.0'                  // Unity Ads
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

Initialize AdGlide once in your `Application.onCreate()`. Note the use of `com.partharoypc.adglide.util.Constant` parameters for robust configuration.

```java
import com.partharoypc.adglide.util.Constant;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AdGlideConfig config = new AdGlideConfig.Builder()
            // ─── Global Settings ─────────────────────────────────────────
            .enableAds(true)                         // Master on/off switch
            .testMode(false)                         // ⚠️ Set to FALSE for production
            .debug(true)                             // Verbose console logging

            // ─── Waterfall Strategy ──────────────────────────────────────
            .primaryNetwork(Constant.ADMOB)
            .backupNetworks(Constant.META, Constant.WORTISE)

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
            .interstitialInterval(2)                 // Show every N clicks (0 = always)
            .rewardedInterval(1)                     // Show every N clicks (0 = always)

            // ─── GDPR / Privacy ─────────────────────────────────────────
            .enableGDPR(true)
            .debugGDPR(false)

            // ─── App Open Exclusions ─────────────────────────────────────
            .excludeOpenAdFrom(SplashActivity.class, PaymentActivity.class)

            // ─── AdMob ───────────────────────────────────────────────────
            .adMobAppId("ca-app-pub-XXXXXXXX~XXXXXXXX")
            .adMobBannerId("ca-app-pub-XXXXXXXX~XXXXXXXX")
            .adMobInterstitialId("ca-app-pub-XXXXXXXX~XXXXXXXX")
            .adMobRewardedId("ca-app-pub-XXXXXXXX~XXXXXXXX")
            .adMobRewardedIntId("ca-app-pub-XXXXXXXX~XXXXXXXX")
            .adMobNativeId("ca-app-pub-XXXXXXXX~XXXXXXXX")
            .adMobAppOpenId("ca-app-pub-XXXXXXXX~XXXXXXXX")

            // ─── House Ads (Zero-Fill Fallback) ──────────────────────────
            .houseAdEnabled(true)
            .houseAdBannerImage("https://yourcdn.com/promo_banner.jpg")
            .houseAdBannerClickUrl("https://play.google.com/store/apps/details?id=your.app")
            .houseAdInterstitialImage("https://yourcdn.com/promo_full.jpg")
            .houseAdInterstitialClickUrl("https://play.google.com/store/apps/details?id=your.app")

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

Our "Super Perfect" Native Ad styles are fully Material Design compliant and seamlessly integrate across **all** supported networks (AdMob, Meta, AppLovin, IronSource, StartApp, Wortise).

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

## 🚀 4. Pro Features

### 🏠 4.1 House Ads (Zero-Fill Fallback)

House Ads keep your inventory monetized even when all ad networks fail to fill. They show static promo images for your own apps or services.

```java
.houseAdEnabled(true)
.houseAdBannerImage("https://yourcdn.com/promo_banner.jpg")
.houseAdBannerClickUrl("https://play.google.com/store/apps/details?id=your.app")
.houseAdInterstitialImage("https://yourcdn.com/promo_full.jpg")
.houseAdInterstitialClickUrl("https://play.google.com/store/apps/details?id=your.app")
```

---

### 🛠️ 4.2 Live Debug HUD

The Debug HUD shows you exactly which network filled, failed, or was rate-limited on your device in real time. It also shows waterfall status with real-time ✓/✗ indicators.

```java
// Trigger via a secret gesture or developer menu
AdGlide.showDebugHUD(activity);
```

> [!TIP]
> Enable in config with `.debug(true)`. The HUD shows a **Waterfall Status** panel, a full **Performance Log**, and a **Clear Logs** button.

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

### ProGuard / R8 Rules

Add these rules to your `proguard-rules.pro` file when `minifyEnabled true`:

```proguard
# AdGlide Core
-keep public class com.partharoypc.adglide.** { *; }

# Ad Networks
-dontwarn com.google.android.gms.ads.**
-dontwarn com.facebook.ads.**
-dontwarn com.applovin.**
-dontwarn com.startapp.**
-dontwarn com.wortise.**
-dontwarn com.ironsource.**
-dontwarn com.unity3d.ads.**
-dontwarn com.unity3d.services.**
```

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
| `primaryNetwork(network)` | `String` | — | Use `Constant.ADMOB` etc. |
| `backupNetworks(n...)` | `String...` | — | Fallback string constants |
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

### Configuration Constants (`com.partharoypc.adglide.util.Constant`)

Use these constants when defining your networks in the `AdGlideConfig.Builder`:

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

## 👨‍💻 Developed By

**Partha Roy**
*Android Developer & Architecture Enthusiast*
- 🌐 **GitHub**: [github.com/partharoypc](https://github.com/partharoypc)

*Built for Scale. Optimized for Speed. Perfected for Developers.*
*© 2026 AdGlide — All rights reserved.*