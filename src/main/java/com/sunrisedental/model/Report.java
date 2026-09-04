package com.sunrisedental.model;

public class Report {
    private String appointmentCode;
    private String appointmentDate;
    private String patientName;
    private String dentistName;
    private String specialization;
    private String treatmentName;
    private String status;
    private double billingFee;

    public Report() {}

    public Report(String appointmentCode, String appointmentDate, String patientName,
                  String dentistName, String specialization, String treatmentName,
                  String status, double billingFee) {
        this.appointmentCode = appointmentCode;
        this.appointmentDate = appointmentDate;
        this.patientName = patientName;
        this.dentistName = dentistName;
        this.specialization = specialization;
        this.treatmentName = treatmentName;
        this.status = status;
        this.billingFee = billingFee;
    }

    public String getAppointmentCode() { return appointmentCode; }
    public void setAppointmentCode(String appointmentCode) { this.appointmentCode = appointmentCode; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getBillingFee() { return billingFee; }
    public void setBillingFee(double billingFee) { this.billingFee = billingFee; }
}