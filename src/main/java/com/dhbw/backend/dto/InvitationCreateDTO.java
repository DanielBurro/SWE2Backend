package com.dhbw.backend.dto;

import lombok.Data;

@Data
public class InvitationCreateDTO {
    // Um jemanden einzuladen
    private Long eventId;
    private Long guestId;
}
