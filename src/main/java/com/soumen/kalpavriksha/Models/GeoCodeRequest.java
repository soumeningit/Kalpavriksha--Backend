package com.soumen.kalpavriksha.Models;

import lombok.Data;

@Data
public class GeoCodeRequest
{
    private String landmark;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
}
