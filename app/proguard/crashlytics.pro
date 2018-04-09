# crashlytics

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exceptions
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**