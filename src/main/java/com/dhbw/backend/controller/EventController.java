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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Alle Events abrufen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste aller Events")
    })
    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents().stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    @Operation(summary = "Zukünftige Events abrufen", description = "Gibt alle Events zurück, die in der Zukunft liegen, sortiert nach Datum.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste zukünftiger Events")
    })
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponseDTO>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents().stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    @Operation(summary = "Event nach ID abrufen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event gefunden"),
            @ApiResponse(responseCode = "404", description = "Event nicht gefunden")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(mapToDTO(eventService.getEventById(id)));
    }

    @Operation(summary = "Events eines Hosts abrufen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste der Events des Hosts"),
            @ApiResponse(responseCode = "404", description = "Host nicht gefunden")
    })
    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<EventResponseDTO>> getEventsByHost(@PathVariable Long hostId) {
        return ResponseEntity.ok(eventService.getEventsByHost(hostId).stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    @Operation(summary = "Neues Event erstellen", description = "Legt ein neues Event an. Der Host wird automatisch aus dem JWT-Token ermittelt.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event erfolgreich erstellt"),
            @ApiResponse(responseCode = "404", description = "Location nicht gefunden"),
            @ApiResponse(responseCode = "422", description = "Validierungsfehler (z.B. Pflichtfeld fehlt)")
    })
    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventCreateDTO dto) {
        Events event = new Events();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());

        Events savedEvent = eventService.createEvent(event, dto.getLocationId());

        // 201 Created — neue Ressource wurde angelegt
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(savedEvent));
    }

    @Operation(summary = "Event aktualisieren", description = "Aktualisiert Titel, Beschreibung, Datum oder Location. Nur der Host darf ändern.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event erfolgreich aktualisiert"),
            @ApiResponse(responseCode = "403", description = "Nur der Host darf dieses Event bearbeiten"),
            @ApiResponse(responseCode = "404", description = "Event nicht gefunden"),
            @ApiResponse(responseCode = "422", description = "Validierungsfehler")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> updateEvent(@PathVariable Long id, @Valid @RequestBody EventUpdateDTO dto) {
        return ResponseEntity.ok(mapToDTO(eventService.updateEvent(id, dto)));
    }

    @Operation(summary = "Event-Status ändern", description = "Setzt den Status auf PLANNED, ACTIVE, CANCELLED oder DONE. Nur der Host darf das.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status erfolgreich geändert"),
            @ApiResponse(responseCode = "403", description = "Nur der Host darf den Status ändern"),
            @ApiResponse(responseCode = "404", description = "Event nicht gefunden"),
            @ApiResponse(responseCode = "422", description = "Ungültiger Status-Wert")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<EventResponseDTO> changeStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(mapToDTO(eventService.changeStatus(id, status)));
    }

    @Operation(summary = "Event löschen", description = "Löscht das Event unwiderruflich. Nur der Host darf löschen.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event erfolgreich gelöscht"),
            @ApiResponse(responseCode = "403", description = "Nur der Host darf dieses Event löschen"),
            @ApiResponse(responseCode = "404", description = "Event nicht gefunden")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        // 204 No Content — kein Body nötig bei Löschung
        return ResponseEntity.noContent().build();
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