package com.sunrisedental.model;

import java.io.Serializable;

public class Dentist implements Serializable {
    private static final long serialVersionUID = 1L;

    // Database Primary Key & Attributes
    private int dentistId;
    private String dentistCode;
    private String name;
    private String specialization;
    private double consultationFee;
    private String contactNumber;
    private String status;

    // Default Constructor
    public Dentist() {
    }

    // Constructor for DB Insert
    public Dentist(String dentistCode, String name, String specialization, double consultationFee, String contactNumber, String status) {
        this.dentistCode = dentistCode;
        this.name = name;
        this.specialization = specialization;
        this.consultationFee = consultationFee;
        this.contactNumber = contactNumber;
        this.status = status;
    }

    // Full Constructor
    public Dentist(int dentistId, String dentistCode, String name, String specialization, double consultationFee, String contactNumber, String status) {
        this.dentistId = dentistId;
        this.dentistCode = dentistCode;
        this.name = name;
        this.specialization = specialization;
        this.consultationFee = consultationFee;
        this.contactNumber = contactNumber;
        this.status = status;
    }

    // Getters and Setters
    public int getDentistId() {
        return dentistId;
    }

    public void setDentistId(int dentistId) {
        this.dentistId = dentistId;
    }

    public String getDentistCode() {
        return dentistCode;
    }

    public void setDentistCode(String dentistCode) {
        this.dentistCode = dentistCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}