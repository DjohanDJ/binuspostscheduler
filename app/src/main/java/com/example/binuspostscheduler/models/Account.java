package com.example.binuspostscheduler.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Account implements Parcelable {
    private String access_token;
    private String access_secret;
    private String uid;
    private String username;
    private String type;
    private boolean checked;
    private String pid;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.access_token);
        dest.writeString(this.access_secret);
        dest.writeString(this.uid);
        dest.writeString(this.username);
        dest.writeString(this.type);
        dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.access_token = source.readString();
        this.access_secret = source.readString();
        this.uid = source.readString();
        this.username = source.readString();
        this.type = source.readString();
        this.checked = source.readByte() != 0;
    }

    protected Account(Parcel in) {
        this.access_token = in.readString();
        this.access_secret = in.readString();
        this.uid = in.readString();
        this.username = in.readString();
        this.type = in.readString();
        this.checked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
}
