package com.aubtin.studyroomreserver.utils;

/**
 * Created by Aubtin on 1/30/2017.
 */

public class User
{
    private String userUID;
    private String firstName;
    private String lastName;
    private String schoolEmail;
    private String name;
    private String email;
    private String schoolUserID;

    public User() {}

    public User(String userUID, String name, String email, String schoolUserID, String firstName, String lastName, String schoolEmail)
    {
        this.userUID = userUID;
        this.name = name;
        this.email = email;
        this.schoolUserID = schoolUserID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.schoolEmail = schoolEmail;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
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

    public String getSchoolUserID() {
        return schoolUserID;
    }

    public void setSchoolUserID(String schoolUserID) {
        this.schoolUserID = schoolUserID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSchoolEmail() {
        return schoolEmail;
    }

    public void setSchoolEmail(String schoolEmail) {
        this.schoolEmail = schoolEmail;
    }
}
