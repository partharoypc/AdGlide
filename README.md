<p align="center">
  <img src="assets/banner.png" alt="AdGlide Banner">
</p>

# AdGlide SDK 🚀
### *The Premium Mediation Wrapper for High-Performance Android Apps*

[![Version](https://img.shields.io/badge/Version-1.3.0-blue.svg)](https://github.com/partharoypc/AdGlide)
[![SDK Support](https://img.shields.io/badge/Android-23%2B-green.svg)](https://developer.android.com)
[![Compile SDK](https://img.shields.io/badge/Compile_SDK-36-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**AdGlide** is an industrial-grade ad mediation SDK designed for professional Android developers. It eliminates the friction of multi-network integration, providing a **"Just Copy-Paste"** architecture that handles initialization, bidding, waterfalls, rate limiting, and pre-fetching out of the box.

---

## ✨ What's New in v1.3.0

- **Optional Dependencies Architecture** — Drastically shrink your APK! AdGlide now uses reflection to load ad networks dynamically. You only need to compile the networks you actually use; the SDK safely ignores missing ones.
- **Dynamic Provider Pattern** — True separation of concerns. Format modules like `BannerAd`, `InterstitialAd`, and `RewardedAd` now securely delegate to modular `ProviderFactory` engines.
- **Smart Readiness Checks** — Added defensive `isAdLoaded()` and `isAdAvailable()` pre-show validations for Interstitial, Rewarded, and App Open ads to eliminate misfired impressions and maximize valid fill rates.
- **100% Match Rate Optimization & Fast-Fail Cascades** — Re-architected `WaterfallManager` to immediately bypass primary networks when invalid or empty Ad Unit IDs are supplied. By bypassing sluggish internal timeout cycles from disabled placements, AdGlide smoothly cascades to backup networks dynamically with literally zero millisecond delay, guaranteeing maximized revenue fill-rates.
- **Unified App Open Architecture** — Centralized waterfall logic for App Open models across AdMob, AppLovin, and Wortise.
- **Intelligent Rate Limiting** — `AdMobRateLimiter` prevents failing AdMob units from creating endless request loops.
- **Configurable App Open Cooldowns** — Added ability to customize the 30-minute default cooldown globally or per-instance.
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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
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
    implementation 'com.github.partharoypc:adglide:1.3.0'
    
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
Initialize AdGlide inside your `Application` class:

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AdGlide.init(this)
            .status(true)         // Master switch for ALL ads
            .testMode(false)      // Development only — set false for production!
            .debug(true)          // Verbose console logging
            .network(AdGlideNetwork.ADMOB)
            .backups(AdGlideNetwork.META, AdGlideNetwork.APPLOVIN)
            
            // Supply SDK IDs (only for networks you use)
            .adMobId("ca-app-pub-3940256099942544~3347511713")
            .startAppId("YOUR_STARTAPP_ID")
            .appLovinId("YOUR_APPLOVIN_KEY")
            .ironSourceId("YOUR_IRONSOURCE_KEY")
            .unityId("YOUR_UNITY_GAME_ID")
            .wortiseId("YOUR_WORTISE_APP_ID")
            .build();
    }
}
```

---

## 🛠️ Ad Implementation Guide

AdGlide uses a **Builder** pattern. Every ad format shares these core methods:

| Method | Description |
| :--- | :--- |
| `.status(boolean)` | Turn this specific ad ON/OFF |
| `.placement(int)` | Remote placement toggle (0 = OFF, 1 = ON) |
| `.network(AdGlideNetwork)` | Set the primary ad network |
| `.backup(AdGlideNetwork)` | Set a single fallback network |
| `.backups(AdGlideNetwork...)` | Set a waterfall of multiple backups |
| `.cooldown(int)` | Set App Open Ad cooldown in minutes |
| `.darkTheme(boolean)` | Match ad styles to dark mode |

---

### 1. App Open Ads

App Open ads display when the app foregrounds (cold start or resume).

**Automatic Lifecycle (Recommended):**
```java
AppOpenAd appOpenAd = new AppOpenAd()
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .adMobId("ca-app-pub-3940256099942544/9257395921");

// Start listening to foreground events
appOpenAd.setLifecycleObserver()
         .setActivityLifecycleCallbacks(this);
```

**Manual Trigger (Splash Screen):**
```java
new AppOpenAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .backup(AdGlideNetwork.APPLOVIN)          // Single backup
    .backups(AdGlideNetwork.APPLOVIN, AdGlideNetwork.WORTISE) // Or waterfall
    .adMobId("ca-app-pub-3940256099942544/9257395921")
    .appLovinId("YOUR_APPLOVIN_UNIT_ID")
    .wortiseId("YOUR_WORTISE_UNIT_ID")
    .cooldown(15)                             // Optional: Override default 30-min cooldown
    .load(new OnShowAdCompleteListener() {
        @Override
        public void onShowAdComplete() {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    });
```

---

### 3. Interstitial Ads

```java
new InterstitialAd.Builder(this)
    .status(true)
    .placement(1)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.STARTAPP, AdGlideNetwork.APPLOVIN)
    .adMobId("ca-app-pub-3940256099942544/1033173712")
    .interval(3) // Frequency capping: shows every 3rd call
    .build()
    .load(new OnInterstitialAdDismissedListener() {
        @Override
        public void onInterstitialAdDismissed() {
            // Navigate to next screen
        }
    })
    .show(this); // Internally checks isAdLoaded() before displaying
```

---

### 4. Native Ads

AdGlide offers **2 specialized native ad builders** for different UI contexts:

| Builder | Use Case |
| :--- | :--- |
| `NativeAd.Builder` | Standard Activity layouts |
| `NativeAdView.Builder` | Custom View integration |

**XML Layout:**
Use one of the predefined layout containers:
```xml
<include layout="@layout/adglide_view_native_ad_medium" />
```

Available layout variants:  
`adglide_view_native_ad_small`, `adglide_view_native_ad_medium`, `adglide_view_native_ad_news`, `adglide_view_native_ad_video_large`

**Activity Implementation:**
```java
new NativeAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.META, AdGlideNetwork.STARTAPP)
    .adMobId("ca-app-pub-3940256099942544/2247696110")
    .metaId("YOUR_META_NATIVE_ID")
    .style(AdGlideNativeStyle.MEDIUM)
    .darkTheme(false)
    .backgroundColor(R.color.white, R.color.black)
    .padding(10, 10, 10, 10)
    .margin(16, 8, 16, 8)
    .build()
    .load();
```

**Fragment Implementation:**
```java
new NativeAdFragment.Builder(getActivity())
    .view(rootView)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .adMobId("YOUR_ADMOB_NATIVE_ID")
    .style(AdGlideNativeStyle.BANNER)
    .darkTheme(false)
    .build()
    .load();
```

**RecyclerView Integration (ViewHolder):**
```java
// Inside your RecyclerView Adapter's onBindViewHolder:
NativeAdViewHolder holder = new NativeAdViewHolder(activity, itemView);
holder.loadNativeAd(adNetwork, backupNetwork, adMobId, metaId, appLovinId, 
    darkTheme, legacyGDPR, nativeAdStyle);
```

---

### 5. Rewarded Ads

```java
RewardedAd rewardedAdBuilder = new RewardedAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.UNITY)
    .adMobId("ca-app-pub-3940256099942544/5224354917")
    .build();

// Pre-load silently
rewardedAdBuilder.load(
    () -> { /* User earned reward! Give coins/lives. */ },
    () -> { /* Ad dismissed */ }
);

// Show on button click
rewardedAdBuilder.show(
    () -> { /* Reward user */ },
    () -> { /* Ad closed */ },
    () -> { Toast.makeText(this, "No ad available.", Toast.LENGTH_SHORT).show(); }
);
```

**Rewarded Interstitial:**
```java
new RewardedInterstitialAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .adMobId("YOUR_ADMOB_REWARDED_INTERSTITIAL_ID")
    .build()
    .load(/* same callbacks as RewardedAd */);
```

---

## 🚀 Pro Performance Features

### Triple-Base64 Security
Protect your Ad Unit IDs from APK decompilation:

```java
// Encode your ID three times with Base64, then decode at runtime:
String safeId = Tools.decode("TWpZNE5UYzVOekk1TkRRME5nPT0=");
new BannerAd.Builder(this).adMobId(safeId).build().load();
```

### GDPR Consent Management
AdGlide includes built-in GDPR support via Google's User Messaging Platform:

```java
// In your main Activity's onCreate:
GDPR gdpr = new GDPR();
gdpr.updateConsentInfo(this, isTestDevice, isChildDirected);
```

---

## 🔒 Network Safety

AdGlide follows a **"Network-First"** approach with automatic connectivity checks:

- **SDK Initialization** — `AdNetwork.initAds()` skips initialization if offline.
- **Ad Loading** — All formats check for internet before sending requests.
- **Fast-Fail Bypassing** — Gracefully instantly bypasses networks displaying empty or invalid Ad Unit IDs.
- **Waterfall Fail-Safe** — If a device goes offline mid-waterfall, the SDK gracefully stops.
- **Rate Limiting** — `AdMobRateLimiter` applies exponential cooldown to failing ad units.

---

## 🛡️ ProGuard / R8 Rules

Add these to your `proguard-rules.pro` for production builds:

```proguard
# AdGlide SDK Core & Dynamic Providers
-keep public class com.partharoypc.adglide.** { *; }
-keep interface com.partharoypc.adglide.util.On*Listener { *; }

# Prevent ProGuard from stripping absent network SDKs used in Reflection
-dontwarn com.google.android.gms.ads.**
-dontwarn com.facebook.ads.**
-dontwarn com.facebook.infer.annotation.**
-dontwarn com.applovin.**
-dontwarn com.startapp.**
-dontwarn com.wortise.**
-dontwarn com.unity3d.ads.**
-dontwarn com.ironsource.**
-dontwarn com.google.ads.mediation.**
-dontwarn com.bytedance.**

# AdMob
-keep class com.google.android.gms.ads.** { *; }

# Meta Audience Network
-keep class com.facebook.ads.** { *; }

# AppLovin
-keep class com.applovin.** { *; }

# StartApp
-keep class com.startapp.** { *; }
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod

# Wortise
-keep class com.wortise.** { *; }

# Unity Ads
-keep class com.unity3d.ads.** { *; }

# IronSource
-keep class com.ironsource.** { *; }
```

---

## 📋 Technical Specifications

| Property | Value |
| :--- | :--- |
| **SDK Version** | 1.3.0 |
| **Min SDK** | 23 (Android 6.0) |
| **Target SDK** | 36 |
| **Compile SDK** | 36 |
| **Java Version** | 17 |
| **Build System** | Gradle 9.3.0 |
| **AndroidX** | Required |
| **Distribution** | JitPack (`com.github.partharoypc:adglide:1.3.0`) |

---

## 🤝 Support & Community
Developed with ❤️ by **[Partha Roy](https://github.com/partharoypc)**.

If this SDK saved you hours of integration headaches, please consider leaving a ⭐ on GitHub!

For bugs, feature requests, or custom mediation integrations, please [open an issue](https://github.com/partharoypc/AdGlide/issues).

---
*AdGlide SDK is MIT Licensed. © 2026 Partha Roy.*
