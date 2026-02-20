# AdGlide SDK - The Absolute Definitive Technical Guide 🚀

[![Version](https://img.shields.io/badge/Version-1.0.0-blue.svg)](https://github.com/partharoypc/AdGlide)
[![SDK Support](https://img.shields.io/badge/Android-21%2B-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-orange.svg)](LICENSE)

AdGlide is an enterprise-grade mediation wrapper for Android. This document is the **Absolute Source of Truth** for the SDK, providing 100% of the information required for high-level integration across all 7 supported ad networks.

---

## 🏗 Ad Networks Capability Matrix

| Network | Key | Banner | Interstitial | Rewarded | Native | App Open |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: |
| **AdMob** | `admob` | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Meta (FAN)**| `meta` | ✅ | ✅ | ✅ | ✅ | ✅* |
| **Unity Ads** | `unity` | ✅ | ✅ | ✅ | ❌ | ❌ |
| **AppLovin MAX**| `applovin` | ✅ | ✅ | ✅ | ✅ | ✅ |
| **IronSource** | `ironsource`| ✅ | ✅ | ✅ | ❌ | ❌ |
| **StartApp** | `startapp` | ✅ | ✅ | ✅ | ⚠️ | ❌ |
| **Wortise** | `wortise` | ✅ | ✅ | ✅ | ⚠️ | ✅ |

*\*Meta App Open is typically handled via AdMob/MAX bidding.*

---

## 📦 Global Initialization Guide

Initialization is handled by the `AdNetwork.Initialize` builder. You MUST provide the specific App IDs/Keys for each network you intend to use.

### Network Initialization Keys
| Network | Method | Required ID Type |
| :--- | :--- | :--- |
| **AdMob** | `setAdMobAppId(id)` | `ca-app-pub-xxxxxxxx~xxxxxxxx` |
| **AppLovin**| `setAppLovinSdkKey(key)`| Long alphanumeric SDK Key |
| **IronSource**| `setironSourceAppKey(key)`| Your IronSource App Key |
| **StartApp**| `setStartappAppId(id)` | StartApp App ID |
| **Unity** | `setUnityGameId(id)` | Unity Game ID |
| **Wortise** | `setWortiseAppId(id)` | Wortise App ID |

---

## 📺 Ad Formats: Ultra-Detailed Reference

### 1. Banner Ads
AdGlide supports adaptive sizing and the AdMob-exclusive collapsible feature.

**Builder Options:**
- `setAdMobBannerId(String)`
- `setMetaBannerId(String)`
- `setUnityBannerId(String)`
- `setAppLovinBannerId(String)` (MAX)
- `setApplovinDiscBannerZoneId(String)` (Discovery)
- `setironSourceBannerId(String)`
- `setWortiseBannerId(String)`
- `setIsCollapsibleBanner(boolean)`: Enables bottom-collapsible banners (AdMob).

---

### 2. Interstitial Ads
Includes built-in **Frequency Capping** via intervals.

**Builder Options:**
- `setAdMobInterstitialId(String)`
- `setMetaInterstitialId(String)`
- `setUnityInterstitialId(String)`
- `setAppLovinInterstitialId(String)` (MAX)
- `setAppLovinInterstitialZoneId(String)` (Discovery)
- `setironSourceInterstitialId(String)`
- `setWortiseInterstitialId(String)`
- **`setInterval(int)`**: If set to `3`, the ad only shows on every 3rd `showInterstitialAd()` call.

---

### 3. Native Ads
Supports AdMob Templates and Meta Custom Layouts.

**Supported Meta Styles:**
- `STYLE_NEWS`: Large image/video feed.
- `STYLE_MEDIUM`: Classic rectangle layout.
- `STYLE_VIDEO_SMALL`: Small video focused.
- `STYLE_VIDEO_LARGE`: Large video focused.
- `STYLE_RADIO`: Minimalist icon-based.

**AdMob Custom Styling:**
```java
NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
    .withMainBackgroundColor(new ColorDrawable(Color.WHITE))
    .withPrimaryTextTypeface(Typeface.DEFAULT_BOLD)
    .build();

new NativeAd.Builder(activity)
    .setNativeTemplateStyle(styles)
    .build();
```

---

### 4. Rewarded Ads
Incentivized video ads with complete callback lifecycle.

**Callback Interfaces:**
- `OnRewardedAdCompleteListener`: Triggered when the user successfully earns the reward.
- `OnRewardedAdDismissedListener`: Triggered when the ad is closed.
- `OnRewardedAdErrorListener`: Triggered if the ad fails to show.

---

### 5. App Open Ads
Optimized for cold starts or background-to-foreground transitions.

**Implementation Modes:**
- **Immediate**: Load and show directly on the splash screen.
- **Lifecycle Based**: Registers `ProcessLifecycleOwner` to automatically show ads when the user returns to the app.

---

## 🌐 Ad Network Specific Deep Dive

### 🟦 AdMob
- **Adaptive Sizing**: Automatically calculates the best height (using `Tools.getAdSize`).
- **Optimization**: Manifest flags `OPTIMIZE_INITIALIZATION` and `OPTIMIZE_AD_LOADING` are recommended.

### 🟧 Meta (Audience Network)
- **Bidding**: Fully supported via AdMob, AppLovin, and IronSource mediation.
- **Caching**: Automated background caching for higher fill rates.

### 🟩 IronSource
- **Manual Management**: You **MUST** call `IronSource.onResume(activity)` and `IronSource.onPause(activity)` in your activity lifecycle.
- **Integration**: AdGlide handles `LevelPlay` listener registration automatically.

### 🟥 StartApp
- **Splash Settings**: StartApp Splash ads are disabled by default within this SDK to prevent double-splash issues.

---

## � Security & Utilities

### The `Tools` Class
- **Triple-Base64 Decoding**: Use `Tools.decode(String)` to obfuscate your Ad IDs in common strings.
- **Adaptive Logic**: `Tools.getAdSize(activity)` handles both Android 11+ `WindowMetrics` and legacy `DisplayMetrics`.

### `AdRepository`
A singleton cache for Interstitial ads. Pre-load ads in your Splash or main activity:
```java
AdRepository.getInstance().preloadInterstitial(activity, "admob", "YOUR_ID");
```

---

## 🛡 ProGuard / R8 Reference

Exhaustive rules for the SDK:

```pro
# AdGlide Internals
-keep public class com.partharoypc.adglide.** { public *; }
-keep interface com.partharoypc.adglide.util.On*Listener { *; }

# Google & AdMob
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.mediation.** { *; }

# AppLovin
-keep class com.applovin.** { *; }

# Meta / Facebook
-keep class com.facebook.ads.** { *; }

# Unity Ads
-keep class com.unity3d.ads.** { *; }
-keep class com.unity3d.services.** { *; }

# IronSource
-keep class com.ironsource.mediationsdk.** { *; }

# Wortise
-keep class com.wortise.ads.** { *; }
```

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

## 🤝 Support & Credits

Developed by **[Partha Roy](https://github.com/partharoypc)**.
For commercial support or custom integrations, please contact the developer via GitHub.
