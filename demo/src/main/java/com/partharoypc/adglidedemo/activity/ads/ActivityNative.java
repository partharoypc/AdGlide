package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.partharoypc.adglide.format.NativeAd;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.data.Constant;
import com.partharoypc.adglidedemo.database.SharedPref;
import static com.partharoypc.adglidedemo.data.Constant.*;

public class ActivityNative extends AppCompatActivity {

    private LinearLayout nativeAdContainer;
    private SharedPref sharedPref;
    private NativeAd.Builder nativeAd;

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
        setNativeAdStyle(nativeAdContainer);

        nativeAd = new NativeAd.Builder(this)
                .setAdStatus(Constant.AD_STATUS)
                .setAdNetwork(Constant.AD_NETWORK)
                .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                .setAdMobNativeId(Constant.ADMOB_NATIVE_ID)
                .setMetaNativeId(Constant.META_NATIVE_ID)
                .setAppLovinNativeId(Constant.APPLOVIN_NATIVE_MANUAL_ID)
                .setWortiseNativeId(Constant.WORTISE_NATIVE_ID)
                .setNativeAdStyle(Constant.NATIVE_STYLE)
                .setNativeAdBackgroundColor(R.color.colorNativeBackgroundLight, R.color.colorNativeBackgroundDark)
                .setPadding(0, 0, 0, 0)
                .setDarkTheme(sharedPref.getIsDarkTheme())
                .build().load();
    }

    private void setNativeAdStyle(LinearLayout nativeAdView) {
        switch (Constant.NATIVE_STYLE) {
            case STYLE_NEWS:
                nativeAdView.addView(
                        View.inflate(this, com.partharoypc.adglide.R.layout.adglide_view_native_ad_news, null));
                break;
            case STYLE_RADIO:
                nativeAdView.addView(
                        View.inflate(this, com.partharoypc.adglide.R.layout.adglide_view_native_ad_radio, null));
                break;
            case STYLE_VIDEO_SMALL:
                nativeAdView
                        .addView(View.inflate(this, com.partharoypc.adglide.R.layout.adglide_view_native_ad_video_small,
                                null));
                break;
            case STYLE_VIDEO_LARGE:
                nativeAdView
                        .addView(View.inflate(this, com.partharoypc.adglide.R.layout.adglide_view_native_ad_video_large,
                                null));
                break;
            default:
                nativeAdView.addView(
                        View.inflate(this, com.partharoypc.adglide.R.layout.adglide_view_native_ad_medium, null));
                break;
        }
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
        super.onDestroy();
        if (nativeAd != null) {
            nativeAd.destroyNativeAd();
        }
    }
}
