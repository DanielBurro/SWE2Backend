package com.dhbw.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeDTO {

    @NotBlank(message = "Das aktuelle Passwort darf nicht leer sein.")
    private String currentPassword;

    @NotBlank(message = "Das neue Passwort darf nicht leer sein.")
    @Size(min = 8, message = "Das neue Passwort muss mindestens 8 Zeichen lang sein.")
    private String newPassword;
}
