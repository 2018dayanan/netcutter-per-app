package com.example.myapplication.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {

    private List<AppItem> appList = new ArrayList<>();
    private final OnRuleChangeListener listener;

    public interface OnRuleChangeListener {
        void onRuleChanged(AppItem item, boolean mobileBlocked, boolean wifiBlocked);
    }

    public AppListAdapter(OnRuleChangeListener listener) {
        this.listener = listener;
    }

    public void submitList(List<AppItem> newList) {
        appList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppItem item = appList.get(position);
        
        holder.appName.setText(item.appName);
        holder.packageName.setText(item.packageName);
        if (item.icon != null) {
            holder.appIcon.setImageDrawable(item.icon);
        } else {
            holder.appIcon.setImageResource(android.R.drawable.sym_def_app_icon);
        }

        // Remove listeners temporarily to avoid firing events during bind
        holder.switchMobile.setOnCheckedChangeListener(null);
        holder.switchWifi.setOnCheckedChangeListener(null);

        // UI shows "Blocked" if checked? Or "Allowed"? 
        // Typically, switch ON means "Blocked" if it's a block app, or ON means "Allowed".
        // Let's say Switch ON = Allowed (Default state). 
        // But prompt says "mobileBlocked" / "wifiBlocked". 
        // If we set switch to text="Data" and checked=true means allowed (not blocked).
        holder.switchMobile.setChecked(!item.mobileBlocked);
        holder.switchWifi.setChecked(!item.wifiBlocked);

        CompoundButton.OnCheckedChangeListener checkListener = (buttonView, isChecked) -> {
            boolean isMobileBlocked = !holder.switchMobile.isChecked();
            boolean isWifiBlocked = !holder.switchWifi.isChecked();
            listener.onRuleChanged(item, isMobileBlocked, isWifiBlocked);
        };

        holder.switchMobile.setOnCheckedChangeListener(checkListener);
        holder.switchWifi.setOnCheckedChangeListener(checkListener);
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView packageName;
        Switch switchMobile;
        Switch switchWifi;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            packageName = itemView.findViewById(R.id.packageName);
            switchMobile = itemView.findViewById(R.id.switchMobile);
            switchWifi = itemView.findViewById(R.id.switchWifi);
        }
    }
}
