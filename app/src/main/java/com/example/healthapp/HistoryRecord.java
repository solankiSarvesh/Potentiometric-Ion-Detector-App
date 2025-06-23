package com.example.healthapp;

public class HistoryRecord {

    private int id;
    private String patientName;
    private String sampleId;
    private String date;
    private double potassium;
    private double sodium;
    private double ph;
    private String result;

    public HistoryRecord() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getSampleId() {
        return sampleId;
    }
    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public double getPotassium() {
        return potassium;
    }
    public void setPotassium(double potassium) {
        this.potassium = potassium;
    }

    public double getSodium() {
        return sodium;
    }
    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public double getPh() {
        return ph;
    }
    public void setPh(double ph) {
        this.ph = ph;
    }

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
}
