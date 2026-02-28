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

```text
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

## ⚡ Step-by-Step Setup Guide (Fully Detailed)

Follow these instructions carefully to integrate AdGlide into your Android project. We've designed this to be as frictionless as possible.

### Step 1: Configure Repositories
First, you need to tell Gradle where to find the AdGlide SDK and the ad networks. 
Open your **`settings.gradle`** (or project-level `build.gradle` in older projects) and add the following:

> **Note:** Only add the custom repositories for the secondary networks you actually plan to use.

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        
        // 1. Required for AdGlide SDK
        maven { url 'https://jitpack.io' } 
        
        // 2. Optional: Add only if you are using these specific networks
        maven {
            url 'https://artifacts.applovin.com/android' // For AppLovin
            content { includeGroup "com.applovin" }
        }
        maven { url 'https://artifact.bytedance.com/repository/pangle' } // For Meta/Pangle Bidding
        maven { url 'https://maven.wortise.com/artifactory/public' }     // For Wortise
        maven { url 'https://android-sdk.is.com/' }                      // For IronSource
    }
}
```

### Step 2: Add Dependencies
Next, open your **app-level `build.gradle`** (usually `app/build.gradle`) and add the AdGlide core library. 

You must also manually add the dependencies for the specific ad networks you want to use. AdGlide is unbundled, which keeps your final APK size slim!

```gradle
dependencies {
    // 🌟 Core AdGlide SDK (Required)
    implementation 'com.github.partharoypc:adglide:1.4.0'
    
    // �️ User Messaging Platform (Required for European GDPR/CCPA compliance)
    implementation 'com.google.android.ump:user-messaging-platform:4.0.0'
    
    // 🔥 OPTIONAL NETWORK DEPENDENCIES 🔥
    // Uncomment and use ONLY the networks you need:
    
    implementation 'com.google.android.gms:play-services-ads:23.6.0'   // AdMob
    // implementation 'com.facebook.android:audience-network-sdk:6.18.0'  // Meta
    // implementation 'com.applovin:applovin-sdk:13.0.1'                  // AppLovin
    // implementation 'com.startapp:inapp-sdk:5.3.0'                      // StartApp
    // implementation 'com.wortise:android-sdk:1.7.0'                     // Wortise
    // implementation 'com.unity3d.ads:unity-ads:4.12.5'                  // Unity
    // implementation 'com.ironsource.sdk:mediationsdk:8.4.0'             // IronSource
}
```

### Step 3: Configure AndroidManifest.xml (CRITICAL)
If you are using **AdMob** or **AppLovin**, you **MUST** declare your App IDs in your `AndroidManifest.xml` inside the `<application>` tag. 
> ⚠️ **Failure to do this will cause your app to crash immediately on launch with a `CrashOnMainActivityLayout` or `MissingManifestEntryException`.**

```xml
<application ...>
    <!-- AdMob App ID (Find this in your AdMob Dashboard -> App Settings) -->
    <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="ca-app-pub-3940256099942544~3347511713"/> <!-- Replace with your actual AdMob App ID! -->
        
    <!-- AppLovin SDK Key (Find this in AppLovin Dashboard -> Account -> Keys) -->
    <meta-data
        android:name="applovin.sdk.key"
        android:value="YOUR_APPLOVIN_SDK_KEY"/>
</application>
```

### Step 4: Create a Custom Application Class
AdGlide v1.4.0 uses a modern Global Configuration Object. To ensure ads load immediately in the background and prevent memory leaks, you must initialize AdGlide in a custom `Application` class, **not** in a standard `Activity`.

Create a new Java or Kotlin class named `MyApplication` that extends `Application`:

