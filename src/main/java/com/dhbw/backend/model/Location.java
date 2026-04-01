package com.dhbw.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
public class Location {

    // 8. Location-ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOC_ID")
    private Long id;

    // 9. Name der Location
    @Column(name = "LOC_NAME", length = 50, nullable = false)
    private String name;

    // 10. Straße
    @Column(name = "LOC_STREET", length = 100)
    private String street;

    // 11. Hausnummer
    @Column(name = "LOC_NUMB", length = 5)
    private String houseNumber;

    // 12. Postleitzahl
    @Column(name = "LOC_ZIP", length = 10)
    private String zipCode;

    // 13. Stadt
    @Column(name = "LOC_CITY", length = 50)
    private String city;

    // 14. Maximale Kapazität
    @Column(name = "LOC_CAPACITY")
    private Integer capacity;
}