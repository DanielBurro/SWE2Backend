package com.dhbw.backend.controller;

import com.dhbw.backend.model.Invitation;
import com.dhbw.backend.service.InvitationService;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/invitations")
@RequiredArgsConstructor
public class PublicInvitationController {

    private final InvitationService invitationService;

    @Operation(summary = "Einladungsdetails abrufen", description = "Gibt Event- und Einladungsinfos zurück wenn der Gast den Link klickt.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Einladungsdetails erfolgreich abgerufen"),
            @ApiResponse(responseCode = "400", description = "Token fehlt oder ist ungültig"),
            @ApiResponse(responseCode = "404", description = "Einladung nicht gefunden")
    })
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInvitationInfo(@RequestParam String token) {
        Invitation inv = invitationService.getInvitationByToken(token);
        return ResponseEntity.ok(Map.of(
                "eventTitle", inv.getEvent().getTitle(),
                "hostName",   inv.getEvent().getHost().getFirstName(),
                "status",     inv.getStatus(),
                "plusOnes",   inv.getPlusOnes()
        ));
    }

    @Operation(summary = "Auf Einladung antworten", description = "Speichert die Antwort des Gastes (ACCEPTED/DECLINED) inkl. optionaler Begleitpersonen.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Antwort erfolgreich gespeichert"),
            @ApiResponse(responseCode = "400", description = "Token fehlt oder ist ungültig"),
            @ApiResponse(responseCode = "404", description = "Einladung nicht gefunden"),
            @ApiResponse(responseCode = "409", description = "Location ist bereits voll ausgebucht"),
            @ApiResponse(responseCode = "422", description = "Ungültiger Status-Wert")
    })
    @PostMapping("/respond")
    public ResponseEntity<Void> respond(@RequestParam String token,
                                        @RequestParam String status,
                                        @RequestParam(required = false) Integer plusOnes) {
        invitationService.respondToInvitationByToken(token, status, plusOnes);
        // 204 No Content — Bestätigung ohne Body
        return ResponseEntity.noContent().build();
    }
}