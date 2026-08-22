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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String patientId = request.getParameter("patientId");

        if (patientId != null && !patientId.trim().isEmpty()) {
            Patient patient = patientDAO.getPatientById(patientId);
            out.print(gson.toJson(patient));
        } else {
            List<Patient> list = patientDAO.getAllPatients();
            out.print(gson.toJson(list));
        }
        out.flush();
    }

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
}