package com.handicape.MarketCreators;

public class User {

    public static String name;
    public static String email;
    public static String url_image;
    public static boolean loginSuccess = false;

    public User() {
    }

    public User(String name, String email, String url_image, boolean loginSuccess) {
        this.name = name;
        this.email = email;
        this.url_image = url_image;
        this.loginSuccess = loginSuccess;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }
}
