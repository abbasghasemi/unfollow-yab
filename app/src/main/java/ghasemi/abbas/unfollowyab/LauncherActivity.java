package ghasemi.abbas.unfollowyab;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import ghasemi.abbas.unfollowyab.api.IgUser;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.builder.Store;


public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(0xfff2f2f2);
            getWindow().setNavigationBarColor(0xffffffff);
            WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
            windowInsetsController.setAppearanceLightStatusBars(true);
            windowInsetsController.setAppearanceLightNavigationBars(true);
        } else {
            getWindow().setStatusBarColor(Color.GRAY);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        ImageView imageView = findViewById(R.id.logo);
        TextView name = findViewById(R.id.name);
        Animation fade = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation top = new TranslateAnimation(0, 0, -200, 0);
        Animation bottom = new TranslateAnimation(0, 0, 200, 0);
        AnimationSet animTop = new AnimationSet(true);
        animTop.setDuration(1000);
        AnimationSet animBottom = new AnimationSet(true);
        animBottom.setDuration(1000);

        animTop.addAnimation(fade);
        animTop.addAnimation(top);
        animBottom.addAnimation(fade);
        animBottom.addAnimation(bottom);

        imageView.startAnimation(animTop);
        name.startAnimation(animBottom);

        new Handler().postDelayed(() -> {
            if (SQL.getSql().isUserLogin()) {
                IgUser.initial();
                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
            }
            Store.data().putLong("lastLaunch", System.currentTimeMillis() / 1000);
            finish();
        }, 1100);
    }

    @Override
    public void onBackPressed() {

    }
}
