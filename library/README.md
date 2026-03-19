# AdGlide Library Module 🚀
### *The Industrial-Grade Core Engine for Android Ad Mediation*

[![Android](https://img.shields.io/badge/Android-23%2B-green.svg)](https://developer.android.com)
[![Compile SDK](https://img.shields.io/badge/Compile_SDK-36-green.svg)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-Clean-blue.svg)](https://en.wikipedia.org/wiki/Clean_code)

This module contains the high-performance source code for the **AdGlide SDK**. It is engineered for stability, zero-latency failovers, and maximum revenue optimization.

---

## 🏗️ 1. Technical Architecture

### 🌊 Sequential Waterfall Engine
The `WaterfallManager` is the orchestrator of AdGlide. It ensures **100% fill rates** by intelligently rotating through backup networks (Meta, AppLovin, StartApp, etc.) only when the primary provider fails to fill a request.

### 🛡️ Intelligent Rate Limiting
AdGlide includes built-in defensive logic to prevent account flags (like "AdMob Error 3"). The SDK applies exponential backoff to failing ad units in real-time, protecting your developer account health without sacrificing user experience.

---

## 🎨 2. Premium Native Templates

AdGlide eliminates the need for complex custom XML for most projects by providing 4 distinct, high-converting templates:

| Layout | Descriptor | Ideal Use Case |
| :--- | :--- | :--- |
| `small` | Compact Radio | List items / Small footers |
| `medium` | Standard Box | News feeds / Card views |
| `banner` | Content Blend | Article inline placements |
| `video` | Immersive Media | High-CPM video rewards |

> [!TIP]
> Use the `.style()` API in the `NativeAd.Builder` to switch between these templates effortlessly.

---

## 📦 3. Module Structure

AdGlide follows a strict **Clean Architecture** principle to ensure zero memory leaks and thread-safe operations:

- **`com.partharoypc.adglide`**: Main entry Facade and Global Configuration.
- **`format/`**: Lifecycle-aware ad type implementations (AppOpen, Banner, etc.).
- **`gdpr/`**: Modern UMP consent logic for European privacy compliance.
- **`util/`**: The core "brain" including the Waterfall engine, Rate Limiter, and Performance Loggers.

---

## 🛠 4. Advanced Development

### 🎯 Custom Native Integration
```java
new NativeAd.Builder(activity)
    .style(AdGlideNativeStyle.MEDIUM) // Choose baseline layout
    .container(myLayout)              // Bind to your UI
    .backgroundColor(Color.WHITE)      // Optional styling
    .load();
```

### 📱 Manual App Open Logic
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

## 📋 5. Specifications
- **Format**: Android Library (`.aar`)
- **Compatibility**: Android 6.0+ (API 23+)
- **Targeting**: Android 15 (API 35)
- **Performance**: Optimized for zero impact on app startup time.

---

*Built for Scale. Optimized for Speed. Perfected for Developers.*
*© 2026 AdGlide — All rights reserved.*
