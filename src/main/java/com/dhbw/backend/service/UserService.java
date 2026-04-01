package com.dhbw.backend.service;

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
        
        // Initial-Daten setzen
        user.setCreatedAt(LocalDate.now());
        
        return userRepository.save(user);
    }
}