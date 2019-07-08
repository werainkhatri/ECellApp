package com.nitrr.ecell.esummit.ecellapp.models.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RegisterDetails implements Serializable {

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
