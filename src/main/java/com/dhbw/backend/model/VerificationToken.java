package com.dhbw.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {

    // 33. Verifizierungs-ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TOKEN_ID")
    private Long id;

    // 35. Verifizierungs-Token
    @Column(name = "TOKEN_VALUE", nullable = false, unique = true)
    private String token;

    // 34. User-ID
    @OneToOne(targetEntity = Users.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "TOKEN_USER_ID")
    private Users user;

    // 35. Ablaufdatum
    @Column(name = "EXPIRY_DATE", nullable = false)
    private LocalDateTime expiryDate;

    public VerificationToken(String token, Users user) {
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusHours(24); 
    }
}