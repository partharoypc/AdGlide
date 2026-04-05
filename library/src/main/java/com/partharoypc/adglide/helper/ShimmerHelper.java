package com.partharoypc.adglide.helper;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

/**
 * A helper to provide a "Premium" shimmer/pulse effect for ad placeholders.
 */
public class ShimmerHelper {

    /**
     * Starts a pulsing alpha animation on the provided view to simulate a loading shimmer.
     */
    public static void startPulsing(View view) {
        if (view == null) return;
        
        AlphaAnimation pulse = new AlphaAnimation(1.0f, 0.4f);
        pulse.setDuration(800);
        pulse.setInterpolator(new LinearInterpolator());
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setRepeatMode(Animation.REVERSE);
        
        view.startAnimation(pulse);
    }

    /**
     * Recursively starts pulsing on all child views that look like skeleton elements (gray backgrounds).
     */
    public static void startShimmer(View view) {
        if (view == null) return;
        
        startPulsing(view);
        
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                startShimmer(group.getChildAt(i));
            }
        }
    }

    /**
     * Stops all animations on the view and its children recursively.
     */
    public static void stopShimmer(View view) {
        if (view == null) return;
        
        view.clearAnimation();
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                stopShimmer(group.getChildAt(i));
            }
        }
    }
}
