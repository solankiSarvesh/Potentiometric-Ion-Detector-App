package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.OnDeleteClickListener {

    private RecyclerView recyclerViewHistory;
    private HistoryAdapter adapter;
    private HistoryDatabaseHelper dbHelper;

    private ImageView homeIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new HistoryDatabaseHelper(this);
        homeIcon = findViewById(R.id.home_icon);
        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(HistoryActivity.this, DashboardActivity.class));
            finish();
        });

        loadHistory();
    }

    private void loadHistory() {
        List<HistoryRecord> historyRecords = dbHelper.getAllHistory();
        adapter = new HistoryAdapter(historyRecords, this);
        recyclerViewHistory.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory(); // Refresh list when returning to this activity
    }

    @Override
    public void onDeleteClick(int recordId) {
        boolean deleted = dbHelper.deleteRecordById(recordId);
        if (deleted) {
            Toast.makeText(this, "Record deleted!", Toast.LENGTH_SHORT).show();
            loadHistory();
        } else {
            Toast.makeText(this, "Delete failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
