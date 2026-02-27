package com.partharoypc.adglide.util;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * Tracks SDK events, errors, and performance for the Debug HUD.
 */
public class PerformanceLogger {
    private static final String TAG = "AdGlide.Stats";
    private static final int MAX_LOGS = 50;
    private static final List<LogEntry> logs = new ArrayList<>();

    public static class LogEntry {
        public long timestamp;
        public String category; // INTERSTITIAL, REWARDED, BANNER, NATIVE, APP_OPEN, CORE
        public String message;
        public String level; // INFO, WARN, ERROR

        public LogEntry(String category, String message, String level) {
            this.timestamp = System.currentTimeMillis();
            this.category = category;
            this.message = message;
            this.level = level;
        }
    }

    public static synchronized void log(String category, String message) {
        log(category, message, "INFO");
    }

    public static synchronized void warn(String category, String message) {
        log(category, message, "WARN");
    }

    public static synchronized void error(String category, String message) {
        log(category, message, "ERROR");
    }

    private static synchronized void log(String category, String message, String level) {
        Log.d(TAG, "[" + category + "] " + message);
        if (logs.size() >= MAX_LOGS) {
            logs.remove(0);
        }
        logs.add(new LogEntry(category, message, level));
    }

    public static synchronized List<LogEntry> getLogs() {
        return new ArrayList<>(logs);
    }
}
