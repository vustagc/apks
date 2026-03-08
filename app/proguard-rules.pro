# -------------------------------------------------
# Android View constructors (needed for AndroidView)
# -------------------------------------------------
-keepclassmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# -------------------------------------------------
# Keep your UI package
# -------------------------------------------------
-keep class pt.isec.ipc.ui.** { *; }

# -------------------------------------------------
# WebView Javascript interfaces (only needed if used)
# -------------------------------------------------
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# -------------------------------------------------
# Jetpack Compose safety rules
# -------------------------------------------------
-keep class androidx.compose.** { *; }
-keep class kotlin.Metadata { *; }

# Needed for Compose reflection metadata
-keepattributes *Annotation*

# -------------------------------------------------
# Debug stack traces
# -------------------------------------------------
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# -------------------------------------------------
# TLS / SSL libraries warnings
# -------------------------------------------------
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**

# -------------------------------------------------
# Compose internal class sometimes stripped
# -------------------------------------------------
-keep class androidx.compose.ui.platform.AndroidCompositionLocals_androidKt { *; }
