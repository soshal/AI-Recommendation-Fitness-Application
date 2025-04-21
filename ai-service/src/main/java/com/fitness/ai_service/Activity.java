package com.fitness.ai_service;



import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data

@AllArgsConstructor
@Builder
public class Activity {

    private String id;
    private String userId;
    private Integer duration; // match type with producer

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Map<String, Object> additionalMetrics;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private ActivityType type;
    private Integer caloriesBurned;
}
