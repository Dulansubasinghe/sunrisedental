package com.sunrisedental.model;

import java.io.Serializable;

public class Treatment implements Serializable {
    private static final long serialVersionUID = 1L;

    // Database Primary Key & Attributes
    private int id;
    private String treatmentCode;
    private String treatmentName;
    private double standardFee;
    private int durationMins;
    private String status;

    // Default Constructor
    public Treatment() {
    }

    // Constructor for DB Insert
    public Treatment(String treatmentCode, String treatmentName, double standardFee, int durationMins, String status) {
        this.treatmentCode = treatmentCode;
        this.treatmentName = treatmentName;
        this.standardFee = standardFee;
        this.durationMins = durationMins;
        this.status = status;
    }

    // Full Constructor
    public Treatment(int id, String treatmentCode, String treatmentName, double standardFee, int durationMins, String status) {
        this.id = id;
        this.treatmentCode = treatmentCode;
        this.treatmentName = treatmentName;
        this.standardFee = standardFee;
        this.durationMins = durationMins;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTreatmentCode() {
        return treatmentCode;
    }

    public void setTreatmentCode(String treatmentCode) {
        this.treatmentCode = treatmentCode;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public double getStandardFee() {
        return standardFee;
    }

    public void setStandardFee(double standardFee) {
        this.standardFee = standardFee;
    }

    public int getDurationMins() {
        return durationMins;
    }

    public void setDurationMins(int durationMins) {
        this.durationMins = durationMins;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}