package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.provider.AppOpenProvider;
import com.partharoypc.adglide.provider.AppOpenProviderFactory;
import com.partharoypc.adglide.util.OnShowAdCompleteListener;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("StaticFieldLeak")
public class AppOpenAd {
    private static final String TAG = "AdGlide";

    public static boolean isAppOpenAdLoaded = false;
    private boolean adStatus = true;
    private String adNetwork = "";
    private String backupAdNetwork = "";
    private WaterfallManager waterfallManager;
    private String adMobAppOpenId = "";
    private String metaAppOpenId = "";
    private String appLovinAppOpenId = "";
    private String wortiseAppOpenId = "";
    private Activity currentActivity;
    private int placementStatus = 1;

    // Provider management
    private static final Map<String, AppOpenProvider> providers = new HashMap<>();

    public AppOpenAd() {
    }

    private AppOpenAd(Builder builder) {
        this.adStatus = builder.adStatus;
        this.adNetwork = builder.adNetwork;
        this.backupAdNetwork = builder.backupAdNetwork;
        this.waterfallManager = builder.waterfallManager;
        this.adMobAppOpenId = builder.adMobAppOpenId;
        this.metaAppOpenId = builder.metaAppOpenId;
        this.appLovinAppOpenId = builder.appLovinAppOpenId;
        this.wortiseAppOpenId = builder.wortiseAppOpenId;
    }

    private static synchronized AppOpenProvider getProvider(String network) {
        AppOpenProvider provider = providers.get(network);
        if (provider == null) {
            provider = AppOpenProviderFactory.getProvider(network);
            if (provider != null) {
                providers.put(network, provider);
            }
        }
        return provider;
    }

    @NonNull
    public AppOpenAd status(boolean adStatus) {
        this.adStatus = adStatus;
        return this;
    }

    @NonNull
    public AppOpenAd placement(int placementStatus) {
        this.placementStatus = placementStatus;
        return this;
    }

    @NonNull
    public AppOpenAd network(@NonNull String adNetwork) {
        this.adNetwork = AdGlideNetwork.fromString(adNetwork).getValue();
        return this;
    }

    @NonNull
    public AppOpenAd network(AdGlideNetwork network) {
        this.adNetwork = network.getValue();
        return this;
    }

