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
import com.partharoypc.adglidedemo.activity.ads.ActivityAppOpen;
import com.partharoypc.adglidedemo.activity.ads.ActivityBanner;
import com.partharoypc.adglidedemo.activity.ads.ActivityInterstitial;
import com.partharoypc.adglidedemo.activity.ads.ActivityNative;
import com.partharoypc.adglidedemo.activity.ads.ActivityRewarded;
import com.partharoypc.adglidedemo.activity.ads.ActivityRewardedInterstitial;
import com.partharoypc.adglidedemo.adapter.DashboardAdapter;
import com.partharoypc.adglidedemo.data.Constant;
import com.partharoypc.adglidedemo.database.SharedPref;
import com.partharoypc.adglidedemo.application.MyApplication;
import com.partharoypc.adglidedemo.model.DashboardItem;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.UNITY;
import static com.partharoypc.adglide.util.Constant.WORTISE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.HOUSE_AD;

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
                sharedPref.setAdNetwork(newNetwork);
                updateActiveNetworkLabel();
                reinitAds();
            }
        });

    }

    private void showToast(String message) {
        runOnUiThread(() -> {
            com.google.android.material.snackbar.Snackbar.make(findViewById(android.R.id.content), message, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
        });
    }

    private void preselectCurrentNetworkChip() {
        int chipId = chipIdForNetwork(sharedPref.getAdNetwork());
        if (chipId != View.NO_ID) {
            Chip chip = chipGroupNetworks.findViewById(chipId);
            if (chip != null) chip.setChecked(true);
        }
    }

    private void updateActiveNetworkLabel() {
        tvActiveNetwork.setText(labelForNetwork(sharedPref.getAdNetwork()));
    }

    private String networkIdForChip(int chipId) {
        if (chipId == R.id.chip_admob)       return ADMOB;
        if (chipId == R.id.chip_meta)        return META;
        if (chipId == R.id.chip_applovin)    return APPLOVIN_MAX;
        if (chipId == R.id.chip_ironsource)  return IRONSOURCE;
        if (chipId == R.id.chip_unity)       return UNITY;
        if (chipId == R.id.chip_wortise)     return WORTISE;
        if (chipId == R.id.chip_startapp)    return STARTAPP;
        if (chipId == R.id.chip_house_ad)    return HOUSE_AD;
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
        if (HOUSE_AD.equals(network))    return R.id.chip_house_ad;
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
            case "house_ad":     return "House Ad";
            default:             return network;
        }
    }

    /** Rebuild & apply the AdGlide config after a network change */
    private void reinitAds() {
        MyApplication.initializeAdGlide(this);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Dashboard
    // ─────────────────────────────────────────────────────────────────────────

    private void initAppConfig() {
        // SharedPref is the source of truth, SDK is initialized in onCreate via reinitAds or directly
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
        items.add(new DashboardItem("App Open Ads",
                "Manually test App Open (Pool & Show)", R.mipmap.ic_launcher, ActivityAppOpen.class));
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
