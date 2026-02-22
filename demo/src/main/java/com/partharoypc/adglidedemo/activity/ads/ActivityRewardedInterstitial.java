package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglidedemo.R;

public class ActivityRewardedInterstitial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial); // Reuse interstitial layout

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Rewarded Interstitial Ad");
        }

        Button btnShow = findViewById(R.id.btn_show);
        btnShow.setOnClickListener(v -> {
            AdGlide.showRewardedInterstitial(this, () -> {
                Toast.makeText(this, "Reward Granted!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