    @NonNull
    public AppOpenAd backup(@Nullable String backupAdNetwork) {
        this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetwork).getValue();
        this.waterfallManager = new WaterfallManager(backupAdNetwork);
        return this;
    }

    @NonNull
    public AppOpenAd backups(@Nullable String... backupAdNetworks) {
        this.waterfallManager = new WaterfallManager(backupAdNetworks);
        if (backupAdNetworks != null && backupAdNetworks.length > 0) {
            this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetworks[0]).getValue();
        }
        return this;
    }

    @NonNull
    public AppOpenAd adMobId(@NonNull String adMobAppOpenId) {
        this.adMobAppOpenId = adMobAppOpenId;
        return this;
    }

    @NonNull
    public AppOpenAd metaId(@NonNull String metaAppOpenId) {
        this.metaAppOpenId = metaAppOpenId;
        return this;
    }

    @NonNull
    public AppOpenAd appLovinId(@NonNull String appLovinAppOpenId) {
        this.appLovinAppOpenId = appLovinAppOpenId;
        return this;
    }

    @NonNull
    public AppOpenAd wortiseId(@NonNull String wortiseAppOpenId) {
        this.wortiseAppOpenId = wortiseAppOpenId;
        return this;
    }

    @NonNull
    public AppOpenAd setLifecycleObserver() {
        onStartLifecycleObserver();
        return this;
    }

    @NonNull
    public AppOpenAd setActivityLifecycleCallbacks(@NonNull Activity activity) {
        onStartActivityLifecycleCallbacks(activity);
        return this;
    }

    public void onStartLifecycleObserver() {
        try {
            if (placementStatus != 0 && adStatus && currentActivity != null) {
                if (currentActivity.getIntent().hasExtra("unique_id")) {
                    return;
                }
                showAdIfAvailable(currentActivity, null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onStartLifecycleObserver: " + e.getMessage());
        }
    }

    public void onStartActivityLifecycleCallbacks(Activity activity) {
        try {
            if (placementStatus != 0 && adStatus) {
                AppOpenProvider provider = getProvider(adNetwork);
                boolean isShowing = provider != null && provider.isShowingAd();
                if (!isShowing) {
                    currentActivity = activity;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onStartActivityLifecycleCallbacks: " + e.getMessage());
        }
    }

    public AppOpenAd showAppOpenAdIfAvailable(Activity activity,
            OnShowAdCompleteListener onShowAdCompleteListener) {
        showAdIfAvailable(activity, onShowAdCompleteListener);
        return this;
    }

    public void showAdIfAvailable(@NonNull Activity activity,
            @Nullable OnShowAdCompleteListener onShowAdCompleteListener) {
        try {
            if (placementStatus != 0 && adStatus) {
                AppOpenProvider provider = getProvider(adNetwork);
                String adUnitId = getAdUnitIdForNetwork(adNetwork);

                if (provider != null && !adUnitId.equals("0")) {
                    provider.showAppOpenAd(activity, new AppOpenProvider.AppOpenListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                            if (onShowAdCompleteListener != null)
                                onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdDismissed() {
                            if (onShowAdCompleteListener != null)
                                onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdShowFailed(String error) {
                            if (onShowAdCompleteListener != null)
                                onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdShowed() {
                        }
                    });
                } else if (onShowAdCompleteListener != null) {
                    onShowAdCompleteListener.onShowAdComplete();
                }
            } else if (onShowAdCompleteListener != null) {
                onShowAdCompleteListener.onShowAdComplete();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in showAdIfAvailable: " + e.getMessage());
            if (onShowAdCompleteListener != null) {
                onShowAdCompleteListener.onShowAdComplete();
            }
        }
    }

    private String getAdUnitIdForNetwork(String network) {
        return switch (network) {
            case ADMOB, META_BIDDING_ADMOB -> adMobAppOpenId;
            case META -> metaAppOpenId;
            case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> appLovinAppOpenId;
            case WORTISE -> wortiseAppOpenId;
            default -> "0";
        };
    }

    // ── Builder ──────────────────────────────────────────────────────────

    public static class Builder {
        private static final String TAG = "AdGlide";
        private final Activity activity;
        private boolean adStatus = true;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private String adMobAppOpenId = "";
        private String metaAppOpenId = "";
        private String appLovinAppOpenId = "";
        private String wortiseAppOpenId = "";

        public Builder(Activity activity) {
            this.activity = activity;
        }

        @NonNull
        public Builder status(boolean adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        @NonNull
        public Builder network(@NonNull String adNetwork) {
            this.adNetwork = AdGlideNetwork.fromString(adNetwork).getValue();
            return this;
        }

        @NonNull
        public Builder network(AdGlideNetwork network) {
            this.adNetwork = network.getValue();
            return this;
        }

        @NonNull
        public Builder backup(@Nullable String backupAdNetwork) {
            this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetwork).getValue();
            this.waterfallManager = new WaterfallManager(backupAdNetwork);
            return this;
        }

        @NonNull
        public Builder backup(AdGlideNetwork backupAdNetwork) {
            return backup(backupAdNetwork.getValue());
        }

        @NonNull
        public Builder backups(String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks.length > 0) {
                this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetworks[0]).getValue();
            }
            return this;
        }

        @NonNull
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return backups(AdGlideNetwork.toStringArray(backupAdNetworks));
        }

        @NonNull
        public Builder adMobId(@NonNull String adMobAppOpenId) {
            this.adMobAppOpenId = adMobAppOpenId;
            return this;
        }

        @NonNull
        public Builder metaId(@NonNull String metaAppOpenId) {
            this.metaAppOpenId = metaAppOpenId;
            return this;
        }

        @NonNull
        public Builder appLovinId(@NonNull String appLovinAppOpenId) {
            this.appLovinAppOpenId = appLovinAppOpenId;
            return this;
        }

        @NonNull
        public Builder wortiseId(@NonNull String wortiseAppOpenId) {
            this.wortiseAppOpenId = wortiseAppOpenId;
            return this;
        }

        @NonNull
        public Builder load() {
            loadAppOpenAd(null);
            return this;
        }

        @NonNull
        public Builder load(OnShowAdCompleteListener onShowAdCompleteListener) {
            loadAppOpenAd(onShowAdCompleteListener);
            return this;
        }

        public void loadAppOpenAd(OnShowAdCompleteListener onShowAdCompleteListener) {
            try {
                if (!adStatus) {
                    if (onShowAdCompleteListener != null)
                        onShowAdCompleteListener.onShowAdComplete();
                    return;
                }

                if (!Tools.isNetworkAvailable(activity)) {
                    Log.e(TAG, "No internet. Skipping App Open load.");
                    if (onShowAdCompleteListener != null)
                        onShowAdCompleteListener.onShowAdComplete();
                    return;
                }

                if (waterfallManager != null)
                    waterfallManager.reset();

                loadAdFromNetwork(adNetwork, onShowAdCompleteListener);
            } catch (Exception e) {
                Log.e(TAG, "Error in loadAppOpenAd: " + e.getMessage());
                if (onShowAdCompleteListener != null)
                    onShowAdCompleteListener.onShowAdComplete();
            }
        }

        private void loadAdFromNetwork(String network,
                OnShowAdCompleteListener onShowAdCompleteListener) {
            try {
                String adUnitId = getAdUnitIdForNetwork(network);
                if (adUnitId == null || adUnitId.trim().isEmpty()
                        || (adUnitId.equals("0") && !network.equals("startapp"))) {
                    Log.d(TAG, "Ad unit ID for " + network + " is invalid. Trying backup.");
                    loadBackupAppOpenAd(onShowAdCompleteListener);
                    return;
                }

                AppOpenProvider provider = getProvider(network);
                if (provider != null) {
                    provider.loadAppOpenAd(activity, adUnitId, new AppOpenProvider.AppOpenListener() {
                        @Override
                        public void onAdLoaded() {
                            isAppOpenAdLoaded = true;
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                            loadBackupAppOpenAd(onShowAdCompleteListener);
                        }

                        @Override
                        public void onAdDismissed() {
                            if (onShowAdCompleteListener != null)
                                onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdShowFailed(String error) {
                            if (onShowAdCompleteListener != null)
                                onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdShowed() {
                        }
                    });
                } else {
                    loadBackupAppOpenAd(onShowAdCompleteListener);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed loading AppOpen from " + network + ": " + e.getMessage());
                loadBackupAppOpenAd(onShowAdCompleteListener);
            }
        }

        private void loadBackupAppOpenAd(OnShowAdCompleteListener onShowAdCompleteListener) {
            if (waterfallManager != null && waterfallManager.hasNext()) {
                String nextNetwork = waterfallManager.getNext();
                Log.d(TAG, "Loading backup AppOpen from: " + nextNetwork);
                loadAdFromNetwork(nextNetwork, onShowAdCompleteListener);
            } else {
                Log.d(TAG, "All AppOpen backups exhausted.");
                if (onShowAdCompleteListener != null)
                    onShowAdCompleteListener.onShowAdComplete();
            }
        }

        private String getAdUnitIdForNetwork(String network) {
            switch (network) {
                case ADMOB:
                case META_BIDDING_ADMOB:
                    return adMobAppOpenId;
                case META:
                    return metaAppOpenId;
                case APPLOVIN:
                case APPLOVIN_MAX:
                case META_BIDDING_APPLOVIN_MAX:
                    return appLovinAppOpenId;
                case WORTISE:
                    return wortiseAppOpenId;
                default:
                    return "0";
            }
        }

        public void showAppOpenAd() {
            showAppOpenAd(null);
        }

        public void showAppOpenAd(OnShowAdCompleteListener onShowAdCompleteListener) {
            try {
                AppOpenProvider provider = getProvider(adNetwork);
                if (provider != null && provider.isAdAvailable()) {
                    provider.showAppOpenAd(activity, new AppOpenProvider.AppOpenListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                        }

                        @Override
                        public void onAdDismissed() {
                            if (onShowAdCompleteListener != null)
                                onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdShowFailed(String error) {
                            if (onShowAdCompleteListener != null)
                                onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdShowed() {
                        }
                    });
                } else if (onShowAdCompleteListener != null) {
                    onShowAdCompleteListener.onShowAdComplete();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showAppOpenAd: " + e.getMessage());
                if (onShowAdCompleteListener != null)
                    onShowAdCompleteListener.onShowAdComplete();
            }
        }

        public void destroyOpenAd() {
            AppOpenAd.isAppOpenAdLoaded = false;
        }

        public boolean isAdAvailable() {
            AppOpenProvider provider = getProvider(adNetwork);
            return provider != null && provider.isAdAvailable();
        }

        public AppOpenAd build() {
            return new AppOpenAd(this);
        }
    }
}
