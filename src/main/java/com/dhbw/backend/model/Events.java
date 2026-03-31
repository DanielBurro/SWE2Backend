package com.dhbw.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
public class Events {

    // 15. Event-ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_ID")
    private Long id;

    // 16. Host-ID (FK) - Der User, der das Event erstellt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_USER_ID", nullable = false)
    private Users host;

    // 17. Location-ID (FK) - Die Location des Events (optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_LOC_ID")
    private Location location;

    // 18. Event-Titel
    @Column(name = "EVENT_TITLE", length = 100, nullable = false)
    private String title;

    // 19. Beschreibung
    @Column(name = "EVENT_DESC", columnDefinition = "TEXT")
    private String description;

    // 20. Datum und Uhrzeit
    @Column(name = "EVENT_DATE", nullable = false)
    private LocalDateTime date;

    // 21. Event-Status
    @Column(name = "EVENT_STATUS", nullable = false)
    private String status;
}