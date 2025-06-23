package com.example.healthapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    private double potassiumValue;
    private double sodiumValue;
    private double phValue;
    private String resultStatus;
    private String sampleId;

    private Button btnSaveResult;

    private ImageView homeIcon;
    private HistoryDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        potassiumValue = getIntent().getDoubleExtra("potassium", 0);
        sodiumValue = getIntent().getDoubleExtra("sodium", 0);
        phValue = getIntent().getDoubleExtra("ph", 0);
        resultStatus = getIntent().getStringExtra("result");
        sampleId = getIntent().getStringExtra("sampleId");

        dbHelper = new HistoryDatabaseHelper(this);
        homeIcon = findViewById(R.id.home_icon);
        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(ResultActivity.this, DashboardActivity.class));
            finish();
        });
        btnSaveResult = findViewById(R.id.btnSaveResult);

        btnSaveResult.setOnClickListener(v -> promptPatientNameAndSave());
    }

    private void promptPatientNameAndSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Patient Name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String patientName = input.getText().toString().trim();

            if (patientName.isEmpty()) {
                Toast.makeText(ResultActivity.this, "Patient name cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if patient exists BEFORE saving
            if (dbHelper.isPatientNameExists(patientName)) {
                Toast.makeText(this, "A record with this patient name already exists.", Toast.LENGTH_LONG).show();
                return;
            }

            // If not exists, save and go
            saveHistoryAndGoToHistoryActivity(patientName);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveHistoryAndGoToHistoryActivity(String patientName) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        HistoryRecord record = new HistoryRecord();
        record.setPatientName(patientName);
        record.setSampleId(sampleId != null ? sampleId : "N/A");
        record.setDate(currentDate);
        record.setPotassium(potassiumValue);
        record.setSodium(sodiumValue);
        record.setPh(phValue);
        record.setResult(resultStatus != null ? resultStatus : "Unknown");

        dbHelper.addHistory(record);

        Toast.makeText(this, "Result saved successfully!", Toast.LENGTH_SHORT).show();

        btnSaveResult.setEnabled(false);

        Intent intent = new Intent(ResultActivity.this, HistoryActivity.class);  // make sure this is your history activity class name
        startActivity(intent);
    }
}
