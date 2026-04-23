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

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Users getUserById(Long id) {
        if (id == null) throw new IllegalArgumentException("ID darf nicht null sein");
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Benutzer mit ID " + id + " nicht gefunden"));
    }

    public Users getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Eingeloggter Benutzer nicht gefunden."));
    }

    public void assertCurrentUser(Long targetUserId) {
        Users currentUser = getCurrentUser();
        if (!currentUser.getId().equals(targetUserId)) {
            throw new SecurityException("Zugriff verweigert: Du kannst nur dein eigenes Profil bearbeiten.");
        }
    }

    @SuppressWarnings("null")
    @Transactional
    public Users updateUser(Long id, UserUpdateDTO updateDTO) {
        assertCurrentUser(id);

        Users existingUser = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User mit ID " + id + " nicht gefunden."));

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

        // (1) Eine E-Mail-Adresse darf im System nur exakt einmal existieren.
        if (updateDTO.getEmail() != null) {
            userRepository.findByEmail(updateDTO.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(id)) {
                    throw new IllegalArgumentException("E-Mail bereits vergeben");
                }
            });
            existingUser.setEmail(updateDTO.getEmail());
        }

        // (16) Das Feld USER_BIO darf maximal 200 Zeichen haben.
        if (updateDTO.getBio() != null) {
            if (updateDTO.getBio().length() > 200) {
                throw new IllegalArgumentException("Bio hat zu viele Zeichen");
            }
            existingUser.setBio(updateDTO.getBio());
        }

        // Regel 15: Das Feld USER_PROFILE_PIC_URL muss mit “http://” oder “https://” anfangen.
        if (updateDTO.getProfilePicUrl() != null && !updateDTO.getProfilePicUrl().isEmpty()) {
            String url = updateDTO.getProfilePicUrl();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                throw new IllegalArgumentException("Ungültige URL");
            }
            existingUser.setProfilePicUrl(url);
        }

        return userRepository.save(existingUser);
    }

    @SuppressWarnings("null")
    @Transactional
    public void changePassword(Long id, PasswordChangeDTO dto) {
        assertCurrentUser(id);

        Users user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User mit ID " + id + " nicht gefunden."));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Das aktuelle Passwort ist falsch.");
        }

        // (2) Das Passwort darf niemals im Klartext gespeichert werden.
        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @SuppressWarnings("null")
    @Transactional
    public void deleteUser(Long id) {
        assertCurrentUser(id);

        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User mit ID " + id + " nicht gefunden.");
        }
        userRepository.deleteById(id);
    }
}