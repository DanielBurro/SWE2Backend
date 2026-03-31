package com.dhbw.backend.dto;

import lombok.Data;

@Data
public class UserCreateDTO {
    // Für die Registrierung
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password; // Wird später im Service gehasht
}