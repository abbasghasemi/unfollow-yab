package ghasemi.abbas.unfollowyab;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.builder.Store;
import ghasemi.abbas.unfollowyab.components.Permission;
import ghasemi.abbas.unfollowyab.components.TextView;
import ghasemi.abbas.unfollowyab.firebase.messaging.Notification;
import ghasemi.abbas.unfollowyab.fragment.PrivacyPolicy;

public class LoginActivity extends AppCompatActivity {
    private AnimationDrawable animationDrawable;
    private boolean askNotificationPermission;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        animationDrawable = (AnimationDrawable) findViewById(R.id.anim_list).getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        findViewById(R.id.checkBox2).setOnClickListener(view -> {
            PrivacyPolicy privacyPolicy = new PrivacyPolicy();
            privacyPolicy.setArguments(new Bundle());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, privacyPolicy).addToBackStack("back").commit();
        });

        findViewById(R.id.go_to_login).setOnClickListener(v -> {
            if (!askNotificationPermission && !new Notification().areNotificationsEnabled(this)) {
                askNotificationPermission = true;
                new Permission(this, v1 -> {
                    Intent intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);
                    intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
                    startActivity(intent);
                }, "مجوز notification", "برنامه برای اطلاع رسانی ها، بروزرسانی ها و فعالیت های برنامه، نیاز به دسترسی اعلان دارد.\nبا صدور مجوز اعلان سریع تر از دیگران با خبر شوید.", R.drawable.ic_round_notifications_24);
                return;
            }
            webLogin();
        });

        anim();


        if (Store.data().getInt("updateTarget") == BuildConfig.VERSION_CODE) {
            final Dialog dialog = new Dialog(this);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_update);
            dialog.findViewById(R.id.cancel).setOnClickListener(v -> dialog.dismiss());
            dialog.findViewById(R.id.update).setOnClickListener(v -> {
                Intent update = new Intent(Intent.ACTION_VIEW);
                update.setData(Uri.parse("https://" + BuildConfig.FLAVOR + ".ir/app/" + BuildConfig.APPLICATION_ID));
                startActivity(update);
                dialog.dismiss();
            });
            TextView message = dialog.findViewById(R.id.message);
            String update = Store.data().getString("updateMessage" + BuildConfig.VERSION_CODE);
            if (!TextUtils.isEmpty(update)) {
                message.setText(update);
            }
            dialog.show();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.back_dialog);
            dialog.getWindow().setWindowAnimations(R.style.anim_dialog);
        }
    }

    private void anim() {
        Animation fade = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        final Animation bottom = new TranslateAnimation(0, 0, 400, 0);
        AnimationSet animBottom = new AnimationSet(true);
        animBottom.setDuration(1000);
        animBottom.addAnimation(fade);
        animBottom.addAnimation(bottom);
        findViewById(R.id.logo).startAnimation(animBottom);
    }

    long time = 0;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else {
            long time = System.currentTimeMillis() - this.time;
            if (2000 >= time) {
                super.onBackPressed();
            } else {
                this.time = System.currentTimeMillis();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null) {
            animationDrawable.start();
        }
        if (askNotificationPermission) {
            webLogin();
        }
    }

    private void webLogin() {
        if (BuildApp.webViewEnabled()) {
            startActivity(new Intent(LoginActivity.this, WebLoginActivity.class));
        } else {
            BuildApp.setCustomFontDialog(new AlertDialog.Builder(this).setIcon(R.drawable.sys_webview).setTitle("No WebView installed!").setMessage("کاربر گرامی برای ورود به برنامه نیاز است، اپلیکیشن Android System WebView را بروزرسانی و فعال نمایید.\n\n در صورتی که تلفن شما برند هواوی و تحریم می باشد، نیاز است برنامه Huawei WebView را نصب نمایید.").setNegativeButton("تنظیمات", (dialogInterface, i) -> {
                if (BuildApp.appInstalledOrNot("com.google.android.webview")) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", "com.google.android.webview", null);
                    intent.setData(uri);
                    startActivity(intent);
                } else if (BuildApp.appInstalledOrNot("com.huawei.webview")) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", "com.huawei.webview", null);
                    intent.setData(uri);
                    startActivity(intent);
                } else if (BuildApp.appInstalledOrNot("com.android.vending")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setPackage("com.android.vending");
                    intent.setData(Uri.parse("market://details?id=com.google.android.webview"));
                    startActivity(intent);
                } else if (BuildApp.appInstalledOrNot("com.huawei.appmarket")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setPackage("com.huawei.appmarket");
                    intent.setData(Uri.parse("appmarket://details?id=com.huawei.webview"));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.webview"));
                    try {
                        startActivity(intent);
                    } catch (Exception exception) {
                        //
                    }
                }
            }).show());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }
}
