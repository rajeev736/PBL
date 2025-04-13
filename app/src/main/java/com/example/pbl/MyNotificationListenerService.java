package com.example.pbl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class MyNotificationListenerService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String title = sbn.getNotification().extras.getString("android.title", "");
        String text = sbn.getNotification().extras.getString("android.text", "");

        String notificationData = title + ": " + text;

        // Save to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("notifications", MODE_PRIVATE);
        String existing = prefs.getString("data", "");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("data", existing + "\n" + notificationData);
        editor.apply();

        Intent intent = new Intent("com.example.pbl.NOTIFICATION_UPDATED");
        sendBroadcast(intent);
    }
}
