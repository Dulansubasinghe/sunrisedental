package com.sunrisedental.model;

public class Bill {
    private String billId;
    private String appointmentNumber;
    private String patientName;
    private double consultationFee;
    private double treatmentCost;
    private double totalAmount;
    private String paymentStatus; // e.g., "PAID", "PENDING"
    private String billDate;

    public Bill() {}

    public Bill(String billId, String appointmentNumber, String patientName, double consultationFee, double treatmentCost, double totalAmount, String paymentStatus, String billDate) {
        this.billId = billId;
        this.appointmentNumber = appointmentNumber;
        this.patientName = patientName;
        this.consultationFee = consultationFee;
        this.treatmentCost = treatmentCost;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.billDate = billDate;
    }

    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public String getAppointmentNumber() { return appointmentNumber; }
    public void setAppointmentNumber(String appointmentNumber) { this.appointmentNumber = appointmentNumber; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public double getTreatmentCost() { return treatmentCost; }
    public void setTreatmentCost(double treatmentCost) { this.treatmentCost = treatmentCost; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getBillDate() { return billDate; }
    public void setBillDate(String billDate) { this.billDate = billDate; }
}