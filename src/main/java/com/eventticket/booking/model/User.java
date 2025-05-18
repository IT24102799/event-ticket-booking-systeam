package com.eventticket.booking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private Long id;
    private String name;
    private String email;
    private String phone;

    // Only serialize password when setting it, not when reading
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // User role (ADMIN, USER)
    private String role;

    public User() {
    }

    public User(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = "USER"; // Default role
    }
}

