package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglide.util.AdGlideCallback;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.data.Constant;
import com.partharoypc.adglidedemo.database.SharedPref;

import static com.partharoypc.adglidedemo.data.Constant.*;

public class ActivityNative extends AppCompatActivity {

    private SharedPref sharedPref;
    private LinearLayout nativeAdContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        setContentView(R.layout.activity_native);

        setupToolbar();
        initViews();
        loadNativeAd();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Native Ad");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void initViews() {
        nativeAdContainer = findViewById(R.id.native_ad_container);
        Constant.NATIVE_STYLE = sharedPref.getNativeStyle();
        Button btnStyle = findViewById(R.id.btn_style);
        Button btnRefresh = findViewById(R.id.btn_refresh);

        btnStyle.setOnClickListener(v -> showStyleDialog());
        btnRefresh.setOnClickListener(v -> loadNativeAd());
    }

    private void loadNativeAd() {
        destroyNative();
        AdGlideLog.d("ActivityNative", "⏳ Loading Native Ad with style: " + Constant.NATIVE_STYLE);

        com.partharoypc.adglide.AdGlideNativeStyle style;
        switch (Constant.NATIVE_STYLE) {
            case STYLE_SMALL:
                style = com.partharoypc.adglide.AdGlideNativeStyle.SMALL;
                break;
            case STYLE_BANNER:
                style = com.partharoypc.adglide.AdGlideNativeStyle.BANNER;
                break;
            case STYLE_VIDEO:
                style = com.partharoypc.adglide.AdGlideNativeStyle.VIDEO;
                break;
            case STYLE_MEDIUM:
            default:
                style = com.partharoypc.adglide.AdGlideNativeStyle.MEDIUM;
                break;
        }

        // Using the "Super Perfect" Native Builder for automatic Shimmer & Pool management
        new com.partharoypc.adglide.format.NativeAd.Builder(this)
                .style(style)
                .container(nativeAdContainer)
                .load(new AdGlideCallback() {
                    @Override
                    public void onAdLoaded(String network) {
                        AdGlideLog.d("ActivityNative", "✅ Native Ad Loaded from " + network);
                    }

                    @Override
                    public void onAdFailedToLoad(String error) {
                        AdGlideLog.e("ActivityNative", "❌ Native Ad Failed: " + error);
                    }
                });
    }

    private void destroyNative() {
        if (nativeAdContainer != null) {
            nativeAdContainer.removeAllViews();
        }
    }

    private void showStyleDialog() {
        final String[] styles = { "Medium (Default)", "Small", "Banner", "Video" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Native Style");
        builder.setItems(styles, (dialog, which) -> {
            switch (which) {
                case 1:
                    Constant.NATIVE_STYLE = STYLE_SMALL;
                    break;
                case 2:
                    Constant.NATIVE_STYLE = STYLE_BANNER;
                    break;
                case 3:
                    Constant.NATIVE_STYLE = STYLE_VIDEO;
                    break;
                default:
                    Constant.NATIVE_STYLE = STYLE_MEDIUM;
                    break;
            }
            sharedPref.setNativeStyle(Constant.NATIVE_STYLE);
            loadNativeAd();
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
