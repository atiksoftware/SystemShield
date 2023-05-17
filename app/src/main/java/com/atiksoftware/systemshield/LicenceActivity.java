package com.atiksoftware.systemshield;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;
import android.util.Base64;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LicenceActivity extends AppCompatActivity {

    Button button_license_submit;
    AppCompatEditText editText_license_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licence);

        button_license_submit = findViewById(R.id.button_license_submit);
        button_license_submit.setOnClickListener(v -> checkLicense());

        editText_license_key = findViewById(R.id.editText_license_key);
    }

    void checkLicense() {
        button_license_submit.setEnabled(false);
        button_license_submit.setText(R.string.license_button_checking);

        String license_key = Objects.requireNonNull(editText_license_key.getText()).toString();


        new Thread(() -> {
            try {
                String api_server_url = new String(Base64.decode(license_key, Base64.DEFAULT)) + "/api.php";

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(api_server_url)
                        .build();
                Response response = client.newCall(request).execute();
                String responseData = Objects.requireNonNull(response.body()).string();

                if (!responseData.equals("0x00")) {
                    throw new Exception();
                }

                // update ui
                runOnUiThread(() -> {
                    button_license_submit.setText(R.string.license_button_check);
                    button_license_submit.setEnabled(true);
                    Toast.makeText(this, R.string.license_license_key_valid, Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getSharedPreferences("options", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("api_server_url", api_server_url);
                    editor.apply();
                    // start ScannerActivity
                    Intent intent = new Intent(this, ScannerActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
            catch (Exception e) {
                runOnUiThread(() -> {
                    button_license_submit.setText(R.string.license_button_check);
                    button_license_submit.setEnabled(true);
                    Toast.makeText(this, R.string.license_license_key_invalid, Toast.LENGTH_SHORT).show();
                });
            }
        }).start();

    }
}