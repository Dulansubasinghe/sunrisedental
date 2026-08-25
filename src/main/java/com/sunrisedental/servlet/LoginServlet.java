package com.sunrisedental.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sunrisedental.dao.UserDAO;
import com.sunrisedental.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private Gson gson;

    @Override
    public void init() {
        userDAO = new UserDAO();
        gson = new Gson();
    }

    // Session Check
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> jsonResponse = new HashMap<>();

        String action = request.getParameter("action");

        // Logout Action handling
        if ("logout".equalsIgnoreCase(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Logged out successfully");
            out.print(gson.toJson(jsonResponse));
            out.flush();
            return;
        }

        // Current Session Check
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User user = (User) session.getAttribute("currentUser");
            jsonResponse.put("status", "authenticated");
            jsonResponse.put("user", user);
        } else {
            jsonResponse.put("status", "unauthenticated");
        }

        out.print(gson.toJson(jsonResponse));
        out.flush();
    }

    // User Authentication (Login Process)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> jsonResponse = new HashMap<>();

        String username = null;
        String password = null;

        // Handle both frontend data formats
        try {
            BufferedReader reader = request.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            if (jsonObject != null) {
                if (jsonObject.has("username")) username = jsonObject.get("username").getAsString();
                if (jsonObject.has("password")) password = jsonObject.get("password").getAsString();
            }
        } catch (Exception ignored) {
            // Fallback to form params if JSON parsing fails
        }

        if (username == null || username.trim().isEmpty()) {
            username = request.getParameter("username");
        }
        if (password == null || password.trim().isEmpty()) {
            password = request.getParameter("password");
        }

        // Validation Check
        if (username != null && password != null && !username.trim().isEmpty() && !password.trim().isEmpty()) {

            User user = userDAO.validateUser(username.trim(), password.trim());

            if (user != null) {
                // Save logged in user in session
                HttpSession session = request.getSession(true);
                session.setAttribute("currentUser", user);

                // Remove sensitive password from response
                user.setPassword(null);

                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Login Successful");
                jsonResponse.put("user", user);
                jsonResponse.put("role", user.getRole());

                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Invalid Username/Password or Account Inactive");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Username and Password are required");
        }

        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
}