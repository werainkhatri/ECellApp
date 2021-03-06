package com.nitrr.ecell.esummit.ecellapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GenericMessage implements Serializable {

    @SerializedName("message")
    @Expose
    String message;

    public String getMessage() {
        return message;
    }
}
