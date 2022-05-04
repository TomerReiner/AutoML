package com.automl.automl;


import androidx.annotation.NonNull;

/**
 * This class represents a user.
 */
public class User {

    private final String username; // The username of the user.
    private final String phoneNum; // The phone number of the user.
    private String password; // The password of the user.

    public User(String username, String phoneNum, String password) {
        this.username = username;
        this.phoneNum = phoneNum;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getPassword() {
        return password;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
