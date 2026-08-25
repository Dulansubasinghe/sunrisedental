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

    // 2. Get All Treatments (Dropdowns & Tables සඳහා)
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
        Treatment treatment = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                treatment = mapResultSetToTreatment(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return treatment;
    }

    // 4. Get Treatment by Treatment Code
    public Treatment getTreatmentByCode(String code) {
        String sql = "SELECT * FROM treatments WHERE treatment_code = ?";
        Treatment treatment = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                treatment = mapResultSetToTreatment(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return treatment;
    }

    // Helper Method: ResultSet -> Treatment Object Conversion
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
}