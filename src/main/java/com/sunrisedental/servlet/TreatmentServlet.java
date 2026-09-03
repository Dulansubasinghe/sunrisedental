package com.sunrisedental.servlet;

import com.google.gson.Gson;
import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.model.Treatment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/treatment")
public class TreatmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TreatmentDAO treatmentDAO;
    private Gson gson;

    @Override
    public void init() {
        treatmentDAO = new TreatmentDAO();
        gson = new Gson();
    }

    // 1.Get All Treatments
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String treatmentCode = request.getParameter("treatmentCode");
        String idParam = request.getParameter("id");

        if (treatmentCode != null && !treatmentCode.trim().isEmpty()) {
            Treatment treatment = treatmentDAO.getTreatmentByCode(treatmentCode);
            out.print(gson.toJson(treatment));

        } else if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                Treatment treatment = treatmentDAO.getTreatmentById(id);
                out.print(gson.toJson(treatment));
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Invalid Treatment ID format.\"}");
            }

        } else {
            List<Treatment> list = treatmentDAO.getAllTreatments();
            out.print(gson.toJson(list));
        }
        out.flush();
    }

    // 2. Add New Treatment
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Treatment treatment = gson.fromJson(reader, Treatment.class);

            boolean isSuccess = treatmentDAO.addTreatment(treatment);

            if (isSuccess) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"status\":\"success\", \"message\":\"Treatment added successfully!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Failed to add treatment.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    // 3. Update / delete Existing Treatment
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Treatment treatment = gson.fromJson(reader, Treatment.class);

            boolean isSuccess = treatmentDAO.updateTreatment(treatment);

            if (isSuccess) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"status\":\"success\", \"message\":\"Treatment updated successfully!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Failed to update treatment.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
}