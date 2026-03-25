# =====================================================================================================
# AdGlide SDK — Internal ProGuard / R8 Configuration
# =====================================================================================================
# This configuration is applied ONLY when compiling the AdGlide library AAR (minifyEnabled true).
# It ensures the SDK is optimally obfuscated, shrunk, and optimized before distribution.
# =====================================================================================================

# -----------------------------------------------------------------------------------------------------
# 1. Inherit Consumer Rules (DRY Principle - No Duplicate Code)
# -----------------------------------------------------------------------------------------------------
# Include consumer-rules.pro to automatically inherit all '-dontwarn' and '-keep' statements
# defined for the SDK's public API and third-party compileOnly ad networks.
-include consumer-rules.pro

# -----------------------------------------------------------------------------------------------------
# 2. Debugging & Stacktrace Metadata
# -----------------------------------------------------------------------------------------------------
# Retain vital debugging metadata so that crash reports map correctly to source code line numbers.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# -----------------------------------------------------------------------------------------------------
# 3. Standard Library Attributes Preservation
# -----------------------------------------------------------------------------------------------------
# Preserve essential attributes required for reflection, generic types, and annotations.
-keepattributes Signature,*Annotation*,Exceptions,InnerClasses,EnclosingMethod

# -----------------------------------------------------------------------------------------------------
# 4. Advanced Security & Size Optimization
# -----------------------------------------------------------------------------------------------------
# Strip out standard Android Log statements to reduce AAR size and prevent leakage of 
# sensitive SDK analytics or network events in production release builds.
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
    public static int println(...);
}

# Optimize Enum values() and valueOf() methods to minimize synthetic overhead.
-assumenosideeffects class java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# -----------------------------------------------------------------------------------------------------
# 5. SDK Preservation
# -----------------------------------------------------------------------------------------------------
# Preserve the BuildConfig and entry points to ensure total stability.
-keep class com.partharoypc.adglide.AdGlide { *; }
-keep class com.partharoypc.adglide.BuildConfig { *; }
-keep class com.partharoypc.adglide.AdGlideConfig { *; }