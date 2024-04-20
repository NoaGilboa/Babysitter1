package com.example.babysitter.Users;

public class Parent extends User {
    private int numberOfChildren;
    private String maritalStatus;

    public Parent() {
        super();
    }
    public Parent(String uid, String name, String phone, String mail, String address, String password, int numberOfChildren,double latitude,double longitude) {
        super(uid, name, phone, mail, address, password,latitude,longitude);
        this.numberOfChildren = numberOfChildren;
        this.userType = "Parent";
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public Parent setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
        return this;
    }

}
