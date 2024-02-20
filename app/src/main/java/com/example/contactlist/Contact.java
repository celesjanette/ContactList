package com.example.contactlist;
import android.graphics.Bitmap;

import java.util.Calendar;
public class Contact {
    private int contactID;
    private String contactName;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String homeNumber;
    private String cellNumber;
    private String eMail;
    private Calendar birthday;
    private Bitmap picture;

    public Contact() {
        contactID = -1;
        birthday = Calendar.getInstance();
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int i) {
        contactID = i;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String s) {
        contactName = s;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public void setBirthday(Calendar c) {
        birthday = c;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String s) {
        streetAddress = s;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String s) {
        city = s;
    }

    public String getState() {
        return state;
    }

    public void setState(String s) {
        state = s;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String s) {
        zipCode = s;
    }

    public void setHomeNumber(String s) {
        homeNumber = s;
    }

    public String getHomeNumber() {
        return homeNumber;
    }

    public void setCellNumber(String s) {
        cellNumber = s;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void seteMail(String s) {
        eMail = s;
    }

    public String geteMail() {
        return eMail;
    }

    public String getPhoneNumber() {
        // Return either the home number or the cell number, depending on which one is set
        if (homeNumber != null && !homeNumber.isEmpty()) {
            return homeNumber;
        } else if (cellNumber != null && !cellNumber.isEmpty()) {
            return cellNumber;
        } else {
            return null; // Or return an empty string, depending on your logic
        }
    }
public void setPicture(Bitmap b) {
        picture = b;
    }
    public Bitmap getPicture() {
        return picture;
    }
}
