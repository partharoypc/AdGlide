package com.partharoypc.adglidedemo.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe utility to track real-time ad performance metrics for the demo dashboard.
 */
public class PerformanceMonitor {

    private static final AtomicInteger totalRequests = new AtomicInteger(0);
    private static final AtomicInteger totalMatches = new AtomicInteger(0);
    private static final AtomicInteger totalImpressions = new AtomicInteger(0);
    private static final AtomicInteger lateMatchesSaved = new AtomicInteger(0);
    private static final AtomicInteger healerSkips = new AtomicInteger(0);

    public static void recordRequest() {
        totalRequests.incrementAndGet();
    }

    public static void recordMatch() {
        totalMatches.incrementAndGet();
    }

    public static void recordImpression() {
        totalImpressions.incrementAndGet();
    }

    public static void recordLateMatchSaved() {
        lateMatchesSaved.incrementAndGet();
    }

    public static void recordHealerSkip() {
        healerSkips.incrementAndGet();
    }

    public static int getTotalRequests() {
        return totalRequests.get();
    }

    public static int getTotalImpressions() {
        return totalImpressions.get();
    }

    public static int getLateMatchesSaved() {
        return lateMatchesSaved.get();
    }

    public static int getHealerSkips() {
        return healerSkips.get();
    }

    public static double getShowRate() {
        int requests = totalRequests.get();
        if (requests == 0) return 0.0;
        return (double) totalImpressions.get() / requests * 100.0;
    }

    public static double getMatchRate() {
        int requests = totalRequests.get();
        if (requests == 0) return 100.0; // Assume 100% until proven otherwise due to House Ad fallback
        return (double) totalMatches.get() / requests * 100.0;
    }

    public static void reset() {
        totalRequests.set(0);
        totalMatches.set(0);
        totalImpressions.set(0);
        lateMatchesSaved.set(0);
        healerSkips.set(0);
    }
}
