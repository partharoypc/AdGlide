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
# Preserve the primary entry point, configuration, and models
-keep public class com.partharoypc.adglide.AdGlide { *; }
-keep public class com.partharoypc.adglide.AdGlideConfig { public *; }
-keep public class com.partharoypc.adglide.AdGlideConfig$Builder { public *; }
-keep public class com.partharoypc.adglide.AdGlideNativeStyle { public *; }
-keep class com.partharoypc.adglide.BuildConfig { *; }

# Preserve all public packages containing ad formats, GDPR forms, helpers, and UI elements
-keep public class com.partharoypc.adglide.format.** { public *; }
-keep public class com.partharoypc.adglide.gdpr.** { public *; }
-keep public class com.partharoypc.adglide.helper.** { public *; }
-keep public class com.partharoypc.adglide.ui.** { public *; }
-keep public class com.partharoypc.adglide.util.** { public *; }

# Preserve specialized providers and initializers
-keep class com.partharoypc.adglide.util.AdGlideInitProvider { *; }
-keep class com.partharoypc.adglide.provider.** { *; }

# Strictly preserve callback interfaces so they are successfully triggered by network responses
-keep interface com.partharoypc.adglide.util.On*Listener { *; }
-keep interface com.partharoypc.adglide.util.AdGlideCallback { *; }

# Preserve vital attributes for reflection and annotations
-keepattributes Signature,*Annotation*,Exceptions,InnerClasses,EnclosingMethod

# -----------------------------------------------------------------------------------------------------
# 2. Third-Party Ad Networks & Mediation Preservation
# -----------------------------------------------------------------------------------------------------
# Vital keep configurations to prevent dynamic reflection crashes in ad modules.

# AdMob (Google Mobile Ads)
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.mediation.** { *; }

# Meta Audience Network (FAN)
-keep class com.facebook.ads.** { *; }
-keep class com.facebook.ads.internal.** { *; }

# AppLovin 
-keep class com.applovin.** { *; }
-keep class com.applovin.mediation.** { *; }

# Unity Ads
-keep class com.unity3d.ads.** { *; }
-keep class com.unity3d.services.** { *; }
-keep class com.unity3d.mediation.** { *; }

# IronSource
-keep class com.ironsource.** { *; }
-keep class com.ironsource.mediationsdk.** { *; }

# StartApp
-keep class com.startapp.** { *; }

# Wortise / Bytedance (Pangle)
-keep class com.wortise.** { *; }
-keep class com.bytedance.sdk.** { *; }

# Google UMP (GDPR Consent)
-keep class com.google.android.ump.** { *; }

# -----------------------------------------------------------------------------------------------------
# 3. Suppress Warnings for Compile-Only Dependencies
# -----------------------------------------------------------------------------------------------------
# These rules strictly bypass warnings for optional dependencies safely.

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
-dontwarn com.bytedance.sdk.**

# Generic Suppressions for common library warnings
-dontwarn androidx.annotation.**
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
-dontwarn org.checkerframework.**
-dontwarn com.google.j2objc.annotations.**
-dontwarn sun.misc.Unsafe