**Java:**
```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Build your global configuration
        AdGlideConfig config = new AdGlideConfig.Builder()
            // --- Core Settings ---
            .enableAds(true)         // Master kill-switch. Set to false to instantly disable all ads app-wide.
            .testMode(true)          // ⚠️ CRITICAL: Set to TRUE during development, FALSE for production!
            .debug(true)             // Enables detailed console logging (Logcat tab: filter by "AdGlide")
            
            // --- Network Strategy ---
            .primaryNetwork(AdGlideNetwork.ADMOB) // The first absolute network AdGlide will try
            .backupNetworks(AdGlideNetwork.META, AdGlideNetwork.APPLOVIN) // Fallbacks in order if primary fails
            
            // --- SDK Keys ---
            .adMobAppId("ca-app-pub-3940256099942544~3347511713") // Redundancy strictly for dynamic bidding flow
            .appLovinSdkKey("YOUR_APPLOVIN_KEY")
            
            // --- Global Ad Unit IDs ---
            // Set these ONCE here globally, and you'll never need to pass IDs to your UI activities!
            .adMobBannerId("ca-app-pub-3940256099942544/6300978111")
            .adMobInterstitialId("ca-app-pub-3940256099942544/1033173712")
            .adMobRewardedId("ca-app-pub-3940256099942544/5224354917")
            .adMobAppOpenId("ca-app-pub-3419835294")
            
            // --- Automation & Smart Features ---
            .autoLoadInterstitial(true) // Automatically pre-caches the next interstitial immediately after one is shown
            .autoLoadRewarded(true)     // Automatically pre-caches the next rewarded ad 
            .enableAppOpenAd(true)      // Automatically handles App Open Ads on cold start and every app resume
            .excludeOpenAdFrom(SplashActivity.class) // Prevents App Open ads on Splash/Loading screens!
            
            // --- Privacy & Debugging ---
            .enableGDPR(true)           // Automatically triggers the UMP European consent form on first launch
            .enableDebugHUD(true)       // Allows you to trigger the secret in-app debugger waterfall tracker
            
            // --- Advanced: Revenue Tracking (LTV) ---
            .onPaidEventListener((valueMicros, currencyCode, precision, network, adUnitId) -> {
                double revenue = valueMicros / 1000000.0;
                // Perfect place to send revenue data to Firebase Analytics, Adjust, or AppsFlyer
                // Log.d("AdGlideRevenue", "Earned: " + revenue + " " + currencyCode);
            })
            .build();
            
        // Fire up the engine 🚀
        AdGlide.initialize(this, config);
    }
}
```

**Kotlin DSL (Clean alternative):**
```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val config = adGlideConfig {
            // Core
            enableAds = true
            testMode = true
            debug = true
            
            // Strategy
            primaryNetwork = AdGlideNetwork.ADMOB.value
            backupNetworks = arrayOf(AdGlideNetwork.APPLOVIN.value)
            
            // Setup keys & IDs
            adMobAppId = "ca-app-pub-3940256099942544~3347511713"
            adMobBannerId = "ca-app-pub-3940256099942544/6300978111"
            
            // Automation
            autoLoadInterstitial = true
            enableAppOpenAd = true
            enableGDPR = true
        }

        AdGlide.initialize(this, config)
    }
}
```

### Step 5: Register Your Application Class (🚨 VERY IMPORTANT)
Because you created a custom Application class (`MyApplication`), you **must** tell Android to inherently use it when the app starts.
Open your `AndroidManifest.xml` and add the `android:name=".MyApplication"` property to the `<application>` tag:

```xml
<application
    android:name=".MyApplication"  <!-- 👈 YOU MUST ADD THIS LINE! -->
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:theme="@style/Theme.MyApp">
    
    ...
</application>
```

**Setup Complete! 🎉** You are now entirely ready to show ads. Head to the next section to see the 1-Line implementation.

---

## 🛠️ 3. Ad Format Guide (The 1-Line API)

AdGlide was built to save you time. Our 1-Line API automatically handles contexts, caching, UI thread posting, and error fallbacks. 

### 📱 App Open Ads (Automated)
App Open ads are high-performing ads that show immediately when a user opens or resumes your app.
Once you enable `enableAppOpenAd(true)` in your `AdGlideConfig`, these are managed entirely automatically via the `Application` activity lifecycle. **No additional code is required** to show them.

> 💡 **Pro Tip: Splash Screens**
> Showing an App Open Ad on top of a Splash screen while it's loading data can look jarring. Prevent this by blacklisting your Splash screen:
> Use `.excludeOpenAdFrom(SplashActivity.class)` in your config builder.

### 🖼️ Banner Ads
Show a high-performance adaptive banner anywhere. Adaptive banners automatically resize to maximize revenue without breaking your layout.

