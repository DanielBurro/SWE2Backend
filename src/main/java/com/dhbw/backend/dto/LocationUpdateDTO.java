package com.dhbw.backend.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LocationUpdateDTO {

    // (9) Name: optional änderbar, max 50 Zeichen
    @Size(min = 1, max = 50, message = "Name muss zwischen 1 und 50 Zeichen lang sein.")
    private String name;

    // (10) Straße: optional
    @Size(max = 100, message = "Straße darf maximal 100 Zeichen lang sein.")
    private String street;

    // (11) Hausnummer: optional
    @Size(max = 5, message = "Hausnummer darf maximal 5 Zeichen lang sein.")
    private String houseNumber;

    // (12) Postleitzahl: optional
    @Size(max = 10, message = "Postleitzahl darf maximal 10 Zeichen lang sein.")
    private String zipCode;

    // (13) Stadt: optional
    @Size(max = 50, message = "Stadt darf maximal 50 Zeichen lang sein.")
    private String city;

    // (14) Kapazität muss positiv sein, falls angegeben
    @Positive(message = "Kapazität muss eine positive Zahl sein.")
    private Integer capacity;
}
