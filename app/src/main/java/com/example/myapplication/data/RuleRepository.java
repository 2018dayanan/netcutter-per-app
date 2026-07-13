package com.example.myapplication.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RuleRepository {

    private AppRuleDao mAppRuleDao;
    private LiveData<List<AppRule>> mAllRules;

    public RuleRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mAppRuleDao = db.appRuleDao();
        mAllRules = mAppRuleDao.getAllRules();
    }

    public LiveData<List<AppRule>> getAllRules() {
        return mAllRules;
    }

    public void insertOrUpdateRule(AppRule rule) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mAppRuleDao.insertOrUpdateRule(rule);
        });
    }

    public List<AppRule> getAllRulesSync() {
        return mAppRuleDao.getAllRulesSync();
    }
}
