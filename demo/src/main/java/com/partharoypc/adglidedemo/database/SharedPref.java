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

}
