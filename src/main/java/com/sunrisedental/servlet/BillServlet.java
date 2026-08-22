package com.sunrisedental.servlet;

import com.google.gson.Gson;
import com.sunrisedental.dao.BillDAO;
import com.sunrisedental.model.Bill;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/bill")
public class BillServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BillDAO billDAO;
    private Gson gson;

    @Override
    public void init() {
        billDAO = new BillDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String billId = request.getParameter("billId");

        if (billId != null && !billId.trim().isEmpty()) {
            Bill bill = billDAO.getBillById(billId);
            out.print(gson.toJson(bill));
        } else {
            List<Bill> list = billDAO.getAllBills();
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
            Bill bill = gson.fromJson(reader, Bill.class);

            // Total Amount Calculation
            double total = bill.getConsultationFee() + bill.getTreatmentCost();
            bill.setTotalAmount(total);

            boolean isSuccess = billDAO.addBill(bill);

            if (isSuccess) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"status\":\"success\", \"message\":\"Bill generated successfully!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Failed to generate bill.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
}