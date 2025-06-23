package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    int[] icons = {
            R.drawable.potassium,
            R.drawable.sodium,
            R.drawable.ph,
            R.drawable.result,
            R.drawable.message,
            R.drawable.history
    };

    String[] titles = {
            "Potassium",
            "Sodium",
            "Ph Measure",
            "Final Result",
            "Message",
            "Records"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        GridLayout gridLayout = findViewById(R.id.gridLayout);

        for (int i = 0; i < titles.length; i++) {
            View item = LayoutInflater.from(this).inflate(R.layout.item_dashboard, gridLayout, false);

            ImageView icon = item.findViewById(R.id.icon);
            TextView title = item.findViewById(R.id.title);

            icon.setImageResource(icons[i]);
            title.setText(titles[i]);

            int index = i;
            item.setOnClickListener(v -> {
                Intent intent;
                switch (index) {
                    case 0:
                        intent = new Intent(this, PotassiumActivity.class);
                        break;
                    case 1:
                        intent = new Intent(this, SodiumActivity.class);
                        break;
                    case 2:
                        intent = new Intent(this, PhActivity.class);
                        break;
                    case 3:
                        intent = new Intent(this, ResultActivity.class);
                        break;
                    case 4:
                        intent = new Intent(this, MessageActivity.class);
                        break;
                    case 5:
                        intent = new Intent(this, HistoryActivity.class);
                        break;
                    default:
                        return;
                }
                startActivity(intent);
            });

            gridLayout.addView(item);
        }
    }
}
