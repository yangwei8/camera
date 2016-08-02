# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\InstallPackage\Android\android_sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepattributes EnclosingMethod
-dontwarn com.tencent.weibo.sdk.**
-keepattributes *Annotation*
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
# Glide图片库的混淆处理
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 高德地图混淆脚本
-keep class com.android.support.**{ *; }
-keep interface android.support.v4.app.**{ *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

-dontwarn com.amap.api.**
-dontwarn com.a.a.**
-dontwarn com.autonavi.**
-keep class com.amap.api.** {*;}
-keep class com.autonavi.** {*;}
-keep class com.a.a.** {*;}

# Gson混淆脚本
-keep class com.google.gson.stream.** {*;}
-keep class com.leautolink.leautocamera.domain.** {*;}

# -------------系统类不需要混淆 --------------------------
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.**
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * { # 保持native方法不被混淆
    native <methods>;
}
-keepclasseswithmembernames class * { # 保持自定义控件不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembernames class * { # 保持自定义控件不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers enum * { # 保持枚举enum类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable { # 保持Parcelable不被混淆
  public static final android.os.Parcelable$Creator *;
}

-keep class * implements android.os.Parcelable { # 保持Parcelable不被混淆
  public static final android.os.Parcelable$Creator *;
}

# ---------------- eventbus避免混淆 ------------
-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}