package com.dhbw.backend.repository;

import com.dhbw.backend.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    // Alle Locations einer Stadt finden (case-insensitive)
    List<Location> findByCityIgnoreCase(String city);

    // Duplikat-Prüfung: gleicher Name + Stadt + Straße
    Optional<Location> findByNameIgnoreCaseAndCityIgnoreCaseAndStreetIgnoreCase(
            String name, String city, String street);
}