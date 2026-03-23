# AdGlide Library Module 🚀
### *The Industrial-Grade Core Engine for Android Ad Mediation*

[![Android](https://img.shields.io/badge/Android-23%2B-green.svg)](https://developer.android.com)
[![Compile SDK](https://img.shields.io/badge/Compile_SDK-36-green.svg)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-Clean-blue.svg)](https://en.wikipedia.org/wiki/Clean_code)

This module contains the high-performance source code for the **AdGlide SDK**. It is engineered for stability, zero-latency failovers, and maximum revenue optimization.

---

## 🏗️ 1. Technical Architecture

### 🌊 Sequential Waterfall Engine
The `WaterfallManager` is the orchestrator of AdGlide. It ensures **100% fill rates** by intelligently rotating through backup networks (Meta, AppLovin, StartApp, etc.).
- **New**: Configurable `adResponseTimeoutMs` (default 3500ms) allows tuning for slow network conditions.

### 🛡️ Intelligent Rate Limiting
AdGlide applies exponential backoff to failing ad units in real-time, protecting your developer account health without sacrificing user experience.

### ⚡ Zero-Wait Ad Caching (Pool System)
The `AdPoolManager` pre-warms ads in the background using a dual-pool system to ensure instant delivery from memory.

---

## 🎨 2. Premium Native Templates

AdGlide provides 4 distinct templates via the `.style()` API:
- `small`, `medium`, `banner`, `video`.

---

## 🔐 3. Privacy & Compliance

### 🇪🇺 Universal GDPR (UMP)
AdGlide integrates the latest Google UMP SDK with support for debug geography and child-directed consent.

---

## 🛠 4. Developer Experience (DX)

### 🎯 Kotlin DSL Support
Initialize AdGlide with modern Kotlin syntax using `adGlideConfig { ... }`.

### 📱 Debug HUD (Secret Menu)
Monitor waterfall logs and test ad fills in real-time with the built-in Debug HUD.


---

## 📋 5. Specifications
- **Format**: Android Library (`.aar`)
- **Compatibility**: Android 6.0+ (API 23+)
- **Targeting**: Android 15 (API 35)
- **Performance**: Optimized for zero impact on app startup time.

---

*Built for Scale. Optimized for Speed. Perfected for Developers.*
*© 2026 AdGlide — All rights reserved.*
