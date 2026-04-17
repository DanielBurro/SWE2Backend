package com.dhbw.backend.controller;

import com.dhbw.backend.dto.EventCreateDTO;
import com.dhbw.backend.dto.EventResponseDTO;
import com.dhbw.backend.dto.EventUpdateDTO;
import com.dhbw.backend.model.Events;
import com.dhbw.backend.service.EventService;

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
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents().stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    @Operation(summary = "Zukünftige Events abrufen", description = "Gibt alle Events zurück, die in der Zukunft liegen, sortiert nach Datum.")
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponseDTO>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents().stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(mapToDTO(eventService.getEventById(id)));
    }

    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<EventResponseDTO>> getEventsByHost(@PathVariable Long hostId) {
        return ResponseEntity.ok(eventService.getEventsByHost(hostId).stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    @Operation(summary = "Neues Event erstellen", description = "Legt ein neues Event an. Der Host wird automatisch aus dem JWT-Token ermittelt.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event erfolgreich erstellt"),
            @ApiResponse(responseCode = "400", description = "Ungültige Eingabe")
    })
    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventCreateDTO dto) {
        Events event = new Events();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());

        Events savedEvent = eventService.createEvent(event, dto.getLocationId());
        return ResponseEntity.ok(mapToDTO(savedEvent));
    }

    @Operation(summary = "Event aktualisieren", description = "Aktualisiert Titel, Beschreibung, Datum oder Location. Nur der Host darf ändern.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event erfolgreich aktualisiert"),
            @ApiResponse(responseCode = "400", description = "Ungültige Eingabe"),
            @ApiResponse(responseCode = "403", description = "Nur der Host darf dieses Event bearbeiten"),
            @ApiResponse(responseCode = "404", description = "Event nicht gefunden")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> updateEvent(@PathVariable Long id, @Valid @RequestBody EventUpdateDTO dto) {
        return ResponseEntity.ok(mapToDTO(eventService.updateEvent(id, dto)));
    }

    @Operation(summary = "Event-Status ändern", description = "Setzt den Status auf PLANNED, ACTIVE, CANCELLED oder DONE. Nur der Host darf das.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status erfolgreich geändert"),
            @ApiResponse(responseCode = "400", description = "Ungültiger Status"),
            @ApiResponse(responseCode = "403", description = "Nur der Host darf den Status ändern")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<EventResponseDTO> changeStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(mapToDTO(eventService.changeStatus(id, status)));
    }

    @Operation(summary = "Event löschen", description = "Löscht das Event unwiderruflich. Nur der Host darf löschen.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event erfolgreich gelöscht"),
            @ApiResponse(responseCode = "403", description = "Nur der Host darf dieses Event löschen"),
            @ApiResponse(responseCode = "404", description = "Event nicht gefunden")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(Map.of("message", "Event erfolgreich gelöscht."));
    }

    private EventResponseDTO mapToDTO(Events event) {
        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate());
        dto.setStatus(event.getStatus());
        if (event.getHost() != null) {
            dto.setHostId(event.getHost().getId());
            dto.setHostName(event.getHost().getUsername());
        }
        if (event.getLocation() != null) {
            dto.setLocationId(event.getLocation().getId());
            dto.setLocationName(event.getLocation().getName());
        }
        return dto;
    }
}