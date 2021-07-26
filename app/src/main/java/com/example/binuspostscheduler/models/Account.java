package com.example.binuspostscheduler.models;

public class Account {
    private String access_token;
    private String access_secret;
    private String uid;
    private String username;
    private String type;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public Account() {
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Account(String access_token, String access_secret, String uid, String username) {
        this.access_token = access_token;
        this.access_secret = access_secret;
        this.uid = uid;
        this.username = username;
        this.type = type;
        this.checked = false;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getAccess_secret() {
        return access_secret;
    }

    public void setAccess_secret(String access_secret) {
        this.access_secret = access_secret;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
