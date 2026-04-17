package com.dhbw.backend.controller;

import com.dhbw.backend.dto.InvitationCreateDTO;
import com.dhbw.backend.dto.InvitationResponseDTO;
import com.dhbw.backend.dto.InvitationUpdateDTO;
import com.dhbw.backend.model.Invitation;
import com.dhbw.backend.service.InvitationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @Operation(summary = "Einzelne Einladung abrufen")
    @GetMapping("/{id}")
    public ResponseEntity<InvitationResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mapToDTO(invitationService.getInvitationById(id)));
    }

    @Operation(summary = "Alle Einladungen eines Events abrufen",
            description = "Optional nach Status filtern: ?status=ACCEPTED")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<InvitationResponseDTO>> getByEvent(
            @PathVariable Long eventId,
            @RequestParam(required = false) String status) {
        List<Invitation> invitations = (status != null && !status.isBlank())
                ? invitationService.getInvitationsForEventByStatus(eventId, status)
                : invitationService.getInvitationsForEvent(eventId);
        return ResponseEntity.ok(invitations.stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InvitationResponseDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(invitationService.getInvitationsForUser(userId).stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    @Operation(summary = "Eigene Einladungen abrufen",
            description = "Gibt alle Einladungen des eingeloggten Users zurück (aus JWT).")
    @GetMapping("/me")
    public ResponseEntity<List<InvitationResponseDTO>> getMyInvitations() {
        return ResponseEntity.ok(invitationService.getMyInvitations().stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    @Operation(summary = "Neue Einladung versenden",
            description = "Lädt einen Gast zu einem Event ein. Nur der Host darf einladen.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Einladung erfolgreich versendet"),
            @ApiResponse(responseCode = "400", description = "Ungültige Eingabe (z.B. Host lädt sich selbst ein oder Gast ist bereits eingeladen)"),
            @ApiResponse(responseCode = "403", description = "Nur der Host darf einladen")
    })
    @PostMapping
    public ResponseEntity<InvitationResponseDTO> sendInvitation(@Valid @RequestBody InvitationCreateDTO dto) {
        Invitation inv = invitationService.sendInvitation(dto.getEventId(), dto.getGuestId());
        return ResponseEntity.ok(mapToDTO(inv));
    }

    @Operation(summary = "Status einer Einladung aktualisieren",
            description = "Setzt den Status (ACCEPTED/DECLINED/CANCELLED) und Begleitpersonen. Nur der eingeladene Gast darf ändern.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status erfolgreich aktualisiert"),
            @ApiResponse(responseCode = "400", description = "Ungültige Eingabe"),
            @ApiResponse(responseCode = "403", description = "Nur der eingeladene Gast darf den Status ändern"),
            @ApiResponse(responseCode = "409", description = "Location ist bereits voll ausgebucht")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<InvitationResponseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody InvitationUpdateDTO dto) {
        Invitation updatedInv = invitationService.updateStatus(id, dto.getStatus(), dto.getPlusOnes());
        return ResponseEntity.ok(mapToDTO(updatedInv));
    }

    @Operation(summary = "Einladung zurückziehen",
            description = "Löscht eine Einladung. Nur der Event-Host darf zurückziehen.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Einladung erfolgreich zurückgezogen"),
            @ApiResponse(responseCode = "403", description = "Nur der Host darf Einladungen zurückziehen"),
            @ApiResponse(responseCode = "404", description = "Einladung nicht gefunden")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteInvitation(@PathVariable Long id) {
        invitationService.deleteInvitation(id);
        return ResponseEntity.ok(Map.of("message", "Einladung erfolgreich zurückgezogen."));
    }

    private InvitationResponseDTO mapToDTO(Invitation inv) {
        InvitationResponseDTO dto = new InvitationResponseDTO();
        dto.setId(inv.getId());
        dto.setStatus(inv.getStatus());
        dto.setPlusOnes(inv.getPlusOnes());
        dto.setSentAt(inv.getSentAt());
        
        if (inv.getEvent() != null) {
            dto.setEventId(inv.getEvent().getId());
            dto.setEventTitle(inv.getEvent().getTitle());
        }
        if (inv.getGuest() != null) {
            dto.setGuestId(inv.getGuest().getId());
            dto.setGuestName(inv.getGuest().getUsername());
        }
        return dto;
    }
}