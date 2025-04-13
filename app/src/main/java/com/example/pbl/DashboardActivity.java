package com.example.pbl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    TextView notificationBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button resetButton = findViewById(R.id.resetButton);
        TextView notificationBox = findViewById(R.id.notificationBox);

        TextView finalNotificationBox = notificationBox;
        resetButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("notifications", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("data"); // Clear stored notification data
            editor.apply();

            // Update UI
            finalNotificationBox.setText("Notifications cleared.");
        });


        // Ask for notification access if not enabled
        if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName())) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        }

        notificationBox = findViewById(R.id.notificationBox); // Must be in layout

        SharedPreferences prefs = getSharedPreferences("notifications", MODE_PRIVATE);
        String notifications = prefs.getString("data", "No notifications yet.");
        notificationBox.setText(notifications);


    }
    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotifications();
        }
    };

    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.example.pbl.NOTIFICATION_UPDATED");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(notificationReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }
        updateNotifications();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(notificationReceiver);
    }

    private void updateNotifications() {
        TextView notificationBox = findViewById(R.id.notificationBox);
        SharedPreferences prefs = getSharedPreferences("notifications", MODE_PRIVATE);
        String notifications = prefs.getString("data", "No notifications yet.");
        notificationBox.setText(notifications);
    }

}
