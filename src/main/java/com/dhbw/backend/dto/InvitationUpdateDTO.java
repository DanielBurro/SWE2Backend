package com.dhbw.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InvitationUpdateDTO {
    // Wenn der Gast zu- oder absagt

    @NotBlank(message = "Status darf nicht leer sein.")
    private String status; // z.B. ACCEPTED, DECLINED

    // (26) Begleitpersonen dürfen nicht negativ sein
    @Min(value = 0, message = "Begleitpersonen dürfen nicht negativ sein.")
    private Integer plusOnes;
}