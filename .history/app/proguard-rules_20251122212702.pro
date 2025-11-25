# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep source file and line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ============================================
# PLATEPILOT APP CLASSES
# ============================================

# Keep all model classes (Room entities, data classes)
-keep class com.bodycalc.platepilot.data.model.** { *; }
-keepclassmembers class com.bodycalc.platepilot.data.model.** { *; }

# Keep repository classes
-keep class com.bodycalc.platepilot.data.repository.** { *; }

# Keep remote/network service classes AND their methods
-keep class com.bodycalc.platepilot.data.remote.** { *; }
-keep interface com.bodycalc.platepilot.data.remote.** { *; }

# ============================================
# ROOM DATABASE
# ============================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface *
-keepclassmembers @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.paging.**

# ============================================
# RETROFIT - CRITICAL FOR GITHUB SYNC
# ============================================
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions

# Keep Retrofit service methods (THIS IS THE KEY FIX)
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}

# Keep generic signature of Call, Response (R8 full mode strips this)
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# ============================================
# OKHTTP
# ============================================
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ============================================
# GSON - JSON PARSING
# ============================================
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from stripping fields used by Gson
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep all fields in data classes (even without @SerializedName)
-keepclassmembers class com.bodycalc.platepilot.data.model.** {
    <fields>;
}

# ============================================
# KOTLINX SERIALIZATION (if using)
# ============================================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keep,includedescriptorclasses class com.bodycalc.platepilot.**$$serializer { *; }
-keepclassmembers class com.bodycalc.platepilot.** {
    *** Companion;
}
-keepclasseswithmembers class com.bodycalc.platepilot.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

# ============================================
# KOTLIN
# ============================================
# CRITICAL: Keep Kotlin metadata for reflection
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata { *; }

-dontwarn kotlin.**
-keep class kotlin.reflect.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**

# ============================================
# VIEWMODELS
# ============================================
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>();
    <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
    <init>(...);
}

# ============================================
# ADMOB
# ============================================
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# ============================================
# ENUMS (often stripped incorrectly)
# ============================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================================
# JETPACK COMPOSE
# ============================================
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ============================================
# COIL (Image Loading)
# ============================================
-keep class coil.** { *; }
-dontwarn coil.**