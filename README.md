# AdGlide SDK 🚀

**The Ultimate Ad Mediation Solution for Android Developers.**

AdGlide simplifies integrating multiple ad networks into a single, cohesive system. With just a few lines of code, you get automatic fallback, smart waterfall handling, and full GDPR compliance.

![Version](https://img.shields.io/badge/Version-1.0.0-blue?style=flat-square) ![Min SDK](https://img.shields.io/badge/Min%20SDK-21-green?style=flat-square) ![Target SDK](https://img.shields.io/badge/Target%20SDK-35-orange?style=flat-square) ![License](https://img.shields.io/badge/License-Proprietary-red?style=flat-square)

---

## 📚 Table of Contents

1.  [Why AdGlide?](#-why-adglide)
2.  [Prerequisites](#-prerequisites)
3.  [Installation & Setup](#-installation--setup) (Groovy & Kotlin DSL)
4.  [Manifest Configuration](#-manifest-configuration)
5.  [Initialization: The Brain](#-initialization-the-brain)
6.  [Ad Formats: Zero to Hero](#-ad-formats-zero-to-hero)
    *   [Banner Ads](#1-banner-ads)
    *   [Interstitial Ads](#2-interstitial-ads)
    *   [Native Ads](#3-native-ads)
    *   [Rewarded Ads](#4-rewarded-ads)
    *   [App Open Ads](#5-app-open-ads)
7.  [GDPR & Privacy (UMP)](#%EF%B8%8F-gdpr--privacy)
8.  [Testing & Verification](#-testing--verification)
9.  [FAQ & Troubleshooting](#-faq--troubleshooting)

---

## 🌟 Why AdGlide?

*   **Smart Fallback**: If AdMob fails, it automatically strives to load FAN, then Unity, etc.
*   **Simple API**: Replaces hundreds of lines of adapter code with a simple Builder pattern.
*   **Safety First**: Built-in memory leak protection and lifecycle management.
*   **Compliance Ready**: One-line Google UMP integration for GDPR.

---

## 🛠 Prerequisites

*   **Android Studio**: Giraffe or newer (recommended).
*   **Min SDK**: API 21 (Android 5.0).
*   **Target SDK**: API 35 (Android 15).
*   **Java**: JDK 17 (Required by Gradle 8+).

---

## 📦 Installation & Setup

We support both **Groovy** and **Kotlin DSL** build scripts. Choose your style!

### Step 1: Add JitPack Repository

**Option A: Groovy (`settings.gradle` or root `build.gradle`)**
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' } // <--- Add this line
    }
}
```

**Option B: Kotlin DSL (`settings.gradle.kts`)**
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io") // <--- Add this line
    }
}
```

### Step 2: Add Dependency

**Option A: Groovy (`app/build.gradle`)**
```gradle
dependencies {
    implementation 'com.github.partharoypc:adglide:1.0.0'
}
```

**Option B: Kotlin DSL (`app/build.gradle.kts`)**
```kotlin
dependencies {
    implementation("com.github.partharoypc:adglide:1.0.0")
}
```

---

## ⚙ Manifest Configuration

Add these **essential** permissions and meta-data to your `AndroidManifest.xml`.

```xml
<manifest ...>
    <!-- 1. Permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        ...
        android:usesCleartextTraffic="true"
        android:hardwareAccelerated="true">
        
        <!-- 2. AdMob App ID (REQUIRED) -->
        <!-- Replace with your actual App ID from AdMob Console -->
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-3940256099942544~3347511713" />

        <!-- 3. AppLovin SDK Key (REQUIRED if using AppLovin) -->
        <meta-data
            android:name="applovin.sdk.key"
            android:value="YOUR_APPLOVIN_SDK_KEY_HERE" />
            
    </application>
</manifest>
```

> [!IMPORTANT]
> **Crash Warning**: If you forget the `com.google.android.gms.ads.APPLICATION_ID` meta-data, your app **will crash** on startup.

---

## 🧠 Initialization: The Brain

Initialize the SDK once in your `Application` class or main `Activity`. This "pre-warms" the ad networks.

```java
// In MainActivity.java or MyApplication.java
new AdNetwork.Initialize(this)
    .setAdStatus("1")            // Master Switch: "1" = ON, "0" = OFF
    .setDebug(true)              // logs tag: "AdNetwork"

    // --- Strategy Configuration ---
    .setAdNetwork("admob")       // Primary Fighter
    .setBackupAdNetwork("fan")   // Backup Fighter
    
    // --- ID Bank (Set all your IDs here) ---
    .setAdMobAppId("ca-app-pub-xxxxx~xxxxx")
    .setStartappAppId("YOUR_STARTAPP_ID")
    .setUnityGameId("YOUR_UNITY_GAME_ID")
    .setAppLovinSdkKey("YOUR_KEY")
    .setWortiseAppId("YOUR_WORTISE_ID")
    
    .build(); // 🚀 Ignites the engine
```

**Supported Network Keys:**
`admob`, `fan` (Meta), `unity`, `applovin_max`, `ironsource`, `startapp`, `wortise`, `google_ad_manager`.

---

## 🎨 Ad Formats: Zero to Hero

### 1. Banner Ads

**XML Layout** (`res/layout/activity_main.xml`):
```xml
<LinearLayout
    android:id="@+id/banner_container"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:gravity="bottom" />
```

**Java Code**:
```java
// 1. Configure the Banner
BannerAd.Builder banner = new BannerAd.Builder(this)
    .setAdStatus("1")
    .setAdNetwork("admob")
    .setBackupAdNetwork("fan")
    .setAdMobBannerId("ca-app-pub-xxxxx/xxxxx")
    .setFanBannerId("YOUR_FAN_ID")
    .setDarkTheme(false)  // Adaptive colors?
    .build();

// 2. Load it
// The SDK automatically finds your provided container view if integrated 
// via specific view binding, otherwise ensure you attach it:
// banner.loadBannerAd(findViewById(R.id.banner_container)); // Pseudocode adjustment based on exact API
banner.loadBannerAd(); 
```

### 2. Interstitial Ads

**Java Code**:
```java
// 1. Setup
InterstitialAd.Builder interstitial = new InterstitialAd.Builder(this)
    .setAdStatus("1")
    .setAdNetwork("admob")
    .setBackupAdNetwork("fan")
    .setAdMobInterstitialId("ca-app-pub-xxxxx/xxxxx")
    .setInterval(1) // Show every time (1) or every 3rd time (3)
    .build();

// 2. Load
interstitial.loadInterstitialAd();

// 3. Show (e.g., inside a button click or game over)
if (interstitial.isAdLoaded()) { // Optional check
    interstitial.showInterstitialAd();
}

// 4. Cleanup (IMPORTANT)
@Override
protected void onDestroy() {
    super.onDestroy();
    if (interstitial != null) {
        interstitial.destroyInterstitialAd();
    }
}
```

### 3. Native Ads

**Java Code**:
```java
NativeAd.Builder nativeAd = new NativeAd.Builder(this)
    .setAdStatus("1")
    .setAdNetwork("admob")
    .setBackupAdNetwork("fan")
    .setAdMobNativeId("ca-app-pub-xxxxx/xxxxx")
    .setNativeAdStyle("medium") // 'small', 'medium', 'large'
    .build();

nativeAd.loadNativeAd();

// Cleanup (IMPORTANT)
@Override
protected void onDestroy() {
    super.onDestroy();
    if (nativeAd != null) {
        nativeAd.destroyNativeAd();
    }
}
```

### 4. Rewarded Ads

**Java Code**:
```java
RewardedAd.Builder rewarded = new RewardedAd.Builder(this)
    .setAdStatus("1")
    .setMainAds("admob")
    .setAdMobRewardedId("ca-app-pub-xxxxx/xxxxx")
    .build(
        () -> { 
            // 💰 USER EARNED REWARD! Give coins/lives.
            addCoins(100);
        },
        () -> { 
            // Ad Closed. Resume game.
            resumeGame();
        }
    );

rewarded.loadRewardedAd();

// Show when ready
rewarded.showRewardedAd();

// Cleanup (IMPORTANT)
@Override
protected void onDestroy() {
    super.onDestroy();
    if (rewarded != null) {
        rewarded.destroyRewardedAd();
    }
}
```

### 5. App Open Ads

**Java Code** (Best in `MyApplication.java`):
```java
AppOpenAd.Builder appOpen = new AppOpenAd.Builder(this)
    .setAdStatus("1")
    .setAdNetwork("admob")
    .setAdMobAppOpenId("ca-app-pub-xxxxx/xxxxx")
    .build();

appOpen.loadAppOpenAd();
appOpen.showAppOpenAd();
```

---

## 🛡️ GDPR & Privacy

Don't risk legal issues! Use our built-in Google UMP helper.

```java
GDPR gdpr = new GDPR(this);

// 1. Simple Update (Auto-detects location)
gdpr.updateGDPRConsentStatus();

// 2. Developer Mode (Force EEA geography to test the dialog)
// updateGDPRConsentStatus(adType, isDebug, isChildDirected)
gdpr.updateGDPRConsentStatus("admob", true, false); 
```

---

## 🧪 Testing & Verification

How do you know it's working? **Check your Logcat!**

1.  Open **Logcat** in Android Studio.
2.  Filter by tag: `AdNetwork`.
3.  Look for these success messages:

```text
D/AdNetwork: [admob] is selected as Primary Ads
D/AdNetwork: [fan] is selected as Backup Ads
D/AdNetwork: Adapter name: com.google.ads.mediation.admob.AdMobAdapter, Description: ready, Latency: 20ms
```

If you see these, **congratulations!** Your integration is perfect.

---

## ❓ FAQ & Troubleshooting

| Question | Answer |
| :--- | :--- |
| **Why is my app crashing on launch?** | You likely missed the `com.google.android.gms.ads.APPLICATION_ID` in `AndroidManifest.xml`. |
| **Why are ads not showing in release build?** | Check ProGuard/R8. Our library includes consumer rules, but ensure specific network SDKs (like Unity) aren't being stripped if you use strict shrinking. |
| **How do I disable ads easily?** | Just change `.setAdStatus("1")` to `.setAdStatus("0")` in your initialization code. |
| **Can I use different IDs for different activities?** | Yes! Just create a new `Builder` instance in each Activity with the specific IDs you need. |

---

## 📄 License & Support

**Copyright © Partha Roy.**
For support, please open an issue on the GitHub repository.

---

*Built with ❤️ for Android Developers.*
