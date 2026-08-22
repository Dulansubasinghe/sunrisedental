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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Dentist> list = dentistDAO.getAllDentists();
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(list));
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
}