package com.dhbw.backend.dto;

import lombok.Data;

@Data
public class LocationDTO {
    // Kann für Erstellung und Antwort genutzt werden
    private Long id;
    private String name;
    private String street;
    private String houseNumber;
    private String zipCode;
    private String city;
    private Integer capacity;
}