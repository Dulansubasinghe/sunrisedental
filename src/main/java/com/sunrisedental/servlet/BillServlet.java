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
import java.util.Map;

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

    // get bill details by appointment code, bill ID,
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String appointmentCode = request.getParameter("appointmentCode");
        String billNumber = request.getParameter("billNumber");
        String billIdParam = request.getParameter("billId");

        if (appointmentCode != null && !appointmentCode.trim().isEmpty()) {
            // Get billing info by appointment code
            Map<String, Object> details = billDAO.getBillingDetailsByCode(appointmentCode.trim());
            if (details != null) {
                out.print(gson.toJson(details));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"status\":\"error\", \"message\":\"No appointment found for code: " + appointmentCode + "\"}");
            }

        } else if (billNumber != null && !billNumber.trim().isEmpty()) {
            // Search by Bill Number
            Bill bill = billDAO.getBillByNumber(billNumber.trim());
            out.print(gson.toJson(bill));

        } else if (billIdParam != null && !billIdParam.trim().isEmpty()) {
            // Search by Bill ID
            try {
                int billId = Integer.parseInt(billIdParam);
                Bill bill = billDAO.getBillById(billId);
                out.print(gson.toJson(bill));
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Invalid Bill ID format.\"}");
            }

        } else {
            // Get All Bills
            List<Bill> list = billDAO.getAllBills();
            out.print(gson.toJson(list));
        }
        out.flush();
    }

    // 2. Create New Bill
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Bill bill = gson.fromJson(reader, Bill.class);

            if (bill == null || bill.getAppointmentId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Invalid Appointment ID.\"}");
                out.flush();
                return;
            }

            // Auto-generate bill number if not provided
            if (bill.getBillNumber() == null || bill.getBillNumber().trim().isEmpty()) {
                bill.setBillNumber("BILL-" + (System.currentTimeMillis() % 100000));
            }

            // Total Amount reCalculate
            double total = bill.getConsultationFee() + bill.getTreatmentCost();
            bill.setTotalAmount(total);

            boolean isSuccess = billDAO.addBill(bill);

            if (isSuccess) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"status\":\"success\", \"message\":\"Bill saved successfully!\", \"billNumber\":\"" + bill.getBillNumber() + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Failed to save bill in database.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
}