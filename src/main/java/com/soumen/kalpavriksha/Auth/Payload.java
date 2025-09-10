package com.soumen.kalpavriksha.Auth;

import lombok.Data;

@Data
public class Payload
{
    private String userId;
    private String role;
    private String email;
    private String username;
}
