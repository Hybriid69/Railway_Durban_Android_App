package com.example.railwaydurban;

public class Registration_Class {
    //Variables
    private String FirstName;
    private String LastName;
    private String EmailAddress;
    private String Password;
    private String PhoneNumber;


    //Contructor method
    public Registration_Class(String firstName, String lastName, String emailAddress, String password, String phoneNumber) {
        FirstName = firstName;
        LastName = lastName;
        EmailAddress = emailAddress;
        Password = password;
        PhoneNumber = phoneNumber;
    }

    //Getter and Setter methods
    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }
}
