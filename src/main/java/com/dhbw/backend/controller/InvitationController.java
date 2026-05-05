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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @Operation(summary = "Einzelne Einladung abrufen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Einladung gefunden"),
            @ApiResponse(responseCode = "404", description = "Einladung nicht gefunden")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InvitationResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mapToDTO(invitationService.getInvitationById(id)));
    }

    @Operation(summary = "Alle Einladungen eines Events abrufen",
            description = "Optional nach Status filtern: ?status=ACCEPTED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste der Einladungen"),
            @ApiResponse(responseCode = "404", description = "Event nicht gefunden")
    })
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<InvitationResponseDTO>> getByEvent(
            @PathVariable Long eventId,
            @RequestParam(required = false) String status) {
        List<Invitation> invitations = (status != null && !status.isBlank())
                ? invitationService.getInvitationsForEventByStatus(eventId, status)
                : invitationService.getInvitationsForEvent(eventId);
        return ResponseEntity.ok(invitations.stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @Operation(summary = "Einladungen eines Users abrufen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste der Einladungen"),
            @ApiResponse(responseCode = "404", description = "User nicht gefunden")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InvitationResponseDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(invitationService.getInvitationsForUser(userId).stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    @Operation(summary = "Eigene Einladungen abrufen",
            description = "Gibt alle Einladungen des eingeloggten Users zurück (aus JWT).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste der eigenen Einladungen"),
            @ApiResponse(responseCode = "401", description = "Nicht authentifiziert")
    })
    @GetMapping("/me")
    public ResponseEntity<List<InvitationResponseDTO>> getMyInvitations() {
        return ResponseEntity.ok(invitationService.getMyInvitations().stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    @Operation(summary = "Neue Einladung versenden",
            description = "Lädt einen Gast zu einem Event ein. Nur der Host darf einladen.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Einladung erfolgreich versendet"),
            @ApiResponse(responseCode = "403", description = "Nur der Host darf einladen"),
            @ApiResponse(responseCode = "404", description = "Event oder Gast nicht gefunden"),
            @ApiResponse(responseCode = "409", description = "Host lädt sich selbst ein, oder Gast bereits eingeladen"),
            @ApiResponse(responseCode = "422", description = "Validierungsfehler")
    })
    @PostMapping
    public ResponseEntity<InvitationResponseDTO> sendInvitation(@Valid @RequestBody InvitationCreateDTO dto) {
        Invitation inv = invitationService.sendInvitation(dto.getEventId(), dto.getGuestId());

        // 201 Created — neue Einladung wurde erstellt
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(inv));
    }

    @Operation(summary = "Status einer Einladung aktualisieren",
            description = "Setzt den Status (ACCEPTED/DECLINED/CANCELLED) und Begleitpersonen. Nur der eingeladene Gast darf ändern.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status erfolgreich aktualisiert"),
            @ApiResponse(responseCode = "403", description = "Nur der eingeladene Gast darf den Status ändern"),
            @ApiResponse(responseCode = "404", description = "Einladung nicht gefunden"),
            @ApiResponse(responseCode = "409", description = "Location ist bereits voll ausgebucht"),
            @ApiResponse(responseCode = "422", description = "Ungültiger Status-Wert")
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
            @ApiResponse(responseCode = "204", description = "Einladung erfolgreich zurückgezogen"),
            @ApiResponse(responseCode = "403", description = "Nur der Host darf Einladungen zurückziehen"),
            @ApiResponse(responseCode = "404", description = "Einladung nicht gefunden")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvitation(@PathVariable Long id) {
        invitationService.deleteInvitation(id);
        // 204 No Content — kein Body nötig bei Löschung
        return ResponseEntity.noContent().build();
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