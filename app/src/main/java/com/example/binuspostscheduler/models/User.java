package com.example.binuspostscheduler.models;

public class User {

    private String username;
    private String id;
    private String email;
    private String password;
    private String role;

    public User() {}

    public User(String username, String id, String email, String password, String role) {
        this.username = username;
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

}
