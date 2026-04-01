package com.dhbw.backend.controller;

import com.dhbw.backend.dto.InvitationCreateDTO;
import com.dhbw.backend.dto.InvitationResponseDTO;
import com.dhbw.backend.dto.InvitationUpdateDTO;
import com.dhbw.backend.model.Invitation;
import com.dhbw.backend.service.InvitationService;
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

    @PostMapping
    public ResponseEntity<InvitationResponseDTO> sendInvitation(@RequestBody InvitationCreateDTO dto) {
        Invitation inv = invitationService.sendInvitation(dto.getEventId(), dto.getGuestId());
        return ResponseEntity.ok(mapToDTO(inv));
    }

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