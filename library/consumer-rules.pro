# =====================================================================================================
# AdGlide SDK — Consumer ProGuard / R8 Rules
# =====================================================================================================
# This configuration is automatically bundled with the AdGlide AAR and applied to any 
# consuming Android application. It ensures that the required public APIs, interfaces, 
# and ad-network dependencies are not stripped or obfuscated unexpectedly by the app's R8.
# =====================================================================================================

# -----------------------------------------------------------------------------------------------------
# 1. Core SDK & Public Interfaces
# -----------------------------------------------------------------------------------------------------
# Preserve the primary configuration, initialization structures, and models
-keep public class com.partharoypc.adglide.AdGlideConfig { public *; }
-keep public class com.partharoypc.adglide.AdGlideConfig$Builder { public *; }
-keep public class com.partharoypc.adglide.AdGlideNativeStyle { public *; }

# Preserve all public packages containing ad formats, GDPR forms, helpers, and UI elements
-keep public class com.partharoypc.adglide.format.** { public *; }
-keep public class com.partharoypc.adglide.gdpr.** { public *; }
-keep public class com.partharoypc.adglide.helper.** { public *; }
-keep public class com.partharoypc.adglide.ui.** { public *; }
-keep public class com.partharoypc.adglide.util.** { public *; }

# Strictly preserve callback interfaces so they are successfully triggered by network responses
-keep interface com.partharoypc.adglide.util.On*Listener { *; }
-keep interface com.partharoypc.adglide.util.AdGlideCallback { *; }

# -----------------------------------------------------------------------------------------------------
# 2. Third-Party Ad Networks & Mediation Preservation
# -----------------------------------------------------------------------------------------------------
# Vital keep configurations to prevent dynamic reflection crashes in ad modules.

# AdMob (Google Mobile Ads)
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.mediation.** { *; }

# Meta Audience Network (FAN)
-keep class com.facebook.ads.** { *; }

# AppLovin 
-keep class com.applovin.** { *; }

# Unity Ads
-keep class com.unity3d.ads.** { *; }
-keep class com.unity3d.services.** { *; }

# IronSource
-keep class com.ironsource.** { *; }

# StartApp
-keep class com.startapp.** { *; }

# Wortise / Bytedance (Pangle)
-keep class com.wortise.** { *; }
-keep class com.bytedance.** { *; }

# Google UMP (GDPR Consent)
-keep class com.google.android.ump.** { *; }

# -----------------------------------------------------------------------------------------------------
# 3. Suppress Warnings for Compile-Only Dependencies
# -----------------------------------------------------------------------------------------------------
# Because AdGlide provides flexible optional dependencies (compileOnly), consuming apps 
# omitting certain networks will trigger "Missing class" errors during their build process.
# These rules strictly bypass those warnings safely.

# AdMob & UMP
-dontwarn com.google.android.gms.ads.**
-dontwarn com.google.ads.mediation.**
-dontwarn com.google.android.ump.**

# Meta Audience Network
-dontwarn com.facebook.ads.**
-dontwarn com.facebook.infer.annotation.**

# AppLovin
-dontwarn com.applovin.**

# Unity
-dontwarn com.unity3d.ads.**

# IronSource
-dontwarn com.ironsource.**

# StartApp
-dontwarn com.startapp.**

# Wortise / Pangle
-dontwarn com.wortise.**
-dontwarn com.bytedance.**

# GIF rendering (16KB alignment polyfill)
-dontwarn pl.droidsonroids.gif.**
