package com.formz.constants;

public enum Role {

    ADMIN(1l,"ADMIN"),
    USER(2l,"USER");

    Role(Long id, String name) {

        this.id = id;
        this.name = name;
    }

    Long id;
    String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
