package com.sunrisedental.servlet;

import com.google.gson.Gson;
import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.model.Dentist;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/dentist")
public class DentistServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DentistDAO dentistDAO;
    private Gson gson;

    @Override
    public void init() {
        dentistDAO = new DentistDAO();
        gson = new Gson();
    }

    // 1.Search Dentist by Code Get Active List, or Get All
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        String dentistCode = request.getParameter("dentistCode");
        String dentistIdParam = request.getParameter("dentistId");

        // Get all active dentists
        if ("activeList".equalsIgnoreCase(action)) {
            List<Dentist> list = dentistDAO.getActiveDentists();
            out.print(gson.toJson(list));

        } else if (dentistCode != null && !dentistCode.trim().isEmpty()) {
            Dentist dentist = dentistDAO.getDentistByCode(dentistCode);
            out.print(gson.toJson(dentist));

        } else if (dentistIdParam != null && !dentistIdParam.trim().isEmpty()) {
            try {
                int dentistId = Integer.parseInt(dentistIdParam);
                Dentist dentist = dentistDAO.getDentistById(dentistId);
                out.print(gson.toJson(dentist));
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Invalid Dentist ID format.\"}");
            }

        } else {
            List<Dentist> list = dentistDAO.getAllDentists();
            out.print(gson.toJson(list));
        }
        out.flush();
    }

    // 2. Register New Dentist
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Dentist dentist = gson.fromJson(reader, Dentist.class);

            boolean isSuccess = dentistDAO.addDentist(dentist);

            if (isSuccess) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"status\":\"success\", \"message\":\"Dentist registered successfully!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Failed to register dentist.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    // 3. Update Dentist Details & Status
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Dentist dentist = gson.fromJson(reader, Dentist.class);

            if (dentist != null && (dentist.getDentistId() > 0 || (dentist.getDentistCode() != null && !dentist.getDentistCode().trim().isEmpty()))) {
                boolean isSuccess = dentistDAO.updateDentist(dentist);

                if (isSuccess) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print("{\"status\":\"success\", \"message\":\"Dentist updated successfully!\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"status\":\"error\", \"message\":\"Failed to update dentist.\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Invalid dentist data provided.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
}