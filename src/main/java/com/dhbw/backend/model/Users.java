package com.dhbw.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "users") // "user" ist in SQL oft reserviert, daher Plural
@Getter
@Setter
@NoArgsConstructor
public class Users {

    // 1. ID des Users (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    // 2. Benutzername
    @Column(name = "USER_UNAME", length = 50, nullable = false)
    private String username;

    // 3. Vorname
    @Column(name = "USER_FNAME", length = 50)
    private String firstName;

    // 4. Nachname
    @Column(name = "USER_LNAME", length = 50)
    private String lastName;

    // 5. E-Mail-Adresse
    @Column(name = "USER_MAIL", length = 100, nullable = false, unique = true)
    private String email;

    // 6. Passwort-Hash
    @Column(name = "USER_PASSWORD_HASH", length = 255, nullable = false)
    private String passwordHash;

    // 7. Erstellungsdatum
    @Column(name = "USER_CREATED_AT", nullable = false)
    private LocalDate createdAt;

    // 28. Profilbild-URL
    @Column(name = "USER_PROFILE_PIC_URL", length = 255)
    private String profilePicUrl;

    // 29. Info-Text über den User
    @Column(name = "USER_BIO", columnDefinition = "TEXT")
    private String bio; // Optional, daher kein nullable = false
}