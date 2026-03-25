package com.partharoypc.adglidedemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.activity.ads.ActivityBanner;
import com.partharoypc.adglidedemo.activity.ads.ActivityInterstitial;
import com.partharoypc.adglidedemo.activity.ads.ActivityNative;
import com.partharoypc.adglidedemo.activity.ads.ActivityRewarded;
import com.partharoypc.adglidedemo.activity.ads.ActivityRewardedInterstitial;
import com.partharoypc.adglidedemo.adapter.DashboardAdapter;
import com.partharoypc.adglidedemo.data.Constant;
import com.partharoypc.adglidedemo.database.SharedPref;
import com.partharoypc.adglidedemo.model.DashboardItem;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.UNITY;
import static com.partharoypc.adglide.util.Constant.WORTISE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SharedPref sharedPref;
    private TextView tvActiveNetwork;
    private View networkChipScroller;
    private ChipGroup chipGroupNetworks;
    private boolean chipPanelOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        applyAppTheme();
        initAppConfig();
        setContentView(R.layout.activity_main);

        setupNetworkSelector();
        setupHeaderButtons();
        setupDashboard();

        // Banner ad at the bottom of the dashboard
        ViewGroup bannerContainer = findViewById(R.id.banner_container);
        AdGlide.showBanner(this, bannerContainer);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Theme
    // ─────────────────────────────────────────────────────────────────────────

    private void applyAppTheme() {
        if (sharedPref.getIsDarkTheme()) {
            setTheme(R.style.AppDarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
    }

    private void toggleTheme() {
        sharedPref.setIsDarkTheme(!sharedPref.getIsDarkTheme());
        recreate();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Header icon buttons
    // ─────────────────────────────────────────────────────────────────────────

    private void setupHeaderButtons() {
        // Theme toggle
        com.google.android.material.button.MaterialButton btnThemeToggle = findViewById(R.id.btn_theme_toggle);
        if (sharedPref.getIsDarkTheme()) {
            btnThemeToggle.setIconResource(R.drawable.ic_light_mode); // Show Sun icon to switch to light
        } else {
            btnThemeToggle.setIconResource(R.drawable.ic_dark_mode);  // Show Moon icon to switch to dark
        }
        btnThemeToggle.setOnClickListener(v -> toggleTheme());

        // Settings shortcut
        findViewById(R.id.btn_settings).setOnClickListener(v ->
                startActivity(new Intent(this, ActivitySettings.class)));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Network selector
    // ─────────────────────────────────────────────────────────────────────────

    private void setupNetworkSelector() {
        tvActiveNetwork = findViewById(R.id.tv_active_network);
        networkChipScroller = findViewById(R.id.network_chip_scroller);
        chipGroupNetworks = findViewById(R.id.chip_group_networks);
        TextView btnChangeNetwork = findViewById(R.id.btn_change_network);

        // Show the current network name
        updateActiveNetworkLabel();

        // Toggle chip panel visibility
        btnChangeNetwork.setOnClickListener(v -> {
            chipPanelOpen = !chipPanelOpen;
            networkChipScroller.setVisibility(chipPanelOpen ? View.VISIBLE : View.GONE);
            btnChangeNetwork.setText(chipPanelOpen ? "Close ✕" : "Change ›");
            if (chipPanelOpen) {
                preselectCurrentNetworkChip();
            }
        });

        // Chip selection → update network instantly
        chipGroupNetworks.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int chipId = checkedIds.get(0);
            String newNetwork = networkIdForChip(chipId);
            if (newNetwork != null) {
                Constant.AD_NETWORK = newNetwork;
                sharedPref.setAdNetwork(newNetwork);
                updateActiveNetworkLabel();
                reinitAds();
            }
        });

        // SDK Listener for Performance Monitoring
        AdGlide.setListener(new AdGlide.AdGlideListener() {
            @Override
            public void onAdStatusChanged(String format, String network, String status) {
                showToast(format + " " + status + " on " + network);
            }

            @Override
            public void onPerformanceMetrics(String format, long loadTimeMs) {
                showToast("⚡ " + format + " load time: " + loadTimeMs + "ms");
            }
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> {
            com.google.android.material.snackbar.Snackbar.make(findViewById(android.R.id.content), message, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
        });
    }

    private void preselectCurrentNetworkChip() {
        int chipId = chipIdForNetwork(Constant.AD_NETWORK);
        if (chipId != View.NO_ID) {
            Chip chip = chipGroupNetworks.findViewById(chipId);
            if (chip != null) chip.setChecked(true);
        }
    }

    private void updateActiveNetworkLabel() {
        tvActiveNetwork.setText(labelForNetwork(Constant.AD_NETWORK));
    }

    private String networkIdForChip(int chipId) {
        if (chipId == R.id.chip_admob)       return ADMOB;
        if (chipId == R.id.chip_meta)        return META;
        if (chipId == R.id.chip_applovin)    return APPLOVIN_MAX;
        if (chipId == R.id.chip_ironsource)  return IRONSOURCE;
        if (chipId == R.id.chip_unity)       return UNITY;
        if (chipId == R.id.chip_wortise)     return WORTISE;
        if (chipId == R.id.chip_startapp)    return STARTAPP;
        return null;
    }

    private int chipIdForNetwork(String network) {
        if (ADMOB.equals(network))       return R.id.chip_admob;
        if (META.equals(network))        return R.id.chip_meta;
        if (APPLOVIN_MAX.equals(network)) return R.id.chip_applovin;
        if (IRONSOURCE.equals(network))  return R.id.chip_ironsource;
        if (UNITY.equals(network))       return R.id.chip_unity;
        if (WORTISE.equals(network))     return R.id.chip_wortise;
        if (STARTAPP.equals(network))    return R.id.chip_startapp;
        return View.NO_ID;
    }

    private String labelForNetwork(String network) {
        if (network == null) return "None";
        switch (network) {
            case "admob":        return "AdMob";
            case "meta":         return "Meta";
            case "applovin":
            case "applovin_max": return "AppLovin MAX";
            case "ironsource":   return "IronSource";
            case "unity":        return "Unity Ads";
            case "wortise":      return "Wortise";
            case "startapp":     return "StartApp";
            default:             return network;
        }
    }

    /** Rebuild & apply the AdGlide config after a network change */
    private void reinitAds() {
        com.partharoypc.adglide.AdGlideConfig config =
                new com.partharoypc.adglide.AdGlideConfig.Builder()
                        .enableAds(Constant.AD_STATUS)
                        .primaryNetwork(Constant.AD_NETWORK)
                        .backupNetworks(Constant.BACKUP_AD_NETWORK)
                        .bannerEnabled(Constant.BANNER_STATUS)
                        .interstitialEnabled(Constant.INTERSTITIAL_STATUS)
                        .nativeEnabled(Constant.NATIVE_STATUS)
                        .rewardedEnabled(Constant.REWARDED_STATUS)
                        .rewardedInterstitialEnabled(Constant.REWARDED_INTERSTITIAL_STATUS)
                        .appOpenEnabled(Constant.OPEN_ADS_ON_START)
                        .autoLoadInterstitial(true)
                        .autoLoadRewarded(true)
                        .interstitialInterval(Constant.INTERSTITIAL_AD_INTERVAL)
                        .rewardedInterval(Constant.REWARDED_AD_INTERVAL)
                        .appOpenCooldown(Constant.APP_OPEN_COOLDOWN_MINUTES)
                        .adResponseTimeout(Constant.AD_RESPONSE_TIMEOUT_MS)
                        .testMode(Constant.TEST_MODE)
                        .enableDebugHUD(Constant.ENABLE_DEBUG_HUD)
                        // SDK keys
                        .startAppId(Constant.STARTAPP_APP_ID)
                        .unityGameId(Constant.UNITY_GAME_ID)
                        .appLovinSdkKey(getResources().getString(R.string.app_lovin_sdk_key))
                        .ironSourceAppKey(Constant.IRONSOURCE_APP_KEY)
                        .wortiseAppId(Constant.WORTISE_APP_ID)
                        // Banner IDs
                        .adMobBannerId(Constant.ADMOB_BANNER_ID)
                        .metaBannerId(Constant.META_BANNER_ID)
                        .unityBannerId(Constant.UNITY_BANNER_ID)
                        .appLovinBannerId(Constant.APPLOVIN_BANNER_ID)
                        .ironSourceBannerId(Constant.IRONSOURCE_BANNER_ID)
                        .wortiseBannerId(Constant.WORTISE_BANNER_ID)
                        // Interstitial IDs
                        .adMobInterstitialId(Constant.ADMOB_INTERSTITIAL_ID)
                        .metaInterstitialId(Constant.META_INTERSTITIAL_ID)
                        .unityInterstitialId(Constant.UNITY_INTERSTITIAL_ID)
                        .appLovinInterstitialId(Constant.APPLOVIN_INTERSTITIAL_ID)
                        .ironSourceInterstitialId(Constant.IRONSOURCE_INTERSTITIAL_ID)
                        .wortiseInterstitialId(Constant.WORTISE_INTERSTITIAL_ID)
                        // Rewarded IDs
                        .adMobRewardedId(Constant.ADMOB_REWARDED_ID)
                        .metaRewardedId(Constant.META_REWARDED_ID)
                        .unityRewardedId(Constant.UNITY_REWARDED_ID)
                        .appLovinRewardedId(Constant.APPLOVIN_MAX_REWARDED_ID)
                        .appLovinRewardedIntId(Constant.APPLOVIN_REWARDED_INT_ID)
                        .ironSourceRewardedId(Constant.IRONSOURCE_REWARDED_ID)
                        .wortiseRewardedId(Constant.WORTISE_REWARDED_ID)
                        // Rewarded Interstitial
                        .adMobRewardedIntId(Constant.ADMOB_REWARDED_INTERSTITIAL_ID)
                        .appLovinRewardedIntId(Constant.APPLOVIN_REWARDED_INT_ID)
                        .wortiseRewardedIntId(Constant.WORTISE_REWARDED_INTERSTITIAL_ID)
                        // App Open
                        .adMobAppOpenId(Constant.ADMOB_APP_OPEN_AD_ID)
                        .metaAppOpenId(Constant.META_APP_OPEN_ID)
                        .appLovinAppOpenId(Constant.APPLOVIN_APP_OPEN_AP_ID)
                        .wortiseAppOpenId(Constant.WORTISE_APP_OPEN_AD_ID)
                        // Native
                        .adMobNativeId(Constant.ADMOB_NATIVE_ID)
                        .metaNativeId(Constant.META_NATIVE_ID)
                        .appLovinNativeId(Constant.APPLOVIN_NATIVE_MANUAL_ID)
                        .ironSourceNativeId(Constant.IRONSOURCE_NATIVE_ID)
                        .wortiseNativeId(Constant.WORTISE_NATIVE_ID)
                        .houseAdEnabled(Constant.HOUSE_AD_ENABLE)
                        .houseAdBannerImage(Constant.HOUSE_AD_BANNER_IMAGE)
                        .houseAdBannerClickUrl(Constant.HOUSE_AD_BANNER_URL)
                        .houseAdInterstitialImage(Constant.HOUSE_AD_INTERSTITIAL_IMAGE)
                        .houseAdInterstitialClickUrl(Constant.HOUSE_AD_INTERSTITIAL_URL)
                        .houseAdNativeTitle(Constant.HOUSE_AD_NATIVE_TITLE)
                        .houseAdNativeDescription(Constant.HOUSE_AD_NATIVE_DESC)
                        .houseAdNativeCTA(Constant.HOUSE_AD_NATIVE_CTA)
                        .houseAdNativeImage(Constant.HOUSE_AD_NATIVE_IMAGE)
                        .houseAdNativeIcon(Constant.HOUSE_AD_NATIVE_ICON)
                        .enableGDPR(true)
                        .excludeOpenAdFrom(ActivitySplash.class, ActivitySettings.class)
                        .debug(com.partharoypc.adglidedemo.BuildConfig.DEBUG)
                        .build();

        AdGlide.initialize(getApplication(), config);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Dashboard
    // ─────────────────────────────────────────────────────────────────────────

    private void initAppConfig() {
        Constant.AD_NETWORK = sharedPref.getAdNetwork();
        Constant.BACKUP_AD_NETWORK = sharedPref.getBackupAdNetwork();
        Constant.OPEN_ADS_ON_START = sharedPref.getIsAppOpenAdEnabled();
        Constant.BANNER_STATUS = sharedPref.getIsBannerEnabled();
        Constant.INTERSTITIAL_STATUS = sharedPref.getIsInterstitialEnabled();
        Constant.NATIVE_STATUS = sharedPref.getIsNativeEnabled();
        Constant.REWARDED_STATUS = sharedPref.getIsRewardedEnabled();
        Constant.REWARDED_INTERSTITIAL_STATUS = sharedPref.getIsRewardedInterstitialEnabled();
        Constant.INTERSTITIAL_AD_INTERVAL = sharedPref.getInterstitialInterval();
        Constant.REWARDED_AD_INTERVAL = sharedPref.getRewardedInterval();
        Constant.TEST_MODE = sharedPref.getTestMode();
        Constant.ENABLE_DEBUG_HUD = sharedPref.getEnableDebugHud();
        Constant.AD_RESPONSE_TIMEOUT_MS = sharedPref.getAdResponseTimeoutMs();
        Constant.APP_OPEN_COOLDOWN_MINUTES = sharedPref.getAppOpenCooldownMinutes();
    }

    private void setupDashboard() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<DashboardItem> items = new ArrayList<>();
        items.add(new DashboardItem("Banner Ads",
                "Standard & collapsible banner strips", R.mipmap.ic_launcher, ActivityBanner.class));
        items.add(new DashboardItem("Interstitial Ads",
                "Full-screen ads between content", R.mipmap.ic_launcher, ActivityInterstitial.class));
        items.add(new DashboardItem("Rewarded Ads",
                "Reward users for watching full videos", R.mipmap.ic_launcher, ActivityRewarded.class));
        items.add(new DashboardItem("Native Ads",
                "Ads that blend seamlessly into content", R.mipmap.ic_launcher, ActivityNative.class));
        items.add(new DashboardItem("Rewarded Interstitial",
                "Full-screen hybrid: watch to earn reward", R.mipmap.ic_launcher, ActivityRewardedInterstitial.class));
        items.add(new DashboardItem("SDK Debugger (HUD)",
                "Live fill analysis & waterfall status", R.mipmap.ic_launcher, null));

        DashboardAdapter adapter = new DashboardAdapter(this, items, item -> {
            if (item.getActivityClass() != null) {
                startActivity(new Intent(this, item.getActivityClass()));
            } else if (item.getTitle().contains("Debugger")) {
                AdGlide.showDebugHUD(this);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
