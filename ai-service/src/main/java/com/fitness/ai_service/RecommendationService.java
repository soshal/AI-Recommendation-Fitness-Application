package com.fitness.ai_service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final ObjectMapper mapper;
    private final RecommendationRepository recommendationRepository;
    private final GeminiService geminiService;

    public List<Recommendation> getUserRecommendations(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    public Recommendation getActivityRecommendation(String activityId) {
        return recommendationRepository.findByActivityId(activityId)
                .orElse(Recommendation.builder()
                        .activityId(activityId)
                        .recommendation("Not available yet. Please try again later.")
                        .improvements(List.of("..."))
                        .suggestions(List.of("..."))
                        .safetyInstructions(List.of("..."))
                        .createdAt(LocalDateTime.now())
                        .build());
    }


    public Recommendation generateRecommendation(Activity activity) {
        try {
            String prompt = createPrompt(activity);
            String aiResponse = geminiService.getAnswer(prompt);
            log.info("🧠 AI Response: {}", aiResponse);
            return processAIResponse(activity, aiResponse);
        } catch (Exception e) {
            log.error("Error generating recommendation for activity {}", activity.getId(), e);
            return createDefaultRecommendation(activity);

        }
    }

    private String createPrompt(Activity activity) {
        return String.format("""
            Analyze this fitness activity and provide detailed recommendation in the following exact JSON format:
            {
              "analysis": {
                "overall": "...",
                "heartRate": "...",
                "pace": "...",
                "caloriesBurned": "..."
              },
              "improvements": [
                { "area": "...", "recommendation": "..." }
              ],
              "suggestions": [
                { "workout": "...", "description": "..." }
              ],
              "safety": ["..."]
            }

            Activity details:
            - Type: %s
            - Duration: %d minutes
            - Calories Burned: %d
            - Additional Metrics: %s
            """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }

    private Recommendation processAIResponse(Activity activity, String aiResponse) throws Exception {
        JsonNode rootNode = mapper.readTree(aiResponse);
        JsonNode textNode = rootNode.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text");

        String jsonContent = textNode.asText()
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();

        JsonNode analysisJson = mapper.readTree(jsonContent);

        StringBuilder fullAnalysis = new StringBuilder();
        addAnalysisSection(fullAnalysis, analysisJson.path("analysis"), "overall", "Overall: ");
        addAnalysisSection(fullAnalysis, analysisJson.path("analysis"), "pace", "Pace: ");
        addAnalysisSection(fullAnalysis, analysisJson.path("analysis"), "heartRate", "Heart Rate: ");
        addAnalysisSection(fullAnalysis, analysisJson.path("analysis"), "caloriesBurned", "Calories: ");

        List<String> improvements = extractImprovements(analysisJson);
        List<String> suggestions = extractSuggestions(analysisJson);
        List<String> safetyGuidelines = extractSafetyGuidelines(analysisJson);

        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType().name())
                .recommendation(fullAnalysis.toString().trim())
                .improvements(improvements)
                .suggestions(suggestions)
                .safetyInstructions(safetyGuidelines)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void addAnalysisSection(StringBuilder builder, JsonNode analysisNode,
                                    String key, String prefix) {
        if (!analysisNode.path(key).isMissingNode()) {
            builder.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private List<String> extractImprovements(JsonNode analysisJson) {
        List<String> improvements = new ArrayList<>();
        JsonNode improvementsNode = analysisJson.path("improvements");

        if (improvementsNode.isArray()) {
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String detail = improvement.path("recommendation").asText();
                improvements.add(String.format("%s: %s", area, detail));
            });
        }

        return improvements.isEmpty()
                ? Collections.singletonList("No specific improvements provided")
                : improvements;
    }

    private List<String> extractSuggestions(JsonNode analysisJson) {
        List<String> suggestions = new ArrayList<>();
        JsonNode suggestionsNode = analysisJson.path("suggestions");

        if (suggestionsNode.isArray()) {
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s: %s", workout, description));
            });
        }

        return suggestions.isEmpty()
                ? Collections.singletonList("No specific suggestions provided")
                : suggestions;
    }

    private List<String> extractSafetyGuidelines(JsonNode analysisJson) {
        List<String> safety = new ArrayList<>();
        JsonNode safetyNode = analysisJson.path("safety");

        if (safetyNode.isArray()) {
            safetyNode.forEach(item -> safety.add(item.asText()));
        }

        return safety.isEmpty()
                ? Collections.singletonList("Follow general safety guidelines")
                : safety;
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType().name())
                .recommendation("Unable to generate detailed analysis at this time.")
                .improvements(List.of(
                        "Continue with your current routine",
                        "Consider consulting a fitness professional"
                ))
                .suggestions(List.of("Unable to generate specific suggestions"))
                .safetyInstructions(List.of("Follow standard safety precautions"))
                .createdAt(LocalDateTime.now())
                .build();


    }


}