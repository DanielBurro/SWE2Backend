package com.dhbw.backend.repository;

import com.dhbw.backend.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    
    // Alle Einladungen für ein spezifisches Event abrufen (Gästeliste)
    List<Invitation> findByEventId(Long eventId);
    
    // Alle Einladungen abrufen, die an einen bestimmten User gesendet wurden
    List<Invitation> findByGuestId(Long guestId);
}