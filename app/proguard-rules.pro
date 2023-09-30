#-------------------------------------------------Glide-------------------------------------------------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}
#-------------------------------------------------Glide-------------------------------------------------

#-------------------------------------------------Billing-------------------------------------------------
-keep class com.android.vending.billing.**
#-------------------------------------------------Billing-------------------------------------------------

#-------------------------------------------------RX-------------------------------------------------
-dontwarn java.util.concurrent.Flow*
#-------------------------------------------------RX-------------------------------------------------

###################################
-repackageclasses ''
-allowaccessmodification
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int e(...);
    public static int w(...);
}
###################################