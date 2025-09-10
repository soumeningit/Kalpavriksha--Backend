package com.soumen.kalpavriksha.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO
{
    private int id;
    private String userId;
    private String name;
    private String email;
    private String password;
    private String contactNo;
    private String role;
    private String imageUrl;
    private boolean isValidUser;
    private boolean isVerified;
    private String landmark;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String latitude;
    private String longitude;
    private String bio;
    private LocalDate dob;
    private String gender;
}
