package com.example.healthapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SodiumActivity extends AppCompatActivity {

    private TextView sampleText, dateText, potentialText, concentrationText, statusText;
    private Button nextButton;
    private ImageView homeIcon;
    private LineChart chart;

    private final String API_URL = "http://192.168.161.130/sodium"; // Replace with actual endpoint

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sodium);

        sampleText = findViewById(R.id.sample_id_na);
        dateText = findViewById(R.id.sample_date_na);
        potentialText = findViewById(R.id.potential_na);
        concentrationText = findViewById(R.id.concentration_na);
        statusText = findViewById(R.id.status_na);
        chart = findViewById(R.id.chart_sodium);
        nextButton = findViewById(R.id.btnNextPh);
        homeIcon = findViewById(R.id.home_icon);

        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(SodiumActivity.this, DashboardActivity.class));
            finish();
        });

        nextButton.setOnClickListener(v -> {
            startActivity(new Intent(SodiumActivity.this, PhActivity.class));
            finish();
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (getIntent() != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            handleNfcIntent(getIntent());
        } else {
            fetchSodiumData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING),
                    PendingIntent.FLAG_MUTABLE
            );
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNfcIntent(intent);
    }

    private void handleNfcIntent(Intent intent) {
        try {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                Ndef ndef = Ndef.get(tag);
                if (ndef != null) {
                    ndef.connect();
                    NdefMessage message = ndef.getNdefMessage();
                    NdefRecord record = message.getRecords()[0];
                    String payload = new String(record.getPayload(), "UTF-8");

                    if (payload.length() > 3 && payload.charAt(0) == 'e') {
                        payload = payload.substring(3);
                    }

                    Log.d("NFC_JSON", payload);
                    JSONObject json = new JSONObject(payload);

                    double concentration = json.getDouble("na");
                    double potential = 0; // Optional: fill if you send via NFC

                    String sample = "NFC Sample";
                    String date = "Now";
                    JSONArray dummyData = new JSONArray(); // optional if no chart data

                    updateUi(sample, date, potential, concentration, dummyData);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "NFC Read Error", Toast.LENGTH_SHORT).show();
            Log.e("NFC", "Exception: ", e);
        }
    }

    private void fetchSodiumData() {
        new Thread(() -> {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                JSONObject json = new JSONObject(builder.toString());

                String sample = json.getString("sample");
                String date = json.getString("date");
                double potential = json.getDouble("na_potential");
                double concentration = json.getDouble("na_concentration");
                JSONArray dataPoints = json.getJSONArray("data");

                runOnUiThread(() -> updateUi(sample, date, potential, concentration, dataPoints));

            } catch (Exception e) {
                Log.e("SodiumActivity", "Error: " + e.getMessage(), e);
            }
        }).start();
    }

    private void updateUi(String sample, String date, double potential, double concentration, JSONArray dataPoints) {
        try {
            ArrayList<Entry> entries = new ArrayList<>();
            for (int i = 0; i < dataPoints.length(); i++) {
                JSONObject point = dataPoints.optJSONObject(i);
                if (point != null) {
                    float t = (float) point.getDouble("t");
                    float v = (float) point.getDouble("v");
                    entries.add(new Entry(t, v));
                }
            }

            String status = evaluateStatus(concentration);

            sampleText.setText("Sample ID: " + sample);
            dateText.setText("Date: " + date);
            potentialText.setText("Na⁺ Potential: " + potential + " mV");
            concentrationText.setText("Na⁺ Concentration: " + String.format("%.2f", concentration) + " mmol/L");
            statusText.setText("Status: " + status);
            statusText.setTextColor(getResources().getColor(
                    status.equals("Normal") ? R.color.green :
                            status.equals("High") ? R.color.red : R.color.orange
            ));

            LineDataSet dataSet = new LineDataSet(entries, "Voltage vs Time");
            dataSet.setColor(getResources().getColor(R.color.blue));
            dataSet.setLineWidth(2f);
            dataSet.setDrawCircles(false);
            dataSet.setValueTextSize(10f);

            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate();

            Description description = new Description();
            description.setText("Na⁺ Potential (mV) over Time");
            chart.setDescription(description);

        } catch (Exception e) {
            Log.e("SodiumActivity", "UI update error", e);
        }
    }

    private String evaluateStatus(double value) {
        if (value >= 135 && value <= 145) return "Normal";
        else if (value > 145) return "High";
        else return "Low";
    }
}
