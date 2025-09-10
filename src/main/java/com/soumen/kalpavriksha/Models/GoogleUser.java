package com.soumen.kalpavriksha.Models;

import lombok.Data;

@Data
public class GoogleUser
{
    private String sub;   // Google ID
    private String email;
    private String name;
    private String picture;
}
