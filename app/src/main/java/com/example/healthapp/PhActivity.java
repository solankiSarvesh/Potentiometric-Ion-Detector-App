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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PhActivity extends AppCompatActivity {

    private TextView sampleText, dateText, phValueText, statusText;
    private Button btnSendPh;
    private ImageView homeIcon;

    private final String API_URL = "http://192.168.1.100/ph"; // Replace with your actual IP endpoint

    private String sample = "", date = "", status = "";
    private double phValue = 0.0;

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ph);

        sampleText = findViewById(R.id.sample_id_ph);
        dateText = findViewById(R.id.sample_date_ph);
        phValueText = findViewById(R.id.ph_value);
        statusText = findViewById(R.id.status_ph);
        btnSendPh = findViewById(R.id.btnSendPh);
        homeIcon = findViewById(R.id.home_icon);

        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(PhActivity.this, DashboardActivity.class));
            finish();
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (getIntent() != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            handleNfcIntent(getIntent());
        } else {
            fetchPhData();
        }

        btnSendPh.setOnClickListener(v -> {
            Intent intent = new Intent(PhActivity.this, ResultActivity.class);
            intent.putExtra("ion", "pH");
            intent.putExtra("sample", sample);
            intent.putExtra("date", date);
            intent.putExtra("potential", 0.0); // No potential reading for pH here
            intent.putExtra("concentration", phValue);
            intent.putExtra("status", status);
            startActivity(intent);
        });
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

                    Log.d("NFC_PH_JSON", payload);
                    JSONObject json = new JSONObject(payload);

                    phValue = json.getDouble("ph");
                    sample = "NFC Sample";
                    date = "Now";
                    status = getStatus(phValue);

                    runOnUiThread(this::updateUI);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "NFC Read Error", Toast.LENGTH_SHORT).show();
            Log.e("PhActivity", "NFC Exception", e);
        }
    }

    private void fetchPhData() {
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

                sample = json.getString("sample");
                date = json.getString("date");
                phValue = json.getDouble("ph_value");
                status = getStatus(phValue);

                runOnUiThread(this::updateUI);

            } catch (Exception e) {
                Log.e("PhActivity", "HTTP Error: " + e.getMessage(), e);
            }
        }).start();
    }

    private void updateUI() {
        sampleText.setText("Sample ID: " + sample);
        dateText.setText("Date: " + date);
        phValueText.setText("pH: " + String.format("%.2f", phValue));
        statusText.setText("Status: " + status);
        statusText.setTextColor(getResources().getColor(
                status.equals("Normal") ? R.color.green :
                        status.equals("High") ? R.color.red : R.color.orange
        ));
    }

    private String getStatus(double pH) {
        if (pH >= 6.8 && pH <= 7.8) return "Normal";
        else if (pH > 7.8) return "High";
        else return "Low";
    }
}
