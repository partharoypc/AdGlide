# AdGlide Library Module 🚀
### The Core Engine for High-Performance Ad Mediation

This module contains the professional-grade source code for the **AdGlide SDK**. It is engineered for stability, maximum fill rates, and zero-latency failovers.

---

## 🏗️ Technical Architecture
### Sequential Waterfall Manager
The `WaterfallManager` is the brain of AdGlide. It ensures 100% fill rates by rotating through backup networks (Meta, StartApp, Unity, IronSource, AppLovin, Wortise) only if the previous network fails to fill.

### Intelligent Rate Limiting
Built-in defensive logic prevents "AdMob Error 3" request loops by applying exponential backoff to failing ad units, protecting your account health.

---

## 🎨 Professional Native Templates
AdGlide provides premium XML templates that you can include directly:
- `adglide_view_native_ad_small` — Compact radio/list style.
- `adglide_view_native_ad_medium` — Standard banner-replacement style.
- `adglide_view_native_ad_news` — Content-blending news style.
- `adglide_view_native_ad_video_large` — High-impact immersive video style.

---

## 📦 SDK Module Structure
```
com.partharoypc.adglide
├── AdGlide.java                # The Facade: 1-line static methods
├── AdGlideConfig.java          # Configuration: Builder pattern
├── format/
│   ├── AppOpenAd.java          # Automated Lifecycle Management
│   ├── BannerAd.java           # Adaptive & Collapsible rendering
│   ├── InterstitialAd.java     # Managed Frequency Capping
│   ├── RewardedAd.java         # Safe Reward Disbursement
│   └── NativeAd.java           # Multi-Type Template Engine
├── gdpr/
│   └── GDPR.java               # Google UMP (EU Consent)
└── util/
    ├── WaterfallManager.java   # Core rotation logic
    ├── RemoteConfig.java       # Dynamic Orchestration
    └── Tools.java              # Security & Base64 decoding
```

---

## 🛠 Advanced Developer Usage

### Custom Native Container
```java
new NativeAd.Builder(activity)
    .style(AdGlideNativeStyle.MEDIUM)
    .container(myCustomLayout)
    .backgroundColor(Color.WHITE)
    .load();
```

### Manual App Open Trigger
```java
new AppOpenAd.Builder(activity)
    .adMobId("YOUR_ID")
    .load(new OnShowAdCompleteListener() {
        @Override
        public void onShowAdComplete() {
            // Proceed to Main
        }
    });
```

---

## 📋 Module Specifications
- **Project Structure**: Android Library (`.aar`)
- **Min SDK**: `23` (Android 6.0)
- **Target SDK**: `35` (Android 15)
- **Dependencies**: Modular & Reflection-aware (Optional dependencies)

---
*Optimized for Revenue. Built for Developers.*
