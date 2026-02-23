package com.partharoypc.adglide;

import android.app.Activity;
import com.partharoypc.adglide.format.AdNetwork;

public class AdGlide {

    /**
     * Starts the SDK initialization process.
     * 
     * @param activity The Activity context.
     * @return An Initialize builder instance for fluent configuration.
     */
    public static AdNetwork.Initialize init(Activity activity) {
        return new AdNetwork.Initialize(activity);
    }
}
