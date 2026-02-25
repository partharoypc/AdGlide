package com.partharoypc.adglide;

import android.content.Context;

public class AdGlide {

    /**
     * Starts the SDK initialization process.
     * 
     * @param context The Application or Activity context.
     * @return An Initialize builder instance for fluent configuration.
     */

    public static com.partharoypc.adglide.format.AdNetwork.Initialize init(Context context) {
        return new com.partharoypc.adglide.format.AdNetwork.Initialize(context);
    }

}
