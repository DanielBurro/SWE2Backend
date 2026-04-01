package com.dhbw.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
@Getter
@Setter
@NoArgsConstructor
public class Invitation {

    // 22. Einladungs-ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INV_ID")
    private Long id;

    // 23. Zugeordnetes Event (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INV_EVENT_ID", nullable = false)
    private Events event;

    // 24. Eingeladener Gast (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INV_USER_ID", nullable = false)
    private Users guest;

    // 25. Zusagestatus (z.B. PENDING, ACCEPTED, DECLINED)
    @Column(name = "INV_STATUS", length = 20, nullable = false)
    private String status;

    // 26. Begleitpersonen
    @Column(name = "INV_PLUS", nullable = false)
    private Integer plusOnes;

    // 27. Sendedatum
    @Column(name = "INV_SENT_AT", nullable = false)
    private LocalDateTime sentAt;
}