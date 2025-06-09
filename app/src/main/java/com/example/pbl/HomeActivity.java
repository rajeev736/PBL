package com.example.pbl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    TextView userName;
    TextView s_amount, i_amount, b_amount;
    RecyclerView recyclerView;
    TransactionAdapter adapter;
    List<TransactionItem> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName())) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        }

        userName = findViewById(R.id.userName);
        s_amount = findViewById(R.id.s_amount);
        i_amount = findViewById(R.id.i_amount);
        b_amount = findViewById(R.id.b_amount);
        recyclerView = findViewById(R.id.recyclerView);

        Button addSpendingBtn = findViewById(R.id.addSpendingBtn);
        Button addIncomeBtn = findViewById(R.id.addIncomeBtn);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Show user's name
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("name");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    userName.setText(snapshot.exists() ? snapshot.getValue(String.class) : "Guest User");
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    userName.setText("Guest User");
                }
            });
        }

        addSpendingBtn.setOnClickListener(v -> openInputDialog("spending"));
        addIncomeBtn.setOnClickListener(v -> openInputDialog("income"));

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        setupBottomNavigation();
        updateUI();
    }

    private void openInputDialog(String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final android.view.View dialogView = inflater.inflate(R.layout.dialog_input_amount, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText inputAmount = dialogView.findViewById(R.id.inputAmount);
        EditText inputCategory = dialogView.findViewById(R.id.inputCategory); // Add this EditText in your layout
        Button addButton = dialogView.findViewById(R.id.addButton);

        addButton.setOnClickListener(v -> {
            String amountStr = inputAmount.getText().toString().trim();
            String category = inputCategory.getText().toString().trim();

            if (amountStr.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please enter amount and category", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount = Integer.parseInt(amountStr);
            SharedPreferences prefs = getSharedPreferences("my_data", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            int income = prefs.getInt("income_amount", 0);
            int spending = prefs.getInt("spending_amount", 0);

            if (type.equals("income")) {
                income += amount;
                editor.putInt("income_amount", income);
            } else {
                spending += amount;
                editor.putInt("spending_amount", spending);
            }

            int count = prefs.getInt("count", 0);
            editor.putString("type_" + count, type);
            editor.putInt("amount_" + count, amount);
            editor.putString("category_" + count, category);
            editor.putInt("count", count + 1);
            editor.apply();

            updateUI();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateUI() {
        SharedPreferences prefs = getSharedPreferences("my_data", MODE_PRIVATE);
        int income = prefs.getInt("income_amount", 0);
        int spending = prefs.getInt("spending_amount", 0);
        int balance = income - spending;

        i_amount.setText("₹" + income);
        s_amount.setText("₹" + spending);
        b_amount.setText("Balance : ₹" + balance);

        // Load transaction history
        int count = prefs.getInt("count", 0);
        transactionList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String type = prefs.getString("type_" + i, "");
            int amount = prefs.getInt("amount_" + i, 0);
            String category = prefs.getString("category_" + i, "");
            transactionList.add(new TransactionItem(type, amount, category));
        }

        adapter = new TransactionAdapter(transactionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView nav = findViewById(R.id.bottomNavigationView);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) return true;
            if (id == R.id.nav_analysis) startActivity(new Intent(this, AnalysisActivity.class));
            else if (id == R.id.nav_accounts) startActivity(new Intent(this, AccountsActivity.class));
            else if (id == R.id.nav_more) startActivity(new Intent(this, MoreActivity.class));

            return true;
        });
    }
}