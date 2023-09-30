package ghasemi.abbas.unfollowyab.firebase.messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ghasemi.abbas.unfollowyab.ApplicationLoader;
import ghasemi.abbas.unfollowyab.MainActivity;

public class NotificationClickAction extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationLoader.setContext(context);
        try {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);
        } catch (Exception exception) {
            //
        }
        int id = intent.getIntExtra("notification_id", -1);
        if (id != -1) {
            Notification.cancel(id);
        }
        Intent click_action = intent.getParcelableExtra("click_action");
        if (click_action != null && !MainActivity.isRunning) {
            try {
                click_action.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(click_action);
            } catch (Exception e) {
                //
            }
        }
    }
}