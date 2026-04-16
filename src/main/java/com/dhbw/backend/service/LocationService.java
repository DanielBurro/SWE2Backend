package com.dhbw.backend.service;

import com.dhbw.backend.dto.LocationCreateDTO;
import com.dhbw.backend.dto.LocationUpdateDTO;
import com.dhbw.backend.model.Location;
import com.dhbw.backend.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location getLocationById(Long id) {
        if (id == null) throw new IllegalArgumentException("ID darf nicht null sein");
        return locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location nicht gefunden"));
    }

    // Alle Locations einer Stadt
    public List<Location> getLocationsByCity(String city) {
        return locationRepository.findByCityIgnoreCase(city);
    }

    @Transactional
    public Location createLocation(LocationCreateDTO dto) {
        // (5) Duplikat-Check: gleicher Name + Stadt + Straße
        if (dto.getName() != null && dto.getCity() != null && dto.getStreet() != null) {
            locationRepository.findByNameIgnoreCaseAndCityIgnoreCaseAndStreetIgnoreCase(
                    dto.getName(), dto.getCity(), dto.getStreet())
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException(
                                "Eine Location mit diesem Namen, Stadt und Straße existiert bereits.");
                    });
        }

        Location location = new Location();
        location.setName(dto.getName());
        location.setStreet(dto.getStreet());
        location.setHouseNumber(dto.getHouseNumber());
        location.setZipCode(dto.getZipCode());
        location.setCity(dto.getCity());
        location.setCapacity(dto.getCapacity());

        return locationRepository.save(location);
    }

    // Location aktualisieren
    @SuppressWarnings("null")
    @Transactional
    public Location updateLocation(Long id, LocationUpdateDTO dto) {
        Location existing = getLocationById(id);

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getStreet() != null) existing.setStreet(dto.getStreet());
        if (dto.getHouseNumber() != null) existing.setHouseNumber(dto.getHouseNumber());
        if (dto.getZipCode() != null) existing.setZipCode(dto.getZipCode());
        if (dto.getCity() != null) existing.setCity(dto.getCity());
        if (dto.getCapacity() != null) existing.setCapacity(dto.getCapacity());

        return locationRepository.save(existing);
    }

    // Location löschen
    @SuppressWarnings("null")
    @Transactional
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new IllegalArgumentException("Location mit ID " + id + " nicht gefunden.");
        }
        locationRepository.deleteById(id);
    }
}