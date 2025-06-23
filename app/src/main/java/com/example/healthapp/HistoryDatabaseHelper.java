package com.example.healthapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class HistoryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "health_history.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "history_table";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PATIENT_NAME = "patient_name";
    private static final String COLUMN_SAMPLE_ID = "sample_id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_POTASSIUM = "potassium";
    private static final String COLUMN_SODIUM = "sodium";
    private static final String COLUMN_PH = "ph";
    private static final String COLUMN_RESULT = "result";

    public HistoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PATIENT_NAME + " TEXT, " +
                COLUMN_SAMPLE_ID + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_POTASSIUM + " REAL, " +
                COLUMN_SODIUM + " REAL, " +
                COLUMN_PH + " REAL, " +
                COLUMN_RESULT + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addHistory(HistoryRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PATIENT_NAME, record.getPatientName());
        values.put(COLUMN_SAMPLE_ID, record.getSampleId());
        values.put(COLUMN_DATE, record.getDate());
        values.put(COLUMN_POTASSIUM, record.getPotassium());
        values.put(COLUMN_SODIUM, record.getSodium());
        values.put(COLUMN_PH, record.getPh());
        values.put(COLUMN_RESULT, record.getResult());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<HistoryRecord> getAllHistory() {
        List<HistoryRecord> historyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                HistoryRecord record = new HistoryRecord();
                record.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                record.setPatientName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_NAME)));
                record.setSampleId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAMPLE_ID)));
                record.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                record.setPotassium(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_POTASSIUM)));
                record.setSodium(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SODIUM)));
                record.setPh(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PH)));
                record.setResult(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESULT)));

                historyList.add(record);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return historyList;
    }

    public boolean isPatientNameExists(String patientName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID},
                COLUMN_PATIENT_NAME + " = ?",
                new String[]{patientName},
                null, null, null);

        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public boolean deleteRecordById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

}
