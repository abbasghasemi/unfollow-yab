package ghasemi.abbas.unfollowyab.firebase.messaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Map;

import ghasemi.abbas.unfollowyab.ApplicationLoader;
import ghasemi.abbas.unfollowyab.LauncherActivity;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.builder.Store;

public class Notification {

    public static int NOTIFICATION_ID = 0;

    public static void init() {
        if (!Store.data().getBool("subTopicAll")) {
            FirebaseMessaging.getInstance().subscribeToTopic("all").addOnCompleteListener((OnCompleteListener<Void>) task -> Store.data().putBool("subTopicAll", task.isSuccessful()));
        }
    }

    public boolean areNotificationsEnabled(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N || notificationManager.areNotificationsEnabled();
    }

    public static void removeChannel(Context context, String channelID) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(channelID) != null) {
            notificationManager.deleteNotificationChannel(channelID);
        }
    }

    public static void removeAndCreateChannel(Context context, String channelID) {
        removeChannel(context, channelID);
        createChannel(context, channelID);
    }

    public static void createChannel(Context context, String channelID) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(channelID) == null) {
            NotificationChannel notificationChannel = new NotificationChannel(channelID, channelID, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public static void cancel(int id) {
        NotificationManager notificationManager = (NotificationManager) ApplicationLoader.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    public static NotificationCompat.Builder createNotification(Context context, String channelID, String title, String body, String url, String name, Uri image) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID).setAutoCancel(true).setOnlyAlertOnce(true).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title).setContentText(body);
        Intent intent;
        if (TextUtils.isEmpty(url)) {
            intent = new Intent(ApplicationLoader.getContext(), LauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            PendingIntent pendingIntent = PendingIntent.getActivity(ApplicationLoader.getContext(), 1048, intent, android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0);
            builder.addAction(0, TextUtils.isEmpty(name) ? "باز کردن" : name, pendingIntent);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(ApplicationLoader.getContext(), 1248, intent, android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0);
        builder.setContentIntent(pendingIntent);
        if (image == null) {
            if (body.length() > 20) {
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
            }
        }
        return builder;
    }

    public static void notification(@NonNull Map<String, String> data, @NonNull String channel_id) {
        if (data.containsKey("channel_id")) {
            channel_id = data.get("channel_id");
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ApplicationLoader.getContext(), channel_id).setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher).setColor(ApplicationLoader.getContext().getResources().getColor(R.color.colorPrimary)).setVibrate(new long[]{0, 250, 250, 250}).setContentTitle(data.get("title")).setContentText(data.get("body"));
        if (data.containsKey("click_action")) {
            PendingIntent pendingIntent = PendingIntent.getActivity(ApplicationLoader.getContext(), 3, ParsCustomContent.dataParser(data.get("click_action")), Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT : PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        }
        if (data.containsKey("actions")) {
            try {
                JSONArray jsonArray = new JSONArray(data.get("actions"));
                ParsCustomContent.actionsParser(jsonArray, (i, intent, name) -> {
                    Intent broad = new Intent(ApplicationLoader.getContext().getPackageName() + ".ACTION_CLICK").setComponent(new ComponentName(ApplicationLoader.getContext(), NotificationClickAction.class));
                    broad.putExtra("click_action", intent);
                    broad.putExtra("notification_id", NOTIFICATION_ID);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(ApplicationLoader.getContext(), i, broad, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT : PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.addAction(0, name, pendingIntent);
                });
            } catch (JSONException e) {
                //
            }
        }
        Uri image = null;
        if (!data.containsKey("image_url")) {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(data.get("body")));
        } else {
            image = Uri.parse(data.get("image_url"));
        }
        NotificationManager notificationManager = (NotificationManager) ApplicationLoader.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(channel_id) == null) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, channel_id, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{0, 250, 250, 250});
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(ApplicationLoader.getContext().getColor(R.color.colorPrimary));
            notificationManager.createNotificationChannel(notificationChannel);
        }
        if (image == null) {
            notificationManager.notify(NOTIFICATION_ID++, builder.build());
        } else {
            Glide.with(ApplicationLoader.getContext()).load(image).into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(drawableToBitmap(resource)));
                    notificationManager.notify(NOTIFICATION_ID++, builder.build());
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    notificationManager.notify(NOTIFICATION_ID++, builder.build());
                }
            });
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}