package com.partharoypc.adglidedemo.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class SharedPref {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String KEY_POSTS = "posts";

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Boolean getIsDarkTheme() {
        return sharedPreferences.getBoolean("theme", false);
    }

    public void setIsDarkTheme(Boolean isDarkTheme) {
        editor.putBoolean("theme", isDarkTheme);
        editor.apply();
    }

    public String getAdNetwork() {
        return sharedPreferences.getString("ad_network", "admob");
    }

    public void setAdNetwork(String adNetwork) {
        editor.putString("ad_network", adNetwork);
        editor.apply();
    }

    public String getBackupAdNetwork() {
        return sharedPreferences.getString("backup_ad_network", "none");
    }

    public void setBackupAdNetwork(String backupAdNetwork) {
        editor.putString("backup_ad_network", backupAdNetwork);
        editor.apply();
    }

    public Boolean getIsAppOpenAdEnabled() {
        return sharedPreferences.getBoolean("app_open_ad_enabled", true);
    }

    public void setIsAppOpenAdEnabled(Boolean isEnabled) {
        editor.putBoolean("app_open_ad_enabled", isEnabled);
        editor.apply();
    }

    public Boolean getIsBannerEnabled() {
        return sharedPreferences.getBoolean("banner_enabled", true);
    }

    public void setIsBannerEnabled(Boolean isEnabled) {
        editor.putBoolean("banner_enabled", isEnabled);
        editor.apply();
    }

    public Boolean getIsInterstitialEnabled() {
        return sharedPreferences.getBoolean("interstitial_enabled", true);
    }

    public void setIsInterstitialEnabled(Boolean isEnabled) {
        editor.putBoolean("interstitial_enabled", isEnabled);
        editor.apply();
    }

    public Boolean getIsNativeEnabled() {
        return sharedPreferences.getBoolean("native_enabled", true);
    }

    public void setIsNativeEnabled(Boolean isEnabled) {
        editor.putBoolean("native_enabled", isEnabled);
        editor.apply();
    }

    public Boolean getIsRewardedEnabled() {
        return sharedPreferences.getBoolean("rewarded_enabled", true);
    }

    public void setIsRewardedEnabled(Boolean isEnabled) {
        editor.putBoolean("rewarded_enabled", isEnabled);
        editor.apply();
    }

    public Boolean getIsRewardedInterstitialEnabled() {
        return sharedPreferences.getBoolean("rewarded_interstitial_enabled", true);
    }

    public void setIsRewardedInterstitialEnabled(Boolean isEnabled) {
        editor.putBoolean("rewarded_interstitial_enabled", isEnabled);
        editor.apply();
    }

    public Boolean getIsHouseAdEnabled() {
        return sharedPreferences.getBoolean("house_ad_enabled", true);
    }

    public void setIsHouseAdEnabled(Boolean isEnabled) {
        editor.putBoolean("house_ad_enabled", isEnabled);
        editor.apply();
    }

    public int getInterstitialInterval() {
        return sharedPreferences.getInt("interstitial_interval", 3);
    }

    public void setInterstitialInterval(int interval) {
        editor.putInt("interstitial_interval", interval);
        editor.apply();
    }

    public int getRewardedInterval() {
        return sharedPreferences.getInt("rewarded_interval", 1);
    }

    public void setRewardedInterval(int interval) {
        editor.putInt("rewarded_interval", interval);
        editor.apply();
    }

    public boolean getTestMode() {
        return sharedPreferences.getBoolean("test_mode", false);
    }

    public void setTestMode(boolean testMode) {
        editor.putBoolean("test_mode", testMode);
        editor.apply();
    }

    public boolean getDebugMode() {
        return sharedPreferences.getBoolean("debug_mode", true);
    }

    public void setDebugMode(boolean debugMode) {
        editor.putBoolean("debug_mode", debugMode);
        editor.apply();
    }

    public boolean getEnableDebugHud() {
        return sharedPreferences.getBoolean("enable_debug_hud", true);
    }

    public void setEnableDebugHud(boolean enable) {
        editor.putBoolean("enable_debug_hud", enable);
        editor.apply();
    }


    public int getAdResponseTimeoutMs() {
        return sharedPreferences.getInt("ad_response_timeout_ms", 3500);
    }

    public void setAdResponseTimeoutMs(int timeoutMs) {
        editor.putInt("ad_response_timeout_ms", timeoutMs);
        editor.apply();
    }

    public int getAppOpenCooldownMinutes() {
        return sharedPreferences.getInt("app_open_cooldown_minutes", 30);
    }

    public void setAppOpenCooldownMinutes(int minutes) {
        editor.putInt("app_open_cooldown_minutes", minutes);
        editor.apply();
    }

    public String getNativeStyle() {
        return sharedPreferences.getString("native_style", "medium");
    }

    public void setNativeStyle(String style) {
        editor.putString("native_style", style);
        editor.apply();
    }

}
