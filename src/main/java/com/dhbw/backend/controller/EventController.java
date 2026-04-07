package com.dhbw.backend.controller;

import com.dhbw.backend.dto.EventCreateDTO;
import com.dhbw.backend.dto.EventResponseDTO;
import com.dhbw.backend.model.Events;
import com.dhbw.backend.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<EventResponseDTO>> getEventsByHost(@PathVariable Long hostId) {
        return ResponseEntity.ok(eventService.getEventsByHost(hostId).stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    
    @Operation(summary = "Neues Event erstellen", description = "Legt ein neues Event an.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event erfolgreich erstellt"),
            @ApiResponse(responseCode = "400", description = "Ungültige Eingabe")
    })
    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody EventCreateDTO dto) {
        Events event = new Events();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());

        Events savedEvent = eventService.createEvent(event, dto.getHostId(), dto.getLocationId());
        return ResponseEntity.ok(mapToDTO(savedEvent));
    }

    private EventResponseDTO mapToDTO(Events event) {
        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate());
        dto.setStatus(event.getStatus());
        if (event.getHost() != null) dto.setHostName(event.getHost().getUsername());
        if (event.getLocation() != null) dto.setLocationName(event.getLocation().getName());
        return dto;
    }
}