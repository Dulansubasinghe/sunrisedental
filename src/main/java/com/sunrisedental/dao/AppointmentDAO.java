package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnection;
import com.sunrisedental.model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    // Register New Appointment
    public boolean addAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (appointment_code, appointment_date, patient_id, dentist_id, treatment_id, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointment.getAppointmentCode());
            stmt.setString(2, appointment.getAppointmentDate());
            stmt.setInt(3, appointment.getPatientId());
            stmt.setInt(4, appointment.getDentistId());
            stmt.setInt(5, appointment.getTreatmentId());
            stmt.setString(6, appointment.getStatus() != null ? appointment.getStatus() : "Pending");

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get Appointment Details by Appointment Code
    public Appointment getAppointmentByCode(String appointmentCode) {
        String sql = "SELECT a.*, p.name AS patient_name, d.name AS dentist_name, t.treatment_name " +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.patient_id " +
                "LEFT JOIN dentists d ON a.dentist_id = d.dentist_id " +
                "LEFT JOIN treatments t ON a.treatment_id = t.id " +
                "WHERE a.appointment_code = ?";

        Appointment appointment = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointmentCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                appointment = mapResultSetToAppointment(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointment;
    }

    // Get All Appointments with Joined Names
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p.name AS patient_name, d.name AS dentist_name, t.treatment_name " +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.patient_id " +
                "LEFT JOIN dentists d ON a.dentist_id = d.dentist_id " +
                "LEFT JOIN treatments t ON a.treatment_id = t.id " +
                "ORDER BY a.appointment_date DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToAppointment(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Update Appointment Status
    public boolean updateStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Convert ResultSet row into Appointment Model Object
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setAppointmentCode(rs.getString("appointment_code"));
        appointment.setAppointmentDate(rs.getString("appointment_date"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setDentistId(rs.getInt("dentist_id"));
        appointment.setTreatmentId(rs.getInt("treatment_id"));
        appointment.setStatus(rs.getString("status"));

        // Optional Joined Display Fields
        try {
            appointment.setPatientName(rs.getString("patient_name"));
            appointment.setDentistName(rs.getString("dentist_name"));
            appointment.setTreatmentName(rs.getString("treatment_name"));
        } catch (SQLException ignored) {
            // Skip unjoined query to avoid null errors
        }

        return appointment;
    }
}