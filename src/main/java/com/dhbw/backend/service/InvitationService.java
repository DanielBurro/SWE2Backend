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

        Invitation inv = new Invitation();
        inv.setEvent(event);
        inv.setGuest(guest);
        inv.setStatus("PENDING");
        inv.setPlusOnes(0);
        inv.setSentAt(LocalDateTime.now());

        return invitationRepository.save(inv);
    }

    @Transactional
    public Invitation updateStatus(Long invitationId, String newStatus, Integer plusOnes) {
        if (invitationId == null || newStatus == null) {
            throw new IllegalArgumentException("Einladung-ID und neuer Status dürfen nicht null sein.");
        }
        Invitation inv = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Einladung nicht gefunden."));
        
        inv.setStatus(newStatus);
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