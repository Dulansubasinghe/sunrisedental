package com.sunrisedental.dao;

import com.sunrisedental.config.DBConnection;
import com.sunrisedental.model.Report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {

    public Map<String, Object> getReportData(String startDate, String endDate, String doctor, String treatment) {
        Map<String, Object> responseData = new HashMap<>();
        List<Report> appointments = new ArrayList<>();
        List<String> dentists = new ArrayList<>();
        List<String> treatments = new ArrayList<>();

        // 1. Dentists List
        String sqlDentists = "SELECT name FROM dentists ORDER BY name ASC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlDentists);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dentists.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. Auto detect Treatments Table Column Names
        String treatmentNameCol = "treatment_name";
        String treatmentPkCol = "treatment_id";
        String treatmentCostCol = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM treatments LIMIT 1");
             ResultSet rs = stmt.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();
            boolean foundName = false;

            for (int i = 1; i <= md.getColumnCount(); i++) {
                String col = md.getColumnName(i);

                if ("treatment_name".equalsIgnoreCase(col)) {
                    treatmentNameCol = "treatment_name";
                    foundName = true;
                } else if ("name".equalsIgnoreCase(col) && !foundName) {
                    treatmentNameCol = "name";
                }

                if ("id".equalsIgnoreCase(col)) {
                    treatmentPkCol = "id";
                } else if ("treatment_id".equalsIgnoreCase(col)) {
                    treatmentPkCol = "treatment_id";
                }

                if ("cost".equalsIgnoreCase(col) || "standard_fee".equalsIgnoreCase(col) ||
                        "price".equalsIgnoreCase(col) || "fee".equalsIgnoreCase(col)) {
                    treatmentCostCol = col;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Fetch Treatments Dropdown List
        String sqlTreatments = "SELECT DISTINCT " + treatmentNameCol + " FROM treatments ORDER BY " + treatmentNameCol + " ASC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlTreatments);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                treatments.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 3. Auto detect Bills Table Amount Column
        String billAmountCol = null;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bills LIMIT 1");
             ResultSet rs = stmt.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                String col = md.getColumnName(i);
                if ("total_amount".equalsIgnoreCase(col)) {
                    billAmountCol = "total_amount";
                    break;
                } else if ("amount".equalsIgnoreCase(col) && billAmountCol == null) {
                    billAmountCol = "amount";
                }
            }
        } catch (SQLException e) {
            // Bills table missing or empty
        }

        // 4. Calculate Dynamic Billing Fee
        String billSelect = (billAmountCol != null) ? "b." + billAmountCol : "NULL";
        String tCostSelect = (treatmentCostCol != null) ? "COALESCE(t." + treatmentCostCol + ", 0)" : "0";

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.appointment_code, a.appointment_date, ");
        sql.append("COALESCE(p.name, 'Patient') AS patient_name, ");
        sql.append("COALESCE(d.name, 'Doctor') AS dentist_name, ");
        sql.append("COALESCE(d.specialization, 'Dental Specialist') AS specialization, ");
        sql.append("COALESCE(t.").append(treatmentNameCol).append(", 'General Treatment') AS treatment_name, ");
        sql.append("a.status, ");
        sql.append("COALESCE(").append(billSelect).append(", (COALESCE(d.consultation_fee, 0) + ").append(tCostSelect).append(")) AS billing_fee ");
        sql.append("FROM appointments a ");
        sql.append("LEFT JOIN patients p ON a.patient_id = p.patient_id ");
        sql.append("LEFT JOIN dentists d ON a.dentist_id = d.dentist_id ");
        sql.append("LEFT JOIN treatments t ON a.treatment_id = t.").append(treatmentPkCol).append(" ");
        if (billAmountCol != null) {
            sql.append("LEFT JOIN bills b ON a.appointment_id = b.appointment_id ");
        }
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        boolean isAllDoctor = (doctor == null || doctor.trim().isEmpty() || doctor.equalsIgnoreCase("ALL") || doctor.toLowerCase().contains("all"));
        boolean isAllTreatment = (treatment == null || treatment.trim().isEmpty() || treatment.equalsIgnoreCase("ALL") || treatment.toLowerCase().contains("all"));

        if (!isAllDoctor) {
            sql.append(" AND d.name = ? ");
            params.add(doctor.trim());
        }

        if (!isAllTreatment) {
            sql.append(" AND t.").append(treatmentNameCol).append(" = ? ");
            params.add(treatment.trim());
        }

        // Bulletproof Date
        if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {
            String sDate = startDate.trim().contains("T") ? startDate.trim().split("T")[0] : startDate.trim();
            String eDate = endDate.trim().contains("T") ? endDate.trim().split("T")[0] : endDate.trim();

            if (sDate.equals(eDate)) {
                sql.append(" AND DATE_FORMAT(a.appointment_date, '%Y-%m-%d') = ? ");
                params.add(sDate);
            } else {
                sql.append(" AND DATE_FORMAT(a.appointment_date, '%Y-%m-%d') BETWEEN ? AND ? ");
                params.add(sDate);
                params.add(eDate);
            }
        }

        sql.append(" ORDER BY a.appointment_date DESC");

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Report report = new Report(
                            rs.getString("appointment_code"),
                            rs.getString("appointment_date"),
                            rs.getString("patient_name"),
                            rs.getString("dentist_name"),
                            rs.getString("specialization"),
                            rs.getString("treatment_name"),
                            rs.getString("status"),
                            rs.getDouble("billing_fee")
                    );
                    appointments.add(report);
                }
            }
        } catch (SQLException e) {
            System.err.println("ReportDAO Error: " + e.getMessage());
            e.printStackTrace();
        }

        responseData.put("appointments", appointments);
        responseData.put("dentists", dentists);
        responseData.put("treatments", treatments);

        return responseData;
    }
}