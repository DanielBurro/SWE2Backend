package com.dhbw.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventResponseDTO {
    // Was das Frontend zum Anzeigen bekommt
    private Long id;
    private String title;
    private String description;
    private LocalDateTime date;
    private String status;
    private Long hostId;
    private String hostName;
    private Long locationId;
    private String locationName;
}