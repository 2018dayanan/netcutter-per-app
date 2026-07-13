package com.example.myapplication.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "app_rules")
public class AppRule {

    @PrimaryKey
    @NonNull
    public String packageName;

    public int uid;
    public boolean mobileBlocked;
    public boolean wifiBlocked;
    public String groupId;

    public AppRule(@NonNull String packageName, int uid, boolean mobileBlocked, boolean wifiBlocked, String groupId) {
        this.packageName = packageName;
        this.uid = uid;
        this.mobileBlocked = mobileBlocked;
        this.wifiBlocked = wifiBlocked;
        this.groupId = groupId;
    }
}
