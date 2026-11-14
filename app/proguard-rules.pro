# NewPipeExtractor - Rhino JavaScript engine
-keep class org.mozilla.javascript.** { *; }
-keep class org.mozilla.classfile.ClassFileWriter
-dontwarn org.mozilla.javascript.tools.**

# Keep NewPipeExtractor classes
-keep class org.schabi.newpipe.extractor.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class androidx.hilt.** { *; }

# Kotlin
-keep class kotlin.** { *; }

# OkHttp
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Keep annotations
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod