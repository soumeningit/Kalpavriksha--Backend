package com.soumen.kalpavriksha.Entity.NoSQL;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "plant_detect_problem")
public class PlantDetectProblemDoc
{
    @Id
    private String id;
    private String userId;

    private boolean isPlant;

    private Long plantIdFromAPI;

    // List of images (stored as JSON objects)
    private List<Map<String, Object>> images;

    // First suggestion (stored as a nested JSON object)
    private Map<String, Object> healthAssessment;

    private LocalDateTime createdAt;

}
