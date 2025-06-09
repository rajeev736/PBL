package com.example.pbl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class MyNotificationListenerService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String title = sbn.getNotification().extras.getString("android.title", "");
        String text = sbn.getNotification().extras.getString("android.text", "");

        if (title == null) title = "";
        if (text == null) text = "";

        String notificationData = title + ": " + text;
        Log.d("NotificationDebug", notificationData);

        // Save notification text
        SharedPreferences notifPrefs = getSharedPreferences("notifications", MODE_PRIVATE);
        String existing = notifPrefs.getString("data", "");
        SharedPreferences.Editor notifEditor = notifPrefs.edit();
        notifEditor.putString("data", existing + "\n" + notificationData);
        notifEditor.apply();

        String lowerText = text.toLowerCase();
        int amount = extractAmount(text);

        if (amount > 0) {
            SharedPreferences prefs = getSharedPreferences("my_data", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            int income = prefs.getInt("income_amount", 0);
            int spending = prefs.getInt("spending_amount", 0);
            int count = prefs.getInt("count", 0);
            String category = getAutoCategory(text);

            if (lowerText.contains("credited") || lowerText.contains("received")) {
                income += amount;
                editor.putInt("income_amount", income);

                editor.putString("type_" + count, "income");
                editor.putInt("amount_" + count, amount);
                editor.putString("category_" + count, category);
                editor.putInt("count", count + 1);

                Log.d("AutoIncome", "Credited ₹" + amount);
            } else if (lowerText.contains("debited") || lowerText.contains("withdrawn")) {
                spending += amount;
                editor.putInt("spending_amount", spending);

                editor.putString("type_" + count, "spending");
                editor.putInt("amount_" + count, amount);
                editor.putString("category_" + count, category);
                editor.putInt("count", count + 1);

                Log.d("AutoSpending", "Debited ₹" + amount);
            }

            editor.apply();

            // Broadcast update
            Intent intent = new Intent("com.example.pbl.NOTIFICATION_UPDATED");
            sendBroadcast(intent);
        }
    }

    private int extractAmount(String text) {
        try {
            text = text.replace(",", "");
            String[] words = text.split(" ");
            for (String word : words) {
                if (word.startsWith("₹")) {
                    return Integer.parseInt(word.replace("₹", "").replaceAll("[^0-9]", ""));
                } else if (word.toLowerCase().startsWith("inr")) {
                    return Integer.parseInt(word.replaceAll("[^0-9]", ""));
                }
            }
        } catch (Exception e) {
            Log.e("ExtractError", "Failed to parse amount: " + e.getMessage());
        }
        return 0;
    }

    private String getAutoCategory(String text) {
        text = text.toLowerCase();

        if (text.contains("amazon") || text.contains("flipkart") || text.contains("myntra"))
            return "Shopping";
        if (text.contains("paytm") || text.contains("google pay") || text.contains("phonepe"))
            return "Wallet";
        if (text.contains("swiggy") || text.contains("zomato") || text.contains("food"))
            return "Food";
        if (text.contains("electricity") || text.contains("gas") || text.contains("water"))
            return "Utilities";
        if (text.contains("salary") || text.contains("credited by employer"))
            return "Salary";
        if (text.contains("atm") || text.contains("withdraw"))
            return "Cash";

        return "Miscellaneous";
    }
}