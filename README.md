<p align="center">
  <img src="assets/banner.png" alt="AdGlide Banner">
</p>

# AdGlide SDK 🚀
### *The Premium Mediation Wrapper for High-Performance Android Apps*

[![Version](https://img.shields.io/badge/Version-1.2.0-blue.svg)](https://github.com/partharoypc/AdGlide)
[![SDK Support](https://img.shields.io/badge/Android-21%2B-green.svg)](https://developer.android.com)
[![Compile SDK](https://img.shields.io/badge/Compile_SDK-35-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**AdGlide** is an industrial-grade ad mediation SDK designed for professional Android developers. It eliminates the friction of multi-network integration, providing a **"Just Copy-Paste"** architecture that handles initialization, bidding, waterfalls, rate limiting, and pre-fetching out of the box.

---

## ✨ What's New in v1.2.0

- **Unified App Open Architecture** — Static helper instances (`AdMobAppOpenAd`, `AppLovinAppOpenAd`, `WortiseAppOpenAd`) with centralized waterfall logic.
- **Smooth Native Ad Transitions** — 400ms fade-in animations (`animateIn`) across all native ad formats for a premium feel.
- **Intelligent Rate Limiting** — `AdMobRateLimiter` prevents failing AdMob units from creating endless request loops with exponential cooldown.
- **Theme-Aware Backgrounds** — All ad backgrounds default to `transparent`, inheriting the host app's theme seamlessly.
- **Centralized Design System** — All layout dimensions, typography, and CTA button styles flow from `dimens.xml`, `styles.xml`, and `colors.xml`.
- **GDPR Compliance** — Built-in `GDPR` and `LegacyGDPR` modules for consent management via Google UMP.

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
| **Medium Rectangle** | ✅ | ✅ | ✅ | ❌ | ✅ | ❌ | ❌ |
| **Bidding** | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ✅ |

### 🎨 Native Ad Styles

| Enum Value | Description |
| :--- | :--- |
| `AdGlideNativeStyle.SMALL` | Compact inline format |
| `AdGlideNativeStyle.MEDIUM` | Standard card format |
| `AdGlideNativeStyle.RADIO` | Horizontal icon + text |
| `AdGlideNativeStyle.NEWS` | Icon + headline + CTA |
| `AdGlideNativeStyle.VIDEO_SMALL` | Small video thumbnail |
| `AdGlideNativeStyle.VIDEO_LARGE` | Full-width video card |
| `AdGlideNativeStyle.STREAM` | Stream/feed-optimized |

### 📦 SDK Module Structure

```
com.partharoypc.adglide
├── AdGlide.java              # SDK entry point & initializer
├── AdGlideNetwork.java       # Enum: ADMOB, META, APPLOVIN, STARTAPP, WORTISE, UNITY, IRONSOURCE...
├── AdGlideNativeStyle.java   # Enum: SMALL, MEDIUM, RADIO, NEWS, VIDEO_SMALL, VIDEO_LARGE, STREAM
├── format/
│   ├── AdNetwork.java            # Network initializer (AdMob, Meta, AppLovin, Unity, IronSource etc.)
│   ├── AppOpenAd.java            # App Open ads (Builder + Lifecycle)
│   ├── AdMobAppOpenAd.java       # AdMob App Open helper (load, cache, show, 4hr expiry)
│   ├── AppLovinAppOpenAd.java    # AppLovin App Open helper
│   ├── WortiseAppOpenAd.java     # Wortise App Open helper
│   ├── BannerAd.java             # Banner ads (adaptive, collapsible)
│   ├── MediumRectangleAd.java    # MREC 300x250 ads
│   ├── InterstitialAd.java       # Full-screen interstitial ads
│   ├── RewardedAd.java           # Rewarded video ads
│   ├── RewardedInterstitialAd.java # Rewarded interstitial ads
│   ├── NativeAd.java             # Native ads (Activity-based)
│   ├── NativeAdFragment.java     # Native ads (Fragment-based)
│   ├── NativeAdView.java         # Native ads (Custom View)
│   ├── NativeAdViewHolder.java   # Native ads (RecyclerView)
│   └── NativeAdViewPager.java    # Native ads (ViewPager)
├── gdpr/
│   ├── GDPR.java                 # Google UMP consent management
│   └── LegacyGDPR.java          # Legacy GDPR consent handling
├── helper/
│   └── AudienceNetworkInitializeHelper.java
├── ui/
│   ├── BannerAdView.java         # Custom banner view
│   ├── LargeNativeAdView.java    # Large native ad view
│   ├── MediumNativeAdView.java   # Medium native ad view
│   └── SmallNativeAdView.java    # Small native ad view
└── util/
    ├── AdMobRateLimiter.java     # Intelligent rate limiting for AdMob
    ├── AdRepository.java         # Pre-fetching & caching engine
    ├── WaterfallManager.java     # Sequential backup network manager
    ├── NativeTemplateStyle.java  # Native ad template styling
    ├── TemplateView.java         # Custom AdMob native template
    ├── Tools.java                # Utility: network checks, Base64 decode, ad requests
    └── Constant.java             # Network key constants
```

---

## ⚡ Step-by-Step Setup Guide

### Step 1: Configure Repositories
In your `settings.gradle` (or project `build.gradle`), add the required maven repositories:

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

### Step 2: Add Dependencies
In your app-level `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.partharoypc:adglide:1.2.0'
    
    // Choose your desired Ad Networks:
    implementation 'com.google.android.gms:play-services-ads:25.0.0'   // AdMob
    implementation 'com.facebook.android:audience-network-sdk:6.21.0'  // Meta
    implementation 'com.applovin:applovin-sdk:13.6.0'                  // AppLovin
    implementation 'com.startapp:inapp-sdk:5.2.6'                      // StartApp
    implementation 'com.wortise:android-sdk:1.7.2'                     // Wortise
    implementation 'com.unity3d.ads:unity-ads:4.16.5'                  // Unity
    implementation 'com.ironsource.sdk:mediationsdk:8.4.0'             // IronSource
    
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
            .testMode(true)       // Development only — set false for production!
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
    .load(new OnShowAdCompleteListener() {
        @Override
        public void onShowAdComplete() {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    });
```

---

### 2. Banner & Medium Rectangle Ads

**XML Layout:**
```xml
<FrameLayout
    android:id="@+id/ad_mob_banner_view_container"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

**Banner:**
```java
new BannerAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.META, AdGlideNetwork.STARTAPP)
    .adMobId("ca-app-pub-3940256099942544/2014213617")
    .metaId("YOUR_META_PLACEMENT_ID")
    .collapsible(true)    // High-CTR collapsible banners (AdMob only)
    .darkTheme(true)
    .build()
    .load();
```

**Medium Rectangle (300×250):**
```java
new MediumRectangleAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.META)
    .adMobId("YOUR_ADMOB_MREC_ID")
    .build()
    .load();
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
    .show(this);
```

---

### 4. Native Ads

AdGlide offers **5 specialized native ad builders** for different UI contexts:

| Builder | Use Case |
| :--- | :--- |
| `NativeAd.Builder` | Standard Activity layouts |
| `NativeAdFragment.Builder` | Fragment-based layouts |
| `NativeAdView.Builder` | Custom View integration |
| `NativeAdViewHolder` | RecyclerView items |
| `NativeAdViewPager.Builder` | ViewPager / TabLayout |

**XML Layout:**
Use one of the predefined layout containers (automatically handles ViewStubs for lazy inflation):
```xml
<include layout="@layout/adglide_view_native_ad_medium" />
```

Available layout variants:  
`adglide_view_native_ad_small`, `adglide_view_native_ad_medium`, `adglide_view_native_ad_news`, `adglide_view_native_ad_radio`, `adglide_view_native_ad_video_small`, `adglide_view_native_ad_video_large`, `adglide_view_native_ad_large`

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
    .style(AdGlideNativeStyle.NEWS)
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

### Pre-Fetching (AdRepository)
Silently pre-fetch ads in your Splash screen so they're instantly available later:

```java
// Splash Activity — download ad to memory
AdRepository.getInstance().preloadInterstitial(
    this, AdGlideNetwork.ADMOB.getValue(), "YOUR_ADMOB_AD_UNIT_ID");

// Main Activity — instant display from cache
new InterstitialAd.Builder(this)
    .network(AdGlideNetwork.ADMOB)
    .adMobId("YOUR_ADMOB_AD_UNIT_ID")
    .build().load().show(this);

// Memory cleanup
@Override
protected void onDestroy() {
    super.onDestroy();
    AdRepository.getInstance().clearCache();
}
```

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
- **Waterfall Fail-Safe** — If a device goes offline mid-waterfall, the SDK gracefully stops.
- **Rate Limiting** — `AdMobRateLimiter` applies exponential cooldown to failing ad units.

---

## 🛡️ ProGuard / R8 Rules

Add these to your `proguard-rules.pro` for production builds:

```proguard
# AdGlide SDK
-keep public class com.partharoypc.adglide.** { *; }
-keep interface com.partharoypc.adglide.util.On*Listener { *; }

# AdMob
-keep class com.google.android.gms.ads.** { *; }

# Meta Audience Network
-keep class com.facebook.ads.** { *; }

# AppLovin
-keep class com.applovin.** { *; }

# StartApp
-keep class com.startapp.** { *; }
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile,
    LineNumberTable, *Annotation*, EnclosingMethod

# Wortise
-keep class com.wortise.** { *; }
```

---

## 📋 Technical Specifications

| Property | Value |
| :--- | :--- |
| **SDK Version** | 1.2.0 |
| **Min SDK** | 21 (Android 5.0) |
| **Target SDK** | 35 |
| **Compile SDK** | 35 |
| **Java Version** | 17 |
| **Build System** | Gradle 9.3.0 |
| **AndroidX** | Required |
| **Distribution** | JitPack (`com.github.partharoypc:adglide:1.2.0`) |

---

## 🤝 Support & Community
Developed with ❤️ by **[Partha Roy](https://github.com/partharoypc)**.

If this SDK saved you hours of integration headaches, please consider leaving a ⭐ on GitHub!

For bugs, feature requests, or custom mediation integrations, please [open an issue](https://github.com/partharoypc/AdGlide/issues).

---
*AdGlide SDK is MIT Licensed. © 2026 Partha Roy.*
