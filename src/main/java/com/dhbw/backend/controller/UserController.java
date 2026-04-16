package com.dhbw.backend.controller;

import com.dhbw.backend.dto.UserCreateDTO;
import com.dhbw.backend.dto.UserDTO;
import com.dhbw.backend.dto.UserUpdateDTO;
import com.dhbw.backend.model.Users;
import com.dhbw.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(mapToDTO(userService.getUserById(id)));
    }

    @Operation(summary = "Neuen Benutzer registrieren", description = "Legt einen neuen User an und hasht das Passwort.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User erfolgreich registriert"),
            @ApiResponse(responseCode = "400", description = "Ungültige Eingabe")
    })
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserCreateDTO dto) {
        Users user = new Users();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPassword());
        
        return ResponseEntity.ok(mapToDTO(userService.registerUser(user)));
    }

    @Operation(summary = "Benutzer aktualisieren", description = "Aktualisiert die Informationen eines bestehenden Benutzers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Benutzer erfolgreich aktualisiert"),
            @ApiResponse(responseCode = "400", description = "Ungültige Eingabe"),
            @ApiResponse(responseCode = "404", description = "Benutzer nicht gefunden")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO updateDTO) {
    Users updatedUser = userService.updateUser(id, updateDTO);
    return ResponseEntity.ok(mapToDTO(updatedUser));
    }

    private UserDTO mapToDTO(Users user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setBio(user.getBio());
        dto.setProfilePicUrl(user.getProfilePicUrl());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}