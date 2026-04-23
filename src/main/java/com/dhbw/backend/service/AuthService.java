package com.dhbw.backend.service;

import com.dhbw.backend.dto.AuthResponseDTO;
import com.dhbw.backend.dto.LoginRequestDTO;
import com.dhbw.backend.dto.UserDTO;
import com.dhbw.backend.model.Users;
import com.dhbw.backend.model.VerificationToken;
import com.dhbw.backend.repository.UserRepository;
import com.dhbw.backend.repository.VerificationTokenRepository;
import com.dhbw.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    // --- LOGIN ---
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        Users user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Ungültige E-Mail oder Passwort."));

        if (!user.isVerified()) {
            throw new IllegalStateException("Bitte verifiziere zuerst deine E-Mail-Adresse über den Link in deinen E-Mails.");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Ungültige E-Mail oder Passwort.");
        }

        String token = jwtService.generateToken(user.getEmail());

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setBio(user.getBio());
        userDTO.setProfilePicUrl(user.getProfilePicUrl());
        
        response.setUser(userDTO);
        return response;
    }

    // --- REGISTRIERUNG ---
    @Transactional
    public Users registerUser(Users newUser) {
        // (1) Eine E-Mail-Adresse darf im System nur exakt einmal existieren.
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-Mail bereits vergeben");
        }

        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Benutzername wird bereits verwendet.");
        }

        // (2) Das Passwort darf niemals im Klartext gespeichert werden.
        String encodedPassword = passwordEncoder.encode(newUser.getPasswordHash());
        newUser.setPasswordHash(encodedPassword);
        
        // (3) Das Feld USER_CREATED_AT muss vom Backend automatisch gesetzt werden.
        newUser.setCreatedAt(LocalDate.now());
        
        newUser.setVerified(false); 
        
        Users savedUser = userRepository.save(newUser);

        // Verifizierungs-Token generieren
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, savedUser);
        tokenRepository.save(verificationToken);

        // (20): Mail-Fehler soll Prozess nicht blockieren.
        try {
            emailService.sendVerificationEmail(savedUser.getEmail(), token);
        } catch (Exception e) {
            System.err.println("E-Mail konnte nicht versendet werden: " + e.getMessage());
            // Hier werfen wir keinen Fehler, damit der User trotzdem gespeichert wird.
            // Später könnte man einen "E-Mail erneut senden" Endpoint bauen.
        }

        return savedUser;
    }

    // --- VERIFIZIERUNG ---
    @Transactional
    public boolean verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElse(null);

        if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false; 
        }

        Users user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);
        return true;
    }
}