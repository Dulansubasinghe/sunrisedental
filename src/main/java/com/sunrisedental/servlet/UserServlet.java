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

    // 1. Get All Users or Search User by ID
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                User user = userDAO.getUserById(id);
                if (user != null) {
                    out.print(gson.toJson(user));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"status\":\"error\", \"message\":\"User not found.\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Invalid User ID format.\"}");
            }
        } else {
            List<User> list = userDAO.getAllUsers();
            out.print(gson.toJson(list));
        }
        out.flush();
    }

    // 2. Create New User with  Validation
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            User newUser = gson.fromJson(reader, User.class);

            String usernameRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z0-9_]{3,20}$";
            String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@#%!*?&])[A-Za-z\\d$@#%!*?&]{6,}$";

            if (newUser.getUsername() == null || !newUser.getUsername().matches(usernameRegex)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Username must contain Uppercase, Lowercase, and Number.\"}");
                out.flush();
                return;
            }

            if (newUser.getPassword() == null || !newUser.getPassword().matches(passwordRegex)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Password must contain Uppercase, Lowercase, Number, and Special character ($@#%!*?&).\"}");
                out.flush();
                return;
            }

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

    // 3. Update Existing User
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            User userToUpdate = gson.fromJson(reader, User.class);

            String usernameRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z0-9_]{3,20}$";
            String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@#%!*?&])[A-Za-z\\d$@#%!*?&]{6,}$";

            if (userToUpdate.getUsername() != null && !userToUpdate.getUsername().matches(usernameRegex)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Invalid Username format.\"}");
                out.flush();
                return;
            }

            if (userToUpdate.getPassword() != null && !userToUpdate.getPassword().isEmpty() && !userToUpdate.getPassword().matches(passwordRegex)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Invalid Password format.\"}");
                out.flush();
                return;
            }

            boolean isSuccess = userDAO.updateUser(userToUpdate);

            if (isSuccess) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"status\":\"success\", \"message\":\"User updated successfully!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Failed to update user.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
}