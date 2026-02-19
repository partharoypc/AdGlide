package com.partharoypc.adglidedemo.model;

public class DashboardItem {
    private String title;
    private String description;
    private int iconResId;
    private Class<?> activityClass;

    public DashboardItem(String title, String description, int iconResId, Class<?> activityClass) {
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.activityClass = activityClass;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIconResId() {
        return iconResId;
    }

    public Class<?> getActivityClass() {
        return activityClass;
    }
}
