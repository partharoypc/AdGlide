# AdGlide SDK 🚀
### *The "Just Copy-Paste" Technical Encyclopedia*

[![Version](https://img.shields.io/badge/Version-1.0.0-blue.svg)](https://github.com/partharoypc/AdGlide)
[![SDK Support](https://img.shields.io/badge/Android-21%2B-green.svg)](https://developer.android.com)

**AdGlide** is a powerhouse mediation wrapper. This guide is built for speed—skip the fluff, grab the code, and ship your app.

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
```gradle
dependencies {
    implementation 'com.github.partharoypc:adglide:1.0.0'
}
```

### 3. Global Initialization
Stick this in your `SplashActivity.onCreate()` and forget about it.
```java
new AdNetwork.Initialize(this)
    .setAdStatus("1")
    .setAdNetwork("admob") // admob, meta, applovin, unity, ironsource, startapp, wortise
    .setBackupAdNetworks("meta", "applovin") // Waterfall fallback order
    .setAdMobAppId("ca-app-pub-3940256099942544~3347511713")
    .setWortiseAppId("YOUR_WORTISE_ID")
    .setAppLovinSdkKey("YOUR_SDK_KEY")
    .setStartappAppId("YOUR_STARTAPP_ID")
    .setUnityGameId("YOUR_UNITY_ID")
    .setironSourceAppKey("YOUR_IRONSOURCE_KEY")
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
| **Rewarded Interstitial** | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |

---

### 🌐 Global Initialization Guide

Initialize the SDK in your `SplashActivity` or `MainActivity`. AdGlide handles the heavy lifting of SDK warm-ups.

```java
new AdNetwork.Initialize(this)
    .setAdStatus("1") // "1" for ON, "0" for OFF
    .setAdNetwork("admob") // Primary Network: admob, meta, applovin, unity, ironsource, startapp, wortise
    .setBackupAdNetworks("meta", "applovin") // Multiple fallbacks for Waterfall Logic
    .setAdMobAppId("ca-app-pub-3940256099942544~3347511713")
    .setWortiseAppId("YOUR_WORTISE_APP_ID")
    .setAppLovinSdkKey("YOUR_SDK_KEY")
    .setStartappAppId("YOUR_STARTAPP_ID")
    .setUnityGameId("YOUR_UNITY_GAME_ID")
    .setironSourceAppKey("YOUR_IRONSOURCE_APP_KEY")
    .setDebug(true) // Set to false in Production
    .build();
```

---

## 📺 Fast-Track Ad Formats

### 1. Banner & Medium Rectangle
**XML Layout:**
```xml
<!-- For Banner & Medium Rectangle -->
<include layout="@layout/adglide_view_banner_ad" />
```

**Java Implementation:**
```java
new BannerAd.Builder(this)
    .setAdMobBannerId("ca-app-pub-3940256099942544/6300978111")
    .setMetaBannerId("YOUR_META_ID")
    .setAppLovinBannerId("YOUR_MAX_ID")
    .setAppLovinBannerZoneId("YOUR_ZONE_ID") // For Discovery
    .setIsCollapsibleBanner(true) // AdMob Collapsible!
    .build();
```

---

### 2. Interstitial Ads (With Frequency Capping)
```java
InterstitialAd.Builder interstitialAd = new InterstitialAd.Builder(this)
    .setAdMobInterstitialId("ca-app-pub-3940256099942544/1033173712")
    .setAppLovinInterstitialZoneId("YOUR_ZONE_ID") // For Discovery
    .setInterval(3) // Shows only on every 3rd try!
    .build();

// When you want to show it:
interstitialAd.show();
```

---

### 3. Native Ads (Modern Styles)
**XML Layout:**
```xml
<include layout="@layout/adglide_view_native_ad_medium" />
```

**Java Implementation:**
```java
new NativeAd.Builder(this)
    .setAdMobNativeId("ca-app-pub-3940256099942544/2247696110")
    .setNativeAdStyle("news") // news, medium, small, radio, video_small, stream
    .setNativeAdBackgroundColor("#FFFFFF", "#212121")
    .build();
```

---

### 4. Rewarded & Rewarded Interstitial
```java
new RewardedAd.Builder(this)
    .setAdMobRewardedId("ca-app-pub-3940256099942544/5224354917")
    .build(new OnRewardedAdCompleteListener() {
        @Override public void onRewardedAdComplete() { /* Reward user */ }
    }, new OnRewardedAdDismissedListener() {
        @Override public void onRewardedAdDismissed() { /* Next step */ }
    });

// 4b. Rewarded Interstitial (AdMob Only)
new RewardedInterstitialAd.Builder(this)
    .setAdMobRewardedInterstitialId("YOUR_ID")
    .build(new OnRewardedAdCompleteListener() {
        @Override public void onRewardedAdComplete() { /* Reward user */ }
    }, new OnRewardedAdDismissedListener() {
        @Override public void onRewardedAdDismissed() { /* Next step */ }
    });
```

---

### 5. App Open Ads (Zero Config)
Registers lifecycle automatically. Stick this in your `Application` class.
```java
new AppOpenAd.Builder(this)
    .setAdMobAppOpenId("ca-app-pub-3940256099942544/9257395921")
    .build();
```

---

## 🚀 Advanced Ad Loading

For more granular control over ad loading, AdGlide provides specific methods to load ads manually. This is useful for pre-loading ads or handling custom display logic.

### 1. Pre-loading Interstitial Ads
```java
InterstitialAd.Builder interstitialAd = new InterstitialAd.Builder(this)
    .setAdMobInterstitialId("ca-app-pub-3940256099942544/1033173712")
    .setInterval(3)
    .build();

// To pre-load the ad without showing it immediately:
interstitialAd.load();

// Later, when you want to show it:
if (interstitialAd.isLoaded()) {
    interstitialAd.show();
}
```

### 2. Pre-loading Rewarded Ads
```java
RewardedAd.Builder rewardedAd = new RewardedAd.Builder(this)
    .setAdMobRewardedId("ca-app-pub-3940256099942544/5224354917")
    .build(new OnRewardedAdCompleteListener() {
        @Override public void onRewardedAdComplete() { /* Reward user */ }
    }, new OnRewardedAdDismissedListener() {
        @Override public void onRewardedAdDismissed() { /* Next step */ }
    });

// To pre-load the ad:
rewardedAd.load();

// Later, when you want to show it:
if (rewardedAd.isLoaded()) {
    rewardedAd.show();
}

### 3. High-Performance Caching (Zero Latency)
Kill the "loading spinner" by pre-fetching ads in the background.

```java
// Pre-load in Splash or Activity Background
AdRepository.getInstance().preloadInterstitial(context, "admob", "YOUR_ID");

// Later, the builder will automatically pick it up from cache
new InterstitialAd.Builder(activity)
    .setAdMobInterstitialId("YOUR_ID")
    .build();
```

---

## 🛠 Builder Fast-Track Extras

Need more control? These methods work across most builders:

| Method | Type | Description |
| :--- | :---: | :--- |
| `setPlacementStatus(int)` | `0/1` | Remote toggle! If `0`, ad won't load. |
| `setDarkTheme(boolean)` | `bool` | Force high-contrast dark mode styles. |
| `setLegacyGDPR(boolean)`| `bool` | Attach legacy consent extras. |
| `setAdMobNativeId(...)` | `String`| Use for both Native AND Banner builders. |

---

## 📚 Network Setup Cheatsheet

| Network | Manifest Requirement (Copy-Paste) | Key Method |
| :--- | :--- | :--- |
| **AdMob** | `<meta-data android:name="com.google.android.gms.ads.APPLICATION_ID" android:value="ca-app-pub-xxx~xxx"/>` | `setAdMobAppId` |
| **AppLovin**| `<meta-data android:name="applovin.sdk.key" android:value="YOUR_LONG_SDK_KEY"/>` | `setAppLovinSdkKey` |
| **IronSource**| *Requires Activity Lifecycle (onResume/onPause)* | `setironSourceAppKey` |
| **StartApp** | *No manifest changes needed* | `setStartappAppId` |
| **Unity** | *No manifest changes needed* | `setUnityGameId` |

---

## 🔄 Waterfall Logic
AdGlide uses a **Sequential Waterfall**. If the Primary fails, it instantly tries the first backup, then the second, and so on.

```java
.setBackupAdNetworks("meta", "applovin", "unity") // Chains them all!
```

---

## 🌐 Tech Manual: Deep-Dives

### 1. Remote Config (The "Switch")
Fetch your JSON live to toggle networks or IDs without an app update.

```java
String CONFIG_URL = "https://yourserver.com/ads.json";

new RemoteConfigHelper().fetchConfig(CONFIG_URL, new RemoteConfigHelper.ConfigListener() {
    @Override
    public void onConfigFetched(JSONObject config) {
        // Build your AdNetwork.Initialize here using the JSON data
    }

    @Override
    public void onConfigFailed(String error) {
        // Fallback to local defaults
    }
});
```

---

### 2. GDPR (Consent Management)

## 🎨 Elite UI Integrations

### 1. Fragments (`NativeAdFragment`)
Perfect for `BottomNavigationView` or `TabLayout` where ads need to live inside a fragment.

```java
// Inside your Fragment's onViewCreated
new NativeAdFragment.Builder(getActivity())
    .setView(view) // Pass the root view of fragment
    .setAdMobNativeId("YOUR_ID")
    .setNativeAdStyle("medium")
    .build();
```

---

### 2. Slide Ads (`NativeAdViewPager`)
Ideal for onboarding screens or image galleries. Requires a view container.

```java
new NativeAdViewPager.Builder(activity, view)
    .setAdMobNativeId("YOUR_ID")
    .build();
```

---

### 3. RecyclerView Lists (`NativeAdViewHolder`)
The ultimate way to inject ads into scrolling lists. Use this static method inside your Adapter.

```java
// Inside your RecyclerView.Adapter.onBindViewHolder
NativeAdViewHolder.loadNativeAd(
    context, 
    "1",          // Ad Status
    1,            // Placement Status
    "admob",      // Primary Network
    "meta",       // Backup
    "ADMOB_ID", 
    "META_ID",
    null, null, null, // Others
    false, false, // Dark theme, Legacy GDPR
    "news",       // Style
    R.color.white, R.color.black
);
```

---

### 4. GDPR (Consent Management)
Pass `true` for `isDebug` in development to force the consent form.
```java
new GDPR(activity).updateGDPRConsentStatus("admob", true, false);
```

**The MD5 Formula**:
The SDK generates this ID internally using `md5(ANDROID_ID).toUpperCase()`.
```java
// How it's done in GDPR.java:
String androidId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
String deviceId = md5(androidId).toUpperCase();
```

**Simulating Regions**:
Force the GDPR prompt regardless of your location:
```java
debugSettings = new ConsentDebugSettings.Builder(activity)
    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
    .addTestDeviceHashedId("YOUR_MD5_ID")
    .build();
```

### 5. Ninja Utilities (`Tools.java`)
Power-user methods for security and layout:

- **Triple-Base64**: `Tools.decode("...")` for maximum ID safety.
- **Adaptive Sizing**: `Tools.getAdSize(activity)` calculates the perfect height.
- **Collapsible Toggle**: `Tools.getAdRequest(true)` enables the bottom flag instantly.

---

### 4. Troubleshooting Quick-Fix
| Symptom | Cause | Solution |
| :--- | :--- | :--- |
| **No Ads** | ID Mismatch | Double check your `APPLICATION_ID` in AndroidManifest. |
| **App Open Crash** | Activity Context | Ensure you pass the current `Activity` to the builder. |
| **IronSource Crash** | Lifecycle | Did you forget `IronSource.onResume()`? |

---

### 📖 API Cheat-Sheet

| Method | Description | Example |
| :--- | :--- | :--- |
| `setAdStatus("1")` | Toggle Ads | `1`=ON, `0`=OFF |
| `setAdNetwork("admob")` | Primary Network | `admob`, `meta`, `applovin` |
| `setBackupAdNetworks(...)` | Sequential Waterfall | `("meta", "applovin")` |
| `setNativeAdStyle("news")` | Meta/AdMob Layout | `news`, `medium`, `small`, `radio`, `video_small`, `stream` |
| `setAppLovinBannerZoneId` | Discovery Ads | Legacy Zone Support |
| `setInterval(3)` | Frequency Capping | Show every 3rd time |
| `setIsCollapsible(true)` | AdMob Feature | Collapsible Banner |
| `RewardedInterstitial` | Format | Hybrid Reward Format |

---

## 🧪 Testing ID Table (AdMob)

| Format | Test Unit ID |
| :--- | :--- |
| **Banner** | `ca-app-pub-3940256099942544/6300978111` |
| **Interstitial** | `ca-app-pub-3940256099942544/1033173712` |
| **Rewarded** | `ca-app-pub-3940256099942544/5224354917` |
| **App Open** | `ca-app-pub-3940256099942544/9257395921` |
| **Native** | `ca-app-pub-3940256099942544/2247696110` |

---

## 🛡 Production Hardening (ProGuard/R8)
If you enable `minifyEnabled true`, you **MUST** add these rules to your `proguard-rules.pro` to prevent the SDK from being stripped.

```proguard
# AdGlide SDK Core
-keep public class com.partharoypc.adglide.format.** { public *; }
-keep public class com.partharoypc.adglide.util.** { public *; }

# Ad Networks & Mediation
-keep class com.google.android.gms.ads.** { *; }
-keep class com.facebook.ads.** { *; }

# Prevent stripping of ad callback interfaces
-keep interface com.partharoypc.adglide.util.On*Listener { *; }
```

---

## 🛡 Security & Triple-Base64 Obfuscation
The `Tools` class provides a `decode` method that uses a custom **Triple-Base64** algorithm to keep your Ad IDs safe from simple string analysis:

```java
String safeId = Tools.decode("TWpZNE5UYzVOekk1TkRRME5nPT0="); 
// Decodes to a real AdMob Unit ID
```

---

## 🤝 Support & Credits

Developed by **[Partha Roy](https://github.com/partharoypc)**.
For commercial support or custom integrations, please contact the developer via GitHub.
```
