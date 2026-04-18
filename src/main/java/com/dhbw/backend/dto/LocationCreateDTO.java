package com.dhbw.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LocationCreateDTO {

    // (9) Name der Location: Pflichtfeld, max 50 Zeichen
    @NotBlank(message = "Name darf nicht leer sein.")
    @Size(max = 50, message = "Name darf maximal 50 Zeichen lang sein.")
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

    // (14) Maximale Kapazität muss positiv sein, falls angegeben
    @Positive(message = "Kapazität muss eine positive Zahl sein.")
    private Integer capacity;
}
