package com.fitness.activity_service;


import com.fitness.activity_service.model.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")

public class ActivityController {

    private final ActivityService activityService;


    public  ActivityController(ActivityService activityService){
        this.activityService= activityService;
    }

    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityService.trackActivity(request));
    }

    @PostMapping("/track")
    public ResponseEntity<Activity> track(@RequestBody Activity activity) {
        activityService.processActivity(activity); // ✅ Add this!
        return ResponseEntity.ok(activityService.saveActivity(activity));
    }
    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getUserActivities(
            @RequestHeader("x-user-id") String userId
    ) {
        return ResponseEntity.ok(activityService.getUserActivities(userId));
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getActivityById(
            @PathVariable String activityId
    ) {
        return ResponseEntity.ok(activityService.getActivityById(activityId));
    }
}
