package com.atiksoftware.systemshield;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LinearLayout layout_starting;
    LinearLayout layout_scanning;
    LinearLayout layout_scanned;
    TextView textView_viruce_and_privacy;
    TextView textView_scanning_desc;
    TextView textView_scanning_app_name;
    TextView textView_scanned_desc;

    int secret_click_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        layout_starting = findViewById(R.id.layout_starting);
        layout_scanning = findViewById(R.id.layout_scanning);
        layout_scanned = findViewById(R.id.layout_scanned);

        textView_viruce_and_privacy = findViewById(R.id.textView_viruce_and_privacy);
        textView_scanning_desc = findViewById(R.id.textView_scanning_desc);
        textView_scanning_app_name = findViewById(R.id.textView_scanning_app_name);
        textView_scanned_desc = findViewById(R.id.textView_scanned_desc);

        textView_viruce_and_privacy.setOnClickListener(v -> {
            secret_click_count++;
            if(secret_click_count < 10) return;
            secret_click_count = 0;
            Intent i = new Intent(this, SecretActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });


        new Thread(this::startScan).start();
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
                layout_starting.setVisibility(LinearLayout.GONE);
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
                    textView_scanning_desc.setText(getString(R.string.scanning_desc, finalCount, total));
                    textView_scanning_app_name.setText(appName + "\n" + packageName);
                });
                Thread.sleep(10);
                count++;
            }

            runOnUiThread(() -> {
                layout_starting.setVisibility(LinearLayout.GONE);
                layout_scanning.setVisibility(LinearLayout.GONE);
                layout_scanned.setVisibility(LinearLayout.VISIBLE);
                textView_scanned_desc.setText(getString(R.string.scanned_desc, total, total));
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}