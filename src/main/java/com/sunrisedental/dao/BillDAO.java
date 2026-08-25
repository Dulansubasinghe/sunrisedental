package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnection;
import com.sunrisedental.model.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    // Create New Bill
    public boolean addBill(Bill bill) {
        String sql = "INSERT INTO bills (bill_number, appointment_id, consultation_fee, treatment_cost, total_amount) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bill.getBillNumber());
            stmt.setInt(2, bill.getAppointmentId());
            stmt.setDouble(3, bill.getConsultationFee());
            stmt.setDouble(4, bill.getTreatmentCost());
            stmt.setDouble(5, bill.getTotalAmount());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Search Bill by bill_id
    public Bill getBillById(int billId) {
        String sql = "SELECT b.*, a.appointment_code, p.name AS patient_name " +
                "FROM bills b " +
                "LEFT JOIN appointments a ON b.appointment_id = a.appointment_id " +
                "LEFT JOIN patients p ON a.patient_id = p.patient_id " +
                "WHERE b.bill_id = ?";

        Bill bill = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, billId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                bill = mapResultSetToBill(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bill;
    }

    // Search Bill by Bill Number
    public Bill getBillByNumber(String billNumber) {
        String sql = "SELECT b.*, a.appointment_code, p.name AS patient_name " +
                "FROM bills b " +
                "LEFT JOIN appointments a ON b.appointment_id = a.appointment_id " +
                "LEFT JOIN patients p ON a.patient_id = p.patient_id " +
                "WHERE b.bill_number = ?";

        Bill bill = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, billNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                bill = mapResultSetToBill(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bill;
    }

    // Get All Bills with Joined Patient Name and Appointment Code
    public List<Bill> getAllBills() {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT b.*, a.appointment_code, p.name AS patient_name " +
                "FROM bills b " +
                "LEFT JOIN appointments a ON b.appointment_id = a.appointment_id " +
                "LEFT JOIN patients p ON a.patient_id = p.patient_id " +
                "ORDER BY b.bill_date DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToBill(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Map ResultSet row to Bill Object
    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setBillNumber(rs.getString("bill_number"));
        bill.setAppointmentId(rs.getInt("appointment_id"));
        bill.setConsultationFee(rs.getDouble("consultation_fee"));
        bill.setTreatmentCost(rs.getDouble("treatment_cost"));
        bill.setTotalAmount(rs.getDouble("total_amount"));
        bill.setBillDate(rs.getString("bill_date"));

        // Optional Joined Display Fields
        try {
            bill.setAppointmentCode(rs.getString("appointment_code"));
            bill.setPatientName(rs.getString("patient_name"));
        } catch (SQLException ignored) {
            // Skip execution if query has no join
        }

        return bill;
    }
}