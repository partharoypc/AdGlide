package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.partharoypc.adglide.format.BannerAd;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.data.Constant;
import com.partharoypc.adglidedemo.database.SharedPref;

public class ActivityBanner extends AppCompatActivity {

    private LinearLayout bannerContainer;
    private BannerAd.Builder bannerAd;
    private SwitchMaterial switchCollapsible;
    private SharedPref sharedPref;
    private LinearLayout bannerAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        sharedPref = new SharedPref(this);

        setupToolbar();
        initViews();
        loadBanner();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Banner Ad");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void initViews() {
        bannerContainer = findViewById(R.id.banner_container);
        switchCollapsible = findViewById(R.id.switch_collapsible);
        Button btnRefresh = findViewById(R.id.btn_refresh);
        Button btnDestroy = findViewById(R.id.btn_destroy);

        btnRefresh.setOnClickListener(v -> loadBanner());
        btnDestroy.setOnClickListener(v -> destroyBanner());
    }

    private void loadBanner() {
        destroyBanner();

        // INFLATE THE LIBRARY LAYOUT HERE
        bannerContainer.removeAllViews();
        bannerAdView = (LinearLayout) View.inflate(this, com.partharoypc.adglide.R.layout.view_banner_ad, null);
        bannerContainer.addView(bannerAdView);

        bannerAd = new BannerAd.Builder(this)
                .setAdStatus(Constant.AD_STATUS)
                .setAdNetwork(Constant.AD_NETWORK)
                .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                .setAdMobBannerId(Constant.ADMOB_BANNER_ID)
                .setGoogleAdManagerBannerId(Constant.GOOGLE_AD_MANAGER_BANNER_ID)
                .setFanBannerId(Constant.FAN_BANNER_ID)
                .setUnityBannerId(Constant.UNITY_BANNER_ID)
                .setAppLovinBannerId(Constant.APPLOVIN_BANNER_ID)
                .setAppLovinBannerZoneId(Constant.APPLOVIN_BANNER_ZONE_ID)
                .setIronSourceBannerId(Constant.IRONSOURCE_BANNER_ID)
                .setWortiseBannerId(Constant.WORTISE_BANNER_ID)
                .setDarkTheme(sharedPref.getIsDarkTheme())
                .setIsCollapsibleBanner(switchCollapsible.isChecked())
                .build();
    }

    private void destroyBanner() {
        if (bannerAd != null) {
            bannerAd.destroyAndDetachBanner();
        }
        if (bannerContainer != null) {
            bannerContainer.removeAllViews();
        }
    }

    @Override
    protected void onDestroy() {
        destroyBanner();
        super.onDestroy();
    }
}
