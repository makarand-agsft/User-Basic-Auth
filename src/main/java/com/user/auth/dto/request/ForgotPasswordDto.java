package com.user.auth.dto.request;

public class ForgotPasswordDto {

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ForgotPasswordDto{" +
                "email='" + email + '\'' +
                '}';
    }
}
