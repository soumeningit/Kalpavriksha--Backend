package com.soumen.kalpavriksha.Models;

import com.soumen.kalpavriksha.Entity.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileDTO
{
    private String userId;
    private String name;
    private String email;
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
    private Gender gender;
}
