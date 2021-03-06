package com.example.omnidrive;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("email")
    private String email;
    @SerializedName("firstname")
    private String firstname;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("password")
    private String password_hash;

    public User(String email, String firstname, String lastname, String password_hash) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password_hash = password_hash;
    }

    public String getEmail() { return email; }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPassword() {
        return password_hash;
    }

}