package com.dhbw.backend.controller;

import com.dhbw.backend.model.Invitation;
import com.dhbw.backend.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/invitations")
@RequiredArgsConstructor
public class PublicInvitationController {

    private final InvitationService invitationService;

    // Einladungsdetails anzeigen (wenn der Gast den Link klickt)
    @GetMapping("/info")
    public ResponseEntity<?> getInvitationInfo(@RequestParam String token) {
        Invitation inv = invitationService.getInvitationByToken(token);
        return ResponseEntity.ok(Map.of(
            "eventTitle", inv.getEvent().getTitle(),
            "hostName", inv.getEvent().getHost().getFirstName(),
            "status", inv.getStatus(),
            "plusOnes", inv.getPlusOnes()
        ));
    }

    // Antwort speichern
    @PostMapping("/respond")
    public ResponseEntity<?> respond(@RequestParam String token, 
                                     @RequestParam String status, 
                                     @RequestParam(required = false) Integer plusOnes) {
        invitationService.respondToInvitationByToken(token, status, plusOnes);
        return ResponseEntity.ok(Map.of("message", "Antwort erfolgreich gespeichert."));
    }
}