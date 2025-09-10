package com.soumen.kalpavriksha.Models;

import lombok.Data;

import java.util.List;

@Data
public class LocationResponse
{
    private List<GeoCodeResponse> results;
}
