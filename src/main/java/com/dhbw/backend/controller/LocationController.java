package com.dhbw.backend.controller;

import com.dhbw.backend.dto.LocationCreateDTO;
import com.dhbw.backend.dto.LocationDTO;
import com.dhbw.backend.dto.LocationUpdateDTO;
import com.dhbw.backend.model.Location;
import com.dhbw.backend.service.LocationService;

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
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @Operation(summary = "Alle Locations abrufen", description = "Optional nach Stadt filtern: ?city=Berlin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste aller Locations")
    })
    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations(
            @RequestParam(required = false) String city) {
        List<Location> locations = (city != null && !city.isBlank())
                ? locationService.getLocationsByCity(city)
                : locationService.getAllLocations();
        return ResponseEntity.ok(locations.stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @Operation(summary = "Location nach ID abrufen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location gefunden"),
            @ApiResponse(responseCode = "404", description = "Location nicht gefunden")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable Long id) {
        return ResponseEntity.ok(mapToDTO(locationService.getLocationById(id)));
    }

    @Operation(summary = "Neue Location erstellen", description = "Legt eine neue Location an. Duplikate (gleicher Name + Stadt + Straße) werden abgelehnt.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Location erfolgreich erstellt"),
            @ApiResponse(responseCode = "409", description = "Location mit gleichem Name + Stadt + Straße existiert bereits"),
            @ApiResponse(responseCode = "422", description = "Validierungsfehler (z.B. Pflichtfeld fehlt)")
    })
    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@Valid @RequestBody LocationCreateDTO dto) {
        // 201 Created — neue Ressource wurde angelegt
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(locationService.createLocation(dto)));
    }

    @Operation(summary = "Location aktualisieren", description = "Aktualisiert die Felder einer bestehenden Location.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location erfolgreich aktualisiert"),
            @ApiResponse(responseCode = "404", description = "Location nicht gefunden"),
            @ApiResponse(responseCode = "422", description = "Validierungsfehler")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocation(
            @PathVariable Long id, @Valid @RequestBody LocationUpdateDTO dto) {
        return ResponseEntity.ok(mapToDTO(locationService.updateLocation(id, dto)));
    }

    @Operation(summary = "Location löschen", description = "Löscht eine Location unwiderruflich.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Location erfolgreich gelöscht"),
            @ApiResponse(responseCode = "404", description = "Location nicht gefunden")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        // 204 No Content — kein Body nötig bei Löschung
        return ResponseEntity.noContent().build();
    }

    private LocationDTO mapToDTO(Location loc) {
        LocationDTO dto = new LocationDTO();
        dto.setId(loc.getId());
        dto.setName(loc.getName());
        dto.setStreet(loc.getStreet());
        dto.setHouseNumber(loc.getHouseNumber());
        dto.setZipCode(loc.getZipCode());
        dto.setCity(loc.getCity());
        dto.setCapacity(loc.getCapacity());
        return dto;
    }
}