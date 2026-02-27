package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.partharoypc.adglide.format.NativeAd;
import com.partharoypc.adglide.AdGlideNativeStyle;
import com.partharoypc.adglidedemo.R;

/**
 * Showcases the different premium Native Ad templates.
 */
public class ActivityNativeShowcase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_showcase);

        setupToolbar();
        loadSmallNative();
        loadMediumNative();
        loadVideoNative();
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Elite Native Templates");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void loadSmallNative() {
        new NativeAd.Builder(this)
                .container(findViewById(R.id.native_small_container))
                .style(AdGlideNativeStyle.SMALL)
                .load();
    }

    private void loadMediumNative() {
        new NativeAd.Builder(this)
                .container(findViewById(R.id.native_medium_container))
                .style(AdGlideNativeStyle.MEDIUM)
                .load();
    }

    private void loadVideoNative() {
        new NativeAd.Builder(this)
                .container(findViewById(R.id.native_video_container))
                .style(AdGlideNativeStyle.VIDEO)
                .load();
    }
}
