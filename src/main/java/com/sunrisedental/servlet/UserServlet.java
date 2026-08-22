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

    private UserDAO userDAO = new UserDAO();
    private Gson gson = new Gson();

    // Load receptionist data for admin
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        List<User> userList = userDAO.getAllUsers();
        String jsonResponse = gson.toJson(userList);

        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();
    }

    // Add receptionist to system
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        BufferedReader reader = req.getReader();
        User newUser = gson.fromJson(reader, User.class);

        boolean isSuccess = userDAO.addUser(newUser);

        PrintWriter out = resp.getWriter();
        if (isSuccess) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"message\": \"User created successfully\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Failed to create user\"}");
        }
        out.flush();
    }

    // Delete user account
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String idParam = req.getParameter("id");
        PrintWriter out = resp.getWriter();

        if (idParam != null) {
            int userId = Integer.parseInt(idParam);
            boolean isSuccess = userDAO.deleteUser(userId);

            if (isSuccess) {
                out.print("{\"message\": \"User deleted successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Failed to delete user\"}");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"User ID is required\"}");
        }
        out.flush();
    }
}