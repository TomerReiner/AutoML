package com.automl.automl;

import android.content.Context;

import java.util.HashMap;

/**
 * This class represents a user.
 */
public class User {

    private String username; // The username of the user.
    private String phoneNum; // The phone number of the user.
    private String password; // The password of the user.

    public User(String username, String phoneNum, String password) {
        this.username = username;
        this.phoneNum = phoneNum;
        this.password = password;
    }

    public User(String username, String phoneNum) {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
