package com.dhbw.backend.controller;

import com.dhbw.backend.dto.InvitationCreateDTO;
import com.dhbw.backend.dto.InvitationResponseDTO;
import com.dhbw.backend.dto.InvitationUpdateDTO;
import com.dhbw.backend.model.Invitation;
import com.dhbw.backend.service.InvitationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<InvitationResponseDTO>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(invitationService.getInvitationsForEvent(eventId).stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InvitationResponseDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(invitationService.getInvitationsForUser(userId).stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    @Operation(summary = "Neue Einladung versenden", description = "Lädt einen Gast zu einem Event ein.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Einladung erfolgreich versendet"),
            @ApiResponse(responseCode = "400", description = "Ungültige Eingabe (z.B. Host lädt sich selbst ein oder Gast ist bereits eingeladen)")
    })
    @PostMapping
    public ResponseEntity<InvitationResponseDTO> sendInvitation(@RequestBody InvitationCreateDTO dto) {
        Invitation inv = invitationService.sendInvitation(dto.getEventId(), dto.getGuestId());
        return ResponseEntity.ok(mapToDTO(inv));
    }

    @Operation(summary = "Status einer Einladung aktualisieren", description = "Setzt den Status (z.B. ACCEPTED) und aktualisiert die Begleitpersonen.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status erfolgreich aktualisiert"),
            @ApiResponse(responseCode = "400", description = "Ungültige Eingabe (z.B. Einladung nicht gefunden)"),
            @ApiResponse(responseCode = "409", description = "Konflikt (z.B. Location ist bereits voll ausgebucht)")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<InvitationResponseDTO> updateStatus(
            @PathVariable Long id, 
            @RequestBody InvitationUpdateDTO dto) {
        Invitation updatedInv = invitationService.updateStatus(id, dto.getStatus(), dto.getPlusOnes());
        return ResponseEntity.ok(mapToDTO(updatedInv));
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