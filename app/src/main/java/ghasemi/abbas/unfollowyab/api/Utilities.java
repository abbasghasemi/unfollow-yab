package ghasemi.abbas.unfollowyab.api;

import android.text.TextUtils;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ghasemi.abbas.unfollowyab.builder.Store;

public class Utilities {

    public static String userID() {
        if (IgUser.currentIgUser == null) {
            throw new RuntimeException("currentUserID is null.");
//            return Store.data().getString("i_l_u_i");
        }
        return IgUser.currentIgUser.userID;
    }

    public static void setUserID(String user_id) {
        Store.data().putString("i_l_u_i", user_id);
    }

    public static String getUserID() {
        return Store.data().getString("i_l_u_i");
    }

    public static String authorization(String user_pk, String sid) {
        if (TextUtils.isEmpty(sid)) return "";
        JSONObject object = new JSONObject();
        try {
            object.put("ds_user_id", user_pk);
            object.put("sessionid", sid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return IgLogin.AUTHORITY_START + Base64.encodeToString(object.toString().getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
    }

    public static String[] openAuthorization(String auth) {
        if (auth != null) {
            String[] auth2 = auth.split(":");
            if (auth2.length == 3) {
                try {
                    JSONObject object = new JSONObject(new String(Base64.decode(auth2[2], Base64.NO_WRAP)));
                    String uid = object.getString("ds_user_id");
                    String sid = object.getString("sessionid");
                    return new String[]{uid, sid};
                } catch (Exception e) {
                    //
                }
            }
        }
        return null;
    }

    public static String cookieEncode(Map<String, String> cookies) {
        StringBuilder builder = new StringBuilder();
        for (String key : cookies.keySet()) {
            builder.append(key).append("=").append(cookies.get(key)).append("; ");
        }
        return builder.toString().trim();
    }

    public static Map<String, String> cookieDecode(String cookie) {
        Map<String, String> _cookies = new HashMap<>();
        if (!TextUtils.isEmpty(cookie)) {
            for (String key_value : cookie.split(";")) {
                key_value = key_value.trim();
                int index = key_value.indexOf('=');
                if (index != -1) {
                    try {
                        _cookies.put(key_value.substring(0, index), key_value.substring(index + 1));
                    } catch (Exception e) {
                        //
                    }
                }
            }
        }
        return _cookies;
    }

    public static String findFromCookie(String key, String cookie) {
        if (!cookie.endsWith(";")) {
            cookie += ";";
        }
        Matcher matcher = Pattern.compile(key + "=(.*?);").matcher(cookie);
        key = "";
        while (matcher.find()) {
            key = matcher.group(1);
        }
        return key;
    }

    public static String androidID() {
        return "android-" + md5(getRandomString()).substring(16);
    }

    private static String md5(String str) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2) h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            //
        }
        return "1b26ac35ef97d";
    }

    private static String getRandomString() {
        String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
}