```java
// Basic 1-Liner: Pass the Activity and your empty ViewGroup container
AdGlide.showBanner(activity, myBannerContainer);
```

*Advanced Banner (Collapsible):*
Collapsible banners are large banners that the user can dismiss into a smaller size. They offer significantly higher eCPMs.
```java
new BannerAd.Builder(activity)
    .container(myBannerContainer)
    .collapsible(true) // Prompts AdMob to serve a collapsible banner
    .load();
```

### 🎬 Interstitial Ads
Interstitials are full-screen ads. If you enabled `autoLoadInterstitial(true)` globally in `AdGlideConfig`, the SDK pre-caches the next ad in the background instantly after the current one closes.

```java
// Show an interstitial instantly. AdGlide handles all UI thread safety.
AdGlide.showInterstitial(activity);
```

*With Dismiss Action (Highly Recommended):*
Never pause your app's main logic *before* showing an ad. Use the dismiss callback to continue your app flow precisely *after* the ad closes or fails to load.
```java
AdGlide.showInterstitial(activity, () -> {
    // This runs immediately if no ad is ready, OR after the ad is closed by the user.
    // Perfect place to navigate to the next screen!
    startActivity(new Intent(activity, NextActivity.class));
});
```

*Manual Preloading:*
If you prefer total control over memory and turned off auto-loading, you can cache ads manually at specific times (e.g., when a game level starts):
```java
AdGlide.preloadInterstitial(activity);
```

### 🎁 Rewarded Ads
Rewarded video ads allow users to watch a 30-second video in exchange for in-app currency or premium features. 

```java
AdGlide.showRewarded(activity, (rewarded) -> {
    if (rewarded) {
        // The user watched the whole video! Grant the reward here.
        grantCoins(50);
        Toast.makeText(activity, "You earned 50 coins!", Toast.LENGTH_SHORT).show();
    } else {
        // The user closed the ad early. Do not grant the reward.
        Toast.makeText(activity, "Watch the full video to get coins", Toast.LENGTH_SHORT).show();
    }
}, () -> {
    // This callback fires when the ad is completely dismissed from the screen.
    // You can safely resume your game or UI state here.
});
```

### 🎨 Native Ads
Native ads blend seamlessly into your app's design. AdGlide provides 4 predefined, high-converting templates so you don't have to write any XML.

```java
// Renders a beautiful Native ad directly inside your container
AdGlide.showNative(activity, myNativeContainer, AdGlideNativeStyle.MEDIUM);
```

Available Styles (`AdGlideNativeStyle`):
- `SMALL` — Small Icon + Title + CTA (Perfect for tight lists/recycler views)
- `MEDIUM` — Large Image + Title + Body + CTA (Perfect for standard news feeds)
- `BANNER` — Horizontal layout resembling a standard banner
- `VIDEO` — Automatically prioritizes video native assets

*Custom Native View Integration:*
If you want to build your own XML layout for the ultimate custom look:
```java
new NativeAd.Builder(activity)
    .container(myNativeContainer)
    .style(AdGlideNativeStyle.MEDIUM) // Defines the baseline behavior
    .load();
```

---

## 🛰️ 4. Dynamic Remote Configuration

Want to change your ad frequency, switch primary networks, or turn off ads completely *without* publishing a new app update? Use Remote Configuration.

Host a JSON file on your own server or GitHub Gist, and AdGlide will sync it globally.

```java
AdGlide.fetchRemoteConfig("https://api.myapp.com/ads.json", (configBuilder) -> {
    // The SDK automatically parses the JSON and updates your AdGlideConfig in real-time!
    // No extra code needed here.
});
```

**JSON Schema Example:**
```json
{
  "ad_status": true,                           // Master kill-switch
  "primary_network": "ADMOB",                  // Change providers instantly
  "backup_networks": ["APPLOVIN", "UNITY"],
  "admob_banner_id": "ca-app-pub-...",         // Rotate IDs if you get limited
  "applovin_interstitial_id": "...",
  "interstitial_interval": 3,                  // Show interstitial every 3 clicks
  "rewarded_interval": 2,                      // Show rewarded every 2 clicks
  "open_ads_on_start": true                    // Toggle App Open ads remotely
}
```

