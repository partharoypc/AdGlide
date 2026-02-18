package com.partharoypc.adglidedemo.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.partharoypc.adglide.format.NativeAdViewPager;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.data.Constant;
import com.partharoypc.adglidedemo.database.SharedPref;

public class ActivityNativeViewPager extends AppCompatActivity {

    SharedPref sharedPref;
    Toolbar toolbar;
    ViewPager viewPager;
    AdapterNativeAdViewPager adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        getAppTheme();
        setContentView(R.layout.activity_native_view_pager);
        initToolbar();
        initViewPager();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("Native Ad ViewPager");
        }
    }

    private void initViewPager() {
        viewPager = findViewById(R.id.view_pager);
        adapter = new AdapterNativeAdViewPager(this, 5); // Demonstrate with 5 pages
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void getAppTheme() {
        if (sharedPref.getIsDarkTheme()) {
            setTheme(R.style.AppDarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
    }

    private class AdapterNativeAdViewPager extends PagerAdapter {

        Context context;
        int count;

        public AdapterNativeAdViewPager(Context context, int count) {
            this.context = context;
            this.count = count;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_native_ad_pager, container, false);

            NativeAdViewPager.Builder nativeAdViewPager = new NativeAdViewPager.Builder(ActivityNativeViewPager.this,
                    view)
                    .setAdStatus(Constant.AD_STATUS)
                    .setAdNetwork(Constant.AD_NETWORK)
                    .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                    .setAdMobNativeId(Constant.ADMOB_NATIVE_ID)
                    .setAdManagerNativeId(Constant.GOOGLE_AD_MANAGER_NATIVE_ID)
                    .setFanNativeId(Constant.FAN_NATIVE_ID)
                    .setAppLovinNativeId(Constant.APPLOVIN_NATIVE_MANUAL_ID)
                    .setNativeAdBackgroundColor(R.color.colorNativeBackgroundLight, R.color.colorNativeBackgroundDark)
                    .setDarkTheme(sharedPref.getIsDarkTheme())
                    .build();

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
