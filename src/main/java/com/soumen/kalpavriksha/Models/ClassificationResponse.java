package com.soumen.kalpavriksha.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClassificationResponse
{
    private String is_gardening_query;
    private double confidence;
}
