package com.example.healthapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;
    private static final String SMS_SENT_ACTION = "SMS_SENT_ACTION";
    private static final String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";

    private EditText phoneInput, messageInput;
    private Button sendButton, getReportButton;
    private TextView statusText;
    private ImageView homeIcon;
    private HistoryDatabaseHelper dbHelper;

    private BroadcastReceiver smsSentReceiver;
    private BroadcastReceiver smsDeliveredReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        phoneInput = findViewById(R.id.phone_input);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_sms_btn);
        getReportButton = findViewById(R.id.get_report_btn);
        statusText = findViewById(R.id.sms_status);
        homeIcon = findViewById(R.id.home_icon);

        dbHelper = new HistoryDatabaseHelper(this);

        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(MessageActivity.this, DashboardActivity.class));
            finish();
        });

        sendButton.setOnClickListener(v -> {
            if (checkSmsPermission()) {
                sendSms();
            } else {
                requestSmsPermission();
            }
        });

        getReportButton.setOnClickListener(v -> promptPatientName());

        // Register BroadcastReceiver for SMS Sent
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case RESULT_OK:
                        statusText.setText("SMS sent successfully!");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        statusText.setText("SMS sending failed: Generic failure.");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        statusText.setText("SMS sending failed: No service.");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        statusText.setText("SMS sending failed: Null PDU.");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        statusText.setText("SMS sending failed: Radio off.");
                        break;
                    default:
                        statusText.setText("SMS sending failed: Unknown error.");
                        break;
                }
            }
        };

        // Register BroadcastReceiver for SMS Delivered
        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case RESULT_OK:
                        Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        registerReceiver(smsSentReceiver, new IntentFilter(SMS_SENT_ACTION));
        registerReceiver(smsDeliveredReceiver, new IntentFilter(SMS_DELIVERED_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

    private boolean checkSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
                sendSms();
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void promptPatientName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Patient Name");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Fetch", (dialog, which) -> {
            String patientName = input.getText().toString().trim();
            fetchReportInfo(patientName);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void fetchReportInfo(String patientName) {
        List<HistoryRecord> records = dbHelper.getAllHistory();
        for (HistoryRecord record : records) {
            if (record.getPatientName().equalsIgnoreCase(patientName)) {
                String report = "Patient: " + record.getPatientName() +
                        "\nDate: " + record.getDate() +
                        "\nSample: " + record.getSampleId() +
                        "\nK⁺: " + record.getPotassium() + " mM" +
                        "\nNa⁺: " + record.getSodium() + " mM" +
                        "\npH: " + record.getPh() +
                        "\nResult: " + record.getResult();

                // Sanitize message text before setting
                report = report.replaceAll("\\s+\n", "\n").trim();

                messageInput.setText(report);
                statusText.setText("Report info loaded.");
                return;
            }
        }
        statusText.setText("No matching report found.");
    }

    private void sendSms() {
        String number = phoneInput.getText().toString().trim();
        String message = messageInput.getText().toString().trim();

        if (TextUtils.isEmpty(number) || TextUtils.isEmpty(message)) {
            statusText.setText("Enter both phone number and message.");
            return;
        }

        // Sanitize message - remove extra spaces and control characters except basic line breaks
        message = message.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "").trim();

        // Optional: Replace Unicode superscript plus sign with normal plus
        message = message.replace("⁺", "+");

        // Log final message for debug (you can replace this with Log.d)
        Toast.makeText(this, "Sending message:\n" + message, Toast.LENGTH_LONG).show();

        try {
            SmsManager smsManager = SmsManager.getDefault();

            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(SMS_SENT_ACTION),
                    getPendingIntentFlags());

            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(SMS_DELIVERED_ACTION),
                    getPendingIntentFlags());

            if (message.length() > 160) {
                java.util.ArrayList<String> parts = smsManager.divideMessage(message);
                java.util.ArrayList<PendingIntent> sentIntents = new java.util.ArrayList<>();
                java.util.ArrayList<PendingIntent> deliveredIntents = new java.util.ArrayList<>();

                for (int i = 0; i < parts.size(); i++) {
                    sentIntents.add(sentPI);
                    deliveredIntents.add(deliveredPI);
                }

                smsManager.sendMultipartTextMessage(number, null, parts, sentIntents, deliveredIntents);
            } else {
                smsManager.sendTextMessage(number, null, message, sentPI, deliveredPI);
            }

            statusText.setText("Sending SMS...");
        } catch (Exception e) {
            statusText.setText("Failed to send SMS: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private int getPendingIntentFlags() {
        // For API 31+, use FLAG_MUTABLE or FLAG_IMMUTABLE as required
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            return PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE;
        } else {
            return PendingIntent.FLAG_UPDATE_CURRENT;
        }
    }
}
