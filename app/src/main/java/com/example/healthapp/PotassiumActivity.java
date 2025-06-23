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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PotassiumActivity extends AppCompatActivity {

    private TextView sampleText, dateText, potentialText, concentrationText, statusText;
    private Button nextButton;
    private ImageView homeIcon;
    private LineChart chartPotassium;

    private final String API_URL = "http://192.168.161.130/potassium";
    private NfcAdapter nfcAdapter;
    private static final String TAG = "PotassiumActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_potassium);

        sampleText = findViewById(R.id.sample_id);
        dateText = findViewById(R.id.sample_date);
        potentialText = findViewById(R.id.potential_k);
        concentrationText = findViewById(R.id.concentration_k);
        statusText = findViewById(R.id.status_k);
        chartPotassium = findViewById(R.id.chart_potassium);
        nextButton = findViewById(R.id.btnNextSodium);
        homeIcon = findViewById(R.id.home_icon);

        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(PotassiumActivity.this, DashboardActivity.class));
            finish();
        });

        nextButton.setOnClickListener(v -> {
            startActivity(new Intent(PotassiumActivity.this, SodiumActivity.class));
            finish();
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (getIntent() != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            handleNfcIntent(getIntent());
        } else {
            fetchPotassiumData(); // fallback to Wi-Fi
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
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                    NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                Ndef ndef = Ndef.get(tag);

                if (ndef != null) {
                    ndef.connect();
                    NdefMessage message = ndef.getNdefMessage();
                    if (message == null) {
                        Log.e(TAG, "Empty NDEF message");
                        return;
                    }

                    NdefRecord record = message.getRecords()[0];
                    byte[] rawPayload = record.getPayload();
                    int langLength = rawPayload[0] & 0x3F;  // Text encoding prefix byte
                    String jsonPayload = new String(rawPayload, langLength + 1,
                            rawPayload.length - langLength - 1, StandardCharsets.UTF_8);

                    Log.d(TAG, "NFC Payload: " + jsonPayload);

                    JSONObject json = new JSONObject(jsonPayload);
                    String sample = json.optString("s", "NFC Sample");
                    String date = json.optString("d", "Now");
                    double potential = json.optDouble("p", 0.0);
                    double concentration = json.optDouble("c", 0.0);
                    JSONArray data = json.optJSONArray("v");
                    if (data == null) data = new JSONArray();

                    updateUi(sample, date, potential, concentration, data);
                    ndef.close();
                } else {
                    Toast.makeText(this, "NFC Tag doesn't support NDEF", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "NFC Read Error", e);
            Toast.makeText(this, "NFC Read Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchPotassiumData() {
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
                double potential = json.getDouble("k_potential");
                double concentration = json.getDouble("k_concentration");
                JSONArray dataPoints = json.getJSONArray("data");

                runOnUiThread(() -> updateUi(sample, date, potential, concentration, dataPoints));
            } catch (Exception e) {
                Log.e(TAG, "WiFi Data Fetch Error", e);
            }
        }).start();
    }

    private void updateUi(String sample, String date, double potential, double concentration, JSONArray dataPoints) {
        try {
            ArrayList<Entry> entries = new ArrayList<>();
            for (int i = 0; i < dataPoints.length(); i++) {
                float value = (float) dataPoints.optDouble(i, 0.0);
                entries.add(new Entry(i, value));  // x = index, y = value
            }

            String status = evaluateStatus(concentration);

            sampleText.setText("Sample ID: " + sample);
            dateText.setText("Date: " + date);
            potentialText.setText("K⁺ Potential: " + potential + " mV");
            concentrationText.setText("K⁺ Concentration: " + String.format("%.2f", concentration) + " mmol/L");
            statusText.setText("Status: " + status);
            statusText.setTextColor(getResources().getColor(
                    status.equals("Normal") ? R.color.green :
                            status.equals("High") ? R.color.red : R.color.orange
            ));

            LineDataSet dataSet = new LineDataSet(entries, "Voltage vs Time");
            dataSet.setColor(getResources().getColor(R.color.blue));
            dataSet.setValueTextColor(getResources().getColor(R.color.black));
            dataSet.setLineWidth(2f);
            dataSet.setDrawCircles(true);

            LineData lineData = new LineData(dataSet);
            chartPotassium.setData(lineData);
            chartPotassium.invalidate();

            Description desc = new Description();
            desc.setText("K⁺ Potential (mV) over Time");
            chartPotassium.setDescription(desc);
        } catch (Exception e) {
            Log.e(TAG, "UI Update Error", e);
        }
    }

    private String evaluateStatus(double value) {
        if (value >= 3.5 && value <= 5.0) return "Normal";
        else if (value > 5.0) return "High";
        else return "Low";
    }
}
