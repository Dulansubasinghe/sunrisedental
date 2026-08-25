package com.sunrisedental.servlet;

import com.google.gson.Gson;
import com.sunrisedental.dao.UserDAO;
import com.sunrisedental.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private Gson gson;

    @Override
    public void init() {
        userDAO = new UserDAO();
        gson = new Gson();
    }

    // Get Single User by ID
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String userIdParam = request.getParameter("userId");
        if (userIdParam == null || userIdParam.trim().isEmpty()) {
            userIdParam = request.getParameter("id"); // Parameter Name Compatibility
        }

        if (userIdParam != null && !userIdParam.trim().isEmpty()) {
            try {
                int userId = Integer.parseInt(userIdParam);
                User user = userDAO.getUserById(userId);

                if (user != null) {
                    user.setPassword(null);
                }
                out.print(gson.toJson(user));

            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Invalid User ID format.\"}");
            }
        } else {
            List<User> list = userDAO.getAllUsers();
            // All Users Passwords Hide
            for (User u : list) {
                u.setPassword(null);
            }
            out.print(gson.toJson(list));
        }
        out.flush();
    }

    // Register New User
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            User newUser = gson.fromJson(reader, User.class);

            boolean isSuccess = userDAO.addUser(newUser);

            if (isSuccess) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"status\":\"success\", \"message\":\"User account created successfully!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Failed to create user account.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    // Delete User Account
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            idParam = request.getParameter("userId");
        }

        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int userId = Integer.parseInt(idParam);
                boolean isSuccess = userDAO.deleteUser(userId);

                if (isSuccess) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print("{\"status\":\"success\", \"message\":\"User deleted successfully!\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"status\":\"error\", \"message\":\"Failed to delete user.\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Invalid User ID format.\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\", \"message\":\"User ID is required.\"}");
        }
        out.flush();
    }
}