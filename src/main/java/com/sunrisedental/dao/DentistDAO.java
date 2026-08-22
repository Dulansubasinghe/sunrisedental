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

    public boolean addDentist(Dentist dentist) {
        String sql = "INSERT INTO dentists (dentist_id, name, specialization, contact_number, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dentist.getDentistId());
            stmt.setString(2, dentist.getName());
            stmt.setString(3, dentist.getSpecialization());
            stmt.setString(4, dentist.getContactNumber());
            stmt.setString(5, dentist.getEmail());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Dentist> getAllDentists() {
        List<Dentist> list = new ArrayList<>();
        String sql = "SELECT * FROM dentists";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Dentist d = new Dentist();
                d.setDentistId(rs.getString("dentist_id"));
                d.setName(rs.getString("name"));
                d.setSpecialization(rs.getString("specialization"));
                d.setContactNumber(rs.getString("contact_number"));
                d.setEmail(rs.getString("email"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}