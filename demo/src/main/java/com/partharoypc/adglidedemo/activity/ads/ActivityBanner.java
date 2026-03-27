package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglide.util.AdGlideCallback;
import com.partharoypc.adglidedemo.R;

public class ActivityBanner extends AppCompatActivity {

    private LinearLayout bannerContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        AdGlideLog.d("ActivityBanner", "⏳ Loading Banner Ad...");

        // Professional Premium API:
        new com.partharoypc.adglide.format.BannerAd.Builder(this)
                .container(bannerContainer)
                .autoRefresh(30) // Demonstrate 30s auto-refresh
                .load(new AdGlideCallback() {
                    @Override
                    public void onAdLoaded() {
                        AdGlideLog.d("ActivityBanner", "💎 Banner Ad Loaded Successfully");
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable String error) {
                        AdGlideLog.e("ActivityBanner", "🛑 Banner Ad Failed: " + (error != null ? error : "Unknown Error"));
                    }
                });
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
