package com.sunrisedental.model;

public class Dentist {
    private String dentistId;
    private String name;
    private String specialization;
    private String contactNumber;
    private String email;

    public Dentist() {}

    public Dentist(String dentistId, String name, String specialization, String contactNumber, String email) {
        this.dentistId = dentistId;
        this.name = name;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public String getDentistId() { return dentistId; }
    public void setDentistId(String dentistId) { this.dentistId = dentistId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}