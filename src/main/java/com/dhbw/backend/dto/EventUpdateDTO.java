package com.dhbw.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventUpdateDTO {

    @Size(min = 1, max = 100, message = "Titel muss zwischen 1 und 100 Zeichen lang sein.")
    private String title;

    private String description;

    // (17) Datum muss in der Zukunft liegen
    @Future(message = "Das Eventdatum muss in der Zukunft liegen.")
    private LocalDateTime date;

    private Long locationId;
}
