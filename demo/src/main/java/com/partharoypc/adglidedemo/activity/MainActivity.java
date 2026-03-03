package com.partharoypc.adglidedemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.partharoypc.adglide.AdGlide;

import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.activity.ads.ActivityBanner;
import com.partharoypc.adglidedemo.activity.ads.ActivityInterstitial;
import com.partharoypc.adglidedemo.activity.ads.ActivityNative;
import com.partharoypc.adglidedemo.activity.ads.ActivityRewarded;
import com.partharoypc.adglidedemo.adapter.DashboardAdapter;
import com.partharoypc.adglidedemo.data.Constant;
import com.partharoypc.adglidedemo.database.SharedPref;
import com.partharoypc.adglidedemo.model.DashboardItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SharedPref sharedPref;
    private RecyclerView recyclerView;
    private DashboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        getAppTheme();
        initAppConfig();
        setContentView(R.layout.activity_main);

        setupToolbar();

        setupDashboard();

        // Show a banner ad at the bottom of the dashboard
        ViewGroup bannerContainer = findViewById(R.id.banner_container);
        AdGlide.showBanner(this, bannerContainer);
    }

    private void initAppConfig() {
        Constant.AD_NETWORK = sharedPref.getAdNetwork();
        Constant.BACKUP_AD_NETWORK = sharedPref.getBackupAdNetwork();
        Constant.OPEN_ADS_ON_RESUME = sharedPref.getIsAppOpenAdEnabled();
        Constant.BANNER_STATUS = sharedPref.getIsBannerEnabled();
        Constant.INTERSTITIAL_STATUS = sharedPref.getIsInterstitialEnabled();
        Constant.NATIVE_STATUS = sharedPref.getIsNativeEnabled();
        Constant.REWARDED_STATUS = sharedPref.getIsRewardedEnabled();
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    private void setupDashboard() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<DashboardItem> items = new ArrayList<>();
        items.add(new DashboardItem("Banner Ads", "Standard and Collapsible Banner Ads", R.mipmap.ic_launcher,
                ActivityBanner.class));
        items.add(new DashboardItem("Interstitial Ads", "Full screen ads", R.mipmap.ic_launcher,
                ActivityInterstitial.class));
        items.add(new DashboardItem("Rewarded Ads", "Reward users for watching ads", R.mipmap.ic_launcher,
                ActivityRewarded.class));
        items.add(new DashboardItem("Native Ads", "Ads that blend into content", R.mipmap.ic_launcher,
                ActivityNative.class));
        items.add(new DashboardItem("Rewarded Interstitial", "Hybrid ad: Watch to earn reward", R.mipmap.ic_launcher,
                com.partharoypc.adglidedemo.activity.ads.ActivityRewardedInterstitial.class));
        items.add(new DashboardItem("SDK Debugger (HUD)", "Analyze fill issues & waterfall", R.mipmap.ic_launcher,
                null)); // Custom click

        adapter = new DashboardAdapter(this, items, item -> {
            if (item.getActivityClass() != null) {
                startActivity(new Intent(MainActivity.this, item.getActivityClass()));
            } else if (item.getTitle().contains("Debugger")) {
                com.partharoypc.adglide.AdGlide.showDebugHUD(MainActivity.this);
            } else if (item.getTitle().contains("Rewarded Interstitial")) {
                showRewardedInterstitial();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showRewardedInterstitial() {
        startActivity(new Intent(this, com.partharoypc.adglidedemo.activity.ads.ActivityRewardedInterstitial.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), ActivitySettings.class));
            return true;
        } else if (id == R.id.action_theme) {
            toggleTheme();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleTheme() {
        sharedPref.setIsDarkTheme(!sharedPref.getIsDarkTheme());
        recreate();
    }

    public void getAppTheme() {
        if (sharedPref.getIsDarkTheme()) {
            setTheme(R.style.AppDarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
    }
}
