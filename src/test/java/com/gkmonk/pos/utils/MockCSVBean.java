package com.gkmonk.pos.utils;

import com.gkmonk.pos.model.CSVAnnotations;

public class MockCSVBean {

    @CSVAnnotations(column = "name")
    private String name;

    @CSVAnnotations(column = "age")
    private int age;

    @CSVAnnotations(column = "active")
    private boolean active;

    // Getters and Setters
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setActive(boolean active) { this.active = active; }

    public String getName() { return name; }
    public int getAge() { return age; }
    public boolean isActive() { return active; }
}
