package com.dhbw.backend.repository;

import com.dhbw.backend.model.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Events, Long> {
    
    // Findet alle Events, die von einem bestimmten User (Host) erstellt wurden
    List<Events> findByHostId(Long hostId);
}