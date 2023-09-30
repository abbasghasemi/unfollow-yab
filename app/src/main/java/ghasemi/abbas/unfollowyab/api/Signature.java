package ghasemi.abbas.unfollowyab.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

class Signature {

    private static String getSignatureString(String s) {
        String sEncodedString = null;
        try {
            SecretKeySpec key = new SecretKeySpec("b03e0daaf2ab17cda2a569cace938d639d1288a1197f9ecf97efd0a4ec0874d7".getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            byte[] bytes = mac.doFinal(s.getBytes("ASCII"));
            StringBuilder hash = new StringBuilder();

            for (byte aByte : bytes) {
                String hex = Integer.toHexString(255 & aByte);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }

            sEncodedString = hash.toString();
        } catch (Exception ignored) {
            //
        }

        return sEncodedString;
    }

    static String signature(String json) {
        String hash = "SIGNATURE";
        try {
            return "d=0&signed_body=" + hash + "." + URLEncoder.encode(json, "utf8");
        } catch (UnsupportedEncodingException e) {
            return "d=0&signed_body=" + hash + "." + json;
        }
    }
}
