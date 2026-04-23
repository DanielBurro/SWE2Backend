package com.dhbw.backend.repository;

import com.dhbw.backend.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    
    // Alle Einladungen für ein spezifisches Event abrufen (Gästeliste)
    List<Invitation> findByEventId(Long eventId);

    // Alle Einladungen für ein Event gefiltert nach Status (z.B. nur ACCEPTED)
    List<Invitation> findByEventIdAndStatusIgnoreCase(Long eventId, String status);
    
    // Alle Einladungen abrufen, die an einen bestimmten User gesendet wurden
    List<Invitation> findByGuestId(Long guestId);

    Optional<Invitation> findByToken(String token);

    // Überprüfen, ob eine Einladung für ein bestimmtes Event und einen bestimmten Gast bereits existiert
    boolean existsByEventIdAndGuestId(Long eventId, Long guestId);

    @Query("SELECT COALESCE(SUM(1 + i.plusOnes), 0) FROM Invitation i WHERE i.event.id = :eventId AND i.status = 'ACCEPTED'")
    int sumAcceptedAttendeesForEvent(@Param("eventId") Long eventId);
}