package ghasemi.abbas.unfollowyab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Process;

import androidx.multidex.MultiDexApplication;

import ghasemi.abbas.unfollowyab.firebase.messaging.Notification;

public class ApplicationLoader extends MultiDexApplication {
    @SuppressLint("StaticFieldLeak")
    private static volatile Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        if (!context.getPackageName().equals(BuildConfig.APPLICATION_ID)) {
            Process.killProcess(Process.myPid());
        }

        Notification.init();
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        if (ApplicationLoader.context != null) {
            return;
        }
        ApplicationLoader.context = context;
    }
}
