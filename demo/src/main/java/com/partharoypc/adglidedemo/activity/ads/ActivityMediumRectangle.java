package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.format.MediumRectangleAd;
import com.partharoypc.adglidedemo.R;

public class ActivityMediumRectangle extends AppCompatActivity {

    private MediumRectangleAd.Builder mrecAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner); // Reuse banner layout for simplicity or create a new one

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Medium Rectangle Ad");
        }

        LinearLayout adContainer = findViewById(R.id.banner_ad_container);
        mrecAd = AdGlide.loadMediumRectangle(this, adContainer);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (mrecAd != null) {
            mrecAd.destroyAd();
        }
        super.onDestroy();
    }
}
