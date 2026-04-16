package com.dhbw.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InvitationCreateDTO {
    // Um jemanden einzuladen

    @NotNull(message = "Event-ID darf nicht null sein.")
    private Long eventId;

    @NotNull(message = "Gast-ID darf nicht null sein.")
    private Long guestId;
}
