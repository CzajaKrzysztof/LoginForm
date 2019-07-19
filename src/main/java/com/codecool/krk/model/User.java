package com.codecool.krk.model;

public class User {
    private int userId;
    private String name;
    private String type;

    public User(int userId, String name, String type) {
        this.userId = userId;
        this.name = name;
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
