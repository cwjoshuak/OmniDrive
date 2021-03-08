package com.example.omnidrive;

public class UserRequest {
    final String email;
    final String firstname;
    final String lastname;
    final String password_hash;

    public UserRequest(String email, String firstname, String lastname, String password_hash) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password_hash = password_hash;
    }
}
