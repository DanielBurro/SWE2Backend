package com.dhbw.backend.dto;

import lombok.Data;

@Data
public class InvitationUpdateDTO {
    // Wenn der Gast zu- oder absagt
    private String status; // z.B. ACCEPTED, DECLINED
    private Integer plusOnes;
}