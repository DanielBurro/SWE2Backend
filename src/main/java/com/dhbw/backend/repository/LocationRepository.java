package com.dhbw.backend.repository;

import com.dhbw.backend.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    // Hier reichen zunächst die Standard-CRUD-Operationen
}