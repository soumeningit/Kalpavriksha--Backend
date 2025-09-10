package com.soumen.kalpavriksha.Models;

import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class PlantIdentificationResponse
{
    @Id
    private String id;

    private Long plantIdFromAPI;

    private String userId;

    private boolean isPlant;

    // List of images (stored as JSON objects)
    private List<Map<String, Object>> images;

    // First suggestion (stored as a nested JSON object)
    private Map<String, Object> suggestion;

    private LocalDateTime createdAt;

    private List<String> commonNames;
}
