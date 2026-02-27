package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglidedemo.R;

public class ActivityBanner extends AppCompatActivity {

    private LinearLayout bannerContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

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
        Button btnRefresh = findViewById(R.id.btn_refresh);
        Button btnDestroy = findViewById(R.id.btn_destroy);

        btnRefresh.setOnClickListener(v -> loadBanner());
        btnDestroy.setOnClickListener(v -> destroyBanner());
    }

    private void loadBanner() {
        destroyBanner();

        SwitchMaterial switchCollapsible = findViewById(R.id.switch_collapsible);
        SwitchMaterial switchAdaptive = findViewById(R.id.switch_adaptive);

        // Professional Elite API:
        new com.partharoypc.adglide.format.BannerAd.Builder(this)
                .container(bannerContainer)
                .collapsible(switchCollapsible.isChecked())
                .adaptive(switchAdaptive.isChecked())
                .load();
    }

    private void destroyBanner() {
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
