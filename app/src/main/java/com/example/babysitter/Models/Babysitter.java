package com.example.babysitter.Models;

public class Babysitter extends User {
    private int age;
    private boolean smoke;
    private String maritalStatus;
    private String description;
    private double hourlyWage;
    private double experience;

    public Babysitter() {
        super();
    }

    public Babysitter(String uid, String name, String phone, String mail, String address, String password,
                      int age, boolean smoke, String maritalStatus, String description, double hourlyWage, double experience,double latitude,double longitude) {
        super(uid, name, phone, mail, address, password,latitude,longitude);
        this.age = age;
        this.smoke = smoke;
        this.maritalStatus = maritalStatus;
        this.description = description;
        this.hourlyWage = hourlyWage;
        this.experience=experience;
        this.userType = "Babysitter";
    }

    public int getAge() {
        return age;
    }

    public Babysitter setAge(int age) {
        this.age = age;
        return this;
    }

    public boolean isSmoke() {
        return smoke;
    }

    public Babysitter setSmoke(boolean smoke) {
        this.smoke = smoke;
        return this;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public Babysitter setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Babysitter setDescription(String description) {
        this.description = description;
        return this;
    }

    public double getHourlyWage() {
        return hourlyWage;
    }

    public Babysitter setHourlyWage(double hourlyWage) {
        this.hourlyWage = hourlyWage;
        return this;
    }

    public double getExperience() {
        return experience;
    }

    public Babysitter setExperience(double experience) {
        this.experience = experience;
        return this;
    }
}
