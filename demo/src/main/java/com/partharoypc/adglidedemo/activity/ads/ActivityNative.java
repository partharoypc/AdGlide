package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.data.Constant;
import static com.partharoypc.adglidedemo.data.Constant.*;

public class ActivityNative extends AppCompatActivity {

    private LinearLayout nativeAdContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Button btnStyle = findViewById(R.id.btn_style);
        Button btnRefresh = findViewById(R.id.btn_refresh);

        btnStyle.setOnClickListener(v -> showStyleDialog());
        btnRefresh.setOnClickListener(v -> loadNativeAd());
    }

    private void loadNativeAd() {
        destroyNative();

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

        // Flagship Elite Builder API:
        new com.partharoypc.adglide.format.NativeAd.Builder(this)
                .container(nativeAdContainer)
                .style(style)
                .load();
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
            loadNativeAd();
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
