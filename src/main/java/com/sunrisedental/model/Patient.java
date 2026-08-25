package com.sunrisedental.model;

import java.io.Serializable;

public class Patient implements Serializable {
    private static final long serialVersionUID = 1L;

    // DB Table Columns
    private int patientId;
    private String patientCode;
    private String name;
    private String contactNumber;
    private String address;

    // Default Constructor
    public Patient() {
    }

    // Constructor for DB Insert
    public Patient(String patientCode, String name, String contactNumber, String address) {
        this.patientCode = patientCode;
        this.name = name;
        this.contactNumber = contactNumber;
        this.address = address;
    }

    // Full Constructor
    public Patient(int patientId, String patientCode, String name, String contactNumber, String address) {
        this.patientId = patientId;
        this.patientCode = patientCode;
        this.name = name;
        this.contactNumber = contactNumber;
        this.address = address;
    }

    // Getters and Setters
    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}