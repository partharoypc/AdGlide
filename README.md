# AdGlide SDK - The Ultimate Technical Encyclopedia 🚀

[![Version](https://img.shields.io/badge/Version-1.0.0-blue.svg)](https://github.com/partharoypc/AdGlide)
[![SDK Support](https://img.shields.io/badge/Android-21%2B-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-orange.svg)](LICENSE)

AdGlide is an enterprise-grade mediation wrapper for Android. This document is the **Ultimate Technical Encyclopedia**, providing 100% of the information required for high-level integration, troubleshooting, and optimization across all 7 supported ad networks.

---

## 🛠 5-Step Master Integration Guide

Follow this sequence for a perfect implementation:

1.  **Repository Setup**: Add JitPack to your `settings.gradle` or `build.gradle` (Project).
2.  **Dependency Injection**: Add `implementation 'com.github.partharoypc:adglide:1.0.0'` to your `build.gradle` (Module).
3.  **Manifest Configuration**: Add the mandatory `<meta-data>` tags for AdMob and AppLovin in your `AndroidManifest.xml`.
4.  **Global Initialization**: Call `AdNetwork.Initialize` in your Splash or Application class.
5.  **Ad Placement**: Use the respective `Builder` classes (Banner, Interstitial, etc.) to load and show ads.

---

## 🏗 Ad Networks Capability Matrix

| Network | Key | Integration Type | Banner | Interstitial | Rewarded | Native | App Open |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **AdMob** | `admob` | Direct | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Meta (FAN)**| `meta` | Direct + Bidding | ✅ | ✅ | ✅ | ✅ | ✅* |
| **Unity Ads** | `unity` | Direct | ✅ | ✅ | ✅ | ❌ | ❌ |
| **AppLovin MAX**| `applovin` | Direct | ✅ | ✅ | ✅ | ✅ | ✅ |
| **IronSource** | `ironsource`| Direct | ✅ | ✅ | ✅ | ❌ | ❌ |
| **StartApp** | `startapp` | Direct | ✅ | ✅ | ✅ | ⚠️ | ❌ |
| **Wortise** | `wortise` | Direct | ✅ | ✅ | ✅ | ⚠️ | ✅ |

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

## 📚 Ultimate Network Setup Encyclopedia

Every network integrated into AdGlide has been tuned for maximum stability. Below are the definitive setup requirements for each.

### 🟦 1. AdMob (Google)
- **SDK Version**: `23.6.0`
- **Manifest Requirement**:
  ```xml
  <meta-data
      android:name="com.google.android.gms.ads.APPLICATION_ID"
      android:value="ca-app-pub-xxxxxxxxxxxxxxxx~xxxxxxxxxx" />
  <meta-data
      android:name="com.google.android.gms.ads.AD_MANAGER_APP"
      android:value="true" />
  ```
- **Builder Method**: `setAdMobAppId(String id)`
- **Pro Tip**: Use `OPTIMIZE_INITIALIZATION` meta-data to speed up app cold starts.

### 🟧 2. Meta (Audience Network)
- **SDK Version**: `6.18.0`
- **Integration**: Pure bidding or direct implementation.
- **Bidding Support**: Works seamlessly via AdMob, AppLovin MAX, and IronSource LevelPlay.
- **Initialization**: Automated via `AdNetwork.Initialize`.

### 🟪 3. AppLovin (MAX & Discovery)
- **SDK Version**: `13.0.1`
- **Manifest Requirement**:
  ```xml
  <meta-data
      android:name="applovin.sdk.key"
      android:value="YOUR_LONG_SDK_KEY_HERE" />
  ```
- **Builder Method**: `setAppLovinSdkKey(String key)`
- **Mediation**: Use `APPLOVIN_MAX` for unified mediation.

### 🟩 4. IronSource (LevelPlay)
- **SDK Version**: `8.4.0`
- **Lifecycle Obligation**: **CRITICAL!** You must add these to your `Activity`:
  ```java
  @Override
  protected void onResume() {
      super.onResume();
      IronSource.onResume(this);
  }
  @Override
  protected void onPause() {
      super.onPause();
      IronSource.onPause(this);
  }
  ```
- **Builder Method**: `setironSourceAppKey(String key)`

### 🟥 5. Unity Ads
- **SDK Version**: `4.12.5`
- **Setup**: Requires a `Unity Game ID`.
- **Builder Method**: `setUnityGameId(String id)`
- **Behavior**: AdGlide handles internal listeners and test mode synchronization.

### ⭐ 6. StartApp
- **SDK Version**: `5.1.0`
- **Default Optimizations**:
  - Splash ads are **disabled** by default to prevent UX conflicts.
  - Return ads are **disabled** to maintain flow control.
- **Builder Method**: `setStartappAppId(String id)`

### 💎 7. Wortise
- **SDK Version**: `1.7.0`
- **Builder Method**: `setWortiseAppId(String id)`
- **Features**: Dedicated support for App Open and Native radio styles.

---

## 🔄 The Waterfall Logic
AdGlide uses a linear **Waterfall Strategy**. If the **Primary Network** fails to load after 2-5 seconds (internal timeout), the SDK automatically attempts to load the **Backup Network**.

If you use `setBackupAdNetworks(String...)`, the SDK will cycle through the entire list until an ad is filled.

---

## 🌐 Legendary Technical Manual: Deep-Dives

### 1. Remote Configuration Encyclopedia
The `RemoteConfigHelper` allows you to switch ad networks dynamically. The expected JSON structure is as follows:

```json
{
  "ad_status": "1",
  "ad_network": "admob",
  "backup_ad_network": "applovin",
  "ad_mob_app_id": "ca-app-pub-3940256099942544~3347511713",
  "ad_mob_banner_id": "ca-app-pub-3940256099942544/6300978111",
  ... (any other ID fields)
}
```

**Implementation Tip**: 
Always check `onConfigFetched(JSONObject config)` before initializing the SDK to ensure your primary network matches your latest server-side decision.

### 2. GDPR & Privacy Masterclass
AdGlide uses the Google User Messaging Platform (UMP). For local testing, you need to generate a **hashed device ID**.

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

### 3. The Great Troubleshooting Matrix

| Symptom | Probable Cause | Expert Solution |
| :--- | :--- | :--- |
| **Ads don't show** | Manifest metadata missing | Ensure `APPLICATION_ID` for AdMob/AppLovin is in `<application>`. |
| **No fill (Error 3)** | Geographic restriction | Check if your test device matches the network's targeting. |
| **App Open doesn't show** | `unique_id` extra missing | Ensure your intent doesn't have `unique_id` if you want the ad to trigger. |
| **IronSource crash** | Lifecycle missing | Call `IronSource.onResume/onPause` in your activity. |
| **ProGuard issues** | Missing rules | Copy the rules from the **Security** section below. |

---

## 📖 Master API Dictionary (Quick Reference)

### `AdNetwork.Initialize`
- `setAdStatus(String)`: "1" for ON, "0" for OFF.
- `setAdNetwork(String)`: Set primary network key.
- `addBackupAdNetwork(String)`: Add a fallback network to the waterfall.
- `setDebug(boolean)`: Enable verbose logging and test modes.

### `BannerAd.Builder`
- `setAdMobBannerId(String)`
- `setMetaBannerId(String)`
- `setAppLovinBannerId(String)` / `setApplovinDiscBannerZoneId(String)`
- `setIsCollapsibleBanner(boolean)`: (AdMob only)

### `NativeAd.Builder`
- `setPadding(L, T, R, B)`: Layout customization.
- `setDarkTheme(boolean)`: Switch to high-contrast dark mode styles.
- `setNativeAdStyle(String)`: Use `Constant.STYLE_*` for Meta layouts.
- `setNativeAdBackgroundColor(int, int)`: Light and Dark resources.

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
