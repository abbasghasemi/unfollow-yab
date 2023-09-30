package ghasemi.abbas.unfollowyab.api;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import ghasemi.abbas.unfollowyab.ApplicationLoader;
import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class IgLogin {

    public static final String AUTHORITY_START = "Bearer IGT:2:";
    private static String androidID = "";
    private static String phoneID = "";
    private static String deviceID = "";
    private static String sessionID = "";
    private static String userAgent = "";
    private static String adID = "";
    private static String randomID = "";
    private static String KEY_ID = null;
    private static String PUBLIC_KEY = null;
    private static final Map<String, String> properties = new HashMap<>();
    private static final Map<String, String> cookies = new HashMap<>();

    public static boolean create(String cookie) {
        if (cookie != null) {
            try {
                if (!cookie.endsWith(";")) {
                    cookie += ";";
                }
                int indexOf;
                String str2;
                if (cookie.contains("sessionid=")) {
                    indexOf = cookie.indexOf("sessionid=") + 10;
                    str2 = "%";
                } else if (cookie.contains("ds_user_id=")) {
                    indexOf = cookie.indexOf("ds_user_id=") + 11;
                    str2 = ";";
                } else {
                    return false;
                }
                String userID = cookie.substring(indexOf, cookie.indexOf(str2, indexOf));
                SQL.getSql().addAccount(userID, cookie);
                Utilities.setUserID(userID);
                IgUser.initial(true);
                return true;
            } catch (Exception e) {
                //
            }
        }
        return false;
    }

    private static final IgHttp.HeaderListener headersListenerLogin = (name, value) -> {
        switch (name) {
            case "ig-set-ig-u-ig-direct-region-hint":
                name = "IG-U-IG-DIRECT-REGION-HINT";
                break;
            case "ig-set-x-mid":
                name = "X-Mid";
                break;
            case "ig-set-authorization":
                name = "Authorization";
                break;
            case "ig-set-ig-u-rur":
                name = "Ig-U-Rur";
                break;
            case "x-ig-set-www-claim":
                name = "X-IG-WWW-Claim";
                break;
            case "ig-set-ig-u-shbts":
                name = "IG-U-SHBTS";
                break;
            case "ig-set-ig-u-shbid":
                name = "IG-U-SHBID";
                break;
            case "cookie":
                value = value.split(";")[0];
                int index = value.indexOf('=');
                try {
                    cookies.put(value.substring(0, index), value.substring(index + 1));
                } catch (Exception e) {
                    //
                }
                return;
            case "ig-set-password-encryption-key-id":
                KEY_ID = value;
                return;
            case "ig-set-password-encryption-pub-key":
                PUBLIC_KEY = value;
                return;
            default:
                return;
        }
        properties.put(name, value);
    };

    private static Map<String, String> loginHeaders() {
        Map<String, String> HEADERS = new HashMap<>();
        HEADERS.put("Ig-Intended-User-Id", "0");
        HEADERS.put("X-Ig-Www-Claim", "0");
        HEADERS.put("User-Agent", getUserAgent());
        HEADERS.put("X-IG-Android-ID", getAndroidID());
        HEADERS.put("X-Pigeon-Session-Id", getRandomID());
        HEADERS.put("X-IG-Device-ID", getDeviceID());
        HEADERS.put("X-IG-Family-Device-ID", getPhoneID());
        HEADERS.putAll(IgApi.DEFAULT_HEADERS);
        HEADERS.putAll(IgApi.randomHeaders());
        setFromProperties(HEADERS);
        if (!cookie().isEmpty()) {
            HEADERS.put("Cookie", cookie());
        }
        return HEADERS;
    }

    private static String encryptPassword(String password) {
        if (TextUtils.isEmpty(PUBLIC_KEY) || TextUtils.isEmpty(KEY_ID)) {
            return "#PWD_INSTAGRAM:0:" + System.currentTimeMillis() + ":" + password;
        }
        try {
            byte[] rand_key = new byte[32], iv = new byte[12];
            SecureRandom sran = new SecureRandom();
            sran.nextBytes(rand_key);
            sran.nextBytes(iv);
            String time = String.valueOf(System.currentTimeMillis());

            // Encrypt random key
            String decoded_pub_key = new String(Base64.decode(PUBLIC_KEY, Base64.NO_WRAP), StandardCharsets.UTF_8).replaceAll("-(.*)-|\n", "");
            Cipher rsa_cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            rsa_cipher.init(Cipher.ENCRYPT_MODE, KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(decoded_pub_key, Base64.NO_WRAP))));
            byte[] rand_key_encrypted = rsa_cipher.doFinal(rand_key);

            // Encrypt password
            Cipher aes_gcm_cipher = Cipher.getInstance("AES/GCM/NoPadding");
            aes_gcm_cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(rand_key, "AES"), new GCMParameterSpec(128, iv));
            aes_gcm_cipher.updateAAD(time.getBytes());
            byte[] password_encrypted = aes_gcm_cipher.doFinal(password.getBytes());

            // Write to final byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(Integer.valueOf(1).byteValue());
            out.write(Integer.valueOf(KEY_ID).byteValue());
            out.write(iv);
            out.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putChar((char) rand_key_encrypted.length).array());
            out.write(rand_key_encrypted);
            out.write(Arrays.copyOfRange(password_encrypted, password_encrypted.length - 16, password_encrypted.length));
            out.write(Arrays.copyOfRange(password_encrypted, 0, password_encrypted.length - 16));
            return String.format("#PWD_INSTAGRAM:%s:%s:%s", "4", time, Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP));
        } catch (Exception e) {
            //
        }
        return "";
    }

    public static void generateToken() {
        cleanLogin();
        userAgent = userAgent();
        androidID = Utilities.androidID();
        sessionID = UUID.randomUUID().toString();
        adID = UUID.randomUUID().toString();
        randomID = UUID.randomUUID().toString();
        phoneID = UUID.randomUUID().toString();
        deviceID = UUID.randomUUID().toString();
//        tray_session_id = nextUUID(sessionID)
//        request_id = nextUUID(adID)
    }

    public static String getAndroidID() {
        if (TextUtils.isEmpty(androidID)) generateToken();
        return androidID;
    }

    public static String getDeviceID() {
        if (TextUtils.isEmpty(deviceID)) generateToken();
        return deviceID;
    }

    public static String getPhoneID() {
        if (TextUtils.isEmpty(phoneID)) generateToken();
        return phoneID;
    }

    public static String getSessionID() {
        if (TextUtils.isEmpty(sessionID)) generateToken();
        return sessionID;
    }

    public static String getUserAgent() {
        if (TextUtils.isEmpty(userAgent)) generateToken();
        return userAgent;
    }

    public static String getAdID() {
        if (TextUtils.isEmpty(adID)) generateToken();
        return adID;
    }

    public static String getRandomID() {
        if (TextUtils.isEmpty(randomID)) generateToken();
        return randomID;
    }

    public static String cookie() {
        return Utilities.cookieEncode(cookies);
    }

    public static void properties(Bundle bundle) {
        for (String key : properties.keySet()) {
            String name = null;
            switch (key.toLowerCase()) {
                case "ig-u-ig-direct-region-hint":
                    name = "drh";
                    break;
                case "x-mid":
                    name = "mid";
                    break;
                case "authorization":
                    name = "authorization";
                    break;
                case "ig-u-rur":
                    name = "rur";
                    break;
                case "x-ig-www-claim":
                    name = "claim";
                    break;
                case "ig-u-shbts":
                    name = "shbts";
                    break;
                case "ig-u-shbid":
                    name = "shbid";
                    break;
            }
            if (null != name) {
                bundle.putString(name, properties.get(key));
            }
        }
    }

    private static void setFromProperties(Map<String, String> request) {
        for (String key : properties.keySet()) {
            String value = properties.get(key);
            if (!TextUtils.isEmpty(value)) {
                request.put(key, value);
            }
        }
    }

    public static void cleanLogin() {
        KEY_ID = null;
        PUBLIC_KEY = null;
        userAgent = null;
        androidID = null;
        sessionID = null;
        phoneID = null;
        deviceID = null;
        adID = null;
        randomID = null;
        properties.clear();
        cookies.clear();
    }

    public static String generateJazoest(String symbols) {
        int amount = 0;
        for (int i = 0; i < symbols.length(); i++) {
            amount += (int) symbols.charAt(i);
        }
        return "2" + amount;
    }

    public static String userAgent() {
        DisplayMetrics displayMetrics = ApplicationLoader.getContext().getResources().getDisplayMetrics();
        String ua = Build.VERSION.SDK_INT + "/" + Build.VERSION.RELEASE + "; " + displayMetrics.densityDpi + "dpi; " + displayMetrics.widthPixels + "x" + displayMetrics.heightPixels + "; " + Build.BRAND + "; " + Build.MODEL + "; " + Build.PRODUCT + "; " + Build.BOARD + "; en_US";
        return "Instagram " + IgApi.versionName + " Android (" + ua + "; " + IgApi.versionID + ")";
    }

}
