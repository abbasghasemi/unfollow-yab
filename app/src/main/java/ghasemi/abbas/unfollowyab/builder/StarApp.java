package ghasemi.abbas.unfollowyab.builder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AlertDialog;

import ghasemi.abbas.unfollowyab.ApplicationLoader;
import ghasemi.abbas.unfollowyab.BuildConfig;
import ghasemi.abbas.unfollowyab.R;

public class StarApp {
    private AlertDialog alertDialog;
    private final Activity activity;

    public StarApp(Activity activity) {
        this.activity = activity;
    }

    public void show() {
        if (Store.data().getBool("rate_app")) {
            return;
        }
        View view = LayoutInflater.from(activity).inflate(R.layout.star, null);
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuildConfig.FLAVOR.equals("cafebazaar") ? Intent.ACTION_EDIT : Intent.ACTION_VIEW);
                String uri;
                if (BuildConfig.FLAVOR.equals("cafebazaar")) {
                    uri = "bazaar://details?id=" + ApplicationLoader.getContext().getPackageName();
                } else {
                    uri = "myket://comment?id=" + ApplicationLoader.getContext().getPackageName();
                }
                intent.setData(Uri.parse(uri));
                try {
                    activity.startActivity(intent);
                } catch (Exception e) {
                    BuildApp.Toast("ابتدا اپ استور " + BuildConfig.FLAVOR + " را نصب نمایید.");
                }
                alertDialog.dismiss();
                Store.data().putBool("rate_app", true);
            }
        });

        View s1 = view.findViewById(R.id.s1);
        s1.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.scale));
        final View s2 = view.findViewById(R.id.s2);
        s2.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.scale));
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 120);
        final View s3 = view.findViewById(R.id.s3);
        s3.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.scale));
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 240);
        final View s4 = view.findViewById(R.id.s4);
        s4.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.scale));
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 360);
        final View s5 = view.findViewById(R.id.s5);
        s5.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.scale));
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                s5.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.scale));
//            }
//        }, 480);

        alertDialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(false)
                .show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}
