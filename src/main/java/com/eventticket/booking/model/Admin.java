package com.eventticket.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Admin {
    private String adminId;
    private String userName; // This will store the email
    private String password;
}
