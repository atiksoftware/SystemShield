package com.atiksoftware.systemshield;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

public class SecretCodeReceiver extends BroadcastReceiver {
    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "SecretCodeReceiver", Toast.LENGTH_SHORT).show();
        //Log.d("SystemShield -> onReceive", "onReceive");
        if (intent.getAction() == null)
            return;

        //Log.d("SystemShield -> action : ", intent.getAction());

        if (intent.getAction().equals("android.provider.Telephony.SECRET_CODE")) {
            String secretCode = intent.getDataString();

            if (secretCode == null)
                return;

            Intent i = new Intent(context, SecretActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
