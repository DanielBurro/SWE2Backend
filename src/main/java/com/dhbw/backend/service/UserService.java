package com.dhbw.backend.service;

import com.dhbw.backend.dto.PasswordChangeDTO;
import com.dhbw.backend.dto.UserUpdateDTO;
import com.dhbw.backend.model.Users;
import com.dhbw.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    // Methode zum Abrufen eines Benutzers nach ID
    public Users getUserById(Long id) {
        if (id == null) throw new IllegalArgumentException("ID darf nicht null sein");
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Benutzer mit ID " + id + " nicht gefunden"));
    }

    // Aktuell eingeloggten User anhand des JWT-Tokens ermitteln
    public Users getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Eingeloggter Benutzer nicht gefunden."));
    }

    // Prüft, ob der eingeloggte User die angegebene ID besitzt
    public void assertCurrentUser(Long targetUserId) {
        Users currentUser = getCurrentUser();
        if (!currentUser.getId().equals(targetUserId)) {
            throw new SecurityException("Zugriff verweigert: Du kannst nur dein eigenes Profil bearbeiten.");
        }
    }

    @Transactional
    public Users registerUser(Users user) {
        // (1) Validierung: E-Mail darf nicht doppelt sein
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-Mail Adresse wird bereits verwendet.");
        }

        // Validierung: Benutzername darf nicht doppelt sein
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Benutzername wird bereits verwendet.");
        }
        
        // (3) Initial-Daten setzen
        user.setCreatedAt(LocalDate.now());

        // (2) Passwort hashenen
        String encodedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(encodedPassword);
        
        return userRepository.save(user);
    }

    @SuppressWarnings("null")
    @Transactional
    public Users updateUser(Long id, UserUpdateDTO updateDTO) {
        // Zugriffskontrolle: Nur das eigene Profil darf bearbeitet werden
        assertCurrentUser(id);

        Users existingUser = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User mit ID " + id + " nicht gefunden."));

        // Username ändern (mit Duplikat-Prüfung)
        if (updateDTO.getUsername() != null) {
            userRepository.findByUsername(updateDTO.getUsername()).ifPresent(u -> {
                if (!u.getId().equals(id)) {
                    throw new IllegalArgumentException("Benutzername wird bereits verwendet.");
                }
            });
            existingUser.setUsername(updateDTO.getUsername());
        }

        if (updateDTO.getFirstName() != null) existingUser.setFirstName(updateDTO.getFirstName());
        if (updateDTO.getLastName() != null) existingUser.setLastName(updateDTO.getLastName());

        // E-Mail ändern (mit Duplikat-Prüfung)
        if (updateDTO.getEmail() != null) {
            userRepository.findByEmail(updateDTO.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(id)) {
                    throw new IllegalArgumentException("E-Mail Adresse wird bereits verwendet.");
                }
            });
            existingUser.setEmail(updateDTO.getEmail());
        }

        if (updateDTO.getBio() != null) existingUser.setBio(updateDTO.getBio());
        if (updateDTO.getProfilePicUrl() != null) existingUser.setProfilePicUrl(updateDTO.getProfilePicUrl());

        return userRepository.save(existingUser);
    }

    // Passwort ändern
    @Transactional
    public void changePassword(Long id, PasswordChangeDTO dto) {
        assertCurrentUser(id);

        Users user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User mit ID " + id + " nicht gefunden."));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Das aktuelle Passwort ist falsch.");
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    // User löschen
    @Transactional
    public void deleteUser(Long id) {
        assertCurrentUser(id);

        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User mit ID " + id + " nicht gefunden.");
        }
        userRepository.deleteById(id);
    }
}