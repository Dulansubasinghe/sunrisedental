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

    // 1. Register New Appointment
    public boolean addAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (appointment_number, patient_name, address, contact_number, " +
                "dentist_name, treatment_type, consultation_fee, treatment_cost, total_bill, " +
                "appointment_date, appointment_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Calculate Total Bill
        double totalBill = appointment.getConsultationFee() + appointment.getTreatmentCost();
        appointment.setTotalBill(totalBill);

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointment.getAppointmentNumber());
            stmt.setString(2, appointment.getPatientName());
            stmt.setString(3, appointment.getAddress());
            stmt.setString(4, appointment.getContactNumber());
            stmt.setString(5, appointment.getDentistName());
            stmt.setString(6, appointment.getTreatmentType());
            stmt.setDouble(7, appointment.getConsultationFee());
            stmt.setDouble(8, appointment.getTreatmentCost());
            stmt.setDouble(9, appointment.getTotalBill());
            stmt.setString(10, appointment.getAppointmentDate());
            stmt.setString(11, appointment.getAppointmentTime());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Search Appointment by Appointment Number
    public Appointment getAppointmentByNumber(String appointmentNumber) {
        String sql = "SELECT * FROM appointments WHERE appointment_number = ?";
        Appointment appointment = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointmentNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                appointment = new Appointment();
                appointment.setAppointmentNumber(rs.getString("appointment_number"));
                appointment.setPatientName(rs.getString("patient_name"));
                appointment.setAddress(rs.getString("address"));
                appointment.setContactNumber(rs.getString("contact_number"));
                appointment.setDentistName(rs.getString("dentist_name"));
                appointment.setTreatmentType(rs.getString("treatment_type"));
                appointment.setConsultationFee(rs.getDouble("consultation_fee"));
                appointment.setTreatmentCost(rs.getDouble("treatment_cost"));
                appointment.setTotalBill(rs.getDouble("total_bill"));
                appointment.setAppointmentDate(rs.getString("appointment_date"));
                appointment.setAppointmentTime(rs.getString("appointment_time"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointment;
    }

    // 3. Get All Appointments
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_date DESC, appointment_time DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentNumber(rs.getString("appointment_number"));
                appointment.setPatientName(rs.getString("patient_name"));
                appointment.setAddress(rs.getString("address"));
                appointment.setContactNumber(rs.getString("contact_number"));
                appointment.setDentistName(rs.getString("dentist_name"));
                appointment.setTreatmentType(rs.getString("treatment_type"));
                appointment.setConsultationFee(rs.getDouble("consultation_fee"));
                appointment.setTreatmentCost(rs.getDouble("treatment_cost"));
                appointment.setTotalBill(rs.getDouble("total_bill"));
                appointment.setAppointmentDate(rs.getString("appointment_date"));
                appointment.setAppointmentTime(rs.getString("appointment_time"));

                list.add(appointment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}