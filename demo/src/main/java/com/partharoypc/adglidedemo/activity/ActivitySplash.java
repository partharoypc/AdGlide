package com.partharoypc.adglidedemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.database.SharedPref;
import com.partharoypc.adglidedemo.data.Constant;

public class ActivitySplash extends AppCompatActivity {

    public static int DELAY_PROGRESS = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Load saved settings into constants
        SharedPref sharedPref = new SharedPref(this);
        Constant.AD_NETWORK = sharedPref.getAdNetwork();
        Constant.BACKUP_AD_NETWORK = sharedPref.getBackupAdNetwork();
        Constant.TEST_MODE = sharedPref.getTestMode();

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
