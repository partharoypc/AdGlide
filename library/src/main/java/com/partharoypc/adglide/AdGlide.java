package com.partharoypc.adglide;

import android.content.Context;
import com.partharoypc.adglide.format.AdNetwork;

public class AdGlide {

    /**
     * Starts the SDK initialization process.
     * 
     * @param context The Application or Activity context.
     * @return An Initialize builder instance for fluent configuration.
     */
    public static AdNetwork.Initialize init(Context context) {
        return new AdNetwork.Initialize(context);
    }
}
