package com.partharoypc.adglide.util;

import com.partharoypc.adglide.util.AdGlideLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionUtils {
    private static final String TAG = "AdGlide.Reflection";
    private static final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    public static boolean isClassAvailable(String className) {
        if (classCache.containsKey(className)) return true;
        try {
            Class<?> clazz = Class.forName(className);
            classCache.put(className, clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (Exception e) {
            AdGlideLog.e(TAG, "Error checking class availability for: " + className);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T createInstance(String className) {
        try {
            Class<?> clazz = classCache.get(className);
            if (clazz == null) {
                clazz = Class.forName(className);
                classCache.put(className, clazz);
            }
            return (T) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            AdGlideLog.e(TAG, "Failed to create instance of: " + className);
            return null;
        }
    }
}
