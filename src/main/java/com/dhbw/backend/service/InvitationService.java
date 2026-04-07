package com.dhbw.backend.service;

import com.dhbw.backend.model.Events;
import com.dhbw.backend.model.Invitation;
import com.dhbw.backend.model.Users;
import com.dhbw.backend.repository.EventRepository;
import com.dhbw.backend.repository.InvitationRepository;
import com.dhbw.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public Invitation sendInvitation(Long eventId, Long guestId) {
        if (eventId == null || guestId == null) {
            throw new IllegalArgumentException("Event-ID und Gast-ID dürfen nicht null sein.");
        }

        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event nicht gefunden."));
        Users guest = userRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Gast nicht gefunden."));

        
        // (11) Überprüfen, ob der Host nicht als Gast eingeladen wird
        if (event.getHost() != null && event.getHost().getId().equals(guestId)) {
            throw new IllegalArgumentException("Host kann nicht als Gast eingeladen werden.");
        }

        // (10) Überprüfen, ob der Gast bereits eingeladen ist
        if (invitationRepository.existsByEventIdAndGuestId(eventId, guestId)) {
            throw new IllegalArgumentException("Gast ist bereits eingeladen.");
        }

        // (12) Status auf "PENDING" setzen und plusOnes auf 0 initialisieren
        Invitation inv = new Invitation();
        inv.setEvent(event);
        inv.setGuest(guest);
        inv.setStatus("PENDING");
        inv.setPlusOnes(0);
        inv.setSentAt(LocalDateTime.now()); // (13) Datum der Einladung setzen

        return invitationRepository.save(inv);
    }

    @Transactional
    public Invitation updateStatus(Long invitationId, String newStatus, Integer plusOnes) {
        if (invitationId == null || newStatus == null) {
            throw new IllegalArgumentException("Einladung-ID und neuer Status dürfen nicht null sein.");
        }
        Invitation inv = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Einladung nicht gefunden."));
        
        // (14) Kapazitätsprüfung, wenn der Status auf "ACCEPTED" gesetzt wird
        if ("ACCEPTED".equalsIgnoreCase(newStatus)) {
            Events event = inv.getEvent();

            if (event.getLocation() != null && event.getLocation().getCapacity() != null) {
                
                int currentAttendees = invitationRepository.sumAcceptedAttendeesForEvent(event.getId());
                
                int additionalGuests = (plusOnes != null) ? plusOnes : 0;
                int newAttendeesForThisInv = 1 + additionalGuests;
                
                // Falls die Einladung vorher schon auf ACCEPTED stand, müssen wir die alten Werte abziehen, damit diese Person bei einem Update nicht doppelt gezählt wird.
                if ("ACCEPTED".equalsIgnoreCase(inv.getStatus())) {
                    currentAttendees -= (1 + inv.getPlusOnes());
                }

                // Prüfung: Übersteigt die neue Gesamtzahl die maximale Kapazität?
                if (currentAttendees + newAttendeesForThisInv > event.getLocation().getCapacity()) {
                    throw new IllegalStateException("Location ist bereits voll ausgebucht.");
                }
            }

        }
        inv.setStatus(newStatus.toUpperCase());

        if (plusOnes != null) {
            inv.setPlusOnes(plusOnes);
        }
        
        return invitationRepository.save(inv);
    }

    public List<Invitation> getInvitationsForEvent(Long eventId) {
        return invitationRepository.findByEventId(eventId);
    }

    public List<Invitation> getInvitationsForUser(Long userId) {
        return invitationRepository.findByGuestId(userId);
    }
}