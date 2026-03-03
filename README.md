# AdGlide SDK 🚀
### *The Premium Mediation Wrapper for High-Performance Android Apps*

[![Version](https://img.shields.io/badge/Version-1.5.0-blue.svg)](https://github.com/partharoypc/AdGlide)
[![SDK Support](https://img.shields.io/badge/Android-23%2B-green.svg)](https://developer.android.com)
[![Compile SDK](https://img.shields.io/badge/Compile_SDK-36-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**AdGlide** is an industrial-grade ad mediation SDK designed for professional Android developers. It eliminates the friction of multi-network integration, providing a **"Just Copy-Paste"** architecture that handles initialization, bidding, waterfalls, rate limiting, and pre-fetching out of the box.

---

## ✨ What's New in v1.5.0

- **Global Configuration Object** — Replaced `AdGlide.init()` chain with a robust `AdGlideConfig` architecture, ensuring thread-safe settings decoupled from Context or Activity lifting constraints.
- **Zero Memory Leaks Architecture** — Eliminated aggressive `Activity` contexts traversing formats. We safely handle transitions using `WeakReference<Activity>` for App Open, Banner, Interstitial, and Native formats, protecting app performance organically.
- **House Ads & Offline Fallback** — Keep users engaged even without an internet connection using static internal promos.
- **LTV & Revenue Callbacks (`OnPaidEventListener`)** — Direct access to micro-revenue estimates from loaded ads to help you calculate precise LTV locally.
- **Manual Preloading APIs** — Added explicit `AdGlide.preloadInterstitial()` and `AdGlide.preloadRewarded()` hooks so you control cache timing manually beyond the automated interval configurations.
- **Modernized Core** — Refactored internal ad unit mapping with static logic and Java 17+ switch expressions for peak performance and safety.

---

## 🏗️ 1. Core Architecture

AdGlide supports four distinct integration patterns:

1. **Direct Use** — Target a specific ad network exclusively.
2. **Bidding Mediation** — Leverage real-time header bidding for supported networks (Meta ↔ AdMob, Meta ↔ AppLovin, Meta ↔ IronSource).
3. **Sequential Waterfall Engine** — The `WaterfallManager` is the orchestrator of AdGlide. It ensures **100% fill rates** by intelligently rotating through backup networks only when the primary provider fails to fill a request.
4. **Intelligent Rate Limiting** — Built-in defensive logic to prevent account flags (like "AdMob Error 3"). The SDK applies exponential backoff to failing ad units in real-time, protecting your developer account health without sacrificing user experience.

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

AdGlide provides distinct, high-converting templates for Native Ads:

| Enum Value | Descriptor | Ideal Use Case |
| :--- | :--- | :--- |
| `AdGlideNativeStyle.SMALL` | Compact Radio | List items / Small footers |
| `AdGlideNativeStyle.MEDIUM` | Standard Box | News feeds / Card views |
| `AdGlideNativeStyle.BANNER` | Content Blend | Article inline placements / News style |
| `AdGlideNativeStyle.VIDEO` | Immersive Media | High-CPM video rewards / Media focus |

### 📦 SDK Module Structure
AdGlide follows a strict **Clean Architecture** principle to ensure zero memory leaks and thread-safe operations:
- **`com.partharoypc.adglide`**: Main entry Facade and Global Configuration.
- **`format/`**: Lifecycle-aware ad type implementations (AppOpen, Banner, etc.).
- **`gdpr/`**: Modern UMP consent logic for European privacy compliance.
- **`util/`**: The core "brain" including the Waterfall engine, Rate Limiter, and Performance Loggers.

---

## ⚡ 2. Step-by-Step Setup Guide

Follow these instructions to integrate AdGlide into your Android project in less than 5 minutes.

### Step 1: Configure Repositories
Add the AdGlide repository and your chosen ad networks to your **`settings.gradle`** file inside the `dependencyResolutionManagement` block.

> [!TIP]
> Only include repositories for the networks you intend to use to keep your build configuration clean.

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        
        // 🌟 Required for AdGlide SDK
        maven { url 'https://jitpack.io' } 
        
        // 🏗️ Optional Ad Networks (Include only what you need)
        maven { url 'https://artifacts.applovin.com/android' } // AppLovin
        maven { url 'https://artifact.bytedance.com/repository/pangle' } // Meta/Pangle
        maven { url 'https://maven.wortise.com/artifactory/public' }     // Wortise
        maven { url 'https://android-sdk.is.com/' }                      // IronSource
    }
}
```

---

### Step 2: Add Dependencies
Open your **app-level `build.gradle`** (e.g., `app/build.gradle`) and add the core AdGlide library. 

AdGlide is **unbundled**—it doesn't force heavy SDKs on you. You must manually add the dependencies for the networks you want to use.

```gradle
dependencies {
    // 🚀 Core AdGlide SDK (Required)
    implementation 'com.github.partharoypc:adglide:1.5.0'
    
    // 🛡️ GDPR & Consent (Required for European Compliance)
    implementation 'com.google.android.ump:user-messaging-platform:4.0.0'
    
    // 🔥 CHOOSE YOUR NETWORKS 🔥
    implementation 'com.google.android.gms:play-services-ads:23.6.0'   // AdMob
    // implementation 'com.facebook.android:audience-network-sdk:6.18.0' // Meta
    // implementation 'com.applovin:applovin-sdk:13.0.1'                 // AppLovin
    // implementation 'com.startapp:inapp-sdk:5.3.0'                     // StartApp
    // implementation 'com.wortise:android-sdk:1.7.0'                    // Wortise
    // implementation 'com.ironsource.sdk:mediationsdk:8.4.0'            // IronSource
}
```

---

### Step 3: Configure AndroidManifest.xml
Declare your App IDs for AdMob or AppLovin.

> [!CAUTION]
> **CRITICAL:** Missing the AdMob App ID will cause an immediate crash on app launch.

```xml
<application ...>
    <!-- 🟢 AdMob App ID -->
    <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="ca-app-pub-3940256099942544~3347511713"/> <!-- REPLACE WITH YOUR ID -->
        
    <!-- 🔵 AppLovin SDK Key -->
    <meta-data
        android:name="applovin.sdk.key"
        android:value="YOUR_APPLOVIN_SDK_KEY"/>
</application>
```

---

### Step 4: Initialize in Application Class
To prevent memory leaks and ensure ads are pre-loaded, initialize AdGlide in a custom `Application` class.

**Java Implementation:**
```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AdGlideConfig config = new AdGlideConfig.Builder()
            .enableAds(true)              // Global toggle
            .testMode(true)               // ⚠️ Set to FALSE for Production
            .debug(true)                  // Enables detailed console logs
            
            // 🎯 Strategy
            .primaryNetwork(AdGlideNetwork.ADMOB)
            .backupNetworks(AdGlideNetwork.META, AdGlideNetwork.STARTAPP)
            
            // 🆔 Ad Unit IDs which is required for the ads to work

            // AdMob
            .adMobAppId("ca-app-pub-3940256099942544~3347511713")
            .adMobBannerId("ca-app-pub-3940256099942544/6300978111")
            .adMobInterstitialId("ca-app-pub-3940256099942544/1033173712")
            .adMobAppOpenId("ca-app-pub-3940256099942544/9257395921")
            .adMobRewardedId("ca-app-pub-3940256099942544/5224354917")
            .adMobRewardedInterstitialId("ca-app-pub-3940256099942544/5224354917")
            .adMobNativeId("ca-app-pub-3940256099942544/2247696110")

            // Meta
            .metaAppId("YOUR_META_APP_ID")
            .metaBannerId("YOUR_META_BANNER_ID")
            .metaInterstitialId("YOUR_META_INTERSTITIAL_ID")
            .metaAppOpenId("YOUR_META_APP_OPEN_ID")
            .metaRewardedId("YOUR_META_REWARDED_ID")
            .metaRewardedInterstitialId("YOUR_META_REWARDED_INTERSTITIAL_ID")
            .metaNativeId("YOUR_META_NATIVE_ID")

            // AppLovin
            .applovinSdkKey("YOUR_APPLOVIN_SDK_KEY")
            .applovinBannerId("YOUR_APPLOVIN_BANNER_ID")
            .applovinInterstitialId("YOUR_APPLOVIN_INTERSTITIAL_ID")
            .applovinAppOpenId("YOUR_APPLOVIN_APP_OPEN_ID")
            .applovinRewardedId("YOUR_APPLOVIN_REWARDED_ID")
            .applovinRewardedInterstitialId("YOUR_APPLOVIN_REWARDED_INTERSTITIAL_ID")
            .applovinNativeId("YOUR_APPLOVIN_NATIVE_ID")

            // StartApp
            .startappAppId("YOUR_STARTAPP_APP_ID")
            .startappBannerId("YOUR_STARTAPP_BANNER_ID")
            .startappInterstitialId("YOUR_STARTAPP_INTERSTITIAL_ID")
            .startappAppOpenId("YOUR_STARTAPP_APP_OPEN_ID")
            .startappRewardedId("YOUR_STARTAPP_REWARDED_ID")
            .startappRewardedInterstitialId("YOUR_STARTAPP_REWARDED_INTERSTITIAL_ID")
            .startappNativeId("YOUR_STARTAPP_NATIVE_ID")

            // IronSource
            .ironSourceAppKey("YOUR_IRONSOURCE_APP_KEY")
            .ironSourceBannerId("YOUR_IRONSOURCE_BANNER_ID")
            .ironSourceInterstitialId("YOUR_IRONSOURCE_INTERSTITIAL_ID")
            .ironSourceAppOpenId("YOUR_IRONSOURCE_APP_OPEN_ID")
            .ironSourceRewardedId("YOUR_IRONSOURCE_REWARDED_ID")
            .ironSourceRewardedInterstitialId("YOUR_IRONSOURCE_REWARDED_INTERSTITIAL_ID")
            .ironSourceNativeId("YOUR_IRONSOURCE_NATIVE_ID")

            // Unity Ads
            .unityGameId("YOUR_UNITY_GAME_ID")
            .unityBannerId("YOUR_UNITY_BANNER_ID")
            .unityInterstitialId("YOUR_UNITY_INTERSTITIAL_ID")
            .unityAppOpenId("YOUR_UNITY_APP_OPEN_ID")
            .unityRewardedId("YOUR_UNITY_REWARDED_ID")
            .unityRewardedInterstitialId("YOUR_UNITY_REWARDED_INTERSTITIAL_ID")
            .unityNativeId("YOUR_UNITY_NATIVE_ID")

            // Wortise
            .wortiseAppId("YOUR_WORTISE_APP_ID")
            .wortiseBannerId("YOUR_WORTISE_BANNER_ID")
            .wortiseInterstitialId("YOUR_WORTISE_INTERSTITIAL_ID")
            .wortiseAppOpenId("YOUR_WORTISE_APP_OPEN_ID")
            .wortiseRewardedId("YOUR_WORTISE_REWARDED_ID")
            .wortiseRewardedInterstitialId("YOUR_WORTISE_REWARDED_INTERSTITIAL_ID")
            .wortiseNativeId("YOUR_WORTISE_NATIVE_ID")  

            // 🤖 Automation
            .autoLoadInterstitial(true)   // Auto-fetch next ad after show
            .enableAppOpenAd(true)        // Handle App Open automatically
            .enableGDPR(true)             // Show UMP consent form
            .build();
            
        AdGlide.initialize(this, config);
    }
}
```

---

### Step 5: Register Application Class
Finally, tell Android to use your custom application class in your `AndroidManifest.xml`.

```xml
<application
    android:name=".MyApplication"  <!-- 👈 DO NOT MISS THIS -->
    ...>
    ...
</application>
```

**Setup Complete! 🎉** You are now ready to show ads with 1-line implementation.

---

## 🛠️ 3. Implementation Guide (The "Just Copy-Paste" API)

AdGlide's architecture is built on a "Logic-First" philosophy. You don't need to worry about `Runnable` posts, UI thread safety, or memory leaks—the SDK handles everything internally.

---

### 📱 3.1 App Open Ads (Full Automation)
App Open ads are the highest eCPM format for most apps. They appear during cold starts and resume events.

> [!IMPORTANT]
> **Zero-Code Implementation:** If enabled in your `AdGlideConfig`, App Open ads require **zero** additional code in your Activities. The SDK tracks the lifecycle and injects the ad overlay automatically.

#### �️ Handling Blacklisted Screens (Splash/Onboarding)
To prevent ads from showing on specific screens (like a Splash screen that's still loading data), use the exclusion API:

```java
// Add to your AdGlideConfig Builder
.excludeOpenAdFrom(SplashActivity.class, OnboardingActivity.class)
```

#### 📱 Manual App Open Logic
If you disabled `enableAppOpenAd` in your config, you can trigger the format manually:
```java
new AppOpenAd.Builder(activity)
    .adMobId("YOUR_ID")
    .load(new OnShowAdCompleteListener() {
        @Override
        public void onShowAdComplete() {
            // Success: Proceed to main activity
        }
    });
```

---

### 🖼️ 3.2 Banner Ads (Adaptive & Collapsible)
AdGlide uses **Anchored Adaptive Banners** by default, which calculate the optimal height for your specific device screen.

#### ⚡ Standard 1-Line Implementation
```java
AdGlide.showBanner(activity, binding.bannerContainer);
```

#### 👑 Premium: Collapsible Banners
Collapsible banners are large-format banners that show a massive ad initially, then retract to a standard size. They can yield up to **5x higher eCPM** than standard banners.

```java
new BannerAd.Builder(activity)
    .container(binding.bannerContainer)
    .collapsible(true) // 👈 Prompts AdMob/networks to serve an expandable asset
    .load();
```

---

### 🎬 3.3 Interstitial Ads (Smart Pre-fetching)
Interstitials are full-screen ads. AdGlide's pre-fetching engine ensures an ad is almost always ready.

#### 🚿 The Recommended "Seamless Flow"
Never block your user's navigation. Use the callback to transition to the next screen only after the ad is dismissed.

```java
AdGlide.showInterstitial(activity, () -> {
    // This logic executes INSTANTLY if no ad is ready, 
    // or precisely after the user clicks "X" to close the ad.
    startActivity(new Intent(activity, NextActivity.class));
});
```

#### 🧠 Manual Preloading (Optimization)
If you disable `autoLoadInterstitial`, you can manual trigger a cache fill at strategic moments (e.g., when a user finishes a level):
```java
AdGlide.preloadInterstitial(activity);
```

---

### 🎁 3.4 Rewarded Ads (In-App Economy)
Used for granting currency, extra lives, or premium access.

```java
AdGlide.showRewarded(activity, (rewarded) -> {
    if (rewarded) {
        grantCoins(50); // The user watched successfully
    }
}, () -> {
    // Ad dismissed: Resume your game state or music here
});
```

---

### 🎨 3.5 Native Ads (Seamless Blending)
Native ads are rendered by the SDK into your container using high-converting pre-built layouts.

| Template | Recommendation |
| :--- | :--- |
| `SMALL` | List items / Small footers |
| `MEDIUM` | News feeds / Card views |
| `BANNER` | Article inline placements / News style |
| `VIDEO` | High-CPM video rewards / Media focus |

```java
// 1-Line Quick Integration
AdGlide.showNative(activity, binding.nativeContainer, AdGlideNativeStyle.MEDIUM);
```

#### 🛠️ Custom Builder for Flexibility
```java
new NativeAd.Builder(activity)
    .container(binding.nativeContainer)
    .style(AdGlideNativeStyle.VIDEO)
    .load();
```

---

## 🚀 4. Pro Features (Maximum Efficiency)

AdGlide includes built-in tools to help you optimize and debug your ad stack in real-time.

### 🏠 4.1 House Ads (Zero-Fill Fallback)
Don't waste impressions. If the ad networks fail to fill a slot, AdGlide automatically fallbacks to your **House Ads**—static internal promos for your other apps or premium upgrades.

```java
// Setup in your AdGlideConfig Builder
.houseAdEnabled(true)
.houseAdBannerImage("https://yourlink.com/banner.jpg")
.houseAdBannerClickUrl("https://play.google.com/store/apps/details?id=your.other.app")
.houseAdInterstitialImage("https://yourlink.com/full.jpg")
.houseAdInterstitialClickUrl("https://yourlink.com/upgrade")
```

---

### 🛠️ 4.2 SDK Debugger (Real-Time HUD)
Mediation can be complex. Activate the HUD to see exactly which network is filling, failing, or being rate-limited on your test device.

```java
// Trigger this via a secret button or developer menu
AdGlide.showDebugHUD(activity);
```

---

## 🔒 5. Production & Security

### 🛡️ ProGuard / R8 Rules
If you use code shrinking (minifyEnabled true), you **must** add these rules to your `proguard-rules.pro` file. This prevents the compiler from stripping the ad network SDKs.

```proguard
# 🌟 Protect AdGlide Core
-keep public class com.partharoypc.adglide.** { *; }

# 🏗️ Protect Underlying Ad Networks
-dontwarn com.google.android.gms.ads.**
-dontwarn com.facebook.ads.**
-dontwarn com.applovin.**
-dontwarn com.startapp.**
```

---

## 📈 6. Best Practices & Pro Tips

| Tip | Strategy |
| :--- | :--- |
| **App Open Ads** | Always enable them. They capture high-value "fresh eyes" attention. |
| **Auto-Loading** | Set `.autoLoadInterstitial(true)` to ensure 0-second wait times for users. |
| **Collapsible Banners** | Use them on your main "menu" or "dashboard" screen for max eCPM. |
| **Revenue Tracking** | Use `.onPaidEventListener` to send micro-cent revenue to Firebase Analytics. |

---

## 🚑 7. FAQ & Troubleshooting

<details>
<summary><b>1. App crashes immediately on launch?</b></summary>
<b>Cause:</b> Missing AdMob App ID in Manifest or missing <code>android:name=".MyApplication"</code>.<br>
<b>Fix:</b> Review Steps 3 and 5 in the Setup Guide.
</details>

<details>
<summary><b>2. Ads are not showing ("No Fill")?</b></summary>
<b>Cause:</b> New ad units take 24h to active, or you're in a low-fill region.<br>
<b>Fix:</b> Ensure <code>.testMode(true)</code> is on during development to see test ads immediately. Use the <b>SDK Debugger</b> to verify network responses.
</details>

<details>
<summary><b>3. How do I disable ads for Premium users?</b></summary>
<b>Fix:</b> Simply re-initialize AdGlide with a disabled config when the purchase is detected:
<pre>AdGlide.initialize(this, new AdGlideConfig.Builder().enableAds(false).build());</pre>
</details>

<details>
<summary><b>4. Does AdGlide support Kotlin?</b></summary>
<b>Yes!</b> AdGlide is built in Java but fully optimized for Kotlin interop, including a clean DSL for configuration.
</details>

---

*Built for Scale. Optimized for Speed. Perfected for Developers.*
*For more information, visit [AdGlide](https://github.com/partharoypc/AdGlide/blob/main/README.md)*
© 2026 AdGlide. All rights reserved.