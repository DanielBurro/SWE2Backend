package com.dhbw.backend.controller;

import com.dhbw.backend.dto.PasswordChangeDTO;
import com.dhbw.backend.dto.UserDTO;
import com.dhbw.backend.dto.UserUpdateDTO;
import com.dhbw.backend.model.Users;
import com.dhbw.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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

    @Operation(summary = "Alle Benutzer abrufen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste aller Benutzer")
    })
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    @Operation(summary = "Eigenes Profil abrufen", description = "Gibt den aktuell eingeloggten User anhand des JWT-Tokens zurück.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil erfolgreich abgerufen"),
            @ApiResponse(responseCode = "401", description = "Nicht authentifiziert")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ResponseEntity.ok(mapToDTO(userService.getCurrentUser()));
    }

    @Operation(summary = "Benutzer nach ID abrufen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Benutzer gefunden"),
            @ApiResponse(responseCode = "404", description = "Benutzer nicht gefunden")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(mapToDTO(userService.getUserById(id)));
    }

    @Operation(summary = "Benutzer aktualisieren", description = "Aktualisiert die Informationen eines bestehenden Benutzers. Nur das eigene Profil kann bearbeitet werden.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Benutzer erfolgreich aktualisiert"),
            @ApiResponse(responseCode = "403", description = "Nur das eigene Profil darf bearbeitet werden"),
            @ApiResponse(responseCode = "404", description = "Benutzer nicht gefunden"),
            @ApiResponse(responseCode = "422", description = "Validierungsfehler")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO updateDTO) {
        Users updatedUser = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(mapToDTO(updatedUser));
    }

    @Operation(summary = "Passwort ändern", description = "Ändert das Passwort des Benutzers. Das aktuelle Passwort muss korrekt angegeben werden.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Passwort erfolgreich geändert"),
            @ApiResponse(responseCode = "401", description = "Aktuelles Passwort falsch"),
            @ApiResponse(responseCode = "403", description = "Nur das eigene Passwort darf geändert werden"),
            @ApiResponse(responseCode = "422", description = "Validierungsfehler (z.B. neues Passwort zu kurz)")
    })
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody PasswordChangeDTO dto) {
        userService.changePassword(id, dto);
        // 204 No Content — kein Body nötig
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Benutzer löschen", description = "Löscht den eigenen Account unwiderruflich.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Benutzer erfolgreich gelöscht"),
            @ApiResponse(responseCode = "403", description = "Nur der eigene Account darf gelöscht werden"),
            @ApiResponse(responseCode = "404", description = "Benutzer nicht gefunden")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        // 204 No Content — kein Body nötig bei Löschung
        return ResponseEntity.noContent().build();
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