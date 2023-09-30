package ghasemi.abbas.unfollowyab.api;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import ghasemi.abbas.unfollowyab.builder.Store;

class Connection {
    private static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
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

    public static String androidID() {
        String android = Store.data().getString("android");
        if (android.isEmpty()) {
            android = md5(getRandomString()).substring(0, 16);
            Store.data().putString("android", android);
        }
        return android;
    }
}
