package com.redkix.automation.model;


public class User {

    private String email;
    private String password;
    private EmailServiceType serviceType;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public EmailServiceType getServiceType() {
        return serviceType;
    }

    public User setServiceType(EmailServiceType serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", serviceType=" + serviceType +
                '}';
    }
}
