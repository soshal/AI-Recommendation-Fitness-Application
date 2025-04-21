package com.fitness.activity_service;


import com.fitness.activity_service.model.Activity;

import com.mongodb.internal.diagnostics.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityResponse trackActivity(ActivityRequest request) {
        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        Activity saved = activityRepository.save(activity);

        // ✅ Publish to RabbitMQ
        processActivity(saved);

        return mapToResponse(saved);
    }


    public List<ActivityResponse> getUserActivities(String userId) {
        return activityRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ActivityResponse getActivityById(String id) {
        return activityRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Activity not found with ID: " + id));
    }

    private ActivityResponse mapToResponse(Activity activity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());
        return response;
    }


        private final UserValidationService userValidationService;

        public Activity saveActivity(Activity activity) {
            if (!userValidationService.validateUser(activity.getUserId())) {
                throw new RuntimeException("Invalid user ID: " + activity.getUserId());
            }
            return activityRepository.save(activity);

    }

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;



    public void processActivity(Activity activity) {
        // Save to DB (not shown)

        try {
            rabbitTemplate.convertAndSend("activity.exchange", "activity.routing.key", activity);
            log.info("Activity published to RabbitMQ: {}", activity.getId());
        } catch (Exception e) {
            log.error("Failed to publish activity to RabbitMQ", e);
        }
    }

}
