package com.partharoypc.adglidedemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.application.MyApplication;
import com.partharoypc.adglidedemo.data.Constant;
import com.partharoypc.adglidedemo.database.SharedPref;

public class ActivitySplash extends AppCompatActivity {

    public static int DELAY_PROGRESS = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Centralized AdGlide & Constants Initialization
        MyApplication.initializeAdGlide(this);

        // UI References
        android.view.View logo = findViewById(R.id.logo_container);
        android.view.View title = findViewById(R.id.brand_name);
        android.view.View tagline = findViewById(R.id.tagline);

        // Animate UI components for "Premium" feel
        android.view.animation.Animation bounce = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.logo_bounce);
        android.view.animation.Animation fadeInUp = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in_up);

        logo.startAnimation(bounce);
        title.startAnimation(fadeInUp);
        tagline.startAnimation(fadeInUp);

        // High-Performance Consent Management (GDPR/UMP)
        AdGlide.requestConsent(this, () -> {
            new Handler(Looper.getMainLooper()).postDelayed(this::startMainActivity, DELAY_PROGRESS);
        });
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
