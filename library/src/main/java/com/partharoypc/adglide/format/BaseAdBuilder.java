package com.partharoypc.adglide.format;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.partharoypc.adglide.util.AdFormat;
import com.partharoypc.adglide.util.AdGlideCallback;
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglide.util.AdLoader;

import java.lang.ref.WeakReference;

/**
 * A centralized Base Builder for AdGlide formats (Interstitial, Rewarded, AppOpen, etc.).
 * Drastically reduces boilerplate by managing the AdLoader, Activity contexts, and public API chains
 * without breaking user-facing syntax.
 */
public abstract class BaseAdBuilder<T extends BaseAdBuilder<T>> {
    protected static final String TAG = "AdGlide";
    protected final AdLoader adLoader;
    protected final WeakReference<Activity> activityRef;
    protected boolean showOnLoad = false;
    protected AdGlideCallback callback;
    protected String currentNetwork;

    public BaseAdBuilder(@NonNull Context context, AdFormat format) {
        if (context instanceof Activity) {
            this.activityRef = new WeakReference<>((Activity) context);
        } else {
            this.activityRef = null;
        }
        this.adLoader = new AdLoader(context, format);
    }

    public Activity getActivity() {
        return activityRef != null ? activityRef.get() : null;
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    @NonNull
    public T build() {
        return self();
    }

    @NonNull
    public T build(AdGlideCallback callback) {
        return self();
    }

    @NonNull
    public T load() {
        doLoad(null);
        return self();
    }

    @NonNull
    public T load(AdGlideCallback callback) {
        doLoad(callback);
        return self();
    }

    @NonNull
    public T loadAndShow(Activity displayActivity, AdGlideCallback callback) {
        this.showOnLoad = true;
        this.callback = callback;
        doLoad(callback);
        return self();
    }

    public void show() {
        Activity activity = getActivity();
        if (activity != null) {
            show(activity, callback);
        } else {
            AdGlideLog.e(TAG, "Cannot show Ad: Activity reference is null.");
        }
    }

    public void show(@NonNull Activity displayActivity) {
        show(displayActivity, callback);
    }

    public void show(AdGlideCallback callback) {
        Activity activity = getActivity();
        if (activity != null) {
            show(activity, callback);
        } else {
            AdGlideLog.e(TAG, "Cannot show Ad: Activity reference is null.");
            if (callback != null) callback.onAdDismissed();
        }
    }

    public void show(@NonNull final Activity displayActivity, final AdGlideCallback callback) {
        // Lifecycle Guard: Prevent crashes if the activity is already finishing or destroyed
        if (displayActivity.isFinishing() || (android.os.Build.VERSION.SDK_INT >= 17 && displayActivity.isDestroyed())) {
            AdGlideLog.e(TAG, "Cannot show Ad: Activity is finishing or already destroyed.");
            if (callback != null) callback.onAdDismissed();
            return;
        }

        // Thread Guard: Force showing on UI thread as required by most Ad SDKs
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> doShow(displayActivity, callback));
    }

    // Handlers specific to the actual format provider mechanisms
    protected abstract void doLoad(AdGlideCallback callback);
    protected abstract void doShow(Activity activity, AdGlideCallback callback);
}
