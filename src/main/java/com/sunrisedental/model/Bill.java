package com.sunrisedental.model;

import java.io.Serializable;

public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;

    // Database Primary & Foreign Key Fields
    private int billId;
    private String billNumber;
    private int appointmentId;
    private double consultationFee;
    private double treatmentCost;
    private double totalAmount;
    private String billDate;

    // Optional Display Fields
    private String appointmentCode;
    private String patientName;

    // Default Constructor
    public Bill() {
    }

    // Constructor for DB Insert
    public Bill(String billNumber, int appointmentId, double consultationFee, double treatmentCost, double totalAmount) {
        this.billNumber = billNumber;
        this.appointmentId = appointmentId;
        this.consultationFee = consultationFee;
        this.treatmentCost = treatmentCost;
        this.totalAmount = totalAmount;
    }

    // Full Constructor
    public Bill(int billId, String billNumber, int appointmentId, double consultationFee, double treatmentCost, double totalAmount, String billDate) {
        this.billId = billId;
        this.billNumber = billNumber;
        this.appointmentId = appointmentId;
        this.consultationFee = consultationFee;
        this.treatmentCost = treatmentCost;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
    }

    // Getters and Setters
    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
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

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getAppointmentCode() {
        return appointmentCode;
    }

    public void setAppointmentCode(String appointmentCode) {
        this.appointmentCode = appointmentCode;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}