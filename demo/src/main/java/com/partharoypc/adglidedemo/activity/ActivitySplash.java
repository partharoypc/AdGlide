package com.partharoypc.adglidedemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglidedemo.R;

public class ActivitySplash extends AppCompatActivity {

    public static int DELAY_PROGRESS = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // High-Performance Consent Management (GDPR/UMP)
        // AdGlide is already globally initialized via MyApplication.java.
        // We only need to request the consent form UI over this root activity.
        AdGlide.requestConsent(this, () -> {
            // Once consent is gathered (or skipped if not needed), continue to Main
            new Handler(Looper.getMainLooper()).postDelayed(this::startMainActivity, DELAY_PROGRESS);
        });
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
