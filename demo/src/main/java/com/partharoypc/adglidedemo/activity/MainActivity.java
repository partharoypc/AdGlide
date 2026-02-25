package com.partharoypc.adglidedemo.activity;

import static com.partharoypc.adglide.util.Constant.ADMOB;

import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.UNITY;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.format.AppOpenAd;
import com.partharoypc.adglide.gdpr.GDPR;
import com.partharoypc.adglidedemo.BuildConfig;
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
    private AppOpenAd.Builder appOpenAdBuilder;
    private GDPR gdpr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        getAppTheme();
        initAppConfig();
        setContentView(R.layout.activity_main);

        setupToolbar();
        initAds();
        loadGdpr();
        loadOpenAds();

        setupDashboard();
    }

    private void initAppConfig() {
        Constant.AD_NETWORK = sharedPref.getAdNetwork();
        Constant.BACKUP_AD_NETWORK = sharedPref.getBackupAdNetwork();
        Constant.OPEN_ADS_ON_RESUME = sharedPref.getIsAppOpenAdEnabled();
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

        adapter = new DashboardAdapter(this, items);
        recyclerView.setAdapter(adapter);
    }

    private void initAds() {
        AdGlide.init(this)
                .status(Constant.AD_STATUS)
                .network(Constant.AD_NETWORK)
                .backup(Constant.BACKUP_AD_NETWORK)
                .adMobId(null)
                .startAppId(Constant.STARTAPP_APP_ID)
                .unityId(Constant.UNITY_GAME_ID)
                .appLovinId(getResources().getString(R.string.app_lovin_sdk_key))
                .ironSourceId(Constant.IRONSOURCE_APP_KEY)
                .wortiseId(Constant.WORTISE_APP_ID)
                .debug(BuildConfig.DEBUG)
                .build();
    }

    private void loadGdpr() {
        gdpr = new GDPR(this);
        gdpr.updateGDPRConsentStatus(Constant.AD_NETWORK, false, false);
    }

    private void loadOpenAds() {
        if (Constant.OPEN_ADS_ON_RESUME) {
            appOpenAdBuilder = new AppOpenAd.Builder(this)
                    .status(Constant.AD_STATUS)
                    .network(Constant.AD_NETWORK)
                    .backup(Constant.BACKUP_AD_NETWORK)
                    .adMobId(Constant.ADMOB_APP_OPEN_AD_ID)
                    .appLovinId(Constant.APPLOVIN_APP_OPEN_AP_ID)
                    .wortiseId(Constant.WORTISE_APP_OPEN_AD_ID)
                    .load();
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (appOpenAdBuilder != null) {
            appOpenAdBuilder.destroyOpenAd();
        }
    }
}
