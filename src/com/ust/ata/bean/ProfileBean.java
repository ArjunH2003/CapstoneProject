package com.ust.ata.bean;

import java.sql.Date;

public class ProfileBean {
    private String userID, firstName, lastName, gender, street, location, city, state, pincode, mobileNo, emailID;
    private Date dateOfBirth;
    // Getters Setters for all
    public String getUserID() { return userID; } public void setUserID(String userID) { this.userID = userID; }
    public String getFirstName() { return firstName; } public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; } public void setLastName(String lastName) { this.lastName = lastName; }
    public String getGender() { return gender; } public void setGender(String gender) { this.gender = gender; }
    public String getStreet() { return street; } public void setStreet(String street) { this.street = street; }
    public String getLocation() { return location; } public void setLocation(String location) { this.location = location; }
    public String getCity() { return city; } public void setCity(String city) { this.city = city; }
    public String getState() { return state; } public void setState(String state) { this.state = state; }
    public String getPincode() { return pincode; } public void setPincode(String pincode) { this.pincode = pincode; }
    public String getMobileNo() { return mobileNo; } public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
    public String getEmailID() { return emailID; } public void setEmailID(String emailID) { this.emailID = emailID; }
    public Date getDateOfBirth() { return dateOfBirth; } public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}