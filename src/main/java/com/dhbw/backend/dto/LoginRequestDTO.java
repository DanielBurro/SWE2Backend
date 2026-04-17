package com.dhbw.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "E-Mail darf nicht leer sein.")
    @Email(message = "Ungültiges E-Mail-Format.")
    private String email;

    @NotBlank(message = "Passwort darf nicht leer sein.")
    private String password;
}