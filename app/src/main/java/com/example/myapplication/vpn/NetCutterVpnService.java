package com.example.myapplication.vpn;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;
import com.example.myapplication.data.AppRule;
import com.example.myapplication.data.RuleRepository;

import java.io.IOException;
import java.util.List;

public class NetCutterVpnService extends VpnService {

    private static final String CHANNEL_ID = "netcutter_vpn_channel";
    private static final int NOTIFICATION_ID = 1;

    private ParcelFileDescriptor vpnInterface;
    private RuleRepository repository;
    private androidx.lifecycle.Observer<List<AppRule>> rulesObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new RuleRepository(getApplication());
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "STOP_VPN".equals(intent.getAction())) {
            stopVpn();
            return START_NOT_STICKY;
        }

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(NOTIFICATION_ID, createNotification("Net Cutter is active"), android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(NOTIFICATION_ID, createNotification("Net Cutter is active"));
        }

        if (rulesObserver == null) {
            rulesObserver = rules -> {
                setupVpn(rules);
            };
            repository.getAllRules().observeForever(rulesObserver);
        }

        return START_STICKY;
    }

    private void setupVpn(List<AppRule> rules) {
        if (vpnInterface != null) {
            try {
                vpnInterface.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Builder builder = new Builder();
        builder.setSession("Net Cutter");
        // We route a dummy IP just to establish the VPN interface,
        // Since we are using addAllowedApplication, only blocked apps will be routed here.
        // Actually, we want to block them, so we route them to a blackhole.
        builder.addAddress("10.0.0.2", 32);
        builder.addRoute("0.0.0.0", 0);

        if (rules != null) {
            for (AppRule rule : rules) {
                // Simplified MVP: If an app is blocked (either wifi or mobile for now), we route it to VPN (blackhole)
                // In a real implementation, we'd check ConnectivityManager active network type, or use advanced parsing.
                if (rule.mobileBlocked || rule.wifiBlocked) {
                    try {
                        builder.addAllowedApplication(rule.packageName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        builder.setBlocking(true);
        try {
            vpnInterface = builder.establish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopVpn() {
        if (vpnInterface != null) {
            try {
                vpnInterface.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            vpnInterface = null;
        }
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rulesObserver != null) {
            repository.getAllRules().removeObserver(rulesObserver);
            rulesObserver = null;
        }
        stopVpn();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Net Cutter VPN Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private Notification createNotification(String contentText) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Net Cutter")
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
    }
}
