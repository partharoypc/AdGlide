package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.format.NativeAd;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.data.Constant;
import com.partharoypc.adglidedemo.database.SharedPref;
import static com.partharoypc.adglidedemo.data.Constant.*;

public class ActivityNative extends AppCompatActivity {

    private LinearLayout nativeAdContainer;
    private NativeAd.Builder nativeAd;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        sharedPref = new SharedPref(this);

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
        nativeAdContainer.removeAllViews();
        nativeAd = AdGlide.loadNativeAd(this, nativeAdContainer)
                .setNativeAdStyle(Constant.NATIVE_STYLE)
                .setNativeAdBackgroundColor(R.color.colorNativeBackgroundLight, R.color.colorNativeBackgroundDark)
                .setPadding(0, 0, 0, 0)
                .setDarkTheme(sharedPref.getIsDarkTheme());
    }

    private void showStyleDialog() {
        final String[] styles = { "Default", "News", "Radio", "Video Small", "Video Large" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Native Style");
        builder.setItems(styles, (dialog, which) -> {
            switch (which) {
                case 1:
                    Constant.NATIVE_STYLE = STYLE_NEWS;
                    break;
                case 2:
                    Constant.NATIVE_STYLE = STYLE_RADIO;
                    break;
                case 3:
                    Constant.NATIVE_STYLE = STYLE_VIDEO_SMALL;
                    break;
                case 4:
                    Constant.NATIVE_STYLE = STYLE_VIDEO_LARGE;
                    break;
                default:
                    Constant.NATIVE_STYLE = STYLE_DEFAULT;
                    break;
            }
            loadNativeAd();
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroyAd();
        }
        super.onDestroy();
    }
}
