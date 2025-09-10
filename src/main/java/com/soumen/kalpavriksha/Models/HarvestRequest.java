package com.soumen.kalpavriksha.Models;

import lombok.Data;

@Data
public class HarvestRequest
{
    private String city;
    private String state;
    private String country;
    private String crop;
    private String latitude;
    private String longitude;
}
