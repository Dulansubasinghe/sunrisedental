package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnection;
import com.sunrisedental.model.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillDAO {

    // Extract consultation and treatment fees dynamically
    public Map<String, Object> getBillingDetailsByCode(String appointmentCode) {
        // Safe SQL Query Structure
        String sql = "SELECT a.*, p.name AS patient_name, d.name AS dentist_name, " +
                "d.consultation_fee AS doc_consultation_fee, " +
                "t.treatment_name, t.* " +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.patient_id " +
                "LEFT JOIN dentists d ON a.dentist_id = d.dentist_id " +
                "LEFT JOIN treatments t ON a.treatment_id = t.id " +
                "WHERE TRIM(a.appointment_code) = TRIM(?)";

        Map<String, Object> details = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointmentCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                details = new HashMap<>();
                details.put("appointmentId", rs.getInt("appointment_id"));
                details.put("appointmentCode", rs.getString("appointment_code"));
                details.put("appointmentDate", rs.getString("appointment_date"));
                details.put("patientName", rs.getString("patient_name"));
                details.put("dentistName", rs.getString("dentist_name"));
                details.put("treatmentName", rs.getString("treatment_name"));

                // Safely parse consultation fee, defaulting to 0 if missing
                double conFee = 0.00;
                try {
                    conFee = rs.getDouble("consultation_fee");
                } catch (SQLException e) {
                    try {
                        conFee = rs.getDouble("doc_consultation_fee");
                    } catch (SQLException ignored) {}
                }
                details.put("consultationFee", conFee);

                // 🔴 treatments table එකේ standard_fee, price, cost, fee කුමක් වුවත් Safe එකේ Extract කරගැනීම
                double treatmentCost = 0.0;
                try {
                    treatmentCost = rs.getDouble("standard_fee");
                } catch (SQLException e1) {
                    try {
                        treatmentCost = rs.getDouble("price");
                    } catch (SQLException e2) {
                        try {
                            treatmentCost = rs.getDouble("cost");
                        } catch (SQLException e3) {
                            try {
                                treatmentCost = rs.getDouble("fee");
                            } catch (SQLException ignored) {}
                        }
                    }
                }
                details.put("treatmentCost", treatmentCost);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return details;
    }

    // 2. Create New Bill
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

    // 3. Search Bill by bill_id
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

    // 4. Search Bill by Bill Number
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

    // 5. Get All Bills
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

    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setBillNumber(rs.getString("bill_number"));
        bill.setAppointmentId(rs.getInt("appointment_id"));
        bill.setConsultationFee(rs.getDouble("consultation_fee"));
        bill.setTreatmentCost(rs.getDouble("treatment_cost"));
        bill.setTotalAmount(rs.getDouble("total_amount"));
        bill.setBillDate(rs.getString("bill_date"));

        try {
            bill.setAppointmentCode(rs.getString("appointment_code"));
            bill.setPatientName(rs.getString("patient_name"));
        } catch (SQLException ignored) {
        }

        return bill;
    }
}