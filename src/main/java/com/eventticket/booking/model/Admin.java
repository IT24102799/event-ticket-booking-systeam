package com.eventticket.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArConstructor

public class Admin {
    private String adminId;
    private String userName;
    private String password;
}
