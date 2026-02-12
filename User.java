package com.helpdesk.model;

public class User {
    public enum Role {
        REQUESTER, TECHNICIAN, ADMIN
    }

    private int id;
    private String name;
    private Role role;

    public User(int id, String name, Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return String.format("User #%d: %s (%s)", id, name, role);
    }
}
