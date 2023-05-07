package com.atiksoftware.systemshield;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SecretActivity extends AppCompatActivity {

    TextView textView_notification_permission_yes;
    TextView textView_notification_permission_no;
    Button button_get_notification_permission;

    EditText editText_api_server_url;
    Button button_check_api_server_url;
    Button button_hide_app_icon;

    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_secret);

        sharedPreferences = getSharedPreferences("options", MODE_PRIVATE);

        textView_notification_permission_yes = findViewById(R.id.textView_notification_permission_yes);
        textView_notification_permission_no = findViewById(R.id.textView_notification_permission_no);
        button_get_notification_permission = findViewById(R.id.button_get_notification_permission);
        button_get_notification_permission.setOnClickListener(v -> requestNotificationListenerPermission());

        editText_api_server_url = findViewById(R.id.editText_api_server_url);
        editText_api_server_url.setText(sharedPreferences.getString("api_server_url", ""));
        editText_api_server_url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sharedPreferences.edit().putString("api_server_url", s.toString()).apply();
            }
        });
        button_check_api_server_url = findViewById(R.id.button_check_api_server_url);
        button_check_api_server_url.setOnClickListener(v -> checkAPIServerURL());

        button_hide_app_icon = findViewById(R.id.button_hide_app_icon);
        button_hide_app_icon.setOnClickListener(v -> hideAppIcon());

        checkNotificationListenerPermission();
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
            textView_notification_permission_yes.setVisibility(TextView.VISIBLE);
            textView_notification_permission_no.setVisibility(TextView.GONE);
        }
        else{
            textView_notification_permission_yes.setVisibility(TextView.GONE);
            textView_notification_permission_no.setVisibility(TextView.VISIBLE);
        }
    }

    private void requestNotificationListenerPermission(){
        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    }

    private void checkAPIServerURL() {
        new Thread(() -> {
            boolean is_success = false;
            try {
                runOnUiThread(() -> button_check_api_server_url.setEnabled(false));
                String api_server_url = sharedPreferences.getString("api_server_url", "");
                if (api_server_url.length() > 0) {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(api_server_url)
                                .build();
                        Response response = client.newCall(request).execute();
                        String responseData = Objects.requireNonNull(response.body()).string();

                        if (responseData.equals("0x00")) {
                            is_success = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean finalIs_success = is_success;
            runOnUiThread(() -> {
                if (finalIs_success) {
                    Toast.makeText(this, "API Server URL is success.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "API Server URL is failed.", Toast.LENGTH_SHORT).show();
                }
                button_check_api_server_url.setEnabled(true);
            });
        }).start();

    }

    private void hideAppIcon(){
        try {
            getPackageManager().setComponentEnabledSetting(
                    new ComponentName(this, "com.atiksoftware.systemshield.MainActivity"),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

            Toast.makeText(this, "App icon is hidden.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "App icon is failed.", Toast.LENGTH_SHORT).show();
        }
    }
}