package com.example.pbl;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

//        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
//        bottomNav.setOnItemSelectedListener(item -> {
//            switch (item.getItemId()) {
//
//                case R.id.nav_home:
//                    return true;
//                case R.id.nav_analysis:
//                    // startActivity(...);
//                    return true;
//                case R.id.nav_accounts:
//                    return true;
//                case R.id.nav_more:
//                    return true;
//            }
//            return false;
//        });
    }
}