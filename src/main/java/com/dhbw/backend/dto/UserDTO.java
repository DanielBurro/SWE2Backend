package com.dhbw.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserDTO {
    // Sichere Antwort ans Frontend, OHNE Passwort
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate createdAt;
    private String bio;
    private String profilePicUrl;
}
