# ============================================================================
# MatrixAds SDK — Consumer ProGuard/R8 Rules
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

# --- Google UMP (GDPR Consent) ---
-keep class com.google.android.ump.** { *; }

# --- Prevent stripping of ad callback interfaces ---
-keep interface com.partharoypc.adglide.util.On*Listener { *; }
