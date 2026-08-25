package com.sunrisedental.servlet;

import com.google.gson.Gson;
import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.model.Appointment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/appointment")
public class AppointmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AppointmentDAO appointmentDAO;
    private Gson gson;

    @Override
    public void init() {
        appointmentDAO = new AppointmentDAO();
        gson = new Gson();
    }

    // Get all Appointment by Code
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String appointmentCode = request.getParameter("appointmentCode");
        // Keep support for old appointmentNumber query
        if (appointmentCode == null || appointmentCode.trim().isEmpty()) {
            appointmentCode = request.getParameter("appointmentNumber");
        }

        if (appointmentCode != null && !appointmentCode.trim().isEmpty()) {
            Appointment appointment = appointmentDAO.getAppointmentByCode(appointmentCode);
            out.print(gson.toJson(appointment));
        } else {
            List<Appointment> list = appointmentDAO.getAllAppointments();
            out.print(gson.toJson(list));
        }
        out.flush();
    }

    // Save New Appointment
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Appointment appointment = gson.fromJson(reader, Appointment.class);

            boolean isSuccess = appointmentDAO.addAppointment(appointment);

            if (isSuccess) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"status\":\"success\", \"message\":\"Appointment saved successfully!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Failed to save appointment.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    // Update Appointment Status
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Appointment appointment = gson.fromJson(reader, Appointment.class);

            if (appointment != null && appointment.getAppointmentId() > 0 && appointment.getStatus() != null) {
                boolean isSuccess = appointmentDAO.updateStatus(appointment.getAppointmentId(), appointment.getStatus());

                if (isSuccess) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print("{\"status\":\"success\", \"message\":\"Appointment status updated successfully!\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"status\":\"error\", \"message\":\"Failed to update appointment status.\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Invalid data provided.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
}