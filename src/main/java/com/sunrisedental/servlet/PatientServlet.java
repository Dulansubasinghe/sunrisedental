package com.sunrisedental.servlet;

import com.google.gson.Gson;
import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.model.Patient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/patient")
public class PatientServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PatientDAO patientDAO;
    private Gson gson;

    @Override
    public void init() {
        patientDAO = new PatientDAO();
        gson = new Gson();
    }

    // Search Patient by Code
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String patientCode = request.getParameter("patientCode");
        String patientIdParam = request.getParameter("patientId");

        if (patientCode != null && !patientCode.trim().isEmpty()) {
            // Search by Patient Code
            Patient patient = patientDAO.getPatientByCode(patientCode);
            out.print(gson.toJson(patient));

        } else if (patientIdParam != null && !patientIdParam.trim().isEmpty()) {
            // Search by Primary Key ID
            try {
                int patientId = Integer.parseInt(patientIdParam);
                Patient patient = patientDAO.getPatientById(patientId);
                out.print(gson.toJson(patient));
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Invalid Patient ID format.\"}");
            }

        } else {
            // Return all patients if no param is passed
            List<Patient> list = patientDAO.getAllPatients();
            out.print(gson.toJson(list));
        }
        out.flush();
    }

    // Register New Patient
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Patient patient = gson.fromJson(reader, Patient.class);

            boolean isSuccess = patientDAO.addPatient(patient);

            if (isSuccess) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"status\":\"success\", \"message\":\"Patient registered successfully!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Failed to register patient.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    // Update Existing Patient Details
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Patient patient = gson.fromJson(reader, Patient.class);

            if (patient != null && patient.getPatientId() > 0) {
                boolean isSuccess = patientDAO.updatePatient(patient);

                if (isSuccess) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print("{\"status\":\"success\", \"message\":\"Patient details updated successfully!\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"status\":\"error\", \"message\":\"Failed to update patient details.\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Invalid Patient ID provided.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
}