---

## 🏠 5. House Ads (Offline Fallback)

Don't lose users just because they have no internet or the ad network failed to fill. Fallback onto **House Ads**: static, internal promotions that route users to your premium upgrades or other apps.

**Enable House Ads globally in your `AdGlideConfig` builder:**
```java
// Add to your MyApplication.java configuration
.houseAdEnabled(true)
.houseAdBannerImage("https://myapp.com/promo_banner.jpg")          // Standard 320x50 image
.houseAdBannerClickUrl("https://myapp.com/premium_upgrade")        // Deep link to your premium paywall
.houseAdInterstitialImage("https://myapp.com/promo_full.jpg")      // Standard 1080x1920 image
.houseAdInterstitialClickUrl("market://details?id=com.my.other")   // Cross-promote another app
```

---

## 🛠️ 6. SDK Debugger (HUD)

Mediation can be complicated. Which network filled? Did AdMob rate-limit you? 
Activate the built-in diagnostic overlay to monitor ad fill and waterfall performance in real-time right on your testing device.

*   **Trigger the Debugger**: We recommend mapping this to a secret button during development (e.g., tap version number 5 times):
```java
AdGlide.showDebugHUD(activity);
```

---

## 🔒 7. Security & ProGuard

If you build your app with R8/ProGuard enabled (which you should for Production!), add these rules to your `proguard-rules.pro` file so the ad networks aren't stripped from your app:

```proguard
# Protect AdGlide Core
-keep public class com.partharoypc.adglide.** { *; }

# Protect Networks (Prevents build warnings / crashes)
-dontwarn com.google.android.gms.ads.**
-dontwarn com.facebook.ads.**
-dontwarn com.applovin.**
-dontwarn com.startapp.**
```

---
---

## 📈 8. Best Practices for Maximum Revenue
To get the absolute highest eCPMs from AdGlide, we highly recommend this setup:
1. **Enable App Open Ads:** Set `.enableAppOpenAd(true)`. The first ad a user sees upon opening your app is extremely valuable to advertisers.
2. **Use Collapsible Banners:** In your main `Activity`, use a collapsible banner instead of a standard adaptive banner. 
3. **Turn on Auto-Loading for Interstitials:** Set `.autoLoadInterstitial(true)`. An interstitial ad that takes 5 seconds to load makes a user close the app. If the ad is pre-fetched, it shows at the exact moment you call `AdGlide.showInterstitial(activity)`.
4. **House Ads:** Always configure a House Ad linking to a premium/ad-free version of your app or your best other app. Do not waste the blank space if the user is offline!

---

## 🚑 9. Common Troubleshooting (FAQ)

<details>
<summary><b>1. "MissingManifestEntryException" or App crashing on launch?</b></summary>
You forgot to add your AdMob or AppLovin App ID to the `<application>` tag in your `AndroidManifest.xml` (See Step 3).
</details>

<details>
<summary><b>2. Ads aren't showing / "No Fill" error?</b></summary>
* Check if you set `.testMode(true)`. Real ads take up to 24-48 hours to appear for new ad unit IDs.
* Ensure your device has an active internet connection (or that House Ads are configured to fallback).
* Open the **SDK Debugger** using `AdGlide.showDebugHUD(activity);` to see exactly which network failed to fill.
</details>

<details>
<summary><b>3. Why did I get a NullPointerException on initialization?</b></summary>
Did you forget to add `android:name=".MyApplication"` to your `AndroidManifest.xml`? Android must know to launch your custom Application class (See Step 5).
</details>

<details>
<summary><b>4. How do I disable ads completely (for premium users)?</b></summary>
The SDK is built to be dynamic. When your premium user buys the upgrade, re-initialize the config with `.enableAds(false)`.
```java
AdGlideConfig premiumConfig = new AdGlideConfig.Builder()
    .enableAds(false) // KILLS ALL ADS INSTANTLY
    .build();
    
AdGlide.initialize(this, premiumConfig);
```
</details>

---

*Built for Scale. Optimized for Speed. Perfected for Developers.*
© 2026 AdGlide. All rights reserved.