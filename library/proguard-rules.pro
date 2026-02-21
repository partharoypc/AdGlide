# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
#-renamesourcefileattribute SourceFile

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