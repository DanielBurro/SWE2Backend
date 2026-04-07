package com.dhbw.backend.service;

import com.dhbw.backend.model.Events;
import com.dhbw.backend.model.Location;
import com.dhbw.backend.model.Users;
import com.dhbw.backend.repository.EventRepository;
import com.dhbw.backend.repository.LocationRepository;
import com.dhbw.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    public List<Events> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional
    public Events createEvent(Events event, Long hostId, Long locationId) {
        // (6) Datum muss in der Zukunft liegen
        if (event.getDate() == null || event.getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Eventdatum liegt in der Vergangenheit");
        }

        // (7) Sicherheits-Check für Host
        if (hostId == null) throw new IllegalArgumentException("Ein Event braucht zwingend einen Host.");
        
        Users host = userRepository.findById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("Host-User nicht gefunden."));
        event.setHost(host);

        // (8) Location prüfen, falls mitgegeben
        if (locationId != null) {
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new IllegalArgumentException("Location existiert nicht"));
            event.setLocation(location);
        }

        return eventRepository.save(event);
    }

    public List<Events> getEventsByHost(Long hostId) {
        if (hostId == null) throw new IllegalArgumentException("ID darf nicht null sein");
        return eventRepository.findByHostId(hostId);
    }
}