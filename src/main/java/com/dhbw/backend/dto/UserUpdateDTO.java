package com.dhbw.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @Size(min = 3, max = 50, message = "Benutzername muss zwischen 3 und 50 Zeichen lang sein.")
    private String username;

    @Size(max = 50, message = "Vorname darf maximal 50 Zeichen lang sein.")
    private String firstName;

    @Size(max = 50, message = "Nachname darf maximal 50 Zeichen lang sein.")
    private String lastName;

    @Email(message = "Ungültiges E-Mail-Format.")
    private String email;

    // (16) Validierung: Bio darf maximal 200 Zeichen lang sein
    @Size(max = 200, message = "Bio darf maximal 200 Zeichen lang sein.")
    private String bio;

    // (15) Validierung: Profilbild-URL muss gültig sein
    @Pattern(regexp = "^(https?://.*)?$", message = "Die URL muss mit http:// oder https:// beginnen.")
    private String profilePicUrl;
}
