package com.partharoypc.adglide.util;

import android.util.Log;

public class ReflectionUtils {
    private static final String TAG = "AdGlide.Reflection";

    public static boolean isClassAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking class availability for: " + className, e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T createInstance(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return (T) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            Log.e(TAG, "Failed to create instance of: " + className, e);
            return null;
        }
    }
}
