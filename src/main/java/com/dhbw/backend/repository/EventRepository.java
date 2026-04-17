package com.dhbw.backend.repository;

import com.dhbw.backend.model.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Events, Long> {
    
    // Findet alle Events, die von einem bestimmten User (Host) erstellt wurden
    List<Events> findByHostId(Long hostId);

    // Findet alle Events, die nach dem angegebenen Datum stattfinden ("upcoming")
    @Query("SELECT e FROM Events e WHERE e.date > :now ORDER BY e.date ASC")
    List<Events> findUpcomingEvents(@Param("now") LocalDateTime now);

    // Findet alle Events nach Status (z.B. "PLANNED", "CANCELLED")
    List<Events> findByStatus(String status);
}