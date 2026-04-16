package com.dhbw.backend.dto;
import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token; // Hier ist der JWT "Stempel"
    private UserDTO user; // Infos zum eingeloggten User
}