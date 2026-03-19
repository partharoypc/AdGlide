# ============================================================================
# AdGlide SDK — Consumer ProGuard/R8 Rules
# These rules are automatically applied to apps that depend on this library.
# ============================================================================

# --- SDK Public API ---
-keep public class com.partharoypc.adglide.format.** { public *; }
-keep public class com.partharoypc.adglide.gdpr.** { public *; }
-keep public class com.partharoypc.adglide.helper.** { public *; }
-keep public class com.partharoypc.adglide.ui.** { public *; }
-keep public class com.partharoypc.adglide.util.** { public *; }

# --- Google Mobile Ads / AdMob ---
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.mediation.** { *; }

# --- Meta Audience Network (FAN) ---
-keep class com.facebook.ads.** { *; }

# --- AppLovin ---
-keep class com.applovin.** { *; }

# --- Unity Ads ---
-keep class com.unity3d.ads.** { *; }
-keep class com.unity3d.services.** { *; }

# --- IronSource ---
-keep class com.ironsource.** { *; }

# --- StartApp ---
-keep class com.startapp.** { *; }

# --- Wortise ---
-keep class com.wortise.** { *; }

# --- Google UMP (GDPR Consent) ---
-keep class com.google.android.ump.** { *; }

# --- Prevent stripping of ad callback interfaces ---
-keep interface com.partharoypc.adglide.util.On*Listener { *; }
-keep interface com.partharoypc.adglide.util.AdGlideCallback { *; }
-keep class com.partharoypc.adglide.AdGlideConfig { *; }
-keep class com.partharoypc.adglide.AdGlideConfig$Builder { *; }
-keep class com.partharoypc.adglide.AdGlideNativeStyle { *; }

# ============================================================================
# Optional Ad Network Dependencies (compileOnly)
# These rules prevent R8/ProGuard from crashing the build with "Missing class"
# errors when a developer chooses to omit a specific ad network.
# ============================================================================

# AdMob (Google Mobile Ads)
-dontwarn com.google.android.gms.ads.**
-dontwarn com.google.ads.mediation.**

# Google UMP
-dontwarn com.google.android.ump.**

# Meta Audience Network (FAN)
-dontwarn com.facebook.ads.**
-dontwarn com.facebook.infer.annotation.**

# AppLovin
-dontwarn com.applovin.**

# Unity Ads
-dontwarn com.unity3d.ads.**

# IronSource
-dontwarn com.ironsource.**

# StartApp
-dontwarn com.startapp.**

# Wortise / Bytedance (Pangle)
-dontwarn com.wortise.**
-dontwarn com.bytedance.**
