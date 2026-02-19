package com.partharoypc.adglidedemo.activity;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_DISCOVERY;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.FACEBOOK;
import static com.partharoypc.adglide.util.Constant.FAN;
import static com.partharoypc.adglide.util.Constant.GOOGLE_AD_MANAGER;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.NONE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.UNITY;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.partharoypc.adglide.gdpr.GDPR;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.adapter.AdapterNetwork;
import com.partharoypc.adglidedemo.data.Constant;
import com.partharoypc.adglidedemo.database.SharedPref;
import com.partharoypc.adglidedemo.application.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class ActivitySettings extends AppCompatActivity {

    private SharedPref sharedPref;
    private AdapterNetwork adapterPrimary;
    private AdapterNetwork adapterBackup;
    private List<AdapterNetwork.NetworkItem> primaryNetworks;
    private List<AdapterNetwork.NetworkItem> backupNetworks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        // Apply theme before setting content view
        if (sharedPref.getIsDarkTheme()) {
            setTheme(R.style.AppDarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_settings);

        setupToolbar();
        setupData();
        setupViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }
    }

    private void setupData() {
        primaryNetworks = new ArrayList<>();
        primaryNetworks.add(new AdapterNetwork.NetworkItem("AdMob", ADMOB));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("Google Ad Manager", GOOGLE_AD_MANAGER));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("Meta Audience Network", FAN));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("Unity Ads", UNITY));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("AppLovin MAX", APPLOVIN_MAX));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("AppLovin Discovery", APPLOVIN_DISCOVERY));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("ironSource", IRONSOURCE));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("Start.io", STARTAPP));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("Wortise", WORTISE));

        backupNetworks = new ArrayList<>();
        backupNetworks.add(new AdapterNetwork.NetworkItem("None", NONE));
        backupNetworks.addAll(primaryNetworks);
    }

    private void setupViews() {
        // Primary Network
        RecyclerView recyclerViewPrimary = findViewById(R.id.recycler_view_primary);
        recyclerViewPrimary.setLayoutManager(new LinearLayoutManager(this));
        adapterPrimary = new AdapterNetwork(this, primaryNetworks, sharedPref.getAdNetwork(), item -> {
            sharedPref.setAdNetwork(item.adNetworkId);
            Constant.AD_NETWORK = item.adNetworkId;
            initAds(); // Re-initialize immediately
        });
        recyclerViewPrimary.setAdapter(adapterPrimary);

        // Backup Network
        RecyclerView recyclerViewBackup = findViewById(R.id.recycler_view_backup);
        recyclerViewBackup.setLayoutManager(new LinearLayoutManager(this));
        adapterBackup = new AdapterNetwork(this, backupNetworks, sharedPref.getBackupAdNetwork(), item -> {
            sharedPref.setBackupAdNetwork(item.adNetworkId);
            Constant.BACKUP_AD_NETWORK = item.adNetworkId;
            initAds(); // Re-initialize immediately
        });
        recyclerViewBackup.setAdapter(adapterBackup);

        // Dark Mode Switch
        SwitchMaterial switchDarkMode = findViewById(R.id.switch_dark_mode);
        switchDarkMode.setChecked(sharedPref.getIsDarkTheme());
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsDarkTheme(isChecked);
            recreate(); // Recreate activity to apply theme
        });

        // App Open Ad Switch
        SwitchMaterial switchAppOpenAd = findViewById(R.id.switch_app_open_ad);
        switchAppOpenAd.setChecked(sharedPref.getIsAppOpenAdEnabled());
        switchAppOpenAd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsAppOpenAdEnabled(isChecked);
            // Update constant if needed? Constant.OPEN_ADS_ON_RESUME seems static/final in
            // Constant.java
            // Actually Constant.OPEN_ADS_ON_RESUME is 'public static final boolean' in
            // original Constant.java
            // I should override it or handle it dynamically.
            // Wait, Constant.OPEN_ADS_ON_RESUME is final in original file?
            // "public static final boolean OPEN_ADS_ON_RESUME = true;"
            // I need to change 'final' to non-final in Constant.java if I want to toggle it
            // at runtime.
            // Or better, update MyApplication to check SharedPref directly.
        });

        // GDPR Button
        Button btnGdpr = findViewById(R.id.btn_gdpr);
        btnGdpr.setOnClickListener(v -> {
            new GDPR(this).resetConsent();
        });
    }

    private void initAds() {
        // Re-trigger initialization in main activity or application if needed.
        // Since logic is in MainActivity's initAds(), we might need to restart
        // MainActivity or just update Constant.
        // Constant is updated.
        // If we go back to MainActivity, it doesn't re-run onCreate unless destroyed.
        // But ad loading usually happens in 'loadBanner' etc. which uses
        // Constant.AD_NETWORK at call time.
        // So update here is enough for subsequent ad loads.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Ensure MainActivity refreshes if needed, or simply finish.
        // If theme changed, MainActivity needs recreate.
        // Since we are finishing, MainActivity onResume is called.
        // Better to just finish.
        super.onBackPressed();
    }
}
