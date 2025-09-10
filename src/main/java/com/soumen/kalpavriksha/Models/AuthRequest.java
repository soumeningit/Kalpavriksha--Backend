package com.soumen.kalpavriksha.Models;

import lombok.Data;

@Data
public class AuthRequest
{
    private int id;
    private String userId;
    private String name;
    private String email;
    private char[] password;
    private String contactNo;
    private String role;
    private String imageUrl;
    private String verificationToken;
    private String resetPasswordToken;
}
