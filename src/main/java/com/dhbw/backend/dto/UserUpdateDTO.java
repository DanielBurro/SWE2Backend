package com.dhbw.backend.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String firstName;
    private String lastName;
    private String bio;
    private String profilePicUrl;
}
