package com.fitness.activity_service.model;



import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "activities")
@Data

@AllArgsConstructor
@Builder
public class Activity {

    @Id
    private String id;

    private String userId;

    private Integer duration; // in minutes
    ;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Field("metrics")
    private Map<String, Object> additionalMetrics;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    private ActivityType type;         // ✅ Match with .type()
    private Integer caloriesBurned;    // ✅ Match with .caloriesBurned()

}
