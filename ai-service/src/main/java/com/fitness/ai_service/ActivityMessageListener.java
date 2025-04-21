package com.fitness.ai_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ActivityMessageListener {

    private final RecommendationService recommendationService;
    private final RecommendationRepository recommendationRepository;

    public ActivityMessageListener(RecommendationService recommendationService,
                                   RecommendationRepository recommendationRepository) {
        this.recommendationService = recommendationService;
        this.recommendationRepository = recommendationRepository;
    }

    @RabbitListener(queues = "activity.q")
    public void processActivity(Activity activity) {
        try {
            log.info("📥 Received activity for processing: {}", activity); // Add this to confirm receipt
            Recommendation recommendation = recommendationService.generateRecommendation(activity);
            recommendationRepository.save(recommendation);
            log.info("✅ Successfully processed activity {}", activity.getId());
            System.out.println("🔥 Listener ACTIVATED for activity: " + activity.getId());
            System.out.println("🔥 Final recommendation: " + recommendation);
            recommendationRepository.save(recommendation);
            log.info("💾 Saved to MongoDB: {}", recommendation.getId());
        } catch (Exception e) {
            log.error("❌ Error processing activity {}", activity.getId(), e);
            System.out.println("🔥 Listener ACTIVATED for activity: " + activity.getId());

        }

    }

}