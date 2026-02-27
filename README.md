# AdGlide SDK 🚀
### *The Premium Mediation Wrapper for High-Performance Android Apps*

[![Version](https://img.shields.io/badge/Version-1.4.0-blue.svg)](https://github.com/partharoypc/AdGlide)
[![SDK Support](https://img.shields.io/badge/Android-23%2B-green.svg)](https://developer.android.com)
[![Compile SDK](https://img.shields.io/badge/Compile_SDK-36-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**AdGlide** is an industrial-grade ad mediation SDK designed for professional Android developers. It eliminates the friction of multi-network integration, providing a **"Just Copy-Paste"** architecture that handles initialization, bidding, waterfalls, rate limiting, and pre-fetching out of the box.

---

## ✨ What's New in v1.4.0

- **Global Configuration Object** — Replaced `AdGlide.init()` chain with a robust `AdGlideConfig` architecture, ensuring thread-safe settings decoupled from Context or Activity lifting constraints.
- **Zero Memory Leaks Architecture** — Eliminated aggressive `Activity` contexts traversing formats. We safely handle transitions using `WeakReference<Activity>` for App Open, Banner, Interstitial, and Native formats, protecting app performance organically.
- **House Ads & Offline Fallback** — Keep users engaged even without an internet connection using static internal promos.
- **LTV & Revenue Callbacks (`OnPaidEventListener`)** — Direct access to micro-revenue estimates from loaded ads to help you calculate precise LTV locally.
- **Manual Preloading APIs** — Added explicit `AdGlide.preloadInterstitial()` and `AdGlide.preloadRewarded()` hooks so you control cache timing manually beyond the automated interval configurations.
- **Modernized Core** — Refactored internal ad unit mapping with static logic and Java 17+ switch expressions for peak performance and safety.

---

## 🏗️ Core Architecture

AdGlide supports four distinct integration patterns:

1. **Direct Use** — Target a specific ad network exclusively.
2. **Bidding Mediation** — Leverage real-time header bidding for supported networks (Meta ↔ AdMob, Meta ↔ AppLovin, Meta ↔ IronSource).
3. **Sequential Waterfall** — A fail-safe `WaterfallManager` that cycles through unlimited backup networks if the primary fails to fill.
4. **Intelligent Rate Limiting** — Built-in `AdMobRateLimiter` with exponential backoff to prevent failed units from looping endlessly.

### 📊 Network × Format Support Matrix

| Ad Format | AdMob | Meta | AppLovin | StartApp | Wortise | Unity | IronSource |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Banner** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Interstitial** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Native** | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ |
| **Rewarded** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Rewarded Interstitial** | ✅ | ❌ | ✅ | ❌ | ✅ | ❌ | ❌ |
| **App Open** | ✅ | ❌ | ✅ | ❌ | ✅ | ❌ | ❌ |
| **Bidding** | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ✅ |
| **Direct Use** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Bidding Mediation** | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ✅ |
| **Sequential Waterfall** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Intelligent Rate Limiting** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

### 🎨 Native Ad Styles

| Enum Value | Description |
| :--- | :--- |
| `AdGlideNativeStyle.SMALL` | Icon + Title (List items) |
| `AdGlideNativeStyle.MEDIUM` | Image + Title + Body (Feed) |
| `AdGlideNativeStyle.BANNER` | Horizontal / News style |
| `AdGlideNativeStyle.VIDEO` | Large Media focus |

### 📦 SDK Module Structure

```
com.partharoypc.adglide
├── AdGlide.java              # SDK entry point & initializer
├── AdGlideNetwork.java       # Enum: ADMOB, META, APPLOVIN, STARTAPP, WORTISE, UNITY, IRONSOURCE...
├── AdGlideNativeStyle.java   # Enum: SMALL, MEDIUM, BANNER, VIDEO
├── format/
│   ├── AdNetwork.java            # Network initializer logic
│   ├── AppOpenAd.java            # App Open ads (Builder + Lifecycle)
│   ├── BannerAd.java             # Banner ads (adaptive, collapsible)
│   ├── InterstitialAd.java       # Full-screen interstitial ads
│   ├── RewardedAd.java           # Rewarded video ads
│   ├── RewardedInterstitialAd.java # Rewarded interstitial ads
│   ├── NativeAd.java             # Native ads (Activity loading)
│   └── NativeAdView.java         # Native ads (Custom View integration)
├── gdpr/
│   ├── GDPR.java                 # Google UMP & Consent integration
│   └── LegacyGDPR.java          # Legacy consent handling
└── util/
    ├── AdMobRateLimiter.java     # Intelligent rate limiting
    ├── WaterfallManager.java     # Sequential backup manager
    ├── Tools.java                # Utilities & Base64 decoding
    └── Constant.java             # Network key constants
```

---

## ⚡ Step-by-Step Setup Guide

### Step 1: Configure Repositories
In your `settings.gradle` (or project `build.gradle`), add the repositories. **Note:** Only add the repositories for the networks you actually plan to use.

```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' } // Required for AdGlide
        
        // --- Optional repositories for specific networks ---
        maven {
            url 'https://artifacts.applovin.com/android' // AppLovin
            content { includeGroup "com.applovin" }
        }
        maven { url 'https://artifact.bytedance.com/repository/pangle' }   // Meta Bidding/Pangle
        maven { url 'https://maven.wortise.com/artifactory/public' }      // Wortise
        maven { url 'https://android-sdk.is.com/' }                       // IronSource
    }
}
```

### Step 2: Add Dependencies
In your app-level `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.partharoypc:adglide:1.4.0'
    
    // 🔥 OPTIONAL DEPENDENCIES 🔥
    // You ONLY need to add repositories and dependencies for the networks you actually use.
    // If you don't use a network, you can safely remove its implementation line AND its repository above.
    
    implementation 'com.google.android.gms:play-services-ads:23.6.0'   // AdMob
    // implementation 'com.facebook.android:audience-network-sdk:6.18.0'  // Meta
    // implementation 'com.applovin:applovin-sdk:13.0.1'                  // AppLovin
    // implementation 'com.startapp:inapp-sdk:5.3.0'                      // StartApp
    // implementation 'com.wortise:android-sdk:1.7.0'                     // Wortise
    // implementation 'com.unity3d.ads:unity-ads:4.12.5'                  // Unity
    // implementation 'com.ironsource.sdk:mediationsdk:8.4.0'             // IronSource
    
    // GDPR (Required for EU compliance)
    implementation 'com.google.android.ump:user-messaging-platform:4.0.0'
}
```

### Step 3: Configure AndroidManifest.xml
**CRITICAL:** If using AdMob or AppLovin, you **MUST** declare your App IDs in the `<application>` tag to prevent crashes.

```xml
<application ...>
    <!-- AdMob App ID -->
    <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="ca-app-pub-3940256099942544~3347511713"/>
        
    <!-- AppLovin SDK Key (if using AppLovin) -->
    <meta-data
        android:name="applovin.sdk.key"
        android:value="YOUR_APPLOVIN_SDK_KEY"/>
</application>
```

### Step 4: Global SDK Initialization
Initialize AdGlide inside your `Application` class using `AdGlideConfig`:

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AdGlideConfig config = new AdGlideConfig.Builder()
            .enableAds(true)         // Master switch for ALL ads
            .testMode(false)      // Development only — set false for production!
            .debug(true)          // Verbose console logging
            .primaryNetwork(AdGlideNetwork.ADMOB)
            .backupNetworks(AdGlideNetwork.META, AdGlideNetwork.APPLOVIN)
            .enableAppOpenAd(true)
            .houseAdEnabled(true)
            
            // Supply SDK IDs (only for networks you use)
            .adMobAppId("ca-app-pub-3940256099942544~3347511713")
            .startAppId("YOUR_STARTAPP_ID")
            .appLovinSdkKey("YOUR_APPLOVIN_KEY")
            .ironSourceAppKey("YOUR_IRONSOURCE_KEY")
            .unityGameId("YOUR_UNITY_GAME_ID")
            .wortiseAppId("YOUR_WORTISE_APP_ID")
            .build();
            
        AdGlide.initialize(this, config);
    }
}
```

### ⚡ Kotlin DSL (Optional)
```kotlin
adGlideConfig {
    primaryNetwork = AdGlideNetwork.ADMOB
    autoLoadInterstitial = true
    debug = true
}
```

---

## 🛠️ 3. Ad Format Guide (The 1-Line API)

### 📱 App Open Ads (Automated)
Once initialized, App Open ads are managed automatically via activity lifecycle. No additional code is required to show ads on cold starts or resumes.

### 🖼️ Banner Ads
Show a high-performance adaptive banner in any container:
```java
AdGlide.showBanner(activity, container);
```
*Advanced:*
```java
new BannerAd.Builder(activity)
    .collapsible(true)
    .container(myLayout)
    .load();
```

### 🎬 Interstitial Ads
```java
AdGlide.showInterstitial(activity);
```
*With Callback:*
```java
AdGlide.showInterstitial(activity, () -> {
    // Action after ad dismissed
});
```

### 🎁 Rewarded Ads
```java
AdGlide.showRewarded(activity, (rewarded) -> {
    if (rewarded) {
        // Grant user 50 coins
    }
});
```

### 🎨 Native Ads
Show beautiful native templates instantly:
```java
AdGlide.showNative(activity, AdGlideNativeStyle.MEDIUM, container);
```
Available Styles: `SMALL`, `MEDIUM`, `BANNER`, `VIDEO`.

---

## 🛰️ 4. Dynamic Remote Configuration

Sync your ad IDs and status from a remote JSON without updating your app:

```java
AdGlide.fetchRemoteConfig("https://api.myapp.com/ads.json", (config) -> {
    // Config synchronized globally!
});
```

**JSON Schema Example:**
```json
{
  "ad_status": true,
  "primary_network": "ADMOB",
  "backup_networks": ["STARTAPP", "UNITY"],
  "admob_banner_id": "ca-app-pub-...",
  "interstitial_interval": 2
}
```

---

## 🛠️ 5. SDK Debugger (HUD)

Activate the built-in diagnostic overlay to monitor ad fill and waterfall performance in real-time.
*   **Trigger**: Use a secret button in your app to call:
```java
AdGlide.showDebugHUD(activity);
```

---

## � 6. Security & ProGuard

Add these to your `proguard-rules.pro`:
```proguard
-keep public class com.partharoypc.adglide.** { *; }
-dontwarn com.google.android.gms.ads.**
-dontwarn com.facebook.ads.**
-dontwarn com.applovin.**
-dontwarn com.startapp.**
```

---
*Built for Scale. Optimized for Speed. Perfected for Developers.*
© 2026 AdGlide Pro. All rights reserved.
