package com.partharoypc.adglidedemo.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.partharoypc.adglidedemo.model.Post;

import java.lang.reflect.Type;
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

    public void savePostList(List<Post> posts) {
        Gson gson = new Gson();
        String json = gson.toJson(posts);
        editor.putString(KEY_POSTS, json);
        editor.apply();
    }

    public List<Post> getPostList() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_POSTS, null);
        Type type = new TypeToken<ArrayList<Post>>() {
        }.getType();
        return gson.fromJson(json, type);
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
