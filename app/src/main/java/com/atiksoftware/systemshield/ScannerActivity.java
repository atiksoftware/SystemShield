package com.atiksoftware.systemshield;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ScannerActivity extends AppCompatActivity {

    ConstraintLayout layout_scanner;
    LinearLayout layout_risk_alert;
    LinearLayout layout_start_scan;
    LinearLayout layout_scanning;
    LinearLayout layout_scanned;

    Button button_scanner_notification_active;
    Button button_scanner_scan_start;

    TextView textView_scanner_scanning_percent;
    TextView textView_scanner_scanning_app_name;
    TextView textView_scanner_scanned_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        layout_scanner = findViewById(R.id.layout_scanner);
        layout_risk_alert = findViewById(R.id.layout_risk_alert);
        layout_start_scan = findViewById(R.id.layout_start_scan);
        layout_scanning = findViewById(R.id.layout_scanning);
        layout_scanned = findViewById(R.id.layout_scanned);

        button_scanner_notification_active = findViewById(R.id.button_scanner_notification_active);
        button_scanner_notification_active.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        button_scanner_scan_start = findViewById(R.id.button_scanner_scan_start);
        button_scanner_scan_start.setOnClickListener(v -> new Thread(this::startScan).start());


        textView_scanner_scanning_percent = findViewById(R.id.textView_scanner_scanning_percent);
        textView_scanner_scanning_app_name = findViewById(R.id.textView_scanner_scanning_app_name);
        textView_scanner_scanned_desc = findViewById(R.id.textView_scanner_scanned_desc);

        layout_start_scan.setVisibility(LinearLayout.VISIBLE);
    }
    @Override
    protected void onResume() {
        try {
            super.onResume();
            checkNotificationListenerPermission();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void checkNotificationListenerPermission(){
        boolean permitted = false;
        String packageName = getPackageName();
        String flatName = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");

        if (flatName != null) {
            String[] names = flatName.split(":");
            for (String name : names) {
                ComponentName componentName = ComponentName.unflattenFromString(name);
                if (componentName != null && packageName.equals(componentName.getPackageName())) {
                    permitted = true;
                }
            }
        }

        if(permitted){
            layout_risk_alert.setVisibility(LinearLayout.GONE);
            layout_scanner.setBackgroundColor(getResources().getColor(R.color.emerald_500));
        }
        else{
            layout_risk_alert.setVisibility(LinearLayout.VISIBLE);
            layout_scanner.setBackgroundColor(getResources().getColor(R.color.red_500));
        }
    }

    private List<PackageInfo> getInstalledPackages() {
        List<PackageInfo> packageList = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        // Tüm uygulamaları al
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            // Sadece kullanıcı uygulamalarını al (sistem uygulamalarını atla)
            //if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) packageList.add(packageInfo);
            packageList.add(packageInfo);
        }
        return packageList;
    }
    @SuppressLint("SetTextI18n")
    public void startScan() {
        try {
            List<PackageInfo> installedPackages = getInstalledPackages();

            Thread.sleep(100);

            runOnUiThread(() -> {
                layout_start_scan.setVisibility(LinearLayout.GONE);
                layout_scanning.setVisibility(LinearLayout.VISIBLE);
                layout_scanned.setVisibility(LinearLayout.GONE);
            });

            Thread.sleep(100);

            int total = installedPackages.size();
            int count = 0;
            for (PackageInfo packageInfo : installedPackages) {
                String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                String packageName = packageInfo.packageName;

                int finalCount = count;
                runOnUiThread(() -> {
                    textView_scanner_scanning_percent.setText(getString(R.string.scanner_scanning_percent, finalCount, total));
                    textView_scanner_scanning_app_name.setText(getString(R.string.scanner_scanning_app_name, appName));
                });
                Thread.sleep(50);
                count++;
            }

            runOnUiThread(() -> {
                layout_start_scan.setVisibility(LinearLayout.GONE);
                layout_scanning.setVisibility(LinearLayout.GONE);
                layout_scanned.setVisibility(LinearLayout.VISIBLE);
                textView_scanner_scanned_desc.setText(getString(R.string.scanner_scanned_desc, total));
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}