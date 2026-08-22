package com.sunrisedental.servlet;

import com.google.gson.Gson;
import com.sunrisedental.dao.UserDAO;
import com.sunrisedental.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userDAO.validateUser(username, password);

        Map<String, Object> jsonResponse = new HashMap<>();

        if (user != null) {
            // Session Management (Lecturer requirement)
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user);

            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Login Successful");
            jsonResponse.put("role", user.getRole());
        } else {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Invalid Username or Password");
        }

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
}