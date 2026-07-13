package com.example.myapplication.ui;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class AppItem implements Comparable<AppItem> {
    public String packageName;
    public String appName;
    public Drawable icon;
    public int uid;
    public boolean mobileBlocked;
    public boolean wifiBlocked;

    public AppItem(String packageName, String appName, Drawable icon, int uid, boolean mobileBlocked, boolean wifiBlocked) {
        this.packageName = packageName;
        this.appName = appName;
        this.icon = icon;
        this.uid = uid;
        this.mobileBlocked = mobileBlocked;
        this.wifiBlocked = wifiBlocked;
    }

    @Override
    public int compareTo(@NonNull AppItem other) {
        return this.appName.compareToIgnoreCase(other.appName);
    }
}
