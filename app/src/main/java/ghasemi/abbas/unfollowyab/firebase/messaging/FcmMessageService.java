package ghasemi.abbas.unfollowyab.firebase.messaging;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import ghasemi.abbas.unfollowyab.ApplicationLoader;
import ghasemi.abbas.unfollowyab.BuildConfig;
import ghasemi.abbas.unfollowyab.MainActivity;
import ghasemi.abbas.unfollowyab.builder.Store;

public class FcmMessageService extends FirebaseMessagingService {

    private String CHANNEL_ID = "public_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        ApplicationLoader.setContext(getApplicationContext());
        Map<String, String> data = remoteMessage.getData();
        if (data.containsKey("type")) {
            if (data.containsKey("target_market")) {
                if (!data.get("target_market").equals(BuildConfig.FLAVOR)) {
                    return;
                }
            }
            if (data.containsKey("target_version") || data.containsKey("target_versions")) {
                switch (data.get("target_version_operation")) {
                    case ">": {
                        int target_version = Integer.parseInt(data.get("target_version"));
                        if (BuildConfig.VERSION_CODE <= target_version) {
                            return;
                        }
                    }
                    break;
                    case "<": {
                        int target_version = Integer.parseInt(data.get("target_version"));
                        if (BuildConfig.VERSION_CODE >= target_version) {
                            return;
                        }
                    }
                    break;
                    case "==": {
                        int target_version = Integer.parseInt(data.get("target_version"));
                        if (BuildConfig.VERSION_CODE != target_version) {
                            return;
                        }
                    }
                    break;
                    case "!=": {
                        int target_version = Integer.parseInt(data.get("target_version"));
                        if (BuildConfig.VERSION_CODE == target_version) {
                            return;
                        }
                    }
                    break;
                    // target_versions
                    case "contains":
                    case "not contain":
                        boolean contains = data.get("target_version_operation").equals("contains");
                        String[] versions = data.get("target_versions").split(",");
                        boolean found = false;
                        for (String v : versions) {
                            if (v.equals("" + BuildConfig.VERSION_CODE)) {
                                found = true;
                                break;
                            }
                        }
                        if (contains && !found) return;
                        else if (!contains && found) return;
                        break;
                }
            }
            if (data.containsKey("target_last_app_engagement")) {
                long lastLaunch = Store.data().getLong("lastLaunch", 0);
                try {
                    lastLaunch = (System.currentTimeMillis() / 1000) - lastLaunch;
                    lastLaunch /= 60 * 60 * 24;
                    int minDay = Integer.parseInt(data.get("target_lae_min_day"));
                    int maxDay = data.containsKey("target_lae_max_day") ? Integer.parseInt(data.get("target_lae_max_day")) : 0;
                    if (minDay <= 0 || minDay > lastLaunch || (maxDay != 0 && maxDay < lastLaunch)) {
                        return;
                    }
                } catch (Exception e) {
                    //
                }
            }
            String language = getResources().getConfiguration().locale.getLanguage().toLowerCase();
            if (data.containsKey("title_loc_args")) {
                try {
                    JSONObject object = new JSONObject(data.get("title_loc_args"));
                    if (object.has(language)) {
                        data.put("title", object.getString(language));
                    }
                } catch (JSONException e) {
                    //
                }
            }
            if (data.containsKey("body_loc_args")) {
                try {
                    JSONObject object = new JSONObject(data.get("body_loc_args"));
                    if (object.has(language)) {
                        data.put("body", object.getString(language));
                    }
                } catch (JSONException e) {
                    //
                }
            }
            if (data.containsKey("actions")) {
                try {
                    JSONArray jsonArray = new JSONArray(data.get("actions"));
                    JSONArray newJsonArray = new JSONArray();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.has("name_loc_args")) {
                            JSONObject object = jsonObject.getJSONObject("name_loc_args");
                            if (object.has(language)) {
                                jsonObject.put("name", object.getString(language));
                            }
                            jsonObject.remove("name_loc_args");
                        }
                        newJsonArray.put(jsonObject);
                    }
                    data.put("actions", newJsonArray.toString());
                } catch (JSONException e) {
                    //
                }
            }
            if (data.containsKey("image_url")) {
                data.put("image_url", Uri.parse(data.get("image_url")).buildUpon().appendQueryParameter("lan", language).build().toString());
            }
            if (data.containsKey("update_app") && data.get("update_app").equals("true")) {
                Store.data().putInt("updateTarget", BuildConfig.VERSION_CODE);
                if (data.containsKey("update_message")) {
                    Store.data().putString("updateMessage", data.get("update_message"));
                } else {
                    Store.data().putString("updateMessage", "");
                }
            }
            if (data.get("type").equals("notification")) {
                Notification.notification(data, CHANNEL_ID);
            } else if (data.get("type").equals("inAppMessage")) {
                try {
                    JSONObject o = new JSONObject();
                    o.put("title", data.get("title"));
                    o.put("body", data.get("body"));
                    o.put("actions", new JSONArray(data.containsKey("actions") ? data.get("actions") : "[]"));
                    o.put("image_url", data.get("image_url"));
                    o.put("use_material_dialog", data.containsKey("use_material_dialog") && data.get("use_material_dialog").equals("true"));
                    if (!MainActivity.isRunning && data.containsKey("can_convert2notification") && data.get("can_convert2notification").equalsIgnoreCase("true")) {
                        Notification.notification(data, CHANNEL_ID);
                    } else {
                        ParsCustomContent.inAppMessage(o);
                    }
                } catch (JSONException e) {
                    //
                }
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
