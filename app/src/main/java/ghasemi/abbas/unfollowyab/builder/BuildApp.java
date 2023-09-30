package ghasemi.abbas.unfollowyab.builder;

import static androidx.annotation.Dimension.DP;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.webkit.CookieManager;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import ghasemi.abbas.unfollowyab.ApplicationLoader;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.components.TextView;


public class BuildApp {
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static float density = -1;

    public static void setCustomFontDialog(AlertDialog alertDialog) {
        Typeface bold = ResourcesCompat.getFont(ApplicationLoader.getContext(), R.font.sans_bold);
        android.widget.TextView button1 = alertDialog.findViewById(android.R.id.button1);
        if (button1 != null) {
            button1.setTypeface(bold);
        }
        android.widget.TextView button2 = alertDialog.findViewById(android.R.id.button2);
        if (button2 != null) {
            button2.setTypeface(bold);
        }
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.back_dialog);
        alertDialog.getWindow().setWindowAnimations(R.style.anim_dialog);
    }

    public static float getDensity() {
        if (density == -1) {
            density = ApplicationLoader.getContext().getResources().getDisplayMetrics().density;
        }
        return density;
    }

    public static int dp(@Dimension(unit = DP) float dp) {
        if (dp == 0) {
            return 0;
        }
        return (int) Math.ceil(getDensity() * dp);
    }


    public static void Toast(String msg) {
        try {
            Toast toast = new Toast(ApplicationLoader.getContext());
            TextView textView = new TextView(ApplicationLoader.getContext());
            textView.setTextColor(0xffffffff);
            textView.setBackgroundDrawable(ApplicationLoader.getContext().getResources().getDrawable(R.drawable.back_edit));
            textView.setPadding(dp(10), dp(10), dp(10), dp(10));
            toast.setView(textView);
            textView.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            Toast.makeText(ApplicationLoader.getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean appInstalledOrNot(String uri) {
        PackageManager pm = ApplicationLoader.getContext().getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return app_installed;
    }

    public static boolean webViewEnabled() {
        try {
            return CookieManager.getInstance() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static void addToClipboard(String str) {
        ClipboardManager clipboardManager = (ClipboardManager) ApplicationLoader.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("label", str));
        }
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            handler.post(runnable);
        } else {
            handler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        handler.removeCallbacks(runnable);
    }

}