package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnection;
import com.sunrisedental.model.Treatment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDAO {

    // Helper Method
    private Treatment mapResultSetToTreatment(ResultSet rs) throws SQLException {
        Treatment treatment = new Treatment();
        treatment.setId(rs.getInt("id"));
        treatment.setTreatmentCode(rs.getString("treatment_code"));
        treatment.setTreatmentName(rs.getString("treatment_name"));
        treatment.setStandardFee(rs.getDouble("standard_fee"));
        treatment.setDurationMins(rs.getInt("duration_mins"));
        treatment.setStatus(rs.getString("status"));
        return treatment;
    }

    // 1. Add New Treatment
    public boolean addTreatment(Treatment treatment) {
        String sql = "INSERT INTO treatments (treatment_code, treatment_name, standard_fee, duration_mins, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, treatment.getTreatmentCode());
            stmt.setString(2, treatment.getTreatmentName());
            stmt.setDouble(3, treatment.getStandardFee());
            stmt.setInt(4, treatment.getDurationMins());
            stmt.setString(5, treatment.getStatus() != null ? treatment.getStatus() : "Active");

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Get All Treatments
    public List<Treatment> getAllTreatments() {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT * FROM treatments ORDER BY id DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToTreatment(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // 3. Get Treatment by ID
    public Treatment getTreatmentById(int id) {
        String sql = "SELECT * FROM treatments WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTreatment(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 4. Get Treatment by Code
    public Treatment getTreatmentByCode(String code) {
        String sql = "SELECT * FROM treatments WHERE treatment_code = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTreatment(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 5. Update Treatment Details & Status
    public boolean updateTreatment(Treatment treatment) {
        String sql = "UPDATE treatments SET treatment_name = ?, standard_fee = ?, duration_mins = ?, status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, treatment.getTreatmentName());
            stmt.setDouble(2, treatment.getStandardFee());
            stmt.setInt(3, treatment.getDurationMins());
            stmt.setString(4, treatment.getStatus());
            stmt.setInt(5, treatment.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}