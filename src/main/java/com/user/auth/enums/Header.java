package com.user.auth.enums;

public enum Header {

    USER_AGENT("User-Agent"),

    BEARER("Bearer "),

    AUTHORIZATION("Authorization");

    private String value;

    Header(String value) {
        this.value=value;
    }

    public String getValue(){
        return value;
    }
}
