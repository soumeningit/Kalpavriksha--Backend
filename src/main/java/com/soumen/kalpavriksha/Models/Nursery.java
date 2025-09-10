package com.soumen.kalpavriksha.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nursery
{
    private String id;
    private String name;
    private String type;
    private double lat;
    private double lon;
    private String address;
    private double[] position;
}

