package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnection;
import com.sunrisedental.model.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    // 1. Register Patient
    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (patient_id, name, age, gender, contact_number, address, medical_history) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getPatientId());
            stmt.setString(2, patient.getName());
            stmt.setInt(3, patient.getAge());
            stmt.setString(4, patient.getGender());
            stmt.setString(5, patient.getContactNumber());
            stmt.setString(6, patient.getAddress());
            stmt.setString(7, patient.getMedicalHistory());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Search Patient
    public Patient getPatientById(String patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        Patient patient = null;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                patient = new Patient();
                patient.setPatientId(rs.getString("patient_id"));
                patient.setName(rs.getString("name"));
                patient.setAge(rs.getInt("age"));
                patient.setGender(rs.getString("gender"));
                patient.setContactNumber(rs.getString("contact_number"));
                patient.setAddress(rs.getString("address"));
                patient.setMedicalHistory(rs.getString("medical_history"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patient;
    }

    // 3. Get All Patients
    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Patient p = new Patient();
                p.setPatientId(rs.getString("patient_id"));
                p.setName(rs.getString("name"));
                p.setAge(rs.getInt("age"));
                p.setGender(rs.getString("gender"));
                p.setContactNumber(rs.getString("contact_number"));
                p.setAddress(rs.getString("address"));
                p.setMedicalHistory(rs.getString("medical_history"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}