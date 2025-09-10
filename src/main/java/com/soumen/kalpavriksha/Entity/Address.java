package com.soumen.kalpavriksha.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Address
{
    private String landmark;
    private String street;
    private String city;
    private String state;
    private String country;
    @Column(name = "pin_code")
    private String postalCode;
}
