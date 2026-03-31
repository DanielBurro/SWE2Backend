package com.dhbw.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventCreateDTO {
    // Was das Frontend zum Erstellen schickt
    private String title;
    private String description;
    private LocalDateTime date;
    private Long hostId;
    private Long locationId; // Optional
}