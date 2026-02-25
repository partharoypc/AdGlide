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
                .status(Constant.AD_STATUS)
                .network(Constant.AD_NETWORK)
                .backup(Constant.BACKUP_AD_NETWORK)
                .adMobId(Constant.ADMOB_NATIVE_ID)
                .metaId(Constant.META_NATIVE_ID)
                .appLovinId(Constant.APPLOVIN_NATIVE_MANUAL_ID)
                .wortiseId(Constant.WORTISE_NATIVE_ID)
                .startAppId(Constant.STARTAPP_APP_ID)
                .ironSourceId(Constant.IRONSOURCE_NATIVE_ID)
                .style(Constant.NATIVE_STYLE)
                .backgroundColor(R.color.colorNativeBackgroundLight, R.color.colorNativeBackgroundDark)
                .padding(0, 0, 0, 0)
                .darkTheme(sharedPref.getIsDarkTheme())
                .build().load();
    }

    private void setNativeAdStyle(LinearLayout nativeAdView) {
        switch (Constant.NATIVE_STYLE) {
            case STYLE_SMALL:
                nativeAdView.addView(
                        View.inflate(this, com.partharoypc.adglide.R.layout.adglide_view_native_ad_radio, null));
                break;
            case STYLE_BANNER:
                nativeAdView.addView(
                        View.inflate(this, com.partharoypc.adglide.R.layout.adglide_view_native_ad_news, null));
                break;
            case STYLE_VIDEO:
                nativeAdView.addView(
                        View.inflate(this, com.partharoypc.adglide.R.layout.adglide_view_native_ad_video_large, null));
                break;
            case STYLE_MEDIUM:
            default:
                nativeAdView.addView(
                        View.inflate(this, com.partharoypc.adglide.R.layout.adglide_view_native_ad_medium, null));
                break;
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
        if (nativeAd != null) {
            nativeAd.destroyNativeAd();
        }
    }
}
