package com.dhbw.backend.service;

import com.dhbw.backend.model.Events;
import com.dhbw.backend.model.Invitation;
import com.dhbw.backend.model.Users;
import com.dhbw.backend.repository.EventRepository;
import com.dhbw.backend.repository.InvitationRepository;
import com.dhbw.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    // Gültige Status-Werte
    private static final Set<String> VALID_STATUSES = Set.of("PENDING", "ACCEPTED", "DECLINED", "CANCELLED");

    // Aktuell eingeloggten User aus dem JWT ermitteln
    private Users getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Eingeloggter Benutzer nicht gefunden."));
    }

    // Einzelne Einladung abrufen
    @SuppressWarnings("null")
    public Invitation getInvitationById(Long id) {
        return invitationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Einladung mit ID " + id + " nicht gefunden."));
    }

    public List<Invitation> getInvitationsForEvent(Long eventId) {
        return invitationRepository.findByEventId(eventId);
    }

    // Gefiltert nach Status
    public List<Invitation> getInvitationsForEventByStatus(Long eventId, String status) {
        return invitationRepository.findByEventIdAndStatusIgnoreCase(eventId, status);
    }

    public List<Invitation> getInvitationsForUser(Long userId) {
        return invitationRepository.findByGuestId(userId);
    }

    // Eigene Einladungen (aus JWT)
    public List<Invitation> getMyInvitations() {
        Users currentUser = getCurrentUser();
        return invitationRepository.findByGuestId(currentUser.getId());
    }


    @Transactional
    public Invitation sendInvitation(Long eventId, Long guestId) {
        if (eventId == null || guestId == null) {
            throw new IllegalArgumentException("Event-ID und Gast-ID dürfen nicht null sein.");
        }

        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event nicht gefunden."));
        Users guest = userRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Gast nicht gefunden."));

        // (18) Autorisierung: Nur der Host darf einladen 
        Users currentUser = getCurrentUser();
        if (!event.getHost().getId().equals(currentUser.getId())) {
            throw new SecurityException("Zugriff verweigert: Nur der Host darf Einladungen versenden.");
        }

        // (11) Host nicht als Gast einladen
        if (event.getHost().getId().equals(guestId)) {
            throw new IllegalArgumentException("Host kann nicht als Gast eingeladen werden.");
        }

        // (10) Überprüfen, ob der Gast bereits eingeladen ist
        if (invitationRepository.existsByEventIdAndGuestId(eventId, guestId)) {
            throw new IllegalArgumentException("Gast ist bereits eingeladen.");
        }

        // (12) Status auf "PENDING", plusOnes auf 0 und Token generieren
        Invitation inv = new Invitation();
        inv.setEvent(event);
        inv.setGuest(guest);
        inv.setStatus("PENDING");
        inv.setPlusOnes(0);
        inv.setSentAt(LocalDateTime.now()); // (13) Datum setzen
        
        // Token für den personalisierten Link
        inv.setToken(java.util.UUID.randomUUID().toString());

        Invitation savedInv = invitationRepository.save(inv);

        // (20) Fehler blockiert Erstellung nicht)
        try {
            emailService.sendInvitationEmail(
                guest.getEmail(), 
                guest.getFirstName(), 
                event.getTitle(), 
                savedInv.getToken()
            );
        } catch (Exception e) {
            // Loggen von den Fehler nur
            System.err.println("E-Mail konnte nicht versendet werden: " + e.getMessage());
        }

        return savedInv;
    }

    @Transactional
    public Invitation updateStatus(Long invitationId, String newStatus, Integer plusOnes) {
        if (invitationId == null || newStatus == null) {
            throw new IllegalArgumentException("Einladung-ID und neuer Status dürfen nicht null sein.");
        }

        // Status-Validierung
        String upperStatus = newStatus.toUpperCase();
        if (!VALID_STATUSES.contains(upperStatus)) {
            throw new IllegalArgumentException("Ungültiger Status. Erlaubt: " + VALID_STATUSES);
        }

        Invitation inv = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Einladung nicht gefunden."));

        // Autorisierung: Nur der eingeladene Gast darf seinen eigenen Status ändern
        Users currentUser = getCurrentUser();
        if (!inv.getGuest().getId().equals(currentUser.getId())) {
            throw new SecurityException("Zugriff verweigert: Nur der eingeladene Gast darf den Status ändern.");
        }
        
        // (14) Kapazitätsprüfung, wenn der Status auf "ACCEPTED" gesetzt wird
        if ("ACCEPTED".equals(upperStatus)) {
            Events event = inv.getEvent();

            if (event.getLocation() != null && event.getLocation().getCapacity() != null) {
                
                int currentAttendees = invitationRepository.sumAcceptedAttendeesForEvent(event.getId());
                
                int additionalGuests = (plusOnes != null) ? plusOnes : 0;
                int newAttendeesForThisInv = 1 + additionalGuests;
                
                // Falls die Einladung vorher schon auf ACCEPTED stand, müssen wir die alten Werte abziehen
                if ("ACCEPTED".equalsIgnoreCase(inv.getStatus())) {
                    currentAttendees -= (1 + inv.getPlusOnes());
                }

                // Prüfung: Übersteigt die neue Gesamtzahl die maximale Kapazität?
                if (currentAttendees + newAttendeesForThisInv > event.getLocation().getCapacity()) {
                    throw new IllegalStateException("Location ist bereits voll ausgebucht.");
                }
            }
        }

        inv.setStatus(upperStatus);
        if (plusOnes != null) {
            inv.setPlusOnes(plusOnes);
        }
        
        return invitationRepository.save(inv);
    }

    // Einladung zurückziehen (nur Host)
    @SuppressWarnings("null")
    @Transactional
    public void deleteInvitation(Long invitationId) {
        Invitation inv = getInvitationById(invitationId);

        Users currentUser = getCurrentUser();
        if (!inv.getEvent().getHost().getId().equals(currentUser.getId())) {
            throw new SecurityException("Zugriff verweigert: Nur der Host darf Einladungen zurückziehen.");
        }

        invitationRepository.deleteById(invitationId);
    }
}