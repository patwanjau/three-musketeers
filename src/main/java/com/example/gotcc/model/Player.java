package com.example.gotcc.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class Player {
    @Pattern(regexp = "^[a-zA-Z][0-9a-zA-Z]{2,}$",
        message = "Username must start with a letter. Only alphanumeric characters allowed and must be more than 2 characters")
    private String username;
    @NotBlank(message = "Player name is required.")
    private String fullname;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
