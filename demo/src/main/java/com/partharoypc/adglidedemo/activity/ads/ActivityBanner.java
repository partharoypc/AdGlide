package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.partharoypc.adglide.AdGlide;
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

        bannerContainer.removeAllViews();
        bannerAd = AdGlide.loadBanner(this, bannerContainer);
        if (switchCollapsible.isChecked()) {
            bannerAd.setIsCollapsibleBanner(true);
        }
    }

    private void destroyBanner() {
        if (bannerAd != null) {
            bannerAd.destroyAd();
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
