package com.dhbw.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public Map<String, String> home() {
        // Gibt ein sauberes JSON-Objekt zurück
        return Map.of(
            "status", "Erfolgreich!", 
            "message", "Das Spring Boot Backend läuft auf Render."
        );
    }
}