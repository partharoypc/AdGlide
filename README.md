<p align="center">
  <img src="assets/banner.png" alt="AdGlide Banner">
</p>

# AdGlide SDK 🚀
### *The Premium Mediation Wrapper for High-Performance Android Apps*

[![Version](https://img.shields.io/badge/Version-1.1.0-blue.svg)](https://github.com/partharoypc/AdGlide)
[![SDK Support](https://img.shields.io/badge/Android-21%2B-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**AdGlide** is an industrial-grade mediation powerhouse designed for professional Android developers. It eliminates the friction of multi-network integration, providing a **"Just Copy-Paste"** architecture that handles initialization, bidding, waterfalls, and pre-fetching out of the box. 

This detailed guide will walk you through implementing **every feature and function** available in AdGlide.

---

## 🏗️ Core Infrastructure & Logic

AdGlide supports three distinct integration patterns:

1.  **Direct Use**: Target a specific network exclusively.
2.  **Bidding Mediation**: Utilize real-time header bidding for supported networks.
3.  **Sequential Waterfall**: A fail-safe `WaterfallManager` that cycles through unlimited backup networks instantly if the primary fails to fill.
4.  **Intelligent Rate Limiting**: Built-in `AdMobRateLimiter` ensures failing AdMob units don't loop endlessly.

### 📊 Capabilities Matrix

| Ad Format | AdMob | Meta | Unity | AppLovin | IronSource | StartApp | Wortise |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Bidding Support**| ✅ | ✅ | ❌ | ✅ | ✅ | ❌ | ❌ |
| **Banner** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Interstitial** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Native** | ✅ | ✅ | ❌ | ✅ | ❌ | ✅ | ✅ |
| **Rewarded** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **App Open** | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ | ✅ |

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
    implementation 'com.github.partharoypc:adglide:1.1.0'
    
    // Choose your desired Ad Networks:
    implementation 'com.google.android.gms:play-services-ads:23.6.0' // AdMob
    implementation 'com.facebook.android:audience-network-sdk:6.18.0' // Meta
    implementation 'com.applovin:applovin-sdk:13.0.1' // AppLovin
    implementation 'com.startapp:inapp-sdk:5.1.0' // StartApp
    // Add Unity, IronSource, Wortise dependencies as needed...
}
```

### Step 3: Configure AndroidManifest.xml
**CRITICAL:** If using AdMob or AppLovin, you MUST declare your App IDs in the `<application>` tag of your `AndroidManifest.xml` to prevent crashes.

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
Initialize AdGlide inside your `Application` class or main `Activity` `onCreate()` method:

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AdGlide.init(this)
            .status(true) // Master Switch for ALL ads (false disables everything)
            .testMode(true) // Set to ONLY during development. Set false for production!
            .debug(true) // Enables verbose console logging
            .network(AdGlideNetwork.ADMOB) // Set the Primary Network
            .backups(AdGlideNetwork.META, AdGlideNetwork.APPLOVIN) // Set Fallback sequence
            
            // Supply SDK IDs for your chosen networks (You only need the ones you use)
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

## �️ Detailed Ad Implementation & Usage

AdGlide uses a flexible **Builder** pattern. Every ad format shares these core methods:
*   `.status(boolean)`: Turn this *specific* ad call ON/OFF.
*   `.placement(int)`: Used to toggle specific ad placements remotely (0 = OFF, 1 = ON).
*   `.network(AdGlideNetwork)`: Override the primary network for this specific ad.
*   `.backup(AdGlideNetwork)`: Set a single fallback if the primary fails.
*   `.backups(AdGlideNetwork...)`: Set a *Waterfall* of multiple backups.

---

### 1. App Open Ads (Splash & Resume)
App Open ads display immediately when the app foregrounds (cold start or resume). 

**Automatic Lifecycle Setup (Recommended):**
Place this in your `Application` class right after initialization. It will automatically detect when users return to the app and show an ad.

```java
AppOpenAd appOpenAd = new AppOpenAd()
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .adMobId("ca-app-pub-3940256099942544/9257395921");

// Starts listening to App Background/Foreground events
appOpenAd.setLifecycleObserver()
         .setActivityLifecycleCallbacks(this); 
```

**Manual Triggering (e.g., Splash Screen):**
```java
new AppOpenAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .adMobId("ca-app-pub-3940256099942544/9257395921")
    .build()
    .load(new OnShowAdCompleteListener() {
        @Override
        public void onShowAdComplete() {
            // Fired when the ad is closed, or if it failed to load. 
            // Put your navigation intent here!
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }); 
```

---

### 2. Banner & Medium Rectangle Ads (MREC)
Banners are persistent views. You must provide a container in your XML layout.

**XML Layout Definition:**
```xml
<!-- Banner Container -->
<FrameLayout
    android:id="@+id/ad_mob_banner_view_container"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

**Java Implementation (Banner):**
```java
new BannerAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.META, AdGlideNetwork.STARTAPP)
    .adMobId("ca-app-pub-3940256099942544/2014213617") // AdMob Banner ID
    .metaId("YOUR_META_PLACEMENT_ID")
    .collapsible(true) // Enables high-CTR collapsible banners (AdMob only)
    .darkTheme(true) // Automatically matches banner styles to dark mode
    .build()
    .load();
```

**Java Implementation (Medium Rectangle 300x250):**
MRECs are larger banners, great for articles or scrolling lists. Use the exact same layout `FrameLayout`, but call `MediumRectangleAd.Builder`:
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

### 3. Interstitial Ads (Full Screen)
AdGlide manages pre-loading gracefully.

**Java Implementation:**
```java
new InterstitialAd.Builder(this)
    .status(true)
    .placement(1)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.STARTAPP, AdGlideNetwork.APPLOVIN)
    .adMobId("ca-app-pub-3940256099942544/1033173712") // AdMob Interstitial ID
    .interval(3) // ✨ FREQUENCY CAPPING: Only physically shows the ad every 3rd time .show() is called!
    .build()
    .load(new OnInterstitialAdDismissedListener() {
        @Override
        public void onInterstitialAdDismissed() {
            // Action to perform when the user closes the Interstitial ad
            // (e.g., Load next level)
        }
    }) 
    .show(this); // Tell it to display immediately once loaded
```

---

### 4. Native Ads (Highly Customizable UI)
Native ads blend directly into your app's UI. AdGlide provides ready-made templates.

**XML Layout Definition:**
Provide a standard container where the native ad should inflate. AdGlide handles inflating the complex ad views inside this container.
```xml
<LinearLayout
    android:id="@+id/native_ad_view_container"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical" />
```

**Java Implementation:**
```java
new NativeAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.META)
    .adMobId("ca-app-pub-3940256099942544/2247696110") // AdMob Native ID
    
    // UI Customization Options:
    .style(AdGlideNativeStyle.STYLE_MEDIUM) // Choose from: STYLE_NEWS, STYLE_MEDIUM, STYLE_SMALL, STYLE_VIDEO_LARGE
    .darkTheme(false) // Force dark mode rendering
    .backgroundColor(R.color.white, R.color.black) // Provide Light/Dark Background colors
    .padding(10, 10, 10, 10) // Internal padding
    .margin(16, 8, 16, 8) // External margins
    
    .build()
    .load();
```

---

### 4.1 Native Ads (Advanced: Fragments & ViewPagers)
For more complex UI requirements like multi-fragment layouts or swipable Tabs, AdGlide provides specialized builders.

**NativeAdFragment (For Fragments):**
```java
new NativeAdFragment.Builder(getActivity())
    .view(rootView) // Pass the fragment's root view
    .adMobId("YOUR_ADMOB_NATIVE_ID")
    .style(AdGlideNativeStyle.STYLE_MEDIUM)
    .build()
    .load();
```

**NativeAdViewPager (For ViewPagers/RecyclerViews):**
```java
new NativeAdViewPager.Builder(activity, itemView) // Pass the item view
    .adMobId("YOUR_ADMOB_NATIVE_ID")
    .style(AdGlideNativeStyle.STYLE_SMALL)
    .build()
    .load();
```

---

### 5. Rewarded Ads (Users get Items/Coins)
Essential for game economics or unlocking premium content.

```java
// 1. First, build and load logic (Can be done in onCreate)
RewardedAd rewardedAdBuilder = new RewardedAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .backups(AdGlideNetwork.UNITY)
    .adMobId("ca-app-pub-3940256099942544/5224354917") // AdMob Rewarded ID
    .build();

// 2. Pre-load the ad silently
rewardedAdBuilder.load(
    new OnRewardedAdCompleteListener() {
        @Override
        public void onRewardedAdComplete() {
            // SUCCESS! The user finished watching the video. 
            // Give them their coins/lives here!
            Log.d("AdGlide", "User earned reward!");
        }
    }, 
    new OnRewardedAdDismissedListener() {
        @Override
        public void onRewardedAdDismissed() {
            // User closed the ad (they may or may not have finished watching)
            // You can reload the ad here.
        }
    }
);

// 3. Trigger the display when the user clicks the "Watch Ad" button
findViewById(R.id.btn_watch_ad).setOnClickListener(v -> {
    rewardedAdBuilder.show(
        new OnRewardedAdCompleteListener() { /* Reward user */ },
        new OnRewardedAdDismissedListener() { /* Ad closed */ },
        new OnRewardedAdErrorListener() {
            @Override
            public void onRewardedAdError() {
                // The ad failed to show (e.g., no internet, or failed to load)
                Toast.makeText(MainActivity.this, "No ad available.", Toast.LENGTH_SHORT).show();
            }
        }
    );
});
```

**Rewarded Interstitial Ads:**
A variation of rewarded ads that automatically play between content transitions without requiring a user opt-in prompt. Use the identical callback setup as Rewarded Ads above, but swap the builder class:

```java
RewardedInterstitialAd.Builder rewardedInterstitialAdBuilder = new RewardedInterstitialAd.Builder(this)
    .status(true)
    .network(AdGlideNetwork.ADMOB)
    .adMobId("YOUR_ADMOB_REWARDED_INTERSTITIAL_ID");

// Call .build().load(...) and .show(...) exactly like RewardedAd!
```

---

## 🚀 Pro Performance Features

### Pre-Fetching (AdRepository)
Nobody likes waiting for an interstitial ad to load when clicking a button. Use `AdRepository` to silently pre-fetch ads in your Splash screen, making them instantly available later.

```java
// In your Splash Activity: Request an ad to be downloaded to memory
AdRepository.getInstance().preloadInterstitial(this, AdGlideNetwork.ADMOB.getValue(), "YOUR_ADMOB_AD_UNIT_ID");

// In your Main Activity later: When you call the builder using the EXACT SAME AD ID, 
// AdGlide instantly retrieves the cached ad instead of downloading a new one!
new InterstitialAd.Builder(this)
    .network(AdGlideNetwork.ADMOB)
    .adMobId("YOUR_ADMOB_AD_UNIT_ID") 
    .build()
    .load()
    .show(this);

// Memory Management: Prevent Memory Leaks when your Activity/App is destroyed!
@Override
protected void onDestroy() {
    super.onDestroy();
    AdRepository.getInstance().clearCache();
}
```

### Triple-Base64 Security (String Obfuscation)
Hackers often decompile APKs to steal your hard-earned Ad Unit IDs and replace them. Use `Tools.decode()` to hide them.

1. Convert your string `"ca-app-pub-YOUR-ID"` to Base64 *three times* using any online tool.
2. Put the final encrypted string in your code:
```java
String safeId = Tools.decode("TWpZNE5UYzVOekk1TkRRME5nPT0=");
new BannerAd.Builder(this)
    .adMobId(safeId) 
    ...
```

---

## 🔒 Network Safety & Connectivity
AdGlide SDK follows a "Network-First" approach. To prevent unnecessary SDK crashes and preserve battery life, connectivity checks are performed automatically:

- **SDK Initialization**: `AdNetwork.initAds()` will skip initialization if no network is detected.
- **Ad Loading**: All ad formats (`Banner`, `Interstitial`, `Rewarded`, etc.) check for internet access *before* sending a request to the ad server.
- **Fail-Safe**: If a device goes offline during a waterfall sequence, the SDK gracefully stops and waits for the next manual request once the connection is restored.

---

## 🛡️ Production Release Requirements (R8/ProGuard)
When building an APK/AAB for production, Android Studio aggressively minifies code. You MUST add these rules to your `proguard-rules.pro` file to prevent the ad SDKs from breaking:

```proguard
-keep public class com.partharoypc.adglide.format.** { public *; }
-keep public class com.partharoypc.adglide.util.** { public *; }
-keep interface com.partharoypc.adglide.util.On*Listener { *; }

# AdMob Rules
-keep class com.google.android.gms.ads.** { *; }

# Meta rules
-keep class com.facebook.ads.** { *; }

# StartApp rules 
-keep class com.startapp.** { *; }
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod
```

---

## 🤝 Support & Community
Developed with ❤️ by **[Partha Roy](https://github.com/partharoypc)**. 

If this SDK saved you hours of integration headaches, please consider leaving a ⭐ on GitHub!

For bugs, feature requests, or custom mediation integrations, please open an issue.

---
*AdGlide SDK is MIT Licensed. © 2026 Partha Roy.*
