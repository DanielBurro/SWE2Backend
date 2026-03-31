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
        if (location == null) throw new IllegalArgumentException("Location darf nicht null sein");
        return locationRepository.save(location);
    }

    public Location getLocationById(Long id) {
        if (id == null) throw new IllegalArgumentException("ID darf nicht null sein");
        return locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location nicht gefunden"));
    }
}