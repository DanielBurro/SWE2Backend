package com.dhbw.backend.service;

import com.dhbw.backend.dto.UserUpdateDTO;
import com.dhbw.backend.model.Users;
import com.dhbw.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    // Methode zum Abrufen eines Benutzers nach ID
    public Users getUserById(Long id) {
        if (id == null) throw new IllegalArgumentException("ID darf nicht null sein");
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Benutzer mit ID " + id + " nicht gefunden"));
    }

    @Transactional
    public Users registerUser(Users user) {
        // (1) Validierung: E-Mail darf nicht doppelt sein
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-Mail Adresse wird bereits verwendet.");
        }

        // (15) Validierung: Profilbild-URL muss gültig sein
        if (user.getProfilePicUrl() != null && !user.getProfilePicUrl().isEmpty()) {
            if (!user.getProfilePicUrl().startsWith("http://") && !user.getProfilePicUrl().startsWith("https://")) {
                throw new IllegalArgumentException("Die URL muss mit http:// oder https:// beginnen.");
            }
        }

        // (16) Validierung: Bio darf maximal 500 Zeichen lang sein
        if (user.getBio() != null && user.getBio().length() > 200) {
            throw new IllegalArgumentException("Die Bio darf maximal 500 Zeichen lang sein.");
        }
        
        // (3) Initial-Daten setzen
        user.setCreatedAt(LocalDate.now());
        
        return userRepository.save(user);
    }

    @SuppressWarnings("null")
    @Transactional
    public Users updateUser(Long id, UserUpdateDTO updateDTO) {

        Users existingUser = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User mit ID " + id + " nicht gefunden."));

        if (updateDTO.getFirstName() != null) existingUser.setFirstName(updateDTO.getFirstName());
        if (updateDTO.getLastName() != null) existingUser.setLastName(updateDTO.getLastName());
        if (updateDTO.getBio() != null) {
            if (updateDTO.getBio().length() > 200) {
                throw new IllegalArgumentException("Die Bio darf maximal 200 Zeichen lang sein.");
            }
            existingUser.setBio(updateDTO.getBio());
        }
        if (updateDTO.getProfilePicUrl() != null) {
            if (!updateDTO.getProfilePicUrl().startsWith("http://") && !updateDTO.getProfilePicUrl().startsWith("https://")) {
                throw new IllegalArgumentException("Die URL muss mit http:// oder https:// beginnen.");
            }
            existingUser.setProfilePicUrl(updateDTO.getProfilePicUrl());
        }

        return userRepository.save(existingUser);
    }
}