package com.sunrisedental.servlet;

import com.google.gson.Gson;
import com.sunrisedental.dao.ReportDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/ReportServlet")
public class ReportServlet extends HttpServlet {

    private ReportDAO reportDAO;
    private Gson gson;

    @Override
    public void init() {
        reportDAO = new ReportDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String doctor = request.getParameter("doctor");
        String treatment = request.getParameter("treatment");

        Map<String, Object> reportData = reportDAO.getReportData(startDate, endDate, doctor, treatment);

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(reportData));
        out.flush();
    }
}