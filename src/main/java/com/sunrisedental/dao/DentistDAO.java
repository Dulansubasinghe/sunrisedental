package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnection;
import com.sunrisedental.model.Dentist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DentistDAO {

    // Register New Dentist
    public boolean addDentist(Dentist dentist) {
        String sql = "INSERT INTO dentists (dentist_code, name, specialization, consultation_fee, contact_number, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dentist.getDentistCode());
            stmt.setString(2, dentist.getName());
            stmt.setString(3, dentist.getSpecialization());
            stmt.setDouble(4, dentist.getConsultationFee());
            stmt.setString(5, dentist.getContactNumber());
            stmt.setString(6, dentist.getStatus() != null ? dentist.getStatus() : "Active");

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Search Dentist by dentist_id
    public Dentist getDentistById(int dentistId) {
        String sql = "SELECT * FROM dentists WHERE dentist_id = ?";
        Dentist dentist = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dentistId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                dentist = mapResultSetToDentist(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dentist;
    }

    // Search Dentist by Code
    public Dentist getDentistByCode(String dentistCode) {
        String sql = "SELECT * FROM dentists WHERE dentist_code = ?";
        Dentist dentist = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dentistCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                dentist = mapResultSetToDentist(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dentist;
    }

    // Get All Dentists
    public List<Dentist> getAllDentists() {
        List<Dentist> list = new ArrayList<>();
        String sql = "SELECT * FROM dentists ORDER BY name ASC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToDentist(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Update Dentist Status
    public boolean updateStatus(int dentistId, String status) {
        String sql = "UPDATE dentists SET status = ? WHERE dentist_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, dentistId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Map ResultSet row to Dentist Model
    private Dentist mapResultSetToDentist(ResultSet rs) throws SQLException {
        Dentist dentist = new Dentist();
        dentist.setDentistId(rs.getInt("dentist_id"));
        dentist.setDentistCode(rs.getString("dentist_code"));
        dentist.setName(rs.getString("name"));
        dentist.setSpecialization(rs.getString("specialization"));
        dentist.setConsultationFee(rs.getDouble("consultation_fee"));
        dentist.setContactNumber(rs.getString("contact_number"));
        dentist.setStatus(rs.getString("status"));
        return dentist;
    }
}