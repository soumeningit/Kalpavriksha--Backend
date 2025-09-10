package com.soumen.kalpavriksha.Models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class PlatDetectProblemResponse
{
    private String id;
    private String userId;
    private boolean isPlant;
    private List<Map<String, Object>> images;
    private Map<String, Object> disease;
    private LocalDateTime createdAt;
}
