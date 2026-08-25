package com.sunrisedental.model;

import java.io.Serializable;

public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    // Database Table Primary & Foreign Key Fields
    private int appointmentId;
    private String appointmentCode;
    private String appointmentDate;
    private int patientId;
    private int dentistId;
    private int treatmentId;
    private String status;

    // Optional Display Fields
    private String patientName;
    private String dentistName;
    private String treatmentName;

    // Default Constructor
    public Appointment() {
    }

    // Constructor for DB Insert
    public Appointment(String appointmentCode, String appointmentDate, int patientId, int dentistId, int treatmentId, String status) {
        this.appointmentCode = appointmentCode;
        this.appointmentDate = appointmentDate;
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.treatmentId = treatmentId;
        this.status = status;
    }

    // Full Constructor
    public Appointment(int appointmentId, String appointmentCode, String appointmentDate, int patientId, int dentistId, int treatmentId, String status) {
        this.appointmentId = appointmentId;
        this.appointmentCode = appointmentCode;
        this.appointmentDate = appointmentDate;
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.treatmentId = treatmentId;
        this.status = status;
    }

    // Getters and Setters
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentCode() {
        return appointmentCode;
    }

    public void setAppointmentCode(String appointmentCode) {
        this.appointmentCode = appointmentCode;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDentistId() {
        return dentistId;
    }

    public void setDentistId(int dentistId) {
        this.dentistId = dentistId;
    }

    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }
}