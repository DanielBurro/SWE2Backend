package com.dhbw.backend.controller;

import com.dhbw.backend.dto.LocationDTO;
import com.dhbw.backend.model.Location;
import com.dhbw.backend.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations().stream()
                .map(this::mapToDTO).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@RequestBody LocationDTO dto) {
        Location loc = new Location();
        loc.setName(dto.getName());
        loc.setStreet(dto.getStreet());
        loc.setHouseNumber(dto.getHouseNumber());
        loc.setZipCode(dto.getZipCode());
        loc.setCity(dto.getCity());
        loc.setCapacity(dto.getCapacity());

        return ResponseEntity.ok(mapToDTO(locationService.createLocation(loc)));
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