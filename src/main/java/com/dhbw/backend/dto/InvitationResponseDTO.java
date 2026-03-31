package com.dhbw.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InvitationResponseDTO {
    // Übersicht für das Frontend, z.B. in der Einladungsliste
    private Long id;
    private Long eventId;
    private String eventTitle;
    private Long guestId;
    private String guestName;
    private String status;
    private Integer plusOnes;
    private LocalDateTime sentAt;
}