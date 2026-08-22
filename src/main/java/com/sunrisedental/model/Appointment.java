package com.sunrisedental.model;

import java.io.Serializable;

public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String appointmentNumber;
    private String patientName;
    private String address;
    private String contactNumber;
    private String dentistName;
    private String treatmentType;
    private double consultationFee;
    private double treatmentCost;
    private double totalBill;
    private String appointmentDate;
    private String appointmentTime;

    // Default Constructor
    public Appointment() {
    }

    // Constructor with parameters
    public Appointment(String appointmentNumber, String patientName, String address,
                       String contactNumber, String dentistName, String treatmentType,
                       double consultationFee, double treatmentCost, double totalBill,
                       String appointmentDate, String appointmentTime) {
        this.appointmentNumber = appointmentNumber;
        this.patientName = patientName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.dentistName = dentistName;
        this.treatmentType = treatmentType;
        this.consultationFee = consultationFee;
        this.treatmentCost = treatmentCost;
        this.totalBill = totalBill;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
    }

    // Getters and Setters
    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public void setTreatmentType(String treatmentType) {
        this.treatmentType = treatmentType;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public double getTreatmentCost() {
        return treatmentCost;
    }

    public void setTreatmentCost(double treatmentCost) {
        this.treatmentCost = treatmentCost;
    }

    public double getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(double totalBill) {
        this.totalBill = totalBill;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
}
