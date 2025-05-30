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

import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    TextView notificationBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button resetButton = findViewById(R.id.resetButton);
        notificationBox = findViewById(R.id.notificationBox);

        resetButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("notifications", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("data");
            editor.apply();

            notificationBox.setText("Notifications cleared.");
        });

        // Ask for notification access if not enabled
        if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName())) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        }

        // Load saved notifications
        SharedPreferences prefs = getSharedPreferences("notifications", MODE_PRIVATE);
        String notifications = prefs.getString("data", "No notifications yet.");
        notificationBox.setText(notifications);

        // Logout using Firebase
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // <- Firebase logout

            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotifications();
        }
    };

    @Override
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
        SharedPreferences prefs = getSharedPreferences("notifications", MODE_PRIVATE);
        String notifications = prefs.getString("data", "No notifications yet.");
        notificationBox.setText(notifications);
    }
}
