package com.sunrisedental.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Database Primary Key & Attributes
    private int userId;
    private String userCode;
    private String fullName;
    private String username;
    private String password;
    private String role;
    private String status;

    // Default Constructor
    public User() {
    }

    // Constructor for DB Insert
    public User(String userCode, String fullName, String username, String password, String role, String status) {
        this.userCode = userCode;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    // Full Constructor
    public User(int userId, String userCode, String fullName, String username, String password, String role, String status) {
        this.userId = userId;
        this.userCode = userCode;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}