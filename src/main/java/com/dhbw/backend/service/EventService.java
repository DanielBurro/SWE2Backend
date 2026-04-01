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
        // Sicherheits-Check für Host
        if (hostId == null) throw new IllegalArgumentException("Ein Event braucht zwingend einen Host.");
        
        Users host = userRepository.findById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("Host-User nicht gefunden."));
        event.setHost(host);

        // Location ist laut Katalog optional (Min 0)
        if (locationId != null) {
            Location loc = locationRepository.findById(locationId)
                    .orElseThrow(() -> new IllegalArgumentException("Location nicht gefunden."));
            event.setLocation(loc);
        }

        // Standard-Status setzen
        if (event.getStatus() == null) {
            event.setStatus("DRAFT");
        }

        return eventRepository.save(event);
    }

    public List<Events> getEventsByHost(Long hostId) {
        if (hostId == null) throw new IllegalArgumentException("ID darf nicht null sein");
        return eventRepository.findByHostId(hostId);
    }
}