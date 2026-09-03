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

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dentistId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDentist(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Search Dentist by Code
    public Dentist getDentistByCode(String dentistCode) {
        String sql = "SELECT * FROM dentists WHERE dentist_code = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dentistCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDentist(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
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

    // Dynamic Update Dentist Details & Status
    public boolean updateDentist(Dentist dentist) {
        StringBuilder sql = new StringBuilder("UPDATE dentists SET ");
        List<Object> params = new ArrayList<>();

        if (dentist.getName() != null && !dentist.getName().trim().isEmpty()) {
            sql.append("name = ?, ");
            params.add(dentist.getName());
        }
        if (dentist.getSpecialization() != null && !dentist.getSpecialization().trim().isEmpty()) {
            sql.append("specialization = ?, ");
            params.add(dentist.getSpecialization());
        }
        if (dentist.getConsultationFee() > 0) {
            sql.append("consultation_fee = ?, ");
            params.add(dentist.getConsultationFee());
        }
        if (dentist.getContactNumber() != null && !dentist.getContactNumber().trim().isEmpty()) {
            sql.append("contact_number = ?, ");
            params.add(dentist.getContactNumber());
        }
        if (dentist.getStatus() != null && !dentist.getStatus().trim().isEmpty()) {
            sql.append("status = ?, ");
            params.add(dentist.getStatus());
        }

        if (params.isEmpty()) return false;

        sql.setLength(sql.length() - 2);

        if (dentist.getDentistId() > 0) {
            sql.append(" WHERE dentist_id = ?");
            params.add(dentist.getDentistId());
        } else if (dentist.getDentistCode() != null && !dentist.getDentistCode().trim().isEmpty()) {
            sql.append(" WHERE dentist_code = ?");
            params.add(dentist.getDentistCode());
        } else {
            return false;
        }

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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

    // Receptionist Dashboard Active Dentists List
    public List<Dentist> getActiveDentists() {
        List<Dentist> list = new ArrayList<>();
        String sql = "SELECT * FROM dentists WHERE status IS NULL OR status != 'Inactive' ORDER BY name ASC";

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
}