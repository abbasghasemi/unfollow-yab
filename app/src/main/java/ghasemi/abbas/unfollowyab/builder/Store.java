
package ghasemi.abbas.unfollowyab.builder;

import android.content.Context;
import android.preference.PreferenceManager;

import ghasemi.abbas.unfollowyab.ApplicationLoader;

public class Store {

    private static Store store;
    private final android.content.SharedPreferences sharedPreferences;

    private Store(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Store data() {
        if (store == null) {
            store = new Store(ApplicationLoader.getContext());
        }
        return store;
    }

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String s) {
        return sharedPreferences.getString(key, s);
    }

    public void putBool(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBool(String key) {
        return getBool(key, false);
    }

    public boolean getBool(String key, boolean value) {
        return sharedPreferences.getBoolean(key, value);
    }

    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int value) {
        return sharedPreferences.getInt(key, value);
    }

    public void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public long getLong(String key) {
        return getLong(key, 0L);
    }

    public long getLong(String key, long value) {
        return sharedPreferences.getLong(key, value);
    }

    public void putFloat(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    public float getFloat(String key) {
        return getFloat(key, 0f);
    }

    public float getFloat(String key, float value) {
        return sharedPreferences.getFloat(key, value);
    }

}
