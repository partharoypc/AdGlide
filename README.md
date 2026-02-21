![AdGlide Banner](assets/banner.png)

# AdGlide SDK 🚀
### *The Premium Mediation Wrapper for High-Performance Android Apps*

[![Version](https://img.shields.io/badge/Version-1.0.0-blue.svg)](https://github.com/partharoypc/AdGlide)
[![SDK Support](https://img.shields.io/badge/Android-21%2B-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**AdGlide** is a powerhouse mediation wrapper designed for developers who value speed, simplicity, and performance. Eliminate the boilerplate of multi-network integration and ship your app with a "Just Copy-Paste" technical architecture.

---

## ⚡ 60-Second Quick Start

### 1. Root `settings.gradle`
Add JitPack and Network-specific repositories:
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

### 2. App `build.gradle`
AdGlide is ultra-lightweight. It **DOES NOT** bundle ad networks by default. You ONLY pull in the networks you actually use.

```gradle
dependencies {
    implementation 'com.github.partharoypc:adglide:1.0.0'
    
    // 🚀 ADD ONLY THE NETWORKS YOU PLAN TO USE:
    implementation 'com.google.android.gms:play-services-ads:23.6.0'
    implementation 'com.google.android.ump:user-messaging-platform:3.1.0' // GDPR requirements
    implementation 'com.facebook.android:audience-network-sdk:6.18.0'
    implementation 'com.google.ads.mediation:facebook:6.18.0.0'
    implementation 'com.applovin:applovin-sdk:13.0.1'
    implementation 'com.unity3d.ads:unity-ads:4.12.5'
    implementation 'com.ironsource.sdk:mediationsdk:8.4.0'
    implementation 'com.startapp:inapp-sdk:5.1.0'
    implementation 'com.wortise:android-sdk:1.7.0'
}
```

### 3. Global Initialization
Initialize once in your `SplashActivity.onCreate()` or `Application` class.
```java
new AdNetwork.Initialize(this)
    .setAdStatus("1") // "1" = ON, "0" = OFF
    .setAdNetwork("admob") // Primary Network
    .setBackupAdNetworks("meta", "applovin") // Sequential Waterfall
    .setAdMobAppId("ca-app-pub-3940256099942544~3347511713")
    .setDebug(true)
    .build();
```

---

### 📊 Advanced Capability Matrix

| Ad Format | AdMob | Meta | Unity | AppLovin | IronSource | StartApp | Wortise |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Direct Use** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Bidding Support**| ✅ | ✅ | ❌ | ✅ | ✅ | ❌ | ❌ |
| **Waterfall** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Banner** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Interstitial** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Native** | ✅ | ✅ | ❌ | ✅ | ❌ | ✅ | ✅ |
| **Rewarded** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **App Open** | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ | ✅ |

---

## 🌐 Initialization Modes & Mediation

AdGlide supports three distinct integration patterns to maximize your yield and simplify your code.

### 1. Direct Use (Standalone)
Target a single network exclusively.
```java
.setAdNetwork("admob") // admob, meta, applovin, unity, ironsource, startapp, wortise
```

### 2. Bidding Mediation (High-Yield)
Utilize real-time bidding for supported networks.
```java
// Supported modes: META_BIDDING_ADMOB, META_BIDDING_APPLOVIN_MAX, META_BIDDING_IRONSOURCE
.setAdNetwork("META_BIDDING_ADMOB") 
```

### 3. Sequential Waterfall (Fail-Safe)
The ultimate fallback system. If the primary network fails to fill, AdGlide cycles through your list automatically.
```java
.setAdNetwork("admob")
.setBackupAdNetworks("meta", "applovin_max", "unity", "startapp")
```

---

## 🚀 All Possible Features & Advanced Config

| Feature | Description | Implementation |
| :--- | :--- | :--- |
| **AdRepository** | Zero-latency pre-fetching singleton | `AdRepository.getInstance().preload(...)` |
| **Triple-Base64** | Industrial-grade ID obfuscation | `Tools.decode("...")` |
| **Adaptive Sizing**| Dynamic banner height calculation | `Tools.getAdSize(activity)` |
| **Frequency Capping**| Show ads every X times to improve UX | `.setInterval(3)` |
| **Collapsible Banner**| AdMob's high-CTR banner format | `.setIsCollapsibleBanner(true)` |
| **GDPR/UMP** | Modern consent management support | `new GDPR(activity).update(...)` |
| **Dark Theme** | Auto-stying for native ads | `.setNativeAdBackgroundColor(...)` |
| **Fluid Layouts** | News, Medium, Radio, Stream, etc. | `.setNativeAdStyle("news")` |
| **Remote Toggle** | Turn ads on/off via server JSON | `RemoteConfigHelper` |

---

## 📺 Universal Ad Formats

### 1. Banner & Medium Rectangle
**XML Layout:**
```xml
<include layout="@layout/adglide_view_banner_ad" />
```

**Java Implementation:**
```java
new BannerAd.Builder(this)
    .setAdMobBannerId("ca-app-pub-3940256099942544/6300978111")
    .setIsCollapsibleBanner(true) // Native AdMob Collapsible!
    .build();
```

---

### 2. Interstitial Ads (With Frequency Capping)
```java
InterstitialAd.Builder interstitialAd = new InterstitialAd.Builder(this)
    .setAdMobInterstitialId("ca-app-pub-3940256099942544/1033173712")
    .setInterval(3) // Shows only on every 3rd call!
    .build();

interstitialAd.show();
```

---

### 3. Native Ads (Fluid Styles)
**XML Layout:**
```xml
<include layout="@layout/adglide_view_native_ad_medium" />
```

**Java Implementation:**
```java
new NativeAd.Builder(this)
    .setAdMobNativeId("ca-app-pub-3940256099942544/2247696110")
    .setNativeAdStyle("news") // news, medium, small, radio, stream
    .setNativeAdBackgroundColor("#FFFFFF", "#212121") // Light & Dark support
    .build();
```

---

## 🚀 Advanced Performance Features

### 1. AdRepository (Zero-Latency Caching)
Kill the "loading spinner" by pre-fetching ads in the background.

```java
// Pre-load in Splash or Activity Background
AdRepository.getInstance().preloadInterstitial(context, "admob", "YOUR_ID");

// The builder will automatically pick it up from cache
new InterstitialAd.Builder(activity)
    .setAdMobInterstitialId("YOUR_ID")
    .build();
```

### 2. Sequential Waterfall Logic
AdGlide uses a smart **Waterfall Manager**. If the Primary fails, it instantly tries the first backup, then the second, and so on, until an ad is filled.

```java
.setBackupAdNetworks("meta", "applovin", "unity", "startapp") // Intelligent chaining
```

### 3. Triple-Base64 Security
Keep your Ad IDs safe from reverse-engineering and simple string analysis.
```java
String safeId = Tools.decode("TWpZNE5UYzVOekk1TkRRME5nPT0="); // Decodes internally
```

---

## 🎨 Elite UI Integrations

### 1. RecyclerView Lists
Inject native ads directly into your scrolling lists with a single method call inside `onBindViewHolder`.

```java
NativeAdViewHolder.loadNativeAd(
    context, status, placement, primary, backup, 
    admobId, metaId, null, null, null, 
    false, false, "news", R.color.white, R.color.black
);
```

### 2. Fragments & ViewPagers
AdGlide provides specialized builders for components that live outside the standard Activity lifecycle.
- **`NativeAdFragment.Builder`**: For fragments.
- **`NativeAdViewPager.Builder`**: For onboarding slides.

---

## 🛡 Production Hardening (ProGuard/R8)
Add these rules to your `proguard-rules.pro` to ensure the SDK functions correctly after code shrinking.

```proguard
-keep public class com.partharoypc.adglide.format.** { public *; }
-keep public class com.partharoypc.adglide.util.** { public *; }
-keep interface com.partharoypc.adglide.util.On*Listener { *; }
```

---


---

## 📘 Full API Reference

### 🏗️ Ad Format Builders

#### `BannerAd.Builder`
The foundational builder for all banner integrations.

| Method | Description |
| :--- | :--- |
| `setAdStatus(String)` | Set to `AD_STATUS_ON` or `AD_STATUS_OFF` (from `Constant`). |
| `setAdNetwork(String)` | Sets the primary network (e.g., `ADMOB`, `META`, `UNITY`). |
| `setBackupAdNetwork(String)` | Sets a single fallback network. |
| `setBackupAdNetworks(String...)` | Configures a sequential waterfall (e.g., `ADMOB, META, UNITY`). |
| `setAdMobBannerId(String)` | Google AdMob / GAM Banner ID. |
| `setMetaBannerId(String)` | Meta Audience Network Placement ID. |
| `setUnityBannerId(String)` | Unity Ads Placement ID. |
| `setAppLovinBannerId(String)` | AppLovin MAX / Discovery ID. |
| `setironSourceBannerId(String)` | IronSource Placement Name. |
| `setWortiseBannerId(String)` | Wortise Ad Unit ID. |
| `setPlacementStatus(int)` | Remote toggle for this specific placement (`1` = ON). |
| `setDarkTheme(boolean)` | Enables specialized dark UI for Native-style banners. |
| `setIsCollapsibleBanner(boolean)` | **(Exclusive)** Enables AdMob Collapsible Banner support. |
| `setLegacyGDPR(boolean)` | Forces legacy GDPR dialog instead of Google UMP. |
| `build()` | Initializes and starts loading the ad. |

#### `InterstitialAd.Builder`
High-performance full-screen ad manager with built-in caps.

| Method | Description |
| :--- | :--- |
| `setInterval(int)` | Show ad every X triggers (e.g., `3` means show on 3rd, 6th...). |
| `setAdMobInterstitialId(String)` | AdMob Interstitial Unit ID. |
| `setMetaInterstitialId(String)` | Meta Interstitial Placement ID. |
| `setUnityInterstitialId(String)` | Unity Interstitial Placement ID. |
| `setAppLovinInterstitialId(String)`| MAX Interstitial ID. |
| `setironSourceInterstitialId(String)`| IronSource Interstitial Name. |
| `setWortiseInterstitialId(String)` | Wortise Interstitial ID. |
| `build()` | Starts pre-fetching the interstitial ad. |
| `show()` | Displays the ad if loaded and interval criteria met. |

#### `NativeAd.Builder`
Advanced customization for seamless UI integration.

| Method | Description |
| :--- | :--- |
| `setNativeAdStyle(String)` | Choose from `news`, `medium`, `small`, `radio`, `stream`. |
| `setNativeAdBackgroundColor(String, String)` | Custom background code for Light & Dark modes. |
| `setPadding(int, int, int, int)` | Precise padding control for the ad container. |
| `setMargin(int, int, int, int)` | Precise margin control for the ad container. |
| `setAdMobNativeId(String)` | AdMob Native Advanced ID. |
| `setMetaNativeId(String)` | Meta Native Ad ID. |
| `setAppLovinNativeId(String)` | Max Native Ad ID. |
| `setWortiseNativeId(String)` | Wortise Native Unit ID. |
| `build()` | Fetches and renders the ad into the provided container. |

#### `RewardedAd.Builder`
Reward-based monetization with full lifecycle callbacks.

| Method | Description |
| :--- | :--- |
| `setAdMobRewardedId(String)` | AdMob Rewarded Unit ID. |
| `setMetaRewardedId(String)` | Meta Rewarded Placement ID. |
| `setUnityRewardedId(String)` | Unity Rewarded Placement ID. |
| `setApplovinMaxRewardedId(String)`| MAX Rewarded ID. |
| `setironSourceRewardedId(String)`| IronSource Rewarded Name. |
| `build(OnRewardedAdCompleteListener, OnRewardedAdDismissedListener)` | Pre-loads with listeners. |
| `show(OnRewardedAdCompleteListener, OnRewardedAdDismissedListener, OnRewardedAdErrorListener)` | Show with state tracking. |

---

### ⚡ Performance & Security Utils

#### `AdRepository` (Zero-Latency Cache)
Standalone singleton for pre-caching ads for high-traffic entry points.

- `getInstance().preloadInterstitial(Context, String network, String id)`: Background load.
- `getInstance().isInterstitialAvailable(String network, String id)`: Check status.
- `getInstance().getInterstitial(String network, String id)`: Fetch from cache.

#### `Tools` (Obfuscation & Layout)
- `decode(String)`: Decodes Triple-Base64 IDs (Recommended for security).
- `getAdSize(Activity)`: Dynamically calculates Adaptive Banner size based on screen width.

#### `Constant` (Network Identifiers)
Use these strings for `setAdNetwork`:
`ADMOB`, `META`, `UNITY`, `APPLOVIN`, `APPLOVIN_MAX`, `IRONSOURCE`, `STARTAPP`, `WORTISE`.

---

## 🤝 Support & Community

Developed with ❤️ by **[Partha Roy](https://github.com/partharoypc)**.

For bugs, feature requests, or custom mediation integrations, please open an issue or contact the developer directly.

---
*AdGlide is MIT Licensed. © 2024 Partha Roy.*

