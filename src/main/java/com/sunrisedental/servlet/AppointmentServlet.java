package com.sunrisedental.servlet;

import com.google.gson.Gson;
import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.model.Appointment;
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

@WebServlet("/appointment")
public class AppointmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private Gson gson;

    @Override
    public void init() {
        appointmentDAO = new AppointmentDAO();
        patientDAO = new PatientDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");

        // Next Appointment Code
        if ("nextCode".equals(action)) {
            String nextCode = appointmentDAO.getNextAppointmentCode();
            out.print("{\"nextCode\":\"" + nextCode + "\"}");
            out.flush();
            return;
        }

        // Today's Appointment Count
        if ("todayCount".equals(action)) {
            int count = appointmentDAO.getTodayAppointmentCount();
            out.print("{\"count\":" + count + "}");
            out.flush();
            return;
        }

        String appointmentCode = request.getParameter("appointmentCode");
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Appointment appointment = gson.fromJson(reader, Appointment.class);

            // 1. Doctor Availability / Date + Time + Doctor Overlap Check
            boolean isAvailable = appointmentDAO.isDentistAvailable(
                    appointment.getDentistId(),
                    appointment.getAppointmentDate(),
                    appointment.getTreatmentId()
            );

            if (!isAvailable) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"The selected doctor already has an appointment during this time slot. Please choose another date or time.\"}");
                out.flush();
                return;
            }

            // 2. Dynamic Patient Matching
            int patientId = appointment.getPatientId();

            if (patientId <= 0) {
                String contact = appointment.getContactNumber();
                String inputName = appointment.getPatientName();

                Patient existingPatient = (contact != null && !contact.trim().isEmpty() && inputName != null && !inputName.trim().isEmpty())
                        ? patientDAO.getPatientByContactAndName(contact, inputName) : null;

                if (existingPatient != null) {
                    patientId = existingPatient.getPatientId();
                } else {
                    Patient newPatient = new Patient();
                    newPatient.setPatientCode("PAT-" + (System.currentTimeMillis() % 10000));
                    newPatient.setName(inputName != null && !inputName.trim().isEmpty() ? inputName.trim() : "Unknown");
                    newPatient.setContactNumber(contact != null ? contact.trim() : "");
                    newPatient.setAddress(appointment.getAddress() != null ? appointment.getAddress().trim() : "");

                    patientId = patientDAO.addPatientAndGetId(newPatient);
                }
            }

            if (patientId > 0) {
                appointment.setPatientId(patientId);

                if (appointment.getAppointmentCode() == null || appointment.getAppointmentCode().trim().isEmpty()) {
                    appointment.setAppointmentCode(appointmentDAO.getNextAppointmentCode());
                }

                boolean isSuccess = appointmentDAO.addAppointment(appointment);

                if (isSuccess) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print("{\"status\":\"success\", \"message\":\"Appointment saved successfully!\", \"appointmentCode\":\"" + appointment.getAppointmentCode() + "\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"status\":\"error\", \"message\":\"Failed to save appointment in database.\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Failed to register patient details.\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Appointment appointment = gson.fromJson(reader, Appointment.class);

            if (appointment != null && appointment.getAppointmentId() > 0) {
                boolean isSuccess = appointmentDAO.updateAppointmentDetails(
                        appointment.getAppointmentId(),
                        appointment.getStatus(),
                        appointment.getContactNumber()
                );

                if (isSuccess) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print("{\"status\":\"success\", \"message\":\"Appointment details updated successfully!\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"status\":\"error\", \"message\":\"Failed to update appointment in database.\"}");
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

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\", \"message\":\"Appointment ID is required\"}");
            out.flush();
            return;
        }

        try {
            int appointmentId = Integer.parseInt(idParam);
            boolean isDeleted = appointmentDAO.deleteAppointment(appointmentId);

            if (isDeleted) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"status\":\"success\", \"message\":\"Appointment deleted successfully!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Failed to delete record from database.\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\", \"message\":\"Invalid Appointment ID format.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
}