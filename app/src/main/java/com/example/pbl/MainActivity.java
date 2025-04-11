package com.example.pbl;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    int count,i;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button btnC =findViewById(R.id.btnCount);
        TextView textC = findViewById(R.id.textCount);
        TextView textCy=findViewById(R.id.textCycle);




        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (count<100){
                    count++;
                    textC.setText("" + count);
                } else {
                    count=0;
                    textC.setText("" + count);
                    i++;
                    textCy.setText(i + " Cycle Completed");
                    if (i==3){
                        finish();
                    }
                }

            }
        });


        }
    }
