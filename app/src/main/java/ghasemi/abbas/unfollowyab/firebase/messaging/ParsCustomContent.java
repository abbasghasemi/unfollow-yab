package ghasemi.abbas.unfollowyab.firebase.messaging;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ghasemi.abbas.unfollowyab.ApplicationLoader;
import ghasemi.abbas.unfollowyab.BuildConfig;
import ghasemi.abbas.unfollowyab.LauncherActivity;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.builder.Store;
import ghasemi.abbas.unfollowyab.components.TextView;

public class ParsCustomContent {

    public static void inAppMessage(Activity activity, JSONObject o) throws JSONException {
        JSONArray jsonArray = o.getJSONArray("actions");
        if (o.getBoolean("use_material_dialog")) {
            View layout = activity.getLayoutInflater().inflate(R.layout.dialog_inapp_message, null);
            AppCompatImageView image_cover = layout.findViewById(R.id.image_cover);
            if (o.has("image_url") && !TextUtils.isEmpty(o.getString("image_url"))) {
                Glide.with(activity).load(o.getString("image_url")).into(image_cover);
            }
            TextView title = layout.findViewById(R.id.title);
            title.setText(o.getString("title"));
            TextView body = layout.findViewById(R.id.body);
            body.setText(o.getString("body"));
            MaterialButton btn1 = layout.findViewById(R.id.btn1);
            MaterialButton btn2 = layout.findViewById(R.id.btn2);
            MaterialButton btn3 = layout.findViewById(R.id.btn3);
            AlertDialog alertDialog = new AlertDialog.Builder(activity).setCancelable(false).setView(layout).create();
            alertDialog.getWindow().setWindowAnimations(R.style.anim_dialog);
            alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.back_dialog);
            btn3.setOnClickListener(view -> alertDialog.dismiss());
            ParsCustomContent.actionsParser(jsonArray, (i, intent, name) -> {
                switch (i) {
                    case 0:
                        btn1.setText(name);
                        btn1.setOnClickListener(view -> {
                            if (intent != null) activity.startActivity(intent);
                        });
                        btn1.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        btn2.setText(name);
                        btn2.setOnClickListener(view -> {
                            if (intent != null) activity.startActivity(intent);
                        });
                        btn2.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        btn3.setText(name);
                        btn3.setOnClickListener(view -> {
                            if (intent != null) activity.startActivity(intent);
                        });
                        break;
                }
            });
            alertDialog.show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(o.getString("title"));
        builder.setMessage(o.getString("body"));
        builder.setCancelable(false);
        builder.setNeutralButton("باشه", null);
        ParsCustomContent.actionsParser(jsonArray, (i, intent, name) -> builder.setPositiveButton(name, (intent == null) ? null : (dialogInterface, i1) -> activity.startActivity(intent)));
        builder.show();
    }

    public static Intent dataParser(String data) {
        Intent intent;
        if (data.startsWith("openActivity")) {
            intent = new Intent(ApplicationLoader.getContext(), LauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else if (!data.equals("dismiss")) {
            intent = new Intent(Intent.ACTION_VIEW);
            if (data.contains(":")) {
                intent.setData(Uri.parse(data));
            } else {
                intent.setData(Uri.parse("https://" + BuildConfig.FLAVOR + ".ir/app/" + data));
            }
        } else {
            intent = null;
        }
        return intent;
    }

    public static void actionsParser(JSONArray jsonArray, ActionsParserListener parserListener) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Intent intent = dataParser(jsonObject.getString("data"));
            parserListener.onPars(i, intent, jsonObject.getString("name"));
        }
    }

    public interface ActionsParserListener {
        void onPars(int i, Intent intent, String name);
    }

    public static void run(Activity activity) {
        try {
            String notificationInAppMessages = Store.data().getString("notificationInAppMessages");
            if (!notificationInAppMessages.isEmpty()) {
                JSONArray jsonArray = new JSONArray(notificationInAppMessages);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        inAppMessage(activity, jsonObject);
                    }
                    Store.data().putString("notificationInAppMessages", "");
                }
            }
        } catch (Exception e) {
            //
        }
    }

    public static void inAppMessage(JSONObject jsonObject) {
        JSONArray jsonArray = new JSONArray();
        String notificationInAppMessages = Store.data().getString("notificationInAppMessages");
        if (!notificationInAppMessages.isEmpty()) {
            try {
                jsonArray = new JSONArray(notificationInAppMessages);
            } catch (JSONException e) {
                //
            }
        }
        jsonArray.put(jsonObject);
        Store.data().putString("notificationInAppMessages", jsonArray.toString());
    }

}
