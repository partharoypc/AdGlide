package com.partharoypc.adglidedemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.partharoypc.adglide.format.NativeAdFragment;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.data.Constant;
import com.partharoypc.adglidedemo.database.SharedPref;

public class FragmentNativeAd extends Fragment {

    SharedPref sharedPref;
    NativeAdFragment.Builder nativeAdFragment;

    public FragmentNativeAd() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_native_ad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPref = new SharedPref(getActivity());

        nativeAdFragment = new NativeAdFragment.Builder(getActivity())
                .setAdStatus(Constant.AD_STATUS)
                .setAdNetwork(Constant.AD_NETWORK)
                .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                .setAdMobNativeId(Constant.ADMOB_NATIVE_ID)
                .setAdManagerNativeId(Constant.GOOGLE_AD_MANAGER_NATIVE_ID)
                .setFanNativeId(Constant.FAN_NATIVE_ID)
                .setAppLovinNativeId(Constant.APPLOVIN_NATIVE_MANUAL_ID)
                .setAppLovinDiscoveryMrecZoneId(Constant.APPLOVIN_BANNER_MREC_ZONE_ID)
                .setNativeAdStyle(Constant.NATIVE_STYLE)
                .setNativeAdBackgroundColor(R.color.colorNativeBackgroundLight, R.color.colorNativeBackgroundDark)
                .setPadding(0, 0, 0, 0)
                .setDarkTheme(sharedPref.getIsDarkTheme())
                .setView(view)
                .build();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (nativeAdFragment != null) {
            nativeAdFragment.destroyNativeAd();
        }
    }
}
