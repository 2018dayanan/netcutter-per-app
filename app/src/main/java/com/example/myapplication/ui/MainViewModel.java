package com.example.myapplication.ui;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.data.AppRule;
import com.example.myapplication.data.RuleRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {

    private final RuleRepository repository;
    private final MediatorLiveData<List<AppItem>> appItemsLiveData = new MediatorLiveData<>();
    private final MutableLiveData<List<ApplicationInfo>> installedAppsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(true);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String currentSearchQuery = "";

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new RuleRepository(application);

        appItemsLiveData.addSource(repository.getAllRules(), rules -> {
            combineData(installedAppsLiveData.getValue(), rules, currentSearchQuery);
        });

        appItemsLiveData.addSource(installedAppsLiveData, apps -> {
            combineData(apps, repository.getAllRules().getValue(), currentSearchQuery);
        });

        loadInstalledApps();
    }

    public LiveData<List<AppItem>> getAppItems() {
        return appItemsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public void filterApps(String query) {
        currentSearchQuery = query;
        combineData(installedAppsLiveData.getValue(), repository.getAllRules().getValue(), currentSearchQuery);
    }

    private void loadInstalledApps() {
        executorService.execute(() -> {
            PackageManager pm = getApplication().getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            List<ApplicationInfo> filteredApps = new ArrayList<>();
            // Optionally filter out some system apps if needed, for now include all
            for (ApplicationInfo info : apps) {
                // simple heuristic to filter some pure system packages could go here
                filteredApps.add(info);
            }
            installedAppsLiveData.postValue(filteredApps);
        });
    }

    private void combineData(List<ApplicationInfo> apps, List<AppRule> rules, String query) {
        if (apps == null) {
            isLoadingLiveData.postValue(true);
            return;
        }
        isLoadingLiveData.postValue(true);
        executorService.execute(() -> {
            PackageManager pm = getApplication().getPackageManager();
            Map<String, AppRule> ruleMap = new HashMap<>();
            if (rules != null) {
                for (AppRule r : rules) {
                    ruleMap.put(r.packageName, r);
                }
            }

            List<AppItem> items = new ArrayList<>();
            for (ApplicationInfo info : apps) {
                String appName = pm.getApplicationLabel(info).toString();
                if (query != null && !query.isEmpty() && !appName.toLowerCase().contains(query.toLowerCase())) {
                    continue;
                }

                AppRule rule = ruleMap.get(info.packageName);
                boolean mobileBlocked = rule != null && rule.mobileBlocked;
                boolean wifiBlocked = rule != null && rule.wifiBlocked;
                
                Drawable icon;
                try {
                    icon = pm.getApplicationIcon(info);
                } catch (Exception e) {
                    icon = null;
                }

                items.add(new AppItem(info.packageName, appName, icon, info.uid, mobileBlocked, wifiBlocked));
            }

            Collections.sort(items);
            appItemsLiveData.postValue(items);
            isLoadingLiveData.postValue(false);
        });
    }

    public void updateRule(AppItem item, boolean mobileBlocked, boolean wifiBlocked) {
        AppRule rule = new AppRule(item.packageName, item.uid, mobileBlocked, wifiBlocked, null);
        repository.insertOrUpdateRule(rule);
    }
}
