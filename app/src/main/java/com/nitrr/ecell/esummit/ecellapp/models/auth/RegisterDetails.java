package com.nitrr.ecell.esummit.ecellapp.models.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RegisterDetails implements Serializable {

    @SerializedName("token")
    @Expose
    String apptoken ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnQiOiJhbmRyb2lkIiwib3JnYW5pemF0aW9uIjoiRUNlbGwifQ.H2aaDJuOxK44D2kwRCWwv9s5rzJGCNYKT3thtQqN-hQ";

    @SerializedName("first_name")
    @Expose
    String firstName;

    @SerializedName("last_name")
    @Expose
    String lastName;

    @SerializedName("email")
    @Expose
    String email;

    @SerializedName("password")
    @Expose
    String password;

    @SerializedName("contact")
    @Expose
    String contact;

    @SerializedName("avatar")
    @Expose
    String avatar;

    @SerializedName("linkedin")
    @Expose
    String linkedin;

    @SerializedName("facebook")
    @Expose
    String facebook;

    public RegisterDetails(String firstName, String lastName, String email, String password, String contact, String avatar, String linkedin, String facebook) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.avatar = avatar;
        this.linkedin = linkedin;
        this.facebook = facebook;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getContact() {
        return contact;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public String getFacebook() {
        return facebook;
    }
}
