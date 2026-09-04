package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnection;
import com.sunrisedental.model.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    // 1. Register New Patient & Return Generated ID
    public int addPatientAndGetId(Patient patient) {
        String sql = "INSERT INTO patients (patient_code, name, contact_number, address) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, patient.getPatientCode());
            stmt.setString(2, patient.getName());
            stmt.setString(3, patient.getContactNumber());
            stmt.setString(4, patient.getAddress());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Search Patient by Contact Number and Name
    public Patient getPatientByContactAndName(String contactNumber, String name) {
        String sql = "SELECT * FROM patients WHERE contact_number = ? AND LOWER(TRIM(name)) = LOWER(TRIM(?))";
        Patient patient = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contactNumber != null ? contactNumber.trim() : "");
            stmt.setString(2, name != null ? name.trim() : "");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    patient = mapResultSetToPatient(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patient;
    }

    // 3. Search Patient by Contact Number
    public Patient getPatientByContact(String contactNumber) {
        String sql = "SELECT * FROM patients WHERE contact_number = ?";
        Patient patient = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contactNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    patient = mapResultSetToPatient(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patient;
    }

    // Register New Patient
    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (patient_code, name, contact_number, address) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getPatientCode());
            stmt.setString(2, patient.getName());
            stmt.setString(3, patient.getContactNumber());
            stmt.setString(4, patient.getAddress());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Search Patient by patient_id
    public Patient getPatientById(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        Patient patient = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    patient = mapResultSetToPatient(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patient;
    }

    // Search Patient by Code
    public Patient getPatientByCode(String patientCode) {
        String sql = "SELECT * FROM patients WHERE patient_code = ?";
        Patient patient = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patientCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    patient = mapResultSetToPatient(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patient;
    }

    // Get All Patients
    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY name ASC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPatient(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Update Patient Details
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET patient_code = ?, name = ?, contact_number = ?, address = ? WHERE patient_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getPatientCode());
            stmt.setString(2, patient.getName());
            stmt.setString(3, patient.getContactNumber());
            stmt.setString(4, patient.getAddress());
            stmt.setInt(5, patient.getPatientId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Map ResultSet row to Patient Model
    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setPatientCode(rs.getString("patient_code"));
        patient.setName(rs.getString("name"));
        patient.setContactNumber(rs.getString("contact_number"));
        patient.setAddress(rs.getString("address"));
        return patient;
    }
}