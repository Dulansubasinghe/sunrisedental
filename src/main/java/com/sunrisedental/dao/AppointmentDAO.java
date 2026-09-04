package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnection;
import com.sunrisedental.model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    // Helper Method for Safe Date & Time Parsing
    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            String s = dateStr.trim().replace(" ", "T");
            if (s.contains(".")) {
                s = s.substring(0, s.indexOf("."));
            }
            if (s.length() == 16) {
                s += ":00";
            }
            return LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(dateStr.trim().replace("T", " "),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss]"));
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

    // Dynamic Appointment Code
    public String getNextAppointmentCode() {
        String sql = "SELECT appointment_code FROM appointments WHERE appointment_code LIKE 'APT-%' ORDER BY appointment_id DESC LIMIT 1";
        int nextId = 1001; // Default starting code

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String lastCode = rs.getString("appointment_code");
                if (lastCode != null && lastCode.startsWith("APT-")) {
                    try {
                        int num = Integer.parseInt(lastCode.replace("APT-", "").trim());
                        nextId = num + 1;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "APT-" + String.format("%04d", nextId);
    }

    // Dentist Time Overlap & Availability Check
    public boolean isDentistAvailable(int dentistId, String newStartStr, int treatmentId) {
        int newDurationMinutes = 30;

        String getDurationSql = "SELECT duration FROM treatments WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(getDurationSql)) {
            stmt.setInt(1, treatmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int dur = rs.getInt("duration");
                if (dur > 0) newDurationMinutes = dur;
            }
        } catch (SQLException e) {
            // default 30 mins
        }

        LocalDateTime newStart = parseDateTime(newStartStr);
        if (newStart == null) {
            // Block booking on invalid date to prevent double booking
            return false;
        }
        LocalDateTime newEnd = newStart.plusMinutes(newDurationMinutes);

        String sql = "SELECT a.appointment_date, COALESCE(t.duration, 30) AS duration " +
                "FROM appointments a " +
                "LEFT JOIN treatments t ON a.treatment_id = t.id " +
                "WHERE a.dentist_id = ? AND (a.status IS NULL OR a.status != 'Cancelled')";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dentistId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String existingStartStr = rs.getString("appointment_date");
                LocalDateTime existingStart = parseDateTime(existingStartStr);
                if (existingStart == null) continue;

                int existingDuration = rs.getInt("duration");
                if (existingDuration <= 0) existingDuration = 30;

                LocalDateTime existingEnd = existingStart.plusMinutes(existingDuration);

                // Overlap Check Logic: Exact same date/time or overlapping time range
                if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                    return false; // Dentist is busy
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true; // Doctor is free
    }

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

    // Get Appointment Details by Code
    public Appointment getAppointmentByCode(String appointmentCode) {
        String sql = "SELECT a.*, p.name AS patient_name, p.contact_number AS contact_number, p.address AS address, " +
                "d.name AS dentist_name, COALESCE(d.consultation_fee, 0) AS consultation_fee, " +
                "t.treatment_name, COALESCE(t.standard_fee, 0) AS standard_fee " +
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

    // Get All Appointments
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p.name AS patient_name, p.contact_number AS contact_number, p.address AS address, " +
                "d.name AS dentist_name, COALESCE(d.consultation_fee, 0) AS consultation_fee, " +
                "t.treatment_name, COALESCE(t.standard_fee, 0) AS standard_fee " +
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

    // Update Status and Contact Number
    public boolean updateAppointmentDetails(int appointmentId, String status, String contactNumber) {
        String updateStatusSql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        String updateContactSql = "UPDATE patients SET contact_number = ? WHERE patient_id = (SELECT patient_id FROM appointments WHERE appointment_id = ?)";

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtStatus = conn.prepareStatement(updateStatusSql);
                 PreparedStatement stmtContact = conn.prepareStatement(updateContactSql)) {

                stmtStatus.setString(1, status);
                stmtStatus.setInt(2, appointmentId);
                stmtStatus.executeUpdate();

                if (contactNumber != null && !contactNumber.trim().isEmpty()) {
                    stmtContact.setString(1, contactNumber.trim());
                    stmtContact.setInt(2, appointmentId);
                    stmtContact.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete Appointment
    public boolean deleteAppointment(int appointmentId) {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get Today's Appointments Count
    public int getTodayAppointmentCount() {
        String sql = "SELECT COUNT(*) FROM appointments WHERE DATE(appointment_date) = CURDATE()";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ResultSet to Model Object Mapper
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setAppointmentCode(rs.getString("appointment_code"));
        appointment.setAppointmentDate(rs.getString("appointment_date"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setDentistId(rs.getInt("dentist_id"));
        appointment.setTreatmentId(rs.getInt("treatment_id"));
        appointment.setStatus(rs.getString("status"));

        try {
            appointment.setPatientName(rs.getString("patient_name"));
            appointment.setDentistName(rs.getString("dentist_name"));
            appointment.setTreatmentName(rs.getString("treatment_name"));
            appointment.setContactNumber(rs.getString("contact_number"));
        } catch (SQLException ignored) {}

        try {
            appointment.setAddress(rs.getString("address"));
        } catch (SQLException ignored) {}

        try {
            double conFee = rs.getDouble("consultation_fee");
            double treatFee = rs.getDouble("standard_fee");
            appointment.setConsultationFee(conFee);
            appointment.setTreatmentFee(treatFee);
            appointment.setTotalFee(conFee + treatFee);
        } catch (SQLException ignored) {}

        return appointment;
    }
}