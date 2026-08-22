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

    // 1. Create New Bill
    public boolean addBill(Bill bill) {
        String sql = "INSERT INTO bills (bill_id, appointment_number, patient_name, consultation_fee, treatment_cost, total_amount, payment_status, bill_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bill.getBillId());
            stmt.setString(2, bill.getAppointmentNumber());
            stmt.setString(3, bill.getPatientName());
            stmt.setDouble(4, bill.getConsultationFee());
            stmt.setDouble(5, bill.getTreatmentCost());
            stmt.setDouble(6, bill.getTotalAmount());
            stmt.setString(7, bill.getPaymentStatus());
            stmt.setString(8, bill.getBillDate());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Search Bill
    public Bill getBillById(String billId) {
        String sql = "SELECT * FROM bills WHERE bill_id = ?";
        Bill bill = null;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, billId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                bill = new Bill();
                bill.setBillId(rs.getString("bill_id"));
                bill.setAppointmentNumber(rs.getString("appointment_number"));
                bill.setPatientName(rs.getString("patient_name"));
                bill.setConsultationFee(rs.getDouble("consultation_fee"));
                bill.setTreatmentCost(rs.getDouble("treatment_cost"));
                bill.setTotalAmount(rs.getDouble("total_amount"));
                bill.setPaymentStatus(rs.getString("payment_status"));
                bill.setBillDate(rs.getString("bill_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bill;
    }

    // 3. Get All Bills
    public List<Bill> getAllBills() {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY bill_date DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Bill b = new Bill();
                b.setBillId(rs.getString("bill_id"));
                b.setAppointmentNumber(rs.getString("appointment_number"));
                b.setPatientName(rs.getString("patient_name"));
                b.setConsultationFee(rs.getDouble("consultation_fee"));
                b.setTreatmentCost(rs.getDouble("treatment_cost"));
                b.setTotalAmount(rs.getDouble("total_amount"));
                b.setPaymentStatus(rs.getString("payment_status"));
                b.setBillDate(rs.getString("bill_date"));
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}