package com.dhbw.backend.service;

import com.dhbw.backend.dto.EventUpdateDTO;
import com.dhbw.backend.model.Events;
import com.dhbw.backend.model.Location;
import com.dhbw.backend.model.Users;
import com.dhbw.backend.repository.EventRepository;
import com.dhbw.backend.repository.LocationRepository;
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
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    // Gültige Status-Werte
    private static final Set<String> VALID_STATUSES = Set.of("PLANNED", "ACTIVE", "CANCELLED", "DONE");

    public List<Events> getAllEvents() {
        return eventRepository.findAll();
    }

    // Einzelnes Event abrufen
    @SuppressWarnings("null")
    public Events getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event mit ID " + id + " nicht gefunden."));
    }

    // Nur zukünftige Events
    public List<Events> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now());
    }

    public List<Events> getEventsByHost(Long hostId) {
        if (hostId == null) throw new IllegalArgumentException("ID darf nicht null sein");
        return eventRepository.findByHostId(hostId);
    }

    // Ermittelt den aktuell eingeloggten User aus dem JWT
    private Users getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Eingeloggter Benutzer nicht gefunden."));
    }

    // Prüft ob der eingeloggte User der Host des Events ist
    private void assertHost(Events event) {
        Users currentUser = getCurrentUser();
        if (!event.getHost().getId().equals(currentUser.getId())) {
            throw new SecurityException("Zugriff verweigert: Nur der Host darf dieses Event bearbeiten.");
        }
    }

    @Transactional
    public Events createEvent(Events event, Long locationId) {
        // Host aus dem JWT-Token ermitteln (nicht vom Frontend übernehmen)
        Users host = getCurrentUser();
        event.setHost(host);

        // (6) Datum muss in der Zukunft liegen
        if (event.getDate() == null || event.getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Eventdatum liegt in der Vergangenheit");
        }

        // (8) Location prüfen, falls mitgegeben
        if (locationId != null) {
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new IllegalArgumentException("Location existiert nicht"));
            event.setLocation(location);
        }

        // (9) Standard-Status auf "PLANNED" setzen
        if (event.getStatus() == null) {
            event.setStatus("PLANNED");
        }

        return eventRepository.save(event);
    }

    // Event aktualisieren
    @SuppressWarnings("null")
    @Transactional
    public Events updateEvent(Long id, EventUpdateDTO dto) {
        Events event = getEventById(id);
        assertHost(event);

        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getDate() != null) {
            if (dto.getDate().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Eventdatum liegt in der Vergangenheit.");
            }
            event.setDate(dto.getDate());
        }
        if (dto.getLocationId() != null) {
            @SuppressWarnings("null")
            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new IllegalArgumentException("Location existiert nicht."));
            event.setLocation(location);
        }

        return eventRepository.save(event);
    }

    // (neu) Status ändern
    @Transactional
    public Events changeStatus(Long id, String newStatus) {
        if (!VALID_STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException("Ungültiger Status. Erlaubt: " + VALID_STATUSES);
        }
        Events event = getEventById(id);
        assertHost(event);
        event.setStatus(newStatus);
        return eventRepository.save(event);
    }

    // (neu) Event löschen
    @SuppressWarnings("null")
    @Transactional
    public void deleteEvent(Long id) {
        Events event = getEventById(id);
        assertHost(event);
        eventRepository.deleteById(id);
    }
}