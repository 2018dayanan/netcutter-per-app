package com.example.myapplication.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppRuleDao {

    @Query("SELECT * FROM app_rules")
    LiveData<List<AppRule>> getAllRules();

    @Query("SELECT * FROM app_rules")
    List<AppRule> getAllRulesSync();

    @Query("SELECT * FROM app_rules WHERE packageName = :packageName LIMIT 1")
    AppRule getRuleByPackageName(String packageName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateRule(AppRule rule);

    @Update
    void updateRule(AppRule rule);
    
    @Query("UPDATE app_rules SET mobileBlocked = :blocked")
    void updateAllMobileBlocked(boolean blocked);

    @Query("UPDATE app_rules SET wifiBlocked = :blocked")
    void updateAllWifiBlocked(boolean blocked);
}
