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

### 📊 Ad Networks Capability Matrix

| Ad Format | AdMob | Meta | Unity | AppLovin | IronSource | StartApp | Wortise |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Banner** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Interstitial** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Native** | ✅ | ✅ | ❌ | ✅ | ❌ | ✅ | ✅ |
| **Rewarded** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **App Open** | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ | ✅ |
| **Medium Rectangle** | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |

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

## 🤝 Support & Community

Developed with ❤️ by **[Partha Roy](https://github.com/partharoypc)**.

For bugs, feature requests, or custom mediation integrations, please open an issue or contact the developer directly.

---
*AdGlide is MIT Licensed. © 2024 Partha Roy.*
