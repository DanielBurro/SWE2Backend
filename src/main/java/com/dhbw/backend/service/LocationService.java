package com.dhbw.backend.service;

import com.dhbw.backend.model.Location;
import com.dhbw.backend.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location createLocation(Location location) {
        // (5) Validierung: Standortname darf nicht leer sein
        if (location == null) throw new IllegalArgumentException("Location darf nicht null sein");

        // (4) Maximale Kapazität muss größer als 0 sein (falls angegeben)
        if (location.getCapacity() != null && location.getCapacity() <= 0) {
            throw new IllegalArgumentException("Kapazität muss positiv sein");
        }

        return locationRepository.save(location);
    }

    public Location getLocationById(Long id) {
        if (id == null) throw new IllegalArgumentException("ID darf nicht null sein");
        return locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location nicht gefunden"));
    }
}